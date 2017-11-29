Library
===

项目说明
---
库的用途在于逻辑层面的封装,在不侵入或少量侵入的前提下<br>
封装了一些和业务模式无强关联的库类<br>
并引用了一些时下较流行的三方开源库

[maven](http://maven.lx0758.cc)
---
```
repositories {
    ...
    maven {
        url 'http://maven.lx0758.cc/artifactory/public/'
    }
    ...
}
```

[banner](/banner/README.md)
---
实现一个适配器模式的伪无限滚动的Banner

[base](/base/README.md)
---
BaseActivity重定义了生命周期细节,增加输入法与触控事件的逻辑,实现可自定义的沉浸式状态栏
BaseFragment实现了Fragment懒加载模型,处理某些情况下Fragment状态异常的问题
BaseDialog扩展沉浸式的Dialog

[boxing](/boxing/README.md)
---
对bilibili的Boxing媒体选择器组件封装

[glide](/glide/README.md)
---
Glide转换器,自定义Glide4配置和实现视屏缩略图的自定义加载过程

[http](/http/README.md)
---
封装OkHttp3和Retrofit2的全局单例Http客户端,并添加了持久化Cookie相关能力

[lbs](/lbs/README.md)
---
基于百度地图和高德地图的业务层Model封装

[list](/list/README.md)
---
基于RecyclerView的Adapter,ItemDecoration和ViewHolder的扩展封装
封装选择控制,数据源类型加载,Header/Footer能力的Adapter
封装类似ListView中分割线控制的ColorDecoration
封装灵活的Holder,内部缓存控件提升性能

[pay](/pay/README.md)/[pay-unionpay](/pay-unionpay/README.md)
---
基于支付宝/微信支付/银联支付的支付逻辑封装

[permission](/permission/README.md)
---
封装链式调用的权限申请工具类

[player](/player/README.md)
---
基于[ijkplayer](https://github.com/Bilibili/ijkplayer)的一个Android播放器库

[other](/other/README.md)/[util](/util/README.md)/[view](/view/README.md)
---
其他一些库类/组件/控件的集合

License
---
[The MIT License Copyright (c) 2017 Liux](/LICENSE.txt)