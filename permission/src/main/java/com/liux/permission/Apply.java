package com.liux.permission;

/**
 * 正式发起权限申请前的回调
 * Created by Liux on 2017/12/14.
 */

public abstract class Apply {

    Request request;

    Apply(Request request) {
        this.request = request;
    }

    public abstract void onContinue();

    public abstract void onCancel();
}
