package com.liux.player.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.widget.FrameLayout;

import com.liux.player.Media;
import com.liux.player.Player;
import com.liux.player.PlayerController;
import com.liux.player.listener.OnPlayerListener;

import java.util.Map;

/**
 * 播放界面总成(显示+逻辑+其他)
 * Created by Liux on 2017/9/17.
 */

public abstract class AbstractPlayerView extends FrameLayout implements PlayerView {

    // 要播放的媒体
    private Media mMedia;
    // 对应的视频播放控制器
    private Player mPlayer;
    // 渲染视图控制器
    private RenderView mRenderView;
    // 控制视图控制器
    private ControlView mControlView;
    // 播放事件监听器
    private OnPlayerListener mOnPlayerListener;

    // 全屏视图
    private FullScreenPlayerView mFullScreenPlayerView;

    // 渲染视图准备情况
    private boolean mRenderPrepared = false;
    // 渲染视图生命周期回调
    private RenderView.Callback mCallback = new RenderView.Callback() {

        @Override
        public void created() {
            if (getPlayer() != null) {
                getPlayer().setRenderView(mRenderView);
            }
            mRenderPrepared = true;
        }

        @Override
        public void destroyed() {
            if (getPlayer() != null) {
                getPlayer().setRenderView(null);
            }
            mRenderPrepared = false;
        }
    };

    public AbstractPlayerView(@NonNull Context context) {
        super(context);

        initView();
    }

    public AbstractPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        initView();
    }

    public AbstractPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AbstractPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        initView();
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void setMedia(String media) {
        setMedia(media, null);
    }

    @Override
    public void setMedia(String media, Map<String, String> header) {
        setMedia(Uri.parse(media), header);
    }

    @Override
    public void setMedia(Uri uri) {
        setMedia(uri, null);
    }

    @Override
    public void setMedia(Uri uri, Map<String, String> header) {
        loadMedia(new Media(uri, header));
    }

    @Override
    public void setMedia(Media media) {

    }

    @Override
    public Media getMedia() {
        return mMedia;
    }

    @Override
    public Player getPlayer() {
        if (mPlayer == null) {
            setPlayer(new PlayerController(getContext()));
        }
        return mPlayer;
    }

    @Override
    public void setPlayer(Player player) {
        if (mPlayer != null && mPlayer != player) {
            mPlayer.release();
        }
        mPlayer = player;

        mPlayer.setPlayerView(this);
        mPlayer.setControlView(mControlView);
        mPlayer.setOnPlayerListener(mOnPlayerListener);

        if (mRenderPrepared) {
            mPlayer.setRenderView(mRenderView);
        }
    }

    @Override
    public void setOnPlayerListener(OnPlayerListener listener) {
        if (getPlayer() != null) {
            getPlayer().setOnPlayerListener(listener);
        }
        mOnPlayerListener = listener;
    }

    @Override
    public boolean getFullScreen() {
        return mFullScreenPlayerView != null;
    }

    @Override
    public void setFullScreen(boolean fullScreen) {
        if (getFullScreen() == fullScreen) return;

        if (fullScreen) {
            mFullScreenPlayerView = new FullScreenPlayerView(this);
            mFullScreenPlayerView.openFullScreen();
        } else if (mFullScreenPlayerView != null){
            mFullScreenPlayerView.closeFullScreen();
            mFullScreenPlayerView = null;
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable parcelable = super.onSaveInstanceState();
        if (mMedia != null) {
            return new MediaSavedState(parcelable).setMedia(mMedia);
        }
        return parcelable;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state != null && state instanceof MediaSavedState) {
            Media media = ((MediaSavedState) state).getMedia();
            if (media != null) {
                loadMedia(media);
            }
        }
        super.onRestoreInstanceState(state);
    }

    /**
     * 初始化渲染视图和控制视图
     */
    private void initView() {
        setBackgroundColor(Color.BLACK);

        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.CENTER;

        mRenderView = initRenderView();
        mRenderView.setCallback(mCallback);
        addView(mRenderView.getView(), lp);

        mControlView = initControlView();
        addView(mControlView.getView(), lp);
    }

    /**
     * 载入播放媒体
     * @param media
     */
    protected void loadMedia(Media media) {
        getPlayer().loadMedia(media);
    }

    /**
     * 初始化渲染 View
     * @return
     */
    protected abstract RenderView initRenderView();

    /**
     * 初始化控制视图
     * @return
     */
    private ControlView initControlView() {
        return new DefaultControlView(this);
    }

    /**
     * 媒体数据状态存储
     */
    public static class MediaSavedState extends BaseSavedState {

        private Media media;

        public MediaSavedState(Parcel source) {
            super(source);
            media = Media.CREATOR.createFromParcel(source);
        }

        @TargetApi(Build.VERSION_CODES.N)
        public MediaSavedState(Parcel source, ClassLoader loader) {
            super(source, loader);
            media = Media.CREATOR.createFromParcel(source);
        }

        public MediaSavedState(Parcelable superState) {
            super(superState);
        }

        @Override

        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeParcelable(media, flags);
        }

        public MediaSavedState setMedia(Media media) {
            this.media = media;
            return this;
        }

        public Media getMedia() {
            return media;
        }
    }
}
