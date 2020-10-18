package com.starway.starrobot.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.printsdk.cmd.PrintCmd;
import com.printsdk.usbsdk.UsbDriver;

/**
 * Created by iBelieve on 2018/5/10.
 */

public class BitmapPrinter {

    private static final String ACTION_USB_PERMISSION = "com.usb.sample.USB_PERMISSION";
    private static Context CONTEXT_INSTANCE;
    private Context context;
    private UsbManager mUsbManager;
    private UsbDriver mUsbDriver;
    UsbDevice mUsbDev1;        //打印机1
    UsbDevice mUsbDev2;        //打印机2
    private final static int PID11 = 8211;
    private final static int PID13 = 8213;
    private final static int PID15 = 8215;
    private final static int VENDORID = 1305;

    private static BitmapPrinter instance;

    private BitmapPrinter() {//必须在主线程初始化才可以
        try {
            this.context = (Context) Class.forName("android.app.ActivityThread")
                    .getMethod("currentApplication")
                    .invoke(null); //反射获取 ApplicationContext

            initUsbDriverService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static BitmapPrinter getInstance() {
        if (instance == null) {
            synchronized (BitmapPrinter.class) {
                if (instance == null) {
                    instance = new BitmapPrinter();
                }
            }
        }
        return instance;
    }

    private void initUsbDriverService() {
        mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        mUsbDriver = new UsbDriver(mUsbManager, context);
        mUsbDriver.setPermissionIntent(PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0));
    }

    private boolean checkPrintStatus() { //检查并初始化打印机状态
        boolean blnRtn = false;
        try {
            if (!mUsbDriver.isConnected()) {// USB线已经连接
                for (UsbDevice device : mUsbManager.getDeviceList().values()) {
                    if ((device.getProductId() == PID11 && device.getVendorId() == VENDORID)
                            || (device.getProductId() == PID13 && device.getVendorId() == VENDORID)
                            || (device.getProductId() == PID15 && device.getVendorId() == VENDORID)) {
                        if (!mUsbManager.hasPermission(device)) {
                            break;
                        }
                        blnRtn = mUsbDriver.usbAttached(device);
                        if (blnRtn == false) {
                            break;
                        }
                        blnRtn = mUsbDriver.openUsbDevice(device);

                        if (blnRtn) { // 打开设备
                            if (device.getProductId() == PID11) {
                                mUsbDev1 = device;
                            } else {
                                mUsbDev2 = device;
                            }
                            mUsbDriver.write(PrintCmd.SetClean());  //清除缓存,初始化
                            Log.i("Printer", "打印机初始化成功！");
                            break;
                        } else { //失败
                            Log.e("Printer", "打印机初始化失败！");
                            break;
                        }
                    }
                }
            } else {
                blnRtn = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return blnRtn;
    }

    private int[] getPixelsByBitmap(Bitmap bm) {
        int width, height;
        width = bm.getWidth();
        height = bm.getHeight();
        int iDataLen = width * height;
        int[] pixels = new int[iDataLen];
        bm.getPixels(pixels, 0, width, 0, 0, width, height);
        return pixels;
    }

    public void printBitmap(Bitmap bitmap) {
        try {
            if (checkPrintStatus()) {
                int[] data = getPixelsByBitmap(bitmap);
                mUsbDriver.write(PrintCmd.SetClean());           //清除缓存,初始化
                mUsbDriver.write(PrintCmd.PrintDiskImagefile(data, bitmap.getWidth(), bitmap.getHeight()));
                mUsbDriver.write(PrintCmd.PrintFeedline(7));  // 走纸换行
                mUsbDriver.write(PrintCmd.PrintCutpaper(0));  // 切纸类型(0全切、1半切)
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
