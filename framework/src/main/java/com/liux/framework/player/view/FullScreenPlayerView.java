package com.liux.framework.player.view;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.widget.FrameLayout;

/**
 * Created by Liux on 2017/9/25.
 */

public class FullScreenPlayerView extends SurfacePlayerView implements RenderView {

    private PlayerView mPlayerView;

    public FullScreenPlayerView(PlayerView view) {
        super(view.getView().getContext());
        mPlayerView = view;
    }

    @Override
    public boolean getFullScreen() {
        return true;
    }

    @Override
    public void setFullScreen(boolean fullScreen) {
        mPlayerView.setFullScreen(fullScreen);
    }

    /**
     * 从当前布局开始向上查找根内容布局
     * @param view
     * @return
     */
    private ViewGroup findContentView(View view) {
        ViewParent parent = view.getParent();
        while (parent != null && parent instanceof ViewGroup) {
            if (((ViewGroup) parent).getId() == Window.ID_ANDROID_CONTENT) {
                return (ViewGroup) parent;
            } else {
                parent = parent.getParent();
            }
        }
        return null;
    }

    /**
     * 开启全屏模式
     */
    public void openFullScreen() {
        ViewGroup contentView = findContentView(mPlayerView.getView());
        FrameLayout.LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        contentView.addView(this, lp);

        setPlayer(mPlayerView.getPlayer());
    }

    /**
     * 关闭全屏模式
     */
    public void closeFullScreen() {
        mPlayerView.setPlayer(getPlayer());

        ViewGroup contentView = findContentView(mPlayerView.getView());
        contentView.removeView(this);
    }
}
