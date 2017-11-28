package com.liux.example.player;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liux.example.R;
import com.liux.player.Media;
import com.liux.player.SurfacePlayerView;

/**
 * Created by Liux on 2017/11/3.
 */

public class PlayerFragment extends Fragment {
    private SurfacePlayerView mSurfacePlayerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_player, null);

        mSurfacePlayerView = (SurfacePlayerView) root.findViewById(R.id.pv_spv);
        mSurfacePlayerView.setMedia(Media.create(PlayerActivity.SOURCE[3]));

        return root;
    }
}
