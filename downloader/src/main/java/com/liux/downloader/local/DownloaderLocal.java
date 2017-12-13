package com.liux.downloader.local;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.liux.downloader.Config;
import com.liux.downloader.Task;
import com.liux.downloader.listener.TaskCallBack;
import com.liux.downloader.remote.DownloaderService;
import com.liux.downloader.remote.IRemote;
import com.liux.downloader.util.DiskUtil;
import com.liux.downloader.util.TaskUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Liux on 2017/12/8.
 */

public class DownloaderLocal extends ILocal.Stub {

    private Config mConfig;
    private Context mContext;
    private RemoteSimulate mRemoteSimulate;

    private List<TaskCallBack> mTaskCallBacks = new ArrayList<>();

    private IRemote mIRemote;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        private int mRetry = 0;
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                IRemote remote = IRemote.Stub.asInterface(service);
                remote.registerLocal(DownloaderLocal.this);
                mIRemote = remote;
                mRetry = 0;
            } catch (RemoteException e) {
                mRetry ++;
                if (mRetry <= 3) {
                    bindDownloadService();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIRemote = null;
            mRetry = 0;
        }
    };

    public DownloaderLocal(Context context, Config config) {
        mConfig = config;
        mContext = context.getApplicationContext();

        mRemoteSimulate = new RemoteSimulate();

        bindDownloadService();
    }

    public void addCallBack(TaskCallBack callBack) {
        mTaskCallBacks.add(callBack);
    }

    public void removeCallBack(TaskCallBack callBack) {
        mTaskCallBacks.remove(callBack);
    }

    public Task getTask(String url, File dir, Map<String, String> header, String name) {
        if (dir == null) dir = mConfig.getDirector();
        if (dir == null) dir = DiskUtil.getStorageDir(mContext);
        Task task = TaskUtil.creatorTask(url, dir, header, name);
        try {
            if (isConnected()) {
                mIRemote.getTask(task);
            } else {
                mRemoteSimulate.getTask(task);
            }
        } catch (RemoteException e) {

        }
        return task;
    }

    public boolean isConnected() {
        return mIRemote != null;
    }

    private void bindDownloadService() {
        Intent intent = new Intent(mContext, DownloaderService.class);
        mContext.startService(intent);
        mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }
}
