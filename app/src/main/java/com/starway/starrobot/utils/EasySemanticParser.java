package com.starway.starrobot.utils;

import android.util.Log;

import com.starway.starrobot.sqLite.SiteBean;

import java.util.List;

/**
 * Created by iBelieve on 2018/4/19.
 */

public class EasySemanticParser {

    private static final String[] WORDS_EXIT = {".*?再见.*?", ".*?拜拜.*?", ".*?没事了", ".*?你走吧", ".*?你去休息吧", ".*?滚.*?", ".*?回家.*?"};
    private static final String[] WORDS_ENSURE = {"好.{0,3}", ".?可以.?", ".?是.?", ".?没问题.?", "OK", ".?行.?", ".?要.?", ".?带我去吧.?"};
    private static final String[] WORDS_RETUEN = {".*?(返回|回到|退回|进入|打开).*?(菜单|上.*层|主页|首页).*?"};
    private static final String[] WORDS_GUDIE = {".*?带我(去|参观|看|找).+?", ".*?在哪.*?", "我要(去|参观|看|找).*?", ".*?怎么走.*?"};
    private static final String[] WORDS_GUIDE_NEXT = {".*?(去|参观).*?下一.*?(点|展区|).*?", ".*?（?:继续|接着).*?(参观|去).*?"};
    private static final String[] WORDS_POWER = {".+?(还|)[剩|有]多少电(量|)", ".+?(还|)(活|用)多久"};
    private static final String[] WORDS_VOICE = {".*?换.*?(声音|语言|方言).*?", ".*?(声音|语言|方言).*?换.*?"};
    private static final String[] WORDS_VOLUME_MAX = {".*?(声音|音量).*?最大.*?", ".*?最大.*?(声音|音量).*?"};
    private static final String[] WORDS_VOLUME_MIN = {".*?(声音|音量).*?最小.*?", ".*?最小.*?(声音|音量).*?"};
    private static final String[] WORDS_VOLUME_PLUS = {".*?(声音|音量).*?大.*?", ".*?大.*?(声音|音量).*?",};
    private static final String[] WORDS_VOLUME_MINUS = {".*?(声音|音量).*?小.*?", ".*?小.*?(声音|音量).*?",};
    private static final String[] WORDS_PRINT_CARD = {".*?打印.*?纪念.*?卡.*?", ".*?(给我|我要|来).*?纪念.*?卡.*?",};

    //zxr
    private static final String[] WORDS_FACE_SHIBIE = {".*?猜猜.*?我是.*?谁.*?", ".*?(猜猜|我是|谁).*?猜.*?我是谁.*?",};
    //zxr
    private static final String[] WORDS_FACE_REGISTERED = {".*?注册.*?人脸.*?.*?", ".*?(注册|人脸|人脸|注册).*?人脸.*?注册.*?",};
    //zxr
    private static final String[] WORDS_JISAUNJIXIEHUI = {".*?计算机协会.*?简介.*?.*?", ".*?(计算机协会|简介).*?计算机协会.*?简介.*?",};


    private List<SiteBean> siteList = null;

    public EasySemanticParser(List<SiteBean> siteBeans) {
        this.siteList = siteBeans;
    }

    /**
     * 匹配规则
     *
     * @param inputString
     * @param rules
     * @return
     */
    private boolean match(String inputString, String[] rules) {
        Log.i("EasySemanticParser", "开始匹配：{" + inputString + "}");
        for (String r : rules) {
            String regex = "^" + r + "$";
            Log.i("EasySemanticParser", "匹配规则：[" + regex + "]");
            if (inputString.matches(regex)) {
                Log.i("EasySemanticParser", "匹配成功!");
                return true;
            }
        }
        Log.i("EasySemanticParser", "解析条件未满足!");
        return false;
    }




    /*
    * 省长专用
    * zxr
    * */
    public boolean queren(String inputString) {
        Log.i("EasySemanticParser", "判断确认指令：" + inputString);
        return match(inputString, WORDS_FACE_SHIBIE);
    }


    /*
     * 计算机协会招新专用
     * zxr
     * */
    public boolean jixie(String inputString) {
        Log.i("EasySemanticParser", "判断确认指令：" + inputString);
        return match(inputString, WORDS_JISAUNJIXIEHUI);
    }


    public boolean zhuce(String inputString) {
        Log.i("EasySemanticParser", "判断确认指令：" + inputString);
        return match(inputString, WORDS_FACE_REGISTERED);
    }






    /**
     * 判断用户是否需要带路
     *
     * @param inputString
     * @return 返回地点名称（英文），不是带路指令则返回null
     */
    public SiteBean needGuide(String inputString) {
        Log.i("EasySemanticParser", "解析引导指令：" + inputString);
        if (siteList != null) {
            if (match(inputString, WORDS_GUDIE)) {
                for (int i = 0; i < siteList.size(); i++) {
                    if (inputString.indexOf(siteList.get(i).getName()) > -1) {
                        Log.i("EasySemanticParser", "地点为：" + siteList.get(i));
                        return siteList.get(i);
                    }
                }
            }
        }
        Log.e("EasySemanticParser", "解析条件未满足");
        return null;
    }

    /**
     * 判断离开指令
     *
     * @param inputString
     * @return
     */
    public boolean isExit(String inputString) {
        Log.i("EasySemanticParser", "判断离开指令：" + inputString);
        return match(inputString, WORDS_EXIT);
    }

    /**
     * 用户是否确认
     *
     * @param inputString
     * @return
     */
    public boolean ensure(String inputString) {
        Log.i("EasySemanticParser", "判断确认指令：" + inputString);
        return match(inputString, WORDS_ENSURE);
    }

    /**
     * 用户是否返回上一层
     *
     * @param inputString
     * @return
     */
    public boolean returnMenu(String inputString) {
        Log.i("EasySemanticParser", "判断返回指令：" + inputString);
        return match(inputString, WORDS_RETUEN);
    }

    /**
     * 判断用户是否询问电量
     *
     * @param inputString
     * @return
     */
    public boolean queryPower(String inputString) {
        Log.i("EasySemanticParser", "判断询问电量指令：" + inputString);
        return match(inputString, WORDS_POWER);
    }

    /**
     * 判断用户切换声音
     *
     * @param inputString
     * @return
     */
    public boolean changeVoice(String inputString) {
        Log.i("EasySemanticParser", "切换声音：" + inputString);
        return match(inputString, WORDS_VOICE);
    }

    /**
     * 判断用户切换声音
     *
     * @param inputString
     * @return
     */
    public boolean printCard(String inputString) {
        Log.i("EasySemanticParser", "打印卡片：" + inputString);
        return match(inputString, WORDS_PRINT_CARD);
    }


    /**
     * 判断用户调节声音大小
     *
     * @param inputString
     * @return
     */
    public String changeVolume(String inputString) {
        Log.i("EasySemanticParser", "调节声音：" + inputString);
        if (match(inputString, WORDS_VOLUME_MAX)) {
            return CMD_VOLUME_MAX;
        }
        if (match(inputString, WORDS_VOLUME_MIN)) {
            return CMD_VOLUME_MIN;
        }
        if (match(inputString, WORDS_VOLUME_PLUS)) {
            return CMD_VOLUME_PLUS;
        }
        if (match(inputString, WORDS_VOLUME_MINUS)) {
            return CMD_VOLUME_MINUS;
        }
        Log.e("EasySemanticParser", "解析条件未满足");
        return null;
    }

    public static final String CMD_VOLUME_PLUS = "plus";
    public static final String CMD_VOLUME_MINUS = "minus";
    public static final String CMD_VOLUME_MAX = "max";
    public static final String CMD_VOLUME_MIN = "min";


}
