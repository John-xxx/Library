Glide
===

使用说明
---
```
implementation 'com.liux:glide:x.y.z'
```

混淆参考
---
```
# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** { **[] $VALUES; public *; }
```

更新说明
---
### x.y.z_201x-xx-xx
    1.