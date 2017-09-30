Framework
===

项目说明
---
库的用途在于逻辑层面的封装,在不侵入或少量侵入的前提下
封装了一些和业务模式无强关联的库类
并引用了一些时下较流行的三方开源库

- Banner<br>
实现一个适配器模式的伪无限滚动的Banner
- BaseActivity/BaseFragment/BaseDialog<br>
BaseActivity重定义了生命周期细节,增加输入法与触控事件的逻辑,实现可自定义的沉浸式状态栏
BaseFragment实现了Fragment懒加载模型,处理某些情况下Fragment状态异常的问题
BaseDialog扩展沉浸式的Dialog
- Boxing<br>
对bilibili的Boxing媒体选择器组件封装
- Glide<br>
Glide转换器,自定义Glide4配置和实现视屏缩略图的自定义加载过程
- Http<br>
封装OkHttp3和Retrofit2的全局单例Http客户端,并添加了持久化Cookie相关能力
- LBS<br>
基于百度地图和高德地图的业务层Model封装
- List<br>
基于RecyclerView的Adapter,ItemDecoration和ViewHolder的扩展封装
封装选择控制,数据源类型加载,Header/Footer能力的Adapter
封装类似ListView中分割线控制的ColorDecoration
封装灵活的Holder,内部缓存控件提升性能
- Pay<br>
基于支付宝/微信支付的支付逻辑封装
- Permission<br>
封装链式调用的权限申请工具类
- Player<br>
基于[ijkplayer](https://github.com/Bilibili/ijkplayer)的一个Android播放器库
- Util/Other<br>
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

// 如果使用到Player相关库类还需要引用
compile 'com.liux:framework-ijkplayer:x.x.x'
```

混淆参考
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
# 3D 地图
-keep class com.amap.api.maps.**{*;}
-keep class com.autonavi.**{*;}
-keep class com.amap.api.trace.**{*;}
# 定位
-keep class com.amap.api.location.**{*;}
-keep class com.amap.api.fence.**{*;}
-keep class com.autonavi.aps.amapapi.model.**{*;}
# 搜索
-keep class com.amap.api.services.**{*;}
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

发布版本 [[更新记录]](/UPGRADE.md)
---
- framework 0.2.8
- framework-ijkplayer 0.8.3

更新时间
---
2017年9月20日

License
---
[The MIT License Copyright (c) 2017 lx0758](/LICENSE.txt)