package com.liux.player.view;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import com.liux.player.R;

/**
 * Created by Liux on 2017/11/26.
 */

public class SmallScreenControlView extends AbstractControlView {

    private AbstractPlayerView mAbstractPlayerView;

    private View mRoot;

    public SmallScreenControlView(AbstractPlayerView view) {
        super(view.getContext());

        mAbstractPlayerView = view;
        mRoot = LayoutInflater.from(getContext()).inflate(R.layout.view_player_control_small, this, true);

        mRoot.setOnTouchListener(new OnTouchListener() {
            int screenWidth;
            int screenHeight;
            int lastX;
            int lastY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action=event.getAction();
                switch(action){
                    case MotionEvent.ACTION_DOWN:
                        DisplayMetrics dm = getResources().getDisplayMetrics();
                        screenWidth = dm.widthPixels;
                        screenHeight = dm.heightPixels;
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int dx =(int)event.getRawX() - lastX;
                        int dy =(int)event.getRawY() - lastY;

                        int left = v.getLeft() + dx;
                        int top = v.getTop() + dy;
                        int right = v.getRight() + dx;
                        int bottom = v.getBottom() + dy;
                        if(left < 0){
                            left = 0;
                            right = left + v.getWidth();
                        }
                        if(right > screenWidth){
                            right = screenWidth;
                            left = right - v.getWidth();
                        }
                        if(top < 0){
                            top = 0;
                            bottom = top + v.getHeight();
                        }
                        if(bottom > screenHeight){
                            bottom = screenHeight;
                            top = bottom - v.getHeight();
                        }
                        mAbstractPlayerView.layout(left, top, right, bottom);
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        mRoot.findViewById(R.id.player_iv_close).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mAbstractPlayerView.closeSmallScreen();
            }
        });
    }

    @Override
    public void loadMedia() {

    }

    @Override
    public void prepare() {

    }

    @Override
    public void prepared() {

    }

    @Override
    public void error(int error) {

    }

    @Override
    public void playStart() {

    }

    @Override
    public void playComplete() {

    }

    @Override
    public void changeFocus() {

    }

    @Override
    public void changeScreen() {

    }

    @Override
    public void callStart() {

    }

    @Override
    public void callPause() {

    }

    @Override
    public void callStop() {

    }

    @Override
    public void callReset() {

    }

    @Override
    public void callRelease() {

    }

    @Override
    public void bufferStart() {

    }

    @Override
    public void bufferEnd() {

    }

    @Override
    public void bufferUpdate(int percent) {

    }

    @Override
    public void seekStart() {

    }

    @Override
    public void seekEnd() {

    }
}
