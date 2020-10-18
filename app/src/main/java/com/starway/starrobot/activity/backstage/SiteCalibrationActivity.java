package com.starway.starrobot.activity.backstage;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.starway.starrobot.R;
import com.starway.starrobot.ability.HardwareServer;
import com.starway.starrobot.ability.hardware.base.BaseHardware;
import com.starway.starrobot.activity.BaseActivity;
import com.starway.starrobot.sqLite.SQLiteHelper;
import com.starway.starrobot.sqLite.SiteBean;
import com.starway.starrobot.view.ProgressWheel;

public class SiteCalibrationActivity extends BaseActivity {

    private ProgressWheel progressWheel;
    private SQLiteHelper sqLiteHelper;
    private TextView loadFinish;
    private SiteBean siteBean;
    private View calibPanel;
    private View beginBtn;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backstage_calibration);

        setTitle(R.string.setting_calibrate);
        setFuncBtnIcon(ICON_ENSURE);

        calibPanel = findViewById(R.id.panel);
        beginBtn = findViewById(R.id.begin);
        progressWheel = findViewById(R.id.progress_wheel);
        loadFinish = findViewById(R.id.load_finish);

        sqLiteHelper = new SQLiteHelper(this, "site.db", null, 1);


        beginBtn.setOnClickListener(new View.OnClickListener() { //点击按钮后开始校准
            @Override
            public void onClick(View v) {
                beginBtn.setVisibility(View.INVISIBLE);
                calibPanel.setVisibility(View.VISIBLE);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        start();
                    }
                },2000); //两秒后开始校准
            }
        });
    }

    private void start() {
        siteBean = sqLiteHelper.queryStartPointSiteBean();
        //机器人位置校准
        HardwareServer.getInstance().positionAdjust(siteBean.getPlace(), new BaseHardware.onResultCallback() {
            @Override
            public void onResult(boolean result, String message) {
                if (result) {
                    Log.d("Chassis", "底盘位置校准成功");
                    loadFinish.setText("校准完成");
                    progressWheel.beginDrawTick();
                    loadFinish.setEnabled(true);

                    showFuncButton(true);
                    setOnFuncBtnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    });
                } else {
                    Log.e("Chassis", "底盘位置校准失败，原因：" + message);
                }
            }
        });

    }

}
