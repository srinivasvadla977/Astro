package com.mycreation.astro;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.mycreation.astro.data_manipulation_model.ActivityTrackerDB_manipulation;
import com.mycreation.astro.data_manipulation_model.DashBoard_img_DBM;
import com.mycreation.astro.data_manipulation_model.GroupChatDatabase_manipulation;
import com.mycreation.astro.data_manipulation_model.KnowledgeBaseDB_manipulation;
import com.mycreation.astro.data_manipulation_model.LeaveTrackerDB_manipulation;
import com.mycreation.astro.data_manipulation_model.LikedItem_DBM;
import com.mycreation.astro.data_manipulation_model.ReqCodeTokenGenerator;
import com.mycreation.astro.data_manipulation_model.TaskDatabaseManipulation;
import com.mycreation.astro.data_manipulation_model.UsersDatabase_Manipulation;
import com.mycreation.astro.object_models.ActivityTracker_model;
import com.mycreation.astro.object_models.GroupChat_model;
import com.mycreation.astro.object_models.KnowledgeBase_model;
import com.mycreation.astro.object_models.LeaveTracker_model;
import com.mycreation.astro.object_models.LikedItemIndex_model;
import com.mycreation.astro.object_models.NewTaskModel;
import com.mycreation.astro.object_models.ReqCode_model;
import com.mycreation.astro.object_models.User_Model;

import java.util.ArrayList;

public class ViewModel_Custom extends AndroidViewModel {

    TaskDatabaseManipulation taskDatabaseManipulation;
    ReqCodeTokenGenerator reqCodeTokenGenerator;
    UsersDatabase_Manipulation usersDatabaseManipulation;
    GroupChatDatabase_manipulation groupChatDatabaseManipulation;
    KnowledgeBaseDB_manipulation knowledgeBaseDBManipulation;
    ActivityTrackerDB_manipulation activityTrackerDBManipulation;
    LeaveTrackerDB_manipulation leaveTrackerDBManipulation;
    DashBoard_img_DBM dashBoardImgDbm;
    LikedItem_DBM likedItemDbm;

    public ViewModel_Custom(@NonNull Application application) {
        super(application);
        this.taskDatabaseManipulation=new TaskDatabaseManipulation(application);
        this.reqCodeTokenGenerator=new ReqCodeTokenGenerator(application);
        this.usersDatabaseManipulation=new UsersDatabase_Manipulation(application);
        this.groupChatDatabaseManipulation=new GroupChatDatabase_manipulation(application);
        this.knowledgeBaseDBManipulation=new KnowledgeBaseDB_manipulation(application);
        this.activityTrackerDBManipulation=new ActivityTrackerDB_manipulation(application);
        this.leaveTrackerDBManipulation=new LeaveTrackerDB_manipulation(application);
        this.dashBoardImgDbm=new DashBoard_img_DBM(application);
        this.likedItemDbm =new LikedItem_DBM(application);
    }

    public void AddNewTaskReminder(NewTaskModel newTaskModel, Uri uri){
        taskDatabaseManipulation.UploadImg_postMsg_addNewTask(newTaskModel,uri);
    }

    public void UploadImage(Uri uri){
        taskDatabaseManipulation.AddImageToFBStorage(uri);
    }

    public MutableLiveData<ArrayList<NewTaskModel>> GetAll_ScheduledTasks(){

        return taskDatabaseManipulation.GetAll_ScheduledTasks();
    }

    public void DeleteScheduledTask(NewTaskModel model){
        taskDatabaseManipulation.DeleteScheduledTask(model);
    }

    public void MarkToDeleteScheduledTask(String docId){
        taskDatabaseManipulation.MarkToDeleteScheduledTask(docId);
    }

    /// below token generator class


    public void InsertNewToken(ReqCode_model token){
        reqCodeTokenGenerator.InsertToken(token);
    }

    public void GetReqCodeToken(ReqCodeTokenGenerator.DataCallBack callback){
         reqCodeTokenGenerator.GetReqToken(callback);
    }

    // below Users database

    public void AddNewUser(User_Model userModel){
        usersDatabaseManipulation.AddUser(userModel);
    }

    public void UpdateUser(User_Model userModel, int updateType, Uri uri){
        usersDatabaseManipulation.UpdateUser(userModel,updateType,uri);
    }

    public void DeleteUser(String docId){
        usersDatabaseManipulation.DeleteUser(docId);
    }

    public MutableLiveData<ArrayList<User_Model>> GetAllUsers(){
        return usersDatabaseManipulation.GetAllUsers();
    }

    public MutableLiveData<ArrayList<User_Model>> GetAllUsers_nonListener(){
        return usersDatabaseManipulation.GetAllUsers_nonListener();
    }

    // below methods are for groupChat

    public void PostMessage(GroupChat_model model, Uri myUri){
        groupChatDatabaseManipulation.PostMessage(model,myUri);
    }

    public MutableLiveData<ArrayList<GroupChat_model>> GetAllChatData(){
        return groupChatDatabaseManipulation.GetAllChatData();
    }

    public void LikeMessage(GroupChat_model model, int likedIndex){
        groupChatDatabaseManipulation.LikeMessage(model, likedIndex);
    }


    // knowledge base manipulation

    public void AddNewAppKnowledge(KnowledgeBase_model model){
        knowledgeBaseDBManipulation.AddNewAppKnowledge(model);
    }

    public void UpdateAppKnowledge(KnowledgeBase_model model, String oldDocId){
        knowledgeBaseDBManipulation.UpdateAppKnowledge(model, oldDocId);
    }

    public MutableLiveData<ArrayList<KnowledgeBase_model>> GetAllApkKnowledge(){
      return knowledgeBaseDBManipulation.GetAllApkKnowledge();
    }

    // Activity tracker

    public MutableLiveData<ArrayList<ActivityTracker_model>> GetActivityTrack_list(){
        return  activityTrackerDBManipulation.GetActivityTrack_list();
    }

    public void AddActivityPerformer(ActivityTracker_model model, String curUser, String title){
        activityTrackerDBManipulation.AddActivityPerformer(model, curUser, title);
    }

    // for leave tracker

    public MutableLiveData<ArrayList<LeaveTracker_model>> GetAllLeaveMarks(){
        return leaveTrackerDBManipulation.GetAllLeaveMarks();
    }

    public void AddOrUpdateTracker(LeaveTracker_model model, int type){
        leaveTrackerDBManipulation.AddOrUpdateTracker(model,type);
    }

    // FOR DASHBOARD IMAGE

    public void UpdateDashBoardImg(Uri uri){
        dashBoardImgDbm.UpdateDashBoardImg(uri);
    }

    public MutableLiveData<String> GetDashBoardImg(){
       return dashBoardImgDbm.GetDashBoardImg();
    }

    public void Delete_dashboard_Image(){
        dashBoardImgDbm.Delete_dashboard_Image();
    }

    // FOR LIKED ITEM OF GROUP CHAT

    public MutableLiveData<LikedItemIndex_model> GetLikedItemIndex(){
        return likedItemDbm.GetLikedItemIndex();
    }

    public void PostLikedItemIndex(int index){
        likedItemDbm.PostLikedItemIndex(index);
    }

}
