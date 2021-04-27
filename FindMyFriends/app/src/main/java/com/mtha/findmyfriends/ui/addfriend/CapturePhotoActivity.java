package com.mtha.findmyfriends.ui.addfriend;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;


import com.huawei.hms.mlsdk.common.internal.client.SmartLog;
import com.mtha.findmyfriends.R;
import com.mtha.findmyfriends.utils.CameraConfiguration;
import com.mtha.findmyfriends.utils.Contants;
import com.mtha.findmyfriends.utils.LensEngine;
import com.mtha.findmyfriends.utils.LensEnginePreview;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CapturePhotoActivity extends AppCompatActivity {
    private static final String TAG = "CapturePhotoActivity";
    private LensEngine lensEngine = null;
    private LensEnginePreview preview;
    private CameraConfiguration cameraConfiguration = null;
    private int facing = CameraConfiguration.CAMERA_FACING_BACK;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_photo);
        ImageButton takePhotoButton = findViewById(R.id.img_takePhoto);
        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CapturePhotoActivity.this.toTakePhoto();
            }
        });
        ImageButton backButton = findViewById(R.id.capture_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CapturePhotoActivity.this.finish();
            }
        });
        this.preview = this.findViewById(R.id.capture_preview);
        this.cameraConfiguration = new CameraConfiguration();
        this.cameraConfiguration.setCameraFacing(this.facing);
        this.createLensEngine();
        this.startLensEngine();
    }

    private void createLensEngine() {
        if (this.lensEngine == null) {
            this.lensEngine = new LensEngine(this, this.cameraConfiguration);
        }
    }

    private void startLensEngine() {
        if (this.lensEngine != null) {
            try {
                this.preview.start(this.lensEngine, false);
            } catch (IOException e) {
                Log.e(CapturePhotoActivity.TAG, "Unable to start lensEngine.", e);
                this.lensEngine.release();
                this.lensEngine = null;
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        this.startLensEngine();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.preview.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.lensEngine != null) {
            this.lensEngine.release();
        }
        this.facing = CameraConfiguration.CAMERA_FACING_BACK;
        this.cameraConfiguration.setCameraFacing(this.facing);
    }

    private void toTakePhoto() {
        lensEngine.takePicture(new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                String filePath = saveBitmapToDisk(bitmap);
                Intent intent = new Intent();
                intent.putExtra(Contants.IMAGE_PATH_VALUE, filePath);
                setResult(Activity.RESULT_OK, intent);
                CapturePhotoActivity.this.finish();
            }
        });
    }


    private String saveBitmapToDisk(Bitmap bitmap) {
        String storePath = getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File.separator + "PhotoTranslate";
        File appDir = new File(storePath);
        if (!appDir.exists()) {
            boolean res = appDir.mkdir();
            if (!res) {
                SmartLog.e(TAG, "saveBitmapToDisk failed");
                return "";
            }
        }

        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();

            Uri uri = Uri.fromFile(file);
            this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file.getAbsolutePath();
    }
}
