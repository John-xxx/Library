package com.liux.framework.permission;

import android.app.Fragment;
import android.support.annotation.NonNull;

/**
 * 申请权限时注入的Fragment
 */
public class PermissionFragment extends Fragment {

    /**
     * 权限申请的回调
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionTool.onRequestResult(this, requestCode, permissions, grantResults);
    }
}
