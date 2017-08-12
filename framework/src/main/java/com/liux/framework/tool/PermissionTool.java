package com.liux.framework.tool;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Process;
import android.support.v4.app.Fragment;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Liux on 2017/8/7.
 */

@SuppressLint("NewApi")
public class PermissionTool {

    private static SparseArray<Request> mRequests = new SparseArray<Request>();

    public static Request with(Fragment fragment) {
        return new Request(fragment);
    }

    public static Request with(Activity activity) {
        return new Request(activity);
    }

    public static void onRequestResult(int requestCode, String[] permissions, int[] grantResults) {
        Request request = mRequests.get(requestCode);
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

        request.callback.onCallback(request.allow, request.reject, request.prohibit);
        mRequests.delete(requestCode);
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

    public static class Request {
        // 被允许的权限
        public List<String> allow = new ArrayList<>();
        // 被拒绝的权限
        public List<String> reject = new ArrayList<>();
        // 被禁止的权限
        public List<String> prohibit = new ArrayList<>();

        private Activity target;
        private Fragment fragment;
        private String[] permissions;
        private OnPermissionCallback callback;

        public Request(Fragment fragment) {
            target = fragment.getActivity();
            this.fragment = fragment;
        }

        public Request(Activity activity) {
            target = activity;
        }

        public Request permissions(String... permissions) {
            this.permissions = permissions;
            return this;
        }

        public Request callback(OnPermissionCallback callback) {
            this.callback = callback;
            return this;
        }

        public void request() {
            if (target == null) throw new NullPointerException("with(Fragment) or with(Activity) cannot be empty");
            if (callback == null) throw new NullPointerException("callback(OnPermissionCallback) cannot be empty");
            if (permissions == null) throw new NullPointerException("permissions(String[]) cannot be empty");

            allow.clear();
            reject.clear();
            prohibit.clear();

            // 小于 M 的直接通过
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                allow = Arrays.asList(permissions);
                callback.onCallback(allow, reject, prohibit);
                return;
            }

            List<String> permissions = new ArrayList<>(Arrays.asList(this.permissions));

            // 过滤掉已经拥有的权限
            Iterator<String> iterator = permissions.iterator();
            while (iterator.hasNext()) {
                String permission = iterator.next();
                if (target.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                    allow.add(permission);
                    iterator.remove();
                }
            }
            if (permissions.isEmpty()) {
                callback.onCallback(allow, reject, prohibit);
                return;
            }

            // 获取请求码
            int requestCode = (int) (System.currentTimeMillis() & 0xFFFF);

            // 请求权限
            if (fragment != null) {
                fragment.requestPermissions(permissions.toArray(new String[permissions.size()]), requestCode);
            } else {
                target.requestPermissions(permissions.toArray(new String[permissions.size()]), requestCode);
            }

            mRequests.append(requestCode, this);
        }
    }

    public static abstract class OnPermissionCallback {

        /**
         * 权限请求结果封装
         * @param allow 被获得的权限
         * @param reject 被拒绝的权限
         * @param prohibit 被禁止的权限
         */
        public abstract void onCallback(List<String> allow, List<String> reject, List<String> prohibit);
    }
}
