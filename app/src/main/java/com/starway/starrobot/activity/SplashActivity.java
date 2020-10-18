package com.starway.starrobot.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;

import com.starway.starrobot.R;
import com.starway.starrobot.service.NetworkService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *  小涂启动页面
 */
public class SplashActivity extends BaseActivity {

    private Handler handler = new Handler();
    private View logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        logo = findViewById(R.id.logo);
        initAbility(); //初始化一次能力
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, WelcomeActivity.class),
                        ActivityOptions.makeSceneTransitionAnimation(SplashActivity.this, logo, "logo").toBundle());
//                finish();
            }
        }, 500);

    }
}
