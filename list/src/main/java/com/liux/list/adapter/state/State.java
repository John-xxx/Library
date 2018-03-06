package com.liux.list.adapter.state;

/**
 * 记录选择状态
 * Created by Liux on 2017/9/19.
 */

public class State<T> {

    private T data;

    public State(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }



    // 不可选择
    public static final int STATE_SELECT_CLOSE = -1;
    // 未选中
    public static final int STATE_SELECT_UNSELECTED = 0;
    // 已选中
    public static final int STATE_SELECT_SELECTED = 1;

    private int mSelectState = STATE_SELECT_UNSELECTED;

    public boolean isSelectClose() {
        return this.mSelectState == STATE_SELECT_CLOSE;
    }
    public void setSelectClose() {
        this.mSelectState = STATE_SELECT_CLOSE;
    }

    public boolean isSelectUnselected() {
        return this.mSelectState == STATE_SELECT_UNSELECTED;
    }
    public void setSelectUnselected() {
        this.mSelectState = STATE_SELECT_UNSELECTED;
    }

    public boolean isSelectSelected() {
        return this.mSelectState == STATE_SELECT_SELECTED;
    }
    public void setSelectSelected() {
        this.mSelectState = STATE_SELECT_SELECTED;
    }



    // 不可滑动
    public static final int STATE_SLIDE_CLOSE = -1;
    // 未滑动
    public static final int STATE_SLIDE_UNSLIDE = 0;
    // 已滑动(左滑/右滑)
    public static final int STATE_SLIDE_LEFT = 1;
    public static final int STATE_SLIDE_RIGHT = 2;

    private int mSlideState = STATE_SLIDE_CLOSE;

    public boolean isSlideClose() {
        return this.mSelectState == STATE_SLIDE_CLOSE;
    }
    public void setSlideClose() {
        this.mSelectState = STATE_SLIDE_CLOSE;
    }

    public boolean isSlideUnslide() {
        return this.mSelectState == STATE_SLIDE_UNSLIDE;
    }
    public void setSlideUnslide() {
        this.mSelectState = STATE_SELECT_UNSELECTED;
    }

    public boolean isSlideLift() {
        return this.mSelectState == STATE_SLIDE_LEFT;
    }
    public void setSlideLift() {
        this.mSelectState = STATE_SLIDE_LEFT;
    }

    public boolean isSlideRight() {
        return this.mSelectState == STATE_SLIDE_RIGHT;
    }
    public void setSlideRight() {
        this.mSelectState = STATE_SLIDE_RIGHT;
    }



    private Object mTag = null;

    public Object getTag() {
        return mTag;
    }

    public void setTag(Object tag) {
        this.mTag = tag;
    }



    @Override
    public String toString() {
        return "State{" +
                "data=" + data +
                ", Select=" + mSelectState +
                ", Slide=" + mSlideState +
                ", Tag=" + mTag +
                '}';
    }
}
