package com.mycreation.astro.myutils_pack;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.mycreation.astro.R;
import com.ortiz.touchview.TouchImageView;

import java.util.Objects;


public class ImagePreview_dialogue_util {

    static Context context;
    static ImagePreview_dialogue_util util;

    private ImagePreview_dialogue_util(){
    }

    public static ImagePreview_dialogue_util GetImgPrevDialogue(Context c){
        context=c;
        if (util==null){
            util=new ImagePreview_dialogue_util();
        }
        return util;
    }

    AlertDialog dialog;
    AlertDialog.Builder builder;

    public void OpenImgPrev(String imgUri) {

            builder = new AlertDialog.Builder(context);

            View myView = LayoutInflater.from(context).inflate(R.layout.image_dialogue, null);

            TouchImageView imageView = myView.findViewById(R.id.zoomImgView);



            Glide.with(context).load(imgUri).into(imageView);

            builder.setView(myView);
            dialog = builder.create();
            dialog.getWindow().setLayout(350, 500);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            dialog.show();


    }

    public void OpenImgPrev_usingResource(Bitmap res){

        builder = new AlertDialog.Builder(context);

        View myView = LayoutInflater.from(context).inflate(R.layout.image_dialogue, null);

        TouchImageView imageView = myView.findViewById(R.id.zoomImgView);

        imageView.setImageBitmap(res);

        builder.setView(myView);
        dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).setLayout(350, 500);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();


    }

    public void ShowAstro_profPic(){

        builder = new AlertDialog.Builder(context);

        View myView = LayoutInflater.from(context).inflate(R.layout.image_dialogue, null);

        TouchImageView imageView = myView.findViewById(R.id.zoomImgView);
        imageView.setImageResource(R.drawable.astro_prof_pic_lion);
        imageView.setScaleType(ImageView.ScaleType.CENTER);

        builder.setView(myView);
        dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).setLayout(350, 400);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();


    }


}
