package com.liux.list.adapter.state;

import com.liux.list.listener.OnSelectListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 2018/3/6
 * By Liux
 * lx0758@qq.com
 */

public class StateProxy<T> {

    private IState mIState;

    private int mMaxSelectCount = 0;
    private boolean mOpenSelect = false;
    private StateList<T> mStateList = new StateList<>();

    private OnSelectListener<T> mOnSelectListener;

    public StateProxy(IState iState) {
        mIState = iState;
    }

    public void setData(List<T> dataSource) {
        if (dataSource != null) {
            mStateList.addAll(dataSource);
        }
    }

    public StateList<T> getData() {
        return mStateList;
    }

    public List<State<T>> getState() {
        return mStateList.getStates();
    }

    public boolean isOpenSelect() {
        return mOpenSelect;
    }

    public void setOpenSelect(boolean open) {
        setOpenSelect(open, 1);
    }

    public void setOpenSelect(boolean open, int maxSelectCount) {
        if (isOpenSelect() == open) return;

        openSelectState(open);
        mOpenSelect = open;

        if (isOpenSelect() && maxSelectCount < 1) {
            maxSelectCount = 1;
        }
        setMaxSelectCount(maxSelectCount);

        mIState.notifyDataSetChanged();
    }

    public int getMaxSelectCount() {
        return mMaxSelectCount;
    }

    public void setMaxSelectCount(int count) {
        if (isOpenSelect()) {
            mMaxSelectCount = count;
        }
    }

    public boolean toggleSelect(T t) {
        return toggleSelect(getData().indexOf(t));
    }

    public boolean toggleSelect(int position) {
        if (!isOpenSelect()) return false;
        return setSelect(position, !isSelect(position));
    }

    public boolean setSelect(T t, boolean selected) {
        return setSelect(getData().indexOf(t), selected);
    }

    public boolean setSelect(int position, boolean selected) {
        if (!isOpenSelect()) return false;

        if (isSelect(position) == selected) return selected;

        List<T> selectedAll = getSelectedAll();
        if (selected) {
            if (mMaxSelectCount == 1) {
                // 单选模式反转上一个
                for (T t : selectedAll) {
                    int index = getData().indexOf(t);

                    boolean result = true;
                    if (mOnSelectListener != null) {
                        result = mOnSelectListener.onSelectChange(t, index, false);
                    }
                    if (!result) {
                        //if (mOnSelectListener != null) {
                        //    mOnSelectListener.onSelectFailure();
                        //}
                        return false;
                    }

                    getData().getState(index).setSelectUnselected();

                    index = mIState.getShamPosition(index);
                    mIState.notifyItemChanged(index);
                }
            } else if (selectedAll.size() >= mMaxSelectCount) {
                // 多选模式检查
                if (mOnSelectListener != null) {
                    mOnSelectListener.onSelectFailure();
                }
                return false;
            }
        }

        boolean result = true;
        if (mOnSelectListener != null) {
            result = mOnSelectListener.onSelectChange(getData().get(position), position, selected);
        }
        if (!result) {
            //if (mOnSelectListener != null) {
            //    mOnSelectListener.onSelectFailure();
            //}
            return false;
        }

        State<T> state = getData().getState(position);
        if (selected) {
            state.setSelectSelected();
        } else {
            state.setSelectUnselected();
        }

        int size = getSelectedAll().size();
        if (size >= mMaxSelectCount) {
            if (mOnSelectListener != null) {
                mOnSelectListener.onSelectComplete();
            }
        }

        position = mIState.getShamPosition(position);
        mIState.notifyItemChanged(position);
        return true;
    }

    public boolean selectAll() {
        if (!isOpenSelect()) return false;

        if (getData().size() > mMaxSelectCount) {
            if (mOnSelectListener != null) {
                mOnSelectListener.onSelectFailure();
            }
            return false;
        }

        switchSelectState(true);
        mIState.notifyDataSetChanged();
        return true;
    }

    public boolean unSelectAll() {
        if (!isOpenSelect()) return false;

        switchSelectState(false);
        mIState.notifyDataSetChanged();
        return true;
    }

    public boolean reverseSelectAll() {
        if (!isOpenSelect()) return false;

        int size = getSelectedAll().size();
        if (getData().size() - size > mMaxSelectCount) {
            if (mOnSelectListener != null) {
                mOnSelectListener.onSelectFailure();
            }
            return false;
        }

        reverseSelectStateAll();
        mIState.notifyDataSetChanged();
        return true;
    }

    public boolean isSelect(T t) {
        return isSelect(getData().indexOf(t));
    }

    public boolean isSelect(int position) {
        return getData().getState(position).isSelectSelected();
    }

    public List<T> getSelectedAll() {
        return getSelectStateAll(true);
    }

    public List<T> getUnselectedAll() {
        return getSelectStateAll(false);
    }

    public void setOnSelectListener(OnSelectListener<T> listener) {
        mOnSelectListener = listener;
    }

    /**
     * 开关数据状态
     * @param open 是否开启选择
     */
    private void openSelectState(boolean open) {
        for (State<T> s : getData().getStates()) {
            if (open) {
                s.setSelectUnselected();
            } else {
                s.setSelectClose();
            }
        }
    }

    /**
     * 切换数据状态
     * @param isSelect 是否选择
     */
    private void switchSelectState(boolean isSelect) {
        for (State<T> s : getData().getStates()) {
            if (isSelect) {
                s.setSelectSelected();
            } else {
                s.setSelectUnselected();
            }
        }
    }

    /**
     * 不考虑反转后数量的操作
     */
    private void reverseSelectStateAll() {
        for (State<T> s : getData().getStates()) {
            if (s.isSelectSelected()) {
                s.setSelectUnselected();
            } else {
                s.setSelectSelected();
            }
        }
    }

    /**
     * 获取某个状态所有数据
     * @param isSelect
     * @return
     */
    private List<T> getSelectStateAll(boolean isSelect) {
        List<T> list = new ArrayList<>();
        if (!isOpenSelect()) return list;
        for (State<T> s: getData().getStates()) {
            if (isSelect) {
                if (s.isSelectSelected()) list.add(s.getData());
            } else {
                if (s.isSelectUnselected()) list.add(s.getData());
            }
        }
        return list;
    }
}
