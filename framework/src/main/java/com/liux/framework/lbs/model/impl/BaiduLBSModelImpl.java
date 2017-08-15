package com.liux.framework.lbs.model.impl;

import android.content.Context;
import android.text.TextUtils;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiBoundSearchOption;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
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
        Flowable.just(new String[] {city, addr})
                .map(new Function<String[], GeoCodeOption>() {
                    @Override
                    public GeoCodeOption apply(@NonNull String[] strings) throws Exception {
                        return new GeoCodeOption()
                                .city(strings[0])
                                .address(strings[1]);
                    }
                })
                .switchMap(new Function<GeoCodeOption, Publisher<GeoCodeResult>>() {
                    @Override
                    public Publisher<GeoCodeResult> apply(@NonNull final GeoCodeOption geoCodeOption) throws Exception {
                        return new Publisher<GeoCodeResult>() {
                            @Override
                            public void subscribe(final Subscriber<? super GeoCodeResult> subscriber) {
                                final GeoCoder geoCoder = GeoCoder.newInstance();
                                geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
                                    @Override
                                    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                                        subscriber.onNext(geoCodeResult);
                                        subscriber.onComplete();
                                        geoCoder.destroy();
                                    }

                                    @Override
                                    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

                                    }
                                });
                                geoCoder.geocode(geoCodeOption);
                            }
                        };
                    }
                })
                .map(new Function<GeoCodeResult, PointBean>() {
                    @Override
                    public PointBean apply(@NonNull GeoCodeResult geoCodeResult) throws Exception {
                        if (geoCodeResult == null || geoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                            throw new NullPointerException("反向解码地理位置失败.");
                        }
                        LatLng ll = geoCodeResult.getLocation();
                        return new PointBean()
                                .setLat(ll.latitude)
                                .setLon(ll.longitude)
                                .setAddress(geoCodeResult.getAddress());
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
                                final GeoCoder geoCoder = GeoCoder.newInstance();
                                geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
                                    @Override
                                    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

                                    }

                                    @Override
                                    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                                        s.onNext(reverseGeoCodeResult);
                                        s.onComplete();
                                        geoCoder.destroy();
                                    }
                                });
                                geoCoder.reverseGeoCode(reverseGeoCodeOption);
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
    public void queryCityPois(final String city, final String keyword, final String type, final int page, final int num, FlowableSubscriber<List<PointBean>> subscriber) {
        Flowable.just(new PoiCitySearchOption())
                .map(new Function<PoiCitySearchOption, PoiCitySearchOption>() {
                    @Override
                    public PoiCitySearchOption apply(@NonNull PoiCitySearchOption poiCitySearchOption) throws Exception {
                        String key = keyword;
                        if (TextUtils.isEmpty(key) && !TextUtils.isEmpty(type)) {
                            key = type;
                        }
                        return poiCitySearchOption
                                .city(city)
                                .keyword(key)
                                .pageNum(page)
                                .pageCapacity(num);
                    }
                })
                .switchMap(new Function<PoiCitySearchOption, Publisher<PoiResult>>() {
                    @Override
                    public Publisher<PoiResult> apply(@NonNull final PoiCitySearchOption poiCitySearchOption) throws Exception {
                        return new Publisher<PoiResult>() {
                            @Override
                            public void subscribe(final Subscriber<? super PoiResult> subscriber) {
                                final PoiSearch poiSearch = PoiSearch.newInstance();
                                poiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
                                    @Override
                                    public void onGetPoiResult(PoiResult poiResult) {
                                        subscriber.onNext(poiResult);
                                        subscriber.onComplete();
                                        poiSearch.destroy();
                                    }

                                    @Override
                                    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

                                    }

                                    @Override
                                    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

                                    }
                                });
                                poiSearch.searchInCity(poiCitySearchOption);
                            }
                        };
                    }
                })
                .map(new Function<PoiResult, List<PointBean>>() {
                    @Override
                    public List<PointBean> apply(@NonNull PoiResult poiResult) throws Exception {
                        if (poiResult != null) {
                            if (poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
                                throw new NullPointerException("检索结果为空,请核对关键字后重试.");
                            }
                            if (poiResult.error != SearchResult.ERRORNO.NO_ERROR) {
                                throw new NullPointerException("检索结果为空,请核对关键字后重试.");
                            }
                        } else {
                            throw new NullPointerException("检索结果为空,请检查网络连接.");
                        }

                        List<PointBean> pois = new ArrayList();
                        for (PoiInfo poi : poiResult.getAllPoi()) {
                            if (poi.type == PoiInfo.POITYPE.BUS_LINE || poi.type == PoiInfo.POITYPE.SUBWAY_LINE) {
                                /* 忽略公交线路和地铁线路 */
                                continue;
                            }
                            if (poi.city == null || poi.name == null || poi.address == null) {
                                /* 忽略空值 */
                                continue;
                            }
                            poi.city = poi.city.trim();
                            poi.name = poi.name.trim();
                            poi.address = poi.address.trim();
                            if (poi.city.isEmpty() || poi.name.isEmpty() || poi.address.isEmpty()) {
                                /* 忽略空值 */
                                continue;
                            }
                            pois.add(new PointBean()
                                    .setCity(poi.city)
                                    .setLat(poi.location.latitude)
                                    .setLon(poi.location.longitude)
                                    .setTitle(poi.name)
                                    .setAddress(poi.address));
                        }
                        if (pois.isEmpty()) {
                            throw new NullPointerException("检索结果为空,请核对关键字后重试.");
                        }
                        return pois;
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
    public void queryNearbyPois(PointBean center, final String keyword, final String type, final int page, final int num, FlowableSubscriber<List<PointBean>> subscriber) {
        Flowable.just(center)
                .map(new Function<PointBean, PoiNearbySearchOption>() {
                    @Override
                    public PoiNearbySearchOption apply(@NonNull PointBean pointBean) throws Exception {
                        String key = keyword;
                        if (TextUtils.isEmpty(key) && !TextUtils.isEmpty(type)) {
                            key = type;
                        }
                        return new PoiNearbySearchOption()
                                .location(new LatLng(pointBean.getLat(), pointBean.getLon()))
                                .keyword(key)
                                .radius(5000)
                                .pageNum(page)
                                .pageCapacity(num)
                                .sortType(PoiSortType.distance_from_near_to_far);
                    }
                })
                .switchMap(new Function<PoiNearbySearchOption, Publisher<PoiResult>>() {
                    @Override
                    public Publisher<PoiResult> apply(@NonNull final PoiNearbySearchOption poiNearbySearchOption) throws Exception {
                        return new Publisher<PoiResult>() {
                            @Override
                            public void subscribe(final Subscriber<? super PoiResult> subscriber) {
                                final PoiSearch poiSearch = PoiSearch.newInstance();
                                poiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
                                    @Override
                                    public void onGetPoiResult(PoiResult poiResult) {
                                        subscriber.onNext(poiResult);
                                        subscriber.onComplete();
                                        poiSearch.destroy();
                                    }

                                    @Override
                                    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

                                    }

                                    @Override
                                    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

                                    }
                                });
                                poiSearch.searchNearby(poiNearbySearchOption);
                            }
                        };
                    }
                })
                .map(new Function<PoiResult, List<PointBean>>() {
                    @Override
                    public List<PointBean> apply(@NonNull PoiResult poiResult) throws Exception {
                        if (poiResult != null) {
                            if (poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
                                throw new NullPointerException("检索结果为空,请核对关键字后重试.");
                            }
                            if (poiResult.error != SearchResult.ERRORNO.NO_ERROR) {
                                throw new NullPointerException("检索结果为空,请核对关键字后重试.");
                            }
                        } else {
                            throw new NullPointerException("检索结果为空,请检查网络连接.");
                        }

                        List<PointBean> pois = new ArrayList();
                        for (PoiInfo poi : poiResult.getAllPoi()) {
                            if (poi.type == PoiInfo.POITYPE.BUS_LINE || poi.type == PoiInfo.POITYPE.SUBWAY_LINE) {
                                /* 忽略公交线路和地铁线路 */
                                continue;
                            }
                            if (poi.city == null || poi.name == null || poi.address == null) {
                                /* 忽略空值 */
                                continue;
                            }
                            poi.city = poi.city.trim();
                            poi.name = poi.name.trim();
                            poi.address = poi.address.trim();
                            if (poi.city.isEmpty() || poi.name.isEmpty() || poi.address.isEmpty()) {
                                /* 忽略空值 */
                                continue;
                            }
                            pois.add(new PointBean()
                                    .setCity(poi.city)
                                    .setLat(poi.location.latitude)
                                    .setLon(poi.location.longitude)
                                    .setTitle(poi.name)
                                    .setAddress(poi.address));
                        }
                        if (pois.isEmpty()) {
                            throw new NullPointerException("检索结果为空,请核对关键字后重试.");
                        }
                        return pois;
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
    public void queryRegionPois(PointBean point_1, PointBean point_2, final String keyword, final String type, final int page, final int num, FlowableSubscriber<List<PointBean>> subscriber) {
        Flowable.just(new PointBean[] {point_1, point_2})
                .map(new Function<PointBean[], PoiBoundSearchOption>() {
                    @Override
                    public PoiBoundSearchOption apply(@NonNull PointBean[] pointBeen) throws Exception {
                        String key = keyword;
                        if (TextUtils.isEmpty(key) && !TextUtils.isEmpty(type)) {
                            key = type;
                        }
                        return new PoiBoundSearchOption()
                                .bound(new LatLngBounds.Builder()
                                        .include(new LatLng(pointBeen[0].getLat(), pointBeen[0].getLon()))
                                        .include(new LatLng(pointBeen[1].getLat(), pointBeen[1].getLon()))
                                        .build())
                                .keyword(key)
                                .pageNum(page)
                                .pageCapacity(num);
                    }
                })
                .switchMap(new Function<PoiBoundSearchOption, Publisher<PoiResult>>() {
                    @Override
                    public Publisher<PoiResult> apply(@NonNull final PoiBoundSearchOption poiBoundSearchOption) throws Exception {
                        return new Publisher<PoiResult>() {
                            @Override
                            public void subscribe(final Subscriber<? super PoiResult> subscriber) {
                                final PoiSearch poiSearch = PoiSearch.newInstance();
                                poiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
                                    @Override
                                    public void onGetPoiResult(PoiResult poiResult) {
                                        subscriber.onNext(poiResult);
                                        subscriber.onComplete();
                                        poiSearch.destroy();
                                    }

                                    @Override
                                    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

                                    }

                                    @Override
                                    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

                                    }
                                });
                                poiSearch.searchInBound(poiBoundSearchOption);
                            }
                        };
                    }
                })
                .map(new Function<PoiResult, List<PointBean>>() {
                    @Override
                    public List<PointBean> apply(@NonNull PoiResult poiResult) throws Exception {
                        if (poiResult != null) {
                            if (poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
                                throw new NullPointerException("检索结果为空,请核对关键字后重试.");
                            }
                            if (poiResult.error != SearchResult.ERRORNO.NO_ERROR) {
                                throw new NullPointerException("检索结果为空,请核对关键字后重试.");
                            }
                        } else {
                            throw new NullPointerException("检索结果为空,请检查网络连接.");
                        }

                        List<PointBean> pois = new ArrayList();
                        for (PoiInfo poi : poiResult.getAllPoi()) {
                            if (poi.type == PoiInfo.POITYPE.BUS_LINE || poi.type == PoiInfo.POITYPE.SUBWAY_LINE) {
                                /* 忽略公交线路和地铁线路 */
                                continue;
                            }
                            if (poi.city == null || poi.name == null || poi.address == null) {
                                /* 忽略空值 */
                                continue;
                            }
                            poi.city = poi.city.trim();
                            poi.name = poi.name.trim();
                            poi.address = poi.address.trim();
                            if (poi.city.isEmpty() || poi.name.isEmpty() || poi.address.isEmpty()) {
                                /* 忽略空值 */
                                continue;
                            }
                            pois.add(new PointBean()
                                    .setCity(poi.city)
                                    .setLat(poi.location.latitude)
                                    .setLon(poi.location.longitude)
                                    .setTitle(poi.name)
                                    .setAddress(poi.address));
                        }
                        if (pois.isEmpty()) {
                            throw new NullPointerException("检索结果为空,请核对关键字后重试.");
                        }
                        return pois;
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