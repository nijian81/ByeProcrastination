package com.jinlinkeji.byetuo.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.jinlinkeji.byetuo.R;

public class MainActivity extends Activity implements View.OnClickListener {

    private Button location, search, task, talk, contacts, personalData,calendar;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        location = (Button) findViewById(R.id.location);
        search = (Button) findViewById(R.id.search);
        task = (Button) findViewById(R.id.task);
        talk = (Button) findViewById(R.id.talk);
        contacts = (Button) findViewById(R.id.contacts);
        personalData = (Button) findViewById(R.id.personalData);
        calendar = (Button) findViewById(R.id.calendar);
        location.setOnClickListener(this);
        search.setOnClickListener(this);
        task.setOnClickListener(this);
        talk.setOnClickListener(this);
        contacts.setOnClickListener(this);
        personalData.setOnClickListener(this);
        calendar.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理

    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.location:
                intent = new Intent();
                intent.setAction("android.intent.action.MyLocation");
                startActivity(intent);
                break;
            case R.id.search:
                intent = new Intent();
                intent.setAction("android.intent.action.Search");
                startActivity(intent);
                break;
            case R.id.task:
                intent = new Intent();
                intent.setAction("android.intent.action.TaskProceed");
                startActivity(intent);
                break;
            case R.id.talk:
                intent = new Intent();
                intent.setAction("android.intent.action.Chat");
                startActivity(intent);
                break;
            case R.id.contacts:
                intent = new Intent();
                intent.setAction("android.intent.action.Contacts");
                startActivity(intent);
                break;
            case R.id.personalData:
                intent = new Intent();
                intent.setAction("android.intent.action.PersonalData");
                startActivity(intent);
                break;
            case R.id.calendar:
                intent = new Intent();
                intent.setAction("android.intent.action.Calendar");
                startActivity(intent);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        this.finish();
        return super.onKeyDown(keyCode, event);
    }
}
