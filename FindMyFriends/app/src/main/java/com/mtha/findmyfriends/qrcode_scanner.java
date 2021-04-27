package com.mtha.findmyfriends;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;

public class qrcode_scanner extends AppCompatActivity {
    private static final int REQUEST_CODE_SCAN_ONE = 0X01;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_scanner);
        HmsScanAnalyzerOptions options = new HmsScanAnalyzerOptions.Creator().
                setHmsScanTypes(HmsScan.QRCODE_SCAN_TYPE , HmsScan.DATAMATRIX_SCAN_TYPE).setPhotoMode(true).create();
        ScanUtil.startScan(qrcode_scanner.this, REQUEST_CODE_SCAN_ONE, options);
    }
}