package com.liux.framework.list.adapter;

/**
 * 记录选择状态
 * Created by Liux on 2017/9/19.
 */

public class State<T> {
    // 不可操作
    public static final int STATE_NONE = -1;
    // 未选择
    public static final int STATE_UNSELECTED = 0;
    // 已选中
    public static final int STATE_SELECTED = 1;

    public T data;
    public int state = STATE_UNSELECTED;

    public State(T data) {
        this.data = data;
    }

    public boolean isSelected() {
        return this.state == STATE_SELECTED;
    }
}
