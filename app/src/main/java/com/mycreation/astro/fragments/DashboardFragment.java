package com.mycreation.astro.fragments;

import static android.content.Context.ALARM_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mycreation.astro.R;
import com.mycreation.astro.ViewModel_Custom;
import com.mycreation.astro.myutils_pack.DayLightSaver_util;
import com.mycreation.astro.myutils_pack.FilePickerUtil;
import com.mycreation.astro.myutils_pack.MyUtils;
import com.mycreation.astro.object_models.NewTaskModel;
import com.mycreation.astro.object_models.User_Model;
import com.mycreation.astro.receivers.TaskReminding_Receiver;
import com.mycreation.astro.recyclerview_adopters.UpcomingTasksAdopter;
import com.mycreation.astro.recyclerview_adopters.Users_status_adopter;
import com.ortiz.touchview.TouchImageView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DashboardFragment extends Fragment {

    RecyclerView upcomingTasksRecView;
    Context context;
    ViewModel_Custom viewModelCustom;
    MyUtils myUtils;
    UpcomingTasksAdopter upcomingTasksAdopter;

    Activity activity;
    AlarmManager alarmManager;

    ArrayList<NewTaskModel> myNewTaskModels=new ArrayList<>();

    //for user status rec view
    ArrayList<User_Model> usersList=new ArrayList<>();
    RecyclerView userStatus_recView;
    Users_status_adopter statusAdopter;

    TouchImageView touchImageView;
    ImageButton imageButton_edit;
    ImageButton delete_DBIB;

    String curUser;
    Spinner filterSpinner;
    String[] spinnerItems={"Upcoming Tasks","Daily Tasks","Weekly Tasks","OneTime Tasks","All Tasks"};
    ArrayAdapter<String> spinnerAdapter;
    int filterMode=0;
    boolean isCurUserTeamMember=false;
    //
    Calendar todCal=Calendar.getInstance();
    Calendar modCal=Calendar.getInstance();

    //for image picking
    private ActivityResultLauncher<String> myPicker;
    private FilePickerUtil filePickerUtil;


    static DashboardFragment dashboardFragment;

    private DashboardFragment() {
        // Required empty public constructor
    }


    public static DashboardFragment getInstance(){
        if (dashboardFragment==null){dashboardFragment= new DashboardFragment();}
        return dashboardFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context=view.getContext();
        myUtils=new MyUtils(context);

        activity= (Activity) context;
        alarmManager=(AlarmManager) context.getSystemService(ALARM_SERVICE);

        viewModelCustom= new ViewModelProvider(DashboardFragment.this).get(ViewModel_Custom.class);

        upcomingTasksRecView= ((Activity) context).findViewById(R.id.upComingTasksRecView);
        upcomingTasksRecView.setLayoutManager(new LinearLayoutManager(context));

        userStatus_recView=((Activity) context).findViewById(R.id.users_recView);
        userStatus_recView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));

        touchImageView=((Activity) context).findViewById(R.id.dashBrd_IMG);
        imageButton_edit=((Activity) context).findViewById(R.id.edit_DBI);
        delete_DBIB=((Activity) context).findViewById(R.id.delete_DBI);

        filterSpinner=((Activity)context).findViewById(R.id.textView);
        spinnerAdapter=new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item,spinnerItems);
        filterSpinner.setAdapter(spinnerAdapter);

        curUser=myUtils.GetUserName_from_deviceId(myUtils.GetUserDeviceNumber(),usersList);


        viewModelCustom.GetAll_ScheduledTasks().observe(getViewLifecycleOwner(), new Observer<ArrayList<NewTaskModel>>() {
            @Override
            public void onChanged(ArrayList<NewTaskModel> newTaskModels) {
                if (newTaskModels!=null){
                    // removing deleted tasks
                    myNewTaskModels.clear();

                    // ADDING 1 HR IF DAYLIGHT SAVER APPLICABLE
                    if (DayLightSaver_util.getDLUtil().CheckIfDayLight_ApplicableToday_for_dashBoard()) {
                        for (NewTaskModel model : newTaskModels) {
                            if (!model.isMarkedToDeleteRight()) {
                                model.setSelectedTimeInMillis(model.getSelectedTimeInMillis()+3600000);
                                myNewTaskModels.add(model);
                            }
                        }
                    }else {
                        for (NewTaskModel model : newTaskModels) {
                            if (!model.isMarkedToDeleteRight()) {
                                myNewTaskModels.add(model);
                            }
                        }
                    }
                        ApplyFilter_loadData(filterMode,myNewTaskModels);
                }
            }
        });

        // for getting Users status
        viewModelCustom.GetAllUsers().observe(getViewLifecycleOwner(), new Observer<ArrayList<User_Model>>() {
            @Override
            public void onChanged(ArrayList<User_Model> userModels) {
                usersList.clear();
                for (User_Model m: userModels){
                    if (m.isTeamMemberRight()){
                        usersList.add(m);
                    }
                }
               // usersList.addAll(userModels);
                Update_UsersStatus_RecView();
                // for disabling buttons for guest
                for (User_Model m:usersList){
                    if (myUtils.GetUserDeviceNumber().equalsIgnoreCase(m.getUserDeviceId())){
                        isCurUserTeamMember=true;
                    }
                }
            }
        });

        //FOR LOADING DASHBOARD IMAGE
        viewModelCustom.GetDashBoardImg().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String string) {
                Glide.with(context).load(string).into(touchImageView);
                //delete_DBIB.setEnabled(string != null);
            }
        });

        //FOR EDITING DASHBOARD IMAGE
        imageButton_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCurUserTeamMember) {
                    filePickerUtil.GetUriFromGallery();
                }else {
                    myUtils.MakeLongToast("You don't have full access yet!");
                }
            }
        });

        // for image picking
        myPicker = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            if (result != null) {
                // Handle the returned URI here
                viewModelCustom.UpdateDashBoardImg(result);
                touchImageView.setImageURI(result);
            }
        });

        filePickerUtil = new FilePickerUtil(myPicker);

        // FOR DELETING DASHBOARD IMAGE
        delete_DBIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCurUserTeamMember){
                viewModelCustom.Delete_dashboard_Image();
                touchImageView.setImageResource(R.drawable.teamwork_withquote_img);}
                else {myUtils.MakeLongToast("You don't have full access yet!");}
            }
        });


        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                filterMode=i;
                ApplyFilter_loadData(filterMode,myNewTaskModels);
                try {
                    ((TextView)adapterView.getChildAt(0)).setTextColor(Color.BLACK);
                }catch (Exception e){}
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    private void ApplyFilter_loadData(int filterType, ArrayList<NewTaskModel> ipModels){
        ArrayList<NewTaskModel> filteredModel=new ArrayList<>();

            if (filterType==0){
                filteredModel=SortAndLoadData(ipModels);

            }else if (filterType==1){
                for (NewTaskModel model: ipModels){
                    if (model.isRepeatingTaskRight() && model.isDailyRepeatingTaskRight()){
                        filteredModel.add(model);
                    }
                    filteredModel=SortAndLoadData(filteredModel);
                }

            } else if (filterType==2) {
                for (NewTaskModel model: ipModels){
                    if (model.isRepeatingTaskRight() && !model.isDailyRepeatingTaskRight()){
                        filteredModel.add(model);
                    }
                    myUtils.SortNewTaskModelList(filteredModel);
                }
            } else if (filterType==3) {
                for (NewTaskModel model:ipModels){
                    if (!model.isRepeatingTaskRight()){
                        filteredModel.add(model);
                    }
                    myUtils.SortNewTaskModelList(filteredModel);
                }
            } else if (filterType==4) {
                filteredModel.addAll(ipModels);
                // to sort descending order
                Collections.sort(filteredModel, new Comparator<NewTaskModel>() {
                    @Override
                    public int compare(NewTaskModel newTaskModel, NewTaskModel t1) {
                        try {
                            return (int) (t1.getScheduledTime() - newTaskModel.getScheduledTime());
                        }catch (Exception e){
                            myUtils.MakeShortToast(e.getMessage());
                        }
                        return 0;
                    }
                });
            }

        LoadDataToView(filteredModel);
    }

    DateFormat sdf= new SimpleDateFormat("dd/MM/yyyy");

    private ArrayList<NewTaskModel> SortAndLoadData(ArrayList<NewTaskModel> models){
        ArrayList<NewTaskModel> sortingList=new ArrayList<>();
        for (NewTaskModel model:models){
            modCal.setTimeInMillis(model.getSelectedTimeInMillis());
            if (model.isDailyRepeatingTaskRight() || modCal.get(Calendar.DAY_OF_WEEK)==todCal.get(Calendar.DAY_OF_WEEK) || sdf.format(modCal.getTime()).equalsIgnoreCase(sdf.format(todCal.getTime()))){
                todCal.set(Calendar.HOUR_OF_DAY, modCal.get(Calendar.HOUR_OF_DAY));
                todCal.set(Calendar.MINUTE,modCal.get(Calendar.MINUTE));
                todCal.set(Calendar.SECOND,0);

                model.setSelectedTimeInMillis(todCal.getTimeInMillis());
                sortingList.add(model);
            }
        }
        // removing already passed tasks
        if (filterMode==0){
            sortingList.removeIf(m -> System.currentTimeMillis() > m.getSelectedTimeInMillis());
        }

        myUtils.SortNewTaskModelList(sortingList);
        return sortingList;
    }


    private void LoadDataToView(ArrayList<NewTaskModel> newTaskModels) {

        upcomingTasksAdopter=new UpcomingTasksAdopter(newTaskModels,context, curUser,isCurUserTeamMember);
        upcomingTasksRecView.setAdapter(upcomingTasksAdopter);

    }





    @SuppressLint("ScheduleExactAlarm")
    private void SetUpAlarmBroadcast(String title, String desc,int reqCode,long triggerTimee) {

        Intent intent=new Intent(activity, TaskReminding_Receiver.class);
        intent.putExtra("title",title);
        intent.putExtra("description",desc);
        //PendingIntent pendingIntent= PendingIntent.getBroadcast(MainActivity.this,requestCode++,intent,PendingIntent.FLAG_MUTABLE); // Mutable allows to re write data on intent like intent.putExtra() where as Immutable won't allow re writing
        PendingIntent pendingIntent= PendingIntent.getBroadcast(activity,reqCode,intent,PendingIntent.FLAG_MUTABLE); // Mutable allows to re write data on intent like intent.putExtra() where as Immutable won't allow re writing

        alarmManager.setExact(AlarmManager.RTC_WAKEUP,triggerTimee,pendingIntent);

        Toast.makeText(context,"Schedule success!",Toast.LENGTH_SHORT).show();

    }

    private void ScheduleTaskNotification(){

        for (NewTaskModel model:myNewTaskModels){

            String title= model.getTaskTitle();
            String desc= model.getTaskDescription();
            long time= model.getSelectedTimeInMillis();
            int reqCode= model.getAlarmReqCode();

          //  SetUpAlarmBroadcast(title,desc,reqCode,time);

        }


    }

    private void Update_UsersStatus_RecView(){

        statusAdopter =new Users_status_adopter(usersList, context);
        userStatus_recView.setAdapter(statusAdopter);

    }


}