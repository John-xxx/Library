package com.liux.list.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 规则管理器
 * @param <T>
 */
public class RuleManage<T> {

    private List<Rule> mRules = new ArrayList<>();

    /**
     * 添加规则
     * {@link MultipleAdapter#addRule(Rule)}
     * @param rule
     */
    void addRule(Rule rule) {
        mRules.add(rule);
    }

    /**
     * 根据数据类型返回规则序号(类型)
     * {@link MultipleAdapter#getItemViewType(int)}
     * @param t
     * @return
     */
    int getRuleType(T t) {
        for (int index = 0; index < mRules.size(); index++) {
            if (mRules.get(index).doBindData(t)) {
                return index;
            }
        }
        throw new IllegalArgumentException("No rule of object [" + t + "] was found");
    }

    /**
     * 根据规则序号(类型)查询对应规则
     * {@link MultipleAdapter#onCreateViewHolder(ViewGroup, int)}
     * @param type
     * @return
     */
    Rule getRuleForType(int type) {
        return mRules.get(type);
    }

    /**
     * 根据目标数据查找对应规则
     * {@link MultipleAdapter#onBindViewHolder(RecyclerView.ViewHolder, int)}
     * @param object
     * @return
     */
    Rule getRuleForObject(Object object) {
        for (Rule rule : mRules) {
            if (rule.doBindData(object)) return rule;
        }
        throw new IllegalArgumentException("No rule of object [" + object + "] was found");
    }
}