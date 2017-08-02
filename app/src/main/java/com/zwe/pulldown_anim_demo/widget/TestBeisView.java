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

public class TestBeisView extends View{
    private final Paint mPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path mPath=new Path();

    public TestBeisView(Context context) {
        super(context);
        init();
    }

    public TestBeisView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TestBeisView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5);

        //一阶贝塞尔曲线
        Path path=mPath;
        path.moveTo(100,100);
        path.lineTo(400,400);

        //二阶贝塞尔曲线
        path.quadTo(600,100,800,400);

        path.moveTo(400,800);

        //三阶贝塞尔曲线
        path.cubicTo(500,600,700,1200,800,800);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(mPath,mPaint);
        canvas.drawPoint(500,0,mPaint);
        canvas.drawPoint(500,600,mPaint);
        canvas.drawPoint(700,1200,mPaint);
    }
}
