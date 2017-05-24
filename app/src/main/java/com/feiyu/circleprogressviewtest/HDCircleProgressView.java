package com.feiyu.circleprogressviewtest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by 李鸿达 on 2017/5/22.
 */

public class HDCircleProgressView extends View {

    private int mMaxProgress = 90 * 10;//90个100ms
    private int mProgress = 0;
    private final int mCircleLineStrokeWidth = 22;
    private final int mTxtStrokeWidth = 2;

    private final RectF mRectF;
    private final Paint mPaint;
    private final Paint mPaintHas;
    private final Paint mTextPaint;
    private final Context ctx;

    private String mTxtHint1;

    private int hasMax = 0;

    public HDCircleProgressView(Context context) {
        super(context, null);
        ctx = context;
        mRectF = new RectF();
        mPaint = new Paint();
        mPaintHas = new Paint();
        mTextPaint = new Paint();
    }

    public HDCircleProgressView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        ctx = context;
        mRectF = new RectF();
        mPaint = new Paint();
        mPaintHas = new Paint();
        mTextPaint = new Paint();
    }

    public HDCircleProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ctx = context;
        mRectF = new RectF();
        mPaint = new Paint();
        mPaintHas = new Paint();
        mTextPaint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = this.getWidth();
        int height = this.getHeight();

        if (width != height) {
            int min = Math.min(width, height);
            width = min;
            height = min;
        }
        canvas.drawColor(Color.TRANSPARENT);

        //设置  当前进度画笔
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.rgb(0xe9, 0xe9, 0xe9));
        mPaint.setStrokeWidth(mCircleLineStrokeWidth);
        mPaint.setStyle(Paint.Style.STROKE);

        mPaintHas.setAntiAlias(true);
        mPaintHas.setColor(Color.rgb(0xe9, 0xe9, 0xe9));
        mPaintHas.setStrokeWidth(mCircleLineStrokeWidth);
        mPaintHas.setStyle(Paint.Style.STROKE);

        //位置
        mRectF.left = mCircleLineStrokeWidth / 2;
        mRectF.top = mCircleLineStrokeWidth / 2;
        mRectF.right = width - mCircleLineStrokeWidth / 2;
        mRectF.bottom = height - mCircleLineStrokeWidth / 2;

        //绘制圆圈，进度条背景
        //todo 绘制整个圆圈灰色背景
        canvas.drawArc(mRectF, -90, 360, false, mPaint);


        //todo 绘制进度背景
        mPaint.setColor(Color.GREEN);
        mPaintHas.setColor(Color.GRAY);

        //绘制当前进度1
        if (hasMax <= mProgress) {
            canvas.drawArc(mRectF, -90, ((float) mProgress / mMaxProgress) * 360, false, mPaintHas);
            hasMax = mProgress;
        } else {
            canvas.drawArc(mRectF, -90, ((float) hasMax / mMaxProgress) * 360, false, mPaintHas);
        }



        //绘制当前进度2
        canvas.drawArc(mRectF, -90, ((float) mProgress / mMaxProgress) * 360, false, mPaint);

        //绘制进度文字
        drawText(canvas,width,height);


        canvas.save();

        //画布移动到中心点
        canvas.translate(width / 2, height / 2);

        mPaint.setColor(Color.GREEN);
        //计算圆的位置
        int radius = width / 2 - mCircleLineStrokeWidth / 2;//绘制圆的半径等于矩形的宽度 - 线的一半宽度
        double angle = 1.0f * mProgress / mMaxProgress * Math.PI * 2;
        //当前的进度/总进度*360=当前的角度   当前的进度/总进度*2PI=当前的弧度
        float circleX = (float) (radius * Math.cos(angle - Math.PI / 2));
        float circleY = (float) (radius * Math.sin(angle - Math.PI / 2));//从-90°开始计算角度
        //计算圆的半径
        int circleRadius = mCircleLineStrokeWidth / 2;


        double angle2 = 1.0f * hasMax / mMaxProgress * Math.PI * 2;
        //当前的进度/总进度*360=当前的角度   当前的进度/总进度*2PI=当前的弧度
        float circleY2 = (float) (radius * Math.sin(angle2 - Math.PI / 2));//从-90°开始计算角度
        float circleX2 = (float) (radius * Math.cos(angle2 - Math.PI / 2));


        //绘制末点的圆
        mPaintHas.setStyle(Paint.Style.FILL);
        canvas.drawCircle(circleX2, circleY2, circleRadius, mPaintHas);






        mPaint.setStyle(Paint.Style.FILL);
        //绘制末点的圆
        canvas.drawCircle(circleX, circleY, circleRadius, mPaint);
        //绘制初始点的圆
        canvas.drawCircle(0, -radius, circleRadius, mPaint);


        canvas.restore();

    }

    private void drawText(Canvas canvas,int width,int height) {
        //todo 进度文字的颜色
        mTextPaint.setColor(Color.GREEN);
        mTextPaint.setStrokeWidth(mTxtStrokeWidth);
        String text = mProgress / 10 + "s";
        int textHeight = height / 4;
        mTextPaint.setTextSize(textHeight);
        int textWidth = (int) mTextPaint.measureText(text, 0, text.length());
        mTextPaint.setStyle(Paint.Style.FILL);
        canvas.drawText(text, width / 2 - textWidth / 2, height / 2, mTextPaint);

        if (!TextUtils.isEmpty(mTxtHint1)) {
            mTextPaint.setStrokeWidth(mTxtStrokeWidth);
            text = mTxtHint1;
            textHeight = height / 16;
            mTextPaint.setTextSize(textHeight);
            //todo 提示文字的颜色
            mTextPaint.setColor(Color.rgb(0x99, 0x99, 0x99));
            textWidth = (int) mTextPaint.measureText(text, 0, text.length());
            mTextPaint.setStyle(Paint.Style.FILL);
            canvas.drawText(text, width / 2 - textWidth / 2, 3 * height / 5 + textHeight / 2, mTextPaint);
        }
    }


    public void setProgressNotInUiThread(int progress) {
        this.mProgress = progress;
        this.postInvalidate();
    }

    public int getmMaxProgress() {
        return mMaxProgress;
    }

    public void setmMaxProgress(int mMaxProgress) {
        this.mMaxProgress = mMaxProgress;
    }

    public int getmProgress() {
        return mProgress;
    }

    public void setmProgress(int mProgress) {
        this.mProgress = mProgress;
    }

    public String getmTxtHint1() {
        return mTxtHint1;
    }

    public void setmTxtHint1(String mTxtHint1) {
        this.mTxtHint1 = mTxtHint1;
        this.invalidate();
    }

    public void clear(){
        hasMax = 0;
        mProgress = 0;
        this.invalidate();
    }
}