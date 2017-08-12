package com.liux.framework.tool;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

/**
 * Handler实现的定时器 <br>
 * Created by Liux on 2017/01/05. <br>
 * 已知问题,Handler会有误差,需要依照系统CountDownTimer修改 <br>
 */

public class CountDownTimer {
    private static final int MSG = 1;

    private int mRequestCode = 0;
    private boolean mRepetition = false;

    private long mGrossTime;
    private long mSurplusTime;
    private long mIntervalTime;

    private OnTimerListener mOnTimerListener;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage (Message msg){
            synchronized (CountDownTimer.this) {
                try {
                    if (mRepetition) {
                        if (mOnTimerListener != null) mOnTimerListener.onTick(mRequestCode, mGrossTime);
                        sendEmptyMessageDelayed(MSG, mIntervalTime);
                    } else {
                        mSurplusTime = mSurplusTime - mIntervalTime;
                        if (mSurplusTime > 0) {
                            if (mOnTimerListener != null) mOnTimerListener.onTick(mRequestCode, mSurplusTime);
                            sendEmptyMessageDelayed(MSG, mIntervalTime);
                        } else {
                            if (mOnTimerListener != null) mOnTimerListener.onFinish(mRequestCode);
                            mHandler.removeMessages(MSG);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public CountDownTimer() {

    }

    /* 循环模式 */
    public CountDownTimer(long interval) {
        mRepetition = true;
        this.mIntervalTime = interval;
    }

    /* 计时模式 */
    public CountDownTimer(long gross, long interval) {
        mRepetition = false;
        this.mGrossTime = gross;
        this.mIntervalTime = interval;
    }

    public boolean isRepetition() {
        return mRepetition;
    }

    public void setRepetition(boolean repetition) {
        this.mRepetition = repetition;
    }

    public int getRequestCode() {
        return mRequestCode;
    }

    public void setRequestCode(int requestCode) {
        this.mRequestCode = requestCode;
    }

    public long getGrossTime() {
        return mGrossTime;
    }

    public void setGrossTime(long grossTime) {
        this.mGrossTime = grossTime;
    }

    public long getIntervalTime() {
        return mIntervalTime;
    }

    public void setIntervalTime(long intervalTime) {
        this.mIntervalTime = intervalTime;
    }

    public void setOnTimerListener(OnTimerListener listener) {
        this.mOnTimerListener = listener;
    }

    public void start() {
        mSurplusTime = mGrossTime + mIntervalTime;
        mHandler.removeMessages(MSG);
        mHandler.sendEmptyMessage(MSG);
    }

    public void stop() {
        mHandler.removeMessages(MSG);
    }

    public void reset() {
        if (mOnTimerListener != null) mOnTimerListener.onReset(mRequestCode);
        stop();
    }

    public void finish() {
        if (mOnTimerListener != null) mOnTimerListener.onFinish(mRequestCode);
        stop();
    }

    public boolean isRun() {
        return mSurplusTime > 0;
    }

    public interface OnTimerListener {

        void onReset(int requestCode);

        void onTick(int requestCode, long surplus);

        void onFinish(int requestCode);
    }

    public static class Builder {
        private CountDownTimer mCountDownTimer;

        public Builder() {
            mCountDownTimer = new CountDownTimer();
        }

        public Builder repetition(boolean repetition) {
            mCountDownTimer.setRepetition(repetition);
            return this;
        }

        public Builder requestCode(int requestCode) {
            mCountDownTimer.setRequestCode(requestCode);
            return this;
        }

        public Builder gross(long gross) {
            mCountDownTimer.setGrossTime(gross);
            return this;
        }

        public Builder interval(long interval) {
            mCountDownTimer.setIntervalTime(interval);
            return this;
        }

        public Builder listener(OnTimerListener listener) {
            mCountDownTimer.setOnTimerListener(listener);
            return this;
        }

        public CountDownTimer build() {
            return mCountDownTimer;
        }
    }
}