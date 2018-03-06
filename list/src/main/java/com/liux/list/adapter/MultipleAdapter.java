package com.liux.list.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.liux.list.adapter.append.AppendProxy;
import com.liux.list.adapter.append.IAppend;
import com.liux.list.adapter.rule.IRule;
import com.liux.list.adapter.rule.Rule;
import com.liux.list.adapter.rule.RuleProxy;
import com.liux.list.adapter.state.IState;
import com.liux.list.adapter.state.State;
import com.liux.list.adapter.state.StateProxy;
import com.liux.list.holder.MarginHolder;
import com.liux.list.listener.OnSelectListener;

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

public class MultipleAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        IRule<T, MultipleAdapter>, IState<T, MultipleAdapter>, IAppend<T, MultipleAdapter> {

    private RuleProxy<T> mRuleProxy = new RuleProxy<>(this);
    private StateProxy<T> mStateProxy = new StateProxy<>(this);
    private AppendProxy<T> mAppendProxy = new AppendProxy<>(this);

    public MultipleAdapter() {

    }

    @Override
    public int getItemViewType(int position) {
        if (mAppendProxy.isAppendPosition(position)) {
            return mAppendProxy.getAppendPositionType(position);
        }

        position = mAppendProxy.getRealPosition(position);

        T t = getData().get(position);
        return mRuleProxy.getRuleType(t);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mAppendProxy.isAppendType(viewType)) {
            return mAppendProxy.getAppendTypeHolder(viewType);
        }

        Rule rule = mRuleProxy.getTypeRule(viewType);
        return rule.createHolder(parent, rule.layout);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MarginHolder) {
            return;
        }

        position = getRealPosition(position);

        T t = getData().get(position);
        Rule rule = mRuleProxy.getObjectRule(t);

        State state = mStateProxy.getData().getState(position);
        if (!isOpenSelect()) state.setSelectClose();

        rule.onDataBind(holder, t, state, position);
    }

    @Override
    public int getItemCount() {
        int count = 0;
        count += mAppendProxy.getAppendItemCount();
        count += mStateProxy.getData().size();
        return count;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mAppendProxy.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        mAppendProxy.onViewAttachedToWindow(holder);
    }

    @Override
    public MultipleAdapter<T> addRule(Rule<? extends T, ? extends RecyclerView.ViewHolder> rule) {
        mRuleProxy.addRule(rule);
        return this;
    }

    @Override
    public MultipleAdapter<T> setHeader(View view) {
        mAppendProxy.setHeader(view);
        return this;
    }

    @Override
    public MultipleAdapter<T> setFooter(View view) {
        mAppendProxy.setFooter(view);
        return this;
    }

    @Override
    public int getRealPosition(int position) {
        return mAppendProxy.getRealPosition(position);
    }

    @Override
    public int getShamPosition(int position) {
        return mAppendProxy.getShamPosition(position);
    }

    @Override
    public boolean isHeaderPosition(int position) {
        return mAppendProxy.isHeaderPosition(position);
    }

    @Override
    public boolean isFooterPosition(int position) {
        return mAppendProxy.isFooterPosition(position);
    }

    @Override
    public List<T> getData() {
        return mStateProxy.getData();
    }

    @Override
    public List<State<T>> getState() {
        return mStateProxy.getState();
    }

    @Override
    public boolean isOpenSelect() {
        return mStateProxy.isOpenSelect();
    }

    @Override
    public MultipleAdapter<T> setOpenSelect(boolean open) {
        mStateProxy.setOpenSelect(open);
        return this;
    }

    @Override
    public MultipleAdapter<T> setOpenSelect(boolean open, int maxSelectCount) {
        mStateProxy.setOpenSelect(open, maxSelectCount);
        return this;
    }

    @Override
    public int getMaxSelectCount() {
        return mStateProxy.getMaxSelectCount();
    }

    @Override
    public MultipleAdapter<T> setMaxSelectCount(int count) {
        mStateProxy.setMaxSelectCount(count);
        return this;
    }

    @Override
    public boolean toggleSelect(T t) {
        return mStateProxy.toggleSelect(t);
    }

    @Override
    public boolean toggleSelect(int position) {
        return mStateProxy.toggleSelect(position);
    }

    @Override
    public boolean setSelect(T t, boolean selected) {
        return mStateProxy.setSelect(t, selected);
    }

    @Override
    public boolean setSelect(int position, boolean selected) {
        return mStateProxy.setSelect(position, selected);
    }

    @Override
    public boolean selectAll() {
        return mStateProxy.selectAll();
    }

    @Override
    public boolean unSelectAll() {
        return mStateProxy.unSelectAll();
    }

    @Override
    public boolean reverseSelectAll() {
        return mStateProxy.reverseSelectAll();
    }

    @Override
    public boolean isSelect(T t) {
        return mStateProxy.isSelect(t);
    }

    @Override
    public boolean isSelect(int position) {
        return mStateProxy.isSelect(position);
    }

    @Override
    public List<T> getSelectedAll() {
        return mStateProxy.getSelectedAll();
    }

    @Override
    public List<T> getUnselectedAll() {
        return mStateProxy.getUnselectedAll();
    }

    @Override
    public MultipleAdapter<T> setOnSelectListener(OnSelectListener<T> listener) {
        mStateProxy.setOnSelectListener(listener);
        return this;
    }
}
