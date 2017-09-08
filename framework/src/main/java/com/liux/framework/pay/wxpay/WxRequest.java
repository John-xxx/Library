package com.liux.framework.pay.wxpay;

import android.app.Activity;

import com.liux.framework.pay.Request;
import com.liux.framework.pay.PayTool;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.modelpay.PayResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.HashMap;
import java.util.Map;

public abstract class WxRequest extends Request<PayReq, PayResp> {
    private static Map<String, WxRequest> WX_REQUESTS = new HashMap<>();

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
        String key = bill.prepayId;
        putWxPay(key, this);
        mIWXAPI.sendReq(bill);
    }

    public static WxRequest getWxPay(String key) {
        WxRequest wxPay = WX_REQUESTS.get(key);
        WX_REQUESTS.remove(key);
        return wxPay;
    }

    public static void putWxPay(String key, WxRequest wxPay) {
        WX_REQUESTS.put(key, wxPay);
    }
}
