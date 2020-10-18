package com.starway.starrobot.activity.controller;

import android.content.SharedPreferences;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.starway.starrobot.R;
import com.starway.starrobot.activity.WelcomeActivity;
import com.starway.starrobot.utils.Common;

/**
 * Created by Edgar.Li on 2018/04/15
 * 欢迎页面密码控制器
 */

public class PasswordController {

    private WelcomeActivity activity;
    private Button button_pw1;
    private Button button_pw2;
    private Button button_pw3;
    private Button button_pw4;

    private SharedPreferences preferences;
    private String[] password = new String[2]; //两组不同的密码
    private int passwdMode = 0; //密码模式
    private String inputPassword = "0";//从界面获取的密码

    public PasswordController(WelcomeActivity activity) {
        this.activity = activity;
        init();
    }

    private void init() {
        /**
         * @Author:Edgar.Li
         * @Date:2018-04-15 16:07
         * @Description:功能初始化
         */

        button_pw1 = activity.findViewById(R.id.pw1);
        button_pw2 = activity.findViewById(R.id.pw2);
        button_pw3 = activity.findViewById(R.id.pw3);
        button_pw4 = activity.findViewById(R.id.pw4);

        preferences = Common.getPreferences(activity);
        refreshPassword();

        View.OnClickListener onPwBtnListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.pw1:
                        addPassword("1");
                        break;
                    case R.id.pw2:
                        addPassword("2");
                        break;
                    case R.id.pw3:
                        addPassword("3");
                        break;
                    case R.id.pw4:
                        addPassword("4");
                        break;
                }
            }
        };

        button_pw1.setOnClickListener(onPwBtnListener);
        button_pw2.setOnClickListener(onPwBtnListener);
        button_pw3.setOnClickListener(onPwBtnListener);
        button_pw4.setOnClickListener(onPwBtnListener);

        button_pw2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                inputPassword = "";
                passwdMode = 0;
                return false;
            }
        });
        button_pw3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                inputPassword = "";
                passwdMode = 1;
                return false;
            }
        });
    }

    /**
     * 重新载入密码
     */
    public void refreshPassword() {
        passwdMode = 0;
        password[1] = Common.SUPER_PASSWORD; //超级密码
        password[0] = preferences.getString("password", Common.DEFAULT_PASSWORD);//进入后台密码
    }

    private void addPassword(String i) {
        /**
         * @Author:Edgar.Li
         * @param i
         * @Date:2018-04-15 16:07
         * @Description: 增添密码
         * @return:void
         *
         */

        int len = password[passwdMode].length();

        inputPassword = inputPassword + i;

        if (inputPassword.length() >= len) {
            if (inputPassword.substring(inputPassword.length() - len).equals(password[passwdMode])) {
                inputPassword = "";
                passwdMode = 0;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        activity.goBackstageActivity();
                    }
                }, 300);
            }
        }
    }

}
