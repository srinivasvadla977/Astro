package com.mycreation.astro.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mycreation.astro.R;
import com.mycreation.astro.ViewModel_Custom;
import com.mycreation.astro.myutils_pack.MyUtils;
import com.mycreation.astro.object_models.KnowledgeBase_model;
import com.mycreation.astro.object_models.User_Model;
import com.mycreation.astro.recyclerview_adopters.KnowledgeBase_adopter;

import java.util.ArrayList;


public class KnowledgeBaseFragment extends Fragment {

    Context context;
    private static User_Model currentUserModel;

    static MyUtils myUtils;

    RecyclerView recyclerView;
    FloatingActionButton fab;

    FragmentManager fragmentManager;
    static KnowledgeBaseFragment knowledgeBaseFragment;

    ArrayList<KnowledgeBase_model> knowledgeBaseModelsList= new ArrayList<>();
    KnowledgeBase_adopter adopter;

    ViewModel_Custom viewModelCustom;

    private KnowledgeBaseFragment() {
        // Required empty public constructor
    }

    /*public KnowledgeBaseFragment(User_Model currentUserModel) {
        this.currentUserModel = currentUserModel;
    }*/

    public static synchronized Fragment GetKnowledgeBaseFrag(User_Model model){


        if (knowledgeBaseFragment==null){
            currentUserModel=model;
            knowledgeBaseFragment= new KnowledgeBaseFragment();
        }
        return knowledgeBaseFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_knowledge_base, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context=view.getContext();
        recyclerView= ((Activity)context).findViewById(R.id.recView);
        fab= ((Activity)context).findViewById(R.id.fab);
        myUtils=new MyUtils(context);

        fragmentManager= getActivity().getSupportFragmentManager();
        viewModelCustom=new ViewModelProvider(this).get(ViewModel_Custom.class);

        viewModelCustom.GetAllApkKnowledge().observe(getViewLifecycleOwner(), new Observer<ArrayList<KnowledgeBase_model>>() {
            @Override
            public void onChanged(ArrayList<KnowledgeBase_model> knowledgeBaseModels) {
                knowledgeBaseModelsList.clear();
                knowledgeBaseModelsList.addAll(knowledgeBaseModels);
               // myUtils.MakeShortToast("data changed");
                UpdateRecView();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myUtils.ReplaceFragment(new KB_HomePage_Fragment(currentUserModel), fragmentManager);
            }
        });


    }


    private void UpdateRecView(){

        myUtils.SortArrayListKB(knowledgeBaseModelsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adopter=new KnowledgeBase_adopter(knowledgeBaseModelsList,context, currentUserModel);
        recyclerView.setAdapter(adopter);

    }

}