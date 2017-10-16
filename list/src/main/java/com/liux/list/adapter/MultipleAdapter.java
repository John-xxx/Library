package com.liux.list.adapter;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import com.liux.list.holder.MarginHolder;
import com.liux.list.holder.SuperHolder;
import com.liux.list.listener.OnSelectListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 支持多项条目的Adapter <br>
 * 典型的应用场景如聊天界面,不同消息类型显示不同布局 <br>
 * 原理是根据不同数据添加不同规则,显示数据时反查询规则实例 <br>
 * Created by Liux on 2017/8/11. <br>
 * <br>
 * 完成适配器 <br>
 * 2017-8-11 <br>
 * <br>
 * 移植添加数据状态能力 <br>
 * 2017-8-11
 */

public class MultipleAdapter<T> extends RecyclerView.Adapter {

    public MultipleAdapter() {
        mDataSource = new StateList<T>();
    }

    /**
     * 使用此方法设置数据源时,由于是新建了一个{@link StateList} <br>
     * 然后调用{@link StateList#addAll(Collection)}方法复制数据 <br>
     * 所以对原始List操作不会同步到适配器数据源 <br>
     * 容易造成混淆,故而废弃此方法
     * @param dataSource
     */
    @Deprecated
    public MultipleAdapter(List<T> dataSource) {
        mDataSource = StateList.from(dataSource);
    }

    @Override
    public int getItemViewType(int position) {
        if (position < mHeaders.size()) return ITEM_VIEW_TYPE_HEADER;
        if (position >= getItemCount() - mFooters.size()) return ITEM_VIEW_TYPE_FOOTER;

        position = getRealPosition(position);

        T t = mDataSource.get(position);
        return mRuleManage.getRuleType(t);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_VIEW_TYPE_HEADER) {
            return mHeaders.get(0);
        }

        if (viewType == ITEM_VIEW_TYPE_FOOTER) {
            return mFooters.get(0);
        }

        Rule rule = mRuleManage.getRuleForType(viewType);
        return SuperHolder.create(parent, rule.layout);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MarginHolder) {
            return;
        }

        position = getRealPosition(position);

        T t = mDataSource.get(position);
        Rule rule = mRuleManage.getRuleForObject(t);
        State state = mDataSource.getState(position);
        if (!isOpenSelect()) state.state = State.STATE_NONE;
        rule.onDataBind((SuperHolder) holder, t, state, position);
    }

    /**
     * 取当前条目总数,包括页眉和页脚
     * @return
     */
    @Override
    public int getItemCount() {
        int count = 0;
        if (!mHeaders.isEmpty()) count = count + mHeaders.size();
        if (!mFooters.isEmpty()) count = count + mFooters.size();
        if (!mDataSource.isEmpty()) count = count + mDataSource.size();
        return count;
    }

    /* ============== 数据源_Bengin ============== */

    private StateList<T> mDataSource;

    public List<T> getDataSource() {
        return mDataSource;
    }

    /* ============== 数据源_End ============== */



    /* ============== 绑定规则_Begin ============== */

    private RuleManage<T> mRuleManage = new RuleManage<>();

    /**
     * 添加数据和视图关联规则
     * @param rule
     * @return
     */
    public MultipleAdapter<T> addRule(Rule<? extends T> rule) {
        mRuleManage.addRule(rule);
        return this;
    }

    /* ============== 绑定规则_End ============== */



    /* ============== Header/Footer_Bengin ============== */

    private static final int ITEM_VIEW_TYPE_HEADER = -10;
    private static final int ITEM_VIEW_TYPE_FOOTER = -20;

    private List<MarginHolder> mHeaders = new ArrayList<>();
    private List<MarginHolder> mFooters = new ArrayList<>();

    /**
     * 设置页眉布局
     * @param view
     */
    public MultipleAdapter<T> setHeader(View view) {
        if (mHeaders.isEmpty()) {
            mHeaders.add(0, new MarginHolder(view));
            notifyItemInserted(0);
        } else {
            mHeaders.set(0, new MarginHolder(view));
            notifyItemChanged(0);
        }
        return this;
    }

    /**
     * 设置页脚布局
     * @param view
     */
    public MultipleAdapter<T> setFooter(View view) {
        if (mFooters.isEmpty()) {
            mFooters.add(0, new MarginHolder(view));
            notifyItemInserted(getItemCount() - 1);
        } else {
            mFooters.set(0, new MarginHolder(view));
            notifyItemChanged(getItemCount() - 1);
        }
        return this;
    }

    /**
     * 获取除去 Header/Footer 之后真实的位置
     * @param position
     * @return
     */
    public int getRealPosition(int position) {
        return position - mHeaders.size();
    }

    /**
     * 获取包含 Header/Footer 之后真实的位置
     * @param position
     * @return
     */
    public int getShamPosition(int position) {
        return position + mHeaders.size();
    }

    /**
     * 检查是否是页眉布局
     * @param position
     * @return
     */
    public boolean isHeaderPosition(int position) {
        return getItemViewType(position) == ITEM_VIEW_TYPE_HEADER;
    }

    /**
     * 检查是否是页脚布局
     * @param position
     * @return
     */
    public boolean isFooterPosition(int position) {
        return getItemViewType(position) == ITEM_VIEW_TYPE_FOOTER;
    }

    /**
     * 适配当 RecyclerView.LayoutManager() 为 GridLayoutManager()
     *
     * @param recyclerView
     */
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if(manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = (GridLayoutManager) manager;
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int type = getItemViewType(position);
                    switch (type) {
                        case ITEM_VIEW_TYPE_HEADER:
                        case ITEM_VIEW_TYPE_FOOTER:
                            return gridManager.getSpanCount();
                        default:
                            return 1;
                    }
                }
            });
        }
    }

    /**
     * 适配当 RecyclerView.LayoutManager() 为 StaggeredGridLayoutManager()
     *
     * @param holder
     */
    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        int type = getItemViewType(holder.getItemViewType());
        switch (type) {
            case ITEM_VIEW_TYPE_HEADER:
            case ITEM_VIEW_TYPE_FOOTER:
                ViewGroup.LayoutParams lp_vg = holder.itemView.getLayoutParams();
                if(lp_vg != null && lp_vg instanceof StaggeredGridLayoutManager.LayoutParams) {
                    StaggeredGridLayoutManager.LayoutParams lp_sglm = (StaggeredGridLayoutManager.LayoutParams) lp_vg;
                    lp_sglm.setFullSpan(true);
                }
                break;
            default:
                break;
        }
    }

    /* ============== Header/Footer_Bengin ============== */



    /* ============== 数据状态_Begin ============== */

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
                    index = getShamPosition(index);
                    notifyItemChanged(index);
                }
            } else if (mDataSource.getStateAllCount(State.STATE_SELECTED) >= mMaxSelectCount) {
                if (mOnSelectListener != null) {
                    mOnSelectListener.onSelectFailure();
                }
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

        position = getShamPosition(position);
        notifyItemChanged(position);
        return true;
    }

    /**
     * 全选
     * @return
     */
    public boolean selectAll() {
        if (!isOpenSelect()) return false;

        if (mDataSource.size() > mMaxSelectCount) {
            if (mOnSelectListener != null) {
                mOnSelectListener.onSelectFailure();
            }
            return false;
        }

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
        if (mDataSource.size() - selected > mMaxSelectCount) {
            if (mOnSelectListener != null) {
                mOnSelectListener.onSelectFailure();
            }
            return false;
        }

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

    /* ============== 数据状态_End ============== */
}
