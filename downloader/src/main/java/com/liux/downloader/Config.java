package com.liux.downloader;

import java.io.File;

/**
 * Created by Liux on 2017/12/8.
 */

public class Config {

    private int maxCount;
    private File director;

    public Config() {
        this.maxCount = 3;
        this.director = null;
    }

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

        private int maxCount;
        private File director;

        public Builder director(File director) {
            this.director = director;
            return this;
        }

        public Builder maxDownloadCount(int maxCount) {
            if (maxCount < 1) maxCount = 1;
            this.maxCount = maxCount;
            return this;
        }

        public Config build() {
            Config config = new Config();

            config.setMaxCount(maxCount);
            config.setDirector(director);

            return config;
        }
    }
}
