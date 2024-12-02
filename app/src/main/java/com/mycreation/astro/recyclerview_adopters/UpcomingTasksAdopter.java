package com.mycreation.astro.recyclerview_adopters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mycreation.astro.R;
import com.mycreation.astro.databinding.UpcomingTasksListItemBinding;
import com.mycreation.astro.fragments.NewTaskOverViewFragment;
import com.mycreation.astro.myutils_pack.DayLightSaver_util;
import com.mycreation.astro.myutils_pack.ImagePreview_dialogue_util;
import com.mycreation.astro.myutils_pack.MyUtils;
import com.mycreation.astro.object_models.NewTaskModel;

import java.util.ArrayList;

public class UpcomingTasksAdopter extends RecyclerView.Adapter<UpcomingTasksAdopter.CustomHolder> {

    //UpcomingTasksListItemBinding binding;
    ArrayList<NewTaskModel> newTaskModels= new ArrayList<>();
    Context context;
    static MyUtils myUtils;
    static String curUser;
    static boolean isTM=false;

    static FragmentManager fragmentManager;

    public UpcomingTasksAdopter(ArrayList<NewTaskModel> newTaskModels, Context context, String user, boolean isTeamMember) {
        this.newTaskModels = newTaskModels;
        this.context = context;
        curUser=user;
        myUtils=new MyUtils(context);
        isTM=isTeamMember;
        fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
    }

    @NonNull
    @Override
    public CustomHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        UpcomingTasksListItemBinding binding= DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.upcoming_tasks_list_item,parent,false);

        return new CustomHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomHolder holder, int position) {

        NewTaskModel curModel= newTaskModels.get(position);
        holder.binding.setEachTaskLV(curModel);

        //Glide.with(context).load(curModel.getTaskImageUrl()).into(holder.binding.imageLv);
        // SHOWING TIME
        String time="At: ";

        /*long millis= curModel.getSelectedTimeInMillis();
        if (DayLightSaver_util.getDLUtil().CheckIfDayLight_ApplicableToday_for_dashBoard()){
            millis+=3600000;
            curModel.setSelectedTimeInMillis(millis);
            //curModel.setSelectedTimeInMillis(curModel.getSelectedTimeInMillis()+3600000);
        }*/

        if (curModel.isRepeatingTaskRight()){
            if (curModel.isDailyRepeatingTaskRight()){
                time+=myUtils.GetTimeFromMillis(curModel.getSelectedTimeInMillis())+" (Every Day)";
            }else {
                time+= myUtils.GetTimeFromMillis_withDay(curModel.getSelectedTimeInMillis())+" (Every Week)";
            }
        }else {
            time+=myUtils.GetDateFromMillis(curModel.getSelectedTimeInMillis())+" (One Time Task)";
        }
        // setting type of task
        /*String taskType="";
        if (curModel.isRepeatingTaskRight()){
            if (curModel.isDailyRepeatingTaskRight()){
                taskType="Daily Once";
            }else { taskType="Weekly Once"; }

        }else if (curModel.isWeekEndRight()){
            taskType="Week End";
        }else { taskType="One Time"; }*/

       // time+=" {type: #"+taskType+"}";

        holder.binding.timeLv.setText(time);
        String scheduler="Set by: "+ curModel.getSchedulerName();
        holder.binding.schedulerLv.setText(scheduler);

        if (curModel.getTaskImageUrl()==null){
            holder.binding.imageLv.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        if (newTaskModels!=null){return newTaskModels.size();}
        return 0;
    }

    public static class CustomHolder extends RecyclerView.ViewHolder{

        UpcomingTasksListItemBinding binding;


        public CustomHolder(@NonNull UpcomingTasksListItemBinding binding) {
            super(binding.getRoot());
            this.binding=binding;

            //onClick listeners below...for showing task image
            binding.imageLv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImagePreview_dialogue_util.GetImgPrevDialogue(itemView.getContext()).OpenImgPrev(binding.getEachTaskLV().getTaskImageUrl());
                }
            });

            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (isTM){
                    myUtils.ReplaceFragment(NewTaskOverViewFragment.getFragment(binding.getEachTaskLV(),curUser,false),fragmentManager);}
                }
            });



        }


    }




}
