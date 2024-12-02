package com.mycreation.astro.myutils_pack;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.activity.result.ActivityResultLauncher;

public class FilePickerUtil {

    private ActivityResultLauncher<String> myPicker;

    public FilePickerUtil(ActivityResultLauncher<String> myPicker) {
        this.myPicker = myPicker;
    }

    public void GetUriFromGallery() {
        myPicker.launch("image/*");
    }

    //using camera




}

