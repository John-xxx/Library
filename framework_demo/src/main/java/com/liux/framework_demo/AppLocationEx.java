package com.liux.framework_demo;

import android.app.Application;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.liux.framework.lbs.model.impl.AMapLBSModelImpl;
import com.liux.framework.lbs.model.impl.BaiduLBSModelImpl;

/**
 * Created by Liux on 2017/8/16.
 */

public class AppLocationEx extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        /* 初始化百度SDK */
        SDKInitializer.initialize(this);
        SDKInitializer.setCoordType(CoordType.GCJ02);

        /* 初始化LBSModel */
        AMapLBSModelImpl.initialize(this);
        BaiduLBSModelImpl.initialize(this);
    }
}
