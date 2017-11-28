package com.liux.player.util;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Liux on 2017/10/29.
 */

public class GestureHelper extends GestureDetector.SimpleOnGestureListener {
    private static final int TYPE_NONE = 0;
    private static final int TYPE_SEEK = 1;
    private static final int TYPE_VOLUME = 2;
    private static final int TYPE_BRIGHTNESS = 3;
    private static final float SCREEN_DISYANCE_MARGIN = 50;
    private static final float SCREEN_DISTANCE_VERTICAL = 0.5f;
    private static final float SCREEN_DISTANCE_HORIZONTAL = 0.8f;

    private View mTarget;
    private GestureDetector mGestureDetector;
    private OnGestureListener mOnGestureListener;

    private int mType = TYPE_NONE;
    private float mSeekRange = 0;
    private float mSeekRatio = 0;
    private float mVolumeRatio = 0;
    private float mBrightnessRatio = 0;
    private MotionEvent mMotionEvent = null;

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            boolean result = mGestureDetector.onTouchEvent(event);
            // 由于抛速度过小情况下onFling不被回调,补偿一个
            if (mType != TYPE_NONE && event.getAction() == MotionEvent.ACTION_UP) {
                onFling(mMotionEvent, event, 0, 0);
            }
            return result;
        }
    };

    public static void install(View target, OnGestureListener listener) {
        new GestureHelper(target, listener);
    }

    public GestureHelper(View target, OnGestureListener listener) {
        mTarget = target;
        mGestureDetector = new GestureDetector(target.getContext(), this);
        mOnGestureListener = listener;

        target.setOnTouchListener(mOnTouchListener);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        int margin = dp2px(mTarget.getContext(), SCREEN_DISYANCE_MARGIN);
        if (e.getRawY() < margin || e.getRawY() > mTarget.getContext().getResources().getDisplayMetrics().heightPixels - margin) {
            return false;
        }
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        mOnGestureListener.onClick(mTarget);
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        mMotionEvent = e1;
        if (!mOnGestureListener.canDrag()) return true;

        switch (mType) {
            case TYPE_NONE:
                mType = computeType(e1.getX(), distanceX, distanceY);
                onScroll(e1, e2, distanceX, distanceY);
                break;
            case TYPE_SEEK:
                if (mOnGestureListener.canSeek()) {
                    computeSeek(distanceX);
                    mOnGestureListener.setSeekRatio(mSeekRatio, false);
                }
                break;
            case TYPE_VOLUME:
                computeVolume(distanceY);
                mOnGestureListener.setVolumeRatio(mVolumeRatio);
                break;
            case TYPE_BRIGHTNESS:
                computeBrightness(distanceY);
                mOnGestureListener.setBrightnessRatio(mBrightnessRatio);
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (!mOnGestureListener.canDrag()) return true;

        switch (mType) {
            case TYPE_NONE:
                break;
            case TYPE_SEEK:
                if (mOnGestureListener.canSeek()) {
                    computeSeek(0);
                    mOnGestureListener.setSeekRatio(mSeekRatio, true);
                }
                break;
            case TYPE_VOLUME:
                break;
            case TYPE_BRIGHTNESS:
                break;
            default:
                break;
        }
        mType = TYPE_NONE;

        return true;
    }

    /**
     * 计算手势类型,并取得初始信息
     * @param x
     * @param distanceX
     * @param distanceY
     * @return
     */
    private int computeType(float x, float distanceX, float distanceY) {
        int type = TYPE_NONE;
        if (Math.abs(distanceX) > Math.abs(distanceY)) {
            type = TYPE_SEEK;
            if (mOnGestureListener.canSeek()) {
                mSeekRange = mOnGestureListener.getSeekRange();
                mSeekRatio = mOnGestureListener.getSeekRatio();
            }
        } else if (x > mTarget.getWidth() / 2){
            type = TYPE_VOLUME;
            mVolumeRatio = mOnGestureListener.getVolumeRatio();
        } else {
            type = TYPE_BRIGHTNESS;
            mBrightnessRatio = mOnGestureListener.getBrightnessRatio();
        }
        return type;
    }

    /**
     * 计算快进/快退秒数
     * @param x
     * @return
     */
    private void computeSeek(float x) {
        float count_ratio = -x / (mTarget.getWidth() * SCREEN_DISTANCE_HORIZONTAL);
        mSeekRatio += count_ratio * mSeekRange;
        if (mSeekRatio <= 0) {
            mSeekRatio = 0;
        }
        if (mSeekRatio >= 1) {
            mSeekRatio = 1;
        }
    }

    /**
     * 计算音量改变数量
     * @param y
     * @return
     */
    private void computeVolume(float y) {
        mVolumeRatio += y / (mTarget.getHeight() * SCREEN_DISTANCE_VERTICAL);
        if (mVolumeRatio <= 0) {
            mVolumeRatio = 0;
        }
        if (mVolumeRatio >= 1) {
            mVolumeRatio = 1;
        }
    }

    /**
     * 计算亮度改变数量
     * @param y
     * @return
     */
    private void computeBrightness(float y) {
        mBrightnessRatio += y / (mTarget.getHeight() * SCREEN_DISTANCE_VERTICAL);
        if (mBrightnessRatio <= 0) {
            mBrightnessRatio = 0;
        }
        if (mBrightnessRatio >= 1) {
            mBrightnessRatio = 1;
        }
    }

    private static int dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public interface OnGestureListener {

        /**
         * 目标发生一次单击
         * @param view
         */
        void onClick(View view);

        /**
         * 获取是否能拖动
         * @return
         */
        boolean canDrag();

        /**
         * 获取是否能快进快退
         * @return
         */
        boolean canSeek();

        /**
         * 获取一次滑动允许的范围
         * @return
         */
        float getSeekRange();

        /**
         * 获取当前位置
         * @return
         */
        float getSeekRatio();

        /**
         * 设置进度改变回调
         * @param second 改变时间<b>相对值</b>
         * @param set 是否是设置进度
         */
        void setSeekRatio(float second, boolean set);

        /**
         * 获取当前音量比例
         * @return
         */
        float getVolumeRatio();

        /**
         * 设置音量改变
         * @param volume 0 - getMax()
         */
        void setVolumeRatio(float volume);

        /**
         * 获取当前亮度
         * @return
         */
        float getBrightnessRatio();

        /**
         * 设置亮度改变
         * @param brightness 0 - 1.0
         */
        void setBrightnessRatio(float brightness);
    }
}
