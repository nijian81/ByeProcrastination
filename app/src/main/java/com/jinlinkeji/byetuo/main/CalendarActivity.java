package com.jinlinkeji.byetuo.main;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.RelativeLayout;

import com.alamkanak.weekview.WeekView;
import com.jinlinkeji.byetuo.R;

public class CalendarActivity extends FragmentActivity implements View.OnClickListener {

    private DayCalendarFragment dayCalendarFragment;
    private DistributeFragment distributeFragment;
    private MonthCalendarFragment monthCalendarFragment;
    private RelativeLayout one, two,center,right,left;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private YearCalendarFragment yearCalendarFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar);

        one = (RelativeLayout) findViewById(R.id.one);
        two = (RelativeLayout) findViewById(R.id.two);
        one.setOnClickListener(this);
        two.setOnClickListener(this);
        center = (RelativeLayout)findViewById(R.id.center);
        center.setOnClickListener(this);
        right = (RelativeLayout)findViewById(R.id.right);
        right.setOnClickListener(this);
        left = (RelativeLayout)findViewById(R.id.left);
        left.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.one:
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                dayCalendarFragment = new DayCalendarFragment();
                fragmentTransaction.replace(R.id.fragment,
                        dayCalendarFragment, "calendarFragment");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case R.id.two:
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                distributeFragment = new DistributeFragment();
                fragmentTransaction.replace(R.id.fragment,
                        distributeFragment, "distributeFragment");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case R.id.center:
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                monthCalendarFragment = new MonthCalendarFragment();
                fragmentTransaction.replace(R.id.fragment,
                        monthCalendarFragment, "monthCalendarFragment");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case R.id.right:
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                yearCalendarFragment = new YearCalendarFragment();
                fragmentTransaction.replace(R.id.fragment,
                        yearCalendarFragment, "yearCalendarFragment");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case R.id.left:
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                dayCalendarFragment = new DayCalendarFragment();
                fragmentTransaction.replace(R.id.fragment,
                        dayCalendarFragment, "calendarFragment");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();
        dayCalendarFragment = new DayCalendarFragment();
        fragmentTransaction.replace(R.id.fragment,
                dayCalendarFragment, "calendarFragment");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}
