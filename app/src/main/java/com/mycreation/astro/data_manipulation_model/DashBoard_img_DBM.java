package com.mycreation.astro.data_manipulation_model;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mycreation.astro.myutils_pack.MyUtils;

public class DashBoard_img_DBM {

    Application apk;
    MyUtils myUtils;

    public DashBoard_img_DBM(Application apk) {
        this.apk = apk;
        FirebaseApp.initializeApp(apk.getApplicationContext());
        myUtils=new MyUtils(apk.getApplicationContext());
    }

    FirebaseStorage storage=FirebaseStorage.getInstance();
    StorageReference reference= storage.getReference();

    StorageReference filePath =reference.child("DashBoard_Image").child("DB_Img");

    MutableLiveData<String> imageLink= new MutableLiveData<>();

    public void UpdateDashBoardImg(Uri uri){

        filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                myUtils.MakeShortToast("Image has been updated!");
            }
        });
    }

    public MutableLiveData<String> GetDashBoardImg(){

        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                imageLink.setValue(uri.toString());
            }
        });
        return imageLink;
    }

    public void Delete_dashboard_Image(){
        filePath.delete();
        myUtils.MakeShortToast("Image has been deleted!");
    }

}
