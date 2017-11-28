package com.liux.example.player;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liux.example.R;
import com.liux.player.Media;
import com.liux.player.PlayerGroup;
import com.liux.player.PlayerView;

import java.util.ArrayList;
import java.util.List;

public class MoreMediaActiviy extends AppCompatActivity {
    private List<Media> mSourceData;
    private RecyclerView mRecyclerView;

    private PlayerGroup mPlayerGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_more);

        mSourceData = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            mSourceData.add(Media.create(PlayerActivity.SOURCE[i % 4]));
        }

        mPlayerGroup = new PlayerGroup();

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player_list, parent, false);
                return new RecyclerView.ViewHolder(itemView){};
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                Media media = mSourceData.get(position);
                PlayerView playerView = (PlayerView) holder.itemView;
                playerView.setPlayerGroup(mPlayerGroup);
                playerView.setMedia(media);
            }

            @Override
            public int getItemCount() {
                return mSourceData.size();
            }
        });
    }
}
