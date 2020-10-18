package com.starway.starrobot.activity.controller;


import android.os.Handler;
import android.util.Log;

/**
 * 倒计时控制器（两个阶段）
 * Created by iBelieve on 2018/5/2.
 */

public class CountdownController {

    private static CountdownController controller;
    private Handler handler;
    private OnTimeoutListener listener;
    private int timeA = 0;
    private int timeB = 0;
    private int count = 0;

    private CountdownController() {
        handler = new Handler();
    }

    public static CountdownController getInstance() {
        if (controller == null) {
            controller = new CountdownController();
        }
        return controller;
    }

    /**
     * 开始计时
     */
    public void start() {
        start(listener, timeA, timeB);
    }

    /**
     * 开始计时(指定回调)
     * @param listener
     * @param timeA
     */
    public void start(OnTimeoutListener listener, int timeA,int timeB) {
        stop();
        if (listener != null) {
            this.listener = listener;
            this.timeA = timeA;
            this.timeB = timeB;
            handler.postDelayed(runnable, this.timeA);
            Log.i("CountdownController", "开始计时：" + this.timeA);
        }
    }

    /**
     * 停止计时
     */
    public void stop() {
        Log.i("CountdownController", "停止计时");
        count = 0;
        handler.removeCallbacks(runnable);
    }

    /**
     * 打断计时
     */
    public void interrupt() {
        Log.i("CountdownController", "计时被打断");
        start();
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (count == 0) {
                count++;
                handler.postDelayed(runnable, timeB);
                listener.onTimeout(0);
            } else {
                listener.onTimeout(1);
                count = 0;
            }
        }
    };

    public CountdownController setListener(OnTimeoutListener listener) {
        this.listener = listener;
        return this;
    }

    public interface OnTimeoutListener {
        void onTimeout(int count);
    }
}
