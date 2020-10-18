package com.starway.starrobot.utils;

/**
 * 静态资源集合类
 * Created by iBelieve on 2018/5/4.
 */

public class ResUtil {

    private static final String[] ANSWER_AGREE = { //同意时的提示语
            "好的",
            "没问题",
            "OK"
    };

    private static final String[] ANSWER_WAKEUP = { //唤醒时的提示语
            "我在，你说！",
            "在呢！", "我在呢！",
            "请问有什么问题？",
            "您找我有啥事呢？"
    };

    private static final String[] ANSWER_LEAVE = { //离开时的提示语
            "再见",
            "下次有事再叫我吧",
            "很高兴为您服务,再见！",
            "期待下次为您服务",
            "那我走了",
            "我走了，不要想我哦"
    };

    private static final String[] ANSWER_UNKNOWN = { //无法解答时的提示语
            "小途好笨，竟然不知道您在说什么！",
            "对不起，小途没有听明白！",
            "小途正在学习中!",
            "对不起，小途没有听懂，请再说一遍!",
            "话说一半可不好哦，可以再说一遍吗？",
            "您好，小途没有理解您的意思呢！"
    };

    public static String[] ANSWER_GUIDE = { //带路时回复
            "好的，请跟我来。",
            "好的，我这就带您去。",
            "请跟我走，马上就到了！",
            "没问题，请跟我来。"
    };

    public static String[] ANSWER_MOVE_FAILD = { //机器人移动失败使回复
            "请让一下，小途要回去啦",
            "你们围住小途是想干嘛呀",
            "请让一下，你们这样围着我，小途喘不过气啦",
            "能不能让我出去啊",
            "不要围着小途，小途不知道怎么走了",
            "你们挡着我的路啦"
    };

    private static final String[] TIP_QA = { //互动问答的提示语
            "你也可以直接询问我问题哦",
            "你可以点击下列列表或者询问我哦",
            "您也可以通过语音提问我哦",
    };

    private static final String[] TIP_GUIDE = { //需要带路时的提示语，“$”代表地点
            "如果要我带路的话,可以这样对我说：“带我去$”",
            "你可以对我说：“我要去$”,我就会带领您去哦",
            "如果您要去哪里的话,可以问我：“$在哪儿？”",
            "需要我带路的话,可以长按下列地点哦",
    };

    private static final String[] TIP_MENU = { //主页提示
            "今天天气怎么样？",
            "带我去$",
            "讲个笑话",
            "学院简介",
    };

    private static final String[] TAG_VOICE = { //声源
            "xiaoyan", "xiaoyu", "vixy", "vixq", "vixf", "vixm", "vixl",
            "vixr", "vixyun", "vixk", "vixqa", "vixying", "vixx", "vinn"
    };

    private static String getRandom(String[] arg) {
        return arg[(int) (Math.random() * arg.length)];
    }

    public static String getRandomAgreeAnswer() {
        return getRandom(ANSWER_AGREE);
    }

    public static String getRandomWakeUpAnswer() {
        return getRandom(ANSWER_WAKEUP);
    }

    public static String getRandomLeaveAnswer() {
        return getRandom(ANSWER_LEAVE);
    }

    public static String getRandomUnknownAnswer() {
        return getRandom(ANSWER_UNKNOWN);
    }

    public static String getRandomMoveFaildAnswer() {
        return getRandom(ANSWER_MOVE_FAILD);
    }

    public static String getRandomGuideAnswer() {
        return getRandom(ANSWER_GUIDE);
    }

    public static String getRandomQATip() {
        return getRandom(TIP_QA);
    }

    public static String getRandomGuideTip(String site) {
        return getRandom(TIP_GUIDE).replace("$", site);
    }

    public static String getRandomMenuTip(String site) {
        return getRandom(TIP_MENU).replace("$", site);
    }

    public static String getRandomVoiceTag() {
        return getRandom(TAG_VOICE);
    }

}
