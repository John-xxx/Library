package com.liux.permission;

/**
 * Created by Liux on 2017/12/14.
 */

public abstract class SimplePermissionListener implements OnPermissionListener {

    @Override
    public void onApply(Apply apply) {
        apply.onContinue();
    }
}
