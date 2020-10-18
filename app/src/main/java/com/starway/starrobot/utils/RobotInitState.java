package com.starway.starrobot.utils;

import android.util.Log;

/**
 * @Author:Edgar.Li
 * @Date:2018-04-10 14:33
 * @version:
 * @purpose:
 * @Description:
 */
public class RobotInitState {
    public static RobotInitState robotInitState;
    private boolean state = false;

    public static synchronized RobotInitState getRobotInitState() {
        if (robotInitState == null) {
            robotInitState = new RobotInitState();
        }

        return robotInitState;
    }

    public boolean hadInitialized() {
        return state;
    }

    public RobotInitState setState(boolean state) {
        Log.e("Init","Ok");
        this.state = state;
        return this;
    }
}

