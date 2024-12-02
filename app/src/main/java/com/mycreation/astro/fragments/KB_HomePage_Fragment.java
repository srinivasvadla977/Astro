package com.mycreation.astro.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mycreation.astro.R;
import com.mycreation.astro.ViewModel_Custom;
import com.mycreation.astro.myutils_pack.MyUtils;
import com.mycreation.astro.object_models.KnowledgeBase_model;
import com.mycreation.astro.object_models.User_Model;

public class KB_HomePage_Fragment extends Fragment {

    TextView title_TV;
    EditText title_ET;
    TextView content_TV;
    EditText content_ET;

    ImageButton edit_imgBtn;
    LinearLayout submitLayout;
    ImageButton submitBTN;
    ImageButton cancelBTN;
    ImageButton backBTN;

    Context context;
    KnowledgeBase_model curRecord=null;
    User_Model currUser;
    String oldDocId;
    boolean isNewRecord;

    ViewModel_Custom viewModelCustom;

    MyUtils myUtils;
    FragmentManager fragmentManager;

    public KB_HomePage_Fragment() {
        // Required empty public constructor
    }

    public KB_HomePage_Fragment(KnowledgeBase_model model, User_Model currUser){
        this.curRecord=model;
        this.currUser=currUser;
    }

    public KB_HomePage_Fragment(User_Model currUser){
        this.currUser=currUser;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_k_b__home_page_, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context=view.getContext();
        InitializeElements((Activity) context);

        myUtils=new MyUtils(context);

        viewModelCustom= new ViewModelProvider(this).get(ViewModel_Custom.class);

        if (curRecord==null){
            EditModeVisibility();
            isNewRecord=true;
        }else {
            ReadModeVisibility();
            title_TV.setText(curRecord.getApkName());
            content_TV.setText(curRecord.getContent());
            oldDocId= curRecord.getDocId();
            isNewRecord=false;
        }

        submitBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(title_ET.getText())){
                    if (!TextUtils.isEmpty(content_ET.getText())){

                        curRecord=new KnowledgeBase_model(title_ET.getText().toString().trim(), content_ET.getText().toString().trim(), System.currentTimeMillis(), currUser.getUserName(), null);

                        if (isNewRecord){
                            viewModelCustom.AddNewAppKnowledge(curRecord);
                        }else {
                            viewModelCustom.UpdateAppKnowledge(curRecord,oldDocId);
                        }

                        myUtils.ReplaceFragment(KnowledgeBaseFragment.GetKnowledgeBaseFrag(currUser),fragmentManager);

                    }else{
                        content_ET.setError("Please provide some information");
                    }
                } else {
                    title_ET.setError("Please give some title");
                }
            }
        });


        edit_imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title_ET.setText(title_TV.getText());
                content_ET.setText(content_TV.getText());

                EditModeVisibility();
            }
        });

        cancelBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if (!isNewRecord){ReadModeVisibility();}
               else {
                   title_ET.setText("");
                   content_ET.setText("");
               }
            }
        });

        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myUtils.ReplaceFragment(KnowledgeBaseFragment.GetKnowledgeBaseFrag(currUser),fragmentManager);
            }
        });

    content_ET.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            content_ET.setError(null);
        }
    });

    title_ET.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            title_ET.setError(null);
        }
    });

    }


    private void InitializeElements(Activity activity){

        title_TV=activity.findViewById(R.id.title_TV);
        title_ET=activity.findViewById(R.id.title_ET);
        content_TV=activity.findViewById(R.id.content_TV);
        content_ET=activity.findViewById(R.id.content_ET);

        edit_imgBtn=activity.findViewById(R.id.edit_IMGBTN);
        submitBTN=activity.findViewById(R.id.submit_BTN);
        cancelBTN=activity.findViewById(R.id.cancel_BTN);
        backBTN=activity.findViewById(R.id.back_img_btn);

        submitLayout=activity.findViewById(R.id.linearLayout_appOrRej);

        fragmentManager=((AppCompatActivity)context).getSupportFragmentManager();

        //
        content_TV.setMovementMethod(new ScrollingMovementMethod());
        content_ET.setMovementMethod(new ScrollingMovementMethod());

    }

    private void EditModeVisibility(){

        title_TV.setVisibility(View.INVISIBLE);
        content_TV.setVisibility(View.INVISIBLE);
        edit_imgBtn.setVisibility(View.INVISIBLE);

        title_ET.setVisibility(View.VISIBLE);
        content_ET.setVisibility(View.VISIBLE);
        submitLayout.setVisibility(View.VISIBLE);

    }

    private void ReadModeVisibility(){

        title_TV.setVisibility(View.VISIBLE);
        content_TV.setVisibility(View.VISIBLE);
        edit_imgBtn.setVisibility(View.VISIBLE);

        title_ET.setVisibility(View.INVISIBLE);
        content_ET.setVisibility(View.INVISIBLE);
        submitLayout.setVisibility(View.INVISIBLE);

    }



}