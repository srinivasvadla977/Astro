package com.mycreation.astro.fragments;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.materialswitch.MaterialSwitch;
import com.mycreation.astro.MainActivity;
import com.mycreation.astro.R;
import com.mycreation.astro.ViewModel_Custom;
import com.mycreation.astro.data_manipulation_model.ReqCodeTokenGenerator;
import com.mycreation.astro.myutils_pack.DayLightSaver_util;
import com.mycreation.astro.myutils_pack.FilePickerUtil;
import com.mycreation.astro.myutils_pack.ImagePreview_dialogue_util;
import com.mycreation.astro.myutils_pack.MyGlobalUtil;
import com.mycreation.astro.myutils_pack.MyUtils;
import com.mycreation.astro.object_models.NewTaskModel;
import com.mycreation.astro.object_models.ReqCode_model;
import com.mycreation.astro.object_models.User_Model;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class NewTaskFragment extends Fragment {

    static NewTaskFragment newTaskFragment;

    Button submitButton;
    MyUtils myUtils;
    MyGlobalUtil myGlobalUtil;

    private ViewModel_Custom viewModelCustom;

    Context context;
    RadioGroup radioGroupType,radioGroupFreq;
    TextView timeTv, dateTv, noteTv;
    EditText taskTitleEt,taskDescEt;
    LinearLayout freqLinearLayout;
    ImageView addingImg,camBtn,fileBTN,deleteBTN;
    MaterialSwitch timeZoneSwitch;
    CheckBox priorityBox;

    long istCetSettler=0;
    String note="Note: The reminder will come 10 mins early from scheduled time for high priority tasks and 5 mins early for normal tasks";

    NewTaskModel newTaskModel;
    ReqCode_model myReqCodeModel;

    boolean isRepeating=false;
    boolean isDailyTask=false;

    private ActivityResultLauncher<String> myPicker;
    private FilePickerUtil filePickerUtil;

    Uri qualityImgUri,lessQualImg;
    Bitmap cameraBitmap;
    Uri compressedBitmapSavedPath=null;

    static User_Model currentUser;

    public NewTaskFragment() {
        // Required empty public constructor
    }

    public static Fragment getNewTaskFragment(User_Model model){
        currentUser=model;
        if (newTaskFragment==null){newTaskFragment=new NewTaskFragment();}
        return newTaskFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context=view.getContext();
        myUtils=new MyUtils(context);
        Activity activity=(Activity) context;

        radioGroupType=((Activity) context).findViewById(R.id.taskType);
        radioGroupFreq=((Activity) context).findViewById(R.id.freqType);
        timeTv=((Activity) context).findViewById(R.id.timeTV);
        dateTv=((Activity) context).findViewById(R.id.dateTV);
        submitButton=((Activity) context).findViewById(R.id.submitBtn);
        taskTitleEt=((Activity) context).findViewById(R.id.taskTitle);
        taskDescEt=((Activity) context).findViewById(R.id.taskDesc);
        freqLinearLayout=((Activity) context).findViewById(R.id.freqTypeLayout);
        addingImg=((Activity) context).findViewById(R.id.addImg);
        priorityBox=((Activity) context).findViewById(R.id.impCHKB);
        timeZoneSwitch=((Activity) context).findViewById(R.id.timeZoneSwitch);
        noteTv=((Activity) context).findViewById(R.id.noteTVV);

        camBtn=((Activity) context).findViewById(R.id.cam);
        fileBTN=((Activity) context).findViewById(R.id.files);
        deleteBTN=((Activity) context).findViewById(R.id.delete);

        taskDescEt.setMovementMethod(new ScrollingMovementMethod());

        myGlobalUtil= MyGlobalUtil.GetGlobalUtilInstance(context);

        viewModelCustom=new ViewModelProvider(NewTaskFragment.this).get(ViewModel_Custom.class);

        freqLinearLayout.setVisibility(View.GONE);

        radioGroupType.check(R.id.radBtnTypeOnce);
        radioGroupType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i==R.id.radBtnTypeRepeat){isRepeating=true;
                    freqLinearLayout.setVisibility(View.VISIBLE);
                    dateTv.setVisibility(View.INVISIBLE);
                        radioGroupFreq.check(R.id.radBtnFreqDaily);
                        isDailyTask=true;
                }
                else {isRepeating=false;
                    freqLinearLayout.setVisibility(View.GONE);
                    dateTv.setVisibility(View.VISIBLE);
                    isDailyTask=false;
                }
            }
        });

        radioGroupFreq.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i==R.id.radBtnFreqDaily){
                    isDailyTask=true;
                    dateTv.setVisibility(View.INVISIBLE);
                }else {
                    isDailyTask=false;
                    dateTv.setVisibility(View.VISIBLE);
                }
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               if (TextUtils.isEmpty(taskTitleEt.getText())){
                    taskTitleEt.setError("Please enter title!");
                } else if (TextUtils.isEmpty(taskDescEt.getText())) {
                    taskDescEt.setError("Don't forget to add some description!");
                } else if (TextUtils.isEmpty(timeTv.getText())) {
                    timeTv.setError("Select Time");
                } else {

                    viewModelCustom.GetReqCodeToken(new ReqCodeTokenGenerator.DataCallBack() {
                        @Override
                        public void onDataReceived(ReqCode_model reqCodeModel) {
                            if (reqCodeModel!=null){

                                int tempToken= reqCodeModel.getToken();

                                newTaskModel=new NewTaskModel(tempToken,isRepeating,isDailyTask,false,myGlobalUtil.GetTriggerTime()+istCetSettler, taskTitleEt.getText().toString().trim(),taskDescEt.getText().toString().trim(),null,null, currentUser.getUserName(), System.currentTimeMillis(),myGlobalUtil.CheckForWeekend(),priorityBox.isChecked());
                              if (compressedBitmapSavedPath!=null)
                                   {  viewModelCustom.AddNewTaskReminder(newTaskModel,compressedBitmapSavedPath);

                                  }
                               else { viewModelCustom.AddNewTaskReminder(newTaskModel,null);
                                         }

                                // for updating database with new token
                                if (tempToken>=98765432){tempToken=0;}
                                reqCodeModel.setToken(tempToken+1);
                                viewModelCustom.InsertNewToken(reqCodeModel);
                                //
                                ClearFields();
                            }
                        }
                    });

                }

            }
        });

        timeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeTv.setError(null);
                myGlobalUtil.ShowTimePicker(context);

                // setting daylight saving warning to ignore
                if (DayLightSaver_util.getDLUtil().CheckIfDayLight_ApplicableToday_for_dashBoard()){
                    //noteTv.setVisibility(View.VISIBLE);
                    note="Note: The reminder will come 10 mins early from scheduled time for high priority tasks and 5 mins early for normal tasks\nPlease ignore DayLightSaving, the system will auto adjust timings!";
                }else {
                    //noteTv.setVisibility(View.GONE);
                }
                noteTv.setText(note);
            }
        });

        dateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myGlobalUtil.ShowDatePicker(context);
            }
        });

        myGlobalUtil.getTimeLiveData().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                timeTv.setText(s);
            }
        });

        myGlobalUtil.getDateLiveData().observe((LifecycleOwner) activity, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                dateTv.setText(s);
            }
        });


        // image picker
        myPicker = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            if (result != null) {
                // Handle the returned URI here
                UriToBitmapConverter(result);
            }
        });

        filePickerUtil = new FilePickerUtil(myPicker);


        fileBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                filePickerUtil.GetUriFromGallery();
              //  GetUriFromCamera();

                if (compressedBitmapSavedPath!=null){
                  //  DeleteImage(compressedBitmapSavedPath);
                }
            }
        });

        taskTitleEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                taskTitleEt.setError(null);
            }
        });

        taskDescEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                taskDescEt.setError(null);
            }
        });

        camBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetUriFromCamera();
            }
        });

        deleteBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addingImg.setImageResource(R.drawable.dummy_img_lv);
                DeleteImage(compressedBitmapSavedPath);
                compressedBitmapSavedPath=null;
            }
        });



        addingImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    BitmapDrawable drawable= (BitmapDrawable) addingImg.getDrawable();
                    ImagePreview_dialogue_util.GetImgPrevDialogue(context).OpenImgPrev_usingResource(drawable.getBitmap());
                }catch (Exception e){
                    myUtils.MakeShortToast("Please Capture an image or upload it from Files (Optional..!)");
                }

            }
        });

        timeZoneSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    timeZoneSwitch.setText("CET");
                    istCetSettler=12600000;
                }else {
                    timeZoneSwitch.setText("IST");
                   if(DayLightSaver_util.getDLUtil().CheckIfDayLight_ApplicableToday_for_dashBoard()){
                       istCetSettler=-3600000;
                   }else {
                    istCetSettler=0;}
                }
            }
        });


        // MODIFYING TIME IF CUR DAY IS IN D.L.S AND MODE IS IST ON DEFAULT MODE
        if (timeZoneSwitch.isActivated()){
            istCetSettler=12600000;
        }else {
            if (DayLightSaver_util.getDLUtil().CheckIfDayLight_ApplicableToday_for_dashBoard()){
                istCetSettler=-3600000;
            }else {
                istCetSettler=0;
            }
        }

    }


    public void GetUriFromCamera(){

        ContentValues contentValues=new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE,"New Pic");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"from camera");
        qualityImgUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent i =new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        i.putExtra(MediaStore.EXTRA_OUTPUT,qualityImgUri);

        startActivityForResult(i,50);

    }

    // for handling camera intent result
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==50 && resultCode==RESULT_OK) {

           UriToBitmapConverter(qualityImgUri);
          // DeleteImage(qualityImgUri);

        }
    }

    // converts given uri into a bitmap and passes output to bitmapSaver
    private void UriToBitmapConverter(Uri qualityImgUrii) {

        try {
            cameraBitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), qualityImgUrii);
            SaveBitmapImage(cameraBitmap,""+System.currentTimeMillis());
        } catch (IOException e) {
            myUtils.MakeShortToast(e.getMessage());
        }

    }

    // saving camera/gallery image with reducing quality to 20% and...showing in img view
    private void SaveBitmapImage(Bitmap bitmap, @NonNull String name) throws IOException {
        OutputStream fos;

        ContentResolver resolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/JPEG");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/" + "Astro");
        lessQualImg = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        fos = resolver.openOutputStream(lessQualImg);

        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, fos);
        fos.flush();
        fos.close();

        addingImg.setImageBitmap(bitmap);
        compressedBitmapSavedPath=lessQualImg;
    }

// deleting unwanted image
    private void DeleteImage(Uri path) {
        //File file=new File(path);
       // boolean b=file.delete();

        ContentResolver resolver=context.getContentResolver();
        if (path!=null){
        int rowsDeleted=resolver.delete(path,null,null);
        //myUtils.MakeShortToast(rowsDeleted+"#");
        }

        //compressedBitmapSavedPath=null;

    }

    //clear fields on success
    private void ClearFields(){

        timeTv.setText("");
        taskTitleEt.setText("");
        taskDescEt.setText("");
        priorityBox.setChecked(false);

        DeleteImage(compressedBitmapSavedPath);
        compressedBitmapSavedPath=null;
        addingImg.setImageResource(R.drawable.dummy_img_lv);

    }

    @Override
    public void onStop() {
        DeleteImage(compressedBitmapSavedPath);
        super.onStop();
    }



}