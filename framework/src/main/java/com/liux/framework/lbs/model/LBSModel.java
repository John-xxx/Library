package com.liux.framework.lbs.model;

import com.liux.framework.lbs.bean.PointBean;
import com.liux.framework.lbs.bean.RouteBean;
import com.liux.framework.lbs.listener.OnLocationListener;

import java.util.List;

import io.reactivex.Observer;

/**
 * Created by Liux on 2017/8/7.
 */

public interface LBSModel {

    /**
     * 单次快速定位
     * @param observer
     */
    void quickLocation(Observer<PointBean> observer);

    /**
     * 单次精确定位
     * @param observer
     */
    void accuracyLocation(Observer<PointBean> observer);

    /**
     * 开始持续定位
     * @param listener
     */
    void startLocation(OnLocationListener listener);

    /**
     * 停止持续定位
     * @param listener
     */
    void stopLocation(OnLocationListener listener);

    /**
     * 地理位置编码
     * @param city
     * @param addr
     * @param observer
     */
    void geoCode(String city, String addr, Observer<PointBean> observer);

    /**
     * 逆向地理位置编码
     * @param point
     * @param observer
     */
    void reverseGeoCode(PointBean point, Observer<PointBean> observer);

    /**
     * 城市内检索兴趣点
     * @param city
     * @param keyword
     * @param type
     * @param page
     * @param num
     * @param observer
     */
    void queryCityPois(String city, String keyword, String type, int page, int num, Observer<List<PointBean>> observer);

    /**
     * 周边检索兴趣点
     * @param center
     * @param keyword
     * @param type
     * @param page
     * @param num
     * @param observer
     */
    void queryNearbyPois(PointBean center, String keyword, String type, int page, int num, Observer<List<PointBean>> observer);

    /**
     * 区域范围内检索兴趣点
     * @param point_1 东北方向点
     * @param point_2 西南方向点
     * @param keyword
     * @param type
     * @param page
     * @param num
     * @param observer
     */
    void queryRegionPois(PointBean point_1, PointBean point_2, String keyword, String type, int page, int num, Observer<List<PointBean>> observer);

    /**
     * 驾车路径规划
     * @param begin
     * @param end
     * @param middle
     * @param policy
     * @param observer
     */
    void queryDriverRoute(PointBean begin, PointBean end, List<PointBean> middle, int policy, Observer<List<RouteBean>> observer);

//    /**
//     * 公交路线规划
//     * @param begin
//     * @param end
//     * @param middle
//     * @param policy
//     * @param observer
//     */
//    void queryBusRoute(PointBean begin, PointBean end, List<PointBean> middle, int policy, Observer<List<RouteBean>> observer);
//
//    /**
//     * 步行路线规划
//     * @param begin
//     * @param end
//     * @param middle
//     * @param policy
//     * @param observer
//     */
//    void queryWalkRoute(PointBean begin, PointBean end, List<PointBean> middle, int policy, Observer<List<RouteBean>> observer);
//
//    /**
//     * 骑行路径规划
//     * @param begin
//     * @param end
//     * @param middle
//     * @param policy
//     * @param observer
//     */
//    void queryBikeRoute(PointBean begin, PointBean end, List<PointBean> middle, int policy, Observer<List<RouteBean>> observer);

    /**
     * 行政区域范围查询
     * @param city
     * @param name
     * @param observer
     */
    void queryAdministrativeRegion(String city, String name, Observer<List<List<PointBean>>> observer);
}
