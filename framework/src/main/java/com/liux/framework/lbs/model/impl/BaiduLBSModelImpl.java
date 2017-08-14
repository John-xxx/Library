package com.liux.framework.lbs.model.impl;

import android.content.Context;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.route.RoutePlanSearch;
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
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

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

    private Context mContext;

    private GeoCoder mGeoCoder;
    private PoiSearch mPoiSearch;
    private RoutePlanSearch mRoutePlanSearch;

    private LocationClient mLocationClient;
    private LocationClientOption mLocationClientOption;

    private List<OnLocationListener> mOnLocationListeners = new ArrayList<>();
    private BDAbstractLocationListener mBDAbstractLocationListener = new BDAbstractLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (mOnLocationListeners.isEmpty()) {
                mLocationClient.stop();
                return;
            }

            PointBean pointBean = BDLocation2PointBean(bdLocation);

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

    private BaiduLBSModelImpl() {

    }

    private BaiduLBSModelImpl(Context context) {
        mContext = context.getApplicationContext();

        mGeoCoder = GeoCoder.newInstance();
        mPoiSearch = PoiSearch.newInstance();
        mRoutePlanSearch = RoutePlanSearch.newInstance();

        mLocationClient = new LocationClient(mContext);

        mLocationClientOption = new LocationClientOption();
        // 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        mLocationClientOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        // 可选，默认gcj02，设置返回的定位结果坐标系
        mLocationClientOption.setCoorType("gcj02");
        // 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        mLocationClientOption.setScanSpan(2 * 1000);
        // 可选，设置是否需要地址信息，默认不需要
        mLocationClientOption.setIsNeedAddress(true);
        // 可选，默认false,设置是否使用gps
        mLocationClientOption.setOpenGps(true);
        // 可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        mLocationClientOption.setLocationNotify(false);
        // 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        mLocationClientOption.setIsNeedLocationDescribe(true);
        // 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        mLocationClientOption.setIsNeedLocationPoiList(true);
        // 可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        mLocationClientOption.setIgnoreKillProcess(true);
        // 可选，默认false，设置是否收集CRASH信息，默认收集
        mLocationClientOption.SetIgnoreCacheException(false);
        // 可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClientOption.setEnableSimulateGps(false);
        // 可选，默认不晓得
        mLocationClientOption.setTimeOut(8 * 1000);
        // 在网络定位时，是否需要设备方向 true:需要;false:不需要
        mLocationClientOption.setNeedDeviceDirect(true);

        mLocationClient.setLocOption(mLocationClientOption);
        mLocationClient.registerLocationListener(mBDAbstractLocationListener);
    }

    /**
     * 快速单次网络定位
     *
     * @param subscriber
     */
    @Override
    public void quickLocation(FlowableSubscriber<PointBean> subscriber) {
        LocationClientOption locationClientOption = new LocationClientOption(mLocationClientOption);
        locationClientOption.setScanSpan(0);
        locationClientOption.setOpenGps(false);
        location(locationClientOption, subscriber);
    }

    /**
     * 单次精确定位
     *
     * @param subscriber
     */
    @Override
    public void accuracyLocation(FlowableSubscriber<PointBean> subscriber) {
        LocationClientOption locationClientOption = new LocationClientOption(mLocationClientOption);
        locationClientOption.setScanSpan(0);
        locationClientOption.setOpenGps(true);
        location(locationClientOption, subscriber);
    }

    private void location(LocationClientOption locationClientOption, FlowableSubscriber<PointBean> subscriber) {
        if (mLocationClient.isStarted()) {
            BDLocation bdLocation = mLocationClient.getLastKnownLocation();
            PointBean pointBean = BDLocation2PointBean(bdLocation);
            if (pointBean != null) {
                subscriber.onNext(pointBean);
                subscriber.onComplete();
                return;
            }
        }
        Flowable.just(locationClientOption)
                .switchMap(new Function<LocationClientOption, Publisher<BDLocation>>() {
                    @Override
                    public Publisher<BDLocation> apply(@NonNull final LocationClientOption locationClientOption) throws Exception {
                        return new Publisher<BDLocation>() {
                            @Override
                            public void subscribe(final Subscriber<? super BDLocation> s) {
                                final LocationClient locationClient = new LocationClient(mContext);
                                locationClient.setLocOption(locationClientOption);
                                locationClient.registerLocationListener(new BDAbstractLocationListener() {
                                    @Override
                                    public void onReceiveLocation(BDLocation bdLocation) {
                                        locationClient.unRegisterLocationListener(this);
                                        locationClient.stop();
                                        s.onNext(bdLocation);
                                        s.onComplete();
                                    }
                                });
                                locationClient.start();
                            }
                        };
                    }
                })
                .map(new Function<BDLocation, PointBean>() {
                    @Override
                    public PointBean apply(@NonNull BDLocation bdLocation) throws Exception {
                        PointBean pointBean = BDLocation2PointBean(bdLocation);
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
            mLocationClient.restart();
            mLocationClient.requestLocation();
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
            mLocationClient.stop();
        }
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
     * @param pointBean
     * @param subscriber
     */
    @Override
    public void reverseGeoCode(PointBean pointBean, FlowableSubscriber<PointBean> subscriber) {
        Flowable.just(pointBean)
                .map(new Function<PointBean, ReverseGeoCodeOption>() {
                    @Override
                    public ReverseGeoCodeOption apply(@NonNull PointBean pointBean) throws Exception {
                        return new ReverseGeoCodeOption().location(new LatLng(pointBean.getLat(), pointBean.getLon()));
                    }
                })
                .switchMap(new Function<ReverseGeoCodeOption, Publisher<ReverseGeoCodeResult>>() {
                    @Override
                    public Publisher<ReverseGeoCodeResult> apply(@NonNull final ReverseGeoCodeOption reverseGeoCodeOption) throws Exception {
                        return new Publisher<ReverseGeoCodeResult>() {
                            @Override
                            public void subscribe(final Subscriber<? super ReverseGeoCodeResult> s) {
                                mGeoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
                                    @Override
                                    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

                                    }

                                    @Override
                                    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                                        s.onNext(reverseGeoCodeResult);
                                        s.onComplete();
                                    }
                                });
                                mGeoCoder.reverseGeoCode(reverseGeoCodeOption);
                            }
                        };
                    }
                })
                .map(new Function<ReverseGeoCodeResult, PointBean>() {
                    @Override
                    public PointBean apply(ReverseGeoCodeResult result) {
                        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                            throw new NullPointerException("反向解码地理位置失败.");
                        }

                        String city = null, title = null, address = null;

                        List<PoiInfo> infos = result.getPoiList();
                        ReverseGeoCodeResult.AddressComponent addressComponent = result.getAddressDetail();
                        if (infos != null  && !infos.isEmpty()) {
                            PoiInfo info = infos.get(0);
                            city = result.getAddressDetail().city;
                            title = info.name;
                            address = info.address;
                        }
                        if (city == null || city.trim().isEmpty()) {
                            city = addressComponent.city;
                        }
                        if (city == null || city.trim().isEmpty()) {
                            city = "未知";
                        }
                        if (title == null || title.trim().isEmpty()) {
                            if (addressComponent != null && addressComponent.street != null && !addressComponent.street.trim().isEmpty()) {
                                title = addressComponent.street.trim() + (addressComponent.streetNumber != null && !addressComponent.streetNumber.trim().isEmpty() ? addressComponent.streetNumber.trim() : "");
                            }
                        }
                        if (title == null || title.trim().isEmpty()) {
                            title = "(位置未知)";
                        }
                        if (address == null || address.trim().isEmpty()) {
                            if (result.getAddress() != null) {
                                address = result.getAddress().trim();
                            }
                        }
                        if (address == null || address.trim().isEmpty()) {
                            address = "(地址未知)";
                        }

                        city = city.trim();
                        title = title.trim();
                        address = address.trim();
                        return new PointBean()
                                .setCity(city)
                                .setLat(result.getLocation().latitude)
                                .setLon(result.getLocation().longitude)
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
    public void queryCityPois(String city, String keyword, String type, int page, int num, FlowableSubscriber<List<PointBean>> subscriber) {

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
    public void queryNearbyPois(PointBean center, String keyword, String type, int page, int num, FlowableSubscriber<List<PointBean>> subscriber) {

    }

    /**
     * 区域范围内检索兴趣点<b>百度地图实现</b>
     *
     * @param point_1
     * @param point_2
     * @param keyword
     * @param type
     * @param page
     * @param num
     * @param subscriber
     */
    @Override
    public void queryRegionPois(PointBean point_1, PointBean point_2, String keyword, String type, int page, int num, FlowableSubscriber<List<PointBean>> subscriber) {

    }

    /**
     * 沿途检索兴趣点<b>高德地图实现</b>
     *
     * @param keyword
     * @param type
     * @param page
     * @param num
     * @param subscriber
     */
    @Override
    public void queryEnroutePois(String keyword, String type, int page, int num, FlowableSubscriber<List<PointBean>> subscriber) {

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

    private PointBean BDLocation2PointBean(BDLocation location) {
        if (location == null) return null;
        if (location.getCityCode() == null) return null;
        if (location.getLatitude() < 0.1 || location.getLongitude() < 0.1) return null;

        int bdid = Integer.valueOf(location.getCityCode());
        PointBean point = new PointBean().setBdid(bdid);

        String title = null, address, mode;

        if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
            title = location.getPoiList().get(0).getName();
        }
        if (title == null || title.trim().isEmpty()) {
            title = location.getLocationDescribe();
        }
        if (title == null || title.trim().isEmpty()){
            title = location.getAddrStr();
        }
        if (title == null || title.trim().isEmpty()){
            title = "(位置未知)";
        }
        address = location.getAddrStr();
        if (address == null || address.trim().isEmpty()){
            address = "(地址未知)";
        }

        switch (location.getLocType()) {
            case BDLocation.TypeGpsLocation:
                mode = "gps";
                break;
            case BDLocation.TypeOffLineLocation:
                mode = "offine";
                break;
            case BDLocation.TypeNetWorkLocation:
                switch (location.getNetworkLocationType()) {
                    case "cl":
                        mode = "net";
                        break;
                    case "wf":
                        mode = "wifi";
                        break;
                    default:
                        mode = "net";
                        break;
                }
                break;
            case BDLocation.TypeCacheLocation:
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
                .setLat(location.getLatitude())
                .setLon(location.getLongitude())
                .setDirection(location.getDirection())
                .setAccuracy(location.getRadius())
                .setAltitude(location.getAltitude())
                .setSpeed(location.getSpeed())
                .setTitle(title)
                .setAddress(address)
                .setMode(mode);

        return point;
    }

    public static BDLocation PointBean2BDLocation(PointBean point) {
        BDLocation location = new BDLocation("");

        location.setLatitude(point.getLat());
        location.setLongitude(point.getLon());
        location.setDirection(point.getDirection());
        location.setAltitude(point.getAltitude());
        location.setRadius(point.getAccuracy());
        location.setSpeed(point.getSpeed());
        location.setLocationDescribe(point.getTitle());
        location.setAddrStr(point.getAddress());

        return location;
    }
}