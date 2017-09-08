package com.liux.framework.pay.unionpay;

import com.liux.framework.pay.Request;
import com.liux.framework.pay.PayTool;

public abstract class UnionRequest extends Request<String, Object> {

    protected UnionRequest(String bill) {
        super(bill);
        PayTool.println("创建银联支付实例:" + bill);
    }

    @Override
    protected void start() {
        PayTool.println("开始银联支付:" + bill.toString());

        PayTool.println("银联支付结果:" + "null");

        PayTool.println("回调支付结果");
        callback(null);
    }
}