package com.liux.downloader;

import java.io.File;

/**
 * Created by Liux on 2017/12/8.
 */

public class Config {

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
