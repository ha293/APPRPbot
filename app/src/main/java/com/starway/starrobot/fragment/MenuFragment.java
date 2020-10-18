package com.starway.starrobot.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.starway.starrobot.R;
import com.starway.starrobot.activity.BaseActivity;
import com.starway.starrobot.activity.controller.CountdownController;
import com.starway.starrobot.sqLite.SiteBean;
import com.starway.starrobot.utils.ResUtil;

import java.util.List;

public class MenuFragment extends Fragment implements View.OnClickListener {

    private OnMenuBtnClickListener listener;
    private BaseActivity baseActivity;
    private View btnjieshap;
    private View btnbook;
    private View btnGudie;
    private View btnQA;
    private List<SiteBean> siteBeans;

    public MenuFragment(List<SiteBean> siteBeans) {
        this.siteBeans = siteBeans;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        baseActivity = (BaseActivity) getActivity();
        btnjieshap = view.findViewById(R.id.btnjieshap);
        btnbook = view.findViewById(R.id.btnbook);
        btnGudie = view.findViewById(R.id.btnGudie);
        btnQA = view.findViewById(R.id.btnQA);

        btnjieshap.setOnClickListener(this);
        btnbook.setOnClickListener(this);
        btnGudie.setOnClickListener(this);
        btnQA.setOnClickListener(this);
        return view;
    }


    @Override
    public void onClick(View v) {
        if (listener != null) {
            switch (v.getId()) {
                case R.id.btnjieshap:
                    listener.onCollegeBtnClick();
                    break;
                case R.id.btnbook:
                    listener.onCenterBtnClick();
                    break;
                case R.id.btnGudie:
                    listener.onGuideBtnClick();
                    break;
                case R.id.btnQA:
                    listener.onQABtnClick();
                    break;

            }
        }
    }

    /**
     *
     *功能事件的回调
     *
     *
     */
    public MenuFragment setListener(OnMenuBtnClickListener listener) {
        this.listener = listener;
        return this;
    }

    /**
     * 功能页面按钮点击回调接口
     */
    public interface OnMenuBtnClickListener {
        void onCollegeBtnClick();

        void onCenterBtnClick();

        void onGuideBtnClick();

        void onQABtnClick();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            refreshMenuPageTitle();
        }
    }

    /**
     * 刷新主页面标题和提示
     */
    public void refreshMenuPageTitle() {
        baseActivity.setTitle("你可以对我说");
        if (siteBeans.size() > 0) {
            baseActivity.setSubtitle(ResUtil.getRandomMenuTip(siteBeans.get((int) (Math.random() * siteBeans.size())).getName()));
        } else {
            baseActivity.setSubtitle("明天天气怎么样");
        }
    }

}
