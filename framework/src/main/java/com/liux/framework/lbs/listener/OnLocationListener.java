package com.liux.framework.lbs.listener;

import com.liux.framework.lbs.bean.PointBean;

/**
 * Created by Liux on 2017/8/13.
 */

public interface OnLocationListener {

    void onSucceed(PointBean position);

    void onFailure();
}
