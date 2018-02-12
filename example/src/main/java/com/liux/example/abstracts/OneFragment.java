package com.liux.example.abstracts;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.liux.abstracts.AbstractsFragment;
import com.liux.example.R;
import com.liux.view.SingleToast;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Liux on 2017/12/3.
 */

public class OneFragment extends AbstractsFragment {
    Unbinder unbinder;

    @Length(message = "请输入电话号码", min = 11, max = 11)
    @BindView(R.id.et_text1)
    EditText etText1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_base_one, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        addIgnoreView(rootView.findViewById(R.id.btn_button_1));

        return rootView;
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
                startActivity(new Intent(getContext(), DialogActivity.class));
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
