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
public class DistributeViewBackup extends View implements View.OnClickListener {

    List<DistributeEntity> list;
    private int size;//圆点的个数
    private MyCustomView myCustomView;
    private TestView testView;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public DistributeViewBackup(Context context) {
        super(context);
    }

    public DistributeViewBackup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DistributeViewBackup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public List<DistributeEntity> getList() {
        return list;
    }

    public void setList(List<DistributeEntity> list) {
        this.list = list;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void init() {
        size = getSize();
        list = getList();
        testView = new TestView(getContext());
    }

    @Override
    protected void onDraw(Canvas canvas) {

        init();
        //外围的圆形
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        for (int i = 0; i < size; i++) {
            if (list.get(i).getType() == 1) {
                paint.setColor(Color.rgb(0, 255, 255));
            }
            if (list.get(i).getType() == 2) {
                paint.setColor(Color.rgb(255, 255, 0));
            }
            if (list.get(i).getType() == 3) {
                paint.setColor(Color.rgb(255, 0, 0));
            }
            float x = list.get(i).getEmergency() / (float) 10.0 * 700 + 250;
            float y = list.get(i).getDifficulty() / (float) 10.0 * 800 + 150;
            //设置画笔为空心
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(5);
            canvas.drawCircle(x, y, 40, paint);
            //画内侧的圆形
            //黑色未进行
            //设置画笔为空心
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(x, y, 27, paint);
        }

        testView.onDraw(canvas);
        testView.setTag("aa");
        testView.setClickable(true);
        testView.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        Toast.makeText(getContext(),"111",Toast.LENGTH_SHORT).show();
        switch (view.getId()) {

        }
    }
}
