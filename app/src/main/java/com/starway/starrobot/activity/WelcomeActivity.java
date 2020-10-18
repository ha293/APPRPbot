package com.starway.starrobot.activity;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.VideoView;


import android.os.Message;
import android.text.format.DateFormat;
import com.starway.starrobot.R;
import com.starway.starrobot.ability.HardwareServer;
import com.starway.starrobot.ability.WakeUpActionHelper;
import com.starway.starrobot.activity.backstage.BackstageActivity;
import com.starway.starrobot.activity.controller.PasswordController;
import com.starway.starrobot.mscability.speech.SpeechHelper;
import com.starway.starrobot.service.NetworkService;
import com.starway.starrobot.service.RobotService;
import com.starway.starrobot.utils.Common;
import com.starway.starrobot.utils.ResUtil;
import com.starway.starrobot.view.ScrollingView;

import java.io.File;
import java.util.Calendar;
import java.util.TimeZone;

public class WelcomeActivity extends BaseActivity {


    private PasswordController passwordController;
    private VideoView videoView;
    private MediaPlayer videoPlayer;

    private WakeUpActionHelper mWakeupActionHelper;
    private View mic;
    private View bar;

    private float volume = 0.03f; //视频音量
    private boolean videoStatusOK = false;
    private Handler handler;
    private TextView mTime;
    private static final int msgKey1 =1 ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        mTime = findViewById(R.id.mytime);
        new TimeThread().start();
       // Log.i("life", this + " onCreate");  打印错误的代码


        initView();
//        initAbility();

        passwordController = new PasswordController(this);

        startService(new Intent(WelcomeActivity.this, NetworkService.class));
        new Handler().postDelayed(new Runnable() { //5秒后再次启动Robot服务,防止机器人初始化失败
            @Override
            public void run() {
                startService(new Intent(WelcomeActivity.this, RobotService.class));
            }
        }, 5000);
    }
/*
*
* */
  public class TimeThread extends Thread {
    @Override
    public void run () {
      do {
        try {
          Thread.sleep(1000);
          Message msg = new Message();
          msg.what = msgKey1;
          mHandler.sendMessage(msg);
        }
        catch (InterruptedException e) {
          e.printStackTrace();
        }
      } while(true);
    }
  }

  private Handler mHandler = new Handler() {
    @Override
    public void handleMessage (Message msg) {
      super.handleMessage(msg);
      switch (msg.what) {
        case msgKey1:
          mTime.setText(getTime());
          break;
        default:
          break;
      }
    }
  };
  //获得当前年月日时分秒星期
  public String getTime(){
    final Calendar c = Calendar.getInstance();
    c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
    String mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
    String mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
    String mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
    String mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
    String mHour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));//时
    String mMinute = String.valueOf(c.get(Calendar.MINUTE));//分
    String mSecond = String.valueOf(c.get(Calendar.SECOND));//秒

    if("1".equals(mWay)){
      mWay ="天";
    }else if("2".equals(mWay)){
      mWay ="一";
    }else if("3".equals(mWay)){
      mWay ="二";
    }else if("4".equals(mWay)){
      mWay ="三";
    }else if("5".equals(mWay)){
      mWay ="四";
    }else if("6".equals(mWay)){
      mWay ="五";
    }else if("7".equals(mWay)){
      mWay ="六";
    }
    return mYear + "年" + mMonth + "月" + mDay+"日"+"  "+"星期"+mWay+"  "+mHour+":"+mMinute+":"+mSecond;
  }

/*
*
*
*
* 上面添加了时间功能
*
*
*
*
* */
    private void initView() {
        handler = new Handler();
        bar = findViewById(R.id.bar);
        mic = findViewById(R.id.mic);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mic.startAnimation(AnimationUtils.loadAnimation(WelcomeActivity.this, R.anim.btn_mic));
            }
        }, 1000);

        videoView = findViewById(R.id.videoView);
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                videoView.stopPlayback();
                videoView.setVisibility(View.GONE);
                videoStatusOK = false;
                System.out.println("播放失败");
                initVideo();
                return true;
            }
        });
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                try {
                    System.out.println("视频播放器建加载完成");
                    videoView.setVisibility(View.VISIBLE);
                    videoPlayer = mediaPlayer;
                    videoPlayer.setLooping(true);
                    videoPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    videoPlayer.start();
                    videoPlayer.setVolume(volume, volume);
                    videoStatusOK = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("视频播放器建加载出错");
                }
            }
        });

        findViewById(R.id.touchPanel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goHomeActivity();
            }
        });
        volume = Common.getPreferences(this).getFloat("volume", 0.01f);

        final ScrollingView videoControl = findViewById(R.id.video_control);
        videoControl.setScrollBlockSize(20).setOnScrollingListener(new ScrollingView.onScrollingListener() {
            @Override
            public void onTouchEvent(int t) {
                if (videoPlayer != null) {
                    try {
                        int pos = videoPlayer.getCurrentPosition();
                        switch (t) {
                            case ScrollingView.ACTION_SCROLL_BOTTOM:
                                volume -= 0.03f;
                                if (volume <= 0) {
                                    volume = 0;
                                }
                                videoPlayer.setVolume(volume, volume);
                                saveVolume();
                                break;
                            case ScrollingView.ACTION_SCROLL_TOP:
                                volume += 0.03f;
                                if (volume > 0.8f) {
                                    volume = 0.8f;
                                }
                                videoPlayer.setVolume(volume, volume);
                                saveVolume();
                                break;
                            case ScrollingView.ACTION_SCROLL_LEFT:
                                pos -= 5000;
                                videoPlayer.seekTo(pos);
                                break;
                            case ScrollingView.ACTION_SCROLL_RIGHT:
                                pos += 5000;
                                videoPlayer.seekTo(pos);
                                break;
                            case ScrollingView.ACTION_DOUBLE_CLICK:
                                if (videoPlayer.isPlaying()) {
                                    videoPlayer.pause();
                                } else {
                                    videoPlayer.start();
                                }
                                break;
                            case ScrollingView.ACTION_CLICK:
                                goHomeActivity();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    if (t == ScrollingView.ACTION_CLICK) {
                        goHomeActivity();
                    }
                }
            }
        });
    }

    private void initVideo() { //初始化视频播放器
        final File videoFile = new File(NetworkService.FTP_HOME_PATH + "welcome.mp4");
        System.out.println("初始化视频" + videoStatusOK + " " + videoFile.getPath());
        if (!videoStatusOK) { //如果视频状态正常
            videoView.stopPlayback();
            if (videoFile.exists()) {
                videoStatusOK = true;
                videoView.setVideoPath(videoFile.getPath());
                videoView.setVisibility(View.VISIBLE);
            } else {
                videoStatusOK = false;
                videoView.setVisibility(View.GONE);
            }
        }
    }

//    private void goHomeActivity() { //太卡了不用这种方案了
//        HardwareServer.getInstance().stopMove();//取消机器人移动
//        startActivity(new Intent(WelcomeActivity.this, HomeActivity.class),
//                ActivityOptions.makeSceneTransitionAnimation(WelcomeActivity.this, Pair.create(mic, "mic"), Pair.create(bar, "bar")).toBundle());
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                videoView.stopPlayback();
//                finish();
//            }
//        }, 800);
//    }

    private void goHomeActivity() {
        HardwareServer.getInstance().stopMove();//取消机器人移动
        startActivity(new Intent(WelcomeActivity.this, HomeActivity.class));
        finish();
    }


    public void goBackstageActivity() {
        videoView.stopPlayback();
        SpeechHelper.getInstance().speak("指令正确");
        startActivity(new Intent(this, BackstageActivity.class));
//        startActivity(new Intent(this, BackstageActivity.class),
//                ActivityOptions.makeSceneTransitionAnimation(this, mic, "bar").toBundle());
    }

    private void saveVolume() {
        Common.getPreferences(this).edit().putFloat("volume", volume).apply();
    }

    @Override
    protected void onResume() {
        Log.i("life", this + " onResume");
        super.onResume();
        passwordController.refreshPassword();
        initVideo();
        initWakeUp();
    }

    @Override
    protected void onPause() {
        Log.i("life", this + " onPause");
        super.onPause();
        if (null != mWakeupActionHelper) {
            mWakeupActionHelper.unregisterWakeUpActionReceiver();
            mWakeupActionHelper = null;
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
            SpeechHelper.getInstance().speak(ResUtil.getRandomWakeUpAnswer());
            HardwareServer.getInstance().doFaceLightToWakeUp();
            HardwareServer.getInstance().doEmojiWakeUp();
            goHomeActivity();
            return true;
        }

        @Override
        public void onRotateEnd(int i) {
            Log.e("wakeup rotate", "旋转 end");
        }
    };

}
