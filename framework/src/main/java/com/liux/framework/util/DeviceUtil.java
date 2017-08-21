package com.liux.framework.util;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;

import java.util.List;

/**
 * Created by Liux on 2016/6/18.
 */
public class DeviceUtil {
    private static String TAG = "DeviceUtil";

    public static String getApplicationName(Context context) {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = context.getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        String applicationName = (String) packageManager.getApplicationLabel(applicationInfo);
        return applicationName;
    }

    public static String getPackageName(Context context) {
        return context.getPackageName();
    }

    public static int getVersionCode(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static String getVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "Null";
    }

    public static String getDeviceName() {
        return Build.MODEL;
    }

    public static String getOSName() {
        return "Android";
    }

    public static String getOSVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取网络状态
     * @param context
     * @return 0_未知 1_WiFi 2_2G 3_3G 4_4G
     */
    public static int getNetWorkStatus(Context context) {
        int netWorkType = 0;

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            int type = networkInfo.getType();

            if (type == ConnectivityManager.TYPE_WIFI) {
                netWorkType = 1;
            } else if (type == ConnectivityManager.TYPE_MOBILE) {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                switch (telephonyManager.getNetworkType()) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        netWorkType =  2;
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        netWorkType =  3;
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        netWorkType =  4;
                        break;
                    default:
                        netWorkType =  0;
                        break;
                }
            }
        }

        return netWorkType;
    }

    /**
     * 判断WiFi是否开启
     * @param context
     * @return
     */
    public static boolean isWifiEnabled(Context context) {
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return manager.isWifiEnabled();
    }

    /**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     * @param context
     * @return true 表示开启
     */
    public static boolean isOpenGps(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return gps || network;
    }

    /**
     * 判断是否是主进程
     * @param context
     * @return
     */
    public static boolean isMainProcess(Context context) {
        List<ActivityManager.RunningAppProcessInfo> infos = ((ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE)).getRunningAppProcesses();
        String myName = context.getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : infos) {
            if (info.pid == myPid && myName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否是在前台运行
     * 兼容 Android L
     * @param context
     * @return
     */
    public static boolean isRunningForeground(Context context){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> infos = am.getRunningAppProcesses();
        if (infos.size() == 0) return false;
        for(ActivityManager.RunningAppProcessInfo process : infos) {
            if(process.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && process.processName.equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断服务是否运行
     * @param context
     * @param service
     * @return
     */
    public static boolean isServiceRunning(Context context, Class<? extends Service> service) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> list = activityManager.getRunningServices(Integer.MAX_VALUE);
        if (list == null || list.size() == 0) return false;
        for (ActivityManager.RunningServiceInfo info : list) {
            if (info.service.getClassName().equals(service.getName())) return true;
        }
        return false;
    }

    public static boolean isMIUI() {
        return "Xiaomi".toLowerCase().equals(Build.MANUFACTURER.toLowerCase());
    }

    public static boolean isMEIZU() {
        return "Meizu".toLowerCase().equals(Build.MANUFACTURER.toLowerCase());
    }
}
