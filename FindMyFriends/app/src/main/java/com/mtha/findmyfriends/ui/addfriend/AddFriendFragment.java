package com.mtha.findmyfriends.ui.addfriend;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;


import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.mtha.findmyfriends.R;


public class AddFriendFragment extends Fragment implements View.OnClickListener {
    private AppCompatActivity appCompatActivity;
    private AddFriendViewModel addFriendViewModel;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        addFriendViewModel =
                new ViewModelProvider(this).get(AddFriendViewModel.class);
        View root = inflater.inflate(R.layout.fragment_addfriend, container, false);
        
        //add back button on ActionBar
        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        return root;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity)
            appCompatActivity = (AppCompatActivity) context;
    }


    @Override
    public void onClick(View view) {

    }
}