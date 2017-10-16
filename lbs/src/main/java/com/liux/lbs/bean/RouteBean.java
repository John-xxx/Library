package com.liux.lbs.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * 路线信息封装 <br>
 * 2017/4/28 <br>
 *
 * @author Liux
 */

public class RouteBean implements Parcelable {
    // 节点信息
    private List<StepBean> step = new ArrayList<>();
    // 全程总距离(米)
    private float distance;
    // 全程费用(元)
    private double money;
    // 全程时间(秒)
    private long time;

    public List<StepBean> getStep() {
        return step;
    }

    public RouteBean setStep(List<StepBean> step) {
        this.step = step;
        return this;
    }

    public float getDistance() {
        return distance;
    }

    public RouteBean setDistance(float distance) {
        this.distance = distance;
        return this;
    }

    public double getMoney() {
        return money;
    }

    public RouteBean setMoney(double money) {
        this.money = money;
        return this;
    }

    public long getTime() {
        return time;
    }

    public RouteBean setTime(long time) {
        this.time = time;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.step);
        dest.writeFloat(this.distance);
        dest.writeDouble(this.money);
        dest.writeLong(this.time);
    }

    public RouteBean() {
    }

    protected RouteBean(Parcel in) {
        this.step = in.createTypedArrayList(StepBean.CREATOR);
        this.distance = in.readFloat();
        this.money = in.readDouble();
        this.time = in.readLong();
    }

    public static final Creator<RouteBean> CREATOR = new Creator<RouteBean>() {
        @Override
        public RouteBean createFromParcel(Parcel source) {
            return new RouteBean(source);
        }

        @Override
        public RouteBean[] newArray(int size) {
            return new RouteBean[size];
        }
    };
}
