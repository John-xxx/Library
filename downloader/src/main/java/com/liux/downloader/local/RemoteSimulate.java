package com.liux.downloader.local;

import android.os.RemoteException;

import com.liux.downloader.Task;
import com.liux.downloader.remote.DownloaderRemote;

/**
 * 远程接口本地模拟实现,用于远程服务还未连接成功时访问
 * Created by Liux on 2017/12/10.
 */

class RemoteSimulate extends DownloaderRemote {

    @Override
    public void getTask(Task task) throws RemoteException {

    }
}
