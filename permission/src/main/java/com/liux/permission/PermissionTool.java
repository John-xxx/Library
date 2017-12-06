package com.liux.permission;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Build;
import android.support.v4.content.PermissionChecker;
import android.util.SparseArray;

import java.util.Iterator;

/**
 * Created by Liux on 2017/8/7.
 */

public class PermissionTool {
    private static final String TAG = "PermissionTool";

    private static SparseArray<Request> REQUESTS = new SparseArray<>();

    public static Request with(Activity activity) {
        return new Request(activity);
    }

    public static Request with(Fragment fragment) {
        return new Request(fragment);
    }

    public static Request with(android.support.v4.app.Fragment fragment) {
        return new Request(fragment);
    }

    /**
     * 权限申请调用
     * 小于 M 的不能直接通过, Android4.4 还有 AppOps 关联限制
     * @param request
     */
    @TargetApi(Build.VERSION_CODES.M)
    static void onRequestCall(Request request) {
        // 检查并提出已经具有的权限
        checkAllowPermissions(request);
        if (request.permissions.isEmpty()) {
            request.listener.onPermission(request.allow, request.reject, request.prohibit);
            return;
        }

        // 如果当前设备版本小于 M,直接回调拒绝
        // 还没找到请求 AppOpsManager 权限的方法
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            request.prohibit.addAll(request.permissions);
            request.listener.onPermission(request.allow, request.reject, request.prohibit);
            return;
        }

        // 获取请求码并请求权限
        int requestCode = (int) (System.currentTimeMillis() & 0xFFFF);
        getPermissionFragment(request).requestPermissions(
                request.permissions.toArray(new String[request.permissions.size()]),
                requestCode
        );
        REQUESTS.append(requestCode, request);
    }

    /**
     * 检查以及拥有的权限
     * @param request
     */
    private static void checkAllowPermissions(Request request) {
        Iterator<String> iterator = request.permissions.iterator();
        while (iterator.hasNext()) {
            String permission = iterator.next();
            if (PermissionChecker.checkCallingOrSelfPermission(request.target, permission)
                    == PermissionChecker.PERMISSION_GRANTED) {
                request.allow.add(permission);
                iterator.remove();
            }
        }
    }

    /**
     * 获取/创建并注入 权限申请需要的 Fragmrnt
     * @param request
     * @return
     */
    private static PermissionFragment getPermissionFragment(Request request) {
        PermissionFragment fragment;
        fragment = (PermissionFragment) request.target.getFragmentManager().findFragmentByTag(TAG);
        if (fragment != null) return fragment;

        fragment = new PermissionFragment();
        FragmentManager manager = request.target.getFragmentManager();
        manager
                .beginTransaction()
                .add(fragment, TAG)
                .commitAllowingStateLoss();
        manager.executePendingTransactions();
        return fragment;
    }

    /**
     * 权限申请结果回调
     * @param fragment
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    static void onRequestResult(PermissionFragment fragment, int requestCode, String[] permissions, int[] grantResults) {
        // 移除注入的 PermissionFragment
        removePermissionFragment(fragment);

        // 第一时间取消引用,防止内存泄漏
        Request request = REQUESTS.get(requestCode);
        REQUESTS.delete(requestCode);
        if (request == null) return;

        // 检查申请回调结果
        checkAcceptPermissions(request, permissions);

        request.listener.onPermission(request.allow, request.reject, request.prohibit);
    }

    /**
     * 移除注入的 PermissionFragment
     * @param fragment
     */
    private static void removePermissionFragment(PermissionFragment fragment) {
        Activity target = fragment.getActivity();
        FragmentManager manager = target.getFragmentManager();
        manager.beginTransaction()
                .remove(fragment)
                .commitAllowingStateLoss();
        manager.executePendingTransactions();
    }

    /**
     * 检查请求后的权限状态
     * @param request
     * @param permissions
     */
    @TargetApi(Build.VERSION_CODES.M)
    private static void checkAcceptPermissions(Request request, String[] permissions) {
        for (String permission : permissions) {
            if (PermissionChecker.checkCallingOrSelfPermission(request.target, permission) == PermissionChecker.PERMISSION_GRANTED) {
                request.allow.add(permission);
            } else if (request.target.shouldShowRequestPermissionRationale(permission)) {
                request.reject.add(permission);
            } else {
                request.prohibit.add(permission);
            }
        }
    }
}
