package com.starway.starrobot.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.starway.starrobot.R;
import com.starway.starrobot.activity.BaseActivity;

import java.io.File;

public class WebFragment extends Fragment {

  private onLoadFinishedListener listener;
  private WebView webView;
  private BaseActivity baseActivity;
  private boolean loadOK = false;
  private String preUrl = "";
  private String preText = "";

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    baseActivity = (BaseActivity) getActivity();
    View view = inflater.inflate(R.layout.fragment_web, container, false);
    webView = view.findViewById(R.id.webView);
    webView.setBackgroundColor(0);
    webView.getBackground().setAlpha(0);
    webView.addJavascriptInterface(new InJavaScriptLocalObj(), "j");
    WebSettings settings = webView.getSettings();
/*    settings.setJavaScriptEnabled(true);*/

    webView.setWebViewClient(new WebViewClient() {
      @Override
      public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        webView.setVisibility(View.VISIBLE);
   /*     webView.getSettings().setJavaScriptEnabled(true);*/
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("http://cse.aiit.edu.cn/");
      }
    });

    return view;
  }

  /**
   * 载入web页面
   *
   * @param title 标题
   * @param url   链接
   */
  public void loadUrl(String title, String url) {
    if (webView != null) {
      System.out.println("load URL: "+url);
      webView.setVisibility(View.INVISIBLE);
      baseActivity.setSubtitle(null);

      File file = new File(url);
      String nowUrl = "file://";

      if (file.exists()) {
        baseActivity.setTitle(title);
        loadOK = true;
        nowUrl += url;
      } else {
        baseActivity.setTitle("资源不存在");
        loadOK = false;
        nowUrl += "/android_asset/html/not_found.html";
      }

      if (!nowUrl.equals(preUrl)) { //防止多次重复加载同一个文件
        webView.loadUrl(nowUrl);
        preUrl = nowUrl;
      } else {
        webView.setVisibility(View.VISIBLE);
        sendText(preText);
      }
    }
  }

  @Override
  public void onHiddenChanged(boolean hidden) {
    if (hidden) {
      System.out.println("被隐藏");
      webView.setVisibility(View.INVISIBLE);
    }
    super.onHiddenChanged(hidden);
  }

  private void sendText(String text) {
    if (listener != null) {
      if (loadOK) {
        listener.onLoadFinished(text);
      }
    }
  }

  /**
   * 设置页面加载完毕响应时间
   *
   * @param listener
   * @return
   */
  public WebFragment setListener(onLoadFinishedListener listener) {
    this.listener = listener;
    return this;
  }

  /**
   * 页面加载完后返回页面内容
   */
  public interface onLoadFinishedListener {
    void onLoadFinished(String text);
  }

  /**
   * js调用java接口
   */
  private final class InJavaScriptLocalObj {
    @JavascriptInterface
    public void getSource(String text) {
      preText = text;
      sendText(text);
    }
  }

}
