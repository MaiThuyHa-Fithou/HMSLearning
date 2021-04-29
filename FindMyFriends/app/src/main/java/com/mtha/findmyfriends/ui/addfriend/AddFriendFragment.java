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
    private static final int REQUEST_CODE_SCAN_ONE = 0X01;
    public static final String RESULT = "SCAN_RESULT";
    RelativeLayout qr_code_scanner;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        addFriendViewModel =
                new ViewModelProvider(this).get(AddFriendViewModel.class);
        View root = inflater.inflate(R.layout.fragment_addfriend, container, false);
        initView(root);
        //add back button on ActionBar
        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = appCompatActivity.getIntent();
        if(intent!=null){
            HmsScan obj = intent.getParcelableExtra(RESULT);
            Toast.makeText(appCompatActivity, obj.getOriginalValue(),Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(appCompatActivity, "Khong vao day ",Toast.LENGTH_LONG).show();
        }


        return root;
    }

    private void initView(View root) {
        qr_code_scanner = root.findViewById(R.id.relativate_scan);
        qr_code_scanner.setOnClickListener(this);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity)
            appCompatActivity = (AppCompatActivity) context;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.relativate_scan:
                Intent intent = new Intent(getContext(), qrcode_scanner.class);
                startActivityForResult(intent, REQUEST_CODE_SCAN_ONE);
                break;
        }
    }
}