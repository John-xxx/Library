package com.liux.example.downloader;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.liux.downloader.Downloader;
import com.liux.downloader.Task;
import com.liux.downloader.listener.SimpleTaskCallBack;
import com.liux.downloader.listener.TaskCallBack;
import com.liux.example.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Liux on 2017/12/8.
 */

public class OneTaskActivity extends AppCompatActivity {
    @BindView(R.id.tv_log)
    TextView tvLog;
    @BindView(R.id.pb_progress)
    ProgressBar pbProgress;
    @BindView(R.id.btn_option)
    Button btnOption;
    @BindView(R.id.btn_del)
    Button btnDel;

    private TaskCallBack mTaskCallBack = new SimpleTaskCallBack();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloader_one);
        ButterKnife.bind(this);

        Downloader.instance().addCallBack(mTaskCallBack);

        Task task = Downloader.instance().get(Constant.LIULISHUO_APK_URL);

        switch (task.getStatus()) {
            case Task.STATUS_UNKOWN:
                btnOption.setText("开始");
                break;
            case Task.STATUS_AWAIT:
                btnOption.setText("暂停");
                break;
            case Task.STATUS_DOWNLOADING:
                btnOption.setText("暂停");
                break;
            case Task.STATUS_FINISH:
                btnOption.setText("打开");
                break;
            case Task.STATUS_PAUSE:
                btnOption.setText("继续");
                break;
            case Task.STATUS_ERROR:
                btnOption.setText("重试");
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Downloader.instance().removeCallBack(mTaskCallBack);
    }

    @OnClick({R.id.btn_option, R.id.btn_del})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_option:
                break;
            case R.id.btn_del:
                break;
        }
    }
}
