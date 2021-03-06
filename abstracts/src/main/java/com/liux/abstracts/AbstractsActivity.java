package com.liux.abstracts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;

import com.liux.abstracts.titlebar.DefaultTitleBar;
import com.liux.abstracts.titlebar.TitleBar;
import com.liux.abstracts.touch.TouchCallback;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;

import java.util.List;
import java.util.Map;

/**
 * 抽象Activity,提供以下能力 <br>
 * 1.自动隐藏输入法 {@link TouchCallback} <br>
 * 2.实现任意数据的"意外"恢复和存储 {@link #onRestoreData(Map)} {@link #onSaveData(Map)} <br>
 * 3.实现各种特殊场景的 {@link TitleBar} 详见 {@link #onInitTitleBar()} <br>
 * 2017-8-21 <br>
 * 调整恢复数据的调用时机<br>
 * 2017-12-4 <br>
 * 实现 {@link TouchCallback} 事件的过滤
 * 2018-2-12 <br>
 * 1.改代理模式实现
 * 2.移除自定义生命周期
 * Created by Liux on 2017/8/7
 */

public abstract class AbstractsActivity extends AppCompatActivity
        implements IAbstractsActivity {
    private String TAG = "AbstractsActivity";

    private AbstractsActivityProxy mProxy = new AbstractsActivityProxy(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProxy.onCreate();
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return mProxy.onRetainCustomNonConfigurationInstance();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mProxy.onDestroy();
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        mProxy.onTitleChanged(title, color);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return mProxy.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mProxy.onTouchEvent(event);
    }

    @Override
    public AppCompatActivity getTarget() {
        return this;
    }

    @Override
    public TitleBar onInitTitleBar() {
        return new DefaultTitleBar(this);
    }

    @Override
    public <T extends TitleBar>T getTitleBar() {
        return mProxy.getTitleBar();
    }

    @Override
    public boolean superDispatchTouchEvent(MotionEvent event) {
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean superOnTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    public Validator getValidator() {
        return mProxy.getValidator();
    }

    @Override
    public boolean isHandlerTouch() {
        return mProxy.isHandlerTouch();
    }

    @Override
    public void setHandlerTouch(boolean handlerTouch) {
        mProxy.setHandlerTouch(handlerTouch);
    }

    @Override
    public boolean hasIgnoreView(View view) {
        return mProxy.hasIgnoreView(view);
    }

    @Override
    public void addIgnoreView(View view) {
        mProxy.addIgnoreView(view);
    }

    @Override
    public void removeIgnoreView(View view) {
        mProxy.removeIgnoreView(view);
    }

    @Override
    public void onValidationSucceeded() {

    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        mProxy.onValidationFailed(errors);
    }

    @Override
    public void onRestoreData(Map<String, Object> data) {

    }

    @Override
    public void onSaveData(Map<String, Object> data) {

    }

    @Override
    public void onValidationFailed(String message) {

    }
}
