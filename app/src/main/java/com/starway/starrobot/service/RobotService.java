package com.starway.starrobot.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.starway.starrobot.ability.HardwareServer;
import com.starway.starrobot.ability.hardware.base.BaseHardware;
import com.starway.starrobot.mscability.speech.SpeechHelper;
import com.starway.starrobot.utils.ResUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class RobotService extends Service {

    public static int POWER = -1; //机器人剩余电量
    public static boolean MOVING = false; //当前是否处于移动状态

    private long lastPowerCheckTime;
    private int currentPathRouteTimes;

    public RobotService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            HardwareServer.getInstance().requestForChassisStatus(new BaseHardware.onResultCallback() {
                @Override
                public void onResult(boolean b, String s) {
                    if (b) {
                        try {
                            parseStatusResult(new JSONObject(s));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (Exception e) {
            System.out.println("RobotService初始化失败："+e);
        }
        return START_STICKY;
    }

    private void parseStatusResult(JSONObject result) {
        if (result != null) {
            try {
                if (result.has("power_percent") && result.has("charge_state")) {
                    checkPower(Integer.parseInt(result.getString("power_percent")), (result.getBoolean("charge_state")));
                }
                if (result.has("move_retry_times") && result.has("hard_estop_state")) {
                    if (!result.getBoolean("hard_estop_state")) {
                        moveRetryFallback(Integer.parseInt(result.getString("move_retry_times")));
                    }
                }
                if (result.has("move_status")) {
                    MOVING = result.getString("move_status").equals("running");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 检查电量是否过低
     *
     * @param powerPercent
     * @param chargeState
     */
    private void checkPower(int powerPercent, boolean chargeState) {
        POWER = powerPercent;
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - this.lastPowerCheckTime >= 50000) {
            if (!chargeState && powerPercent <= 20) {
                SpeechHelper.getInstance().speak("当前电量不足" + powerPercent + "%");
            }
            this.lastPowerCheckTime = currentTimeMillis;
        }
    }

    /**
     * 检查当前移动是否失败
     *
     * @param times
     */
    private void moveRetryFallback(int times) {
        if (this.currentPathRouteTimes > 0 && times == 0) {
            this.currentPathRouteTimes = 0;
        } else if (times - this.currentPathRouteTimes >= 2) {
            this.currentPathRouteTimes = times;
            if (HardwareServer.getInstance().checkChassisIsInMoving()) {
                SpeechHelper.getInstance().speak(ResUtil.getRandomMoveFaildAnswer());
            }
        }
    }
}
