LBS
===

为了不增加发布库类方法数量和包尺寸,以下库需要自行单独引用
(可以从项目中直接拷贝)
```
/* BaiduLBS */
BaiduLBS_Android_4.4.1
http://lbsyun.baidu.com/index.php?title=androidsdk/sdkandev-download

/* AMapLBS */
AMap3DMap_5.3.0_AMapSearch_5.3.1_AMapLocation_3.5.0_20170817
http://lbs.amap.com/api/android-sdk/download
```

使用说明
---
```
implementation 'com.liux:lbs:x.y.z'
```

混淆参考
---
```
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
# BaiduLBS
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
```

更新说明
---
### x.y.z_201x-xx-xx
    1.