Pay
===

使用说明
---
```
implementation 'com.liux:pay:x.y.z'
```

混淆参考
---
```
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

更新说明
---
### 0.2.3_2018-02-27
    1.升级依赖版本

### 0.2.2_2018-02-13
    1.整合银联支付实现

### 0.2.1_2017-12-15
    1.修复SDK权限BUG

### 0.2.0_2017-12-14
    1.升级SDK到27
    2.支持库到27+

### 0.1.0_2017-11-12
    1.完成从Framework分包