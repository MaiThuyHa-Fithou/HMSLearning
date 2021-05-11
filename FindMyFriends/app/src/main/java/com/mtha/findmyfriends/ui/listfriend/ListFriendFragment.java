package com.mtha.findmyfriends.ui.listfriend;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;

import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;


import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mtha.findmyfriends.R;
import com.mtha.findmyfriends.data.model.Contact;
import com.mtha.findmyfriends.data.model.ContactAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class ListFriendFragment extends Fragment  {
    private AppCompatActivity appCompatActivity;
    private ListFriendViewModel addFriendViewModel;
    ArrayList<Contact> listContact = new ArrayList<>();
    FirebaseDatabase database;
    DatabaseReference reference;

    ListView lvContact;
    ContactAdapter contactAdapter;

    SearchView searchView;
    SearchView.OnQueryTextListener queryTextListener;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        addFriendViewModel =
                new ViewModelProvider(this).get(ListFriendViewModel.class);
        View root = inflater.inflate(R.layout.fragment_listfriend, container, false);
        
        //add back button on ActionBar
        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //firebase
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        reference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot data: snapshot.getChildren()) {
                    Contact contact = data.getValue(Contact.class);
                    listContact.add(contact);
                    Toast.makeText(appCompatActivity, ""+contact.getFullName(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.w("TAG", "loadPost:onCancelled", error.toException());
            }
        });
        lvContact = root.findViewById(R.id.lvContact);
        contactAdapter = new ContactAdapter(appCompatActivity,listContact,R.layout.item_contact);
        lvContact.setAdapter(contactAdapter);
        return root;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity)
            appCompatActivity = (AppCompatActivity) context;
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search,menu);
        SearchManager searchManager = (SearchManager) appCompatActivity.getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.item_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(appCompatActivity.getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                contactAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                contactAdapter.getFilter().filter(query);
                return false;
            }
        });
       super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_search:
                // Not implemented here
                return false;
            default:
                break;
        }
        searchView.setOnQueryTextListener(queryTextListener);
        return super.onOptionsItemSelected(item);
    }

}