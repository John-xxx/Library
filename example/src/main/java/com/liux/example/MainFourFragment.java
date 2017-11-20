package com.liux.example;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liux.base.BaseFragment;
import com.liux.list.adapter.MultipleAdapter;
import com.liux.list.adapter.Rule;
import com.liux.list.adapter.State;
import com.liux.list.decoration.AbsItemDecoration;
import com.liux.list.holder.SuperHolder;
import com.liux.list.listener.OnSelectListener;
import com.liux.view.SingleToast;

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
    protected void onRestoreData(Bundle data) {

    }

    @Override
    protected View onInitView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_four, container, false);
        unbinder = ButterKnife.bind(this, view);

        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvList.addItemDecoration(new AbsItemDecoration() {
            @Override
            public Decoration getItemOffsets(int position) {
                // 划重点
                if (mMultipleAdapter.isHeaderPosition(position) ||
                        mMultipleAdapter.isFooterPosition(position)) {
                    return null;
                }
                ColorDecoration decoration = new ColorDecoration();
                decoration.color = Color.parseColor("#FF00FF");
                decoration.bottom = 15;
                return decoration;
            }
        });
        mMultipleAdapter = new MultipleAdapter<Object>()
                .setHeader(LayoutInflater.from(getContext()).inflate(R.layout.layout_header, rvList, false))
                .setFooter(LayoutInflater.from(getContext()).inflate(R.layout.layout_footer, rvList, false))
                .addRule(new Rule<String>(android.R.layout.simple_list_item_1) {
                    @Override
                    public boolean doBindData(Object object) {
                        return object instanceof String;
                    }

                    @Override
                    public void onDataBind(SuperHolder holder, String s, State state, final int position) {
                        holder.setText(android.R.id.text1, String.format("String is %s (%d)", s, state.state));
                        holder.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mMultipleAdapter.toggle(position);
                            }
                        });
                    }
                })
                .addRule(new Rule<Integer>(android.R.layout.simple_list_item_2) {
                    @Override
                    public boolean doBindData(Object object) {
                        return object instanceof Integer;
                    }

                    @Override
                    public void onDataBind(SuperHolder holder, Integer integer, State state, final int position) {
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
        mMultipleAdapter.setOnSelectListener(new OnSelectListener<Object>() {
            @Override
            public boolean onSelectChange(Object o, int position, boolean isSelect) {
                SingleToast.makeText(getActivity(), o + " request select:" + isSelect, SingleToast.LENGTH_SHORT).show();
                return position % 3 != 0;
            }

            @Override
            public void onSelectFailure() {
                SingleToast.makeText(getActivity(), "select failure", SingleToast.LENGTH_SHORT).show();
            }

            @Override
            public void onSelectComplete() {
                List<Object> list = mMultipleAdapter.getStateAll(State.STATE_SELECTED);
                SingleToast.makeText(getActivity(), list.toString(), SingleToast.LENGTH_SHORT).show();
            }
        });
        rvList.setAdapter(mMultipleAdapter);

        return view;
    }

    @Override
    protected void onLazyLoad() {

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
