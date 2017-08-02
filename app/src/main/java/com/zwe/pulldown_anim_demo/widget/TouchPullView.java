package com.zwe.pulldown_anim_demo.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.view.animation.PathInterpolatorCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;

import com.zwe.pulldown_anim_demo.R;

/**
 * Created by Asus on 2017/8/1.
 */

public class TouchPullView extends View{

    private Paint mCircle;//圆的画笔

    private float mCircleRadius=50;//圆的半径

    private float mCirclePointX,mCirclePointY;

    private float mProgress;//进度值

    private int mDragHeight=300;//可拖动高度

    private int mTargetWidth=400;//目标宽度

    //贝塞尔曲线的路径和画笔
    private Path mPath=new Path();
    private Paint mPathPaint;

    private int mTargetGravityHeight=10;//重心点高度，决定控制点的y

    private int mTargetAngle=105;//角度变换0~120


    private Interpolator mProgressInterpolator=new DecelerateInterpolator();

    private Interpolator mTanenAngleInterpolator;

    private Drawable mContent=null;
    private int mContentMargin=0;

    public TouchPullView(Context context) {
        super(context);
        init(null);
    }

    public TouchPullView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public TouchPullView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public TouchPullView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    /*
    *
    * 初始化画笔
    * */
    private void init(AttributeSet attrs) {
        //得到用户设置的参数
        final Context context=getContext();
        TypedArray array=context.obtainStyledAttributes(attrs,R.styleable.TouchPullView,0,0);
        int color=array.getColor(R.styleable.TouchPullView_pColor,0x20000000);
        mCircleRadius=array.getDimension(R.styleable.TouchPullView_pRadius,mCircleRadius);
        mDragHeight=array.getDimensionPixelOffset(R.styleable.TouchPullView_pDragHeight,mDragHeight);
        mTargetAngle=array.getInteger(R.styleable.TouchPullView_pTangenAngle,100);
        mTargetWidth=array.getDimensionPixelOffset(R.styleable.TouchPullView_pTargetWidth,mTargetWidth);
        mTargetGravityHeight=array.getDimensionPixelOffset(R.styleable.TouchPullView_pTargetGravityHeight,mTargetGravityHeight);
        mContent=array.getDrawable(R.styleable.TouchPullView_pContentDrawable);
        mContentMargin=array.getDimensionPixelOffset(R.styleable.TouchPullView_pContentDrawableMargin,0);

        array.recycle();

        Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setAntiAlias(true);//抗锯齿
        p.setDither(true);//防抖动
        p.setStyle(Paint.Style.FILL);//填充
        p.setColor(0xFFFF4081);
        mCircle=p;


        p=new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setAntiAlias(true);//抗锯齿
        p.setDither(true);//防抖动
        p.setStyle(Paint.Style.FILL);//填充
        p.setColor(0xFFFF4081);
        mPathPaint=p;

        //切角路劲差值器
        mTanenAngleInterpolator= PathInterpolatorCompat.create(
                (mCircleRadius*2.0f)/mDragHeight,
                90.0f/mTargetAngle
        );
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //进行基础坐标参数系改变
        int count=canvas.save();
        float tranX=(getWidth()-getValueByLine(getWidth(),mTargetWidth,mProgress))/2;
        canvas.translate(tranX,0);

        //画贝塞尔曲线
        canvas.drawPath(mPath,mPathPaint);

        //画圆
        canvas.drawCircle(mCirclePointX,mCirclePointY,mCircleRadius,mCircle);

        Drawable drawable=mContent;
        if (drawable!=null){
            canvas.save();
            //剪切矩形区域
            canvas.clipRect(drawable.getBounds());
            //绘制drawable
            drawable.draw(canvas);
            canvas.restore();
        }

        canvas.restoreToCount(count);

    }

    /**
     * 当进行测量时候触发
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);


        int widthMode=MeasureSpec.getMode(widthMeasureSpec);//宽度的类型
        int width=MeasureSpec.getSize(widthMeasureSpec);

        int heightMode=MeasureSpec.getMode(heightMeasureSpec);//高度的类型
        int height=MeasureSpec.getSize(heightMeasureSpec);

        int iHeight= (int) ((mDragHeight*mProgress+0.5f)+getPaddingTop()+getPaddingBottom());
        int iWidth= (int) (2*mCircleRadius+getPaddingLeft()+getPaddingRight());

        int measureWidth,measureHeight;

        if (widthMode==MeasureSpec.EXACTLY){
            //等于确切值
            measureWidth=width;
        }else if (widthMode==MeasureSpec.AT_MOST){
            //等于最多的
            measureWidth=Math.min(iWidth,width);
        }else {
            measureWidth=iWidth;
        }

        if (heightMode==MeasureSpec.EXACTLY){
            //等于确切值
            measureHeight=height;
        }else if (heightMode==MeasureSpec.AT_MOST){
            //等于最多的
            measureHeight=Math.min(iHeight,height);
        }else {
            measureHeight=iHeight;
        }

        //设置宽高
        setMeasuredDimension(measureWidth,measureHeight);

    }

    /**
     *
     * 当大小改变时候触发
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //super.onSizeChanged(w, h, oldw, oldh);
        /*mCirclePointX=getWidth()>>1;
        mCirclePointY=getHeight()>>1;*/
        updatePathLayout();//当高度变化时进行路径更新
    }

    /*
            * 设置进度
            *
            * */
    public void setProgress(float progress){
        //System.out.println(progress);
        mProgress=progress;
        requestLayout();//请求从新进行测量
    }

    /**
     * 更新路径
     */
    private void updatePathLayout(){
        final float progress=mProgressInterpolator.getInterpolation(mProgress);//获取进度

        //获取可绘制区域宽高
        final float w=getValueByLine(getWidth(),mTargetWidth,mProgress);
        final float h=getValueByLine(0,mDragHeight,mProgress);

        final float cPointx=w/2.0f;//中心对称轴 圆心x
        final float cRadius=mCircleRadius;//圆的半径
        final float cPointy=h-cRadius;//中心对称轴 圆心y

        final float endControlY=mTargetGravityHeight;//控制点y值


        //更新圆的坐标
        mCirclePointX=cPointx;
        mCirclePointY=cPointy;

        final Path path=mPath;
        //复位操作
        path.reset();
        path.moveTo(0,0);

        //左边部分，结束点和控制点
        float lEndPointX,lEndPointY;
        float lControlPointX,lControlPointY;

        //获取当前切线弧度
        float angle=mTargetAngle*mTanenAngleInterpolator.getInterpolation(progress);
        double radian=Math.toRadians(angle);
        float x= (float) (Math.sin(radian)*cRadius);
        float y= (float) (Math.cos(radian)*cRadius);

        lEndPointX=cPointx-x;
        lEndPointY=cPointy+y;

        //控制点y的变化
        lControlPointY=getValueByLine(0,endControlY,progress);
        //控制点与结束点之间y的高度
        float tHeight=lEndPointY-lControlPointY;
        //控制点与x坐标距离
        float tWidth= (float) (tHeight/Math.tan(radian));

        lControlPointX=lEndPointX-tWidth;

        //贝塞尔曲线
        path.quadTo(lControlPointX,lControlPointY,lEndPointX,lEndPointY);
        //连接到右边
        path.lineTo(cPointx+(cPointx-lEndPointX),lEndPointY);
        //画右边的曲线
        path.quadTo(cPointx+(cPointx-lControlPointX),lControlPointY,w,0);

        //更新内容部分drawable
        updateContentLayout(cPointx,cPointy,cRadius);

    }

    /**
     * 对内容部分进行测量并设置
     * @param cx
     * @param cy
     * @param radius
     */
    private void updateContentLayout(float cx, float cy,float radius){
        Drawable drawable=mContent;
        if (drawable!=null){
            int margin=mContentMargin;
            int l= (int) (cx-radius+margin);
            int r= (int) (cx+radius-margin);
            int t= (int) (cy-radius+margin);
            int b= (int) (cy+radius-margin);
            drawable.setBounds(l,t,r,b);
        }
    }


    /**
     * 获取当前值
     * @param start
     * @param end
     * @param progress
     * @return
     */
    private float getValueByLine(float start,float end,float progress){
        return start+(end+start)*progress;
    }

    private ValueAnimator valueAnimator;

    public void release(){
        if (valueAnimator==null){
            final ValueAnimator animator=ValueAnimator.ofFloat(mProgress,0f);
            animator.setInterpolator(new DecelerateInterpolator());
            animator.setDuration(400);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    Object val=animator.getAnimatedValue();
                    if (val instanceof Float){
                        setProgress((Float) val);
                    }
                }
            });
            valueAnimator=animator;
        }else {
            valueAnimator.cancel();
            valueAnimator.setFloatValues(mProgress,0f);
        }
        valueAnimator.start();
    }

}
