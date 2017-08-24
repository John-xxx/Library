package com.liux.framework_demo;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liux.framework.base.BaseFragment;
import com.liux.framework.tool.PermissionTool;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Liux on 2017/8/9.
 */

public class MainTwoFragment extends BaseFragment {

    Unbinder unbinder;

    @Override
    protected void onInitData(Bundle savedInstanceState) {

    }

    @Override
    protected View onInitView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_two, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onLazyLoad() {

    }

    @Override
    protected void onRestoreData(Bundle data) {

    }

    @Override
    protected void onSaveData(Bundle data) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.btn_call, R.id.btn_camera, R.id.btn_call_camera})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_call:
                PermissionTool.with(this)
                        .permissions(Manifest.permission.CALL_PHONE)
                        .callback(new PermissionTool.OnPermissionCallback() {
                            @Override
                            public void onCallback(List<String> allow, List<String> reject, List<String> prohibit) {

                            }
                        })
                        .request();
                break;
            case R.id.btn_camera:
                PermissionTool.with(this)
                        .permissions(Manifest.permission.CAMERA)
                        .callback(new PermissionTool.OnPermissionCallback() {
                            @Override
                            public void onCallback(List<String> allow, List<String> reject, List<String> prohibit) {

                            }
                        })
                        .request();
                break;
            case R.id.btn_call_camera:
                PermissionTool.with(this)
                        .permissions(Manifest.permission.CALL_PHONE, Manifest.permission.CAMERA)
                        .callback(new PermissionTool.OnPermissionCallback() {
                            @Override
                            public void onCallback(List<String> allow, List<String> reject, List<String> prohibit) {

                            }
                        })
                        .request();
                break;
        }
    }
}
