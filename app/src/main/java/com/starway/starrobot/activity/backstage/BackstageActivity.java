package com.starway.starrobot.activity.backstage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.starway.starrobot.R;
import com.starway.starrobot.ability.HardwareServer;
import com.starway.starrobot.activity.BaseActivity;
import com.starway.starrobot.service.NetworkService;
import com.starway.starrobot.service.FloatBtnService;
import com.starway.starrobot.utils.Common;
import com.starway.starrobot.utils.RobotInitState;

public class BackstageActivity extends BaseActivity implements View.OnClickListener {
    private View openWiFi;
/*    private View siteManager;*/
   /* private View btnStartCali;*/
    private View openHardware;
    private View openSetting;
  /*  private View modPas;*/
    private TextView ftpInfo;
    private NetworkService.FtpInfo ftpGlobalInfo;
    private Intent floatBtnIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backstage_main);

        setTitle("后台管理");

        openWiFi = findViewById(R.id.openWiFi);
        /*siteManager = findViewById(R.id.siteManager);*/
        /*btnStartCali = findViewById(R.id.start_calibration);*/
        openHardware = findViewById(R.id.open_hardware);
        openSetting = findViewById(R.id.open_setting);
       /* modPas = findViewById(R.id.edit_pas);*/
        ftpInfo = findViewById(R.id.ftp_info);

       /* modPas.setOnClickListener(this);*/
        openSetting.setOnClickListener(this);
        openWiFi.setOnClickListener(this);
        openHardware.setOnClickListener(this);
       /* siteManager.setOnClickListener(this);*/
       /* btnStartCali.setOnClickListener(this);*/

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NetworkService.ACTION_FTP_START);
        intentFilter.addAction(NetworkService.ACTION_FTP_STOP);
        registerReceiver(broadcastReceiver, intentFilter);

        floatBtnIntent = new Intent(BackstageActivity.this, FloatBtnService.class);

        setFuncBtnIcon(ICON_POWER);
        showFuncButton(true);
        setOnFuncBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(BackstageActivity.this)
                        .setMessage("是否重启机器人主机")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("重启", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sendBroadcast(new Intent(Intent.ACTION_REBOOT)
                                        .putExtra("nowait", 1)
                                        .putExtra("interval", 1)
                                        .putExtra("window", 0));
                            }
                        }).show();
            }
        });

        /*****启动动画*****/
        findViewById(R.id.menu).startAnimation(AnimationUtils.loadAnimation(this, R.anim.activity_fade_in));
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshFtpInfo(NetworkService.getFtpAddrInfo());
        stopService(floatBtnIntent);
        HardwareServer.getInstance().takeCenterLightBlink();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
        HardwareServer.getInstance().takeCenterLightOff();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.openWiFi:
                startActivity(new Intent(this, WiFiManageActivity.class));
                break;
           /* case R.id.start_calibration:
                startActivity(new Intent(this, SiteCalibrationActivity.class));//起点校准
                break;
            case R.id.edit_pas:
                startActivity(new Intent(this, PasswordEditActivity.class));
                break;*/
            case R.id.open_hardware:
                Common.doStartApplicationWithPackageName(getApplication(), "com.starway.hardware");
                startService(floatBtnIntent);
                break;
            case R.id.open_setting:
                Common.doStartApplicationWithPackageName(getApplication(), "com.android.tv.settings");
                startService(floatBtnIntent);
                break;
            /*case R.id.siteManager:
                if (RobotInitState.getRobotInitState().hadInitialized()) {
                    startActivity(new Intent(this, SiteManageActivity.class));
                } else {
                    Common.showToast(this, "系统正在初始化，请稍后再试！");
                }
                break;*/
        }
    }

    private void refreshFtpInfo(NetworkService.FtpInfo info) {
        if (info != null && info.isAvailable()) {
            ftpInfo.setText("[ FTP用户名：" + info.getUser() + "，密码：" + info.getPassword()+" ]");
            setSubtitle(info.getAddress());
        } else {
            ftpInfo.setText("FTP服务已暂停");
            setSubtitle(null);
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case NetworkService.ACTION_FTP_START:
                    refreshFtpInfo((NetworkService.FtpInfo) intent.getSerializableExtra("ftp"));
                    break;
                case NetworkService.ACTION_FTP_STOP:
                    refreshFtpInfo(null);
                    break;
            }
        }
    };

}
