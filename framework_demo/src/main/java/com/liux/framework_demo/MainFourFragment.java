package com.liux.framework_demo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.liux.framework.base.BaseFragment;
import com.liux.framework.list.ItemDecoration;
import com.liux.framework.list.MultipleAdapter;
import com.liux.framework.list.SuperHolder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Liux on 2017/8/11.
 */

public class MainFourFragment extends BaseFragment {

    @BindView(R.id.rv_list)
    RecyclerView rvList;
    Unbinder unbinder;

    private MultipleAdapter<Object> mMultipleAdapter;

    @Override
    protected void onInitData(Bundle savedInstanceState) {

    }

    @Override
    protected View onInitView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_four, container, false);
        unbinder = ButterKnife.bind(this, view);

        mMultipleAdapter = new MultipleAdapter<Object>()
                .addRule(new MultipleAdapter.Rule<String>(android.R.layout.simple_list_item_1) {
                    @Override
                    public boolean doBindData(Object object) {
                        return object instanceof String;
                    }

                    @Override
                    public void onDataBind(SuperHolder holder, String s, MultipleAdapter.State state, final int position) {
                        holder.setText(android.R.id.text1, String.format("String is %s (%d)", s, state.state));
                        holder.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mMultipleAdapter.toggle(position);
                            }
                        });
                    }
                })
                .addRule(new MultipleAdapter.Rule<Integer>(android.R.layout.simple_list_item_2) {
                    @Override
                    public boolean doBindData(Object object) {
                        return object instanceof Integer;
                    }

                    @Override
                    public void onDataBind(SuperHolder holder, Integer integer, MultipleAdapter.State state, final int position) {
                        holder.setText(android.R.id.text1, String.format("Integer is %d (%d)", integer, state.state));
                        holder.setText(android.R.id.text2, String.format("I'm a descriptive text", integer));
                        holder.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mMultipleAdapter.toggle(position);
                            }
                        });
                    }
                });
        mMultipleAdapter.setOnSelectListener(new MultipleAdapter.OnSelectListener<Object>() {
                    @Override
                    public void onSelectChange(Object o, int position, boolean isSelect) {
                        Toast.makeText(getActivity(), o + " isSelect:" + isSelect, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSelectComplete() {
                        List<Object> list = mMultipleAdapter.getStateAll(MultipleAdapter.State.STATE_SELECTED);
                        Toast.makeText(getActivity(), list.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
        rvList.addItemDecoration(new ItemDecoration() {
            @Override
            public Decoration getItemOffsets(int position) {
                ColorDecoration decoration = new ColorDecoration();
                decoration.color = Color.parseColor("#FF00FF");
                decoration.bottom = 15;
                return decoration;
            }
        });
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvList.setAdapter(mMultipleAdapter);
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

    @OnClick({R.id.btn_add_string, R.id.btn_add_integer, R.id.btn_del_first, R.id.btn_open5, R.id.btn_set8, R.id.btn_close})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_add_string:
                mMultipleAdapter.getDataSource().add(String.valueOf(System.currentTimeMillis() / 10000L));
                break;
            case R.id.btn_add_integer:
                mMultipleAdapter.getDataSource().add(Integer.valueOf((int) (System.currentTimeMillis() % 1502000000000L)));
                break;
            case R.id.btn_del_first:
                if (mMultipleAdapter.getDataSource().isEmpty()) return;
                mMultipleAdapter.getDataSource().remove(0);
                break;
            case R.id.btn_open5:
                mMultipleAdapter.setOpenSelect(true, 5);
                break;
            case R.id.btn_set8:
                break;
            case R.id.btn_close:
                mMultipleAdapter.setOpenSelect(false);
                break;
        }
        mMultipleAdapter.notifyDataSetChanged();
    }
}
