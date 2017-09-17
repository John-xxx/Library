Framework
===

项目说明
---
这个项目是我自己用于对以前不熟的一些基础知识的重温<br>
以及对当下流行技术的学习和封装等等.<br>
主要包含以下方面(持续更新):<br>

- BaseActivity/BaseFragment<br>
主要包含通用TitleBar支持,沉浸式状态栏支持
- 运行时权限<br>
封装运行时权限,易控制
- RecyclerView<br>
基于RecyclerView的适配器和Holder封装,实现单选/多选等逻辑控制
- LBS Model<br>
基于百度地图和高德地图的业务层Model封装
- PayTool<br>
基于支付宝/微信支付的支付逻辑封装
- 媒体<br>
基于[ijkplayer](https://github.com/Bilibili/ijkplayer)的一个Android播放器库
- 其他<br>
其他一些组件/控件的集合

引用三方库及版本
---

####开源库
- AppCompat-v7_25.3.1
- RecyclerView-v7_25.3.1
- RxJava2_2.1.3
- OkHttp3_3.9.0
- Retrofit2_2.3.0
- Glide4_4.0
- FastJson_1.1.63.android
- Boxing_0.8.0
- UCrop_2.2.0
- ijkplayer_0.8.3

####闭源库
- BaiduLBS_Android_4.4.1
- AMap3DMap_5.3.0_AMapSearch_5.3.1_AMapLocation_3.5.0_20170817
- alipaySdk-20170725
- wechat-sdk-android-without-mta-1.3.4

####注意
为了不增加发布库类方法数量和包尺寸,以下库需要自行单独引用(可以从项目中直接拷贝)

/* BaiduLBS */<br>
http://lbsyun.baidu.com/index.php?title=androidsdk/sdkandev-download

/* AMapLBS */<br>
http://lbs.amap.com/api/android-sdk/download

/* AliPay */<br>
https://docs.open.alipay.com/54/104509

/* WeiXinPay */<br>
https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&id=open1419319167&token=&lang=zh_CN

/* ijkplayer */<br>
compile 'com.liux:framework-ijkplayer:x.x.x'

使用说明
---
```
compile 'com.liux:framework:x.x.x'
compile 'com.liux:framework-ijkplayer:x.x.x'
```

混淆说明
---
```
# Framework
-dontwarn com.liux.framework.**
-keep class com.liux.framework {*;}

# Android
-dontwarn android.**
-keep class android.** {*;}

# RxJava2
-dontwarn rx.*
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

# OkHttp3
-dontwarn okhttp3.**
-keep class okhttp3.** {*;}
-dontwarn okio.**
-keep class okio.** {*;}

# Retrofit2
-dontwarn retrofit2.**
-keep class retrofit2.** {*;}
-keepattributes Signature
-keepattributes Exceptions

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** { **[] $VALUES; public *; }

# FastJSON
-dontwarn com.alibaba.fastjson.**
-keep class com.alibaba.fastjson.** {*;}
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,LocalVariable*Table,*Annotation*,Synthetic,EnclosingMethod

#BaiduLBS
-keep class com.baidu.** { *; }
-keep class vi.com.gdi.bgl.android.**{*;} 

# AMapLBS
# 3D 地图 V5.0.0之前：
-keep class com.amap.api.maps.**{*;}
-keep class com.autonavi.amap.mapcore.*{*;}
-keep class com.amap.api.trace.**{*;}
# 3D 地图 V5.0.0之后：
-keep class com.amap.api.maps.**{*;}
-keep class com.autonavi.**{*;}
-keep class com.amap.api.trace.**{*;}
# 定位
-keep class com.amap.api.location.**{*;}
-keep class com.amap.api.fence.**{*;}
-keep class com.autonavi.aps.amapapi.model.**{*;}
# 搜索
-keep class com.amap.api.services.**{*;}
# 2D地图
-keep class com.amap.api.maps2d.**{*;}
-keep class com.amap.api.mapcore2d.**{*;}
# 导航
-keep class com.amap.api.navi.**{*;}
-keep class com.autonavi.**{*;}

# Alipay
-keep class com.alipay.android.app.IAlixPay {*;}
-keep class com.alipay.android.app.IAlixPay$Stub {*;}
-keep class com.alipay.android.app.IRemoteServiceCallback {*;}
-keep class com.alipay.android.app.IRemoteServiceCallback$Stub {*;}
-keep class com.alipay.sdk.app.PayTask {public *;}
-keep class com.alipay.sdk.app.AuthTask {public *;}

# WeiXinPay
-keep class com.tencent.wxop.** {*;}
-keep class com.tencent.mm.sdk.** {*;}
-keep class com.tencent.mm.opensdk.** {*;}
-keep class com.tencent.mm.sdk.openapi.WXMediaMessage {*;}
-keep class com.tencent.mm.sdk.openapi.** implements com.tencent.mm.sdk.openapi.WXMediaMessage$IMediaObject {*;}

```

发布版本
---
- framework 0.2.5
- framework-ijkplayer 0.8.3

更新时间
---
2017年9月15日

License
---
[The MIT License Copyright (c) 2017 lx0758](/LICENSE.txt)