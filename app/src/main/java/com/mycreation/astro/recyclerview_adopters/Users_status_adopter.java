package com.mycreation.astro.recyclerview_adopters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mycreation.astro.R;
import com.mycreation.astro.myutils_pack.ImagePreview_dialogue_util;
import com.mycreation.astro.object_models.User_Model;

import java.util.ArrayList;

public class Users_status_adopter extends RecyclerView.Adapter<Users_status_adopter.CustomHolder> {

   static ArrayList<User_Model> usersList=new ArrayList<>();

    public Users_status_adopter (ArrayList<User_Model> usersListt, Context con){
        usersList=usersListt;

       /* selectedItem.observe((LifecycleOwner) con, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                ImagePreview_dialogue_util.GetImgPrevDialogue(con).OpenImgPrev(usersList.get(integer).getProfilePic());
            }
        });*/
    }

    @NonNull
    @Override
    public CustomHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.users_status_li,parent,false);

        return new CustomHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomHolder holder, int position) {

        User_Model model=usersList.get(position);

        holder.name.setText(model.getUserName());
        holder.status.setText(model.getUserStatus());

        if (model.getProfilePic()!=null){
        Glide.with(holder.itemView.getContext()).load(model.getProfilePic()).into(holder.imageView);}

        if (model.getUserStatus()!=null && model.getUserStatus().equalsIgnoreCase("On Shift")){
            holder.cardView.setCardBackgroundColor(Color.parseColor("#0AF014"));

        }

    }


    @Override
    public int getItemCount() {
        if (usersList!=null){return usersList.size();}
        return 0;
    }

    public static class CustomHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView name;
        TextView status;
        CardView cardView;

        public CustomHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.status_profPic);
            name=itemView.findViewById(R.id.status_name);
            status=itemView.findViewById(R.id.status_user);
            cardView=itemView.findViewById(R.id.cardView6);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImagePreview_dialogue_util.GetImgPrevDialogue(imageView.getContext()).OpenImgPrev(usersList.get(getLayoutPosition()).getProfilePic());
                   // ImagePreview_dialogue_util.GetImgPrevDialogue(itemView.getContext()).OpenImgPrev(imageView.);
                   // selectedItem.setValue(view.getId());

                }
            });
        }
    }



}
