package com.starway.starrobot.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by iBelieve on 2018/5/10.
 */

public class CardPrinterUtil {

    private SharedPreferences preferences;
    private BitmapPrinter printer;
    private SimpleDateFormat dateFormat;
    private Context context;
    private boolean running = false;
    private int index = 1;
    private int count = 1;

    private static CardPrinterUtil instance;


    public static CardPrinterUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (CardPrinterUtil.class) {
                if (instance == null) {
                    instance = new CardPrinterUtil(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    private CardPrinterUtil(Context context) {
        this.context = context;
        this.printer = BitmapPrinter.getInstance();
        this.preferences = Common.getPreferences(context);
        this.count = preferences.getInt("print_count", 1);
        this.index = preferences.getInt("print_index", 1);
        this.dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
    }

    /**
     * 打印纪念卡片
     * @param name 姓名
     */
    public boolean printCard(String name) {
        try {
            if (running) {
                return false;
            }
            running = true;

            Bitmap bitmap = BitmapFactory.decodeStream(context.getAssets().open("card/" + index + ".png"))
                    .copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            //姓名外边框
            paint.setAntiAlias(true);
            paint.setColor(Color.WHITE);
            paint.setFakeBoldText(true);
            paint.setTextSize(48);
            paint.setStrokeWidth(11);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            canvas.drawText(name, 50, 540, paint);
            //姓名文字
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(0);
            canvas.drawText(name, 50, 540, paint);
            //编号
            paint.reset();
            paint.setAntiAlias(true);
            paint.setTextSize(25);
            paint.setFakeBoldText(true);
            paint.setColor(Color.BLACK);
            paint.setTextAlign(Paint.Align.RIGHT);
            paint.setTextScaleX(1.1f);
            canvas.drawText("No." + String.format("%08d", count), 970, 50, paint);
            //日期
            paint.setTextSize(19);
            paint.setTextScaleX(1);
            String nowDate = dateFormat.format(new Date());
            canvas.drawText(nowDate, 970, 85, paint);
            //分割线
            paint.setStrokeWidth(3);
            paint.setStrokeCap(Paint.Cap.ROUND);
            canvas.drawLine(970, 60, 780, 60, paint);
            //选择并打印图片
            Bitmap rotateBitmap = rotateBitmap(bitmap, 90);
            printer.printBitmap(rotateBitmap);
            rotateBitmap.recycle();
            //切换下一张图片，保存当前编号
            index = index >= 6 ? 1 : index + 1;
            preferences.edit().putInt("print_index", index).apply();
            preferences.edit().putInt("print_count", ++count).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
        running = false;
        return true;
    }

    /**
     * 选择 Bitmap（处理结束后会把输入图像释放掉）
     * @param origin 输入图片
     * @param degree 选择角度
     * @return
     */
    private Bitmap rotateBitmap(Bitmap origin, float degree) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(degree);
        Bitmap newBitmap = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBitmap.equals(origin)) {
            return newBitmap;
        }
        origin.recycle();
        return newBitmap;
    }

}
