package com.liux.permission;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v4.content.PermissionChecker;
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
        Activity target = fragment.getActivity();

        // 移除注入的 PermissionFragment
        FragmentManager manager = target.getFragmentManager();
        manager.beginTransaction()
                .remove(fragment)
                .commitAllowingStateLoss();
        manager.executePendingTransactions();

        // 第一时间取消引用,防止内存泄漏
        Request request = REQUESTS.get(requestCode);
        REQUESTS.delete(requestCode);

        if (request == null) return;

        for (int i = 0; i < permissions.length; i++) {
            String permission = permissions[i];
            if (PermissionChecker.checkCallingOrSelfPermission(target, permission) == PermissionChecker.PERMISSION_GRANTED) {
                request.allow.add(permission);
            } else if (request.target.shouldShowRequestPermissionRationale(permission)) {
                request.reject.add(permission);
            } else {
                request.prohibit.add(permission);
            }
        }

        request.listener.onPermission(request.allow, request.reject, request.prohibit);
    }
}
