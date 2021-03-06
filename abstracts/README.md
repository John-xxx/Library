Abstracts
===

使用说明
---
```
implementation 'com.liux:abstracts:x.y.z'
```

混淆参考
---
```
# Support
-keep class android.support.** {*;}
# Saripaar
-keep class com.mobsandgeeks.saripaar.** {*;}
-keep @com.mobsandgeeks.saripaar.annotation.ValidateUsing class * {*;}
```

更新说明
---
### x.y.z_201x-xx-xx
    1.更新支持库版本

### 0.2.6_2018-03-14
    1.更新支持库版本
    2.修复当设置无标题时自定义TitleBar崩溃问题

### 0.2.5_2018-03-04
    1.优化默认的TitleBar尺寸

### 0.2.4_2018-02-12
    1.集成saripaar的表单验证能力

### 0.2.3_2018-02-12
    1.使用代理模式重构项目

### 0.2.2_2018-02-11
    1.细化生命周期
    2.修复输入法内存泄漏

### 0.2.1_2018-01-16
    1.优化点击空白隐藏输入法能力

### 0.2.0_2017-12-14
    1.升级SDK到27
    2.支持库到27+

### 0.1.9_2017-12-12
    1.修复全屏/沉浸式状态栏下键盘挡住输入框问题

### 0.1.8_2017-12-05
    1.优化UI效果

### 0.1.7_2017-12-04
    1.增加点击空白隐藏输入法能力

### 0.1.6_2017-12-03
    1.AbstractsDialog增加全屏效果

### 0.1.5_2017-12-03
    1.更改base为abstracts,包名同步修改

### 0.1.3_2017-12-01
    1.修复沉浸式标题栏和RESIZE样式冲突的问题

### 0.1.2_2017-11-20
    1.调整回调事件顺序

### 0.1.1_2017-11-13
    1.添加白色标题栏控件

### 0.1.0_2017-11-12
    1.完成从Framework分包