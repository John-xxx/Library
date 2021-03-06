package com.liux.abstracts;

import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

import com.liux.abstracts.titlebar.TitleBar;
import com.liux.abstracts.touch.TouchCallback;
import com.mobsandgeeks.saripaar.Validator;

import java.util.Map;

/**
 * 2018/2/12
 * By Liux
 * lx0758@qq.com
 */

public interface IAbstractsActivity extends TouchCallback, Validator.ValidationListener {

    AppCompatActivity getTarget();

    // ===============================================================

    /**
     * 复写此方法实现自定义ToolBar
     * @return
     */
    TitleBar onInitTitleBar();

    /**
     * 获取当前使用的TitleBar
     * @return
     */
    <T extends TitleBar>T getTitleBar();

    // ===============================================================

    /**
     * 使用 {@link AppCompatActivity#getLastCustomNonConfigurationInstance()}
     * @param data
     */
    void onRestoreData(Map<String, Object> data);

    /**
     * {@link AppCompatActivity#onRetainCustomNonConfigurationInstance()} 后调用
     * @param data
     */
    void onSaveData(Map<String, Object> data);

    // ===============================================================

    /**
     * 调用父类 dispatchTouchEvent
     * @param event
     * @return
     */
    boolean superDispatchTouchEvent(MotionEvent event);

    /**
     * 调用父类 onTouchEvent
     * @param event
     * @return
     */
    boolean superOnTouchEvent(MotionEvent event);

    // ===============================================================

    /**
     * 获取表单验证器
     * @return
     */
    Validator getValidator();

    /**
     * 显示表单验证失败信息
     * @param message
     */
    void onValidationFailed(String message);
}
