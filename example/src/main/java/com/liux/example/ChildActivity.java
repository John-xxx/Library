package com.liux.example;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.widget.RadioGroup;

import com.liux.base.BaseActivity;
import com.liux.base.titlebar.TitleBar;
import com.liux.base.titlebar.WhiteTitleBar;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Liux on 2017/8/13.
 */

public class ChildActivity extends BaseActivity {

    @BindView(R.id.vp_content)
    ViewPager vpContent;
    @BindView(R.id.rg_selector)
    RadioGroup rgSelector;

    Fragment[] mFragments = new Fragment[] {
            new ChildOneFragment(),
            new ChildTwoFragment()
    };

    @Override
    protected TitleBar onInitTitleBar() {
        return new WhiteTitleBar(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState, Intent intent) {
        setContentView(R.layout.activity_child);
    }

    @Override
    protected void onInitData(@Nullable Bundle savedInstanceState, Intent intent) {

    }

    @Override
    protected void onInitView(@Nullable Bundle savedInstanceState) {


        ButterKnife.bind(this);

        vpContent.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(), mFragments));
        vpContent.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        rgSelector.check(R.id.rb_one);
                        break;
                    case 1:
                        rgSelector.check(R.id.rb_two);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        rgSelector.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rb_one:
                        vpContent.setCurrentItem(0);
                        break;
                    case R.id.rb_two:
                        vpContent.setCurrentItem(1);
                        break;
                }
            }
        });
    }

    @Override
    protected void onLazyLoad() {

    }

    @Override
    protected void onRestoreData(Map<String, Object> data) {

    }

    @Override
    protected void onSaveData(Map<String, Object> data) {

    }
}
