package com.liux.example;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.liux.base.BaseFragment;
import com.liux.glide.GlideApp;
import com.liux.permission.OnPermissionListener;
import com.liux.permission.PermissionTool;
import com.liux.util.IntentUtil;
import com.liux.util.UriUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Liux on 2017/8/9.
 */

public class MainTwoFragment extends BaseFragment {
    private static final int REQUEST_CODE_CAMERA = 1;

    @BindView(R.id.iv_preview)
    ImageView ivPreview;
    Unbinder unbinder;

    private Uri mTempFile;

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;
        switch (requestCode) {
            case REQUEST_CODE_CAMERA:
                GlideApp.with(ivPreview)
                        .asBitmap()
                        .load(mTempFile)
                        .into(ivPreview);
                break;
        }
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
                        .listener(new OnPermissionListener() {
                            @Override
                            public void onPermission(List<String> allow, List<String> reject, List<String> prohibit) {
                                Intent intent = new Intent(Intent.ACTION_CALL);
                                intent.setData(Uri.parse("tel:10010"));
                                startActivity(intent);
                            }
                        })
                        .request();
                break;
            case R.id.btn_camera:
                PermissionTool.with(this)
                        .permissions(Manifest.permission.CAMERA)
                        .listener(new OnPermissionListener() {
                            @Override
                            public void onPermission(List<String> allow, List<String> reject, List<String> prohibit) {
                                mTempFile = UriUtil.getProviderCacheUri(getContext(), System.currentTimeMillis() + ".jpg");
                                IntentUtil.callCamera(MainTwoFragment.this, mTempFile, REQUEST_CODE_CAMERA);
                            }
                        })
                        .request();
                break;
            case R.id.btn_call_camera:
                PermissionTool.with(this)
                        .permissions(Manifest.permission.CALL_PHONE, Manifest.permission.CAMERA)
                        .listener(new OnPermissionListener() {
                            @Override
                            public void onPermission(List<String> allow, List<String> reject, List<String> prohibit) {
                                Intent intent = new Intent(Intent.ACTION_CALL);
                                intent.setData(Uri.parse("tel:10010"));
                                startActivity(intent);

                                mTempFile = UriUtil.getProviderCacheUri(getContext(), System.currentTimeMillis() + ".jpg");
                                IntentUtil.callCamera(MainTwoFragment.this, mTempFile, REQUEST_CODE_CAMERA);
                            }
                        })
                        .request();
                break;
        }
    }
}
