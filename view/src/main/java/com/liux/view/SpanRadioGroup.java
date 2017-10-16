package com.liux.view;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * 支持控制子孙层 RadioButton 的 RadioGroup
 * Created by Liux on 2016/11/28.
 */
public class SpanRadioGroup extends RadioGroup {
    private static String TAG = "SpanRadioGroup";

    private int mCheckedId = View.NO_ID;
    private boolean mProtectFromCheckedChange = false;
    private OnCheckedChangeListener mOnCheckedChangeListener;

    private OnHierarchyChangeListener mOnHierarchyChangeListener = new OnHierarchyChangeListener() {
        @Override
        public void onChildViewAdded(View parent, View child) {
            if (child instanceof CompoundButton) {
                int id = child.getId();

                if (id == View.NO_ID && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    id = View.generateViewId();
                    child.setId(id);
                }

                if (id != View.NO_ID && ((CompoundButton) child).isChecked()) {
                    setCheckedId(id);
                }

                ((CompoundButton) child).setOnCheckedChangeListener(mChildOnCheckedChangeListener);
            } else {
                findCheckedView(child);
            }
        }

        @Override
        public void onChildViewRemoved(View parent, View child) {
            if (child instanceof CompoundButton) {
                ((RadioButton) child).setOnCheckedChangeListener(null);
            }
        }
    };

    private CompoundButton.OnCheckedChangeListener mChildOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (mProtectFromCheckedChange) {
                return;
            }

            mProtectFromCheckedChange = true;
            if (getCheckedRadioButtonId() != -1) {
                setCheckedStateForView(getCheckedRadioButtonId(), false);
            }
            mProtectFromCheckedChange = false;

            int id = buttonView.getId();
            setCheckedId(id);
        }
    };

    public SpanRadioGroup(Context context) {
        super(context);
        init();
    }

    public SpanRadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    public int getCheckedRadioButtonId() {
        return mCheckedId;
    }

    /**
     * 在某些情况下孙控件被 remove 后无法控制的情况,手动刷新
     */
    public void refreshChild() {
        findCheckedView(this);
    }

    public OnCheckedChangeListener getOnCheckedChangeListener() {
        return mOnCheckedChangeListener;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        this.mOnCheckedChangeListener = listener;
    }

    private void init() {
        setOnHierarchyChangeListener(mOnHierarchyChangeListener);
    }

    private void setCheckedId(int id) {
        mCheckedId = id;
        if (mOnCheckedChangeListener != null) {
            mOnCheckedChangeListener.onCheckedChanged(this, id);
        }
    }

    private void setCheckedStateForView(int viewId, boolean checked) {
        View checkedView = findViewById(viewId);
        if (checkedView != null && checkedView instanceof RadioButton) {
            ((RadioButton) checkedView).setChecked(checked);
        }
    }

    private void findCheckedView(View child) {
        if (child instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) child;
            for (int i = 0; i < group.getChildCount(); i++) {
                mOnHierarchyChangeListener.onChildViewAdded(group, group.getChildAt(i));
            }
        }
    }
}
