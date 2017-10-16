package com.liux.lbs.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 地理位置点属性封装 <br>
 * Created by Liux on 2016/12/2.
 */
public class PointBean implements Parcelable {
    // 对应百度ID
    private int bdid;
    // 地理位置代码
    private int code;
    // 上级代码
    private int superior;
    // 0_全国 1_省份 2_城市 3_县区
    private int type;
    // 城市名称
    private String city;
    // 经度
    private double lon;
    // 纬度
    private double lat;
    // 方向
    private float direction;
    // 精度
    private float accuracy;
    // 海拔
    private double altitude;
    // 速度
    private float speed;
    // 缩放等级
    private int zoom;
    // 标题
    private String title;
    // 地址
    private String address;
    // 定位方式
    private String mode;

    public String getTitle() {
        return title;
    }

    public PointBean setTitle(String title) {
        this.title = title;
        return this;
    }

    public int getBdid() {
        return bdid;
    }

    public PointBean setBdid(int bdid) {
        this.bdid = bdid;
        return this;
    }

    public int getCode() {
        return code;
    }

    public PointBean setCode(int code) {
        this.code = code;
        return this;
    }

    public double getLat() {
        return lat;
    }

    public PointBean setLat(double lat) {
        this.lat = lat;
        return this;
    }

    public double getLon() {
        return lon;
    }

    public PointBean setLon(double lon) {
        this.lon = lon;
        return this;
    }

    public String getCity() {
        return city;
    }

    public PointBean setCity(String city) {
        this.city = city;
        return this;
    }

    public int getType() {
        return type;
    }

    public PointBean setType(int type) {
        this.type = type;
        return this;
    }

    public int getSuperior() {
        return superior;
    }

    public PointBean setSuperior(int superior) {
        this.superior = superior;
        return this;
    }

    public int getZoom() {
        return zoom;
    }

    public PointBean setZoom(int zoom) {
        this.zoom = zoom;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public PointBean setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getMode() {
        return mode;
    }

    public PointBean setMode(String mode) {
        this.mode = mode;
        return this;
    }

    public float getDirection() {
        return direction;
    }

    public PointBean setDirection(float direction) {
        this.direction = direction;
        return this;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public PointBean setAccuracy(float accuracy) {
        this.accuracy = accuracy;
        return this;
    }

    public double getAltitude() {
        return altitude;
    }

    public PointBean setAltitude(double altitude) {
        this.altitude = altitude;
        return this;
    }

    public float getSpeed() {
        return speed;
    }

    public PointBean setSpeed(float speed) {
        this.speed = speed;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.bdid);
        dest.writeInt(this.code);
        dest.writeInt(this.superior);
        dest.writeInt(this.type);
        dest.writeString(this.city);
        dest.writeDouble(this.lon);
        dest.writeDouble(this.lat);
        dest.writeFloat(this.direction);
        dest.writeFloat(this.accuracy);
        dest.writeDouble(this.altitude);
        dest.writeFloat(this.speed);
        dest.writeInt(this.zoom);
        dest.writeString(this.title);
        dest.writeString(this.address);
        dest.writeString(this.mode);
    }

    public PointBean() {
    }

    protected PointBean(Parcel in) {
        this.bdid = in.readInt();
        this.code = in.readInt();
        this.superior = in.readInt();
        this.type = in.readInt();
        this.city = in.readString();
        this.lon = in.readDouble();
        this.lat = in.readDouble();
        this.direction = in.readFloat();
        this.accuracy = in.readFloat();
        this.altitude = in.readDouble();
        this.speed = in.readFloat();
        this.zoom = in.readInt();
        this.title = in.readString();
        this.address = in.readString();
        this.mode = in.readString();
    }

    public static final Creator<PointBean> CREATOR = new Creator<PointBean>() {
        @Override
        public PointBean createFromParcel(Parcel source) {
            return new PointBean(source);
        }

        @Override
        public PointBean[] newArray(int size) {
            return new PointBean[size];
        }
    };
}
