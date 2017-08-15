package com.liux.framework.lbs.model.impl;

import android.content.Context;
import android.graphics.Point;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.AoiItem;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.nearby.NearbySearch;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.liux.framework.lbs.bean.PointBean;
import com.liux.framework.lbs.bean.RouteBean;
import com.liux.framework.lbs.listener.OnLocationListener;
import com.liux.framework.lbs.model.LBSModel;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Liux on 2017/8/13.
 */

public class AMapLBSModelImpl implements LBSModel {
    private static volatile LBSModel mInstance;
    public static LBSModel getInstance() {
        if (mInstance == null) throw new NullPointerException("BaiduLBSModelImpl has not been initialized");
        return mInstance;
    }
    public static void initialize(Context context) {
        if (mInstance != null) return;
        synchronized (AMapLBSModelImpl.class) {
            if (mInstance != null) return;
            mInstance = new AMapLBSModelImpl(context);
        }
    }

    private Context mContext;

    private AMapLocationClient mAMapLocationClient;
    private AMapLocationClientOption mAMapLocationClientOption;

    private List<OnLocationListener> mOnLocationListeners = new ArrayList<>();
    private AMapLocationListener mAMapLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (mOnLocationListeners.isEmpty()) {
                mAMapLocationClient.stopLocation();
                return;
            }

            PointBean pointBean = AMapLocation2PointBean(aMapLocation);

            for (OnLocationListener listener : mOnLocationListeners) {
                if (listener == null) continue;
                if (pointBean != null) {
                    listener.onSucceed(pointBean);
                } else {
                    listener.onFailure();
                }
            }
        }
    };

    public AMapLBSModelImpl() {

    }

    public AMapLBSModelImpl(Context context) {
        mContext = context.getApplicationContext();

        mAMapLocationClient = new AMapLocationClient(mContext);

        mAMapLocationClientOption = new AMapLocationClientOption();
        // 设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mAMapLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        // 设置单次定位
        mAMapLocationClientOption.setOnceLocation(false);
        // 只有在单次定位高精度定位模式下有效设置为true时，会等待GPS定位结果返回，
        // 最多等待30秒，若30秒后仍无GPS结果返回，返回网络定位结果
        mAMapLocationClientOption.setGpsFirst(true);
        // 设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。
        // 如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mAMapLocationClientOption.setOnceLocationLatest(false);
        // 设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
        mAMapLocationClientOption.setInterval(2 * 1000);
        // 设置是否返回地址信息（默认返回地址信息）
        mAMapLocationClientOption.setNeedAddress(true);
        // 设置是否强制刷新WIFI，默认为true，强制刷新。
        mAMapLocationClientOption.setWifiScan(true);
        // 设置是否允许模拟位置,默认为false，不允许模拟位置
        mAMapLocationClientOption.setMockEnable(false);
        // 单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mAMapLocationClientOption.setHttpTimeOut(8 * 1000);
        // 开启缓存机制
        mAMapLocationClientOption.setLocationCacheEnable(true);
        // 当不是GPS模式时返回方向,海拔高度,速度信息
        mAMapLocationClientOption.setSensorEnable(true);

        mAMapLocationClient.setLocationOption(mAMapLocationClientOption);
        mAMapLocationClient.setLocationListener(mAMapLocationListener);
    }

    /**
     * 快速单次网络定位
     *
     * @param subscriber
     */
    @Override
    public void quickLocation(FlowableSubscriber<PointBean> subscriber) {
        AMapLocationClientOption aMapLocationClientOption = mAMapLocationClientOption.clone();
        aMapLocationClientOption.setOnceLocation(true);
        aMapLocationClientOption.setGpsFirst(false);
        location(aMapLocationClientOption, subscriber);
    }

    /**
     * 单次精确定位
     *
     * @param subscriber
     */
    @Override
    public void accuracyLocation(FlowableSubscriber<PointBean> subscriber) {
        AMapLocationClientOption aMapLocationClientOption = mAMapLocationClientOption.clone();
        aMapLocationClientOption.setOnceLocation(true);
        aMapLocationClientOption.setGpsFirst(true);
        location(aMapLocationClientOption, subscriber);
    }

    private void location(AMapLocationClientOption aMapLocationClientOption, FlowableSubscriber<PointBean> subscriber) {
        if (mAMapLocationClient.isStarted()) {
            AMapLocation aMapLocation = mAMapLocationClient.getLastKnownLocation();
            PointBean pointBean = AMapLocation2PointBean(aMapLocation);
            if (pointBean != null) {
                subscriber.onNext(pointBean);
                subscriber.onComplete();
                return;
            }
        }
        Flowable.just(aMapLocationClientOption)
                .switchMap(new Function<AMapLocationClientOption, Publisher<AMapLocation>>() {
                    @Override
                    public Publisher<AMapLocation> apply(@NonNull final AMapLocationClientOption aMapLocationClientOption) throws Exception {
                        return new Publisher<AMapLocation>() {
                            @Override
                            public void subscribe(final Subscriber<? super AMapLocation> s) {
                                final AMapLocationClient aMapLocationClient = new AMapLocationClient(mContext);
                                aMapLocationClient.setLocationOption(aMapLocationClientOption);
                                aMapLocationClient.setLocationListener(new AMapLocationListener() {
                                    @Override
                                    public void onLocationChanged(AMapLocation aMapLocation) {
                                        aMapLocationClient.unRegisterLocationListener(this);
                                        aMapLocationClient.stopLocation();
                                        aMapLocationClient.onDestroy();
                                        s.onNext(aMapLocation);
                                        s.onComplete();
                                    }
                                });
                                aMapLocationClient.startLocation();
                            }
                        };
                    }
                })
                .map(new Function<AMapLocation, PointBean>() {
                    @Override
                    public PointBean apply(@NonNull AMapLocation aMapLocation) throws Exception {
                        PointBean pointBean = AMapLocation2PointBean(aMapLocation);
                        if (pointBean == null) {
                            throw new NullPointerException("定位失败,请检查网络连接.");
                        }
                        return pointBean;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * 开始持续定位
     *
     * @param listener
     */
    @Override
    public void startLocation(OnLocationListener listener) {
        if (listener == null) return;

        if (mOnLocationListeners.isEmpty()) {
            mAMapLocationClient.startLocation();
        }
        mOnLocationListeners.add(listener);
    }

    /**
     * 停止持续定位
     *
     * @param listener
     */
    @Override
    public void stopLocation(OnLocationListener listener) {
        if (mOnLocationListeners.contains(listener)) {
            mOnLocationListeners.remove(listener);
        }

        if (mOnLocationListeners.isEmpty()) {
            mAMapLocationClient.stopLocation();
        }
    }

    /**
     * 地理位置编码
     *
     * @param city 城市编码/城市名称/行政区划代码
     * @param addr
     * @param subscriber
     */
    @Override
    public void geoCode(String city, String addr, FlowableSubscriber<PointBean> subscriber) {
        Flowable.just(new String[] {city, addr})
                .map(new Function<String[], GeocodeQuery>() {
                    @Override
                    public GeocodeQuery apply(@NonNull String[] strings) throws Exception {
                        return new GeocodeQuery(strings[1], strings[0]);
                    }
                })
                .map(new Function<GeocodeQuery, List<GeocodeAddress>>() {
                    @Override
                    public List<GeocodeAddress> apply(@NonNull GeocodeQuery geocodeQuery) throws Exception {
                        return new GeocodeSearch(mContext).getFromLocationName(geocodeQuery);
                    }
                })
                .map(new Function<List<GeocodeAddress>, PointBean>() {
                    @Override
                    public PointBean apply(@NonNull List<GeocodeAddress> geocodeAddresses) throws Exception {
                        GeocodeAddress address = geocodeAddresses.get(0);
                        return new PointBean()
                                .setLat(address.getLatLonPoint().getLatitude())
                                .setLon(address.getLatLonPoint().getLongitude())
                                .setCity(address.getCity())
                                .setAddress(address.getFormatAddress());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * 逆向地理位置编码
     *
     * @param pointBean
     * @param subscriber
     */
    @Override
    public void reverseGeoCode(final PointBean pointBean, FlowableSubscriber<PointBean> subscriber) {
        Flowable.just(pointBean)
                .map(new Function<PointBean, RegeocodeQuery>() {
                    @Override
                    public RegeocodeQuery apply(@NonNull PointBean pointBean) throws Exception {
                        LatLonPoint point = new LatLonPoint(pointBean.getLat(), pointBean.getLon());
                        return new RegeocodeQuery(
                                point ,
                                2000,
                                GeocodeSearch.AMAP);
                    }
                })
                .map(new Function<RegeocodeQuery, RegeocodeAddress>() {
                    @Override
                    public RegeocodeAddress apply(@NonNull RegeocodeQuery regeocodeQuery) throws Exception {
                        return new GeocodeSearch(mContext).getFromLocation(regeocodeQuery);
                    }
                })
                .map(new Function<RegeocodeAddress, PointBean>() {
                    @Override
                    public PointBean apply(@NonNull RegeocodeAddress regeocodeAddress) throws Exception {
                        String city = regeocodeAddress.getCity(), title = null, address = null;

                        if (regeocodeAddress.getAois() != null && !regeocodeAddress.getAois().isEmpty()) {
                            AoiItem aoi = regeocodeAddress.getAois().get(0);
                            title = aoi.getAoiName();
                        }
                        if (regeocodeAddress.getPois() != null && !regeocodeAddress.getPois().isEmpty()) {
                            PoiItem poi = regeocodeAddress.getPois().get(0);
                            if (title == null || title.trim().isEmpty()) {
                                title = poi.getTitle();
                            }
                            address = poi.getSnippet();
                        }
                        if (city == null || city.trim().isEmpty()) {
                            city = "未知";
                        }
                        if (title == null || title.trim().isEmpty()) {
                            title = regeocodeAddress.getTownship();
                        }
                        if (title == null || title.trim().isEmpty()) {
                            title = regeocodeAddress.getNeighborhood();
                        }
                        if (title == null || title.trim().isEmpty()) {
                            title = regeocodeAddress.getBuilding();
                        }
                        if (title == null || title.trim().isEmpty()) {
                            title = "(位置未知)";
                        }
                        if (address == null || address.trim().isEmpty()) {
                            address = regeocodeAddress.getFormatAddress();
                        }
                        if (address == null || address.trim().isEmpty()) {
                            address = "(地址未知)";
                        }

                        city = city.trim();
                        title = title.trim();
                        address = address.trim();
                        return new PointBean()
                                .setCity(city)
                                .setLat(pointBean.getLat())
                                .setLon(pointBean.getLon())
                                .setTitle(title)
                                .setAddress(address);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * 城市内检索兴趣点
     *
     * @param city
     * @param keyword
     * @param type
     * @param page
     * @param num
     * @param subscriber
     */
    @Override
    public void queryCityPois(String city, String keyword, String type, final int page, final int num, FlowableSubscriber<List<PointBean>> subscriber) {
        Flowable.just(new String[] {city, keyword, type})
                .map(new Function<String[], PoiSearch.Query>() {
                    @Override
                    public PoiSearch.Query apply(@NonNull String[] strings) throws Exception {
                        // 第一个参数表示搜索字符串
                        // 第二个参数表示poi搜索类型
                        // 第三个参数表示poi搜索区域（空字符串代表全国）
                        PoiSearch.Query query = new PoiSearch.Query(strings[1], strings[2], strings[0]);
                        query.setPageNum(page);
                        query.setPageSize(num);
                        query.setCityLimit(true);
                        return query;
                    }
                })
                .map(new Function<PoiSearch.Query, PoiResult>() {
                    @Override
                    public PoiResult apply(@NonNull PoiSearch.Query query) throws Exception {
                        return new PoiSearch(mContext, query).searchPOI();
                    }
                })
                .map(new Function<PoiResult, List<PointBean>>() {
                    @Override
                    public List<PointBean> apply(PoiResult poiResult) {
                        List<PointBean> infos = new ArrayList<>();
                        List<PoiItem> pois = poiResult.getPois();
                        for (PoiItem item : pois) {
                            infos.add(new PointBean()
                                    .setCity(item.getCityName())
                                    .setLat(item.getLatLonPoint().getLatitude())
                                    .setLon(item.getLatLonPoint().getLongitude())
                                    .setTitle(item.getTitle())
                                    .setAddress(item.getSnippet()));
                        }
                        if (page == 0 && infos.isEmpty()) {
                            throw new NullPointerException("POI搜索结果为空,请检查关键词后重试.");
                        }
                        return infos;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

    }

    /**
     * 周边检索兴趣点
     *
     * @param center
     * @param keyword
     * @param type
     * @param page
     * @param num
     * @param subscriber
     */
    @Override
    public void queryNearbyPois(final PointBean center, String keyword, String type, final int page, final int num, FlowableSubscriber<List<PointBean>> subscriber) {
        Flowable.just(new String[] {keyword, type})
                .map(new Function<String[], PoiSearch.Query>() {
                    @Override
                    public PoiSearch.Query apply(@NonNull String[] strings) throws Exception {
                        // 第一个参数表示搜索字符串
                        // 第二个参数表示poi搜索类型
                        // 第三个参数表示poi搜索区域（空字符串代表全国）
                        PoiSearch.Query query = new PoiSearch.Query(strings[0], strings[1], "");
                        query.setPageNum(page);
                        query.setPageSize(num);
                        query.setCityLimit(true);
                        return query;
                    }
                })
                .map(new Function<PoiSearch.Query, PoiResult>() {
                    @Override
                    public PoiResult apply(@NonNull PoiSearch.Query query) throws Exception {
                        PoiSearch poiSearch = new PoiSearch(mContext, query);
                        poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(center.getLat(), center.getLon()), 5000, true));
                        return poiSearch.searchPOI();
                    }
                })
                .map(new Function<PoiResult, List<PointBean>>() {
                    @Override
                    public List<PointBean> apply(PoiResult poiResult) {
                        List<PointBean> infos = new ArrayList<>();
                        List<PoiItem> pois = poiResult.getPois();
                        for (PoiItem item : pois) {
                            infos.add(new PointBean()
                                    .setCity(item.getCityName())
                                    .setLat(item.getLatLonPoint().getLatitude())
                                    .setLon(item.getLatLonPoint().getLongitude())
                                    .setTitle(item.getTitle())
                                    .setAddress(item.getSnippet()));
                        }
                        if (page == 0 && infos.isEmpty()) {
                            throw new NullPointerException("POI搜索结果为空,请检查关键词后重试.");
                        }
                        return infos;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * 区域范围内检索兴趣点
     *
     * @param point_1 东北方向点
     * @param point_2 西南方向点
     * @param keyword
     * @param type
     * @param page
     * @param num
     * @param subscriber
     */
    @Override
    public void queryRegionPois(final PointBean point_1, final PointBean point_2, String keyword, String type, final int page, final int num, FlowableSubscriber<List<PointBean>> subscriber) {
        Flowable.just(new String[] {keyword, type})
                .map(new Function<String[], PoiSearch.Query>() {
                    @Override
                    public PoiSearch.Query apply(@NonNull String[] strings) throws Exception {
                        // 第一个参数表示搜索字符串
                        // 第二个参数表示poi搜索类型
                        // 第三个参数表示poi搜索区域（空字符串代表全国）
                        PoiSearch.Query query = new PoiSearch.Query(strings[0], strings[1], "");
                        query.setPageNum(page);
                        query.setPageSize(num);
                        query.setCityLimit(true);
                        return query;
                    }
                })
                .map(new Function<PoiSearch.Query, PoiResult>() {
                    @Override
                    public PoiResult apply(@NonNull PoiSearch.Query query) throws Exception {
                        PoiSearch poiSearch = new PoiSearch(mContext, query);
                        poiSearch.setBound(new PoiSearch.SearchBound(
                                new LatLonPoint(point_1.getLat(), point_1.getLon()),
                                new LatLonPoint(point_2.getLat(), point_2.getLon())
                        ));
                        return poiSearch.searchPOI();
                    }
                })
                .map(new Function<PoiResult, List<PointBean>>() {
                    @Override
                    public List<PointBean> apply(PoiResult poiResult) {
                        List<PointBean> infos = new ArrayList<>();
                        List<PoiItem> pois = poiResult.getPois();
                        for (PoiItem item : pois) {
                            infos.add(new PointBean()
                                    .setCity(item.getCityName())
                                    .setLat(item.getLatLonPoint().getLatitude())
                                    .setLon(item.getLatLonPoint().getLongitude())
                                    .setTitle(item.getTitle())
                                    .setAddress(item.getSnippet()));
                        }
                        if (page == 0 && infos.isEmpty()) {
                            throw new NullPointerException("POI搜索结果为空,请检查关键词后重试.");
                        }
                        return infos;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
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
    public void queryDriverRoute(PointBean begin, PointBean end, List<PointBean> middle, int policy, FlowableSubscriber<List<RouteBean>> subscriber) {

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
    public void queryBusRoute(PointBean begin, PointBean end, List<PointBean> middle, int policy, FlowableSubscriber<List<RouteBean>> subscriber) {

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
    public void queryWalkRoute(PointBean begin, PointBean end, List<PointBean> middle, int policy, FlowableSubscriber<List<RouteBean>> subscriber) {

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
    public void queryBikeRoute(PointBean begin, PointBean end, List<PointBean> middle, int policy, FlowableSubscriber<List<RouteBean>> subscriber) {

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

    private PointBean AMapLocation2PointBean(AMapLocation aMapLocation) {
        if (null == aMapLocation || aMapLocation.getErrorCode() != AMapLocation.LOCATION_SUCCESS) return null;

        if (null == aMapLocation.getAdCode() || aMapLocation.getAdCode().isEmpty()) return null;

        int code = Integer.valueOf(aMapLocation.getAdCode());
        PointBean point = new PointBean().setCode(code);

        String title, address, mode;

        title = aMapLocation.getAoiName();
        if (title == null || title.trim().isEmpty()) {
            title = aMapLocation.getPoiName();
        }
        if (title == null || title.trim().isEmpty()) {
            if (aMapLocation.getStreet() != null && !aMapLocation.getStreet().isEmpty()) {
                title = aMapLocation.getStreet() + (aMapLocation.getStreetNum() != null && !aMapLocation.getStreetNum().trim().isEmpty() ? aMapLocation.getStreetNum() : "");
            }
        }
        if (title == null || title.trim().isEmpty()) {
            title = aMapLocation.getDistrict();
        }
        if (title == null || title.trim().isEmpty()) {
            title = aMapLocation.getAddress();
        }
        if (title == null || title.trim().isEmpty()){
            title = "(位置未知)";
        }

        address = aMapLocation.getAddress();
        if (address == null || address.trim().isEmpty()){
            address = "(地址未知)";
        }

        switch (aMapLocation.getLocationType()) {
            case AMapLocation.LOCATION_TYPE_GPS:
                mode = "gps";
                break;
            case AMapLocation.LOCATION_TYPE_OFFLINE:
                mode = "offine";
                break;
            case AMapLocation.LOCATION_TYPE_WIFI:
                mode = "wifi";
                break;
            case AMapLocation.LOCATION_TYPE_CELL:
                mode = "net";
                break;
            case AMapLocation.LOCATION_TYPE_FIX_CACHE:
            case AMapLocation.LOCATION_TYPE_SAME_REQ:
                mode = "cache";
                break;
            default:
                mode = "unknown";
                break;
        }

        title = title.trim();
        address = address.trim();
        mode = mode.trim();
        point
                .setLat(aMapLocation.getLatitude())
                .setLon(aMapLocation.getLongitude())
                .setDirection(aMapLocation.getBearing())
                .setAccuracy(aMapLocation.getAccuracy())
                .setAltitude(aMapLocation.getAltitude())
                .setSpeed(aMapLocation.getSpeed())
                .setTitle(title)
                .setAddress(address)
                .setMode(mode);

        return point;
    }

    public static AMapLocation PointBean2BDLocation(PointBean point) {
        AMapLocation location = new AMapLocation("");

        return location;
    }
}
