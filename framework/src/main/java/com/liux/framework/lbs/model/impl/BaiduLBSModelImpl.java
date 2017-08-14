package com.liux.framework.lbs.model.impl;

import android.content.Context;

import com.liux.framework.lbs.bean.PointBean;
import com.liux.framework.lbs.bean.RouteBean;
import com.liux.framework.lbs.listener.OnLocationListener;
import com.liux.framework.lbs.model.LBSModel;

import java.util.List;

import io.reactivex.FlowableSubscriber;

/**
 * Created by Liux on 2017/8/13.
 */

public class BaiduLBSModelImpl implements LBSModel {
    private static volatile LBSModel mInstance;
    public static LBSModel getInstance() {
        if (mInstance == null) throw new NullPointerException("BaiduLBSModelImpl has not been initialized");
        return mInstance;
    }
    public static void initialize(Context context) {
        if (mInstance != null) return;
        synchronized (BaiduLBSModelImpl.class) {
            if (mInstance != null) return;
            mInstance = new BaiduLBSModelImpl(context);
        }
    }

    private BaiduLBSModelImpl() {

    }

    private BaiduLBSModelImpl(Context context) {

    }

    /**
     * 快速单次网络定位
     *
     * @param subscriber
     */
    @Override
    public void quickLocation(FlowableSubscriber<PointBean> subscriber) {

    }

    /**
     * 开始持续定位
     *
     * @param listener
     */
    @Override
    public void startLocation(OnLocationListener listener) {

    }

    /**
     * 停止持续定位
     *
     * @param listener
     */
    @Override
    public void stopLocation(OnLocationListener listener) {

    }

    /**
     * 地理位置编码
     *
     * @param city
     * @param addr
     * @param subscriber
     */
    @Override
    public void geoCode(String city, String addr, FlowableSubscriber<PointBean> subscriber) {

    }

    /**
     * 逆向地理位置编码
     *
     * @param position
     * @param subscriber
     */
    @Override
    public void reverseGeoCode(PointBean position, FlowableSubscriber<PointBean> subscriber) {

    }

    /**
     * 城市内检索兴趣点
     *
     * @param city
     * @param keyword
     * @param pange
     * @param subscriber
     */
    @Override
    public void queryCityPois(String city, String keyword, int pange, FlowableSubscriber<List<PointBean>> subscriber) {

    }

    /**
     * 周边检索兴趣点
     *
     * @param center
     * @param keyword
     * @param page
     * @param subscriber
     */
    @Override
    public void queryNearbyPois(PointBean center, String keyword, int page, FlowableSubscriber<List<PointBean>> subscriber) {

    }

    /**
     * 区域范围内检索兴趣点<b>百度地图实现</b>
     *
     * @param point_1
     * @param point_2
     * @param keyword
     * @param page
     * @param subscriber
     */
    @Override
    public void queryRegionPois(PointBean point_1, PointBean point_2, String keyword, int page, FlowableSubscriber<List<PointBean>> subscriber) {

    }

    /**
     * 沿途检索兴趣点<b>高德地图实现</b>
     *
     * @param keyword
     * @param page
     * @param subscriber
     */
    @Override
    public void queryEnroutePois(String keyword, int page, FlowableSubscriber<List<PointBean>> subscriber) {

    }

    /**
     * 驾车路径规划
     *
     * @param begin
     * @param end
     * @param middle
     * @param policy
     * @param subscriber
     */
    @Override
    public void queryDriverRoute(PointBean begin, PointBean end, List<PointBean> middle, int policy, FlowableSubscriber<RouteBean> subscriber) {

    }

    /**
     * 公交路线规划
     *
     * @param begin
     * @param end
     * @param middle
     * @param policy
     * @param subscriber
     */
    @Override
    public void queryBusRoute(PointBean begin, PointBean end, List<PointBean> middle, int policy, FlowableSubscriber<RouteBean> subscriber) {

    }

    /**
     * 步行路线规划
     *
     * @param begin
     * @param end
     * @param middle
     * @param policy
     * @param subscriber
     */
    @Override
    public void queryWalkRoute(PointBean begin, PointBean end, List<PointBean> middle, int policy, FlowableSubscriber<RouteBean> subscriber) {

    }

    /**
     * 骑行路径规划
     *
     * @param begin
     * @param end
     * @param middle
     * @param policy
     * @param subscriber
     */
    @Override
    public void queryBikeRoute(PointBean begin, PointBean end, List<PointBean> middle, int policy, FlowableSubscriber<RouteBean> subscriber) {

    }

    /**
     * 行政区域范围查询
     *
     * @param city
     * @param name
     * @param subscriber
     */
    @Override
    public void queryAdministrativeRegion(String city, String name, FlowableSubscriber<List<PointBean>> subscriber) {

    }

    /**
     * 公交线路查询
     *
     * @param city
     * @param name
     * @param subscriber
     */
    @Override
    public void queryBusLines(String city, String name, FlowableSubscriber<Object> subscriber) {

    }

    /**
     * 公交站信息查询<b>高德地图实现</b>
     *
     * @param city
     * @param name
     * @param subscriber
     */
    @Override
    public void queryBusStation(String city, String name, FlowableSubscriber<PointBean> subscriber) {

    }
}