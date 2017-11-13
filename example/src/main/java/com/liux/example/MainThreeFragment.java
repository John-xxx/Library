package com.liux.example;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bilibili.boxing.BoxingMediaLoader;
import com.bilibili.boxing.model.entity.BaseMedia;
import com.bilibili.boxing.model.entity.impl.ImageMedia;
import com.bilibili.boxing.model.entity.impl.VideoMedia;
import com.liux.base.BaseFragment;
import com.liux.boxing.BoxingTool;
import com.liux.boxing.OnMultiSelectListener;
import com.liux.boxing.OnSingleSelectListener;
import com.liux.boxing.OnVideoSelectListener;
import com.liux.list.adapter.Rule;
import com.liux.list.adapter.State;
import com.liux.list.decoration.GridItemDecoration;
import com.liux.list.adapter.MultipleAdapter;
import com.liux.list.holder.SuperHolder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Liux on 2017/8/9.
 */

public class MainThreeFragment extends BaseFragment {

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

        rvList.addItemDecoration(new GridItemDecoration(10, 3) {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                // 划重点
                int position = parent.getChildLayoutPosition(view);
                if (mMultipleAdapter.isHeaderPosition(position) ||
                        mMultipleAdapter.isFooterPosition(position)) {
                    return;
                }
                super.getItemOffsets(outRect, view, parent, state);
            }
        });
        rvList.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mMultipleAdapter = new MultipleAdapter<String>()
                .setHeader(LayoutInflater.from(getContext()).inflate(R.layout.layout_header, rvList, false))
                .setFooter(LayoutInflater.from(getContext()).inflate(R.layout.layout_footer, rvList, false))
                .addRule(new Rule<String>(R.layout.layout_boxing_simple_media_item) {
                    @Override
                    public boolean doBindData(Object object) {
                        return true;
                    }

                    @Override
                    public void onDataBind(SuperHolder holder, String path, State state, int position) {
                        ImageView imageView = holder.getView(R.id.media_item);
                        int height = imageView.getMeasuredWidth();
                        imageView.setMinimumHeight(height);
                        BoxingMediaLoader.getInstance().displayThumbnail(imageView, path, 150, 150);
                        //GlideApp.with(imageView.getContext()).asBitmap().load(path).override(150, 150).into(imageView);
                    }
                });
        rvList.setAdapter(mMultipleAdapter);

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

    @OnClick({R.id.btn_select_pic, R.id.btn_select_pic_clip, R.id.btn_select_pics, R.id.btn_select_video})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_select_pic:
                BoxingTool.startSingle(this, true, false, new OnSingleSelectListener() {
                    @Override
                    public void onSingleSelect(ImageMedia imageMedia) {
                        mMultipleAdapter.getDataSource().clear();
                        mMultipleAdapter.getDataSource().add(imageMedia.getPath());
                        mMultipleAdapter.notifyDataSetChanged();
                    }
                });
                break;
            case R.id.btn_select_pic_clip:
                BoxingTool.startSingle(this, true, true, new OnSingleSelectListener() {
                    @Override
                    public void onSingleSelect(ImageMedia imageMedia) {
                        mMultipleAdapter.getDataSource().clear();
                        mMultipleAdapter.getDataSource().add(imageMedia.getPath());
                        mMultipleAdapter.notifyDataSetChanged();
                    }
                });
                break;
            case R.id.btn_select_pics:
                BoxingTool.startMulti(this, 5, true, new OnMultiSelectListener() {
                    @Override
                    public void onMultiSelect(List<ImageMedia> imageMedias) {
                        mMultipleAdapter.getDataSource().clear();
                        for (BaseMedia media : imageMedias) {
                            mMultipleAdapter.getDataSource().add(media.getPath());
                        }
                        mMultipleAdapter.notifyDataSetChanged();
                    }
                });
                break;
            case R.id.btn_select_video:
                BoxingTool.startVideo(this, new OnVideoSelectListener() {
                    @Override
                    public void onVideoSelect(VideoMedia videoMedia) {
                        mMultipleAdapter.getDataSource().clear();
                        mMultipleAdapter.getDataSource().add(videoMedia.getPath());
                        mMultipleAdapter.notifyDataSetChanged();
                    }
                });
                break;
        }
    }
}
