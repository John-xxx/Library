package com.liux.example.abstracts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;

import com.liux.abstracts.AbstractsActivity;
import com.liux.example.R;
import com.liux.view.SingleToast;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Length;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Liux on 2017/12/3.
 */

public class DefaultTitleBarActivity extends AbstractsActivity {

    @Length(message = "请输入电话号码", min = 11, max = 11)
    @BindView(R.id.et_text1)
    EditText etText1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_demo);
        ButterKnife.bind(this);

//        DefaultTitleBar titleBar = getTitleBar();
//        titleBar
//                .setTitleBarColor()
//                .setStatusBarColor()
//                .setOnTitleBarListener()
//                .setStatusBarMode(false)
//                .setTitle()
//                .setTitleColor()
//                .hasBack()
//                .getBack()
//                .getBackIcon()
//                .getBackText()
//                .hasMore()
//                .getMore()
//                .getMoreIcon()
//                .getMoreText();
        // 忽略某控件
        addIgnoreView(findViewById(R.id.btn_button_1));
    }

    @OnClick({R.id.btn_button_1, R.id.btn_button_2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_button_1:
                getValidator().setViewValidatedAction(new Validator.ViewValidatedAction() {
                    @Override
                    public void onAllRulesPassed(View view) {
                        SingleToast.makeText(view.getContext(), "验证通过", SingleToast.LENGTH_SHORT).show();
                    }
                });
                getValidator().validate();
                break;
            case R.id.btn_button_2:
                break;
        }
    }
}
