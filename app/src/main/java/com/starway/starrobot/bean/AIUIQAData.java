package com.starway.starrobot.bean;

import java.util.List;

/**
 * Created by iBelieve on 2018/5/3.
 */

public class AIUIQAData {


    public QAData data;

    public class QAData{
        public int count;
        public int index;

        public List<QA> result;

        public class QA{
            public List<String> answerList;
            public List<String> questionList;


            @Override
            public String toString() {
                return "QA{" +
                        "answerList=" + answerList +
                        ", questionList=" + questionList +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "QAData{" +
                    "count=" + count +
                    ", index=" + index +
                    ", result=" + result +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "AIUIQAData{" +
                "data=" + data +
                '}';
    }
}
