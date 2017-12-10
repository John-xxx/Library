package com.liux.downloader;

import java.io.File;

/**
 * Created by Liux on 2017/12/8.
 */

public class Config {

    private int maxCount;
    private File director;

    public int getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    public File getDirector() {
        return director;
    }

    public void setDirector(File director) {
        this.director = director;
    }

    public static class Builder {

        public Builder director(File dir) {
            return this;
        }

        public Builder maxDownloadCount(int count) {
            return this;
        }

        public Config build() {
            return null;
        }
    }
}
