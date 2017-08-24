package com.liux.framework_demo;

import android.app.Application;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.bilibili.boxing.BoxingCrop;
import com.bilibili.boxing.BoxingMediaLoader;
import com.bilibili.boxing.loader.IBoxingMediaLoader;
import com.liux.framework.lbs.model.impl.AMapLBSModelImpl;
import com.liux.framework.lbs.model.impl.BaiduLBSModelImpl;
import com.liux.framework.tool.BoxingGlideLoader;
import com.liux.framework.tool.BoxingUcrop;

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

        /* 初始化Boxing */
        BoxingCrop.getInstance().init(new BoxingUcrop());
        BoxingMediaLoader.getInstance().init(new BoxingGlideLoader());
    }
}
