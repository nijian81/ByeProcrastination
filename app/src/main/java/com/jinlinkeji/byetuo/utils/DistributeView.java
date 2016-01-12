package com.jinlinkeji.byetuo.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.List;

/**
 * Created by nijian on 2015/9/2.
 */
public class DistributeView extends View {

    List<DistributeEntity> list;
    private int size;//圆点的个数
    int type, emergency, difficulty;
    String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public DistributeView(Context context) {
        super(context);
    }

    public DistributeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DistributeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getEmergency() {
        return emergency;
    }

    public void setEmergency(int emergency) {
        this.emergency = emergency;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public void init() {
        type = getType();
        emergency = getEmergency();
        difficulty = getDifficulty();
        content = getContent();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        init();
        //外围的圆形
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        if (type == 1) {
            paint.setColor(Color.rgb(0, 255, 255));
        }
        if (type == 2) {
            paint.setColor(Color.rgb(255, 255, 0));
        }
        if (type == 3) {
            paint.setColor(Color.rgb(255, 0, 0));
        }
        float x = emergency / (float) 10.0 * 700 + 250;
        float y = difficulty / (float) 10.0 * 800 + 150;
        //设置画笔为空心
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        canvas.drawCircle(45, 45, 40, paint);
        //画内侧的圆形
        //黑色未进行
        //设置画笔为空心
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(45, 45, 27, paint);
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
////        Toast.makeText(getContext(), "啊哈哈哈", Toast.LENGTH_SHORT).show();
//        return true;
//    }

}
