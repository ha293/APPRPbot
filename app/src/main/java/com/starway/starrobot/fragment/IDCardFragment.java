package com.starway.starrobot.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.android.hardware.idscanner.IDCardInfo;
import com.starway.starrobot.R;
import com.starway.starrobot.activity.BaseActivity;
import com.starway.starrobot.utils.CardPrinterUtil;

public class IDCardFragment extends Fragment {

    private TextView nameView;
    private TextView birthdayView;
    private TextView addressView;
    private BaseActivity baseActivity;
    private CardPrinterUtil printerUtil;
    private String name = "纪念卡";
    private String idCardNo = "0000";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        baseActivity = (BaseActivity) getActivity();
        printerUtil = CardPrinterUtil.getInstance(getContext());
        View view = inflater.inflate(R.layout.fragment_idcard, container, false);
        nameView = view.findViewById(R.id.name);
        birthdayView = view.findViewById(R.id.birthday);
        addressView = view.findViewById(R.id.address);
        View printBtn = view.findViewById(R.id.print);
        printBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printerUtil.printCard(name);
            }
        });
        printBtn.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.btn_mic));
        return view;
    }

    public void showIDCardInfo(final IDCardInfo idCardInfo) {
        if (idCardInfo != null) {
            if (!idCardInfo.getIdcardno().equals(idCardNo)) {
                System.out.println("================================");
                System.out.println(idCardInfo.getName());
                System.out.println(idCardInfo.getAddress());
                System.out.println(idCardInfo.getBirthday());
                System.out.println(idCardInfo.getGrantdept());
                System.out.println(idCardInfo.getSex());
                System.out.println(idCardInfo.getIdcardno());
                System.out.println(idCardInfo.getNation());
                System.out.println(idCardInfo.getUserlifebegin());
                System.out.println(idCardInfo.getUserlifeend());
                System.out.println("================================");
                this.name = idCardInfo.getName();
                this.idCardNo = idCardInfo.getIdcardno();
                baseActivity.setSubtitle(idCardInfo.getIdcardno().trim());
                nameView.setText(idCardInfo.getName().trim());
                birthdayView.setText(idCardInfo.getBirthday().replaceAll("(....)(..)(..)", "$1年$2月$3日"));
                // 地址简写：默认显示到xx省xx市，直辖市显示:xx市xx区，自治区开头地址短的显示xxYY市或xx区
                addressView.setText(idCardInfo.getAddress().replaceAll("^(.{4,}?(?:市|区|县)).+$", "$1"));
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            baseActivity.showFuncButton(true).setTitle("身份证信息");
        }
    }
}
