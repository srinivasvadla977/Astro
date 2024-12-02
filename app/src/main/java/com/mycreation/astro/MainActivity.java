package com.mycreation.astro;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.text.Layout;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.mycreation.astro.fragments.AdminFragment;
import com.mycreation.astro.fragments.CalenderFragment;
import com.mycreation.astro.fragments.DashboardFragment;
import com.mycreation.astro.fragments.GroupChatFragment;
import com.mycreation.astro.fragments.KnowledgeBaseFragment;
import com.mycreation.astro.fragments.NewTaskFragment;
import com.mycreation.astro.fragments.NewTaskOverViewFragment;
import com.mycreation.astro.fragments.NewUserFragment;
import com.mycreation.astro.myutils_pack.AlertDialogueUtil;
import com.mycreation.astro.myutils_pack.FilePickerUtil;
import com.mycreation.astro.myutils_pack.ImagePreview_dialogue_util;
import com.mycreation.astro.myutils_pack.MyGlobalUtil;
import com.mycreation.astro.myutils_pack.MyUtils;
import com.mycreation.astro.object_models.ActivityTracker_model;
import com.mycreation.astro.object_models.GroupChat_model;
import com.mycreation.astro.object_models.NewTaskModel;
import com.mycreation.astro.object_models.User_Model;
import com.mycreation.astro.receivers.GroupChat_notification_broadcastReceiver;
import com.mycreation.astro.services.GroupChat_notification_Service;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    View navigationHeader;
    ActionBarDrawerToggle actionBarDrawerToggle;

    Fragment tempFragment;
    MyGlobalUtil myGlobalUtil;
    MyUtils myUtils;

    String[] permissionsList={"android.permission.READ_MEDIA_IMAGES","android.permission.CAMERA","android.permission.POST_NOTIFICATIONS", "android.permission.SCHEDULE_EXACT_ALARM","android.permission.WAKE_LOCK","android.permission.VIBRATE","android.permission.READ_MEDIA_AUDIO","android.permission.SYSTEM_ALERT_WINDOW","android.permission.TURN_SCREEN_ON","android.permission.READ_PHONE_NUMBERS","ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS"};

    User_Model guestUser;
    private MutableLiveData<User_Model> currentUserModel=new MutableLiveData<>();
    boolean isloginComplete=false;

    //navigation header elements
    ImageButton editProfPic,editProfName;
    ImageView profPicDrawer_img;
    EditText hiddenET_forName;
    TextView profName_TV;

    MaterialSwitch materialSwitch1;

    ViewModel_Custom viewModelCustom;
    ArrayList<User_Model> usersList=new ArrayList<>();

    //for image picking
    private ActivityResultLauncher<String> myPicker;
    private FilePickerUtil filePickerUtil;

    FragmentManager fragmentManager=getSupportFragmentManager();

    // for image prev view
    ImagePreview_dialogue_util imgDialUtil;

    // for locally updating user status
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor spEditor;

    // FOR TASK SCHEDULER
    TaskReminding_Scheduler scheduler;
    ArrayList<NewTaskModel> myNewTaskModels=new ArrayList<>();

    //Broadcast
    int RESCHEDULING_REQ_CODE= 101;
    AlarmManager alarmManager;

    CoordinatorLayout coordinatorLayout;
    Snackbar snackbar;

    Vibrator vibrator;


    //########################################################### ON CREATE ##########################################################################
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawerLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    GlobalInitializer();
    RequestForPermissions();



    // initializing date & time picker globally with GlobalUtil class to reduce fragment load time
    myGlobalUtil=MyGlobalUtil.GetGlobalUtilInstance(this);

    myUtils=new MyUtils(this);

    viewModelCustom=new ViewModelProvider(this).get(ViewModel_Custom.class);


    // initializing a guest user
    guestUser=new User_Model("Guest",null,null,false,myUtils.GetUserDeviceNumber(),null);
    // storing the model in live data to observe changes so that we can redirect user once admin approves request
    currentUserModel.setValue(guestUser);

    long[] vibPattern={0,100,100,100};
    //vibrator.vibrate(vibPattern,-1);


    // loading all users into usersList
        viewModelCustom.GetAllUsers().observe(this, new Observer<ArrayList<User_Model>>() {
            @Override
            public void onChanged(ArrayList<User_Model> userModels) {
                usersList.addAll(userModels);
                // checking if user exist if yes storing model in curUser
                CheckCurUser(guestUser);
            }
        });

    // sending user to new user fragment or group chan fragment
        RedirectUser(guestUser);


    // navigation drawer set up
    actionBarDrawerToggle=new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
    drawerLayout.addDrawerListener(actionBarDrawerToggle);
    actionBarDrawerToggle.syncState();
    Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                item.setChecked(true);

                User_Model tempModel=currentUserModel.getValue();

                if (item.getItemId() == R.id.scheduleTask) {

                    if (tempModel!=null && tempModel.isTeamMemberRight()) {
                    tempFragment= NewTaskFragment.getNewTaskFragment(currentUserModel.getValue());
                    ReplaceFragment(tempFragment);}
                    else {
                        ReplaceFragment(NewUserFragment.getInstance(usersList));
                        vibrator.vibrate(vibPattern,-1);
                    }

                }else if (item.getItemId() == R.id.calender_item) {

                    ReplaceFragment(CalenderFragment.GetCalenderFrag(currentUserModel.getValue()));

                } else if (item.getItemId() == R.id.admin_item) {

                    if (tempModel!=null && tempModel.isTeamMemberRight()) {
                        tempFragment = AdminFragment.GetAdminFrag(currentUserModel.getValue());
                        ReplaceFragment(tempFragment);
                    }else {
                        ReplaceFragment(NewUserFragment.getInstance(usersList));
                        vibrator.vibrate(vibPattern,-1);
                    }

                } else if (item.getItemId() == R.id.groupChat) {

                    if (tempModel!=null && tempModel.isTeamMemberRight()) {
                        tempFragment = GroupChatFragment.getFragment(Objects.requireNonNull(currentUserModel));
                        ReplaceFragment(tempFragment);
                    }else {
                        ReplaceFragment(NewUserFragment.getInstance(usersList));
                        vibrator.vibrate(vibPattern,-1);
                    }

                } else if (item.getItemId()==R.id.dashBoard) {
                    tempFragment=DashboardFragment.getInstance();
                    ReplaceFragment(tempFragment);
                }
                else if (item.getItemId()==R.id.knowledgeBase){
                    if (tempModel!=null && tempModel.isTeamMemberRight()) {
                        ReplaceFragment(KnowledgeBaseFragment.GetKnowledgeBaseFrag(currentUserModel.getValue()));
                    }else {
                        ReplaceFragment(NewUserFragment.getInstance(usersList));
                        vibrator.vibrate(vibPattern,-1);
                    }
                } else if (item.getItemId()==R.id.appInfo) {
                    AlertDialogueUtil.getAlertDialogueUtil(MainActivity.this).ShowDialogue("About this App!","This App is built for MarketsTeam-A internal communication to enhance the team coordination.");
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        // observing current user live data object and redirecting the user as per result
        currentUserModel.observe(this, new Observer<User_Model>() {
            @Override
            public void onChanged(User_Model userModel) {

                if (materialSwitch1!=null){
                    materialSwitch1.setEnabled(userModel.isTeamMemberRight());
                }

                //tempModel_ForRedirect.setValue(userModel);
                if (!isloginComplete){
                    RedirectUser(userModel);
                }
            }
        });


        viewModelCustom.GetAll_ScheduledTasks().observe(this, new Observer<ArrayList<NewTaskModel>>() {
                    @Override
                    public void onChanged(ArrayList<NewTaskModel> newTaskModels) {
                        myNewTaskModels=newTaskModels;
                        if (sharedPreferences.getBoolean("isCurUserConnected",false) && scheduler!= null){
                            scheduler.ScheduleTaskNotification(myNewTaskModels);
                        }
                    }
                });

                // logic for updating profile pic
                editProfPic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        filePickerUtil.GetUriFromGallery();
                    }
                });

        // for image picking

        myPicker = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            if (result != null) {
                // Handle the returned URI here
                viewModelCustom.UpdateUser(currentUserModel.getValue(),1,result);
                profPicDrawer_img.setImageURI(result);
            }
        });

        filePickerUtil = new FilePickerUtil(myPicker);

        //logic for updating name

        profName_TV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profName_TV.setVisibility(View.INVISIBLE);
                hiddenET_forName.setVisibility(View.VISIBLE);
                hiddenET_forName.requestFocus();
                hiddenET_forName.setText(profName_TV.getText());
                editProfName.setVisibility(View.VISIBLE);             }
        });

        editProfName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(hiddenET_forName.getText())){
                if (!hiddenET_forName.getText().toString().equals(currentUserModel.getValue().getUserName())){
                    currentUserModel.getValue().setUserName(hiddenET_forName.getText().toString());
                    viewModelCustom.UpdateUser(currentUserModel.getValue(),2,null);
                }
                UndoHeaderVisibility();}
                else {
                    myUtils.MakeShortToast("Please enter name!");
                }
            }
        });

        // navigation drawer state change listener
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                UndoHeaderVisibility();
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });


        //  showing profile preview with dialogue
        profPicDrawer_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ImagePreview_dialogue_util.GetImgPrevDialogue(MainActivity.this).OpenImgPrev(currentUserModel.getValue().getProfilePic());

            }
        });

        //  CHECKING IF INT AVAILABLE
          if (!myUtils.IsInternetAvailable()){
             snackbar= myUtils.GetSnackBar(coordinatorLayout,"Please check your Internet Connection!","Dismiss!");

             snackbar.setAnchorView(drawerLayout);
             snackbar.show();
             // myUtils.MakeLongToast("Please check your Internet Connection!");

          }
    }

    private void GlobalInitializer() {

        drawerLayout=findViewById(R.id.drawerLayout);
        navigationView=findViewById(R.id.navigationView);

        // to access items inside navigation drawer header
        navigationHeader=navigationView.getHeaderView(0);

        editProfPic=navigationHeader.findViewById(R.id.edit_profPic);
        editProfName=navigationHeader.findViewById(R.id.edit_name);
        profPicDrawer_img=navigationHeader.findViewById(R.id.profilePic_drawer);
        hiddenET_forName=navigationHeader.findViewById(R.id.userName_drawer_ET);
        profName_TV=navigationHeader.findViewById(R.id.userName_drawer);

        // disabling edit option for new user
        editProfPic.setVisibility(View.INVISIBLE);

        //
        sharedPreferences= getSharedPreferences("my_prefs", Activity.MODE_PRIVATE);
        spEditor= sharedPreferences.edit();

        alarmManager=(AlarmManager)getSystemService(ALARM_SERVICE);
        coordinatorLayout=findViewById(R.id.cooardinatorLayout);
        vibrator=(Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }




    // draws navigation drawer when pressed on default toolbar menu icon
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // to close navigation drawer when back button is pressed
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    // to replace frame layout with fragment
    private void ReplaceFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();

    }


    public void RequestForPermissions(){
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(permissionsList,101);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==101){
            guestUser.setUserDeviceId(myUtils.GetUserDeviceNumber());
            CheckCurUserAndRedirect(guestUser);
        }
    }

    //to update navigation header according to user type
    private void FillNavigationHeaderFields(User_Model model){

        if (!model.isTeamMemberRight()){editProfName.setVisibility(View.GONE);editProfPic.setVisibility(View.INVISIBLE); }
        else { editProfName.setVisibility(View.VISIBLE);editProfPic.setVisibility(View.VISIBLE);  }

        TextView tv=navigationHeader.findViewById(R.id.userName_drawer);
        tv.setText(model.getUserName());

        if (model.getProfilePic()!=null){
        Glide.with(MainActivity.this).load(model.getProfilePic()).into((android.widget.ImageView) navigationHeader.findViewById(R.id.profilePic_drawer));}

    }

    // checking if current user exists already
    private void CheckCurUser(User_Model curUserModel){

        if (usersList!=null){
            for (User_Model model: usersList) {
                if (curUserModel.getUserDeviceId()!=null && curUserModel.getUserDeviceId().equalsIgnoreCase(model.getUserDeviceId())){
                    currentUserModel.setValue(model);
                    guestUser=model;
                    FillNavigationHeaderFields(guestUser);
                }
            }
        }


    }

    //
    private void CheckCurUserAndRedirect(User_Model curUserModel){

        if (usersList!=null){
            for (User_Model model: usersList) {
                if (curUserModel.getUserDeviceId()!=null && curUserModel.getUserDeviceId().equalsIgnoreCase(model.getUserDeviceId())){
                    currentUserModel.setValue(model);
                    guestUser=model;
                    FillNavigationHeaderFields(guestUser);
                    RedirectUser(guestUser);
                }
            }
        }
    }

    // redirecting user as per their rights
    private void RedirectUser(User_Model user){

        if (user.isTeamMemberRight()){
           // CHECKING IF OPENED BY NOTIFICATION CLICK
            if (getIntent().getBooleanExtra("TaskOverViewFrag",false)){
                Intent i= getIntent();
                NotificationTriggered_for_taskOverview_fragment((NewTaskModel) i.getSerializableExtra("taskModel"));
            }
            else {
                ReplaceFragment(GroupChatFragment.getFragment(currentUserModel));
            }
            isloginComplete=true;
           // menuItem.setChecked(true);
        }
        else {
            ReplaceFragment(NewUserFragment.getInstance(usersList));
        }
    }

//for hiding profile edit text and showing TV
    private void UndoHeaderVisibility(){

        hiddenET_forName.setVisibility(View.INVISIBLE);
        editProfName.setVisibility(View.GONE);
        profName_TV.setVisibility(View.VISIBLE);

    }

    boolean isIterating=false;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_for_switch,menu);
        MenuItem item= menu.findItem(R.id.toolBar_switch);
        item.setActionView(R.layout.user_status_switch);


        boolean curState=sharedPreferences.getBoolean("isCurUserConnected",false);

        materialSwitch1=item.getActionView().findViewById(R.id.status_switch);

        scheduler=new TaskReminding_Scheduler(MainActivity.this, currentUserModel.getValue().getUserName());

        if (curState){
            materialSwitch1.setText("On Shift");
        }
        else {
            materialSwitch1.setText("Off Shift");
        }
        materialSwitch1.setChecked(curState);

        User_Model userModel=currentUserModel.getValue();
        if (!userModel.isTeamMemberRight()){
            materialSwitch1.setEnabled(false);
        }


        materialSwitch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    materialSwitch1.setText("On Shift");
                    userModel.setUserStatus("On Shift");

                    if (!isIterating) {
                        viewModelCustom.UpdateUser(userModel, 4, null);
                        // CALLING NOTIFICATION SCHEDULER
                        scheduler.ScheduleTaskNotification(myNewTaskModels);
                        AutoScheduler_nightShift.getInstance(MainActivity.this, currentUserModel.getValue().getUserName()).ScheduleForNightShift();
                    }
                } else {
                        AlertDialogueUtil.getAlertDialogueUtil(MainActivity.this).ShowDialogue("Are you Sure?", "You don't receive task related notifications if disabled", new AlertDialogueUtil.DialogueCallBack() {
                            @Override
                            public void onConfirmation(boolean confirm) {
                                if (confirm) {
                                    materialSwitch1.setText("Off Shift");
                                    userModel.setUserStatus("Off Shift");
                                    viewModelCustom.UpdateUser(userModel, 4, null);

                                    // CANCELLING ALL NOTIFICATIONS
                                    scheduler.UnScheduleAllNotifications(myNewTaskModels);
                                    isIterating = false;

                                } else {
                                    isIterating = true;
                                    materialSwitch1.setChecked(true);
                                }
                            }
                        });

            }
                spEditor.putBoolean("isCurUserConnected",b);
                spEditor.commit();
            }
        });

        return super.onCreateOptionsMenu(menu);


    }

    //############################ service & broad cast testing ############################################

    @Override
    protected void onStop() {
        //for starting service
        Reschedule();
        super.onStop();
    }

    @SuppressLint("ScheduleExactAlarm")
    private void Reschedule(){

        long triggerAt= System.currentTimeMillis()+(1000*5); // 5 secs

        Intent intent=new Intent(MainActivity.this, GroupChat_notification_broadcastReceiver.class);
        PendingIntent pendingIntent= PendingIntent.getBroadcast(MainActivity.this,RESCHEDULING_REQ_CODE,intent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_MUTABLE); // Mutable allows to re write data on intent like intent.putExtra() where as Immutable won't allow re writing

        if (pendingIntent==null){
        pendingIntent=PendingIntent.getBroadcast(MainActivity.this,RESCHEDULING_REQ_CODE,intent,PendingIntent.FLAG_MUTABLE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP,triggerAt,pendingIntent);
        }

    }

    @Override
    protected void onStart() {

        // navigation toggle
        actionBarDrawerToggle=new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // broadcast
        Intent intent=new Intent(MainActivity.this, GroupChat_notification_broadcastReceiver.class);
        PendingIntent pendingIntent= PendingIntent.getBroadcast(MainActivity.this,RESCHEDULING_REQ_CODE,intent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_MUTABLE);

        if (pendingIntent!=null){
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }

        //STOPPING SERVICE IF EXIST
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (GroupChat_notification_Service.class.getName().equals(service.service.getClassName())) {

                Intent myIntent = new Intent(MainActivity.this, GroupChat_notification_Service.class);
                stopService(myIntent);
            }
        }
        super.onStart();
    }

    ///
    private void SetUpSnackBar(){

        snackbar=Snackbar.make(coordinatorLayout,"No internet connection!",Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        }

        private void NotificationTriggered_for_taskOverview_fragment(NewTaskModel taskModel){

        try {
            myUtils.ReplaceFragment(NewTaskOverViewFragment.getFragment(taskModel,currentUserModel.getValue().getUserName(),taskModel.isWeekEndRight()),fragmentManager);

        }catch (Exception e){
            myUtils.MakeLongToast(e.getMessage());
        }

        }

    }

