package com.mtha.findmyfriends.ui.listfriend;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.SearchView;
import android.widget.Toast;


import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.mtha.findmyfriends.R;
import com.mtha.findmyfriends.data.model.Contact;
import com.mtha.findmyfriends.data.model.ContactAdapter;
import com.mtha.findmyfriends.data.model.ContactDbHelper;
import com.mtha.findmyfriends.data.model.MyDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;


public class ListFriendFragment extends Fragment implements ContactAdapter.ContactAdapterListener {
    private AppCompatActivity appCompatActivity;
    private ListFriendViewModel addFriendViewModel;
    List<Contact> listContact;


    ContactDbHelper contactDbHelper;
    RecyclerView recyclerView ;
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
        recyclerView = root.findViewById(R.id.listViewContact);
        // white background notification bar
     //   whiteNotificationBar(recyclerView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(root.getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(root.getContext(), DividerItemDecoration.VERTICAL, 10));
        contactDbHelper = new ContactDbHelper(root.getContext());
        getContactListView();
        return root;
    }
    private void getContactListView(){
        listContact = contactDbHelper.getAllContacts();
        contactAdapter = new ContactAdapter(appCompatActivity,listContact,this);
        recyclerView.setAdapter(contactAdapter);
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

    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            appCompatActivity.getWindow().setStatusBarColor(Color.WHITE);
        }
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

    @Override
    public void onContactSelected(Contact contact) {
        Toast.makeText(appCompatActivity, "Selected: " + contact.getFullName() + ", " + contact.getPhoneNumb(), Toast.LENGTH_LONG).show();
    }
}