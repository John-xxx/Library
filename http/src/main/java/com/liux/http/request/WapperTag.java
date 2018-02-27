package com.liux.http.request;

/**
 * 2018/2/27
 * By Liux
 * lx0758@qq.com
 */

public class WapperTag {

    public static Object warpper(Object tag) {
        return new WapperTag(tag);
    }

    private Object mTag;

    public WapperTag(Object tag) {
        this.mTag = tag;
    }

    public Object getTag() {
        return mTag;
    }

    public void setTag(Object mTag) {
        this.mTag = mTag;
    }
}
