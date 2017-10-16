package com.liux.other;

import android.os.FileObserver;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * 能够监控子目录的文件监听器
 */
public abstract class RecursiveFileObserver extends FileObserver {
    private final static String TAG = "RecursiveFileObserver";

    private List<SingleFileObserver> mObservers;
    private String mPath;
    private int mMask;

    public RecursiveFileObserver(String path) {
        this(path, ALL_EVENTS);
    }

    public RecursiveFileObserver(String path, int mask) {
        super(path, mask);
        mPath = path;
        mMask = mask;

        initObservers();
    }

    private void initObservers() {
        mObservers = new ArrayList();

        mObservers.add(new SingleFileObserver(mPath, mMask));
    }

    @Override
    public void startWatching() {
        if (mObservers == null) {
            return;
        }

        for (SingleFileObserver observer : mObservers) {
            observer.startWatching();
        }
    }

    @Override
    public void stopWatching() {
        if (mObservers == null) {
            return;
        }

        for (SingleFileObserver observer : mObservers) {
            observer.stopWatching();
        }

        mObservers.clear();
        mObservers = null;
    }

    public class SingleFileObserver extends FileObserver {
        private String mPath;
        private int mMask;

        public SingleFileObserver(String path) {
            this(path, ALL_EVENTS);
        }

        public SingleFileObserver(String path, int mask) {
            super(path, mask);
            mPath = path;
            mMask = mask;

            initChildDir();
        }

        private void initChildDir() {
            File[] dirs = new File(mPath).listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isDirectory();
                }
            });

            if (dirs != null) {
                for (File dir : dirs) {
                    mObservers.add(new SingleFileObserver(dir.getPath(), mMask));
                }
            }
        }

        @Override
        public void onEvent(int event, String path) {
            String newPath = mPath + "/" + path;
            if ((event & ALL_EVENTS) == CREATE) {
                SingleFileObserver observer = new SingleFileObserver(newPath, mMask);
                observer.startWatching();
                mObservers.add(observer);
            }
            RecursiveFileObserver.this.onEvent(event, newPath);
        }
    }
}