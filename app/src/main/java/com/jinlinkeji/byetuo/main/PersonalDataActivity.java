package com.jinlinkeji.byetuo.main;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinlinkeji.byetuo.R;
import com.jinlinkeji.byetuo.utils.MyCustomView;
import com.jinlinkeji.byetuo.utils.WaterWaveProgress;

public class PersonalDataActivity extends Activity implements View.OnClickListener {

    private TextView buyHours, learnHours, fitnessHours, meetHours, walkHours;
    private MyCustomView myCustomView;
    private WaterWaveProgress waveProgress;
    private ImageView greenLine1, greenLine2, greenLine3, greenLine4, greenLine5, yellowLine1, yellowLine2, yellowLine3, yellowLine4, yellowLine5, pinkLine1, pinkLine2, pinkLine3, pinkLine4, pinkLine5, blueLine1, blueLine2, blueLine3, blueLine4, blueLine5, purpleLine1, purpleLine2, purpleLine3, purpleLine4, purpleLine5;
    Double buyHourParam,learnHourParam,fitnessHourParam,meetHourParam,walkHourParam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_data);

        //设置底部小时的显示
        buyHours = (TextView) findViewById(R.id.buyHours);
        learnHours = (TextView) findViewById(R.id.learnHours);
        fitnessHours = (TextView) findViewById(R.id.fitnessHours);
        meetHours = (TextView) findViewById(R.id.meetHours);
        walkHours = (TextView) findViewById(R.id.walkHours);
        greenLine1  =(ImageView)findViewById(R.id.greenLine1);
        greenLine2  =(ImageView)findViewById(R.id.greenLine2);
        greenLine3  =(ImageView)findViewById(R.id.greenLine3);
        greenLine4  =(ImageView)findViewById(R.id.greenLine4);
        greenLine5  =(ImageView)findViewById(R.id.greenLine5);
        yellowLine1 = (ImageView) findViewById(R.id.yellowLine1);
        yellowLine2 = (ImageView) findViewById(R.id.yellowLine2);
        yellowLine3 = (ImageView) findViewById(R.id.yellowLine3);
        yellowLine4 = (ImageView) findViewById(R.id.yellowLine4);
        yellowLine5 = (ImageView) findViewById(R.id.yellowLine5);
        pinkLine1 = (ImageView) findViewById(R.id.pinkLine1);
        pinkLine2 = (ImageView) findViewById(R.id.pinkLine2);
        pinkLine3 = (ImageView) findViewById(R.id.pinkLine3);
        pinkLine4 = (ImageView) findViewById(R.id.pinkLine4);
        pinkLine5 = (ImageView) findViewById(R.id.pinkLine5);
        blueLine1 = (ImageView) findViewById(R.id.blueLine1);
        blueLine2 = (ImageView) findViewById(R.id.blueLine2);
        blueLine3 = (ImageView) findViewById(R.id.blueLine3);
        blueLine4 = (ImageView) findViewById(R.id.blueLine4);
        blueLine5 = (ImageView) findViewById(R.id.blueLine5);
        purpleLine1 = (ImageView) findViewById(R.id.purpleLine1);
        purpleLine2 = (ImageView) findViewById(R.id.purpleLine2);
        purpleLine3 = (ImageView) findViewById(R.id.purpleLine3);
        purpleLine4 = (ImageView) findViewById(R.id.purpleLine4);
        purpleLine5 = (ImageView) findViewById(R.id.purpleLine5);
        buyHourParam = 3.5;
        learnHourParam = 4.5;
        fitnessHourParam = 2.5;
        meetHourParam = 1.2;
        walkHourParam  = 4.9;
        //判断购买日用品显示
        if(0<=buyHourParam &&buyHourParam<=1){
            greenLine1.setVisibility(View.VISIBLE);
            greenLine2.setVisibility(View.GONE);
            greenLine3.setVisibility(View.GONE);
            greenLine4.setVisibility(View.GONE);
            greenLine5.setVisibility(View.GONE);
        }
        if(1<buyHourParam &&buyHourParam<=2){
            greenLine1.setVisibility(View.VISIBLE);
            greenLine2.setVisibility(View.VISIBLE);
            greenLine3.setVisibility(View.GONE);
            greenLine4.setVisibility(View.GONE);
            greenLine5.setVisibility(View.GONE);
        }
        if(2<buyHourParam &&buyHourParam<=3){
            greenLine1.setVisibility(View.VISIBLE);
            greenLine2.setVisibility(View.VISIBLE);
            greenLine3.setVisibility(View.VISIBLE);
            greenLine4.setVisibility(View.GONE);
            greenLine5.setVisibility(View.GONE);
        }
        if(3<buyHourParam &&buyHourParam<=4){
            greenLine1.setVisibility(View.VISIBLE);
            greenLine2.setVisibility(View.VISIBLE);
            greenLine3.setVisibility(View.VISIBLE);
            greenLine4.setVisibility(View.VISIBLE);
            greenLine5.setVisibility(View.GONE);
        }
        if(4<buyHourParam){
            greenLine1.setVisibility(View.VISIBLE);
            greenLine2.setVisibility(View.VISIBLE);
            greenLine3.setVisibility(View.VISIBLE);
            greenLine4.setVisibility(View.VISIBLE);
            greenLine5.setVisibility(View.VISIBLE);
        }

        //判断学习显示
        if(0<=learnHourParam &&learnHourParam<=1){
            yellowLine1.setVisibility(View.VISIBLE);
            yellowLine2.setVisibility(View.GONE);
            yellowLine3.setVisibility(View.GONE);
            yellowLine4.setVisibility(View.GONE);
            yellowLine5.setVisibility(View.GONE);
        }
        if(1<learnHourParam &&learnHourParam<=2){
            yellowLine1.setVisibility(View.VISIBLE);
            yellowLine2.setVisibility(View.VISIBLE);
            yellowLine3.setVisibility(View.GONE);
            yellowLine4.setVisibility(View.GONE);
            yellowLine5.setVisibility(View.GONE);
        }
        if(2<learnHourParam &&learnHourParam<=3){
            yellowLine1.setVisibility(View.VISIBLE);
            yellowLine2.setVisibility(View.VISIBLE);
            yellowLine3.setVisibility(View.VISIBLE);
            yellowLine4.setVisibility(View.GONE);
            yellowLine5.setVisibility(View.GONE);
        }
        if(3<learnHourParam &&learnHourParam<=4){
            yellowLine1.setVisibility(View.VISIBLE);
            yellowLine2.setVisibility(View.VISIBLE);
            yellowLine3.setVisibility(View.VISIBLE);
            yellowLine4.setVisibility(View.VISIBLE);
            yellowLine5.setVisibility(View.GONE);
        }
        if(4<learnHourParam){
            yellowLine1.setVisibility(View.VISIBLE);
            yellowLine2.setVisibility(View.VISIBLE);
            yellowLine3.setVisibility(View.VISIBLE);
            yellowLine4.setVisibility(View.VISIBLE);
            yellowLine5.setVisibility(View.VISIBLE);
        }

        //判断健身显示
        if(0<=fitnessHourParam &&fitnessHourParam<=1){
            pinkLine1.setVisibility(View.VISIBLE);
            pinkLine2.setVisibility(View.GONE);
            pinkLine3.setVisibility(View.GONE);
            pinkLine4.setVisibility(View.GONE);
            pinkLine5.setVisibility(View.GONE);
        }
        if(1<fitnessHourParam &&fitnessHourParam<=2){
            pinkLine1.setVisibility(View.VISIBLE);
            pinkLine2.setVisibility(View.VISIBLE);
            pinkLine3.setVisibility(View.GONE);
            pinkLine4.setVisibility(View.GONE);
            pinkLine5.setVisibility(View.GONE);
        }
        if(2<fitnessHourParam &&fitnessHourParam<=3){
            pinkLine1.setVisibility(View.VISIBLE);
            pinkLine2.setVisibility(View.VISIBLE);
            pinkLine3.setVisibility(View.VISIBLE);
            pinkLine4.setVisibility(View.GONE);
            pinkLine5.setVisibility(View.GONE);
        }
        if(3<fitnessHourParam &&fitnessHourParam<=4){
            pinkLine1.setVisibility(View.VISIBLE);
            pinkLine2.setVisibility(View.VISIBLE);
            pinkLine3.setVisibility(View.VISIBLE);
            pinkLine4.setVisibility(View.VISIBLE);
            pinkLine5.setVisibility(View.GONE);
        }
        if(4<fitnessHourParam){
            pinkLine1.setVisibility(View.VISIBLE);
            pinkLine2.setVisibility(View.VISIBLE);
            pinkLine3.setVisibility(View.VISIBLE);
            pinkLine4.setVisibility(View.VISIBLE);
            pinkLine5.setVisibility(View.VISIBLE);
        }

        //判断开会显示
        if(0<=meetHourParam &&meetHourParam<=1){
            blueLine1.setVisibility(View.VISIBLE);
            blueLine2.setVisibility(View.GONE);
            blueLine3.setVisibility(View.GONE);
            blueLine4.setVisibility(View.GONE);
            blueLine5.setVisibility(View.GONE);
        }
        if(1<meetHourParam &&meetHourParam<=2){
            blueLine1.setVisibility(View.VISIBLE);
            blueLine2.setVisibility(View.VISIBLE);
            blueLine3.setVisibility(View.GONE);
            blueLine4.setVisibility(View.GONE);
            blueLine5.setVisibility(View.GONE);
        }
        if(2<meetHourParam &&meetHourParam<=3){
            blueLine1.setVisibility(View.VISIBLE);
            blueLine2.setVisibility(View.VISIBLE);
            blueLine3.setVisibility(View.VISIBLE);
            blueLine4.setVisibility(View.GONE);
            blueLine5.setVisibility(View.GONE);
        }
        if(3<meetHourParam &&meetHourParam<=4){
            blueLine1.setVisibility(View.VISIBLE);
            blueLine2.setVisibility(View.VISIBLE);
            blueLine3.setVisibility(View.VISIBLE);
            blueLine4.setVisibility(View.VISIBLE);
            blueLine5.setVisibility(View.GONE);
        }
        if(4<meetHourParam){
            blueLine1.setVisibility(View.VISIBLE);
            blueLine2.setVisibility(View.VISIBLE);
            blueLine3.setVisibility(View.VISIBLE);
            blueLine4.setVisibility(View.VISIBLE);
            blueLine5.setVisibility(View.VISIBLE);
        }

        //判断饭后散步显示
        if(0<=walkHourParam &&walkHourParam<=1){
            purpleLine1.setVisibility(View.VISIBLE);
            purpleLine2.setVisibility(View.GONE);
            purpleLine3.setVisibility(View.GONE);
            purpleLine4.setVisibility(View.GONE);
            purpleLine5.setVisibility(View.GONE);
        }
        if(1<walkHourParam &&walkHourParam<=2){
            purpleLine1.setVisibility(View.VISIBLE);
            purpleLine2.setVisibility(View.VISIBLE);
            purpleLine3.setVisibility(View.GONE);
            purpleLine4.setVisibility(View.GONE);
            purpleLine5.setVisibility(View.GONE);
        }
        if(2<walkHourParam &&walkHourParam<=3){
            purpleLine1.setVisibility(View.VISIBLE);
            purpleLine2.setVisibility(View.VISIBLE);
            purpleLine3.setVisibility(View.VISIBLE);
            purpleLine4.setVisibility(View.GONE);
            purpleLine5.setVisibility(View.GONE);
        }
        if(3<walkHourParam &&walkHourParam<=4){
            purpleLine1.setVisibility(View.VISIBLE);
            purpleLine2.setVisibility(View.VISIBLE);
            purpleLine3.setVisibility(View.VISIBLE);
            purpleLine4.setVisibility(View.VISIBLE);
            purpleLine5.setVisibility(View.GONE);
        }
        if(4<walkHourParam){
            purpleLine1.setVisibility(View.VISIBLE);
            purpleLine2.setVisibility(View.VISIBLE);
            purpleLine3.setVisibility(View.VISIBLE);
            purpleLine4.setVisibility(View.VISIBLE);
            purpleLine5.setVisibility(View.VISIBLE);
        }
        buyHours.setText("2.3 hours");
        learnHours.setText("4.5 hours");
        fitnessHours.setText("3 hours");
        meetHours.setText("1.6 hours");
        walkHours.setText("2.5 hours");
        myCustomView = (MyCustomView) findViewById(R.id.circle_view);
        waveProgress = (WaterWaveProgress) findViewById(R.id.waterWaveProgress1);
        //设置内部圆环不显示进度
        waveProgress.setShowProgress(false);
        //设置水波可以浮动
        waveProgress.animateWave();
        //设置当前完成的任务时间占当天时间的比例
        waveProgress.setProgress(70);
        //设置不显示中心的进度指
        waveProgress.setShowNumerical(false);
        //设置购买日用品的弧度
        myCustomView.setBuyArc(200, 30);
        //设置学习的弧度
        myCustomView.setLearnArc(230, 10);
        //设置饭后散步的弧度
        myCustomView.setWalkArc(240, 50);
        //设置健身的弧度
        myCustomView.setFitnessArc(290, 20);
        //设置开会的弧度
        myCustomView.setMeetArc(0, 50);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

        }
    }

}
