package com.mycreation.astro.recyclerview_adopters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mycreation.astro.R;
import com.mycreation.astro.ViewModel_Custom;
import com.mycreation.astro.databinding.OthersMsgListitemBinding;
import com.mycreation.astro.databinding.OthersMsgNoImgLiBinding;
import com.mycreation.astro.databinding.SelfMsgListitemBinding;
import com.mycreation.astro.databinding.SelfMsgNoImgLiBinding;
import com.mycreation.astro.fragments.BottomSheet_LikesFragment;
import com.mycreation.astro.myutils_pack.ImagePreview_dialogue_util;
import com.mycreation.astro.myutils_pack.MyUtils;
import com.mycreation.astro.object_models.GroupChat_model;
import com.mycreation.astro.object_models.User_Model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.zip.Inflater;

public class GroupChat_adopter extends RecyclerView.Adapter<GroupChat_adopter.CustomViewHolder> {

    private static final int RECEIVED_MSG=1;
    private static final int SENT_MSG=2;

    private static final int RECEIVED_MSG_NO_IMG=3;
    private static final int SENT_MSG_NO_IMG=4;

    Calendar calendar=Calendar.getInstance();
    Calendar prevDayCalender=Calendar.getInstance();
    DateFormat dateFormat=new SimpleDateFormat("(EEE) dd/MMM/yyyy");
    DateFormat timeFormat=new SimpleDateFormat("hh:mm a");
    // cal for msg date
    Calendar yesCal=Calendar.getInstance();
    Calendar todCal=Calendar.getInstance();

    ArrayList<GroupChat_model> groupChatModelsList= new ArrayList<>();
    static ArrayList<GroupChat_model> groupChatModelsList_forLikes= new ArrayList<>();
    Context context;
    static MyUtils myUtils;
    static User_Model currentUser;

    public static ViewModel_Custom viewModelCustom;
    static ArrayList<User_Model> allUsersList=new ArrayList<>();

    public GroupChat_adopter(@NonNull ArrayList<GroupChat_model> groupChatModelsList,@NonNull Context context, User_Model currentUserr) {
        this.groupChatModelsList = groupChatModelsList;
        groupChatModelsList_forLikes= groupChatModelsList;
        this.context = context;
        myUtils=new MyUtils(context);
        currentUser=currentUserr;

       // myUtils.MakeLongToast(groupChatModelsList.size()+"");

        viewModelCustom=new ViewModelProvider((ViewModelStoreOwner) context).get(ViewModel_Custom.class);

        viewModelCustom.GetAllUsers().observe((LifecycleOwner) context, new Observer<ArrayList<User_Model>>() {
            @Override
            public void onChanged(ArrayList<User_Model> models) {
                allUsersList.clear();
                allUsersList.addAll(models);
            }
        });

        yesCal.add(Calendar.DATE,-1);
    }

    @Override
    public int getItemViewType(int position) {
        if (currentUser.getUserDeviceId().equalsIgnoreCase(groupChatModelsList.get(position).getDeviceId())){
            if (groupChatModelsList.get(position).getImageUrl()!=null){
            return SENT_MSG;
            }else { return SENT_MSG_NO_IMG; }
        }
        else {
            if (groupChatModelsList.get(position).getImageUrl()!=null) {
                return RECEIVED_MSG;
            }else { return RECEIVED_MSG_NO_IMG; }
        }
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType==RECEIVED_MSG){
            OthersMsgListitemBinding receivedItemBinding= DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.others_msg_listitem,parent,false);
            return new CustomViewHolder(receivedItemBinding);
        }
        else if (viewType==SENT_MSG){
            SelfMsgListitemBinding selfMsgListitemBinding=DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),R.layout.self_msg_listitem,parent,false);
            return new CustomViewHolder(selfMsgListitemBinding);
        }
        else if (viewType==RECEIVED_MSG_NO_IMG) {
            OthersMsgNoImgLiBinding othersMsgNoImgLiBinding=DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),R.layout.others_msg_no_img_li,parent,false);
            return new CustomViewHolder(othersMsgNoImgLiBinding);
        }
        else {
            SelfMsgNoImgLiBinding selfMsgNoImgLiBinding= DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),R.layout.self_msg_no_img_li,parent,false);
            return new CustomViewHolder(selfMsgNoImgLiBinding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {

        boolean hasSameSender= false;
        boolean hasSameDate= false;

        GroupChat_model model= groupChatModelsList.get(position);
        calendar.setTimeInMillis(model.getTimeStamp());


        // CHECKING IF DATE AND SENDER ARE SAME AS EARLIER MSG
        if (position!=0){

            // VISIBILITY LOGIC for date TV
            GroupChat_model previousMsgModel = groupChatModelsList.get(position-1);
            prevDayCalender.setTimeInMillis(previousMsgModel.getTimeStamp());

            if (dateFormat.format(calendar.getTime()).equalsIgnoreCase(dateFormat.format(prevDayCalender.getTime()))){
                hasSameDate=true;
            }

            // CHECKING IF SAME PROFILE PIC
            if (model.getDeviceId().equalsIgnoreCase(previousMsgModel.getDeviceId())){
                hasSameSender=true;
            }

        }

        //COUNTING NUMBER OF LIKES
        int likeCount=0;
        String likes= model.getLikes();
        if (likes!=null) {
            for (char c : likes.toCharArray()) {
                if (c == '#') {
                    likeCount++;
                }
            }
        }

        // for checking if today or yesterday msg
        //yesCal.set(Calendar.DAY_OF_MONTH,todCal.get(Calendar.DAY_OF_MONTH-1));
        //yesCal.add(Calendar.DATE,-1);

        // SET UP FOR RECEIVED MESSAGE ITEM
        if (holder.getItemViewType()==RECEIVED_MSG){

            holder.receivedItemBinding.setReceivedMessageListItem(model);

           // holder.receivedItemBinding.dateTv.setText(dateFormat.format(calendar.getTime()));
            holder.receivedItemBinding.dateTv.setText(CheckIfTodayOrYesterday(model.getTimeStamp()));
            holder.receivedItemBinding.timeTv.setText(timeFormat.format(calendar.getTime()));

            // set visibility of date and profPic
            if (hasSameDate){ holder.receivedItemBinding.dateTv.setVisibility(View.GONE); }
            else { holder.receivedItemBinding.dateTv.setVisibility(View.VISIBLE); }

            if (hasSameSender){holder.receivedItemBinding.cardView.setVisibility(View.INVISIBLE);
                holder.receivedItemBinding.userName.setVisibility(View.GONE);
            }
            else {holder.receivedItemBinding.cardView.setVisibility(View.VISIBLE);
                holder.receivedItemBinding.userName.setVisibility(View.VISIBLE);
            }

            // SETTING MESSAGE TV INVINCIBLE
            if (model.getMessage()!=null && !model.getMessage().equalsIgnoreCase("")){
                holder.receivedItemBinding.messageTv.setVisibility(View.VISIBLE);
            }else {
                holder.receivedItemBinding.messageTv.setVisibility(View.GONE);
            }

            if (model.getProfilePic()!=null && !((Activity) context).isFinishing() && !((Activity) context).isDestroyed()){
                Glide.with(context).load(model.getProfilePic()).into(holder.receivedItemBinding.picImg);
            }else {
                if (model.getDeviceId().equalsIgnoreCase("12345")){
                    holder.receivedItemBinding.picImg.setImageResource(R.drawable.astro_prof_pic_lion);
                }else{
                holder.receivedItemBinding.picImg.setImageResource(R.drawable.round_person_24);}
            }
            // MAKING SHARED IMG VIEW VISIBLE ONLY WHEN URL IS NOT NULL
            if (model.getImageUrl()!=null && !((Activity) context).isFinishing() && !((Activity) context).isDestroyed()){
                holder.receivedItemBinding.imgCard.setVisibility(View.VISIBLE);
                Glide.with(context).load(model.getImageUrl()).into(holder.receivedItemBinding.sharedImg);
            }else{
                holder.receivedItemBinding.imgCard.setVisibility(View.INVISIBLE);
                holder.receivedItemBinding.sharedImg.setImageDrawable(null);
            }

            //add logic to likes below
            if (likeCount!=0) {
               // String temp= new String(Character.toChars(0x1F60A))+" "+likeCount;
                String temp= "\uD83D\uDC4D\uD83C\uDFFD "+likeCount;
                holder.receivedItemBinding.likesTv.setText(temp);
                holder.receivedItemBinding.likesCard.setVisibility(View.VISIBLE);
            }
            else {
                holder.receivedItemBinding.likesCard.setVisibility(View.GONE);
            }



        }  //SETUP FOR SENT MEG ITEMS
        else if (holder.getItemViewType()==SENT_MSG){

            holder.sentItemBinding.setOwnMsgLI(model);

           // holder.sentItemBinding.selfDateTv.setText(dateFormat.format(calendar.getTime()));
            holder.sentItemBinding.selfDateTv.setText(CheckIfTodayOrYesterday(model.getTimeStamp()));
            holder.sentItemBinding.sendTimeTv.setText(timeFormat.format(calendar.getTime()));

            //  set visibility of date and profPic
            if (hasSameDate){ holder.sentItemBinding.selfDateTv.setVisibility(View.GONE);}
            else { holder.sentItemBinding.selfDateTv.setVisibility(View.VISIBLE); }

            if (hasSameSender){ holder.sentItemBinding.cardView.setVisibility(View.INVISIBLE); }
            else { holder.sentItemBinding.cardView.setVisibility(View.VISIBLE); }

            if (model.getMessage()!=null && !model.getMessage().equalsIgnoreCase("")){
                holder.sentItemBinding.sendMessageTv.setVisibility(View.VISIBLE);
            }else {
                holder.sentItemBinding.sendMessageTv.setVisibility(View.GONE);
            }

            String profPic="";
            if (currentUser.getProfilePic()!=null){
                profPic= currentUser.getProfilePic();
            } else if (model.getProfilePic()!=null) {
                profPic= model.getProfilePic();
            }

            if (profPic!=null && !((Activity) context).isFinishing() && !((Activity) context).isDestroyed()){
                Glide.with(context).load(profPic).into(holder.sentItemBinding.selfPicImg);
            }else {
                holder.sentItemBinding.selfPicImg.setImageResource(R.drawable.round_person_24);
            }


            /*if (model.getProfilePic()!=null){
                //Glide.with(context).load(model.getProfilePic()).into(holder.sentItemBinding.selfPicImg);

                Glide.with(context).load(currentUser.getProfilePic()).into(holder.sentItemBinding.selfPicImg);
            }*/


            if (model.getImageUrl()!=null && !((Activity) context).isFinishing() && !((Activity) context).isDestroyed()){
                holder.sentItemBinding.cardView2.setVisibility(View.VISIBLE);
                Glide.with(context).load(model.getImageUrl()).into(holder.sentItemBinding.sendSharedImg);
            }else{
                holder.sentItemBinding.cardView2.setVisibility(View.INVISIBLE);
                holder.sentItemBinding.sendSharedImg.setImageDrawable(null);
            }

            //add logic to likes below
            if (likeCount!=0) {
               // String temp= new String(Character.toChars(0x1F60A))+" "+likeCount;
                String temp= "\uD83D\uDC4D\uD83C\uDFFD "+likeCount;
                holder.sentItemBinding.likesSendTv.setText(temp);
                holder.sentItemBinding.likesCard.setVisibility(View.VISIBLE);
            }
            else {
                holder.sentItemBinding.likesCard.setVisibility(View.GONE);
            }


        }
        //SETUP FOR SENT MSG WITHOUT IMG
        else if (holder.getItemViewType()==SENT_MSG_NO_IMG){

            holder.selfMsgNoImgLiBinding.setOwnMsgLI(model);

            //holder.selfMsgNoImgLiBinding.selfDateTv.setText(dateFormat.format(calendar.getTime()));
            holder.selfMsgNoImgLiBinding.selfDateTv.setText(CheckIfTodayOrYesterday(model.getTimeStamp()));
            holder.selfMsgNoImgLiBinding.sendTimeTv.setText(timeFormat.format(calendar.getTime()));

            //  set visibility of date and profPic
            if (hasSameDate){ holder.selfMsgNoImgLiBinding.selfDateTv.setVisibility(View.GONE);}
            else { holder.selfMsgNoImgLiBinding.selfDateTv.setVisibility(View.VISIBLE); }

            if (hasSameSender){ holder.selfMsgNoImgLiBinding.cardView.setVisibility(View.INVISIBLE); }
            else { holder.selfMsgNoImgLiBinding.cardView.setVisibility(View.VISIBLE); }

            // visibility for message tv
            if (model.getMessage()!=null && !model.getMessage().equalsIgnoreCase("")){
                holder.selfMsgNoImgLiBinding.sendMessageTv.setVisibility(View.VISIBLE);
            }else {
                holder.selfMsgNoImgLiBinding.sendMessageTv.setVisibility(View.GONE);
            }

            // PROFILE PIC LOGIC
            String profPic="";
            if (currentUser.getProfilePic()!=null){
                profPic= currentUser.getProfilePic();
            } else if (model.getProfilePic()!=null) {
                profPic= model.getProfilePic();
            }

            if (profPic!=null && !((Activity) context).isFinishing() && !((Activity) context).isDestroyed()){
                Glide.with(context).load(profPic).into(holder.selfMsgNoImgLiBinding.selfPicImg);
            }else {
                holder.selfMsgNoImgLiBinding.selfPicImg.setImageResource(R.drawable.round_person_24);
            }

            /*if (model.getProfilePic()!=null){
                //Glide.with(context).load(model.getProfilePic()).into(holder.selfMsgNoImgLiBinding.selfPicImg);
                Glide.with(context).load(currentUser.getProfilePic()).into(holder.selfMsgNoImgLiBinding.selfPicImg);
            }*/

            //add logic to likes below
            if (likeCount!=0) {
                // String temp= new String(Character.toChars(0x1F60A))+" "+likeCount;
                String temp= "\uD83D\uDC4D\uD83C\uDFFD "+likeCount;
                holder.selfMsgNoImgLiBinding.likesSendTv.setText(temp);
                holder.selfMsgNoImgLiBinding.likesCard.setVisibility(View.VISIBLE);
            }
            else {
                holder.selfMsgNoImgLiBinding.likesCard.setVisibility(View.GONE);
            }

        }
        //SETUP FOR RECEIVED MSG WITHOUT IMG
        else {

            holder.othersMsgNoImgLiBinding.setReceivedMessageListItem(model);

           // holder.othersMsgNoImgLiBinding.dateTv.setText(dateFormat.format(calendar.getTime()));
            holder.othersMsgNoImgLiBinding.dateTv.setText(CheckIfTodayOrYesterday(model.getTimeStamp()));
            holder.othersMsgNoImgLiBinding.timeTv.setText(timeFormat.format(calendar.getTime()));

            // set visibility of date and profPic
            if (hasSameDate){ holder.othersMsgNoImgLiBinding.dateTv.setVisibility(View.GONE); }
            else { holder.othersMsgNoImgLiBinding.dateTv.setVisibility(View.VISIBLE); }

            if (hasSameSender){holder.othersMsgNoImgLiBinding.cardView.setVisibility(View.INVISIBLE);
                                holder.othersMsgNoImgLiBinding.userName.setVisibility(View.GONE);}
            else {holder.othersMsgNoImgLiBinding.cardView.setVisibility(View.VISIBLE);
                 holder.othersMsgNoImgLiBinding.userName.setVisibility(View.VISIBLE);}

            // SETTING MESSAGE TV INVINCIBLE
            if (model.getMessage()!=null && !model.getMessage().equalsIgnoreCase("")){
                holder.othersMsgNoImgLiBinding.messageTv.setVisibility(View.VISIBLE);
            }else {
                holder.othersMsgNoImgLiBinding.messageTv.setVisibility(View.GONE);
            }

            if (model.getProfilePic()!=null && !((Activity) context).isFinishing() && !((Activity) context).isDestroyed()){
                Glide.with(context).load(model.getProfilePic()).into(holder.othersMsgNoImgLiBinding.picImg);
            }else {
                if (model.getDeviceId().equalsIgnoreCase("12345")){
                    holder.othersMsgNoImgLiBinding.picImg.setImageResource(R.drawable.astro_prof_pic_lion);
                }else{
                holder.othersMsgNoImgLiBinding.picImg.setImageResource(R.drawable.round_person_24);}
            }

            //add logic to likes below
            if (likeCount!=0) {
                // String temp= new String(Character.toChars(0x1F60A))+" "+likeCount;
                String temp= "\uD83D\uDC4D\uD83C\uDFFD "+likeCount;
                holder.othersMsgNoImgLiBinding.likesTv.setText(temp);
                holder.othersMsgNoImgLiBinding.likesCard.setVisibility(View.VISIBLE);
            }
            else {
                holder.othersMsgNoImgLiBinding.likesCard.setVisibility(View.GONE);
            }

        }


    }

    @Override
    public int getItemCount() {
        if (groupChatModelsList!=null){return groupChatModelsList.size();}
        return 0;
    }


    public static class CustomViewHolder extends RecyclerView.ViewHolder{

        OthersMsgListitemBinding receivedItemBinding;
        SelfMsgListitemBinding sentItemBinding;

        SelfMsgNoImgLiBinding selfMsgNoImgLiBinding;
        OthersMsgNoImgLiBinding othersMsgNoImgLiBinding;


        // view holder for incoming msg with img
        public CustomViewHolder(@NonNull OthersMsgListitemBinding receivedItemBinding) {
            super(receivedItemBinding.getRoot());
            this.receivedItemBinding=receivedItemBinding;
            //onClick
            receivedItemBinding.getRoot().setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    // for updating liked item index
                    //int i= groupChatModelsList_forLikes.indexOf(receivedItemBinding.getReceivedMessageListItem());
                    int i=getLayoutPosition();
                   // viewModelCustom.PostLikedItemIndex(i);

                    viewModelCustom.LikeMessage(CheckIfCurrentUserVoted(receivedItemBinding.getReceivedMessageListItem(),receivedItemBinding.likesCard),i);

                    return false;
                }
            });

            receivedItemBinding.likesCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ShowLikersInBottomSheet(receivedItemBinding.getReceivedMessageListItem().getLikes(), itemView.getContext());
                }
            });

            receivedItemBinding.sharedImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImagePreview_dialogue_util.GetImgPrevDialogue(itemView.getContext()).OpenImgPrev(receivedItemBinding.getReceivedMessageListItem().getImageUrl());
                }
            });

            receivedItemBinding.picImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (receivedItemBinding.getReceivedMessageListItem().getDeviceId().equalsIgnoreCase("12345")){
                        ImagePreview_dialogue_util.GetImgPrevDialogue(itemView.getContext()).ShowAstro_profPic();
                    }else {
                    ImagePreview_dialogue_util.GetImgPrevDialogue(itemView.getContext()).OpenImgPrev(receivedItemBinding.getReceivedMessageListItem().getProfilePic());
                }}
            });

        }

        // view holder for sent message with img
        public CustomViewHolder(@NonNull SelfMsgListitemBinding selfMsgListitemBinding){
            super(selfMsgListitemBinding.getRoot());
            this.sentItemBinding=selfMsgListitemBinding;

            //onClick
            selfMsgListitemBinding.likesCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ShowLikersInBottomSheet(sentItemBinding.getOwnMsgLI().getLikes(), itemView.getContext());
                }
            });

            selfMsgListitemBinding.sendSharedImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImagePreview_dialogue_util.GetImgPrevDialogue(itemView.getContext()).OpenImgPrev(selfMsgListitemBinding.getOwnMsgLI().getImageUrl());
                }
            });

            selfMsgListitemBinding.selfPicImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //ImagePreview_dialogue_util.GetImgPrevDialogue(itemView.getContext()).OpenImgPrev(selfMsgListitemBinding.getOwnMsgLI().getProfilePic());
                    ImagePreview_dialogue_util.GetImgPrevDialogue(itemView.getContext()).OpenImgPrev(currentUser.getProfilePic());
                }
            });

        }

        // view holder for sent message without img
        public CustomViewHolder(@NonNull SelfMsgNoImgLiBinding selfMsgNoImgLiBinding){
            super(selfMsgNoImgLiBinding.getRoot());
            this.selfMsgNoImgLiBinding=selfMsgNoImgLiBinding;

            selfMsgNoImgLiBinding.likesCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ShowLikersInBottomSheet(selfMsgNoImgLiBinding.getOwnMsgLI().getLikes(), itemView.getContext());
                }
            });

            selfMsgNoImgLiBinding.selfPicImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   // ImagePreview_dialogue_util.GetImgPrevDialogue(itemView.getContext()).OpenImgPrev(selfMsgNoImgLiBinding.getOwnMsgLI().getProfilePic());
                    ImagePreview_dialogue_util.GetImgPrevDialogue(itemView.getContext()).OpenImgPrev(currentUser.getProfilePic());
                }
            });

        }

        // view holder for received message without img
        public CustomViewHolder(@NonNull  OthersMsgNoImgLiBinding othersMsgNoImgLiBinding){
            super(othersMsgNoImgLiBinding.getRoot());
            this.othersMsgNoImgLiBinding=othersMsgNoImgLiBinding;

            othersMsgNoImgLiBinding.getRoot().setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    // for updating liked item index
                   // int i= groupChatModelsList_forLikes.indexOf(othersMsgNoImgLiBinding.getReceivedMessageListItem());
                   int i=getLayoutPosition();
                   // viewModelCustom.PostLikedItemIndex(i);

                    viewModelCustom.LikeMessage(CheckIfCurrentUserVoted(othersMsgNoImgLiBinding.getReceivedMessageListItem(),othersMsgNoImgLiBinding.likesCard),i);

                    return false;
                }
            });

            othersMsgNoImgLiBinding.likesCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ShowLikersInBottomSheet(othersMsgNoImgLiBinding.getReceivedMessageListItem().getLikes(), itemView.getContext());
                }
            });

            othersMsgNoImgLiBinding.picImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (othersMsgNoImgLiBinding.getReceivedMessageListItem().getDeviceId().equalsIgnoreCase("12345")){
                        ImagePreview_dialogue_util.GetImgPrevDialogue(itemView.getContext()).ShowAstro_profPic();
                    }else {
                    ImagePreview_dialogue_util.GetImgPrevDialogue(itemView.getContext()).OpenImgPrev(othersMsgNoImgLiBinding.getReceivedMessageListItem().getProfilePic());
                }}
            });

        }


        private GroupChat_model CheckIfCurrentUserVoted(GroupChat_model model, CardView likesCard){
            if (model.getLikes()!=null && model.getLikes().contains(currentUser.getUserDeviceId())){
                model.setLikes(model.getLikes().replace("#"+currentUser.getUserDeviceId(),""));
            }else {
                if (model.getLikes()!=null){
                model.setLikes(model.getLikes()+"#"+currentUser.getUserDeviceId());}
                else {
                    model.setLikes("#"+currentUser.getUserDeviceId());
                }
                //likesCard.setVisibility(View.VISIBLE);
            }
         /*   if (model.getLikes()!=null || !model.getLikes().equalsIgnoreCase("")){
                likesCard.setVisibility(View.VISIBLE);
            }else { likesCard.setVisibility(View.GONE); }*/
            return model;
        }

        private void ShowLikersInBottomSheet(String string, Context c){
            // ADD LOGIC TO SHOW LIKERS IN BOTTOM SHEET BELOW

           BottomSheet_LikesFragment bottomSheet= BottomSheet_LikesFragment.getLikes_bottomSheet(myUtils.GetUserName_from_deviceId(string,allUsersList));
           bottomSheet.show(((AppCompatActivity)c).getSupportFragmentManager(),bottomSheet.getTag());
        }


    }

    DateFormat compDateFormat= new SimpleDateFormat("dd/MM/yyyy");
    Calendar modCal= Calendar.getInstance();

    private String CheckIfTodayOrYesterday(long millis){

        modCal.setTimeInMillis(millis);

        if (compDateFormat.format(modCal.getTime()).equalsIgnoreCase(compDateFormat.format(todCal.getTime()))){
            return "Today";
        }
        else if(compDateFormat.format(modCal.getTime()).equalsIgnoreCase(compDateFormat.format(yesCal.getTime()))){
            return "Yesterday";
        }else {
            return dateFormat.format(calendar.getTime());
        }

    }





}
