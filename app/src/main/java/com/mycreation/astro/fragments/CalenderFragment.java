package com.mycreation.astro.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.applandeo.materialcalendarview.CalendarView;

import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.applandeo.materialcalendarview.CalendarDay;
import com.applandeo.materialcalendarview.listeners.OnCalendarDayClickListener;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.mycreation.astro.R;
import com.mycreation.astro.ViewModel_Custom;
import com.mycreation.astro.myutils_pack.MyUtils;
import com.mycreation.astro.myutils_pack.Tracker_calender_util;
import com.mycreation.astro.object_models.ActivityTracker_model;
import com.mycreation.astro.object_models.GroupChat_model;
import com.mycreation.astro.object_models.LeaveTracker_model;
import com.mycreation.astro.object_models.User_Model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class CalenderFragment extends Fragment {

    CalendarView calendarView;
    TextView notes_TV;
    MaterialSwitch trackerSwitch;
    Context context;
    Tracker_calender_util trackerCalenderUtil;
    Button submmitBtn;

    ViewModel_Custom viewModelCustom;
    MyUtils myUtils;
    DateFormat dateFormat= new SimpleDateFormat("EEE dd-MMM-yyyy");

    private static CalenderFragment calenderFragment;
    static User_Model currentUser;

    ArrayList<CalendarDay> calendarDays=new ArrayList<>();
    ArrayList<ActivityTracker_model> activityTrackerModels_temp=new ArrayList<>();

    MutableLiveData<ArrayList<ActivityTracker_model>> liveData=new MutableLiveData<>();

    boolean isLeaveTrackingMode= false;
    ArrayList<LeaveTracker_model> allLeaveNotesFromDB_arrayList=new ArrayList<>();
    ArrayList<CalendarDay> calendarDays_forLeaveTrack_arrList=new ArrayList<>();

    CalendarDay selectedleaveDay;
    CalendarDay deSelectCalDay;

    ArrayList<User_Model> userModels=new ArrayList<>();

    private CalenderFragment() {
        // Required empty public constructor
    }

    public static CalenderFragment GetCalenderFrag(User_Model model){
        if (calenderFragment==null){
            currentUser=model;
            calenderFragment=new CalenderFragment();}
        return calenderFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calender, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context=view.getContext();
        calendarView=((Activity)context).findViewById(R.id.calenderView);
        notes_TV=((Activity)context).findViewById(R.id.calenderNote_TV);
        trackerSwitch=((Activity)context).findViewById(R.id.materialSwitch);
        submmitBtn=((Activity)context).findViewById(R.id.submit_btn);
        trackerCalenderUtil=new Tracker_calender_util(view.getContext());

        myUtils=new MyUtils(context);
        notes_TV.setMovementMethod(new ScrollingMovementMethod());

        viewModelCustom=new ViewModelProvider(this).get(ViewModel_Custom.class);

        // for getting users data
        viewModelCustom.GetAllUsers_nonListener().observe(getViewLifecycleOwner(), new Observer<ArrayList<User_Model>>() {
            @Override
            public void onChanged(ArrayList<User_Model> models) {
                userModels=models;
            }
        });


        viewModelCustom.GetActivityTrack_list().observe(getViewLifecycleOwner(), new Observer<ArrayList<ActivityTracker_model>>() {
            @Override
            public void onChanged(ArrayList<ActivityTracker_model> activityTrackerModels) {
                //calendarView.setCalendarDays(GetCalendarDays(activityTrackerModels));
                activityTrackerModels_temp=activityTrackerModels;
                if (!isLeaveTrackingMode){
                    GetCalendarDays(activityTrackerModels);
                }
            }
        });

        //GETTING LEAVES TRACKER NOTES FROM DB
        viewModelCustom.GetAllLeaveMarks().observe(getViewLifecycleOwner(), new Observer<ArrayList<LeaveTracker_model>>() {
            @Override
            public void onChanged(ArrayList<LeaveTracker_model> leaveTrackerModels) {

                allLeaveNotesFromDB_arrayList=leaveTrackerModels;
                if (isLeaveTrackingMode){
                    GetCalDaysAndFillCalView_forLeavesTRack(leaveTrackerModels);
                    UpdateTV_notes();
                }

            }
        });

// FOR FILLING NOTES TV and TO SUBMIT LEAVE REQ
calendarView.setOnCalendarDayClickListener(new OnCalendarDayClickListener() {
    @Override
    public void onClick(@NonNull CalendarDay calendarDay) {

        if (isLeaveTrackingMode) {
            //LOGIC FOR HIGHLIGHTING SELECTED DATE
            CalendarDay tempCalDay=calendarDay;
            tempCalDay.setBackgroundResource(R.drawable.leave_calenderday_background);
            ArrayList<CalendarDay> tempCalDayList = new ArrayList<>(calendarDays_forLeaveTrack_arrList);

            tempCalDayList.add(tempCalDay);
            calendarView.setCalendarDays(tempCalDayList);

            selectedleaveDay= calendarDay;

            // add logic for leave tracking Text view
            Calendar cal= calendarDay.getCalendar();
           // String tempNote = dateFormat.format(cal.getTime());
            String tempNote ="";
            try {
                tempNote = tempNote + "\n\n" + allLeaveNotesFromDB_arrayList.get(calendarDays_forLeaveTrack_arrList.indexOf(calendarDay)).getPersonsOnLeave();
            } catch (Exception e) {
                tempNote = tempNote + "\n\n" + "No records";
            }
            UpdateTV_notes();





        }
        else {
            Calendar cal = calendarDay.getCalendar();
            String tempNote = dateFormat.format(cal.getTime());
            try {
                tempNote = tempNote + "\n\n" + activityTrackerModels_temp.get(calendarDays.indexOf(calendarDay)).getPerformer();
            } catch (Exception e) {
                tempNote = tempNote + "\n\n" + "No records";
            }
            notes_TV.setText(tempNote);
        }
    }
});



        trackerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b){
            isLeaveTrackingMode=true;
            trackerSwitch.setText("Leave tracking mode");
            submmitBtn.setVisibility(View.VISIBLE);
            GetCalDaysAndFillCalView_forLeavesTRack(allLeaveNotesFromDB_arrayList);


        }
        else {
            isLeaveTrackingMode=false;
            trackerSwitch.setText("Activity tracking mode");
            //GetCalendarDays(activityTrackerModels_temp);
            calendarView.setCalendarDays(calendarDays);
            submmitBtn.setVisibility(View.INVISIBLE);
        }
    }
});



submmitBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {

        LeaveTracker_model leaveTrackerModel;
        //
        if (currentUser.isTeamMemberRight()){
        if (selectedleaveDay!=null){
            Calendar calendar= selectedleaveDay.getCalendar();
            long futureTimeStamp= calendar.getTimeInMillis();
            //leaveTrackerModel.setTimeStamp(futureTimeStamp);
            try {
               leaveTrackerModel= CheckIfAlreadyMarkedForLeave(allLeaveNotesFromDB_arrayList.get(calendarDays_forLeaveTrack_arrList.indexOf(selectedleaveDay)));
              // viewModelCustom.AddOrUpdateTracker(leaveTrackerModel,2);
            }
            catch (Exception e){
                leaveTrackerModel= new LeaveTracker_model(futureTimeStamp, "#"+currentUser.getUserDeviceId(), null);
                viewModelCustom.AddOrUpdateTracker(leaveTrackerModel,1);
            }
        }
        else {
            myUtils.MakeShortToast("Please select a date to mark");
        }
        }else {
            myUtils.MakeShortToast("You don't have full access yet!");
        }
    }
});

    }

// gives calender days from timestamp for Activity model
    private void GetCalendarDays(ArrayList<ActivityTracker_model> models){
        calendarDays.clear();
        for (ActivityTracker_model model: models) {
           calendarDays.add(trackerCalenderUtil.GetCalenderDay(model.getTimeStamp(),model.getActType()));
        }
       // myUtils.MakeLongToast(calendarDays.size()+"");
        calendarView.setCalendarDays(calendarDays);
        //return calendarDays;
    }

    // gives calender days from timestamp for leave model
       private  void GetCalDaysAndFillCalView_forLeavesTRack(ArrayList<LeaveTracker_model> models){
         calendarDays_forLeaveTrack_arrList.clear();
         for (LeaveTracker_model model:models){
             calendarDays_forLeaveTrack_arrList.add(trackerCalenderUtil.GetCalenderDay(model.getTimeStamp(),1));
         }
        calendarView.setCalendarDays(calendarDays_forLeaveTrack_arrList);
       }


    // RETURNS MODEL WITH CURRENT USER IF NOT EXIST ALREADY, ELSE REMOVE CUR USER
    private LeaveTracker_model CheckIfAlreadyMarkedForLeave(LeaveTracker_model model){



        if (model.getPersonsOnLeave()!=null && model.getPersonsOnLeave().contains(currentUser.getUserDeviceId())){
            String rep="#"+currentUser.getUserDeviceId();
            model.setPersonsOnLeave(model.getPersonsOnLeave().replace(rep,""));
           // myUtils.MakeLongToast(model.getPersonsOnLeave());
        }else {
            if (model.getPersonsOnLeave()!=null) {
                model.setPersonsOnLeave(model.getPersonsOnLeave() + "#" + currentUser.getUserDeviceId());
            }else {
                model.setPersonsOnLeave("#"+currentUser.getUserDeviceId());
            }
        }

        if (model.getPersonsOnLeave()==null || Objects.equals(model.getPersonsOnLeave(), "")){
            viewModelCustom.AddOrUpdateTracker(model,3);
        }
        else {
            viewModelCustom.AddOrUpdateTracker(model,2);
        }

        return model;
    }

    private void UpdateTV_notes(){

        String tempNote ="";
        try {
            tempNote = tempNote + allLeaveNotesFromDB_arrayList.get(calendarDays_forLeaveTrack_arrList.indexOf(selectedleaveDay)).getPersonsOnLeave();
            tempNote= myUtils.GetUserName_from_deviceId(tempNote,userModels);
        } catch (Exception e) {
            tempNote ="No records";
        }
        Calendar cal= selectedleaveDay.getCalendar();
        tempNote = dateFormat.format(cal.getTime()) + "\n\n" + tempNote;

        notes_TV.setText(tempNote);
    }


}