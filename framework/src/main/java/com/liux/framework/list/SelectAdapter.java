package com.liux.framework.list;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * 1.实现将对 itemView 的操作回调到 View 层 <br>
 * 2.封装 item 状态存储控制,继而实现单选,多选等业务 <br>
 * 2017-8-11 <br>
 * <br>
 * Created by Liux on 2017/8/9. <br>
 * <br>
 * @deprecated 已将其能力扩展到MultipleAdapter
 */

@Deprecated
public abstract class SelectAdapter<T> extends RecyclerView.Adapter<SuperHolder> {

    private int mLayout;

    public SelectAdapter(@LayoutRes int layout) {
        mLayout = layout;
        mDataSource = new StateList<>();
    }

    /**
     * 使用此方法设置数据源时,由于是新建了一个{@link StateList} <br>
     * 然后调用{@link StateList#addAll(Collection)}方法复制数据 <br>
     * 所以对原始List操作不会同步到适配器数据源 <br>
     * 容易造成混淆,故而废弃此方法 <br>
     * @param layout
     * @param data
     */
    @Deprecated
    public SelectAdapter(@LayoutRes int layout, List<T> data) {
        mLayout = layout;
        mDataSource = StateList.from(data);
    }

    @Override
    public SuperHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return SuperHolder.create(parent, mLayout);
    }

    @Override
    public void onBindViewHolder(SuperHolder holder, int position) {
        State state = mDataSource.getState(position);
        if (!isOpenSelect()) state.state = State.STATE_NONE;
        onDataBind(holder, mDataSource.get(position), state, position);
    }

    @Override
    public int getItemCount() {
        return mDataSource != null ? mDataSource.size() : 0;
    }

    public abstract void onDataBind(SuperHolder holder, T t, State<T> state, int position);

    /* ============== 数据源_Bengin ============== */

    private StateList<T> mDataSource;

    public List<T> getDataSource() {
        return mDataSource;
    }

    /* ============== 数据源_End ============== */



    /* ================ Select_Begin ================ */

    private int mMaxSelectCount = 0;
    private boolean mOpenSelect = false;
    private OnSelectListener mOnSelectListener;

    /**
     * 设置开启选择(单选)
     * @param open
     */
    public void setOpenSelect(boolean open) {
        setOpenSelect(open, 1);
    }

    /**
     * 设置开启选择
     * @param open
     * @param maxSelectCount
     */
    public void setOpenSelect(boolean open, int maxSelectCount) {
        if (mOpenSelect == open) return;
        switchOpenSelect(open);
        mOpenSelect = open;

        if (mOpenSelect && maxSelectCount < 1) {
            maxSelectCount = 1;
        }
        mMaxSelectCount = maxSelectCount;

        notifyDataSetChanged();
    }

    /**
     * 是否开启选择
     * @return
     */
    public boolean isOpenSelect() {
        return mOpenSelect;
    }

    /**
     * 获取最大可选择数
     * @return
     */
    public int getMaxSelectCount() {
        return mMaxSelectCount;
    }

    /**
     * 设置最大可选择数
     * @param count
     */
    public void setMaxSelectCount(int count) {
        if (isOpenSelect()) {
            mMaxSelectCount = count;
        }
    }

    /**
     * 切换某条数据选中状态
     * @param t
     * @return
     */
    public boolean toggle(T t) {
        return toggle(mDataSource.indexOf(t));
    }

    /**
     * 切换某条数据选中状态
     * @param position
     * @return
     */
    public boolean toggle(int position) {
        if (!isOpenSelect()) return false;
        return setSelect(position, !isSelect(position));
    }

    /**
     * 设置某条数据选中状态
     * @param t
     * @param selected
     * @return
     */
    public boolean setSelect(T t, boolean selected) {
        return setSelect(mDataSource.indexOf(t), selected);
    }

    /**
     * 设置某条数据选中状态
     * @param position
     * @param selected
     * @return
     */
    public boolean setSelect(int position, boolean selected) {
        if (!isOpenSelect()) return false;

        if (isSelect(position) == selected) return selected;
        if (selected) {
            if (mMaxSelectCount == 1) {
                // 单选模式
                List<T> ts = mDataSource.getStateAll(State.STATE_SELECTED);
                for (T t : ts) {
                    int index = mDataSource.indexOf(t);
                    mDataSource.setState(index, State.STATE_UNSELECTED);
                    if (mOnSelectListener != null) {
                        mOnSelectListener.onSelectChange(t, index, false);
                    }
                    notifyItemChanged(index);
                }
            } else if (mDataSource.getStateAllCount(State.STATE_SELECTED) >= mMaxSelectCount) {
                return false;
            }
        }

        mDataSource.getState(position).state = selected ? State.STATE_SELECTED : State.STATE_UNSELECTED;

        if (mOnSelectListener != null) {
            mOnSelectListener.onSelectChange(mDataSource.get(position), position, selected);
        }

        if (mDataSource.getStateAllCount(State.STATE_SELECTED) >= mMaxSelectCount) {
            if (mOnSelectListener != null) {
                mOnSelectListener.onSelectComplete();
            }
        }

        notifyItemChanged(position);
        return true;
    }

    /**
     * 全选
     * @return
     */
    public boolean selectAll() {
        if (!isOpenSelect()) return false;

        if (mDataSource.size() > mMaxSelectCount) return false;

        mDataSource.setStateAll(State.STATE_SELECTED);
        notifyDataSetChanged();
        return true;
    }

    /**
     * 全不选
     * @return
     */
    public boolean unSelectAll() {
        if (!isOpenSelect()) return false;

        mDataSource.setStateAll(State.STATE_UNSELECTED);
        notifyDataSetChanged();
        return true;
    }

    /**
     * 反选
     * @return
     */
    public boolean reverseSelectAll() {
        if (!isOpenSelect()) return false;

        int selected = mDataSource.getStateAllCount(State.STATE_SELECTED);
        if (mDataSource.size() - selected > mMaxSelectCount) return false;

        mDataSource.reverseStateAll();
        notifyDataSetChanged();
        return true;
    }

    /**
     * 某条数据是否选中
     * @param t
     * @return
     */
    public boolean isSelect(T t) {
        return isSelect(mDataSource.indexOf(t));
    }

    /**
     * 某条数据是否选中
     * @param position
     * @return
     */
    public boolean isSelect(int position) {
        return mDataSource.getState(position).state == State.STATE_SELECTED;
    }

    /**
     * 获取某种状态的全部数据
     * @param state
     * @return
     */
    public List<T> getStateAll(int state) {
        return mDataSource.getStateAll(state);
    }

    /**
     * 设置选择事件监听
     * @param listener
     */
    public void setOnSelectListener(OnSelectListener<T> listener) {
        mOnSelectListener = listener;
    }

    /**
     * 切换全部数据状态
     * @param open
     */
    private void switchOpenSelect(boolean open) {
        mDataSource.setStateAll(open ? State.STATE_UNSELECTED : State.STATE_NONE);
    }

    /**
     * 选择事件监听器
     * @param <T>
     */
    public interface OnSelectListener<T> {

        /**
         * 选择状态变化
         * @param t
         * @param position
         * @param isSelect
         */
        void onSelectChange(T t, int position, boolean isSelect);

        /**
         * 选择个数达到最大限制数
         */
        void onSelectComplete();
    }

    /**
     * 包裹条目状态的 ArrayList
     * @param <T>
     */
    public static class StateList<T> extends ArrayList<T> {

        private List<State<T>> mStates = new ArrayList<State<T>>();

        public static <T> StateList<T> from(List<T> data) {
            StateList<T> stateList = new StateList<T>();
            if (data != null) {
                stateList.addAll(data);
            }
            return stateList;
        }

        @Override
        public boolean add(T t) {
            mStates.add(new State<T>(t));
            return super.add(t);
        }

        @Override
        public boolean remove(Object o) {
            int index = indexOf(o);
            mStates.remove(index);
            return super.remove(o);
        }

        @Override
        public boolean addAll(@NonNull Collection<? extends T> c) {
            List<State<T>> list = new ArrayList<>();
            for (T t : c) {
                list.add(new State<T>(t));
            }
            mStates.addAll(list);
            return super.addAll(c);
        }

        @Override
        public boolean addAll(int index, @NonNull Collection<? extends T> c) {
            List<State<T>> list = new ArrayList<>();
            for (T t : c) {
                list.add(new State<T>(t));
            }
            mStates.addAll(index, list);
            return super.addAll(index, c);
        }

        @Override
        public boolean removeAll(@NonNull Collection<?> c) {
            Iterator<State<T>> iterator = mStates.iterator();
            while (iterator.hasNext()) {
                State<T> state = iterator.next();
                if (c.contains(state.data)) {
                    iterator.remove();
                }
            }
            return super.removeAll(c);
        }

        @Override
        public boolean retainAll(@NonNull Collection<?> c) {
            Iterator<State<T>> iterator = mStates.iterator();
            while (iterator.hasNext()) {
                State<T> state = iterator.next();
                if (!c.contains(state.data)) {
                    iterator.remove();
                }
            }
            return super.retainAll(c);
        }

        @Override
        public void clear() {
            mStates.clear();
            super.clear();
        }

        @Override
        public T set(int index, T element) {
            mStates.set(index, new State<T>(element));
            return super.set(index, element);
        }

        @Override
        public void add(int index, T element) {
            mStates.add(index, new State<T>(element));
            super.add(index, element);
        }

        @Override
        public T remove(int index) {
            mStates.remove(index);
            return super.remove(index);
        }

        public State<T> getState(int index) {
            return mStates.get(index);
        }

        public State<T> getState(T t) {
            int index = indexOf(t);
            if (index != -1) {
                return mStates.get(index);
            }
            return null;
        }

        public void setState(int index, int state) {
            mStates.get(index).state = state;
        }

        public void setState(T t, int state) {
            int index = indexOf(t);
            if (index != -1) {
                mStates.get(index).state = state;
            }
        }

        public List<State<T>> getStates() {
            return mStates;
        }

        public void setStateAll(int state) {
            for (State<T> s : mStates) {
                s.state = state;
            }
        }

        public List<T> getStateAll(int state) {
            List<T> ts = new ArrayList<T>();
            for (State<T> s : mStates) {
                if (s.state == state) ts.add(s.data);
            }
            return ts;
        }

        public int getStateAllCount(int state) {
            int count = 0;
            for (State<T> s : mStates) {
                if (s.state == state) count++;
            }
            return count;
        }

        public void reverseStateAll() {
            for (State<T> s : mStates) {
                if (s.state == State.STATE_SELECTED) {
                    s.state = State.STATE_UNSELECTED;
                } else {
                    s.state = State.STATE_SELECTED;
                }
            }
        }
    }

    public static class State<T> {
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

    /* ================ Select_End ================ */
}
