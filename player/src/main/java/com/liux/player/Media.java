package com.liux.player;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Liux on 2017/10/27.
 */

public class Media implements Parcelable {
    private Uri uri;
    private Map<String, String> headers;

    private int pos;
    private String title;
    private Object tag;

    public static Media create(Uri uri) {
        return new Media().setUri(uri);
    }

    public static Media create(String path) {
        return new Media().setPath(path);
    }

    public static Media create(Uri uri, Map<String, String> headers) {
        return new Media().setUri(uri).setHeaders(headers);
    }

    public static Media create(String path, Map<String, String> headers) {
        return new Media().setPath(path).setHeaders(headers);
    }

    public static boolean isEmpty(Media media) {
        if (media == null) return true;
        if (media.getUri() == null || Uri.EMPTY.equals(media.getUri())) return true;
        return false;
    }

    public static void checkEmpty(Media media) {
        if (Media.isEmpty(media)) {
            throw new NullPointerException("Player media is empty");
        }
    }

    public Media() {
        
    }

    public Media setUri(Uri uri) {
        this.uri = uri;
        return this;
    }

    public Media setPath(String path) {
        this.uri = Uri.parse(path);
        return this;
    }

    public Media setHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public Uri getUri() {
        return uri;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public int getPos() {
        return pos;
    }

    public Media setPos(int pos) {
        this.pos = pos;
        return this;
    }

    public String getTitle() {
        if (!TextUtils.isEmpty(title)) {
            return title;
        }
        return uri.toString();
    }

    public Media setTitle(String title) {
        this.title = title;
        return this;
    }

    public Object getTag() {
        return tag;
    }

    public Media setTag(Object tag) {
        this.tag = tag;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Media media = (Media) o;

        if (uri == null) {
            if (media.uri != null) {
                return false;
            }
        } else if (!uri.equals(media.uri)) {
            return false;
        }
        if (headers == null) {
            if (media.headers != null) {
                return false;
            }
        } else if (!headers.equals(media.headers)){
            return false;
        }
//        if (pos != media.pos) return false;
//        if (title == null && media.title != null) return false;
//        if (!title.equals(media.title)) return false;
        return true;
    }

    @Override
    public String toString() {
        return "Media{" +
                "uri=" + uri +
                ", headers=" + headers +
                ", pos=" + pos +
                ", title='" + title + '\'' +
                ", tag=" + tag +
                '}';
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
        dest.writeInt(this.pos);
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
        this.pos = in.readInt();
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
