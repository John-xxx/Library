package com.liux.example;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.bilibili.boxing.BoxingCrop;
import com.bilibili.boxing.BoxingMediaLoader;
import com.liux.lbs.model.impl.AMapLBSModelImpl;
import com.liux.lbs.model.impl.BaiduLBSModelImpl;
import com.liux.boxing.BoxingGlideLoader;
import com.liux.boxing.BoxingUcrop;

/**
 * Created by Liux on 2017/8/16.
 */

public class ApplocationCus extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        /* 初始化百度SDK */
        SDKInitializer.initialize(this);
        SDKInitializer.setCoordType(CoordType.GCJ02);

        /* 初始化LBSModel */
        AMapLBSModelImpl.initialize(this);
        BaiduLBSModelImpl.initialize(this);

        /* 初始化Boxing */
        BoxingCrop.getInstance().init(new BoxingUcrop());
        BoxingMediaLoader.getInstance().init(new BoxingGlideLoader());
    }
}
