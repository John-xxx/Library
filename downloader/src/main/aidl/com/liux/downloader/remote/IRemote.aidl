package com.liux.downloader.remote;

import com.liux.downloader.local.ILocal;
import com.liux.downloader.Task;

interface IRemote {

    void registerLocal(ILocal local);

    void getTask(inout Task task);
}
