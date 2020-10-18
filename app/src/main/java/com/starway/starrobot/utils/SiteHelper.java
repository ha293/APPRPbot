package com.starway.starrobot.utils;

import android.os.Handler;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.starway.starrobot.ability.HardwareServer;
import com.starway.starrobot.ability.hardware.base.BaseHardware;
import com.starway.starrobot.utils.RobotInitState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author:Edgar.Li
 * @Date:2018-04-10 10:12
 * @version:
 * @purpose:
 * @Description:地点列表
 */
public class SiteHelper {

    private OnSiteLoadedListener onSiteLoaded;


    private boolean loadFinished = false;
    Handler handler = new Handler();


    public void setOnSiteLoadedListener(OnSiteLoadedListener listener) {
        this.onSiteLoaded = listener;
        reload();
    }


    public void reload() {
        try {
            startGetSite();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!loadFinished) {
                        onSiteLoaded.onLoaded(false, null);
                    }
                }
            }, 3000);
        } catch (Exception e) {
            onSiteLoaded.onLoaded(false, null);
        }
    }


    private void startGetSite() {
        if (RobotInitState.getRobotInitState().hadInitialized()) {

            HardwareServer.getInstance().pointSynchronization(new BaseHardware.onResultCallback() {
                @Override
                public void onResult(boolean result, String message) {
                    if (result) {
                        //结果数据message为json字符串

                        System.out.println("加载成功");

                        loadFinished = true;
                        System.out.println(loadFinished);parse(message);

                    } else {
                        Log.e("Chassis", "底盘地点列表信息获取失败，原因：" + message);
                        System.out.println("加载失败");
                        onSiteLoaded.onLoaded(false, null);
                    }
                }
            });
        }
    }


    public void parse(String str) {
        final List<String> names = new ArrayList<String>();

        JsonParser parser = new JsonParser();
        JsonElement e = parser.parse(str);
        Set<Map.Entry<String, JsonElement>> es = e.getAsJsonObject().entrySet();
        for (Map.Entry<String, JsonElement> xx : es) {
            JsonObject element = xx.getValue().getAsJsonObject();
            String name = element.get("marker_name").getAsString();
            names.add(name);
        }
        onSiteLoaded.onLoaded(true, names);
    }


    /**
     * @Author:Edgar.Li
     * @Date:2018-04-10 20:25
     * @version:
     * @purpose:
     * @Description:
     */
    public interface OnSiteLoadedListener {
        void onLoaded(boolean result, List<String> sites);
    }
}
