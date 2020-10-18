package com.starway.starrobot.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.starway.starrobot.R;
import com.starway.starrobot.activity.BaseActivity;

public class AnswerFragment extends Fragment {

    private TextView textView;
    private BaseActivity baseActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_text, container, false);
        textView = view.findViewById(R.id.textView);
//        textView.setMovementMethod(ScrollingMovementMethod.getInstance()); //设置TextVIew滚动，暂时用不到了
        baseActivity = (BaseActivity) getActivity();
        return view;
    }

    public void setAnswerText(String title, String text) { //设置回复文本

        if (textView != null) {
            if (text.length() > 50) {
                textView.setTextSize(30);
                textView.setGravity(Gravity.TOP);
            } else {
                textView.setTextSize(45);
                textView.setGravity(Gravity.CENTER|Gravity.TOP);
            }
            baseActivity.setTitle(title);
            baseActivity.setSubtitle(null);
            baseActivity.setFuncBtnIcon(BaseActivity.ICON_ENSURE).showFuncButton(true);
            textView.setText(text);
        }
    }

}
