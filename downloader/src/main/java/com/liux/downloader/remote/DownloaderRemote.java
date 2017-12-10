package com.liux.downloader.remote;

import android.os.RemoteException;

import com.liux.downloader.Task;
import com.liux.downloader.local.ILocal;

/**
 * Created by Liux on 2017/12/10.
 */

public class DownloaderRemote extends IRemote.Stub {

    private ILocal mILocal;

    @Override
    public void registerLocal(ILocal local) throws RemoteException {
        mILocal = local;
    }

    @Override
    public void getTask(Task task) throws RemoteException {

    }
}
