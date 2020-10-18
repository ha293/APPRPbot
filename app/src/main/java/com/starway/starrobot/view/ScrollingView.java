package com.starway.starrobot.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 自定义支持滑动的view
 * Created by iBelieve on 2018/5/4.
 */
public class ScrollingView extends View {

    public static final int ACTION_CLICK = -1; //点击
    public static final int ACTION_DOUBLE_CLICK = -2; //双击
    public static final int ACTION_SCROLL_LEFT = 0; //向左滑动
    public static final int ACTION_SCROLL_RIGHT = 1; //向右滑动
    public static final int ACTION_SCROLL_TOP = 2; //向上滑动
    public static final int ACTION_SCROLL_BOTTOM = 4; //向下滑动

    private float scrollParameter = 0.5f;
    private float scrollDx = 10;
    private float scrollDy = 10;
    private onScrollingListener listener;
    private Handler handler = new Handler();

    private float preX = 0;
    private float preY = 0;
    private boolean flag = true;
    private long lastClickTime;
    private int scrollMode = 0;
    private int width;
    private int height;

    public ScrollingView(Context context) {
        super(context);
    }

    public ScrollingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.width = getMeasuredWidth();
        this.height = getMeasuredHeight();
        calVal();
    }

    private void calVal() {
        this.scrollDx = scrollParameter * width;
        this.scrollDy = scrollParameter * height;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (listener != null) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    preX = event.getX();
                    preY = event.getY();
                    flag = true;
                    scrollMode = -1;
                    break;
                case MotionEvent.ACTION_MOVE:
                    float x = event.getX();
                    float y = event.getY();
                    float dx = Math.abs(preX - x);
                    float dy = Math.abs(preY - y);

                    if (scrollMode < 0) {
                        scrollMode = dx > dy ? 0 : 1;
                    }

                    switch (scrollMode) {
                        case 0:
                            if (dx > scrollDx) {
                                if (x - preX < 0) {
                                    listener.onTouchEvent(ACTION_SCROLL_LEFT);
                                } else {
                                    listener.onTouchEvent(ACTION_SCROLL_RIGHT);
                                }
                                flag = false;
                                preX = x;
                            }
                            break;
                        case 1:
                            if (dy > scrollDy) {
                                if (y - preY < 0) {
                                    listener.onTouchEvent(ACTION_SCROLL_TOP);
                                } else {
                                    listener.onTouchEvent(ACTION_SCROLL_BOTTOM);
                                }
                                flag = false;
                                preY = y;
                            }
                            break;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (flag) {
                        long currentClickTime = System.currentTimeMillis();
                        if (currentClickTime - lastClickTime < 300) {
                            handler.removeCallbacks(clickRunnable);
                            listener.onTouchEvent(ACTION_DOUBLE_CLICK);
                        } else {
                            handler.postDelayed(clickRunnable, 290);
                        }

                        flag = false;
                        lastClickTime = currentClickTime;
                        return super.onTouchEvent(event);
                    }
                    return false;
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

    private Runnable clickRunnable = new Runnable() {
        @Override
        public void run() {
            if (listener != null) {
                listener.onTouchEvent(ACTION_CLICK);
            }
        }
    };

    /**
     * 设置滑动响应速度 (0<level<=1)
     *
     * @param level
     * @return
     */
    public ScrollingView setScrollSpeed(float level) {
        level = level <= 0 ? 0.1f : level > 1 ? 1 : level;
        this.scrollParameter = (1.01f - level) / 10f;
        if (width != 0 && height != 0) {
            calVal();
        }
        return this;
    }

    /**
     * 分块设置法 (|block|block|block|block|block|)
     *
     * @param size
     * @return
     */
    public ScrollingView setScrollBlockSize(int size) {
        this.scrollParameter = 1.0f / size;
        if (width != 0 && height != 0) {
            calVal();
        }
        return this;
    }

    /**
     * 设置View滑动时的相应事件
     *
     * @param listener
     * @return
     */
    public ScrollingView setOnScrollingListener(onScrollingListener listener) {
        this.listener = listener;
        return this;
    }

    public interface onScrollingListener {
        void onTouchEvent(int t);
    }

}
