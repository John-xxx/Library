package com.liux.example;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.liux.example.banner.BannerActivity;
import com.liux.example.base.BaseActivity;
import com.liux.example.boxing.BoxingActivity;
import com.liux.example.downloader.DownloaderActivity;
import com.liux.example.glide.GlideActivity;
import com.liux.example.http.HTTPActivity;
import com.liux.example.lbs.LBSActivity;
import com.liux.example.list.ListActivity;
import com.liux.example.other.OtherActivity;
import com.liux.example.pay.PayActivity;
import com.liux.example.permission.PermissionActivity;
import com.liux.example.player.PlayerActivity;
import com.liux.example.util.UtilActivity;
import com.liux.example.view.ViewActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private List<Map<String, Object>> mDataSource;
    private SimpleAdapter mSimpleAdapter;
    private ListView mListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mDataSource = new ArrayList<>();
        mSimpleAdapter = new SimpleAdapter(
                this,
                mDataSource,
                android.R.layout.simple_list_item_2,
                new String[]{"title", "describe"},
                new int[]{android.R.id.text1, android.R.id.text2}
        );
        mListView = (ListView) findViewById(R.id.lv_list);
        mListView.setAdapter(mSimpleAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Class activity = (Class) mDataSource.get(position).get("activity");
                startActivity(new Intent(MainActivity.this, activity));
            }
        });

        add("Banner", "一个支持Adapter的Banner封装", BannerActivity.class);
        add("Base", "对于Activity和Fragment的封装", BaseActivity.class);
        add("Boxing", "基于Boxing封装的图片选择/预览库", BoxingActivity.class);
        add("Downloader", "全局/多线程/断点续传下载器实现", DownloaderActivity.class);
        add("Glide", "基于Glide4实现自定义加载过程", GlideActivity.class);
        add("HTTP", "基于Retorfit/OkHttp的封装", HTTPActivity.class);
        add("LBS", "封装百度/高德的LBS数据层", LBSActivity.class);
        add("List", "基于RecycleView的封装", ListActivity.class);
        add("Other", "一些工具类集合", OtherActivity.class);
        add("Pay", "封装支付宝/微信/银联支付过程", PayActivity.class);
        add("Permission", "运行时权限申请过程封装", PermissionActivity.class);
        add("Player", "基于ijkplayer封装的播放器", PlayerActivity.class);
        add("Util", "一些工具方法集合", UtilActivity.class);
        add("View", "一些自定义View集合", ViewActivity.class);

        mSimpleAdapter.notifyDataSetChanged();
    }

    private void add(String title, String describe, Class<? extends Activity> activity) {
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("describe", describe);
        map.put("activity", activity);
        mDataSource.add(map);
    }
}
