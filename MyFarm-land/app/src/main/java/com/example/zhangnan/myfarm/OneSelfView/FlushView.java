package com.example.zhangnan.myfarm.OneSelfView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

/**
 * Created by zhangnan on 17/6/8.
 */

public class FlushView extends View {

    private int mWidth;
    private int mHeight;

    private Paint mPaint;
    private float mRadius = 50.0f;
    private float mCenterX;
    private float mCenterY;

    //移动圆参数
    private float moveCircleRadius;
    private float moveCircleCenterX;
    private float moveCircleCenterY;

    //直线函数参数
    private float a;
    private float b;

    private Path mPath;
    private Animation blueAnimator;

    private enum Status{
        blue , violet , yellow
    }
    private Status status = Status.blue;

    public FlushView(Context context) {
        super(context);
    }

    public FlushView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
        initAnimation();
        startAnimation(blueAnimator);
        invalidate();
    }

    public FlushView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    private void init(){
        // #673ab7 #cddc39
        mPath = new Path();
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.parseColor("#03a9f4"));
    }

    private void initData(){

        mCenterX = mWidth / 2;
        mCenterY = mHeight / 2;
        moveCircleRadius = (float) Math.sqrt(Math.pow(mRadius,2) * 2);
        moveCircleCenterY = (float) (mCenterY + Math.sqrt(Math.pow(mRadius,2) * 2));
        moveCircleCenterX = (float) (mCenterX + Math.sqrt(Math.pow(moveCircleRadius + mRadius,2) - Math.pow(mRadius,2) * 2));
        a = (moveCircleCenterY - mCenterY) / (moveCircleCenterX - mCenterX);
        b = ((moveCircleCenterY + mCenterY) - (moveCircleCenterY - mCenterY)*(mCenterX + moveCircleCenterX)/(moveCircleCenterX - mCenterX)) / 2;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        mWidth = widthSize;
        mHeight = heightSize;

        initData();
        super.onMeasure(MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (status == Status.blue){
            mPaint.setColor(Color.parseColor("#03a9f4"));
            canvas.drawCircle(mCenterX , mCenterY , mRadius , mPaint);
        }else if (status == Status.violet){
            mPaint.setColor(Color.parseColor("#673ab7"));
            canvas.drawCircle(mCenterX , mCenterY , mRadius , mPaint);
        }else if (status == Status.yellow){
            mPaint.setColor(Color.parseColor("#cddc39"));
            canvas.drawCircle(mCenterX , mCenterY , mRadius , mPaint);
        }

        mPaint.setColor(Color.parseColor("#ffffff"));
        canvas.drawCircle(moveCircleCenterX , moveCircleCenterY , moveCircleRadius , mPaint);

    }

    private void initAnimation(){
        blueAnimator = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                moveCircleCenterY = a * moveCircleCenterX + b;
                if (Math.abs(moveCircleCenterX - mCenterX)>60)
                {
                    moveCircleCenterX = moveCircleCenterX - 100 * interpolatedTime ;
                }
                invalidate();
            }
        };
        blueAnimator.setInterpolator(new LinearInterpolator());
        blueAnimator.setDuration(1000);
        blueAnimator.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (status == Status.blue){
                    status = Status.violet;
                    blueAnimator.cancel();
                    resetPosition();
                    startAnimation(blueAnimator);
                }else if (status == Status.violet){
                    status = Status.yellow;
                    blueAnimator.cancel();
                    resetPosition();
                    startAnimation(blueAnimator);
                }else if (status == Status.yellow){
                    status = Status.blue;
                    blueAnimator.cancel();
                    resetPosition();
                    startAnimation(blueAnimator);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void resetPosition(){
        initData();
        invalidate();
    }

    public void resetStatus(){
        status = Status.blue;
    }

}
