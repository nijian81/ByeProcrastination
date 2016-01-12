package com.jinlinkeji.byetuo.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by nijian on 2015/9/2.
 */
public class MyCustomView extends View {

    private RectF rectF;
    //依次代表购买日用品的起始角度、购买日用品持续的时间、....后面的一样
    private int buyBeginValue, buyDuringValue, learnBeginValue, learnDuringValue, fitnessBeginValue, fitnessDuringValue, meetBeginValue, meetDuringValue, walkBeginValue, walkDuringValue;

    public int getBuyBeginValue() {
        return buyBeginValue;
    }

    public void setBuyBeginValue(int buyBeginValue) {
        this.buyBeginValue = buyBeginValue;
    }

    public int getBuyDuringValue() {
        return buyDuringValue;
    }

    public void setBuyDuringValue(int buyDuringValue) {
        this.buyDuringValue = buyDuringValue;
    }

    public int getLearnBeginValue() {
        return learnBeginValue;
    }

    public void setLearnBeginValue(int learnBeginValue) {
        this.learnBeginValue = learnBeginValue;
    }

    public int getLearnDuringValue() {
        return learnDuringValue;
    }

    public void setLearnDuringValue(int learnDuringValue) {
        this.learnDuringValue = learnDuringValue;
    }

    public int getFitnessBeginValue() {
        return fitnessBeginValue;
    }

    public void setFitnessBeginValue(int fitnessBeginValue) {
        this.fitnessBeginValue = fitnessBeginValue;
    }

    public int getFitnessDuringValue() {
        return fitnessDuringValue;
    }

    public void setFitnessDuringValue(int fitnessDuringValue) {
        this.fitnessDuringValue = fitnessDuringValue;
    }

    public int getMeetBeginValue() {
        return meetBeginValue;
    }

    public void setMeetBeginValue(int meetBeginValue) {
        this.meetBeginValue = meetBeginValue;
    }

    public int getMeetDuringValue() {
        return meetDuringValue;
    }

    public void setMeetDuringValue(int meetDuringValue) {
        this.meetDuringValue = meetDuringValue;
    }

    public int getWalkBeginValue() {
        return walkBeginValue;
    }

    public void setWalkBeginValue(int walkBeginValue) {
        this.walkBeginValue = walkBeginValue;
    }

    public int getWalkDuringValue() {
        return walkDuringValue;
    }

    public void setWalkDuringValue(int walkDuringValue) {
        this.walkDuringValue = walkDuringValue;
    }

    public MyCustomView(Context context) {
        super(context);
    }

    public MyCustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyCustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init() {

        buyBeginValue = getBuyBeginValue();
        buyDuringValue = getBuyDuringValue();
        learnBeginValue = getLearnBeginValue();
        learnDuringValue = getLearnDuringValue();
        walkBeginValue = getWalkBeginValue();
        walkDuringValue = getWalkDuringValue();
        fitnessBeginValue = getFitnessBeginValue();
        fitnessDuringValue = getFitnessDuringValue();
        meetBeginValue = getMeetBeginValue();
        meetDuringValue = getMeetDuringValue();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        init();
        //初始化弧线的参数
        int leftX = this.getWidth() / 2 - 210;
        int rightX = this.getWidth() / 2 + 210;
        int upY = this.getHeight() / 2 - 210;
        int downY = this.getHeight() / 2 + 210;
        rectF = new RectF(leftX, upY, rightX, downY);

        //外围的圆形
        int width = this.getWidth();
        int height = this.getHeight();
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.rgb(220, 220, 220));
        //设置画笔为空心
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(30);
        canvas.drawCircle(width / 2, height / 2, 210, paint);

//        //画内侧的圆形
//        paint.setAntiAlias(true);
//        //绿色已进行
////        paint.setColor(Color.rgb(32, 126, 92));
//        //黑色未进行
//        paint.setColor(Color.rgb(68, 70, 83));
//        //设置画笔为空心
//        paint.setStyle(Paint.Style.FILL);
//        canvas.drawCircle(width / 2, height / 2, 135, paint);

        //画弧形购买日用品
        Paint paintArc = new Paint();
        paintArc.setAntiAlias(true);
        paintArc.setColor(Color.rgb(32, 126, 92));
        //设置画笔为空心
        paintArc.setStyle(Paint.Style.STROKE);
        paintArc.setStrokeWidth(30);
        canvas.drawArc(rectF, buyBeginValue, buyDuringValue, false, paintArc);

        //画弧形学习
        Paint paintArcLearn = new Paint();
        paintArcLearn.setAntiAlias(true);
        paintArcLearn.setColor(Color.rgb(249, 206, 3));
        //设置画笔为空心
        paintArcLearn.setStyle(Paint.Style.STROKE);
        paintArcLearn.setStrokeWidth(30);
        //关于arc中的两个角度是这样定义的，第一个代表起始角度，第二个代表结束角度
        canvas.drawArc(rectF, learnBeginValue, learnDuringValue, false, paintArcLearn);
        super.onDraw(canvas);

        //画弧形散步
        Paint paintArcWalk = new Paint();
        paintArcWalk.setAntiAlias(true);
        paintArcWalk.setColor(Color.rgb(156, 54, 242));
        //设置画笔为空心
        paintArcWalk.setStyle(Paint.Style.STROKE);
        paintArcWalk.setStrokeWidth(30);
        //关于arc中的两个角度是这样定义的，第一个代表起始角度，第二个代表结束角度
        canvas.drawArc(rectF, walkBeginValue, walkDuringValue, false, paintArcWalk);
        super.onDraw(canvas);

        //画弧形健身
        Paint paintArcFitness = new Paint();
        paintArcFitness.setAntiAlias(true);
        paintArcFitness.setColor(Color.rgb(255, 136, 132));
        //设置画笔为空心
        paintArcFitness.setStyle(Paint.Style.STROKE);
        paintArcFitness.setStrokeWidth(30);
        //关于arc中的两个角度是这样定义的，第一个代表起始角度，第二个代表结束角度
        canvas.drawArc(rectF, fitnessBeginValue, fitnessDuringValue, false, paintArcFitness);
        super.onDraw(canvas);

        //开会
        Paint paintArcMeet = new Paint();
        paintArcMeet.setAntiAlias(true);
        paintArcMeet.setColor(Color.rgb(55, 167, 243));
        //设置画笔为空心
        paintArcMeet.setStyle(Paint.Style.STROKE);
        paintArcMeet.setStrokeWidth(30);
        //关于arc中的两个角度是这样定义的，第一个代表起始角度，第二个代表结束角度
        canvas.drawArc(rectF, meetBeginValue, meetDuringValue, false, paintArcMeet);
        super.onDraw(canvas);
    }

    public void setBuyArc(int begin,int during){
        setBuyBeginValue(begin);
        setBuyDuringValue(during);
        invalidate();
    }

    public void setLearnArc(int begin,int during){
        setLearnBeginValue(begin);
        setLearnDuringValue(during);
        invalidate();
    }
    public void setWalkArc(int begin,int during){
        setWalkBeginValue(begin);
        setWalkDuringValue(during);
        invalidate();
    }
    public void setFitnessArc(int begin,int during){
        setFitnessBeginValue(begin);
        setFitnessDuringValue(during);
        invalidate();
    }
    public void setMeetArc(int begin,int during){
        setMeetBeginValue(begin);
        setMeetDuringValue(during);
        invalidate();
    }

}
