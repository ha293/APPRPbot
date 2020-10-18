package com.starway.starrobot.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by iBelieve on 2018/4/12.
 */

public class CircleTickView extends View {


    private PathMeasure tickPathMeasure;
    /**
     * 打钩百分比
     */
    float tickPercent = 0;

    private Path path;
    //初始化打钩路径
    private Path tickPath;


    // 圆圈的大小,半径
    private int circleRadius;
    private int circleColor;
    private int circleStrokeWidth;

    private RectF rec;
    private Paint tickPaint;


    public CircleTickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public CircleTickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CircleTickView(Context context) {
        super(context);
        init(context, null);
    }

    public void init(Context context, AttributeSet attrs) {

//        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleTickView);
//        circleRadius = mTypedArray.getInteger(R.styleable.CircleTickView_circleRadius, 150);
//        circleColor = mTypedArray.getColor(R.styleable.CircleTickView_circleViewColor, ContextCompat.getColor(context, R.color.colorPrimary));
//        circleStrokeWidth = mTypedArray.getInteger(R.styleable.CircleTickView_circleStrokeWidth, 20);
//        mTypedArray.recycle();
//
//        tickPaint = new Paint();
//        tickPathMeasure = new PathMeasure();
//        rec = new RectF();
//        path = new Path();
//        tickPath = new Path();
//        tickPaint.setStyle(Paint.Style.STROKE);
//        tickPaint.setAntiAlias(true);
//        tickPaint.setColor(circleColor);
//        tickPaint.setStrokeWidth(circleStrokeWidth);
//
//        //打钩动画
//        ValueAnimator mTickAnimation;
//        mTickAnimation = ValueAnimator.ofFloat(0f, 1f);
//        mTickAnimation.setStartDelay(1000);
//        mTickAnimation.setDuration(500);
//        mTickAnimation.setInterpolator(new AccelerateInterpolator());
//        mTickAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                tickPercent = (float) animation.getAnimatedValue();
//                invalidate();
//            }
//        });
//        mTickAnimation.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        // 根据设置该view的高度，进行对所画图进行居中处理
        int offsetHeight = (height - circleRadius * 2) / 2;

        // 设置第一条直线的相关参数
        int firStartX = width / 2 - circleRadius * 3 / 5;
        int firStartY = offsetHeight + circleRadius;

        int firEndX = (width / 2 - circleRadius / 5) - 1;
        int firEndY = offsetHeight + circleRadius + circleRadius / 2 + 1;


        int secEndX = width / 2 + circleRadius * 3 / 5;
        int secEndY = offsetHeight + circleRadius / 2;


        rec.set(width / 2 - circleRadius, offsetHeight, width / 2 + circleRadius, offsetHeight + circleRadius * 2);
        tickPath.moveTo(firStartX, firStartY);
        tickPath.lineTo(firEndX, firEndY);
        tickPath.lineTo(secEndX, secEndY);
        tickPathMeasure.setPath(tickPath, false);
        /*
         * On KITKAT and earlier releases, the resulting path may not display on a hardware-accelerated Canvas.
         * A simple workaround is to add a single operation to this path, such as dst.rLineTo(0, 0).
         */
        tickPathMeasure.getSegment(0, tickPercent * tickPathMeasure.getLength(), path, true);
        path.rLineTo(0, 0);
        canvas.drawPath(path, tickPaint);
        canvas.drawArc(rec, 0, 360, false, tickPaint);
    }


}