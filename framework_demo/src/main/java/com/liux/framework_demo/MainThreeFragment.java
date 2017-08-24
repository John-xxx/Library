package com.liux.framework_demo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bilibili.boxing.Boxing;
import com.bilibili.boxing.BoxingMediaLoader;
import com.bilibili.boxing.model.config.BoxingConfig;
import com.bilibili.boxing.model.config.BoxingCropOption;
import com.bilibili.boxing.model.entity.BaseMedia;
import com.bilibili.boxing.model.entity.impl.ImageMedia;
import com.bilibili.boxing.utils.BoxingFileHelper;
import com.bilibili.boxing.utils.ImageCompressor;
import com.bilibili.boxing_impl.ui.BoxingActivity;
import com.liux.framework.base.BaseFragment;
import com.liux.framework.list.GridItemDecoration;
import com.liux.framework.list.MultipleAdapter;
import com.liux.framework.list.SelectAdapter;
import com.liux.framework.list.SuperHolder;
import com.liux.framework.tool.GlideApp;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Liux on 2017/8/9.
 */

public class MainThreeFragment extends BaseFragment {
    private static final int REQUEST_CODE = 1024;
    private static final int REQUEST_CODE_COMPRESS = 2048;

    @BindView(R.id.rv_list)
    RecyclerView rvList;
    Unbinder unbinder;

    private MultipleAdapter<String> mMultipleAdapter;

    @Override
    protected void onInitData(Bundle savedInstanceState) {

    }

    @Override
    protected View onInitView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_three, container, false);
        unbinder = ButterKnife.bind(this, view);

        mMultipleAdapter = new MultipleAdapter<String>()
                .addRule(new MultipleAdapter.Rule<String>(R.layout.layout_boxing_simple_media_item) {
                    @Override
                    public boolean doBindData(Object object) {
                        return true;
                    }

                    @Override
                    public void onDataBind(SuperHolder holder, String path, MultipleAdapter.State state, int position) {
                        ImageView imageView = holder.getView(R.id.media_item);
                        int height = imageView.getMeasuredWidth();
                        imageView.setMinimumHeight(height);
                        BoxingMediaLoader.getInstance().displayThumbnail(imageView, path, 150, 150);
                        //GlideApp.with(imageView.getContext()).asBitmap().load(path).override(150, 150).into(imageView);
                    }
                });
        rvList.setAdapter(mMultipleAdapter);
        rvList.addItemDecoration(new GridItemDecoration(10, 3));
        rvList.setLayoutManager(new GridLayoutManager(getContext(), 3));
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
        List<BaseMedia> medias = Boxing.getResult(data);
        switch (requestCode) {
            case REQUEST_CODE:
                break;
        }
        mMultipleAdapter.getDataSource().clear();
        for (BaseMedia media : medias) {
            mMultipleAdapter.getDataSource().add(media.getPath());
        }
        mMultipleAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.btn_select_pic, R.id.btn_select_pic_clip, R.id.btn_select_pics, R.id.btn_select_video})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_select_pic:
                BoxingConfig singleImgConfig = new BoxingConfig(BoxingConfig.Mode.SINGLE_IMG).withMediaPlaceHolderRes(R.drawable.ic_boxing_default_image);
                Boxing.of(singleImgConfig).withIntent(getContext(), BoxingActivity.class).start(this, REQUEST_CODE);
                break;
            case R.id.btn_select_pic_clip:
                String cachePath = BoxingFileHelper.getCacheDir(getContext());
                if (TextUtils.isEmpty(cachePath)) {
                    Toast.makeText(getContext().getApplicationContext(), R.string.boxing_storage_deny, Toast.LENGTH_SHORT).show();
                    return;
                }
                Uri destUri = new Uri.Builder()
                        .scheme("file")
                        .appendPath(cachePath)
                        .appendPath(String.format(Locale.US, "%s.png", System.currentTimeMillis()))
                        .build();
                BoxingConfig singleCropImgConfig = new BoxingConfig(BoxingConfig.Mode.SINGLE_IMG).withCropOption(new BoxingCropOption(destUri))
                        .withMediaPlaceHolderRes(R.drawable.ic_boxing_default_image);
                Boxing.of(singleCropImgConfig).withIntent(getContext(), BoxingActivity.class).start(this, REQUEST_CODE);
                break;
            case R.id.btn_select_pics:
                BoxingConfig config = new BoxingConfig(BoxingConfig.Mode.MULTI_IMG).needCamera(R.drawable.ic_boxing_camera_white).needGif();
                Boxing.of(config).withIntent(getContext(), BoxingActivity.class).start(this, REQUEST_CODE);
                break;
            case R.id.btn_select_video:
                BoxingConfig videoConfig = new BoxingConfig(BoxingConfig.Mode.VIDEO).withVideoDurationRes(R.drawable.ic_boxing_play);
                Boxing.of(videoConfig).withIntent(getContext(), BoxingActivity.class).start(this, REQUEST_CODE);
                break;
        }
    }
}
