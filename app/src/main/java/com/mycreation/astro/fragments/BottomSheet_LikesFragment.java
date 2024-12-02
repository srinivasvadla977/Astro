package com.mycreation.astro.fragments;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.mycreation.astro.R;

public class BottomSheet_LikesFragment extends BottomSheetDialogFragment {

    static String likedUser="";
    static BottomSheet_LikesFragment likesFragment;
    View view;
    TextView tv;


    public static BottomSheet_LikesFragment getLikes_bottomSheet(String s){
        likedUser=s;
        if (likesFragment==null){likesFragment=new BottomSheet_LikesFragment();}
        return likesFragment;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view=inflater.inflate(R.layout.bottomsheet_likes,container,false);
        tv= view.findViewById(R.id.likersTV);
        tv.setMovementMethod(new ScrollingMovementMethod());

        if (likedUser != null){
            tv.setText(likedUser);
        }

        return view;
    }

}
