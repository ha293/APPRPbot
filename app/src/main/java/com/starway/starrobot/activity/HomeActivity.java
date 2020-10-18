package com.starway.starrobot.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.google.gson.Gson;
import com.starway.starrobot.R;
import com.starway.starrobot.ability.HardwareServer;
import com.starway.starrobot.ability.WakeUpActionHelper;
import com.starway.starrobot.ability.speech.AIUIAdapter;
import com.starway.starrobot.ability.speech.AIUIHelper;
import com.starway.starrobot.activity.controller.BusinessController;
import com.starway.starrobot.activity.controller.CountdownController;
import com.starway.starrobot.mscability.speech.SpeechHelper;
import com.starway.starrobot.utils.ResUtil;
import com.starway.starrobot.utils.SemanticBean;

import org.json.JSONObject;

import java.util.Date;
import java.util.concurrent.Executors;

public class HomeActivity extends BaseActivity implements AIUIHelper.NLPListener {

    private SpeechHelper speechHelper;
    private HardwareServer hardwareServer;

    private WakeUpActionHelper mWakeupActionHelper;
    private BusinessController businessController;
    private TextView speakText;
    private View mic;
    private View fragmentContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mic = findViewById(R.id.mic);
        mic.startAnimation(AnimationUtils.loadAnimation(this, R.anim.btn_mic));
        mic.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hardwareServer.doEmojiAngle();
                speechHelper.speak("手脏脏，洗完手手才能摸我哟！"); //TODO: ResUtil
                return false;
            }
        });

        speechHelper = SpeechHelper.getInstance();
        speechHelper.setVoicer("jiajia");
        hardwareServer = HardwareServer.getInstance();
        speakText = findViewById(R.id.speakText);

        businessController = new BusinessController(this);
//        initAbility(); //初始化机器人能力

        /*****启动动画*****/
        fragmentContent = findViewById(R.id.fragment_content);
        fragmentContent.startAnimation(AnimationUtils.loadAnimation(this, R.anim.activity_fade_in));


        Log.i("test", "onResume：" + this.toString() + " " + new Date());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("test", "onCreate：" + this.toString() + " " + new Date());
        initAIUI();
        initWakeUp();
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.i("test", "onPause：" + this.toString());
        stopAIUI();
        if (null != mWakeupActionHelper) {
            mWakeupActionHelper.unregisterWakeUpActionReceiver();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("test", "onDestroy：" + this.toString());
        if (businessController != null) {
            businessController.stop();
        }
    }

    public void initAIUI() { //初始化AIUI服务
        Log.i("test", "初始化AIUI服务");
        try {
            AIUIHelper.getInstance().addNLPListener(this);
            Executors.newSingleThreadExecutor().submit(new Runnable() {
                @Override
                public void run() {
                    AIUIHelper.getInstance().start();
                }
            });
            AIUIHelper.getInstance().setInputMode(AIUIAdapter.MODE_VOICE);
            AIUIHelper.getInstance().setSleepEnable(false); //禁止AIUI休眠
            Log.i("test", "AIUI初始化成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopAIUI() {
        System.out.println("临时暂停服务");
        try {
            AIUIHelper.getInstance().stop();
            AIUIHelper.getInstance().removeNLPListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化唤醒功能
     */
    private void initWakeUp() {
        mWakeupActionHelper = new WakeUpActionHelper(this);
        mWakeupActionHelper.registerWakeUpActionReceiver();
        mWakeupActionHelper.setOnWakeUpActionListener(wakeUpActionListener);
    }

    /**
     * 唤醒监听器
     */
    private WakeUpActionHelper.OnWakeUpActionListener wakeUpActionListener = new WakeUpActionHelper.OnWakeUpActionListener() {
        @Override
        public boolean getAngle(int i) {
            CountdownController.getInstance().interrupt();
            hardwareServer.doFaceLightToWakeUp();
            hardwareServer.doEmojiWakeUp();
            speechHelper.speak(ResUtil.getRandomWakeUpAnswer());
            return true;
        }

        @Override
        public void onRotateEnd(int i) {
            Log.e("wakeup rotate", "旋转 end");
        }
    };


    /**
     * AIUI的语义监听回调
     */
    @Override
    public void onAiuiResponse(JSONObject json) {

        try {
            final SemanticBean semanticBean = new Gson().fromJson(json.optString("intent"), SemanticBean.class);

            Log.d("semanticResult", "service:" + semanticBean.getService() + ",text:" + semanticBean.getInputText());

            if (!semanticBean.getInputText().isEmpty()) {
                speakText.setText(semanticBean.getInputText());
                if (businessController != null) {
                    businessController.parser(semanticBean);
                }


            }

            Log.e("semantic answer", semanticBean.getAnswer());





        } catch (Exception e) {
            e.printStackTrace();
        }




    }

    public void backToWelcomeActivity() {
        startActivity(new Intent(this, WelcomeActivity.class));
        finish();
    }

    @Override
    public void onAiuiWakeUp() {
    }

    @Override
    public void onAiuiSleep() {
    }

    @Override
    public void onError(int code) {
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        CountdownController.getInstance().interrupt(); //有触摸事件时打断计时
        return super.dispatchTouchEvent(ev);
    }
}
