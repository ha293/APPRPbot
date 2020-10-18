package com.starway.starrobot.activity;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.starway.starrobot.R;
import com.starway.starrobot.ability.StarRobotAbility;
import com.starway.starrobot.mscability.StarMscAbility;
import com.starway.starrobot.mscability.speech.SpeechHelper;
import com.starway.starrobot.utils.Common;
import com.starway.starrobot.utils.RobotInitState;
import com.starway.starrobot.utils.VolumeUtil;
import com.starway.starrobot.view.ScrollingView;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by iBelieve on 2018/4/9.
 */

public class BaseActivity extends AppCompatActivity {

    private static boolean HOOK_WEBVIEW = false;
    private View btnBack;
    private View btnFunc;
    private ImageView btnFuncIcon;
    private TextView barTitle;
    private TextView subTitle;

    public static final int ICON_ADD = R.mipmap.ic_add;
    public static final int ICON_POWER = R.mipmap.ic_power;
    public static final int ICON_ENSURE = R.mipmap.ic_ensure;
    public static final int ICON_RELOAD = R.mipmap.ic_reload;
    public static final int ICON_FIND = R.mipmap.ic_find;

    @Override
    public void setContentView(int layoutResID) {
        if (!HOOK_WEBVIEW) {
            hookWebView();
        }
        super.setContentView(layoutResID);
        getWindow().getDecorView().setSystemUiVisibility( //隐藏虚拟按键
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);

        init();
    }

    private void init() {
        btnBack = findViewById(R.id.btnBack);
        btnFunc = findViewById(R.id.btnFunc);
        barTitle = findViewById(R.id.barTitle);
        subTitle = findViewById(R.id.barSubtitle);
        btnFuncIcon = findViewById(R.id.btnFuncIcon);

        if (btnBack != null) {
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        ScrollingView scrollingView = findViewById(R.id.volume);
        if (scrollingView != null) {
            scrollingView.setScrollBlockSize(30)
                    .setOnScrollingListener(new ScrollingView.onScrollingListener() {
                        @Override
                        public void onTouchEvent(int t) {
                            switch (t) {
                                case ScrollingView.ACTION_SCROLL_LEFT:
                                    VolumeUtil.getInstance(BaseActivity.this).minusVolume(1);
                                    break;
                                case ScrollingView.ACTION_SCROLL_RIGHT:
                                    VolumeUtil.getInstance(BaseActivity.this).plusVolume(1);
                                    break;
                            }
                        }
                    });
        }
    }

    @Override
    public void setTitle(int titleId) {
        setTitle(getText(titleId));
    }

    /**
     * 设置主标题
     *
     * @param title
     */
    @Override
    public void setTitle(CharSequence title) {
        if (barTitle != null) {
            barTitle.setText(title);
        }
    }

    /**
     * 设置副标题
     *
     * @param title
     */
    public BaseActivity setSubtitle(String title) {
        if (subTitle != null) {
            subTitle.setText(title);
            if (title == null || title.isEmpty()) {
                barTitle.setTextColor(0xFF2AF727);
            } else {
                barTitle.setTextColor(0xFFFFFFFF);
            }
        }
        return this;
    }

    /**
     * 显示/隐藏功能按钮
     *
     * @param flag
     */
    public BaseActivity showFuncButton(boolean flag) {
        if (btnFunc != null) {
            btnFunc.setVisibility(flag ? View.VISIBLE : View.INVISIBLE);
        }
        return this;
    }

    /**
     * 这种功能按钮图标
     *
     * @param resId
     */
    public BaseActivity setFuncBtnIcon(int resId) {
        if (btnFuncIcon != null) {
            btnFuncIcon.setImageResource(resId);
        }
        return this;
    }


    /**
     * 设置功能按钮点击监听事件
     *
     * @param listener
     */
    public BaseActivity setOnFuncBtnClickListener(View.OnClickListener listener) {
        if (btnFunc != null) {
            btnFunc.setOnClickListener(listener);
        }
        return this;
    }

    /**
     * 设置返回按钮点击监听事件
     *
     * @param listener
     */
    public BaseActivity setOnBackBtnClickListener(View.OnClickListener listener) {
        if (btnBack != null) {
            btnBack.setOnClickListener(listener);
        }
        return this;
    }

    /**
     * 解决系统应用无法使用webview的问题
     */
    public static void hookWebView() {
        HOOK_WEBVIEW = true;
        int sdkInt = Build.VERSION.SDK_INT;
        try {
            Class<?> factoryClass = Class.forName("android.webkit.WebViewFactory");
            Field field = factoryClass.getDeclaredField("sProviderInstance");
            field.setAccessible(true);
            Object sProviderInstance = field.get(null);
            if (sProviderInstance != null) {
                Log.d("HookWebView", "sProviderInstance isn't null");
                return;
            }
            Method getProviderClassMethod;
            if (sdkInt > 22) {
                getProviderClassMethod = factoryClass.getDeclaredMethod("getProviderClass");
            } else if (sdkInt == 22) {
                getProviderClassMethod = factoryClass.getDeclaredMethod("getFactoryClass");
            } else {
                Log.i("HookWebView", "Don't need to Hook WebView");
                return;
            }
            getProviderClassMethod.setAccessible(true);
            Class<?> providerClass = (Class<?>) getProviderClassMethod.invoke(factoryClass);
            Class<?> delegateClass = Class.forName("android.webkit.WebViewDelegate");
            Constructor<?> providerConstructor = providerClass.getConstructor(delegateClass);
            if (providerConstructor != null) {
                providerConstructor.setAccessible(true);
                Constructor<?> declaredConstructor = delegateClass.getDeclaredConstructor();
                declaredConstructor.setAccessible(true);
                sProviderInstance = providerConstructor.newInstance(declaredConstructor.newInstance());
                Log.d("HookWebView", "sProviderInstance:{}" + sProviderInstance);
                field.set("sProviderInstance", sProviderInstance);
            }
            Log.d("HookWebView", "Hook done!");
        } catch (Throwable e) {
            Log.e("HookWebView", e.toString());
        }
    }

    /**
     * 初始化机器人能力
     */
    protected void initAbility() {
        try {
            StarMscAbility.getInstance().initWithAppid(this, Common.AIUI_APPID);
            StarRobotAbility.getInstance().initAbilityWithoutNetwork(this, new StarRobotAbility.onResultCallback() {
                @Override
                public void onResult(boolean isSuccess, String msg) {
                    if (isSuccess) {
                        RobotInitState.getRobotInitState().setState(true);
                        Log.d("robot ability", "初始化成功");
                    } else {
                        Log.e("MainActivity", "机器人服务初始化失败，原因：" + msg);
                    }
                }
            });
            SpeechHelper.getInstance().setVoicer("jiajia");
        } catch (Exception e) {
            Log.e("robot ability", "初始化异常");
        }
    }

}
