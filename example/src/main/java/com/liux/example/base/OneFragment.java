package com.liux.example.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liux.abstracts.AbstractsFragment;
import com.liux.example.R;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Liux on 2017/12/3.
 */

public class OneFragment extends AbstractsFragment {
    Unbinder unbinder;

    @Override
    protected void onInitData(Bundle savedInstanceState) {

    }

    @Override
    protected void onRestoreData(Bundle data) {

    }

    @Override
    protected View onInitView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_base_one, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    protected void onLazyLoad() {

    }

    @Override
    protected void onSaveData(Bundle data) {

    }

    @Override
    protected void onVisibleChanged() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btn_button)
    public void onViewClicked() {
        startActivity(new Intent(getContext(), DialogActivity.class));
    }
}
