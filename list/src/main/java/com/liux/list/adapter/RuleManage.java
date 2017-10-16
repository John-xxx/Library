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

    private List<Rule<? extends T>> mRules = new ArrayList<>();

    /**
     * 添加规则
     * {@link MultipleAdapter#addRule(Rule)}
     * @param rule
     */
    void addRule(Rule<? extends T> rule) {
        mRules.add(rule);
    }

    /**
     * 根据数据类型返回规则序号(类型)
     * {@link MultipleAdapter#getItemViewType(int)}
     * @param t
     * @return
     */
    int getRuleType(T t) {
        for (int i = 0; i < mRules.size(); i++) {
            if (mRules.get(i).doBindData(t)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 根据规则序号(类型)查询对应规则
     * {@link MultipleAdapter#onCreateViewHolder(ViewGroup, int)}
     * @param type
     * @return
     */
    Rule<? extends T> getRuleForType(int type) {
        if (type == -1) {
            throw new IllegalArgumentException("No rule of type " + type + " was found");
        }
        return mRules.get(type);
    }

    /**
     * 根据目标数据查找对应规则
     * {@link MultipleAdapter#onBindViewHolder(RecyclerView.ViewHolder, int)}
     * @param object
     * @return
     */
    Rule<? extends T> getRuleForObject(Object object) {
        for (Rule<? extends T> rule : mRules) {
            if (rule.doBindData(object)) return rule;
        }
        throw new IllegalArgumentException("No rule of object " + object + " was found");
    }
}