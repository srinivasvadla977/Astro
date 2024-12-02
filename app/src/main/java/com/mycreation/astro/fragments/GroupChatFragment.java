package com.mycreation.astro.fragments;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.SENSOR_SERVICE;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.mycreation.astro.R;
import com.mycreation.astro.ViewModel_Custom;
import com.mycreation.astro.myutils_pack.FilePickerUtil;
import com.mycreation.astro.myutils_pack.MyUtils;
import com.mycreation.astro.object_models.GroupChat_model;
import com.mycreation.astro.object_models.LikedItemIndex_model;
import com.mycreation.astro.object_models.User_Model;
import com.mycreation.astro.recyclerview_adopters.GroupChat_adopter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

public class GroupChatFragment extends Fragment {

   RecyclerView recyclerView;
   ImageView sendingImgIV, attachmentIV, captureIV;
   ImageButton removeIMGBTN;
   EditText sendingMsgTV;
   CardView sendMsgBTN,messageCard, messageNavigator;
   TextView newMsgCountTV;

   LinearLayoutManager linearLayoutManager;

   Bitmap cameraBitmap;
   Context context;

   MyUtils myUtils;
   Uri qualityImgUri,lessQualImg,compressedBitmapSavedPath=null;

   static GroupChatFragment myFragment;

   static MutableLiveData<User_Model> currentUserModelLD=new MutableLiveData<>();
   static User_Model currentUserModel;

    //for image picking
    private ActivityResultLauncher<String> myPicker;
    private FilePickerUtil filePickerUtil;

    ArrayList<GroupChat_model> groupChatModelsList=new ArrayList<>();
    GroupChat_adopter adopter;

    boolean curUserStatus;
    boolean isFirstTimeUserModelObserving= true;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor spEditor;

    Snackbar snackbar;
    CoordinatorLayout coordinatorLayout;

    // for locally checking if data change is related to likes
    int tempChatSize;
    int lastReadMsgIndex, lastItem;
    LikedItemIndex_model likedItemIndexModel;


    private GroupChatFragment() {
        // Required empty public constructor
    }

    public GroupChatFragment(MutableLiveData<User_Model> model){
        currentUserModelLD=model;
    }

    @Singleton
    public static Fragment getFragment(@NonNull MutableLiveData<User_Model> model){

        if (myFragment==null){
            myFragment= new GroupChatFragment();
            currentUserModelLD=model;
        }
        return myFragment;
    }

    ViewModel_Custom viewModelCustom;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group_chat, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context=view.getContext();
        InitializeElements((Activity) context);
        myUtils=new MyUtils(context);
        viewModelCustom=new ViewModelProvider(this).get(ViewModel_Custom.class);

        linearLayoutManager= new LinearLayoutManager(context);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addOnScrollListener(recScrollListener);
        sendingMsgTV.setMovementMethod(new ScrollingMovementMethod());

        FragmentManager fragmentManager=getActivity().getSupportFragmentManager();


        currentUserModelLD.observe(getViewLifecycleOwner(), new Observer<User_Model>() {
            @Override
            public void onChanged(User_Model model) {
                currentUserModel=model;
                recyclerView.setAdapter(adopter);
            }
        });


        // FOR RETRIEVING MESSAGES
        viewModelCustom.GetAllChatData().observe(getViewLifecycleOwner(), new Observer<ArrayList<GroupChat_model>>() {
            @Override
            public void onChanged(ArrayList<GroupChat_model> groupChatModels) {

                if (!currentUserModel.isTeamMemberRight()){
                    myUtils.ReplaceFragment(new NewUserFragment(), fragmentManager);
                }

                try {

                    if (tempChatSize == groupChatModels.size() && !groupChatModels.isEmpty() && !isFirstTimeUserModelObserving) {
                        // checking for change in likes
                        myUtils.SortArrayList(groupChatModels);
                        groupChatModelsList.clear();
                        groupChatModelsList.addAll(groupChatModels);
                    }
                    // checking for new message
                    else {
                        // for multiple incoming msg
                        if (groupChatModels.size() - tempChatSize >= 2 && !isFirstTimeUserModelObserving) {
                           // myUtils.MakeLongToast(groupChatModels.size() + "#" + tempChatSize);
                            groupChatModelsList.clear();
                            groupChatModelsList.addAll(groupChatModels);
                            UpdateView_for_multipleMsg(groupChatModels.size() - tempChatSize);
                        } else {

                            groupChatModelsList.clear();
                            groupChatModelsList.addAll(groupChatModels);
                            UpdateView();
                        }
                        tempChatSize = groupChatModels.size();
                    }
                }catch (Exception e){
                    myUtils.MakeLongToast(e.getMessage());
                }

                CheckForChanges();

            }
        });

        // FOR CHANGES IN LIKES
        viewModelCustom.GetLikedItemIndex().observe(getViewLifecycleOwner(), new Observer<LikedItemIndex_model>() {
            @Override
            public void onChanged(LikedItemIndex_model model) {
                likedItemIndexModel=model;
                if (adopter!=null){
                adopter.notifyItemChanged(model.getIndex());}
            }
        });



        // FOR SENDING MESSAGES
        sendMsgBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!TextUtils.isEmpty(sendingMsgTV.getText()) || sendingImgIV.getDrawable()!=null){

                    GroupChat_model model= new GroupChat_model(currentUserModel.getUserName(), sendingMsgTV.getText().toString().trim(),null,System.currentTimeMillis(),null, currentUserModel.getProfilePic(), null, currentUserModel.getUserDeviceId());
                    viewModelCustom.PostMessage(model,compressedBitmapSavedPath);
                   // myUtils.MakeShortToast("msg sent");
                    //
                    sendingMsgTV.setText("");
                    DeleteImage(compressedBitmapSavedPath);
                    compressedBitmapSavedPath=null;
                    MakeInVisible();

                }else {
                    sendingMsgTV.setError("Please enter message");
                }
            }
        });

        //FOR CLEARING ERROR
        sendingMsgTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendingMsgTV.setError(null);
            }
        });

        // FOR ADDING ATTACHMENT
        attachmentIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filePickerUtil.GetUriFromGallery();
            }
        });

        // LOGIC FOR IMAGE PICKER
        myPicker = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            if (result != null) {
                // Handle the returned URI here
                UriToBitmapConverter(result);
                MakeVisible();
            }
        });
        filePickerUtil = new FilePickerUtil(myPicker);

        //ON CLICK FOR CAMERA CAPTURE
        captureIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetUriFromCamera();
                //MakeVisible();
            }
        });

        // ON CLICK FOR REMOVING SELECTED IMAGES
        removeIMGBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteImage(compressedBitmapSavedPath);
                DeleteImage(qualityImgUri);
                compressedBitmapSavedPath=null;
                //sendingImgIV.setImageDrawable(null);
                MakeInVisible();

            }
        });


        messageNavigator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {recyclerView.scrollToPosition(lastReadMsgIndex-1);}catch (Exception e){ myUtils.MakeLongToast(e.getMessage()); recyclerView.scrollToPosition(groupChatModelsList.size()-1);}
            }
        });


    }


    private void InitializeElements(Activity activity){

        recyclerView=activity.findViewById(R.id.group_chat_recview);
        sendingImgIV= activity.findViewById(R.id.sendingImg);
        attachmentIV=activity.findViewById(R.id.attachmentIV);
        captureIV=activity.findViewById(R.id.captureImgIV);
        removeIMGBTN=activity.findViewById(R.id.removeIMGBTN);
        sendingMsgTV=activity.findViewById(R.id.sendingMsgTV);
        sendMsgBTN=activity.findViewById(R.id.cardView4);
        messageCard=activity.findViewById(R.id.messageCard);
        messageNavigator=activity.findViewById(R.id.msgNavigator);
        newMsgCountTV=activity.findViewById(R.id.newMsgCount);

        //
        sharedPreferences= context.getSharedPreferences("my_prefs", Activity.MODE_PRIVATE);
        curUserStatus= sharedPreferences.getBoolean("isCurUserConnected",false);
        spEditor= sharedPreferences.edit();

        tempChatSize=sharedPreferences.getInt("CHAT_SIZE",0);
        lastReadMsgIndex=sharedPreferences.getInt("LAST_READ",0);
      //  lastItem=lastReadMsgIndex;
       // Toast.makeText(context,lastReadMsgIndex+"%",Toast.LENGTH_SHORT).show();
        coordinatorLayout=activity.findViewById(R.id.corLay);

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

        sendingImgIV.setImageBitmap(bitmap);
        compressedBitmapSavedPath=lessQualImg;
    }

    //  USING CAMERA TO GET IMG
    public void GetUriFromCamera(){

        ContentValues contentValues=new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE,"New Pic");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"from camera");
        qualityImgUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent i =new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        i.putExtra(MediaStore.EXTRA_OUTPUT,qualityImgUri);

        startActivityForResult(i,60);

    }

    // for handling camera intent result
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==60 && resultCode==RESULT_OK) {
            UriToBitmapConverter(qualityImgUri);
            MakeVisible();
        }
    }

    // for making img view visible
    private void MakeVisible(){
        sendingImgIV.setVisibility(View.VISIBLE);
        removeIMGBTN.setVisibility(View.VISIBLE);
    }

    // for making img view invincible
    private void MakeInVisible(){
        sendingImgIV.setVisibility(View.GONE);
        removeIMGBTN.setVisibility(View.GONE);
    }

    private void UpdateView(){

        if (lastReadMsgIndex>groupChatModelsList.size()){
            lastReadMsgIndex=groupChatModelsList.size();
        }

       // recyclerView.setLayoutManager(linearLayoutManager);
        myUtils.SortArrayList(groupChatModelsList);
        if (isFirstTimeUserModelObserving) {
            adopter = new GroupChat_adopter(groupChatModelsList, context, currentUserModel);
            recyclerView.setAdapter(adopter);
           // myUtils.MakeShortToast(lastReadMsgIndex+"@");
            try {recyclerView.scrollToPosition(lastReadMsgIndex-1);}catch (Exception e){ myUtils.MakeLongToast(e.getMessage());}
            isFirstTimeUserModelObserving=false;
        }else {
            adopter.notifyItemInserted(groupChatModelsList.size()-1);
            // SMOOTH SCROLLING ONLY IF THE MSG SENT BY CUR USER
            if (currentUserModel.getUserDeviceId().equalsIgnoreCase(groupChatModelsList.get(groupChatModelsList.size()-1).getDeviceId())){
            recyclerView.scrollToPosition(groupChatModelsList.size()-1);}
        }
       // recyclerView.smoothScrollToPosition(groupChatModelsList.size());
    }

    private void UpdateView_for_multipleMsg(int i){

        myUtils.SortArrayList(groupChatModelsList);
        adopter.notifyItemRangeInserted((groupChatModelsList.size()-i),(groupChatModelsList.size()-1));
        try {recyclerView.scrollToPosition(lastReadMsgIndex-1);}catch (Exception e){ myUtils.MakeLongToast(e.getMessage());}
    }

    private void UpdateView_forProfPic(){



    }

    @Override
    public void onStop() {

        spEditor.putInt("CHAT_SIZE",tempChatSize);
        spEditor.putInt("LAST_READ",lastReadMsgIndex);
        spEditor.commit();
        super.onStop();
    }

    @Override
    public void onResume() {
        isFirstTimeUserModelObserving=true;
       // lastReadMsgIndex=sharedPreferences.getInt("LAST_READ",0);

        super.onResume();
    }

    // deleting unwanted image
    private void DeleteImage(Uri path) {


        ContentResolver resolver=context.getContentResolver();
        if (path!=null){
            resolver.delete(path,null,null);
           // myUtils.MakeShortToast(rowsDeleted+"#");
        }

    }



    //
    private final RecyclerView.OnScrollListener recScrollListener= new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            int visibleItemCount=linearLayoutManager.getChildCount();
           // int totalItemCount=linearLayoutManager.getChildCount();
            int firstVisibleItem=linearLayoutManager.findFirstVisibleItemPosition();
            lastItem=firstVisibleItem+visibleItemCount;

            if (lastItem>=lastReadMsgIndex){
                    lastReadMsgIndex=lastItem;
            }

          //  SETTING VISIBILITY OF NAVIGATOR
            CheckForChanges();

        }
    };

    private void CheckForChanges(){

        if (groupChatModelsList.size()>lastItem){
            messageNavigator.setVisibility(View.VISIBLE);

            if (groupChatModelsList.size()-lastReadMsgIndex>0){
            newMsgCountTV.setVisibility(View.VISIBLE);
            String count=groupChatModelsList.size()-lastReadMsgIndex+"";
            newMsgCountTV.setText(count);}

        }else {
            newMsgCountTV.setVisibility(View.GONE);
            messageNavigator.setVisibility(View.GONE);
        }
    }





    }

