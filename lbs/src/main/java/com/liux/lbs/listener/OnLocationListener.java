package com.liux.lbs.listener;

import com.liux.lbs.bean.PointBean;

/**
 * Created by Liux on 2017/8/13.
 */

public interface OnLocationListener {

    void onSucceed(PointBean position);

    void onFailure(String msg);
}
