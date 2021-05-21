package com.mtha.findmyfriends.ui.message;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectUser;
import com.mtha.findmyfriends.R;
import com.mtha.findmyfriends.data.model.ChatList;
import com.mtha.findmyfriends.data.model.Contact;
import com.mtha.findmyfriends.data.model.ContactsAdapter;
import com.mtha.findmyfriends.data.model.OnItemClick;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {

    private AppCompatActivity appCompatActivity;
    ArrayList<Contact> listContact = new ArrayList<>();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();


    //add new
    private RecyclerView recyclerView;

    Typeface MR, MRR;

    private List<Contact> mContact;
    FrameLayout frameLayout;
    TextView es_descp, es_title;

    private List<ChatList> usersList;
    static OnItemClick onItemClick;
    ContactsAdapter contactAdapter;
    AGConnectUser curUser;

    public ChatFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance(OnItemClick click) {
        onItemClick = click;
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity)
            appCompatActivity = (AppCompatActivity) context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_chat, container, false);

        //add back button on ActionBar
        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        MRR = Typeface.createFromAsset(getContext().getAssets(), "font/myriadregular.ttf");
        MR = Typeface.createFromAsset(getContext().getAssets(), "font/myriad.ttf");

        recyclerView = root.findViewById(R.id.recycler_view);
        frameLayout = root.findViewById(R.id.es_layout);
        es_descp = root.findViewById(R.id.es_descp);
        es_title = root.findViewById(R.id.es_title);

        es_descp.setTypeface(MR);
        es_title.setTypeface(MRR);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        curUser = AGConnectAuth.getInstance().getCurrentUser(); //get current user
        listContact = new ArrayList<>();
        usersList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference().child("chatlist").child(curUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatList chatlist = snapshot.getValue(ChatList.class);
                    usersList.add(chatlist);
                }
                if (usersList.size() == 0) {
                    frameLayout.setVisibility(View.VISIBLE);
                } else {
                    frameLayout.setVisibility(View.GONE);
                }

                chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return root;
    }

    private void chatList() {
        mContact = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference().child("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mContact.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Contact user = snapshot.getValue(Contact.class);
                    for (ChatList chatlist : usersList) {
                        if (user != null && user.getUid() != null && chatlist != null && chatlist.getId() != null &&
                                user.getUid().equals(chatlist.getId())) {
                            mContact.add(user);
                        }
                    }
                }

                Toast.makeText(appCompatActivity,"chat list" + mContact.size(), Toast.LENGTH_LONG).show();
                contactAdapter = new ContactsAdapter(getContext(), onItemClick, mContact, true);
                recyclerView.setAdapter(contactAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}