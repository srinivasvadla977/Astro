package com.mycreation.astro.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mycreation.astro.R;
import com.mycreation.astro.ViewModel_Custom;
import com.mycreation.astro.myutils_pack.MyUtils;
import com.mycreation.astro.object_models.ActivityTracker_model;
import com.mycreation.astro.object_models.NewTaskModel;
import com.ortiz.touchview.TouchImageView;

import javax.inject.Singleton;


public class NewTaskOverViewFragment extends Fragment {

    static NewTaskOverViewFragment newTaskOverViewFragment;
    static NewTaskModel myModel;
    static String curUser;
    Context context;
    MyUtils myUtils;
    ViewModel_Custom viewModelCustom;

    TouchImageView imageView;
    TextView titleTV, descriptionTV;
    Button deleteBTN, updateTrackerBTN;
    ImageButton backBTN;

    static boolean isNotificationTriggered=false;

    public NewTaskOverViewFragment() {
        // Required empty public constructor
    }

    public static NewTaskOverViewFragment getFragment(NewTaskModel model, String user, boolean isTriggeredByNotification){
        if (newTaskOverViewFragment==null){
            //newTaskOverViewFragment= new NewTaskOverViewFragment();
        }
        newTaskOverViewFragment= new NewTaskOverViewFragment();
        myModel=model;
        curUser=user;
        isNotificationTriggered= isTriggeredByNotification;
        return newTaskOverViewFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_task_over_view, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context= view.getContext();
        Initializer((Activity) context);

        if (myModel.getTaskImageUrl()!=null){
            imageView.setVisibility(View.VISIBLE);
        Glide.with(context).load(myModel.getTaskImageUrl()).into(imageView);}
        else {
            imageView.setVisibility(View.GONE);
        }

        titleTV.setText(myModel.getTaskTitle());
        String desc= myModel.getTaskDescription()+"\n\nSet by: "+myModel.getSchedulerName()+"\nScheduled time: "+myUtils.GetKBDateFormat(myModel.getSelectedTimeInMillis());
        descriptionTV.setText(desc);

        deleteBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModelCustom.MarkToDeleteScheduledTask(myModel.getTaskDocId());
                GoBack();
            }
        });

        updateTrackerBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ActivityTracker_model activityTrackerModel=new ActivityTracker_model(System.currentTimeMillis(),curUser,2,null);
                viewModelCustom.AddActivityPerformer(activityTrackerModel,curUser,myModel.getTaskTitle());
                GoBack();
            }
        });

        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //myUtils.ReplaceFragment(DashboardFragment.getInstance(),getActivity().getSupportFragmentManager());
                GoBack();
            }
        });

    }

    private void Initializer(Activity activity){

        imageView= activity.findViewById(R.id.taskOverviewIMG);
        titleTV=activity.findViewById(R.id.taskOverviewTitleTV);
        descriptionTV=activity.findViewById(R.id.taskOverviewDescripTV);
        deleteBTN=activity.findViewById(R.id.taskOverviewDeleteBTN);
        updateTrackerBTN=activity.findViewById(R.id.taskOverviewUpdateTrackerBTN);
        backBTN=activity.findViewById(R.id.taskOverviewBackBTN);

        descriptionTV.setMovementMethod(new ScrollingMovementMethod());

        myUtils=new MyUtils(context);
        viewModelCustom= new ViewModelProvider(NewTaskOverViewFragment.this).get(ViewModel_Custom.class);

        if (isNotificationTriggered){
            deleteBTN.setVisibility(View.GONE);
            updateTrackerBTN.setVisibility(View.VISIBLE);
        }else {
            deleteBTN.setVisibility(View.VISIBLE);
            updateTrackerBTN.setVisibility(View.GONE);
        }

    }

    private void GoBack(){
        myUtils.ReplaceFragment(DashboardFragment.getInstance(),getActivity().getSupportFragmentManager());
    }

}