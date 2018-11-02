package com.sty.viewpager.tab.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;

import com.sty.viewpager.tab.R;

/**
 * Created by tian on 2018/11/1.
 */

public class CustomTabSliding extends View {
    private float MULTIPLE_RADIUS = 4/3f;
    private Context mContext;

    private Paint mPaint;
    private Paint mTranPaint;
    private Paint mFirstTextPaint;
    private Paint mSecondTextPaint;
    private int startX;
    private int startY;

    private int circleDefColor;
    private int circleSelColor;
    private int lineDefColor;
    private int lineSelColor;
    private int firstTextColor;
    private int secondTextColor;

    private int screenWidth;
    private int lineHeight;
    private int lineLength;
    private int circleRadius;
    private int textLength;

    private int number;
    private int curCircle; //圆的位置索引（从0开始） -->当前等级
    private int curPage; //当前的page（从1开始）
    private float curPercent;

    private String[] firstStrings;
    private String[] secondStrings;
    private int firstTextSize;
    private int secondTextSize;
    private int circleDiameter;

    private int horizontalMarginX = 32; //dp
    private int horizontalMarginY = 32; //dp

    private RectF rectF;
    private RectF textRectF;

    public CustomTabSliding(Context context) {
        this(context, null);
    }

    public CustomTabSliding(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomTabSliding(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;

        TypedArray ta = mContext.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomTabSliding, defStyleAttr, 0);

        circleDefColor = ta.getColor(R.styleable.CustomTabSliding_circleDefColor, Color.parseColor("#FFFFFF"));
        circleSelColor = ta.getColor(R.styleable.CustomTabSliding_circleSelColor, Color.parseColor("#5BBE72"));
        lineDefColor = ta.getColor(R.styleable.CustomTabSliding_lineDefColor, Color.parseColor("#FFFFFF"));
        lineSelColor = ta.getColor(R.styleable.CustomTabSliding_lineSelColor, Color.parseColor("#5BBE72"));
        firstTextColor = ta.getColor(R.styleable.CustomTabSliding_firstTextColor, Color.parseColor("#FFFFFF"));
        secondTextColor = ta.getColor(R.styleable.CustomTabSliding_secondTextColor, Color.parseColor("#5BBE72"));

        screenWidth = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
        //横向线宽
        lineHeight = ta.getDimensionPixelSize(R.styleable.CustomTabSliding_lineHeight, sp2px(mContext, 7));
        circleRadius = ta.getDimensionPixelSize(R.styleable.CustomTabSliding_circleRadius, sp2px(mContext, 10));
        textLength = ta.getDimensionPixelSize(R.styleable.CustomTabSliding_textLength, sp2px(mContext, 120));

        number = ta.getInt(R.styleable.CustomTabSliding_number, 2);
        curCircle = ta.getInt(R.styleable.CustomTabSliding_curCircle, 0);
        curPage = ta.getInt(R.styleable.CustomTabSliding_curPage, 0);
        curPercent = ta.getFloat(R.styleable.CustomTabSliding_curPercent, 0);

        firstTextSize = ta.getDimensionPixelSize(R.styleable.CustomTabSliding_firstTextSize, sp2px(mContext, 14));
        secondTextSize = ta.getDimensionPixelSize(R.styleable.CustomTabSliding_secondTextSize, sp2px(mContext, 14));

        ta.recycle();  //回收

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mTranPaint = new Paint();
        mTranPaint.setColor(Color.TRANSPARENT);
        mTranPaint.setAntiAlias(true);
        mFirstTextPaint = new Paint();
        mFirstTextPaint.setAntiAlias(true);
        mSecondTextPaint = new Paint();
        mSecondTextPaint.setAntiAlias(true);

        rectF = new RectF();
        textRectF = new RectF();
        circleDiameter = 2 * circleRadius;
        startX = circleRadius + sp2px(context, horizontalMarginX);
        startY = circleRadius + sp2px(context, horizontalMarginY);

        //线长根据屏幕宽度自动计算
        lineLength = screenWidth / 2 - startX;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //底线
        rectF.left = startX;
        rectF.top = startY - lineHeight / 2;
        rectF.right = startX + lineLength * (number - 1);
        rectF.bottom = startY + lineHeight / 2;

        mPaint.setColor(lineDefColor);
        canvas.drawRect(rectF, mPaint);

        //选中线
        if(curPage - curCircle == 1){                                     //展示最后的不满一级的进度时以去掉两端半圆的中间矩形的长度为进度条的总长度计算
            rectF.right = startX + lineLength * curCircle + circleRadius + (lineLength - circleRadius * (MULTIPLE_RADIUS + 1)) * curPercent;
        }else {
            rectF.right = startX + lineLength * curCircle + circleRadius + (lineLength - circleRadius * 2) * curPercent;
        }
        mPaint.setColor(lineSelColor);
        canvas.drawRect(rectF, mPaint);

        //画圆点和圆点下方的字
        mFirstTextPaint.setColor(firstTextColor);
        mSecondTextPaint.setColor(secondTextColor);
        for(int i = 0; i < number; i++){
            if(i <= curCircle){ //达到等级的圆点
                mPaint.setColor(circleSelColor);
                canvas.drawCircle(startX + i * lineLength, startY, circleRadius, mPaint);
            }else { //未达到等级的圆点
                mPaint.setColor(circleDefColor);
                canvas.drawCircle(startX + i * lineLength, startY, circleRadius, mPaint);
            }
            //字
            //first Text
            textRectF.left = startX + i * lineLength - 2 * circleDiameter;
            textRectF.top = startY + circleDiameter;
            textRectF.right = startX + i * lineLength - 2 * circleDiameter + textLength;
            textRectF.bottom = startY + circleDiameter + circleRadius;
            canvas.drawRect(textRectF, mTranPaint);
            mFirstTextPaint.setTextSize(firstTextSize);
            if(firstStrings != null && i < firstStrings.length){
                //坐标：左下角
                canvas.drawText(firstStrings[i], textRectF.left, textRectF.bottom, mFirstTextPaint);
            }

            //second Text
            textRectF.left = startX + i * lineLength - circleDiameter - circleRadius * 1.1f;
            textRectF.top = startY + 2 * circleDiameter;
            textRectF.right = startX + i * lineLength - circleDiameter - circleRadius * 1.1f + textLength;
            textRectF.bottom = startY + 2 * circleDiameter + circleRadius;
            canvas.drawRect(textRectF, mTranPaint);
            mSecondTextPaint.setTextSize(secondTextSize);
            if (secondStrings != null && i < secondStrings.length) {
                //坐标：左下角
                canvas.drawText(secondStrings[i], textRectF.left, textRectF.bottom, mSecondTextPaint);
            }

            if(i == curPage){
                //选中圆点-->加大
                if(curPage > curCircle){
                    mPaint.setColor(circleDefColor);
                }else{
                    mPaint.setColor(circleSelColor);
                }
                canvas.drawCircle(startX + i * lineLength, startY, circleRadius * MULTIPLE_RADIUS, mPaint);
            }
        }
    }

    private int sp2px(Context context, float spValue){
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public int getCircleDefColor() {
        return circleDefColor;
    }

    public void setCircleDefColor(int circleDefColor) {
        this.circleDefColor = circleDefColor;
    }

    public int getCircleSelColor() {
        return circleSelColor;
    }

    public void setCircleSelColor(int circleSelColor) {
        this.circleSelColor = circleSelColor;
    }

    public int getLineDefColor() {
        return lineDefColor;
    }

    public void setLineDefColor(int lineDefColor) {
        this.lineDefColor = lineDefColor;
    }

    public int getLineSelColor() {
        return lineSelColor;
    }

    public void setLineSelColor(int lineSelColor) {
        this.lineSelColor = lineSelColor;
    }

    public int getFirstTextColor() {
        return firstTextColor;
    }

    public void setFirstTextColor(int firstTextColor) {
        this.firstTextColor = firstTextColor;
    }

    public int getSecondTextColor() {
        return secondTextColor;
    }

    public void setSecondTextColor(int secondTextColor) {
        this.secondTextColor = secondTextColor;
    }

    public int getLineHeight() {
        return lineHeight;
    }

    public void setLineHeight(int lineHeight) {
        this.lineHeight = lineHeight;
    }

    public int getLineLength() {
        return lineLength;
    }

    public void setLineLength(int lineLength) {
        this.lineLength = lineLength;
    }

    public int getCircleRadius() {
        return circleRadius;
    }

    public void setCircleRadius(int circleRadius) {
        this.circleRadius = circleRadius;
    }

    public int getTextLength() {
        return textLength;
    }

    public void setTextLength(int textLength) {
        this.textLength = textLength;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getCurCircle() {
        return curCircle;
    }

    public void setCurCircle(int curCircle) {
        this.curCircle = curCircle;
    }

    public int getCurPage() {
        return curPage;
    }

    public void setCurPage(int curPage) {
        this.curPage = curPage;
        invalidate();
    }

    public float getCurPercent() {
        return curPercent;
    }

    public void setCurPercent(float curPercent) {
        this.curPercent = curPercent;
    }

    public String[] getFirstStrings() {
        return firstStrings;
    }

    public void setFirstStrings(String[] firstStrings) {
        this.firstStrings = firstStrings;
    }

    public String[] getSecondStrings() {
        return secondStrings;
    }

    public void setSecondStrings(String[] secondStrings) {
        this.secondStrings = secondStrings;
    }

    public int getFirstTextSize() {
        return firstTextSize;
    }

    public void setFirstTextSize(int firstTextSize) {
        this.firstTextSize = firstTextSize;
    }

    public int getSecondTextSize() {
        return secondTextSize;
    }

    public void setSecondTextSize(int secondTextSize) {
        this.secondTextSize = secondTextSize;
    }

    public int getCircleDiameter() {
        return circleDiameter;
    }

    public void setCircleDiameter(int circleDiameter) {
        this.circleDiameter = circleDiameter;
    }
}
