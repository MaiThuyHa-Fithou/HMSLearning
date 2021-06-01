package com.mtha.findmyfriends.ui.listfriend;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectUser;
import com.mtha.findmyfriends.R;
import com.mtha.findmyfriends.data.model.ChatList;
import com.mtha.findmyfriends.data.model.Contact;
import com.mtha.findmyfriends.data.model.ContactAdapter;
import com.mtha.findmyfriends.data.model.ContactsAdapter;
import com.mtha.findmyfriends.data.model.OnItemClick;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class ListFriendFragment extends Fragment {
    private AppCompatActivity appCompatActivity;
    private ListFriendViewModel addFriendViewModel;
    ArrayList<Contact> listContact = new ArrayList<>();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();


    //add new
    private RecyclerView recyclerView;

    Typeface MR, MRR;
    FrameLayout frameLayout;
    TextView es_descp, es_title;

    private ContactsAdapter userAdapter;
    private List<Contact> mUsers;
    static OnItemClick onItemClick;

    EditText search_users;
    AGConnectUser curUser;

    public static ListFriendFragment newInstance(OnItemClick click) {

        onItemClick = click;
        Bundle args = new Bundle();

        ListFriendFragment fragment = new ListFriendFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_listfriend, container, false);

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
        mUsers = new ArrayList<>();

        readUsers();

        search_users = root.findViewById(R.id.search_users);
        search_users.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUsers(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        return root;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity)
            appCompatActivity = (AppCompatActivity) context;
    }

    private void searchUsers(String s) {

        Query query = FirebaseDatabase.getInstance().getReference().child("users").orderByChild("fullName")
                .startAt(s)
                .endAt(s + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Contact user = snapshot.getValue(Contact.class);

                    assert user != null;
                    assert curUser != null;
                    if (!user.getUid().equals(curUser.getUid())) {
                        mUsers.add(user);
                    }
                }

                userAdapter = new ContactsAdapter(getContext(), onItemClick, mUsers, false);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void readUsers() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (search_users.getText().toString().equals("")) {
                    mUsers.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Contact user = snapshot.getValue(Contact.class);

                        if (user != null && user.getUid() != null &&
                                curUser != null && !user.getUid().equals(curUser.getUid())) {
                            mUsers.add(user);

                        }
                    }


                    if (mUsers.size() == 0) {
                        frameLayout.setVisibility(View.VISIBLE);
                    } else {
                        frameLayout.setVisibility(View.GONE);
                    }

                    userAdapter = new ContactsAdapter(getContext(), onItemClick, mUsers, false);
                    recyclerView.setAdapter(userAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}