package com.liux.framework.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.liux.framework.R;
import com.liux.framework.util.ScreenUtil;

public class EditTextCus extends AppCompatEditText {
    private static String TAG = "EditTextCus";

    final int DRAWABLE_LEFT = 0;
    final int DRAWABLE_TOP = 1;
    final int DRAWABLE_RIGHT = 2;
    final int DRAWABLE_BOTTOM = 3;

    private boolean mCancel;
    private int mPadding;
    private Drawable mClear;
    private boolean mTouchState;

    private boolean mParting;
    private int mPartingColor;

    private DrawableRightListener mRightListener = new DrawableRightListener() {
        @Override
        public void onDrawableRightClick(View view) {
            EditTextCus.this.setText(null);
        }
    };

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() == 0) {
                setCompoundDrawablePadding(0);
                setCompoundDrawables(null, null, null, null);
            } else {
                setCompoundDrawablePadding(mPadding);
                setCompoundDrawables(null, null, mClear, null);
            }
        }
    };

    private OnFocusChangeListener mOnFocusChangeListener = new OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus && getText().length() != 0) {
                setCompoundDrawablePadding(mPadding);
                setCompoundDrawables(null, null, mClear, null);
            } else {
                setCompoundDrawablePadding(0);
                setCompoundDrawables(null, null, null, null);
            }
        }
    };

    public EditTextCus(Context context) {
        super(context);
        initView();
    }

    public EditTextCus(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttribute(attrs, 0);
        initView();
    }

    public EditTextCus(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttribute(attrs, defStyleAttr);
        initView();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!mParting) {
            super.onDraw(canvas);
        } else {
            super.onDraw(canvas);
        }
    }

    public void setDrawableRightListener(DrawableRightListener listener) {
        this.mRightListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Drawable drawableRight = getCompoundDrawables()[DRAWABLE_RIGHT];
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (drawableRight != null && event.getRawX() >= (getRight() - drawableRight.getBounds().width())) {
                    mTouchState = true;
                    return true ;
                } else {
                    mTouchState = false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mTouchState) {
                    if (drawableRight != null && event.getRawX() >= (getRight() - drawableRight.getBounds().width())) {
                        return true;
                    } else {
                        mTouchState = false;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mTouchState) {
                    if (drawableRight != null && event.getRawX() >= (getRight() - drawableRight.getBounds().width())) {
                        if (mRightListener != null) {
                            mRightListener.onDrawableRightClick(this);
                        }
                        return true ;
                    } else {
                        mTouchState = false;
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private void initAttribute(AttributeSet attrs, int defStyleAttr) {
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.EditTextCus, defStyleAttr, 0);

        mCancel = array.getBoolean(R.styleable.EditTextCus_etc_cancel, true);

        mParting = array.getBoolean(R.styleable.EditTextCus_etc_parting, false) && ((getInputType() & InputType.TYPE_TEXT_FLAG_MULTI_LINE) == InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        mPartingColor = array.getColor(R.styleable.EditTextCus_etc_parting_color, Color.GRAY);

        array.recycle();
    }

    private void initView() {
        if (mCancel) {
            mPadding = ScreenUtil.dp2px(getContext(), 8);
            mClear = getResources().getDrawable(R.drawable.view_edittextcus_clear);
            mClear.setBounds(0, 0, (int) getTextSize(), (int) getTextSize());
            addTextChangedListener(mTextWatcher);
            setOnFocusChangeListener(mOnFocusChangeListener);
        }
        setCompoundDrawablePadding(0);
        setCompoundDrawables(null, null, null, null);

        if (mParting) {

        }
    }

    public interface DrawableRightListener {

        void onDrawableRightClick(View view) ;
    }
}