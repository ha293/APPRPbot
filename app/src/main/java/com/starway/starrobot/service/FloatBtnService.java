package com.starway.starrobot.service;

import android.app.Instrumentation;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.starway.starrobot.R;
import com.starway.starrobot.utils.Common;

/**
 * 悬浮按钮服务
 * Created by iBelieve on 2018/5/4.
 */
public class FloatBtnService extends Service {

    private WindowManager windowManager;
    private View view;

    @Override
    public void onCreate() {
        super.onCreate();
        view = LayoutInflater.from(this).inflate(R.layout.layout_floatbtn, null);
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);


        WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.RGBA_8888
        );
        wmParams.width = wmParams.height = 134;
        wmParams.gravity = Gravity.TOP | Gravity.LEFT;
        windowManager.addView(view, wmParams);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.back();
            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (view != null) {
            windowManager.removeView(view);
        }
        super.onDestroy();
    }
}
