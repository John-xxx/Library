package com.liux.example.permission;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.liux.example.R;
import com.liux.glide.GlideApp;
import com.liux.permission.OnPermissionListener;
import com.liux.permission.PermissionTool;
import com.liux.util.IntentUtil;
import com.liux.util.UriUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Liux on 2017/11/28.
 */

public class PermissionActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_CAMERA = 1;

    @BindView(R.id.iv_preview)
    ImageView ivPreview;

    private Uri mTempFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_permission);
        ButterKnife.bind(this);
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

    @SuppressLint("MissingPermission")
    @OnClick({R.id.btn_call, R.id.btn_camera, R.id.btn_call_camera})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_call:
                PermissionTool.with(this)
                        .permissions(Manifest.permission.CALL_PHONE)
                        .listener(new OnPermissionListener() {
                            @Override
                            public void onPermission(List<String> allow, List<String> reject, List<String> prohibit) {
                                if (allow.contains(Manifest.permission.CALL_PHONE)) {
                                    Intent intent = new Intent(Intent.ACTION_CALL);
                                    intent.setData(Uri.parse("tel:10010"));
                                    startActivity(intent);
                                }
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
                                if (allow.contains(Manifest.permission.CAMERA)) {
                                    mTempFile = UriUtil.getProviderCacheUri(PermissionActivity.this, System.currentTimeMillis() + ".jpg");
                                    IntentUtil.callCamera(PermissionActivity.this, mTempFile, REQUEST_CODE_CAMERA);
                                }
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
                                if (allow.contains(Manifest.permission.CALL_PHONE)) {
                                    Intent intent = new Intent(Intent.ACTION_CALL);
                                    intent.setData(Uri.parse("tel:10010"));
                                    startActivity(intent);
                                }

                                if (allow.contains(Manifest.permission.CAMERA)) {
                                    mTempFile = UriUtil.getProviderCacheUri(PermissionActivity.this, System.currentTimeMillis() + ".jpg");
                                    IntentUtil.callCamera(PermissionActivity.this, mTempFile, REQUEST_CODE_CAMERA);
                                }
                            }
                        })
                        .request();
                break;
        }
    }
}
