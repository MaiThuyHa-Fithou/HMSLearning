package com.mtha.findmyfriends.ui.profile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.hmsscankit.WriterException;
import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.LocationAvailability;
import com.huawei.hms.location.LocationServices;
import com.huawei.hms.maps.model.LatLng;
import com.huawei.hms.ml.scan.HmsBuildBitmapOption;
import com.huawei.hms.ml.scan.HmsScan;
import com.mtha.findmyfriends.R;
import com.mtha.findmyfriends.data.model.Contact;
import com.mtha.findmyfriends.utils.CheckLocationSetting;
import com.mtha.findmyfriends.utils.ImageSaver;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private AppCompatActivity appCompatActivity;
    private ProfileViewModel profileViewModel;
    private Bitmap resultBitmap;
    CheckLocationSetting setting ;
    EditText mFullName, mEmail, mPhone;
    ImageView btnEdit, btnGenQRCode, changAvatar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
       getViews(root);
        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setting = new CheckLocationSetting(appCompatActivity,appCompatActivity.getApplicationContext());
        setting.requestLocation();
        return root;
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof Activity)
            appCompatActivity=(AppCompatActivity)context;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    private void getViews(View root){
        mFullName = root.findViewById(R.id.fullName);
        mEmail = root.findViewById(R.id.email);
        mPhone = root.findViewById(R.id.phone);
        btnEdit = root.findViewById(R.id.btnEdit);
        btnGenQRCode = root.findViewById(R.id.btnGenQR);
        changAvatar = root.findViewById(R.id.changeAvatar);
        btnEdit.setOnClickListener(this);
        btnGenQRCode.setOnClickListener(this);
        changAvatar.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnEdit:
                break;
            case R.id.btnGenQR:
                myQRCode();
                break;
            case R.id.changeAvatar:
                break;
        }
    }

    private void myQRCode(){
        String fullname = mFullName.getText().toString();
        String phone = mPhone.getText().toString();
        String email = mEmail.getText().toString();
        final Contact[] contact = {null};
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices
                .getFusedLocationProviderClient(appCompatActivity);
        Task<Location> task = fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location == null) {
                            return;
                        }
                        contact[0] = new Contact(fullname,phone,email,location.getLatitude(),location.getLongitude());
                        Toast.makeText(appCompatActivity, contact[0].toString()
                                , Toast.LENGTH_SHORT).show();
                        JSONObject userObj = new JSONObject();
                        try {
                            userObj.put("fullname", fullname);
                            userObj.put("email", email);
                            userObj.put("phone", phone);
                            userObj.put("latitude", location.getLatitude());
                            userObj.put("longtitude", location.getLongitude());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            generateQRCode(userObj);
                        } catch (WriterException | IOException e) {
                            e.printStackTrace();
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        //Exception handling logic.
                       e.printStackTrace();
                    }
                });
    }

    @Override
    public void onResume() {
        setting.requestLocationUpdatesWithCallback();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        setting.removeLocationUpdatesWithCallback();
        super.onDestroy();
    }

    private void generateQRCode(JSONObject userObj) throws WriterException, IOException {

        String content = userObj.toString();
        int type = HmsScan.QRCODE_SCAN_TYPE;
        int width =500;
        int height = 500;

        HmsBuildBitmapOption options = new HmsBuildBitmapOption.Creator()
                .setBitmapBackgroundColor(Color.WHITE)
                .setBitmapColor(Color.BLACK)
                .setBitmapMargin(3).create();
        resultBitmap = ScanUtil.buildBitmap(content, type, width, height, options);
        if(resultBitmap!=null) {
            //  changAvatar.setImageBitmap(resultBitmap);
            //call dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(appCompatActivity);
            View contactDetail = LayoutInflater.from(appCompatActivity).inflate(R.layout.my_qrcode, null);
            builder.setView(contactDetail);
            ImageView imgQR = contactDetail.findViewById(R.id.imgMyQRCode);
            imgQR.setImageBitmap(resultBitmap);
            final AlertDialog dialog = builder.create();
            contactDetail.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            dialog.show();
            new ImageSaver(appCompatActivity).
                    setFileName("myqrcode.png").
                    setDirectoryName("FindMyFriend").save(resultBitmap);
           // saveQRCodeFile();
        }
    }
    private void saveQRCodeFile() throws IOException {
        String fileName = System.currentTimeMillis() + ".jpg";
        String storePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File appDir = new File(storePath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        File file = new File(appDir, fileName);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        boolean isSuccess = resultBitmap.compress(Bitmap.CompressFormat.JPEG, 70, fileOutputStream);
        fileOutputStream.flush();
        fileOutputStream.close();
        Uri uri = Uri.fromFile(file);
        appCompatActivity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
        if (isSuccess) {
            Toast.makeText(appCompatActivity, "Barcode has been saved locally", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(appCompatActivity, "Barcode save failed", Toast.LENGTH_SHORT).show();
        }
    }
}