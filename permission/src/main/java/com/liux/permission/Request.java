package com.liux.permission;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v4.content.PermissionChecker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@SuppressLint("NewApi")
public class Request {
    // 被允许的权限
    protected List<String> allow = new ArrayList<>();
    // 被拒绝的权限
    protected List<String> reject = new ArrayList<>();
    // 被禁止的权限
    protected List<String> prohibit = new ArrayList<>();

    protected Activity target;
    protected String[] permissions;
    protected OnPermissionListener listener;

    public Request(Activity activity) {
        target = activity;
    }

    public Request(Fragment fragment) {
        target = fragment.getActivity();
    }

    public Request(android.support.v4.app.Fragment fragment) {
        target = fragment.getActivity();
    }

    public Request permissions(String... permissions) {
        this.permissions = permissions;
        return this;
    }

    public Request listener(OnPermissionListener listener) {
        this.listener = listener;
        return this;
    }

    public void request() {
        if (target == null) throw new NullPointerException("with(Fragment) or with(Activity) cannot be empty");
        if (listener == null) throw new NullPointerException("listener(OnPermissionListener) cannot be empty");
        if (permissions == null || permissions.length == 0) throw new NullPointerException("permissions(String[]) cannot be empty");

        allow.clear();
        reject.clear();
        prohibit.clear();

        // 小于 M 的直接通过
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//            allow = Arrays.asList(permissions);
//            listener.onPermission(allow, reject, prohibit);
//            return;
//        }

        List<String> permissions = new ArrayList<>(Arrays.asList(this.permissions));

        // 过滤掉已经拥有的权限
        Iterator<String> iterator = permissions.iterator();
        while (iterator.hasNext()) {
            String permission = iterator.next();
            if (PermissionChecker.checkCallingOrSelfPermission(target, permission) == PermissionChecker.PERMISSION_GRANTED) {
                allow.add(permission);
                iterator.remove();
            }
        }
        if (permissions.isEmpty()) {
            listener.onPermission(allow, reject, prohibit);
            return;
        }

        // 获取请求码
        int requestCode = (int) (System.currentTimeMillis() & 0xFFFF);

        // 注入Fragment 并请求权限
        PermissionFragment fragment = new PermissionFragment();
        FragmentManager manager = target.getFragmentManager();
        manager
                .beginTransaction()
                .add(fragment, "PermissionTool")
                .commitAllowingStateLoss();
        manager.executePendingTransactions();

        // 请求权限
        fragment.requestPermissions(permissions.toArray(new String[permissions.size()]), requestCode);

        PermissionTool.onRequestCall(requestCode, this);
    }
}