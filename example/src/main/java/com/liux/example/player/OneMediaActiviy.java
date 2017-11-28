package com.liux.example.player;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.liux.example.R;
import com.liux.player.Media;
import com.liux.player.TexturePlayerView;

public class OneMediaActiviy extends AppCompatActivity {
    private TexturePlayerView mTexturePlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_one);

        getFragmentManager()
                .beginTransaction()
                .add(R.id.fl_root, new PlayerFragment())
                .commit();

        mTexturePlayerView = (TexturePlayerView) findViewById(R.id.pv_tpv);
        mTexturePlayerView.setMedia(Media.create(PlayerActivity.SOURCE[1]));
    }

    @Override
    public void onBackPressed() {
        if (mTexturePlayerView.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }
}
