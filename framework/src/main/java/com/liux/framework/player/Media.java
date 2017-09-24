package com.liux.framework.player;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Liux on 2017/9/23.
 */

public class Media implements Parcelable {

    private Uri uri;
    private Map<String, String> headers;

    private int position;
    private String title;

    /**
     * 媒体 Uri 和请求头
     * @param uri
     */
    public Media(Uri uri, Map<String, String> headers) {
        if (uri == null) throw new NullPointerException("Media sources cannot be empty");

        this.uri = uri;
        this.headers = headers;

        position = 0;
        title = null;
    }

    /**
     * 获取媒体 Uri
     * @return
     */
    public Uri getUri() {
        return uri;
    }

    /**
     * 获取媒体请求头
     * @return
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * 获取缓存的位置
     * @return
     */
    public int getPosition() {
        return position;
    }

    /**
     * 保存当前播放进度
     * @param position
     * @return
     */
    public Media setPosition(int position) {
        this.position = position;
        return this;
    }

    /**
     * 获取视频标题
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     * 保存视频标题
     * @param title
     * @return
     */
    public Media setTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.uri, flags);
        int headersSize = this.headers != null ? this.headers.size() : 0;
        dest.writeInt(headersSize);
        if (headersSize > 0) {
            for (Map.Entry<String, String> entry : this.headers.entrySet()) {
                dest.writeString(entry.getKey());
                dest.writeString(entry.getValue());
            }
        }
        dest.writeInt(this.position);
        dest.writeString(this.title);
    }

    protected Media(Parcel in) {
        this.uri = in.readParcelable(Uri.class.getClassLoader());
        int headersSize = in.readInt();
        if (headersSize > 0) {
            this.headers = new HashMap<String, String>(headersSize);
            for (int i = 0; i < headersSize; i++) {
                String key = in.readString();
                String value = in.readString();
                this.headers.put(key, value);
            }
        }
        this.position = in.readInt();
        this.title = in.readString();
    }

    public static final Creator<Media> CREATOR = new Creator<Media>() {
        @Override
        public Media createFromParcel(Parcel source) {
            return new Media(source);
        }

        @Override
        public Media[] newArray(int size) {
            return new Media[size];
        }
    };
}
