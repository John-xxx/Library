Pay
===

使用说明
---
```
compile 'com.liux:pay:x.y.z'
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
####x.y.z_201x-xx-xx
    1.