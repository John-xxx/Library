package com.liux.framework.banner;

import android.support.annotation.LayoutRes;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * ViewPager里面对每个页面的管理是key-value形式的，也就是说每个page都有个对应的id（id是object类型），
 * 需要对page操作的时候都是通过id来完成的
 * https://segmentfault.com/q/1010000000484617
 * Created by Liux on 2017/8/28.
 */

public abstract class BannerAdapter<T> extends PagerAdapter {

    private int mLayout;
    private List<T> mDataSource;

    public BannerAdapter(List<T> data, @LayoutRes int layout) {
        mLayout = layout;
        mDataSource = data;
    }

    /**
     * 获取数据数量
     * @return
     */
    @Override
    public int getCount() {
        return mDataSource == null ? 0 : Integer.MAX_VALUE;
    }

    /**
     * 获取数据真实的数量
     * @return
     */
    public int getRealityCount() {
        return mDataSource == null ? 0 : mDataSource.size();
    }

    /**
     * 初始化Item
     * @param container
     * @param position
     * @return
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        BannerHolder holder = BannerHolder.create(container, mLayout);

        int index = position % getRealityCount();

        onBindData(holder, mDataSource.get(index), position);
        container.addView(holder.getItemView());

        return holder;
    }

    /**
     * 判断是否是要显示的数据
     * @param view
     * @param object
     * @return
     */
    @Override
    public boolean isViewFromObject(View view, Object object) {
        BannerHolder holder = (BannerHolder) object;
        return view == holder.getItemView();
    }

    /**
     * 当Item超出ViewPager缓存范围时,将调用此方法销毁View
     * @param container
     * @param position
     * @param object
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        BannerHolder holder = (BannerHolder) object;
        container.removeView(holder.getItemView());
    }

    public abstract void onBindData(BannerHolder holder, T t, int position);
}
