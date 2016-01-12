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
public class TestView extends View {

    public TestView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //外围的圆形
        int width = this.getWidth();
        int height = this.getHeight();
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.rgb(220, 220, 220));
        //设置画笔为空心
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(30);
        canvas.drawCircle(500, 500, 50, paint);

        super.onDraw(canvas);
    }

}
