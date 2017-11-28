package com.liux.example.lbs;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.liux.example.R;
import com.liux.lbs.bean.PointBean;
import com.liux.lbs.bean.RouteBean;
import com.liux.lbs.listener.OnLocationListener;
import com.liux.lbs.model.LBSModel;
import com.liux.lbs.model.impl.AMapLBSModelImpl;
import com.liux.lbs.model.impl.BaiduLBSModelImpl;
import com.liux.permission.OnPermissionListener;
import com.liux.permission.PermissionTool;
import com.liux.view.SingleToast;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.observers.DisposableObserver;

/**
 * Created by Liux on 2017/11/28.
 */

public class LBSActivity extends AppCompatActivity {

    private static final String[] permissions = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private LBSModel mAMapLBSModel = AMapLBSModelImpl.getInstance();
    private LBSModel mBaiduLBSModel = BaiduLBSModelImpl.getInstance();

    private OnLocationListener mAMapOnLocationListener = new OnLocationListener() {
        @Override
        public void onSucceed(PointBean position) {
            makeText(String.format("高德持续定位成功:%s_%s", position.getTitle(), position.getAddress()));
        }

        @Override
        public void onFailure(String msg) {
            makeText("高德持续定位失败 " + msg);
        }
    };

    private OnLocationListener mBaiduOnLocationListener = new OnLocationListener() {
        @Override
        public void onSucceed(PointBean position) {
            makeText(String.format("百度持续定位成功:%s_%s", position.getTitle(), position.getAddress()));
        }

        @Override
        public void onFailure(String msg) {
            makeText("百度持续定位失败 " + msg);
        }
    };
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lbs);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_quick_location_amap, R.id.btn_accuracy_location_amap, R.id.btn_start_location_amap, R.id.btn_stop_location_amap, R.id.btn_geocode_amap, R.id.btn_regeocode_amap, R.id.btn_city_poi_amap, R.id.btn_nearby_poi_amap, R.id.btn_region_poi_amap, R.id.btn_driver_route_amap, R.id.btn_boundary_amap, R.id.btn_quick_location_baidu, R.id.btn_accuracy_location_baidu, R.id.btn_start_location_baidu, R.id.btn_stop_location_baidu, R.id.btn_geocode_baidu, R.id.btn_regeocode_baidu, R.id.btn_city_poi_baidu, R.id.btn_nearby_poi_baidu, R.id.btn_region_poi_baidu, R.id.btn_driver_route_baidu, R.id.btn_boundary_baidu})
    public void onViewClicked(final View view) {
        PermissionTool.with(this)
                .permissions(permissions)
                .listener(new OnPermissionListener() {
                    @Override
                    public void onPermission(List<String> allow, List<String> reject, List<String> prohibit) {
                        if (!reject.isEmpty() || !prohibit.isEmpty()) {
                            makeText("请求定位权限没有成功,无法进行操作.");
                            return;
                        }
                        onCallLBS(view);
                    }
                })
                .request();
    }

    private void onCallLBS(View view) {
        switch (view.getId()) {
            case R.id.btn_quick_location_amap:
                mAMapLBSModel.quickLocation(new DisposableObserver<PointBean>() {
                    @Override
                    public void onNext(PointBean pointBean) {
                        makeText(String.format("高德网络定位成功:%s_%s", pointBean.getTitle(), pointBean.getAddress()));
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        makeText(String.format("高德网络定位失败:%s", throwable.getMessage()));
                    }

                    @Override
                    public void onComplete() {

                    }
                });
                break;
            case R.id.btn_accuracy_location_amap:
                mAMapLBSModel.accuracyLocation(new DisposableObserver<PointBean>() {
                    @Override
                    public void onNext(PointBean pointBean) {
                        makeText(String.format("高德高精度定位成功:%s_%s", pointBean.getTitle(), pointBean.getAddress()));
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        makeText(String.format("高德高精度定位失败:%s", throwable.getMessage()));
                    }

                    @Override
                    public void onComplete() {

                    }
                });
                break;
            case R.id.btn_start_location_amap:
                mAMapLBSModel.startLocation(mAMapOnLocationListener);
                break;
            case R.id.btn_stop_location_amap:
                mAMapLBSModel.stopLocation(mAMapOnLocationListener);
                break;
            case R.id.btn_geocode_amap:
                mAMapLBSModel.geoCode("成都", "倪家桥站", new DisposableObserver<PointBean>() {
                    @Override
                    public void onNext(PointBean pointBean) {
                        makeText(String.format("高德地理位置编码成功:%s", pointBean.getAddress()));
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        makeText(String.format("高德地理位置编码失败:%s", throwable.getMessage()));
                    }

                    @Override
                    public void onComplete() {

                    }
                });
                break;
            case R.id.btn_regeocode_amap:
                mAMapLBSModel.reverseGeoCode(new PointBean().setLat(30.542385).setLon(104.067652), new DisposableObserver<PointBean>() {
                    @Override
                    public void onNext(PointBean pointBean) {
                        makeText(String.format("高德逆向地理位置编码成功:%s_%s", pointBean.getTitle(), pointBean.getAddress()));
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        makeText(String.format("高德逆向地理位置编码失败:%s", throwable.getMessage()));
                    }

                    @Override
                    public void onComplete() {

                    }
                });
                break;
            case R.id.btn_city_poi_amap:
                mAMapLBSModel.queryCityPois("成都", "倪家桥", null, 0, 10, new DisposableObserver<List<PointBean>>() {
                    @Override
                    public void onNext(List<PointBean> pointBeen) {
                        makeText(String.format("高德城市检索成功:%s", pointBeen.toString()));
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        makeText(String.format("高德城市检索失败:%s", throwable.getMessage()));
                    }

                    @Override
                    public void onComplete() {

                    }
                });
                break;
            case R.id.btn_nearby_poi_amap:
                mAMapLBSModel.queryNearbyPois(new PointBean().setLat(30.542385).setLon(104.067652), "天府三街", null, 0, 10, new DisposableObserver<List<PointBean>>() {
                    @Override
                    public void onNext(List<PointBean> pointBeen) {
                        makeText(String.format("高德周边检索成功:%s", pointBeen.toString()));
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        makeText(String.format("高德周边检索失败:%s", throwable.getMessage()));
                    }

                    @Override
                    public void onComplete() {

                    }
                });
                break;
            case R.id.btn_region_poi_amap:
                mAMapLBSModel.queryRegionPois(
                        new PointBean().setLat(30.733573).setLon(104.143524),
                        new PointBean().setLat(30.564625).setLon(103.991088),
                        "倪家桥",
                        null,
                        0,
                        10,
                        new DisposableObserver<List<PointBean>>() {
                            @Override
                            public void onNext(List<PointBean> pointBeen) {
                                makeText(String.format("高德区域检索成功:%s", pointBeen.toString()));
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                makeText(String.format("高德区域检索失败:%s", throwable.getMessage()));
                            }

                            @Override
                            public void onComplete() {

                            }
                        }
                );
                break;
            case R.id.btn_driver_route_amap:
                mAMapLBSModel.queryDriverRoute(
                        new PointBean().setLat(30.733573).setLon(104.143524),
                        new PointBean().setLat(30.564625).setLon(103.991088),
                        null,
                        0,
                        new DisposableObserver<List<RouteBean>>() {
                            @Override
                            public void onNext(List<RouteBean> routeBeen) {
                                makeText(String.format("高德驾车路径规划成功:%s", routeBeen.toString()));
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                makeText(String.format("高德驾车路径规划失败:%s", throwable.getMessage()));
                            }

                            @Override
                            public void onComplete() {

                            }
                        }
                );
                break;
            case R.id.btn_boundary_amap:
                mAMapLBSModel.queryAdministrativeRegion("成都", "金堂", new DisposableObserver<List<List<PointBean>>>() {
                    @Override
                    public void onNext(List<List<PointBean>> lists) {
                        makeText(String.format("高德查询行政区边界成功:%s", lists.toString()));
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        makeText(String.format("高德查询行政区边界失败:%s", throwable.getMessage()));
                    }

                    @Override
                    public void onComplete() {

                    }
                });
                break;
            case R.id.btn_quick_location_baidu:
                mBaiduLBSModel.quickLocation(new DisposableObserver<PointBean>() {
                    @Override
                    public void onNext(PointBean pointBean) {
                        makeText(String.format("百度网络定位成功:%s_%s", pointBean.getTitle(), pointBean.getAddress()));
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        makeText(String.format("百度网络定位失败:%s", throwable.getMessage()));
                    }

                    @Override
                    public void onComplete() {

                    }
                });
                break;
            case R.id.btn_accuracy_location_baidu:
                mBaiduLBSModel.accuracyLocation(new DisposableObserver<PointBean>() {
                    @Override
                    public void onNext(PointBean pointBean) {
                        makeText(String.format("百度高精度定位成功:%s_%s", pointBean.getTitle(), pointBean.getAddress()));
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        makeText(String.format("百度高精度定位失败:%s", throwable.getMessage()));
                    }

                    @Override
                    public void onComplete() {

                    }
                });
                break;
            case R.id.btn_start_location_baidu:
                mBaiduLBSModel.startLocation(mBaiduOnLocationListener);
                break;
            case R.id.btn_stop_location_baidu:
                mBaiduLBSModel.stopLocation(mBaiduOnLocationListener);
                break;
            case R.id.btn_geocode_baidu:
                mBaiduLBSModel.geoCode("成都", "倪家桥站", new DisposableObserver<PointBean>() {
                    @Override
                    public void onNext(PointBean pointBean) {
                        makeText(String.format("百度地理位置编码成功:%s", pointBean.getAddress()));
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        makeText(String.format("百度地理位置编码失败:%s", throwable.getMessage()));
                    }

                    @Override
                    public void onComplete() {

                    }
                });
                break;
            case R.id.btn_regeocode_baidu:
                mBaiduLBSModel.reverseGeoCode(new PointBean().setLat(30.542385).setLon(104.067652), new DisposableObserver<PointBean>() {
                    @Override
                    public void onNext(PointBean pointBean) {
                        makeText(String.format("百度逆向地理位置编码成功:%s_%s", pointBean.getTitle(), pointBean.getAddress()));
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        makeText(String.format("百度逆向地理位置编码失败:%s", throwable.getMessage()));
                    }

                    @Override
                    public void onComplete() {

                    }
                });
                break;
            case R.id.btn_city_poi_baidu:
                mBaiduLBSModel.queryCityPois("成都", "倪家桥", null, 0, 10, new DisposableObserver<List<PointBean>>() {
                    @Override
                    public void onNext(List<PointBean> pointBeen) {
                        makeText(String.format("百度城市检索成功:%s", pointBeen.toString()));
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        makeText(String.format("百度城市检索失败:%s", throwable.getMessage()));
                    }

                    @Override
                    public void onComplete() {

                    }
                });
                break;
            case R.id.btn_nearby_poi_baidu:
                mBaiduLBSModel.queryNearbyPois(new PointBean().setLat(30.542385).setLon(104.067652), "天府三街", null, 0, 10, new DisposableObserver<List<PointBean>>() {
                    @Override
                    public void onNext(List<PointBean> pointBeen) {
                        makeText(String.format("百度周边检索成功:%s", pointBeen.toString()));
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        makeText(String.format("百度周边检索失败:%s", throwable.getMessage()));
                    }

                    @Override
                    public void onComplete() {

                    }
                });
                break;
            case R.id.btn_region_poi_baidu:
                mBaiduLBSModel.queryRegionPois(
                        new PointBean().setLat(30.733573).setLon(104.143524),
                        new PointBean().setLat(30.564625).setLon(103.991088),
                        "倪家桥",
                        null,
                        0,
                        10,
                        new DisposableObserver<List<PointBean>>() {
                            @Override
                            public void onNext(List<PointBean> pointBeen) {
                                makeText(String.format("百度区域检索成功:%s", pointBeen.toString()));
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                makeText(String.format("百度区域检索失败:%s", throwable.getMessage()));
                            }

                            @Override
                            public void onComplete() {

                            }
                        }
                );
                break;
            case R.id.btn_driver_route_baidu:
                mBaiduLBSModel.queryDriverRoute(
                        new PointBean().setLat(30.733573).setLon(104.143524),
                        new PointBean().setLat(30.564625).setLon(103.991088),
                        null,
                        0,
                        new DisposableObserver<List<RouteBean>>() {
                            @Override
                            public void onNext(List<RouteBean> routeBeen) {
                                makeText(String.format("百度驾车路径规划成功:%s", routeBeen.toString()));
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                makeText(String.format("百度驾车路径规划失败:%s", throwable.getMessage()));
                            }

                            @Override
                            public void onComplete() {

                            }
                        }
                );
                break;
            case R.id.btn_boundary_baidu:
                mBaiduLBSModel.queryAdministrativeRegion("成都", "金堂", new DisposableObserver<List<List<PointBean>>>() {
                    @Override
                    public void onNext(List<List<PointBean>> lists) {
                        makeText(String.format("百度查询行政区边界成功:%s", lists.toString()));
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        makeText(String.format("百度查询行政区边界失败:%s", throwable.getMessage()));
                    }

                    @Override
                    public void onComplete() {

                    }
                });
                break;
        }
    }

    private void makeText(String msg) {
        SingleToast.makeText(this, msg, SingleToast.LENGTH_SHORT).show();
    }
}
