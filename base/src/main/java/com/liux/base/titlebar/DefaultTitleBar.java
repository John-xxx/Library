package com.liux.base.titlebar;

import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liux.base.R;

/**
 * 默认的自定义{@link TitleBar}实现 <br>
 * 在 {@link Window#ID_ANDROID_CONTENT} 布局下插入一个线性布局, <br>
 * 并将 DefaultTitleBar 和原来的子布局插入/移动到线性布局 <br>
 *
 * http://blog.csdn.net/yewei02538/article/details/60979075
 */
public class DefaultTitleBar extends TransparentTitleBar {
    private View mTitleBar, mBack, mMore;
    private TextView mTitle, mBackText, mMoreText;
    private ImageView mBackImage, mMoreImage;

    private OnTitleBarListener mOnTitleBarListener;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int i = v.getId();
            if (i == R.id.view_titlebar_default_back) {
                if (mOnTitleBarListener == null || !mOnTitleBarListener.onBack()) {
                    getActivity().onBackPressed();
                }
            } else if (i == R.id.view_titlebar_default_more) {
                if (mOnTitleBarListener == null || !mOnTitleBarListener.onMore()) {

                }
            }
        }
    };

    public DefaultTitleBar(AppCompatActivity activity) {
        super(activity);

        // 默认开启自适应输入法模式
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    @Override
    public void initView(int topPadding) {
        super.initView(topPadding);

        AppCompatActivity activity = getActivity();

        FrameLayout content = getContent();

        LinearLayout content_chlid = new LinearLayout(activity);
        content_chlid.setOrientation(LinearLayout.VERTICAL);

        mTitleBar = LayoutInflater.from(activity).inflate(R.layout.view_titlebar_default, content_chlid, false);
        mTitleBar.setPadding(
                mTitleBar.getPaddingLeft(),
                topPadding,
                mTitleBar.getPaddingRight(),
                mTitleBar.getPaddingBottom()
        );
        content_chlid.addView(mTitleBar);

        for (int i = 0; i < content.getChildCount(); i++) {
            View view = content.getChildAt(i);
            content.removeView(view);
            content_chlid.addView(view);
        }

        content.addView(content_chlid);

        mBack = mTitleBar.findViewById(R.id.view_titlebar_default_back);
        mMore = mTitleBar.findViewById(R.id.view_titlebar_default_more);
        mTitle = (TextView) mTitleBar.findViewById(R.id.view_titlebar_default_title);
        mBackText = (TextView) mTitleBar.findViewById(R.id.view_titlebar_default_back_text);
        mMoreText = (TextView) mTitleBar.findViewById(R.id.view_titlebar_default_more_text);
        mBackImage = (ImageView) mTitleBar.findViewById(R.id.view_titlebar_default_back_image);
        mMoreImage = (ImageView) mTitleBar.findViewById(R.id.view_titlebar_default_more_image);

        mBack.setOnClickListener(mOnClickListener);
        mMore.setOnClickListener(mOnClickListener);

//            TypedValue typedValue = new TypedValue();
//
//            activity.getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
//            mTitleBar.setBackgroundColor(typedValue.data);
//
//            activity.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
//            ((ViewGroup) mTitleBar).getChildAt(0).setBackgroundColor(typedValue.data);
    }

    @Override
    public void setTitle(String title) {
        super.setTitle(title);

        mTitle.setText(title);
    }

    public DefaultTitleBar hasBack(boolean has) {
        mBack.setVisibility(has ? View.VISIBLE : View.GONE);
        return this;
    }

    public DefaultTitleBar hasMore(boolean has) {
        mMore.setVisibility(has ? View.VISIBLE : View.GONE);
        return this;
    }

    public DefaultTitleBar setOnTitleBarListener(OnTitleBarListener listener) {
        mOnTitleBarListener = listener;
        return this;
    }

    public View getBack() {
        return mBack;
    }

    public View getMore() {
        return mMore;
    }

    public ImageView getBackIcon() {
        return mBackImage;
    }

    public ImageView getMoreIcon() {
        return mMoreImage;
    }

    public TextView getBackText() {
        return mBackText;
    }

    public TextView getMoreText() {
        return mMoreText;
    }

    public interface OnTitleBarListener {

        /**
         * 返回事件触发 <br>
         * @return 是否已处理,否则调用 {@link android.app.Activity#onBackPressed()}
         */
        boolean onBack();

        /**
         * 更多事件处理 <br>
         * @return 是否已处理
         */
        boolean onMore();
    }
}
