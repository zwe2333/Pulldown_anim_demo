package com.zwe.pulldown_anim_demo.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Asus on 2017/8/1.
 */

public class BezierView extends View{
    public BezierView(Context context) {
        super(context);
        init();
    }

    public BezierView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BezierView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public BezierView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private Path mBezierPath=new Path();

    private Paint mPaint=new Paint(Paint.ANTI_ALIAS_FLAG);

    private void init() {
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5);

        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        initBezier();//初始化贝塞尔四阶~n阶
                    }
                }
        ).start();

    }

    private void initBezier() {
        float[] xPoints=new float[]{0,200,500,700,800,500,600,200};
        float[] yPoints=new float[]{0,700,1200,200,800,1300,600,1000};


        int fps=10000;

        Path path=mBezierPath;

        for (int i=0;i<fps;i++){

            float progress=i/(float)fps;//进度

            float x = calculateBezier(progress,xPoints);

            float y = calculateBezier(progress,yPoints);

            postInvalidate();

            path.lineTo(x,y);

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 计算某时刻的贝塞尔的值(x or y)
     * @param t 时间(0~1)
     * @param values 返回当前t时刻贝塞尔所处点
     * @return
     */
    private float calculateBezier(float t,float... values){

        final int len=values.length;

        for (int i=len-1;i>0;i--){
            for (int j=0;j<i;j++){
                values[j] = values[j] + ( values[j+1] - values[j] ) * t;
            }
        }
        return values[0];//运算时结果保存在第一位
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(mBezierPath,mPaint);
    }
}
