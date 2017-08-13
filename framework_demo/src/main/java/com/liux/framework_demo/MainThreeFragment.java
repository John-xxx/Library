package com.liux.framework_demo;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.liux.framework.base.BaseFragment;
import com.liux.framework.list.SelectAdapter;
import com.liux.framework.list.SuperHolder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Liux on 2017/8/9.
 */

public class MainThreeFragment extends BaseFragment {

    @BindView(R.id.rv_list)
    RecyclerView rvList;
    Unbinder unbinder;

    private SelectAdapter<String> mSelectAdapter;

    @Override
    protected void onInitData(Bundle savedInstanceState) {

    }

    @Override
    protected View onInitView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_three, container, false);
        unbinder = ButterKnife.bind(this, view);

        mSelectAdapter = new SelectAdapter<String>(android.R.layout.simple_list_item_1) {
            @Override
            public void onDataBind(SuperHolder holder, String s, State<String> state, final int position) {
                holder.setText(android.R.id.text1, String.format("%s (%d)", s, state.state));
                holder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toggle(position);
                    }
                });
            }
        };
        mSelectAdapter.setOnSelectListener(new SelectAdapter.OnSelectListener<String>() {
            @Override
            public void onSelectChange(String o, int position, boolean isSelect) {
                Toast.makeText(getActivity(), o + " isSelect:" + isSelect, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSelectComplete() {
                List<String> list = mSelectAdapter.getStateAll(SelectAdapter.State.STATE_SELECTED);
                Toast.makeText(getActivity(), list.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvList.setAdapter(mSelectAdapter);
        return view;
    }

    @Override
    protected void onLazyLoad() {

    }

    @Override
    protected void onRestoreData(Bundle data) {

    }

    @Override
    protected void onSaveData(Bundle data) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.btn_add, R.id.btn_del_first, R.id.btn_del_last, R.id.btn_open5, R.id.btn_set8, R.id.btn_close})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
                mSelectAdapter.getDataSource().add(String.valueOf(System.currentTimeMillis()));
                break;
            case R.id.btn_del_first:
                if (mSelectAdapter.getDataSource().isEmpty()) return;
                mSelectAdapter.getDataSource().remove(0);
                break;
            case R.id.btn_del_last:
                if (mSelectAdapter.getDataSource().isEmpty()) return;
                mSelectAdapter.getDataSource().remove(mSelectAdapter.getDataSource().size() - 1);
                break;
            case R.id.btn_open5:
                mSelectAdapter.setOpenSelect(true, 5);
                break;
            case R.id.btn_set8:
                break;
            case R.id.btn_close:
                mSelectAdapter.setOpenSelect(false);
                break;
        }
        mSelectAdapter.notifyDataSetChanged();
    }
}
