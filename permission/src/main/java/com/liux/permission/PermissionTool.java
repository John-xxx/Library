package com.liux.permission;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AppOpsManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Process;
import android.util.SparseArray;

/**
 * Created by Liux on 2017/8/7.
 */

@SuppressLint("NewApi")
public class PermissionTool {
    private static final String TAG = "PermissionTool";

    private static SparseArray<Request> REQUESTS = new SparseArray<Request>();

    public static Request with(Activity activity) {
        return new Request(activity);
    }

    public static Request with(Fragment fragment) {
        return new Request(fragment);
    }

    public static Request with(android.support.v4.app.Fragment fragment) {
        return new Request(fragment);
    }

    public static void onRequestCall(int requestCode, Request request) {
        REQUESTS.append(requestCode, request);
    }

    protected static void onRequestResult(PermissionFragment fragment, int requestCode, String[] permissions, int[] grantResults) {
        // 移除注入的 PermissionFragment
        FragmentManager manager = fragment.getActivity().getFragmentManager();
        manager.beginTransaction()
                .remove(fragment)
                .commitAllowingStateLoss();
        manager.executePendingTransactions();

        // 第一时间取消引用,防止内存泄漏
        Request request = REQUESTS.get(requestCode);
        REQUESTS.delete(requestCode);

        if (request == null) return;

        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED && checkMiuiPermission(request.target, permissions[i])) {
                request.allow.add(permissions[i]);
            } else if (request.target.shouldShowRequestPermissionRationale(permissions[i])) {
                request.reject.add(permissions[i]);
            } else {
                request.prohibit.add(permissions[i]);
            }
        }

        request.listener.onPermission(request.allow, request.reject, request.prohibit);
    }

    /**
     * MIUI 需要 AppOpsManager 验证
     * @param context
     * @param permission
     * @return
     */
    private static boolean checkMiuiPermission(Context context, String permission) {
        boolean ops = true;
        AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        switch (permission) {
            case Manifest.permission.ACCESS_FINE_LOCATION:
                ops = manager.checkOp(AppOpsManager.OPSTR_FINE_LOCATION, Process.myUid(), context.getPackageName()) == AppOpsManager.MODE_ALLOWED;
                break;
            case Manifest.permission.ACCESS_COARSE_LOCATION:
                ops = manager.checkOp(AppOpsManager.OPSTR_COARSE_LOCATION, Process.myUid(), context.getPackageName()) == AppOpsManager.MODE_ALLOWED;
                break;
        }
        return ops;
    }
}
