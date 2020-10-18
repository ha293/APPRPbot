package com.starway.starrobot.activity.backstage;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.starway.starrobot.R;
import com.starway.starrobot.activity.BaseActivity;
import com.starway.starrobot.utils.Common;

public class PasswordEditActivity extends BaseActivity implements View.OnClickListener {

    private Button btnA;//左上
    private Button btnB;//右上
    private Button btnC;//左下
    private Button btnD;//右下

    private Button rollback;
    private TextView showPas;


    String password = "";//密码
    SharedPreferences spf;
    SharedPreferences.Editor editor;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backstage_password);

        showFuncButton(true);
        setTitle(R.string.setting_password);
        spf = Common.getPreferences(this);
        editor = spf.edit();
        initView();
    }

    private void initView() {
        btnA = findViewById(R.id.btn_1);
        btnB = findViewById(R.id.btn_2);
        btnC = findViewById(R.id.btn_3);
        btnD = findViewById(R.id.btn_4);

        showPas = findViewById(R.id.show_pas);
        rollback = findViewById(R.id.rollback);

        btnA.setOnClickListener(this);
        btnB.setOnClickListener(this);
        btnC.setOnClickListener(this);
        btnD.setOnClickListener(this);
        rollback.setOnClickListener(this);

        setOnFuncBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePassword();
            }
        });
    }


    public void savePassword() {
        if (password.length() < 4) {
            Common.showToast(this, "密码长度不能少于4位");
        } else {
            Common.showToast(this, "保存中");
            editor.putString("password", password);
            editor.apply();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Common.showToast(PasswordEditActivity.this, "密码修改完成");
                    finish();
                }
            }, 500);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_1:
                addPassward(1);
                break;
            case R.id.btn_2:
                addPassward(2);
                break;
            case R.id.btn_3:
                addPassward(3);
                break;
            case R.id.btn_4:
                addPassward(4);
                break;
            case R.id.rollback:
                deleteLast();
                break;
        }
    }

    private void deleteLast() {
        /**
         * @Author:Edgar.Li
         * @Date:2018-04-15 15:03
         * @Description:回退一位密码
         * @return:void
         *
         */

        if(!password.isEmpty()) {
            password = password.substring(0, password.length() - 1);
            refreshPasswdView();
        }
    }

    private void addPassward(int i) {
        /**
         * @Author:Edgar.Li
         * @Date:2018-04-13 17:43
         * @Description:添加左上按钮
         * @return:void
         *
         */

        if (password.length() < 10) {
            password += String.valueOf(i);
            refreshPasswdView();
        } else {
            Common.showToast(this, "密码长度超过10");
        }
    }


    private void refreshPasswdView() {
        /**
         * @Author:Edgar.Li
         * @Date:2018-04-13 17:43
         * @Description:将password刷新显示的showpas上面
         * @return:void
         *
         */
        String pw = password
                .replace("1", " Ａ")
                .replace("2", " Ｂ")
                .replace("3", " Ｃ")
                .replace("4", " Ｄ");
        showPas.setText(pw);
    }

}
