package com.mtha.findmyfriends.ui.addfriend;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.huawei.hms.hmsscankit.ScanUtil;

import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;

import com.mtha.findmyfriends.R;


public class qrcode_scanner extends AppCompatActivity {
    private static final int REQUEST_CODE_SCAN_ONE = 0X01;
    public static final int DEFAULT_VIEW = 0x22;
    public static final String RESULT = "SCAN_RESULT";
    Button btnSave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_scanner);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA, 	Manifest.permission.READ_EXTERNAL_STORAGE},
                    DEFAULT_VIEW);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(permissions==null||grantResults==null||grantResults.length<2|| grantResults[0]!=
        PackageManager.PERMISSION_GRANTED||grantResults[1]!=PackageManager.PERMISSION_GRANTED){
            return;
        }
        if(requestCode==DEFAULT_VIEW){
            HmsScanAnalyzerOptions options = new HmsScanAnalyzerOptions.Creator().
                    setHmsScanTypes(HmsScan.ALL_SCAN_TYPE,HmsScan.CODE128_SCAN_TYPE).create();
            ScanUtil.startScan(qrcode_scanner.this, REQUEST_CODE_SCAN_ONE, options);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            Log.d("SCAN_CODE","obj.originalValue");
            return;
        }

        //Default View
        if (requestCode == REQUEST_CODE_SCAN_ONE) {

            HmsScan obj = data.getParcelableExtra(ScanUtil.RESULT);
            Log.d("SCAN_CODE",obj.originalValue);
            if (obj != null) {
                Toast.makeText(this, "" +obj.getOriginalValue(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, AddFriendFragment.class);
                intent.putExtra(RESULT, obj);
                startActivity(intent);
            }

        }
    }
}