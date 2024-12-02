package com.mycreation.astro.myutils_pack;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;

import androidx.core.content.ContextCompat;

import com.applandeo.materialcalendarview.CalendarDay;
import com.applandeo.materialcalendarview.CalendarUtils;
import com.mycreation.astro.R;

import java.util.Calendar;

public class Tracker_calender_util {

    static Context context;
   // Calendar calendar= Calendar.getInstance();
    MyUtils myUtils;

    public Tracker_calender_util(Context context){
        this.context=context;
    }



    // leave=1, week end=2, night shift=3
    public CalendarDay GetCalenderDay(long millis, int markType){

        Calendar calendar= Calendar.getInstance();
        myUtils=new MyUtils(context);
        calendar.setTimeInMillis(millis);
        CalendarDay calendarDay= new CalendarDay(calendar);
        //myUtils.MakeLongToast(calendarDay.toString());
        if (markType==1){
          // calendarDay.setBackgroundDrawable(GetCircleDrawableWithText_forLeave());
           calendarDay.setImageDrawable(GetCircleDrawableWithText_forLeave());
           calendarDay.setBackgroundResource(R.drawable.leave_calenderday_background);
        } else if (markType==2) {
            calendarDay.setImageDrawable(GetCircleDrawableWithText_forWeekEnd());
            calendarDay.setBackgroundResource(R.drawable.weekend_calday_background);
        }
        else {
            calendarDay.setImageDrawable(GetCircleDrawableWithText_forNightShift());
            calendarDay.setBackgroundResource(R.drawable.nightshift_calday_background);
        }
        return calendarDay;
    }

    // calender highlighter
    private static Drawable GetCircleDrawableWithText_forLeave() {

       // Drawable background = ContextCompat.getDrawable(context, R.drawable.leave_calenderday_background);
        Drawable text = CalendarUtils.getDrawableText(context, "LV", null, android.R.color.black, 12);

        //Drawable[] layers = {background, text};
        Drawable[] layers = {null, text};
        return new LayerDrawable(layers);
    }


    private static Drawable GetCircleDrawableWithText_forWeekEnd() {

        //Drawable background = ContextCompat.getDrawable(context, R.drawable.weekend_calday_background);
        Drawable text = CalendarUtils.getDrawableText(context, "WE", null, android.R.color.black, 12);

        Drawable[] layers = {null, text};
        return new LayerDrawable(layers);
    }

    private static Drawable GetCircleDrawableWithText_forNightShift() {

       // Drawable background = ContextCompat.getDrawable(context, R.drawable.nightshift_calday_background);
        Drawable text = CalendarUtils.getDrawableText(context, "NS", null, android.R.color.black, 12);

        Drawable[] layers = {null, text};
        return new LayerDrawable(layers);
    }


}
