package com.starway.starrobot.view;

import android.app.Activity;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import com.starway.starrobot.R;

public class Videoview extends Activity {

    VideoView videoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videoview);
        initView();
    }

    public void initView(){
        videoView= (VideoView) findViewById(R.id.videoView);
        playVideo();
    }
    public void playVideo(){
        String uri = "android.resource://" + getPackageName() + "/" + R.raw.test;
        videoView.setVideoURI(Uri.parse(uri));
        MediaController mc = new MediaController(this);
        mc.setAnchorView(videoView);//设置控制器 控制的是那一个videoview
        videoView.setMediaController(mc); //设置videoview的控制器为mc
        videoView.start();
    }
}
