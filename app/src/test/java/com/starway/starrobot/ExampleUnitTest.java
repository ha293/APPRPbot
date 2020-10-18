package com.starway.starrobot;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.starway.starrobot.ability.hardware.print.PrinterLines;
import com.starway.starrobot.bean.AIUIQAData;
import com.starway.starrobot.sqLite.SiteBean;
import com.starway.starrobot.utils.HttpUtil;
import com.starway.starrobot.utils.PrinterQR;

import org.junit.Test;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.Source;

import jxl.biff.formula.FunctionNames;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {


    @Test
    public void addition_isCorrect() throws Exception {

        System.out.println((int)((Math.random() * 9 + 1) * 1000));

        System.out.println("呼伦贝尔市埇桥区萧县新庄镇沟头寺行政村沟头寺自然村129".replaceAll("^(.{4,}?(?:市|区|县)).+$","$1"));

//        HttpUtil httpUtil = HttpUtil.getInstance();
//        System.out.println(httpUtil._get("http://aiui.xfyun.cn/repository/queryRepoData?repositoryId=1622c42b509&pageIndex=2",AIUIQAData.class));
//        httpUtil.get("http://aiui.xfyun.cn/repository/queryRepoData?repositoryId=1622c42b509&pageIndex=2", new HttpUtil.ObjectCallback<AIUIQAData>() {
//            @Override
//            public void onResponse(Call call, AIUIQAData obj) {
//                System.out.println(obj);
//            }
//        });

//        assertEquals(4, 2 + 2);

//        try {
//            String json = "{\"aa\":{\"name\":\"Lixun\",age: 21},\"bb\":{\"name\":\"YYYW\",age: 22},\"cc\":{\"name\":\"ZhangX\",age: 26}}";
//            JsonParser p = new JsonParser();
//            JsonElement e = p.parse(json);
//
//            Set<Map.Entry<String, JsonElement>> es = e.getAsJsonObject().entrySet();
//            for (Map.Entry<String, JsonElement> xx : es) {
//                JsonObject element = xx.getValue().getAsJsonObject();
//                String name = element.get("name").getAsString();
//                int age = element.get("age").getAsInt();
//                System.out.println(name+" "+age);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}