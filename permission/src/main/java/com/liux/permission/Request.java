package com.liux.permission;

import android.app.Activity;
import android.app.Fragment;
import android.os.Looper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Request {
    // 是否已经被使用
    boolean use = false;
    // 被允许的权限
    List<String> allow;
    // 被拒绝的权限
    List<String> reject;
    // 被禁止的权限
    List<String> prohibit;

    Activity target;
    List<String> permissions;
    OnPermissionListener listener;

    Request(Activity activity) {
        target = activity;
    }

    Request(Fragment fragment) {
        target = fragment.getActivity();
    }

    Request(android.support.v4.app.Fragment fragment) {
        target = fragment.getActivity();
    }

    public Request permissions(String... permissions) {
        if (this.permissions == null) {
            this.permissions = new ArrayList<>();
        }
        this.permissions.addAll(Arrays.asList(permissions));
        return this;
    }

    public Request listener(OnPermissionListener listener) {
        this.listener = listener;
        return this;
    }

    public void request() {
        if (use)
            throw new IllegalStateException("The request body cannot be reused");
        if (target == null)
            throw new NullPointerException("with(Fragment) or with(Activity) cannot be empty");
        if (listener == null)
            throw new NullPointerException("listener(OnPermissionListener) cannot be empty");
        if (permissions == null || permissions.isEmpty())
            throw new NullPointerException("permissions(String[]) cannot be empty");
        if (Looper.getMainLooper().getThread() != Thread.currentThread())
            throw new IllegalStateException("Must be called from main thread of process");

        use = true;
        allow = new ArrayList<>();
        reject = new ArrayList<>();
        prohibit = new ArrayList<>();

        PermissionTool.onRequestCall(Request.this);
    }
}