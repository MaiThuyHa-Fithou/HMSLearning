package com.mtha.findmyfriends.ui.addfriend;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.huawei.hms.hmsscankit.ScanUtil;

import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;
import com.huawei.hms.mlsdk.common.internal.client.SmartLog;
import com.mtha.findmyfriends.R;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;
import static com.huawei.hms.hmsscankit.ScanUtil.RESULT;

public class AddFriendFragment extends Fragment implements View.OnClickListener {
    private AppCompatActivity appCompatActivity;
    private AddFriendViewModel addFriendViewModel;
    public static final int CAMERA_REQ_CODE = 111;
    private static final int REQUEST_CODE_SCAN_ONE = 0X01;
    RelativeLayout qr_code_scanner;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        addFriendViewModel =
                new ViewModelProvider(this).get(AddFriendViewModel.class);
        View root = inflater.inflate(R.layout.fragment_addfriend, container, false);
        initView(root);
        //add back button on ActionBar
        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        return root;
    }


    private void initView(View root){
        qr_code_scanner = root.findViewById(R.id.relativate_scan);
        qr_code_scanner.setOnClickListener(this);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof Activity)
            appCompatActivity=(AppCompatActivity)context;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Toast.makeText(appCompatActivity, "addffgf", Toast.LENGTH_SHORT).show();
        if (permissions == null || grantResults == null) {
            return;
        }
        if (grantResults.length < 2 || grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (requestCode == CAMERA_REQ_CODE && grantResults.length == 2
                && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            HmsScanAnalyzerOptions options = new HmsScanAnalyzerOptions.Creator().
                    setHmsScanTypes(HmsScan.QRCODE_SCAN_TYPE , HmsScan.DATAMATRIX_SCAN_TYPE).setPhotoMode(true).create();
            ScanUtil.startScan(appCompatActivity, REQUEST_CODE_SCAN_ONE, options);
        }

    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }

        //Default View
        if (requestCode == REQUEST_CODE_SCAN_ONE) {
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(appCompatActivity.getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.relativate_scan:
                ActivityCompat.requestPermissions(
                        appCompatActivity,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                        CAMERA_REQ_CODE);
                Intent intent = new Intent(AddFriendFragment.this,qr_code_scanner.class);
                startActivityForResult(intent,REQUEST_CODE_SCAN_ONE);
                break;
        }
    }
}