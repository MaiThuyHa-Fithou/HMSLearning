package com.mtha.findmyfriends.data.model;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mtha.findmyfriends.R;
import com.mtha.findmyfriends.ui.message.MessageActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsAdapter  extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    private Context mContext;
    private List<Contact> mContacts;
    private boolean ischat;
    private OnItemClick onItemClick;

    Typeface MR,MRR;
    String theLastMessage;

    public ContactsAdapter(Context mContext, OnItemClick onItemClick,List<Contact> mContacts,boolean ischat){
        this.onItemClick = onItemClick;
        this.mContacts = mContacts;
        this.mContext = mContext;
        this.ischat = ischat;

        if(mContext!=null) {
            MRR = Typeface.createFromAsset(mContext.getAssets(), "font/myriadregular.ttf");
            MR = Typeface.createFromAsset(mContext.getAssets(), "font/myriad.ttf");
        }

    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.contact_item,parent,false);
        return new ContactsAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        final Contact contact = mContacts.get(position);
        holder.username.setTypeface(MR);
        holder.last_msg.setTypeface(MRR);
        holder.username.setText(contact.getFullName());
        if (contact.getImage().equals("avatar.jpg")){
            holder.profile_image.setImageResource(R.drawable.avatar);
        } else {
            Glide.with(mContext).load(contact.getImage()).into(holder.profile_image);
        }

        if (ischat){
            lastMessage(contact.getUid(), holder.last_msg);
        } else {
            holder.last_msg.setVisibility(View.GONE);
        }

        if (ischat){
            if (contact.getStatus().equals("online")){
                holder.img_on.setVisibility(View.VISIBLE);
                holder.img_off.setVisibility(View.GONE);
            } else {
                holder.img_on.setVisibility(View.GONE);
                holder.img_off.setVisibility(View.GONE);
            }
        } else {
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("userid", contact.getUid());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }


    //check for last message
    private void lastMessage(final String userid, final TextView last_msg){
        theLastMessage = "default";
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersReference = database.getReference().child("users");
        DatabaseReference reference = database.getReference().child("messages");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (usersReference.child(userid) != null && chat != null) {
                        if (chat.getReceiver().equals(usersReference.child(userid).getKey()) && chat.getSender().equals(userid) ||
                                chat.getReceiver().equals(userid) && chat.getSender().equals(usersReference.child(userid).getKey())) {
                            theLastMessage = chat.getMessage();
                        }
                    }
                }

                switch (theLastMessage){
                    case  "default":
                        last_msg.setText("No Message");
                        break;

                    default:
                        last_msg.setText(theLastMessage);
                        break;
                }

                theLastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public  class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public CircleImageView profile_image;
        private ImageView img_on;
        private ImageView img_off;
        private TextView last_msg;

        public ViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
            last_msg = itemView.findViewById(R.id.last_msg);
        }
    }
}
