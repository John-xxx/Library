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

注意事项
---
    1.某些情况下使用全局 Base 无法正确匹配路径以"/"开头的根路径URL
      初始化设置 http://api.baidu.com/api/
      全局设置 http://app.google.com/v1.0/
      请求设置 @GET("/api/xxx")
      正常应该为 http://app.google.com/api/xxx
      最终请求地址变成 http://app.google.com/v1.0/xxx
      
      原因是初始化请求以"/"开头,经过Retorfit解析后变为
      http://api.baidu.com/api/xxx
      在经过全局 Base 替换 http://api.baidu.com/api/ 为 http://app.google.com/v1.0/
      最终变成 http://app.google.com/v1.0/xxx
      
      即当初始化设置URL根Path和请求根Path发生重复时出现
    
    2.使用 MultipartBody 上传 InputStream 时,应避免多个线程共享 InputStream
      因为上传过程中 InputStream 会被 读取/重置 多次,在多线程环境下会造成数据紊乱
      
      故,在使用时应达到每个线程单独使用一个 InputStream 实例的条件
      
      另: InputStream 输入源要保证 InputStream.available() 能返回流整体长度,
      如若不能,则应返回 -1
    
    3.在使用 MultipartBody 传输 byte[] 或 InoutStream 时,因为框架发送的是原始二进制数据,
      所以服务端接收也必须接收原始数据.否则有可能造成数据被强行"字符"转换,导致获得的数据损坏
      并且,传输 InoutStream 到服务器也是按照 byte[] 方式接收
      
      /**
       * 错误用例(Java):
       * @param modelMap
       * @param id
       * @param name
       * @param data
       * @param stream
       * @param file
       * @return
       */
      @PostMapping(value = "/api")
      public String upload(ModelMap modelMap, int[] id, String[] name, byte[] data, byte[] stream, MultipartFile file) {
          System.out.println("data:" + Arrays.toString(data));
          System.out.println("stream:" + Arrays.toString(stream));
          return "Done!";
      }
      
      /**
       * 正确用例(Java):
       * @param modelMap
       * @param request
       * @param id
       * @param name
       * @param file
       * @return
       */
      @PostMapping(value = "/api")
      public String upload(ModelMap modelMap, HttpServletRequest request, int[] id, String[] name, MultipartFile file) {
          try {
              byte[] data = readInputStream(request.getPart("data").getInputStream());
              byte[] stream = readInputStream(request.getPart("stream").getInputStream());
              System.out.println("data:" + Arrays.toString(data));
              System.out.println("stream:" + Arrays.toString(stream));
          } catch (Exception e) {
              
          }
          return "Done!";
      }
      
      详情参见:
      https://github.com/lx0758/Java_Demo/tree/master/Server
      
    3.参数拦截器处理 MultipartBody 时,会将 text 类型的文本参数逆解析
      此动作可能会导致 Part 某些自定义的 Header 丢失,详情请看源码

更新说明
---
### x.y.z_201x-xx-xx
    1.
    
### 0.4.0_2018-04-10
    1.请求支持 byte[] 和 InputStream
    2.更换主类 HttpClient 名称为 Http
    3.增强参数拦截监听器能力,修复Header丢失问题
    4.修复设置进度监听器后参数监听器失效问题
    5.优化 FastJSON 转换器配置
    
### 0.3.6_2018-04-04
    1.修正响应回调完成状态错误的问题
    
### 0.3.5_2018-04-04
    1.修复动态BaseUrl造成queryParam重复的问题
    2.修复上传/下载回调多次并且重复调用的问题
    3.新增实现对每个请求/全局设定连接/写/读超时时间支持
    4.新增手动创建Request增加Fragment的支持
    5.新增鉴别手动创建的Request的支持
    6.升级Retorfit至2.4.0

### 0.3.1_2018-03-28
    1.更新代码结构
    2.调整Logger打印输出

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