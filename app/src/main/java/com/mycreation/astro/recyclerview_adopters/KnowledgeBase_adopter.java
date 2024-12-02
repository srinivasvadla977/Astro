package com.mycreation.astro.recyclerview_adopters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mycreation.astro.R;
import com.mycreation.astro.databinding.KnowledgebaseListItemBinding;
import com.mycreation.astro.fragments.KB_HomePage_Fragment;
import com.mycreation.astro.myutils_pack.MyUtils;
import com.mycreation.astro.object_models.KnowledgeBase_model;
import com.mycreation.astro.object_models.User_Model;

import java.util.ArrayList;

public class KnowledgeBase_adopter extends RecyclerView.Adapter<KnowledgeBase_adopter.CustomViewHolder> {

    ArrayList<KnowledgeBase_model> arrayList=new ArrayList<>();
    Context context;
    static MyUtils myUtils;
    static User_Model curUser;

    public KnowledgeBase_adopter(ArrayList<KnowledgeBase_model> arrayList, Context context, User_Model curU) {
        this.arrayList = arrayList;
        this.context = context;
        this.curUser=curU;
        myUtils= new MyUtils(context);
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        KnowledgebaseListItemBinding binding= DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.knowledgebase_list_item, parent, false);
        return new CustomViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {

        KnowledgeBase_model model= arrayList.get(position);

        holder.binding.setKbLI(model);

        String editor= "Last edit by: "+model.getEditor();
        holder.binding.editorNameTV.setText(editor);
        holder.binding.timeTV.setText(myUtils.GetKBDateFormat(model.getTimeStamp()));
    }

    @Override
    public int getItemCount() {
        if (arrayList!=null){return arrayList.size();}
        return 0;
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {

        KnowledgebaseListItemBinding binding;

        public CustomViewHolder( KnowledgebaseListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;



            // onclick
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                  FragmentManager fragmentManager= ((AppCompatActivity)view.getContext()).getSupportFragmentManager();

                  KB_HomePage_Fragment fragment=new KB_HomePage_Fragment(binding.getKbLI(),curUser);
                  myUtils.ReplaceFragment(fragment,fragmentManager);

                }
            });

        }

    }

}
