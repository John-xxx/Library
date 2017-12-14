package com.liux.permission;

import java.util.List;

public interface OnPermissionListener {

    /**
     * 正式发起申请前的回调
     * @param apply
     */
    void onApply(Apply apply);

    /**
     * 权限请求结果封装
     * @param allow 被获得的权限
     * @param reject 被拒绝的权限
     * @param prohibit 被禁止的权限
     */
    void onPermission(List<String> allow, List<String> reject, List<String> prohibit);
}
