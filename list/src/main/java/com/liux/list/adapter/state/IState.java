package com.liux.list.adapter.state;

import android.support.v7.widget.RecyclerView;

import com.liux.list.adapter.append.IAppend;
import com.liux.list.listener.OnSelectListener;

import java.util.List;

/**
 * 2018/3/6
 * By Liux
 * lx0758@qq.com
 */

public interface IState<T, R extends RecyclerView.Adapter> extends IAppend<T, R> {

    void notifyDataSetChanged();

    /**
     * 获取数据源
     * @return
     */
    List<T> getData();

    /**
     * 获取全部状态
     * @return
     */
    List<State<T>> getState();

    /**
     * 是否开启选择
     * @return 开启
     */
    boolean isOpenSelect();

    /**
     * 设置开启选择(单选)
     * @param open 开启
     */
    R setOpenSelect(boolean open);

    /**
     * 设置开启选择
     * @param open 开启
     * @param maxSelectCount 最大数量
     */
    R setOpenSelect(boolean open, int maxSelectCount);

    /**
     * 获取最大可选择数
     * @return 最大可选择数
     */
    int getMaxSelectCount();

    /**
     * 设置最大可选择数
     * @param count 最大可选择数
     */
    R setMaxSelectCount(int count);

    /**
     * 切换某条数据选中状态
     * @param t 数据
     * @return 是否选中
     */
    boolean toggleSelect(T t);

    /**
     * 切换某条数据选中状态
     * @param position 数据位置
     * @return 是否选中
     */
    boolean toggleSelect(int position);

    /**
     * 设置某条数据选中状态
     * @param t 数据
     * @param selected 选中状态
     * @return 是否选中
     */
    boolean setSelect(T t, boolean selected);

    /**
     * 设置某条数据选中状态
     * @param position 数据位置
     * @param selected 选中状态
     * @return 是否选中
     */
    boolean setSelect(int position, boolean selected);

    /**
     * 全选
     * @return 是否全选成功
     */
    boolean selectAll();

    /**
     * 全不选
     * @return 是否全不选成功
     */
    boolean unSelectAll();

    /**
     * 反选
     * @return 是否反选成功
     */
    boolean reverseSelectAll();

    /**
     * 某条数据是否选中
     * @param t 数据
     * @return 是否选中
     */
    boolean isSelect(T t);

    /**
     * 某条数据是否选中
     * @param position 数据位置
     * @return 是否选中
     */
    boolean isSelect(int position);

    /**
     * 获取已选中的全部数据
     * @return 数据列表
     */
    List<T> getSelectedAll();

    /**
     * 获取未选中的全部数据
     * @return 数据列表
     */
    List<T> getUnselectedAll();

    /**
     * 设置选择事件监听
     * @param listener 监听器
     */
    R setOnSelectListener(OnSelectListener<T> listener);
}
