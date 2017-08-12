package com.liux.framework.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.widget.Toast;

import java.io.File;

/**
 * IntentUtil
 * 2017/3/22
 *
 * @author Liux
 */

public class IntentUtil {

    /**
     * 启动通用的地图软件经行路线规划
     *
     * @param context
     * @param lat     终点纬度(gcj02)
     * @param lng     终点经度(gcj02)
     */
    public static void startGeneralMapNavigator(Context context, double lat, double lng) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(String.format("geo:%f,%f", lat, lng)));
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "您尚未安装地图软件或无权限调用.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 启动百度地图软件经行路线规划
     *
     * @param context
     * @param lat     终点纬度(bd09ll)
     * @param lng     终点经度(bd09ll)
     */
    public static void startBaiduMapNavigator(Context context, double lat, double lng, String name) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.baidu.BaiduMap");
            intent.setData(Uri.parse(String.format("baidumap://map/direction?destination=latlng:%f,%f|name:%s&mode=driving", lat, lng, name)));
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "您尚未安装百度地图软件或无权限调用.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 启动百度地图软件经行路线规划
     *
     * @param context
     * @param lat     终点纬度(gcj02)
     * @param lng     终点经度(gcj02)
     */
    public static void startAMapNavigator(Context context, double lat, double lng, String name) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setPackage("com.autonavi.minimap");
            intent.setData(Uri.parse(String.format("androidamap://route?sourceApplication=Back&dlat=%f&dlon=%f&dname=%s&dev=0&m=0&t=2", lat, lng, name)));
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "您尚未安装高德地图软件或无权限调用.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 启动腾讯地图软件经行路线规划
     *
     * @param context
     * @param lat     终点纬度(gcj02)
     * @param lng     终点经度(gcj02)
     */
    public static void startQQMapNavigator(Context context, double lat, double lng, String name) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.tencent.map");
            intent.setData(Uri.parse(String.format("http://apis.map.qq.com/uri/v1/routeplan?type=drive&to=%s&tocoord=%f,%f&coord_type=1&referer=Back", name, lat, lng)));
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "您尚未安装腾讯地图软件或无权限调用.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 启动谷歌地图软件经行路线规划
     *
     * @param context
     * @param lat     终点纬度(gcj02)
     * @param lng     终点经度(gcj02)
     */
    public static void startGoogleMapNavigator(Context context, double lat, double lng) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.apps.maps");
            intent.setData(Uri.parse(String.format("http://ditu.google.cn/maps?f=d&source=s_d&daddr=%f,%f&hl=zh", lat, lng)));
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "您尚未安装谷歌地图软件或无权限调用.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 调用系统呼叫电话
     *
     * @param context
     * @param number  呼叫号码
     */
    public static void callPhone(Context context, String number) {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + number));
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "找不到关联的程序,发起拨号失败!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 调用系统发送短信
     *
     * @param context
     * @param number  发送号码
     * @param content 短信内容
     */
    public static void sendSMS(Context context, String number, String content) {
        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("smsto:" + number));
            intent.putExtra("sms_body", content);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "找不到关联的程序,短信发送失败!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 前往应用设置
     * @param context
     */
    public static void startApplicationSetting(Context context) {
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "找不到关联的程序!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 设置MIUI的神隐模式
     *
     * @param context
     * @param pkgname 自定义包名,为空代表自己
     * @param appname 自定义应用名,为空代表自己
     * @return 返回结果
     */
    public static boolean setMIUIPowerKeeper(Context context, String pkgname, String appname) {
        if (!DeviceUtil.isMIUI()) return false;
        try {
            Intent intent = new Intent("miui.intent.action.HIDDEN_APPS_CONFIG_ACTIVITY");
            intent.putExtra("package_name", pkgname != null ? pkgname : DeviceUtil.getPackageName(context));
            intent.putExtra("package_label", appname != null ? appname : DeviceUtil.getApplicationName(context));
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 安装Apk文件
     *
     * @param context
     * @param file
     */
    public static void installApk(Context context, File file) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            // 华为EMUI说,我必须要这句
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "没有找到程序安装器,软件安装失败!", Toast.LENGTH_SHORT).show();
        }
    }

    public static void callAlbum(Activity activity, int requestCode) {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            activity.startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            Toast.makeText(activity, "没有找到合适的相册程序.", Toast.LENGTH_SHORT).show();
        }
    }

    public static void callCamera(Activity activity, Uri uri, int requestCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", false);
        try {
            activity.startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            Toast.makeText(activity, "没有找到合适的相机程序.", Toast.LENGTH_SHORT).show();
        }
    }

    public static void callCrop(Activity activity,
                                Uri in,
                                Uri out,
                                int out_width,
                                int out_height,
                                int requestCode) {
        Intent intent = new Intent();
        intent.setAction("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(in, "image/*");// mUri是已经选择的图片Uri
        intent.putExtra("crop", true);
        intent.putExtra("scale", true);
        intent.putExtra("aspectX", out_width);// 裁剪框比例
        intent.putExtra("aspectY", out_height);
        intent.putExtra("outputX", out_width);// 输出图片大小
        intent.putExtra("outputY", out_height);
        intent.putExtra("return-data", false);
        intent.putExtra("noFaceDetection", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, out);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        try {
            activity.startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            Toast.makeText(activity, "没有找到合适的裁剪程序.", Toast.LENGTH_SHORT).show();
        }
    }
}
