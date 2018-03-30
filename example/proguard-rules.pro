# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\android-sdk-windows/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-ignorewarnings

# Support
-keep class android.support.** {*;}
# Saripaar
-keep class com.mobsandgeeks.saripaar.** {*;}
-keep @com.mobsandgeeks.saripaar.annotation.ValidateUsing class * {*;}
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
# BaiduLBS
-dontwarn com.baidu.**
-keep class com.baidu.** { *; }
-keep class vi.com.gdi.bgl.android.**{*;}
# AMapLBS
-dontwarn com.amap.**
-dontwarn com.autonavi.**
-keep class com.amap.**{*;}
-keep class com.autonavi.**{*;}