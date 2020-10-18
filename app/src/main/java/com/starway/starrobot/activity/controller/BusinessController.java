package com.starway.starrobot.activity.controller;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

import com.android.hardware.idscanner.IDCardInfo;
import com.starway.starrobot.R;
import com.starway.starrobot.ability.HardwareServer;
import com.starway.starrobot.ability.hardware.base.BaseHardware;
import com.starway.starrobot.activity.HomeActivity;
import com.starway.starrobot.fragment.AnswerFragment;
import com.starway.starrobot.fragment.FaceFragment;
import com.starway.starrobot.fragment.FaceFragment_registered;
import com.starway.starrobot.fragment.GuideFragment;
import com.starway.starrobot.fragment.IDCardFragment;
import com.starway.starrobot.fragment.MenuFragment;
import com.starway.starrobot.fragment.QAFragment;
import com.starway.starrobot.fragment.WebFragment;
import com.starway.starrobot.mscability.speech.SpeechHelper;
import com.starway.starrobot.mscability.speech.TTS;
import com.starway.starrobot.service.NetworkService;
import com.starway.starrobot.service.RobotService;
import com.starway.starrobot.sqLite.SQLiteHelper;
import com.starway.starrobot.sqLite.SiteBean;
import com.starway.starrobot.utils.CardPrinterUtil;
import com.starway.starrobot.utils.EasySemanticParser;
import com.starway.starrobot.utils.ResUtil;
import com.starway.starrobot.utils.SemanticBean;
import com.starway.starrobot.utils.VolumeUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by iBelieve on 2018/4/19.
 * 核心业务控制器
 */

public class BusinessController implements CountdownController.OnTimeoutListener {

    private static final int LEAVE_TIME_ONCE = 50000;
    private static final int LEAVE_TIME_TWICE = 8000;

    private SpeechHelper speechHelper;
    private HardwareServer hardwareServer;

    private CardPrinterUtil printerUtil;
    private CountdownController countdownController;
    private EasySemanticParser semanticParser;
    private SQLiteHelper sqLiteHelper;
    private Context context;
    private HomeActivity homeActivity;
    private String startPoint;
    private List<SiteBean> siteBeans;
    private boolean enablePrase = true;

    private FragmentManager fragmentManager;
    private Fragment currentFragment;
    private Fragment previousFragment;
    private Map<String, Fragment> fragments = new HashMap<>();

    public BusinessController(Context context) {
        Log.i("test", "初始化控制器：" + this.toString());
        this.context = context;
        printerUtil = CardPrinterUtil.getInstance(context);
        sqLiteHelper = SQLiteHelper.getSqLiteHelper(context);
        siteBeans = sqLiteHelper.queryAllSiteBean(SQLiteHelper.QUERY_AVAILABLE);
        semanticParser = new EasySemanticParser(siteBeans);
        homeActivity = (HomeActivity) context;
        init();
        Log.i("test", "初始化完毕");
    }

    /**
     * 初始化控制器
     */
    private void init() {
        speechHelper = SpeechHelper.getInstance();
        hardwareServer = HardwareServer.getInstance();
        startPoint = sqLiteHelper.queryStartPointSiteBean().getPlace();
        fragmentManager = homeActivity.getSupportFragmentManager();

        homeActivity.setOnFuncBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToPreviousFragment();
            }
        });
        homeActivity.setOnBackBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToMenuPage();
            }
        });

        hardwareServer.startIdScanWithListener(idCardRegistHCallback);

        fragments.put("face",new FaceFragment().setListener(new FaceFragment.onFaceDetectedListener() {
            @Override
            public boolean onFaceDetected(String arg) {
                WebFragment fragment = (WebFragment) switchFragment("web");
                switch (arg) {
                    case "110110":
                    case "111111":
                    case "120120":

                        fragment.loadUrl("省长", NetworkService.FTP_HOME_PATH + "HTML/stadholder.html");
                        speakAndCountdown("欢迎李省长莅临安徽信息工程学院");

                        return true;
                    case "900900":
                        fragment.loadUrl("省委书记", NetworkService.FTP_HOME_PATH + "HTML/ppcs.html");
                        speakAndCountdown("欢迎李书记莅临安徽信息工程学院");
                        return true;
                    default:
                        fragment.loadUrl("识别成功", NetworkService.FTP_HOME_PATH + "HTML/other.html");
                }
                return false;
            }
        }));

       fragments.put("FaceFragment_registered",new FaceFragment_registered());//注册新的Fragment


        fragments.put("idcard", new IDCardFragment());

        fragments.put("menu", new MenuFragment(siteBeans)
                .setListener(new MenuFragment.OnMenuBtnClickListener() {
                    @Override
                    public void onCollegeBtnClick() {
                        WebFragment fragment = (WebFragment) switchFragment("web");
                        fragment.loadUrl("安徽信息工程学院", NetworkService.FTP_HOME_PATH + "HTML/aiit.html");
                    }

                    @Override
                    public void onCenterBtnClick() {
                        WebFragment fragment = (WebFragment) switchFragment("web");
                        fragment.loadUrl("智能之翼实验室", NetworkService.FTP_HOME_PATH + "HTML/shuangchuang.html");
                    }

                    @Override
                    public void onGuideBtnClick() {
                        switchFragment("guide");
                        if (siteBeans.size() > 0) {
                            String text = ResUtil.getRandomGuideTip(siteBeans.get((int) (Math.random() * siteBeans.size())).getName());
                            homeActivity.setSubtitle(text);
                            speakAndCountdown(text);
                        } else {
                            homeActivity.setSubtitle("暂无地点");
                        }
                    }

                    @Override
                    public void onQABtnClick() {
                        switchFragment("qa");
                        String text = ResUtil.getRandomQATip();
                        homeActivity.setSubtitle(text);
                        speakAndCountdown(text);
                    }
                })
        );
        Log.i("test", "fragments初始化一半");
        fragments.put("web", new WebFragment()
                .setListener(new WebFragment.onLoadFinishedListener() {
                    @Override
                    public void onLoadFinished(String text) {
                        speakAndCountdown(text.replaceAll("[A-Za-z]{2,}", "")); //不读英文
                    }
                })
        );
        Log.i("test", "fragments初始化一半多了");
        fragments.put("guide", new GuideFragment()
                .setListener(new GuideFragment.OnSiteItemClickListener() {
                    @Override
                    public void onItemClick(SiteBean site) {
                        showAndSpeakTextPage(site.getName(), site.getDescribe());
                    }

                    @Override
                    public void onItemLongClick(SiteBean site) {
                        doNeedGuide(site);
                    }
                })
        );

        fragments.put("qa", new QAFragment()
                .setListener(new QAFragment.OnQAItemClickListener() {
                    @Override
                    public void onItemClick(String title, String text) {
                        showAndSpeakTextPage(title, text);
                    }
                })
        );

        fragments.put("answer", new AnswerFragment());

        for (Map.Entry<String, Fragment> entity : fragments.entrySet()) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.fragment_content, entity.getValue(), entity.getKey());
            if (entity.getKey() != "menu") {
                transaction.hide(entity.getValue());
            } else {
                currentFragment = entity.getValue();
            }
            transaction.commitAllowingStateLoss();
        }
        Log.i("test", "fragments初始化完毕");
        countdownController = CountdownController.getInstance();
        countdownController.start(this, LEAVE_TIME_ONCE, LEAVE_TIME_TWICE);
    }

    /**
     * 切换 Fragment
     *
     * @param tag Fragment标记
     * @return
     */
    public Fragment switchFragment(String tag) {
        Fragment fragment = fragments.get(tag);
        if (fragment != null && currentFragment != fragment) {
            fragmentManager.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .hide(currentFragment)
                    .show(fragment)
                    .commitAllowingStateLoss();
            if (previousFragment != null) {
                previousFragment.onPause();
            }
            previousFragment = currentFragment;
            currentFragment = fragment;
        }
        return fragment;
    }

    /**
     * 返回前一个Fragment
     */
    public void backToPreviousFragment() {
        enablePrase = true; //允许解析
        hardwareServer.stopMove(); //先停止移动
        homeActivity.showFuncButton(false);
        if (previousFragment != currentFragment) {
            fragmentManager.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .hide(currentFragment)
                    .show(previousFragment)
                    .commitAllowingStateLoss();
            currentFragment = previousFragment;
        }
    }


    /**
     * 返回主菜单（不进行检查）
     */
    public void onlyBackToMenuPage(boolean stop) {
        enablePrase = true; //允许解析
        hardwareServer.stopMove(); //停止移动
        if (stop) {
            speechHelper.stopSpeak();  //闭嘴
        }
        homeActivity.showFuncButton(false);
        switchFragment("menu");
    }

    /**
     * 返回主菜单（检查当前是子功能还是主页）
     */
    public void backToMenuPage() {
        enablePrase = true; //允许解析
        speechHelper.stopSpeak();  //闭嘴
        hardwareServer.stopMove(); //先停止移动
        homeActivity.showFuncButton(false);
        if ("menu".equals(currentFragment.getTag())) {
            backToWelcomeActivity();
        } else {
            onlyBackToMenuPage(false);
        }
    }

    /**
     * 返回欢迎页面
     */
    public void backToWelcomeActivity() {
        Log.i("test", "任务结束，返回欢迎页面：" + this.toString());
        speechHelper.stopSpeak(true);  //闭嘴
        hardwareServer.stopMove(); //先停止移动
        countdownController.stop();  //停止计时
        if (startPoint != null && !startPoint.isEmpty()) { //再回到最初的起点
            hardwareServer.moveToPosition(startPoint, null);
        }
        speechHelper.speak(ResUtil.getRandomLeaveAnswer());//这里不能用speakAndCountdown，因为speakAndCountdown读完后会继续计时
        homeActivity.backToWelcomeActivity();
        homeActivity.finish();
    }

    /**
     * 停止并清理 Controller
     */
    public void stop() {
        Log.i("test", "stop：" + this.toString());
        countdownController.stop(); //停止计时
        for (Map.Entry<String, Fragment> entity : fragments.entrySet()) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.remove(entity.getValue());
            transaction.commitAllowingStateLoss();
        }
    }

    /**
     * 切换到文本页面并读出来
     *
     * @param title
     * @param text
     */
    public void showAndSpeakTextPage(String title, String text) {
        AnswerFragment fragment = (AnswerFragment) switchFragment("answer");
        fragment.setAnswerText(title, text);
        speakAndCountdown(text);
    }

    /**
     * 读出文字(读完后会继续计时)
     *
     * @param text
     */
    public void speakAndCountdown(String text) {
        speechHelper.stopSpeak();
        countdownController.stop(); //先停止计时
        speechHelper.speak(text, new TTS.onSpeakCallback() {
            @Override
            public void onSpeak(String s) {
                if (!RobotService.MOVING) {
                    countdownController.start(); //讲完后开始计时
                }
            }
        });
    }


    /**
     * 本地解析语义
     *
     * @param semanticBean
     */
    public void parser(SemanticBean semanticBean) {
        String inputString = semanticBean.getInputText();

        countdownController.interrupt();
        hardwareServer.doFaceLightInProgress();
        hardwareServer.doEmojiHappy();

        if (semanticParser.isExit(inputString)) {
            doExit();
            return;
        }

        if (semanticParser.returnMenu(inputString)) {
            returnMenu();
            return;
        }


        if (enablePrase) {

            //周祥冉测试这里(猜猜我是谁)
            if(semanticParser.queren(inputString)){
                guessMe();
                return;
            }

            //周祥冉测试这里（注册人脸）
            if(semanticParser.zhuce(inputString)){
                zhucerenlian();
                return;
            }

            //周祥冉测试这里（计协招新介绍）
            if(semanticParser.jixie(inputString)){
                ACM_load();
                return;
            }


            if (semanticParser.queryPower(inputString)) {
                doQueryPower();
                return;
            }
            if (semanticParser.changeVoice(inputString)) {
                doChangeVoice();
                return;
            }
            SiteBean siteBean = semanticParser.needGuide(inputString);
            if (siteBean != null) {
                doNeedGuide(siteBean);
                return;
            }
            String t = semanticParser.changeVolume(inputString);
            if (t != null) {
                doChangeVolume(t);
                return;
            }
            if (semanticParser.printCard(inputString)) {
                printCard();
                return;
            }
            if (semanticParser.ensure(inputString)) {
                //TODO: 用户确认事件
                speechHelper.speak("嗯");
                return;
            }
            onOther(semanticBean);
        }
    }

    /**************************场景处理*************************/


    /**
     * 省长专用
     */
    public void guessMe() {
        switchFragment("face");
    }

    public void zhucerenlian() {
        switchFragment("FaceFragment_registered");
    }

    public void ACM_load() {
       /* speakAndCountdown("下面是有关于ACM部门的介绍："+
                "1. 接受上级交接的任务,负责制定项目任务的计划和安排,组织实施。\n" +
                "2. 关于 ACM 类赛事，在全院范围类发掘人才，开展系列培训，为各项赛事输送人才。\n" +
                "3. 主动联系 ACM 赛事负责老师，配合好老师展开 ACM 相关赛事的校内选拔工作，争取能让学院能够在本赛事能够发挥出更好的水平。\n" +
                "4.做好赛事整个流程的材料整理与存储工作，在赛事结束后，发送给办公室进行存档。\n");*/
        WebFragment fragment = (WebFragment) switchFragment("web");
        fragment.loadUrl("计算机协会官网", NetworkService.FTP_HOME_PATH + "HTML/jixie_tiaozhuan.html");

    }

    /**
     * 当用户退出时
     */
    public void doExit() {
        backToWelcomeActivity();
    }

    /**
     * 当用户退出时
     */
    public void returnMenu() {
        speakAndCountdown(ResUtil.getRandomAgreeAnswer());
        onlyBackToMenuPage(false);
    }

    /**
     * 当用户询问电量时
     */
    public void doQueryPower() {
        if (RobotService.POWER < -1) {
            speakAndCountdown("还未初始化完成哦");
        } else {
            speakAndCountdown("当前剩余：" + RobotService.POWER + "%电量");
        }
    }

    /**
     * 当用户需要带路时
     *
     * @param place 地点
     */
    public void doNeedGuide(final SiteBean place) {
        countdownController.stop(); //先停止计时
        hardwareServer.stopMove(); //停止移动
        showAndSpeakTextPage(place.getName(), ResUtil.getRandomGuideAnswer());
        hardwareServer.moveToPosition(place.getPlace(), new BaseHardware.onResultCallback() {
            @Override
            public void onResult(boolean b, String s) {
                // TODO：这里测试一下是否是移动完毕后，MOVING为false
                enablePrase = false;
                showAndSpeakTextPage(place.getName(), place.getDescribe());
            }
        });
    }

    /**
     * 当用户要求更换声源时
     */
    public void doChangeVoice() {
        String voice = ResUtil.getRandomVoiceTag();
        speechHelper.setVoicer(voice);
        speakAndCountdown("这种声音您喜欢吗？");
    }

    /**
     * 当用户要求打印纪念卡片
     */
    private void printCard() {
        showAndSpeakTextPage("打印纪念卡片", "好的，这就为您打印。");
        printerUtil.printCard("[纪念卡]");
    }


    /**
     * 调节声音时
     *
     * @param t 返回结果
     */
    public void doChangeVolume(String t) {
        switch (t) {
            case EasySemanticParser.CMD_VOLUME_PLUS:
                VolumeUtil.getInstance(context).plusVolume();
                speakAndCountdown(ResUtil.getRandomAgreeAnswer());
                break;
            case EasySemanticParser.CMD_VOLUME_MINUS:
                VolumeUtil.getInstance(context).minusVolume();
                speakAndCountdown(ResUtil.getRandomAgreeAnswer());
                break;
            case EasySemanticParser.CMD_VOLUME_MAX:
                VolumeUtil.getInstance(context).setMaxVolume();
                speakAndCountdown("音量已经调到最大了");
                break;
            case EasySemanticParser.CMD_VOLUME_MIN:
                VolumeUtil.getInstance(context).setMinVolume();
                speakAndCountdown("音量已经调到最小了");
                break;
        }
    }



    /**
     * 其他情况
     *
     * @param bean
     */
    public void onOther(SemanticBean bean) {

        String answer = bean.getAnswer();
        if (!answer.isEmpty()) {
            answer = answer.replaceAll("(\"|\\[.+?\\])", "");
            showAndSpeakTextPage(bean.getInputText(), answer);
            if (bean.answer.emotion != null) {
                switch (bean.answer.emotion) {
                    case "default":
                        hardwareServer.doEmojiWakeUp();
                        break;
                    case "happy":
                        hardwareServer.doEmojiHappy();
                        break;
                    case "neutral":
                        hardwareServer.doEmojiDizzy();
                        break;
                    case "sorrow":
                        hardwareServer.doEmojiSad();
                        break;
                    case "angry":
                        hardwareServer.doEmojiAngle();
                        break;
                    default:
                        hardwareServer.doEmojiWakeUp();
                }
            }
//            if (bean.answer.imgUrl != null) {
//                HttpUtil.getInstance().get(bean.answer.imgUrl, imgLoadCallback);
//            }
        } else {
            if ((int) (Math.random() * 3) == 0) { //有三分之一的概率回复“小途不明白”
                speechHelper.stopSpeak();  //闭嘴
                speakAndCountdown(ResUtil.getRandomUnknownAnswer());
            }

            hardwareServer.doHeadShake(); //随机显示一个表情
            switch ((int) (Math.random() * 3)) {
                case 0:
                    hardwareServer.doEmojiDizzy();
                    break;
                case 1:
                    hardwareServer.doEmojiWeep();
                    break;
                case 2:
                    hardwareServer.doEmojiSweat();
                    break;
            }

        }
    }

    /**************************场景处理*************************/

    /**
     * 当计时结束时
     *
     * @param count 第几次计时
     */
    @Override
    public void onTimeout(int count) {
        switch (count) {
            case 0:
                speechHelper.speak("还在吗？"); //这里不能用speakAndCountdown，因为speakAndCountdown读完后会继续计时
                break;
            case 1:
                backToWelcomeActivity();
                break;
        }
    }

    private Handler handler = new Handler();

    private Handler idCardPrintHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            IDCardInfo idCardInfo = (IDCardInfo) msg.getData().get("info");
            IDCardFragment fragment = (IDCardFragment) switchFragment("idcard");
            fragment.showIDCardInfo(idCardInfo);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() { //不能注册太快速的哦
                    hardwareServer.startIdScanWithListener(idCardRegistHCallback);
                }
            }, 1000);

            System.out.println("注册身份证扫描服务");
            return false;
        }
    });

    private BaseHardware.idScanCallback idCardRegistHCallback = new BaseHardware.idScanCallback() {
        @Override
        public void onIDscanEnd(IDCardInfo idCardInfo, byte[] bytes) {
            Message message = new Message();
            Bundle bundle = new Bundle();
            bundle.putParcelable("info", idCardInfo);
            message.setData(bundle);
            idCardPrintHandler.sendMessage(message);
            System.out.println("扫描结束");
        }
    };
}
