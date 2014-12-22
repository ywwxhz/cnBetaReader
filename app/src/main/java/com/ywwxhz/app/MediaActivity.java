package com.ywwxhz.app;

import android.app.Activity;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.ywwxhz.cnbetareader.R;

/**
 * com.ywwxhz.app
 * Created by 远望の无限 on 2014/12/20 15:50.
 */
public class MediaActivity extends Activity {

    private VideoView mVideoView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if(extras.containsKey("videoUrl")){
            setContentView(R.layout.activity_media);
            mVideoView = (VideoView) findViewById(R.id.video);
            mVideoView.setVideoURI(Uri.parse(extras.getString("videoUrl")));
            mVideoView.setMediaController(new MediaController(this));
            mVideoView.start();
            mVideoView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction()==MotionEvent.ACTION_UP){
                        if(getActionBar().isShowing()){
                            getActionBar().hide();
                        }else{
                            getActionBar().show();
                        }
                    }
                    return false;
                }
            });
            mVideoView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getActionBar().hide();
                }
            },200);
        }else{
            Toast.makeText(this,"url 非法",Toast.LENGTH_SHORT).show();
            this.finish();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
