package com.starway.starrobot.utils;

import android.graphics.Bitmap;

import com.printsdk.cmd.PrintCmd;
import com.starway.starrobot.ability.hardware.print.PrinterLines;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iBelieve on 2018/5/8.
 */

public class PrinterBitmap extends PrinterLines {
    private List<byte[]> mData = new ArrayList();

    public PrinterBitmap(Bitmap bm) {
        try {
            this.mData.clear();
            this.mData.add(PrintCmd.PrintDiskImagefile(getPixelsByBitmap(bm), bm.getWidth(), bm.getHeight()));
            this.mData.add(PrintCmd.PrintFeedline(3));// 走纸换行
        } catch (Exception var3) {
            var3.printStackTrace();
        }
    }

    public List<byte[]> getVaule() {
        System.out.println("被调用: "+mData.size());
        return this.mData;
    }

    public static int[] getPixelsByBitmap(Bitmap bm) {
        int width, height;
        width = bm.getWidth();
        height = bm.getHeight();
        int iDataLen = width * height;
        int[] pixels = new int[iDataLen];
        bm.getPixels(pixels, 0, width, 0, 0, width, height);
        return pixels;
    }

    private byte[] setPrintConcentration(int concentrationValue) {
        byte[] b_send = new byte[3];
        int iIndex = 0;
        b_send[(iIndex++)] = 0x12;
        b_send[(iIndex++)] = 0x7e;
        b_send[(iIndex++)] = (byte) concentrationValue;
        return b_send;
    }

}
