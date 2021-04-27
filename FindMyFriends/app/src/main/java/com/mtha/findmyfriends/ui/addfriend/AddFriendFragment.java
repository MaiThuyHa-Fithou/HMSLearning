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
import android.os.Bundle;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.common.utils.SmartLog;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;
import com.mtha.findmyfriends.R;
import com.mtha.findmyfriends.data.model.LoggedInUser;
import com.mtha.findmyfriends.utils.Contants;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddFriendFragment extends Fragment {
    private AppCompatActivity appCompatActivity;
    private AddFriendViewModel addFriendViewModel;
    private String path=null;
    private String barCode=null;
    private LoggedInUser loggedInUser;//information user code
    private RelativeLayout relativeLayoutScan, relativeLayoutTakePhoto;
    private ImageView imageView;

    private Map<String, String> barcodeToProduct = new HashMap<>();

    private int REQUEST_QUERY_PRODUCT = 1001;
    private int REQUEST_ADD_PRODUCT = 1002;
    private static final int PERMISSION_REQUESTS = 1;

    private Bitmap originBitmap;

    Dialog dia;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        addFriendViewModel =
                new ViewModelProvider(this).get(AddFriendViewModel.class);
        View root = inflater.inflate(R.layout.fragment_addfriend, container, false);
        initView(root);
        initAction();
        //add back button on ActionBar
        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // CAMERA_REQ_CODE is user-defined and is used to receive the permission verification result.
        ActivityCompat.requestPermissions(appCompatActivity,new String[]{Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE}, Contants.CAMERA_REQ_CODE);
        if (!allPermissionsGranted()) {
            getRuntimePermissions();
        }
        return root;
    }

    private void initView(View view) {
        this.relativeLayoutScan = view.findViewById(R.id.relativate_scan);
        this.relativeLayoutTakePhoto = view.findViewById(R.id.relativate_camera);

        this.imageView = view.findViewById(R.id.previewPane);
    }
    private void getRuntimePermissions() {
        List<String> allNeededPermissions = new ArrayList<>();
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(appCompatActivity, permission)) {
                allNeededPermissions.add(permission);
            }
        }
        if (!allNeededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                    appCompatActivity, allNeededPermissions.toArray(new String[0]), PERMISSION_REQUESTS);
        }
    }
    private boolean allPermissionsGranted() {
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(appCompatActivity, permission)) {
                return false;
            }
        }
        return true;
    }
    private static boolean isPermissionGranted(Context context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED) {
            SmartLog.i("TAG", "Permission granted: " + permission);
            return true;
        }
        SmartLog.i("TAG", "Permission NOT granted: " + permission);
        return false;
    }

    private String[] getRequiredPermissions() {
        try {
            PackageInfo info =
                    appCompatActivity.getPackageManager()
                            .getPackageInfo(appCompatActivity.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] ps = info.requestedPermissions;
            if (ps != null && ps.length > 0) {
                return ps;
            } else {
                return new String[0];
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            return new String[0];
        }
    }
    private void initAction() {
        this.relativeLayoutScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanBarcode(Contants.REQUEST_CODE_SCAN_ALL);
            }
        });



        this.relativeLayoutTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto(Contants.REQUEST_TAKE_PHOTO);
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof Activity)
            appCompatActivity=(AppCompatActivity)context;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void takePhoto(int requestCode) {
        Intent intent = new Intent(appCompatActivity, CapturePhotoActivity.class);
        this.startActivityForResult(intent, requestCode);
    }

    private void scanBarcode(int requestCode) {
        HmsScanAnalyzerOptions options = new HmsScanAnalyzerOptions.Creator().
                setHmsScanTypes(HmsScan.QRCODE_SCAN_TYPE).create();
        ScanUtil.startScan(appCompatActivity, requestCode, options);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) {
            return;
        }
        if ((requestCode == Contants.REQUEST_CODE_SCAN_ALL)
                && (resultCode == Activity.RESULT_OK)) {

            HmsScan obj = data.getParcelableExtra(ScanUtil.RESULT);
            String path = "";
            if (obj != null && obj.getOriginalValue() != null) {
                path = barcodeToProduct.get(obj.getOriginalValue());
            }
            if (path != null && !path.equals("")) {
                loadCameraImage(path);
                showPictures();
            }

        } else if ((requestCode == Contants.REQUEST_TAKE_PHOTO)
                && (resultCode == Activity.RESULT_OK)) {
           this.path = data.getStringExtra(Contants.IMAGE_PATH_VALUE);
            Log.d("aaa","{"+path+"}");
            this.loadCameraImage(path);
        }

    }
    private void showPictures() {
        dia = new Dialog(appCompatActivity, R.style.edit_AlertDialog_style);
        dia.setContentView(R.layout.activity_start_dialog);
        ImageView imageView = (ImageView) dia.findViewById(R.id.start_img);
        imageView.setImageBitmap(originBitmap);
        dia.setCanceledOnTouchOutside(true); // Sets whether this dialog is
        Window w = dia.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        lp.y = 40;
        dia.onWindowAttributesChanged(lp);
        imageView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dia.dismiss();
                    }
                });
        dia.show();
    }
    private void loadCameraImage(String path) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(path);
            this.originBitmap = BitmapFactory.decodeStream(fis);
            this.originBitmap = this.originBitmap.copy(Bitmap.Config.ARGB_4444, true);
        } catch (IOException error) {
            error.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException error) {
                    error.printStackTrace();
                }
            }
        }
    }

    private void loadCameraImage() {
        if (this.path == null) {
            return;
        }
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(path);
            Bitmap originBitmap = BitmapFactory.decodeStream(fis);
            originBitmap = originBitmap.copy(Bitmap.Config.ARGB_4444, true);
            Log.d("bbb","{"+path+"}");
            Log.d("bitmap", originBitmap.toString());
            imageView.setImageBitmap(originBitmap);
        } catch (IOException error) {
            error.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException error) {
                    error.printStackTrace();
                }
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Check whether requestCode is set to the value of CAMERA_REQ_CODE during permission application, and then check whether the permission is enabled.
        if (requestCode == Contants.CAMERA_REQ_CODE && grantResults.length == 2 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] ==
                PackageManager.PERMISSION_GRANTED) {
            // Call the barcode scanning API to build the scanning capability.

        }
    }
}