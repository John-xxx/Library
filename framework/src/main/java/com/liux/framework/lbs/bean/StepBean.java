package com.liux.framework.lbs.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * 路线节点封装 <br>
 * 2017/4/28 <br>
 *
 * @author Liux
 */

public class StepBean implements Parcelable {
    // 节点内经纬度点
    private List<PositionBean> point = new ArrayList<>();
    // 节点距离(米)
    private float distance;
    // 节点费用(元)
    private double money;
    // 节点时间(秒)
    private long time;
    // 节点方向
    private float direction;

    public List<PositionBean> getPoint() {
        return point;
    }

    public StepBean setPoint(List<PositionBean> point) {
        this.point = point;
        return this;
    }

    public float getDistance() {
        return distance;
    }

    public StepBean setDistance(float distance) {
        this.distance = distance;
        return this;
    }

    public double getMoney() {
        return money;
    }

    public StepBean setMoney(double money) {
        this.money = money;
        return this;
    }

    public long getTime() {
        return time;
    }

    public StepBean setTime(long time) {
        this.time = time;
        return this;
    }

    public float getDirection() {
        return direction;
    }

    public StepBean setDirection(float direction) {
        this.direction = direction;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.point);
        dest.writeFloat(this.distance);
        dest.writeDouble(this.money);
        dest.writeLong(this.time);
        dest.writeFloat(this.direction);
    }

    public StepBean() {
    }

    protected StepBean(Parcel in) {
        this.point = in.createTypedArrayList(PositionBean.CREATOR);
        this.distance = in.readFloat();
        this.money = in.readDouble();
        this.time = in.readLong();
        this.direction = in.readFloat();
    }

    public static final Parcelable.Creator<StepBean> CREATOR = new Parcelable.Creator<StepBean>() {
        @Override
        public StepBean createFromParcel(Parcel source) {
            return new StepBean(source);
        }

        @Override
        public StepBean[] newArray(int size) {
            return new StepBean[size];
        }
    };
}
