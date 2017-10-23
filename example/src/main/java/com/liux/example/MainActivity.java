package com.liux.example;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.widget.RadioGroup;

import com.liux.base.BaseActivity;
import com.liux.base.titlebar.DefaultTitleBar;
import com.liux.permission.OnPermissionListener;
import com.liux.permission.PermissionTool;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    @BindView(R.id.vp_content)
    ViewPager vpContent;
    @BindView(R.id.rg_selector)
    RadioGroup rgSelector;

    Fragment[] mFragments = new Fragment[] {
            new MainOneFragment(),
            new MainTwoFragment(),
            new MainThreeFragment(),
            new MainFourFragment()
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState, Intent intent) {
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onInitData(@Nullable Bundle savedInstanceState, Intent intent) {

    }

    @Override
    protected void onInitView(@Nullable Bundle savedInstanceState) {
        DefaultTitleBar titleBar = (DefaultTitleBar) getTitleBar();
        titleBar
                .hasBack(false)
                .hasMore(true)
                .setOnTitleBarListener(new DefaultTitleBar.OnTitleBarListener() {
                    @Override
                    public boolean onBack() {
                        return false;
                    }

                    @Override
                    public boolean onMore() {
                        startActivity(new Intent(MainActivity.this, ChildActivity.class));
                        return true;
                    }
                });
        titleBar.getMoreText().setText("Child");

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
                    case 2:
                        rgSelector.check(R.id.rb_three);
                        break;
                    case 3:
                        rgSelector.check(R.id.rb_four);
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
                    case R.id.rb_three:
                        vpContent.setCurrentItem(2);
                        break;
                    case R.id.rb_four:
                        vpContent.setCurrentItem(3);
                        break;
                }
            }
        });
    }

    @Override
    protected void onLazyLoad() {
        PermissionTool.with(this)
                .permissions(Manifest.permission.CALL_PHONE)
                .listener(new OnPermissionListener() {
                    @Override
                    public void onPermission(List<String> allow, List<String> reject, List<String> prohibit) {

                    }
                })
                .request();
    }

    @Override
    protected void onRestoreData(Map<String, Object> data) {
        long time = (long) data.get("time");
    }

    @Override
    protected void onSaveData(Map<String, Object> data) {
        data.put("time", System.currentTimeMillis());
    }
}
