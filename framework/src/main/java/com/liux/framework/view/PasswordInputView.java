package com.liux.framework.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputFilter;
import android.text.method.MovementMethod;
import android.util.AttributeSet;

import com.liux.framework.R;
import com.liux.framework.util.ScreenUtil;

@SuppressLint("DrawAllocation")
public class PasswordInputView extends AppCompatEditText {
    
    private int mPasswordLength = 6;
    private int mPasswordColor = 0xff000000;
    private int mPasswordWidth = 10;
    private int mPasswordInterval = 10;
    private Drawable mPasswordDrawable = null;

    private int mOneWidth;
    private int mTextLength;
    private Paint mPasswordPaint;

    private OnPasswordListener mOnPasswordListener;

    public PasswordInputView(Context context) {
        super(context);
    }
    
    public PasswordInputView(Context context, AttributeSet attr) {
        super(context, attr);
        init(context, attr);
    }
    
    private void init(Context context, AttributeSet attr) {
        TypedArray ta = context.obtainStyledAttributes(attr, R.styleable.PasswordInputView);
        try {
            mPasswordWidth = ta.getDimensionPixelSize(R.styleable.PasswordInputView_password_size, ScreenUtil.dp2px(getContext(), mPasswordWidth));
            mPasswordColor = ta.getColor(R.styleable.PasswordInputView_password_color, mPasswordColor);
            mPasswordLength = ta.getInt(R.styleable.PasswordInputView_password_length, mPasswordLength);
            mPasswordDrawable = ta.getDrawable(R.styleable.PasswordInputView_password_drawable);
            mPasswordInterval = ta.getDimensionPixelSize(R.styleable.PasswordInputView_password_interval, ScreenUtil.dp2px(getContext(), mPasswordInterval));
        } catch (Exception e) {
            e.printStackTrace();
        }
        ta.recycle();

        setCursorVisible(false);
        setFilters(new InputFilter[] {new InputFilter.LengthFilter(mPasswordLength)});
        
        mPasswordPaint = new Paint();
        mPasswordPaint.setAntiAlias(true);
        mPasswordPaint.setColor(mPasswordColor);
        mPasswordPaint.setStrokeWidth(mPasswordWidth);
    }

    @Override
    protected MovementMethod getDefaultMovementMethod() {
        //return ArrowKeyMovementMethod.getInstance();
        return null;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(MeasureSpec.UNSPECIFIED, widthMeasureSpec), getDefaultSize(MeasureSpec.UNSPECIFIED, heightMeasureSpec));

        int width = getMeasuredWidth();
        int height = (width - ((mPasswordLength - 1) * mPasswordInterval)) / mPasswordLength;
        mOneWidth = height;
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();

        int left = 0;
        for (int i = 0; i < mPasswordLength; i++) {
            RectF rect = new RectF(left, 0, left + mOneWidth, height);

            // 画边框
            mPasswordDrawable.setBounds((int) rect.left, (int) rect.top, (int) rect.right, (int) rect.bottom);
            mPasswordDrawable.draw(canvas);

            if (i < mTextLength) {
                canvas.drawCircle(rect.left + rect.width() / 2, rect.top + rect.height() / 2, mPasswordWidth, mPasswordPaint);
            }

            left += mOneWidth + mPasswordInterval;
        }
    }
    
    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);

        mTextLength = text.length();
        invalidate();

        if (mOnPasswordListener != null) {
            mOnPasswordListener.setOnPasswordChange(text);
        }

        if (text.length() >= mPasswordLength) {
            if (mOnPasswordListener != null) {
                mOnPasswordListener.setOnPasswordFinished(text.subSequence(0, mPasswordLength));
            }
        }
    }

    public void setmOnPasswordListener(OnPasswordListener mOnPasswordListener) {
        this.mOnPasswordListener = mOnPasswordListener;
    }

    public interface OnPasswordListener {

        void setOnPasswordChange(CharSequence charSequence);

        void setOnPasswordFinished(CharSequence charSequence);
    }
}
