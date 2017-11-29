package com.liux.lbs.model.impl;

import android.content.Context;
import android.text.TextUtils;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.district.DistrictResult;
import com.baidu.mapapi.search.district.DistrictSearch;
import com.baidu.mapapi.search.district.DistrictSearchOption;
import com.baidu.mapapi.search.district.OnGetDistricSearchResultListener;
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
import com.baidu.mapapi.search.route.BikingRoutePlanOption;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.liux.lbs.bean.PointBean;
import com.liux.lbs.bean.RouteBean;
import com.liux.lbs.bean.StepBean;
import com.liux.lbs.listener.OnLocationListener;
import com.liux.lbs.model.LBSModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
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
    public static void destroy() {
        if (mInstance != null) {
            ((BaiduLBSModelImpl) mInstance).destroyInstance();
        }
    }

    private Context mContext;

    private LocationClient mLocationClient;
    private LocationClientOption mLocationClientOption;

    private List<OnLocationListener> mOnLocationListeners = new ArrayList<>();
    private BDLocationListener mBDLocationListener = new BDLocationListener() {
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
                    listener.onFailure("定位失败,原因:");
                }
            }
        }
    };

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
        mLocationClient.registerNotifyLocationListener(mBDLocationListener);
    }

    /**
     * 销毁实例
     */
    private void destroyInstance() {
        mInstance = null;
        mContext = null;
        mOnLocationListeners.clear();
        mLocationClient.unRegisterLocationListener(mBDLocationListener);
        mLocationClient.stop();
        mLocationClient = null;
    }

    /**
     * 快速单次网络定位
     *
     * @param observer
     */
    @Override
    public void quickLocation(Observer<PointBean> observer) {
        LocationClientOption locationClientOption = new LocationClientOption(mLocationClientOption);
        locationClientOption.setScanSpan(0);
        locationClientOption.setOpenGps(false);

        // 2017-8-18
        // LocationClient.isStarted() 逻辑是当其运行时才会返回 true
        // LocationClient.getLastKnownLocation() 返回的是最后一次的结果
        if (mLocationClient.isStarted()) {
            BDLocation bdLocation = mLocationClient.getLastKnownLocation();
            PointBean pointBean = BDLocation2PointBean(bdLocation);
            if (pointBean != null) {
                observer.onNext(pointBean);
                observer.onComplete();
                return;
            }
        }
        location(locationClientOption, observer);
    }

    /**
     * 单次精确定位
     *
     * @param observer
     */
    @Override
    public void accuracyLocation(Observer<PointBean> observer) {
        LocationClientOption locationClientOption = new LocationClientOption(mLocationClientOption);
        locationClientOption.setScanSpan(0);
        locationClientOption.setOpenGps(true);
        location(locationClientOption, observer);
    }

    private void location(LocationClientOption locationClientOption, final Observer<PointBean> observer) {
        Observable.just(locationClientOption)
                .switchMap(new Function<LocationClientOption, ObservableSource<BDLocation>>() {
                    @Override
                    public ObservableSource<BDLocation> apply(@NonNull final LocationClientOption locationClientOption) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<BDLocation>() {
                            @Override
                            public void subscribe(@NonNull final ObservableEmitter<BDLocation> observableEmitter) throws Exception {
                                final LocationClient locationClient = new LocationClient(mContext);
                                locationClient.setLocOption(locationClientOption);
                                locationClient.registerNotifyLocationListener(new BDLocationListener() {
                                    @Override
                                    public void onReceiveLocation(BDLocation bdLocation) {
                                        locationClient.unRegisterLocationListener(this);
                                        locationClient.stop();
                                        observableEmitter.onNext(bdLocation);
                                        observableEmitter.onComplete();
                                    }
                                });
                                locationClient.start();
                            }
                        });
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
                .subscribe(observer);
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
        if (!mOnLocationListeners.contains(listener)) {
            mOnLocationListeners.add(listener);
        }
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
     * @param observer
     */
    @Override
    public void geoCode(String city, String addr, final Observer<PointBean> observer) {
        Observable.just(new String[] {city, addr})
                .map(new Function<String[], GeoCodeOption>() {
                    @Override
                    public GeoCodeOption apply(@NonNull String[] strings) throws Exception {
                        return new GeoCodeOption()
                                .city(strings[0])
                                .address(strings[1]);
                    }
                })
                .switchMap(new Function<GeoCodeOption, ObservableSource<GeoCodeResult>>() {
                    @Override
                    public ObservableSource<GeoCodeResult> apply(@NonNull final GeoCodeOption geoCodeOption) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<GeoCodeResult>() {
                            @Override
                            public void subscribe(@NonNull final ObservableEmitter<GeoCodeResult> observableEmitter) throws Exception {
                                final GeoCoder geoCoder = GeoCoder.newInstance();
                                geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
                                    @Override
                                    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                                        geoCoder.destroy();
                                        observableEmitter.onNext(geoCodeResult);
                                        observableEmitter.onComplete();
                                    }

                                    @Override
                                    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

                                    }
                                });
                                geoCoder.geocode(geoCodeOption);
                            }
                        });
                    }
                })
                .map(new Function<GeoCodeResult, PointBean>() {
                    @Override
                    public PointBean apply(@NonNull GeoCodeResult geoCodeResult) throws Exception {
                        if (geoCodeResult != null) {
                            if (geoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                                throw new NullPointerException("检索结果为空：" + geoCodeResult.error.name());
                            }
                        } else {
                            throw new NullPointerException("检索结果为空,请检查网络连接.");
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
                .subscribe(observer);
    }

    /**
     * 逆向地理位置编码
     *
     * @param point
     * @param observer
     */
    @Override
    public void reverseGeoCode(PointBean point, final Observer<PointBean> observer) {
        Observable.just(point)
                .map(new Function<PointBean, ReverseGeoCodeOption>() {
                    @Override
                    public ReverseGeoCodeOption apply(@NonNull PointBean pointBean) throws Exception {
                        return new ReverseGeoCodeOption().location(new LatLng(pointBean.getLat(), pointBean.getLon()));
                    }
                })
                .switchMap(new Function<ReverseGeoCodeOption, ObservableSource<ReverseGeoCodeResult>>() {
                    @Override
                    public ObservableSource<ReverseGeoCodeResult> apply(@NonNull final ReverseGeoCodeOption reverseGeoCodeOption) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<ReverseGeoCodeResult>() {
                            @Override
                            public void subscribe(@NonNull final ObservableEmitter<ReverseGeoCodeResult> observableEmitter) throws Exception {
                                final GeoCoder geoCoder = GeoCoder.newInstance();
                                geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
                                    @Override
                                    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

                                    }

                                    @Override
                                    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                                        geoCoder.destroy();
                                        observableEmitter.onNext(reverseGeoCodeResult);
                                        observableEmitter.onComplete();
                                    }
                                });
                                geoCoder.reverseGeoCode(reverseGeoCodeOption);
                            }
                        });
                    }
                })
                .map(new Function<ReverseGeoCodeResult, PointBean>() {
                    @Override
                    public PointBean apply(ReverseGeoCodeResult result) {
                        if (result != null) {
                            if (result.error != SearchResult.ERRORNO.NO_ERROR) {
                                throw new NullPointerException("检索结果为空：" + result.error.name());
                            }
                        } else {
                            throw new NullPointerException("检索结果为空,请检查网络连接.");
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
                .subscribe(observer);
    }

    /**
     * 城市内检索兴趣点
     *
     * @param city
     * @param keyword
     * @param type
     * @param page
     * @param num
     * @param observer
     */
    @Override
    public void queryCityPois(final String city, final String keyword, final String type, final int page, final int num, final Observer<List<PointBean>> observer) {
        Observable.just(new PoiCitySearchOption())
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
                .switchMap(new Function<PoiCitySearchOption, ObservableSource<PoiResult>>() {
                    @Override
                    public ObservableSource<PoiResult> apply(@NonNull final PoiCitySearchOption poiCitySearchOption) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<PoiResult>() {
                            @Override
                            public void subscribe(@NonNull final ObservableEmitter<PoiResult> observableEmitter) throws Exception {
                                final PoiSearch poiSearch = PoiSearch.newInstance();
                                poiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
                                    @Override
                                    public void onGetPoiResult(PoiResult poiResult) {
                                        poiSearch.destroy();
                                        observableEmitter.onNext(poiResult);
                                        observableEmitter.onComplete();
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
                        });
                    }
                })
                .map(new Function<PoiResult, List<PointBean>>() {
                    @Override
                    public List<PointBean> apply(@NonNull PoiResult poiResult) throws Exception {
                        if (poiResult != null) {
                            if (poiResult.error != SearchResult.ERRORNO.NO_ERROR) {
                                throw new NullPointerException("检索结果为空：" + poiResult.error.name());
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
                .subscribe(observer);
    }

    /**
     * 周边检索兴趣点
     *
     * @param center
     * @param keyword
     * @param type
     * @param page
     * @param num
     * @param observer
     */
    @Override
    public void queryNearbyPois(PointBean center, final String keyword, final String type, final int page, final int num, final Observer<List<PointBean>> observer) {
        Observable.just(center)
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
                .switchMap(new Function<PoiNearbySearchOption, ObservableSource<PoiResult>>() {
                    @Override
                    public ObservableSource<PoiResult> apply(@NonNull final PoiNearbySearchOption poiNearbySearchOption) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<PoiResult>() {
                            @Override
                            public void subscribe(@NonNull final ObservableEmitter<PoiResult> observableEmitter) throws Exception {
                                final PoiSearch poiSearch = PoiSearch.newInstance();
                                poiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
                                    @Override
                                    public void onGetPoiResult(PoiResult poiResult) {
                                        poiSearch.destroy();
                                        observableEmitter.onNext(poiResult);
                                        observableEmitter.onComplete();
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
                        });
                    }
                })
                .map(new Function<PoiResult, List<PointBean>>() {
                    @Override
                    public List<PointBean> apply(@NonNull PoiResult poiResult) throws Exception {
                        if (poiResult != null) {
                            if (poiResult.error != SearchResult.ERRORNO.NO_ERROR) {
                                throw new NullPointerException("检索结果为空：" + poiResult.error.name());
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
                .subscribe(observer);
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
     * @param observer
     */
    @Override
    public void queryRegionPois(PointBean point_1, PointBean point_2, final String keyword, final String type, final int page, final int num, Observer<List<PointBean>> observer) {
        Observable.just(new PointBean[] {point_1, point_2})
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
                .switchMap(new Function<PoiBoundSearchOption, ObservableSource<PoiResult>>() {
                    @Override
                    public ObservableSource<PoiResult> apply(@NonNull final PoiBoundSearchOption poiBoundSearchOption) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<PoiResult>() {
                            @Override
                            public void subscribe(@NonNull final ObservableEmitter<PoiResult> observableEmitter) throws Exception {
                                final PoiSearch poiSearch = PoiSearch.newInstance();
                                poiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
                                    @Override
                                    public void onGetPoiResult(PoiResult poiResult) {
                                        poiSearch.destroy();
                                        observableEmitter.onNext(poiResult);
                                        observableEmitter.onComplete();
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
                        });
                    }
                })
                .map(new Function<PoiResult, List<PointBean>>() {
                    @Override
                    public List<PointBean> apply(@NonNull PoiResult poiResult) throws Exception {
                        if (poiResult != null) {
                            if (poiResult.error != SearchResult.ERRORNO.NO_ERROR) {
                                throw new NullPointerException("检索结果为空：" + poiResult.error.name());
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
                .subscribe(observer);
    }

    /**
     * 驾车路径规划
     *
     * @param begin
     * @param end
     * @param middle
     * @param policy
     * @param observer
     */
    @Override
    public void queryDriverRoute(final PointBean begin, final PointBean end, final List<PointBean> middle, int policy, final Observer<List<RouteBean>> observer) {
        Observable.just(policy)
                .map(new Function<Integer, DrivingRoutePlanOption.DrivingPolicy>() {
                    @Override
                    public DrivingRoutePlanOption.DrivingPolicy apply(@NonNull Integer integer) throws Exception {
                        DrivingRoutePlanOption.DrivingPolicy drivingPolicy;
                        switch (integer) {
                            case 0:
                                drivingPolicy = DrivingRoutePlanOption.DrivingPolicy.ECAR_TIME_FIRST;
                                break;
                            case 1:
                                drivingPolicy = DrivingRoutePlanOption.DrivingPolicy.ECAR_DIS_FIRST;
                                break;
                            case 2:
                                drivingPolicy = DrivingRoutePlanOption.DrivingPolicy.ECAR_FEE_FIRST;
                                break;
                            case 3:
                                drivingPolicy = DrivingRoutePlanOption.DrivingPolicy.ECAR_AVOID_JAM;
                                break;
                            default:
                                throw new IllegalStateException("policy ");
                        }
                        return drivingPolicy;
                    }
                })
                .map(new Function<DrivingRoutePlanOption.DrivingPolicy, DrivingRoutePlanOption>() {
                    @Override
                    public DrivingRoutePlanOption apply(@NonNull DrivingRoutePlanOption.DrivingPolicy drivingPolicy) throws Exception {
                        List<PlanNode> planNodes = new ArrayList<PlanNode>();
                        if (middle != null) {
                            for (PointBean point : middle) {
                                planNodes.add(PlanNode.withLocation(new LatLng(point.getLat(), point.getLon())));
                            }
                        }
                        return new DrivingRoutePlanOption()
                                .from(PlanNode.withLocation(new LatLng(begin.getLat(), begin.getLon())))
                                .to(PlanNode.withLocation(new LatLng(end.getLat(), end.getLon())))
                                .passBy(planNodes.isEmpty() ? null : planNodes)
                                .policy(drivingPolicy)
                                .trafficPolicy(DrivingRoutePlanOption.DrivingTrafficPolicy.ROUTE_PATH);
                    }
                })
                .switchMap(new Function<DrivingRoutePlanOption, ObservableSource<DrivingRouteResult>>() {
                    @Override
                    public ObservableSource<DrivingRouteResult> apply(@NonNull final DrivingRoutePlanOption drivingRoutePlanOption) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<DrivingRouteResult>() {
                            @Override
                            public void subscribe(@NonNull final ObservableEmitter<DrivingRouteResult> observableEmitter) throws Exception {
                                final RoutePlanSearch routePlanSearch = RoutePlanSearch.newInstance();
                                routePlanSearch.setOnGetRoutePlanResultListener(new OnGetRoutePlanResultListener() {
                                    @Override
                                    public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

                                    }

                                    @Override
                                    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

                                    }

                                    @Override
                                    public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

                                    }

                                    @Override
                                    public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
                                        routePlanSearch.destroy();
                                        observableEmitter.onNext(drivingRouteResult);
                                        observableEmitter.onComplete();
                                    }

                                    @Override
                                    public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

                                    }

                                    @Override
                                    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

                                    }
                                });
                                routePlanSearch.drivingSearch(drivingRoutePlanOption);
                            }
                        });
                    }
                })
                .map(new Function<DrivingRouteResult, List<RouteBean>>() {
                    @Override
                    public List<RouteBean> apply(@NonNull DrivingRouteResult drivingRouteResult) throws Exception {
                        if (drivingRouteResult != null) {
                            if (drivingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
                                throw new NullPointerException("查询路径信息失败：" + drivingRouteResult.error.name());
                            }
                        } else {
                            throw new NullPointerException("获取路径信息失败.");
                        }

                        List<RouteBean> routes = new ArrayList<RouteBean>();

                        for (DrivingRouteLine line : drivingRouteResult.getRouteLines()) {
                            List<StepBean> steps = new ArrayList<>();
                            for (DrivingRouteLine.DrivingStep step : line.getAllStep()) {
                                StepBean bean = new StepBean()
                                        .setTime((long) step.getDuration())
                                        .setMoney(0.0)
                                        .setDistance(step.getDistance());
                                for (LatLng point : step.getWayPoints()) {
                                    bean.getPoint().add(new PointBean()
                                            .setLat(point.latitude)
                                            .setLon(point.longitude));
                                }
                                steps.add(bean);
                            }
                            routes.add(new RouteBean()
                                    .setStep(steps)
                                    .setTime(line.getDuration())
                                    .setMoney(0.0)
                                    .setDistance(line.getDistance()));
                        }

                        return routes;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    /**
     * 公交路线规划
     *
     * @param begin
     * @param end
     * @param middle
     * @param policy
     * @param observer
     */
//    @Override
    public void queryBusRoute(PointBean begin, PointBean end, List<PointBean> middle, int policy, Observer<List<RouteBean>> observer) {
        Observable.just(policy)
                .map(new Function<Integer, TransitRoutePlanOption>() {
                    @Override
                    public TransitRoutePlanOption apply(@NonNull Integer integer) throws Exception {
                        return null;
                    }
                })
                .switchMap(new Function<TransitRoutePlanOption, ObservableSource<TransitRouteResult>>() {
                    @Override
                    public ObservableSource<TransitRouteResult> apply(@NonNull TransitRoutePlanOption transitRoutePlanOption) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<TransitRouteResult>() {
                            @Override
                            public void subscribe(@NonNull ObservableEmitter<TransitRouteResult> observableEmitter) throws Exception {

                            }
                        });
                    }
                })
                .map(new Function<TransitRouteResult, List<RouteBean>>() {
                    @Override
                    public List<RouteBean> apply(@NonNull TransitRouteResult transitRouteResult) throws Exception {
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    /**
     * 步行路线规划
     *
     * @param begin
     * @param end
     * @param middle
     * @param policy
     * @param observer
     */
//    @Override
    public void queryWalkRoute(PointBean begin, PointBean end, List<PointBean> middle, int policy, Observer<List<RouteBean>> observer) {
        Observable.just(policy)
                .map(new Function<Integer, WalkingRoutePlanOption>() {
                    @Override
                    public WalkingRoutePlanOption apply(@NonNull Integer integer) throws Exception {
                        return null;
                    }
                })
                .switchMap(new Function<WalkingRoutePlanOption, ObservableSource<WalkingRouteResult>>() {
                    @Override
                    public ObservableSource<WalkingRouteResult> apply(@NonNull WalkingRoutePlanOption walkingRoutePlanOption) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<WalkingRouteResult>() {
                            @Override
                            public void subscribe(@NonNull ObservableEmitter<WalkingRouteResult> observableEmitter) throws Exception {

                            }
                        });
                    }
                })
                .map(new Function<WalkingRouteResult, List<RouteBean>>() {
                    @Override
                    public List<RouteBean> apply(@NonNull WalkingRouteResult walkingRouteResult) throws Exception {
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    /**
     * 骑行路径规划
     *
     * @param begin
     * @param end
     * @param middle
     * @param policy
     * @param observer
     */
//    @Override
    public void queryBikeRoute(PointBean begin, PointBean end, List<PointBean> middle, int policy, Observer<List<RouteBean>> observer) {
        Observable.just(policy)
                .map(new Function<Integer, BikingRoutePlanOption>() {
                    @Override
                    public BikingRoutePlanOption apply(@NonNull Integer integer) throws Exception {
                        return null;
                    }
                })
                .switchMap(new Function<BikingRoutePlanOption, ObservableSource<BikingRouteResult>>() {
                    @Override
                    public ObservableSource<BikingRouteResult> apply(@NonNull BikingRoutePlanOption bikingRoutePlanOption) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<BikingRouteResult>() {
                            @Override
                            public void subscribe(@NonNull ObservableEmitter<BikingRouteResult> observableEmitter) throws Exception {

                            }
                        });
                    }
                })
                .map(new Function<BikingRouteResult, List<RouteBean>>() {
                    @Override
                    public List<RouteBean> apply(@NonNull BikingRouteResult bikingRouteResult) throws Exception {
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    /**
     * 行政区域范围查询
     *
     * @param city
     * @param name
     * @param observer
     */
    @Override
    public void queryAdministrativeRegion(String city, String name, final Observer<List<List<PointBean>>> observer) {
        Observable.just(new String[] {city, name})
                .map(new Function<String[], DistrictSearchOption>() {
                    @Override
                    public DistrictSearchOption apply(@NonNull String[] strings) throws Exception {
                        return new DistrictSearchOption()
                                .cityName(strings[0])
                                .districtName(strings[1]);
                    }
                })
                .switchMap(new Function<DistrictSearchOption, ObservableSource<DistrictResult>>() {
                    @Override
                    public ObservableSource<DistrictResult> apply(@NonNull final DistrictSearchOption districtSearchOption) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<DistrictResult>() {
                            @Override
                            public void subscribe(@NonNull final ObservableEmitter<DistrictResult> observableEmitter) throws Exception {

                                final DistrictSearch districtSearch = DistrictSearch.newInstance();
                                districtSearch.setOnDistrictSearchListener(new OnGetDistricSearchResultListener() {
                                    @Override
                                    public void onGetDistrictResult(DistrictResult districtResult) {
                                        districtSearch.destroy();
                                        observableEmitter.onNext(districtResult);
                                        observableEmitter.onComplete();
                                    }
                                });
                                districtSearch.searchDistrict(districtSearchOption);
                            }
                        });
                    }
                })
                .map(new Function<DistrictResult, List<List<PointBean>>>() {
                    @Override
                    public List<List<PointBean>> apply(@NonNull DistrictResult districtResult) throws Exception {
                        if (districtResult != null) {
                            if (districtResult.error != SearchResult.ERRORNO.NO_ERROR) {
                                throw new NullPointerException("获取边界信息失败：" + districtResult.error.name());
                            }
                        } else {
                            throw new NullPointerException("获取边界信息失败.");
                        }

                        List<List<PointBean>> areas = new ArrayList<List<PointBean>>();

                        List<List<LatLng>> polyLines = districtResult.getPolylines();

                        for (List<LatLng> area : polyLines) {
                            List<PointBean> points = new ArrayList<PointBean>();

                            for (LatLng ll : area) {
                                points.add(new PointBean().setLat(ll.latitude).setLon(ll.longitude));
                            }

                            areas.add(points);
                        }

                        return areas;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
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
                .setCity(location.getCity())
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