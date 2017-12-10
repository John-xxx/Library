package com.liux.downloader;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Liux on 2017/12/10.
 */

public class Task implements Parcelable {
    public static final int STATUS_UNKOWN = 0;
    public static final int STATUS_AWAIT = 1;
    public static final int STATUS_DOWNLOADING = 2;
    public static final int STATUS_FINISH = 3;
    public static final int STATUS_PAUSE = 4;
    public static final int STATUS_ERROR = 5;

    private int id;
    private String url;
    private String header;
    private String dir;
    private String name;
    private long sofar;
    private long total;
    private String etag;
    private int status;
    private long update;

    public Task() {

    }

    public Task(String url, String dir, String headerString) {
        this.url = url;
        this.dir = dir;
        this.header = headerString;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSofar() {
        return sofar;
    }

    public void setSofar(long sofar) {
        this.sofar = sofar;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getUpdate() {
        return update;
    }

    public void setUpdate(long update) {
        this.update = update;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.url);
        dest.writeString(this.header);
        dest.writeString(this.dir);
        dest.writeString(this.name);
        dest.writeLong(this.sofar);
        dest.writeLong(this.total);
        dest.writeString(this.etag);
        dest.writeInt(this.status);
        dest.writeLong(this.update);
    }

    protected Task(Parcel in) {
        this.id = in.readInt();
        this.url = in.readString();
        this.header = in.readString();
        this.dir = in.readString();
        this.name = in.readString();
        this.sofar = in.readLong();
        this.total = in.readLong();
        this.etag = in.readString();
        this.status = in.readInt();
        this.update = in.readLong();
    }

    public static final Parcelable.Creator<Task> CREATOR = new Parcelable.Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel source) {
            return new Task(source);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    public void readFromParcel(Parcel reply) {
        this.id = reply.readInt();
        this.url = reply.readString();
        this.header = reply.readString();
        this.dir = reply.readString();
        this.name = reply.readString();
        this.sofar = reply.readLong();
        this.total = reply.readLong();
        this.etag = reply.readString();
        this.status = reply.readInt();
        this.update = reply.readLong();
    }
}
