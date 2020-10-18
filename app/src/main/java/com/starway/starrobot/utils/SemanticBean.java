package com.starway.starrobot.utils;

import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 语义结果对象
 * Created by xiongchen on 2016/11/10.
 */
public class SemanticBean {

    //用户说的话
    private String text = "";
    //响应码
    private String rc = "-1";
    //指令类型
    private String operation = "";
    //服务ID
    private String service = "";

    //语义结果
    private List<Semantic> semantic;

    //回答 有answer则无semantic
    public Answer answer;

    /**
     * 判断语义是否有效
     *
     * @return
     */
    public boolean isValid() {
        return true;
    }

    public void setInputText(String text) {
        this.text = text;
    }

    /**
     * 获取输入的文字
     *
     * @return
     */
    public String getInputText() {
        return text;
    }

    /**
     * 获取服务器响应码
     *
     * @return
     */
    public String getRc() {
        return rc;
    }

    /**
     * 获取服务id
     *
     * @return
     */
    public String getService() {
        return service;
    }

    public void setService(String ser) {
        this.service = ser;
    }

    /**
     * 获取指令
     *
     * @return
     */
    public String getOperation() {

        return operation;
    }

    /**
     * 获取回答
     *
     * @return
     */
    public String getAnswer() {
        String str = answer != null ? answer.text : "";
        return str;
    }

    /**
     * 设置回答
     */
    public void setAnswer(Answer answer) {
        this.answer = answer;
    }


    public List<Semantic> getSemantic() {
        return semantic;
    }

    public void setSemantic(List<Semantic> semantic) {
        this.semantic = semantic;
    }

    /** end **/

    /**
     * 语义类
     */
    public static class Semantic {

        private String intent;

        private List<Slots> slots;

        public List<Slots> getSlots() {
            return slots;
        }

        public void setSlots(List<Slots> slots) {
            this.slots = slots;
        }
        public String getIntent() {
            return intent;
        }

        public void setIntent(String intent) {
            this.intent = intent;
        }
    }

    public static class Slots {

        public String name;

        public String value;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class Location {
        public String type;
        public String poi;
        public String city;

        @Override
        public String toString() {
            return "{" +
                    "type='" + type + '\'' +
                    ", poi='" + poi + '\'' +
                    ", city='" + city + '\'' +
                    '}';
        }
    }

    /**
     * 回答
     */
    public static class Answer {
        //回答的文本，jsonObject中其他字段暂时无用
        public String text;
        public String emotion;

        @Override
        public String toString() {
            return "{" +
                    "text='" + text + '\'' +
                    '}';
        }
    }


    @Override
    public String toString() {
        return "SemanticBean{" +
                "text='" + text + '\'' +
                "rc='" + rc + '\'' +
                ", operation='" + operation + '\'' +
                ", service='" + service + '\'' +
                ", emantic=" + (null != semantic ? semantic.toString() : "") +
                ", answer=" + (null != answer ? answer.toString() : "") +
                '}';
    }

    /**
     * 用于json解析
     *
     * @return
     */
    public static Type getClassType() {
        return new TypeToken<SemanticBean>() {
        }.getType();
    }

}
