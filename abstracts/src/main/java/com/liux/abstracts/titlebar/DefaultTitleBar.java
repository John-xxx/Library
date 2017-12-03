package com.liux.abstracts.titlebar;

import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.liux.abstracts.R;

import java.lang.reflect.Field;

/**
 * 默认的自定义{@link TitleBar}实现 <br>
 * 处理默认的Toolbar,并填充一个自定义TitleBar <br>
 *
 * http://blog.csdn.net/yewei02538/article/details/60979075
 */
public class DefaultTitleBar extends TitleBar<DefaultTitleBar> {
    private View mRoot, mBack, mMore;
    private TextView mTitle, mBackText, mMoreText;
    private ImageView mBackImage, mMoreImage;

    private OnTitleBarListener mOnTitleBarListener;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int i = v.getId();
            if (i == R.id.view_titlebar_default_back) {
                if (mOnTitleBarListener == null || !mOnTitleBarListener.onBack()) {
                    getActivity().onBackPressed();
                }
            } else if (i == R.id.view_titlebar_default_more) {
                if (mOnTitleBarListener == null || !mOnTitleBarListener.onMore()) {

                }
            }
        }
    };

    public DefaultTitleBar(AppCompatActivity activity) {
        super(activity);
    }

    @Override
    public void initView() {
        mRoot = LayoutInflater.from(getActivity()).inflate(
                R.layout.view_titlebar_default,
                null,
                false
        );
        getActivity().getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActivity().getSupportActionBar().setCustomView(
                mRoot,
                new ActionBar.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );

        operationToolbar(mRoot);

        mBack = mRoot.findViewById(R.id.view_titlebar_default_back);
        mMore = mRoot.findViewById(R.id.view_titlebar_default_more);
        mTitle = (TextView) mRoot.findViewById(R.id.view_titlebar_default_title);
        mBackText = (TextView) mRoot.findViewById(R.id.view_titlebar_default_back_text);
        mMoreText = (TextView) mRoot.findViewById(R.id.view_titlebar_default_more_text);
        mBackImage = (ImageView) mRoot.findViewById(R.id.view_titlebar_default_back_image);
        mMoreImage = (ImageView) mRoot.findViewById(R.id.view_titlebar_default_more_image);

        mBack.setOnClickListener(mOnClickListener);
        mMore.setOnClickListener(mOnClickListener);
    }

    @Override
    public DefaultTitleBar setTitle(CharSequence title) {
        mTitle.setText(title);
        return this;
    }

    @Override
    public DefaultTitleBar setTitleColor(int color) {
        mTitle.setTextColor(color);
        return this;
    }

    public DefaultTitleBar hasBack(boolean has) {
        mBack.setVisibility(has ? View.VISIBLE : View.GONE);
        return this;
    }

    public DefaultTitleBar hasMore(boolean has) {
        mMore.setVisibility(has ? View.VISIBLE : View.GONE);
        return this;
    }

    public DefaultTitleBar setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().setStatusBarColor(color);
        }
        return this;
    }

    public DefaultTitleBar setTitleBarColor(int color) {
        mRoot.setBackgroundColor(color);
        return this;
    }

    public DefaultTitleBar setOnTitleBarListener(OnTitleBarListener listener) {
        mOnTitleBarListener = listener;
        return this;
    }

    public View getView() {
        return mRoot;
    }

    public View getBack() {
        return mBack;
    }

    public View getMore() {
        return mMore;
    }

    public ImageView getBackIcon() {
        return mBackImage;
    }

    public ImageView getMoreIcon() {
        return mMoreImage;
    }

    public TextView getTitleText() {
        return mTitle;
    }

    public TextView getBackText() {
        return mBackText;
    }

    public TextView getMoreText() {
        return mMoreText;
    }

    /**
     * 取消Toolbar自带的边距,处理Toolbar可以自适应标题栏高度
     * @param view
     */
    private void operationToolbar(View view) {
        try {
            Toolbar toolbar = (Toolbar) view.getParent();
            // 修改Toolbar边距
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) toolbar.getLayoutParams();
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            lp.setMargins(0, 0, 0, 0);
            toolbar.setPadding(0, 0, 0, 0);
            toolbar.setLayoutParams(lp);
            toolbar.setContentInsetsAbsolute(0, 0);
            toolbar.setContentInsetsRelative(0, 0);

            // 设置Toolbar背景
            ((View) toolbar.getParent()).setBackgroundColor(Color.TRANSPARENT);

            // 设置Toolbar尺寸
            toolbar.setMinimumHeight(0);
            // 隐藏 MenuView
            toolbar.getMenu();
            Class clazz = toolbar.getClass();
            Field field = clazz.getDeclaredField("mMenuView");
            field.setAccessible(true);
            ActionMenuView actionMenuView = (ActionMenuView) field.get(toolbar);
            actionMenuView.setVisibility(View.GONE);
        } catch (Exception e) {

        }
    }

    public interface OnTitleBarListener {

        /**
         * 返回事件触发 <br>
         * @return 是否已处理,否则调用 {@link android.app.Activity#onBackPressed()}
         */
        boolean onBack();

        /**
         * 更多事件处理 <br>
         * @return 是否已处理
         */
        boolean onMore();
    }
}
