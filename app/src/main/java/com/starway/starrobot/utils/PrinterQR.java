package com.starway.starrobot.utils;

import com.printsdk.cmd.PrintCmd;
import com.starway.starrobot.ability.hardware.print.PrinterLines;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by iBelieve on 2018/5/8.
 */

public class PrinterQR extends PrinterLines {
    private List<byte[]> mData = new ArrayList();

    public PrinterQR() {
    }

    private void initData() {
        try {
            this.mData.clear();
            this.mData.add(PrintCmd.SetAlignment(1));
            this.mData.add(PrintCmd.PrintQrcode("iBelieve..", 25, 6, 1));
            this.mData.add(PrintCmd.PrintFeedline(3));// 走纸换行
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    public List<byte[]> getVaule() {
        this.initData();
        System.out.println("被调用: "+mData.size());
        return this.mData;
    }
}
