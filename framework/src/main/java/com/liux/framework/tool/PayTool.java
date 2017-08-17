package com.liux.framework.tool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.modelpay.PayResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Liux on 2017/8/7.
 */

public class PayTool {
    private static final String TAG = "[PayTool]";

    public static boolean DEBUG = false;

    private static Map<String, WxPay> WX_REQUESTS = new HashMap<>();

    public static Request with(Activity activity) {
        return new Request(activity);
    }

    @SuppressWarnings("WeakerAccess")
    public static class Request {
        private Activity mActivity;

        Request(Activity activity) {
            mActivity = activity;
        }

        public void pay(Pay pay) {
            pay.target(mActivity);
            pay.pay();
        }
    }

    private static abstract class Pay<T, R> {
        T mBill;
        Activity mActivity;

        private Pay(T bill) {
            mBill = bill;
        }

        protected void target(Activity activity) {
            mActivity = activity;
        }

        public abstract void callback(R r);

        protected abstract void pay();
    }

    public abstract static class AliPay extends Pay<String, AliResult> {
        // 订单支付成功
        public static final String ALI_MEMO_SUCCEED = "9000";
        // 正在处理中，支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
        public static final String ALI_MEMO_UNDERWAY = "8000";
        // 订单支付失败
        public static final String ALI_MEMO_FAILURE = "4000";
        // 重复请求
        public static final String ALI_MEMO_REPEAT = "5000";
        // 用户中途取消
        public static final String ALI_MEMO_CANCEL = "6001";
        // 网络连接出错
        public static final String ALI_MEMO_ERROR = "6002";
        // 支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
        public static final String ALI_MEMO_UNKNOWN = "6004";

        protected AliPay(String bill) {
            super(bill);
        }

        @Override
        protected void pay() {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    // 开启沙箱环境
                    // EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);
                    PayTask alipay = new PayTask(mActivity);
                    final String result = alipay.pay(mBill, true);
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback(new AliResult(result));
                        }
                    });
                }
            };
            Thread payThread = new Thread(runnable);
            payThread.start();
        }
    }

    public static abstract class WxPay extends Pay<PayReq, PayResp> {
        private String mIWXID;
        private IWXAPI mIWXAPI;

        protected WxPay(String wxid, PayReq bill) {
            super(bill);
            mIWXID = wxid;
            WxPayActivity.IWXID = wxid;
        }

        @Override
        protected void target(Activity activity) {
            super.target(activity);
            mIWXAPI = WXAPIFactory.createWXAPI(activity, mIWXID);
            mIWXAPI.registerApp(mIWXID);
        }

        @Override
        protected void pay() {
            String key = String.valueOf(System.currentTimeMillis());
            mBill.extData = key;
            WX_REQUESTS.put(key, this);

            mIWXAPI.sendReq(mBill);
        }
    }

    public static abstract class UnionPay extends Pay<String, Boolean> {

        protected UnionPay(String bill) {
            super(bill);
        }

        @Override
        protected void pay() {
            callback(false);
        }
    }

    public static class AliResult {
        private String resultStatus;
        private String result;
        private String memo;

        public AliResult(String rawResult) {
            if (TextUtils.isEmpty(rawResult)) return;

            String[] resultParams = rawResult.split(";");
            for (String resultParam : resultParams) {
                if (resultParam.startsWith("resultStatus")) {
                    resultStatus = gatValue(resultParam, "resultStatus");
                }
                if (resultParam.startsWith("result")) {
                    result = gatValue(resultParam, "result");
                }
                if (resultParam.startsWith("memo")) {
                    memo = gatValue(resultParam, "memo");
                }
            }
        }

        @Override
        public String toString() {
            return "resultStatus={" + resultStatus + "};memo={" + memo + "};result={" + result + "}";
        }

        private String gatValue(String content, String key) {
            String prefix = key + "={";
            return content.substring(content.indexOf(prefix) + prefix.length(), content.lastIndexOf("}"));
        }

        public String getResultStatus() {
            return resultStatus;
        }

        public String getMemo() {
            return memo;
        }

        public String getResult() {
            return result;
        }
    }

    public static class WxPayActivity extends Activity implements IWXAPIEventHandler {
        private static String IWXID;

        private IWXAPI mIWXAPI;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            mIWXAPI = WXAPIFactory.createWXAPI(this, IWXID);
            mIWXAPI.handleIntent(getIntent(), this);
        }

        @Override
        protected void onNewIntent(Intent intent) {
            super.onNewIntent(intent);
            mIWXAPI.handleIntent(intent, this);
        }

        @Override
        public void onReq(BaseReq baseReq) {

        }

        @Override
        public void onResp(BaseResp baseResp) {
            if (baseResp.getType() != ConstantsAPI.COMMAND_PAY_BY_WX) return;

            PayResp payResp = (PayResp) baseResp;
            String key = payResp.extData;

            WxPay wxPay = WX_REQUESTS.get(key);
            WX_REQUESTS.remove(key);
            if (wxPay != null) {
                wxPay.callback(payResp);
            }

            finish();
        }
    }
}
