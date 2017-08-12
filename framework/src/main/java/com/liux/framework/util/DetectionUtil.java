package com.liux.framework.util;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import java.util.List;

/**
 * Created by Liux on 2016/8/17.
 */
public class DetectionUtil {

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

    /**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     * @return true 表示开启
     */
    public static boolean isLocation(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }
        return false;
    }

    /**
     * 判断WiFi是否连接
     * @param context
     * @return
     */
    public static boolean isWifiConnected(Context context) {
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return manager.isWifiEnabled();
    }

    /**
     * 取当前网络类型
     * @param context
     * @return -1：没有网络  1：WIFI网络  2：移动数据
     */
    public static int getNetype(Context context) {
        int netType = -1;
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if(networkInfo == null) {
            return netType;
        }
        int nType = networkInfo.getType();
        if(nType == ConnectivityManager.TYPE_MOBILE) {
            netType = 2;
        }
        else if(nType == ConnectivityManager.TYPE_WIFI) {
            netType = 1;
        }
        return netType;
    }
}
