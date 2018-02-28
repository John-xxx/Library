Http
===

使用说明
---
```
implementation 'com.liux:http:x.y.z'
```

混淆参考
---
```
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
# FastJSON
-dontwarn com.alibaba.fastjson.**
-keep class com.alibaba.fastjson.** {*;}
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,LocalVariable*Table,*Annotation*,Synthetic,EnclosingMethod
```

更新说明
---
### x.y.z_201x-xx-xx
    1.更新代码结构

### 0.3.0_2018-02-27
    1.完成链式调用的HttpClient
    2.增加上传/下载进度反馈能力

### 0.2.3_2018-02-13
    1.调整代码,美化日志拦截器

### 0.2.2_2018-01-16
    1.实现Retorfit动态基础地址能力

### 0.2.1_2017-12-21
    1.分离UserAgent拦截器
    2.增加日志输出拦截器

### 0.2.0_2017-12-14
    1.升级SDK到27
    2.支持库到27+

### 0.1.10_2017-11-30
    1.优化请求头/参数拦截器

### 0.1.9_2017-11-29
    1.分离拦截器回调

### 0.1.8_2017-11-29
    1.分离代码

### 0.1.7_2017-11-27
    1.调整代码,引用Android8.0的MIME码表

### 0.1.6_2017-11-22
    1.升级依赖版本

### 0.1.5_2017-11-22
    1.修复依赖的FastJson转换器会转换字符串的问题

### 0.1.2_2017-11-22
    1.修复复合表单默认不是FORM表单的问题

### 0.1.1_2017-11-14
    1.调整依赖

### 0.1.0_2017-11-12
    1.完成从Framework分包