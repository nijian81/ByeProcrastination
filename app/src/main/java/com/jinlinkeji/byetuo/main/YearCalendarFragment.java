package com.jinlinkeji.byetuo.main;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jinlinkeji.byetuo.R;
import com.wgc.shuwoom.scrollercalendar.ScrollerCalendar;
import com.wgc.shuwoom.scrollercalendar.ScrollerCalendarController;


public class YearCalendarFragment extends Fragment implements ScrollerCalendarController {

    private ScrollerCalendar pickerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.year_calendar_fragment, container,
                false);

        pickerView = (ScrollerCalendar)rootView.findViewById(R.id.pickerView);
        pickerView.setController(this);

        return rootView;
    }

    @Override
    public void onMonthOfYearSelected(int year, int month) {

    }
}