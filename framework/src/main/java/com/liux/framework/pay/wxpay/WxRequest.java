package com.liux.framework.pay.wxpay;

import android.app.Activity;

import com.liux.framework.pay.Request;
import com.liux.framework.pay.PayTool;
import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.modelpay.PayResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.HashMap;
import java.util.Map;

public abstract class WxRequest extends Request<PayReq, PayResp> {
    private static Map<String, WxRequest> WX_REQUESTS = new HashMap<>();

    public static WxRequest getWxPay(String key) {
        WxRequest wxPay = WX_REQUESTS.get(key);
        WX_REQUESTS.remove(key);
        return wxPay;
    }

    public static void putWxPay(String key, WxRequest wxPay) {
        WX_REQUESTS.put(key, wxPay);
    }

    public static final int ERR_PARAM = -101;
    public static final int ERR_VERSION = -102;

    private IWXAPI mIWXAPI;

    protected WxRequest(PayReq bill) {
        super(bill);
        WxPayActivity.IWXID = bill.appId;
        PayTool.println("创建微信支付实例:" + bill.toString());
    }

    @Override
    protected void init(Activity activity) {
        super.init(activity);
        mIWXAPI = WXAPIFactory.createWXAPI(activity, bill.appId);
        boolean succeed = mIWXAPI.registerApp(bill.appId);
        PayTool.println("初始化微信支付实例:" + "[" + succeed + "]");
    }

    @Override
    protected void start() {
        PayTool.println("开始微信支付:" + bill.toString());
        if (bill == null || !bill.checkArgs()) {
            callFailure(ERR_PARAM, "请求参数自检失败");
            return;
        }
        if (mIWXAPI.getWXAppSupportAPI() < Build.PAY_SUPPORTED_SDK_INT) {
            callFailure(ERR_VERSION, "未安装微信或版本过低");
            return;
        }
        String key = bill.prepayId;
        putWxPay(key, this);
        mIWXAPI.sendReq(bill);
    }

    private void callFailure(int code, String msg) {
        PayResp resp = new PayResp();
        resp.errCode = code;
        PayTool.println("微信支付结果:" + msg);
        PayTool.println("回调支付结果");
        callback(resp);
    }
}
