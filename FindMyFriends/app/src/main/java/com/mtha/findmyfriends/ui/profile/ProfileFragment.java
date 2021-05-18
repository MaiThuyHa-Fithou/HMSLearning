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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectUser;
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

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private AppCompatActivity appCompatActivity;
    private ProfileViewModel profileViewModel;
    private Bitmap resultBitmap;

    EditText mFullName, mEmail, mPhone;
    ImageView btnEdit, btnGenQRCode, changAvatar;
    AGConnectUser user;
    String uid;
    FirebaseDatabase database;
    DatabaseReference reference;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
       getViews(root);
        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        user = AGConnectAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        //get email login
       /* Intent intent = appCompatActivity.getIntent();
        uid = intent.getStringExtra("uid");
        email = intent.getStringExtra("email");*/
        getContactInfo(uid);
        return root;
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof Activity)
            appCompatActivity=(AppCompatActivity)context;
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

    private void getContactInfo(String uid){
        Query query =reference.child("users").child(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Contact contact = snapshot.getValue(Contact.class);
                Toast.makeText(appCompatActivity,contact.getFullName(),Toast.LENGTH_LONG).show();
                //set contact info
                mFullName.setText(contact.getFullName());
                mEmail.setText(contact.getEmail());
                mPhone.setText(contact.getPhone());
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnEdit:
                updContactDB( uid);
                break;
            case R.id.btnGenQR:
                myQRCode();
                break;
            case R.id.changeAvatar:
                //open gallery
                break;
        }
    }

    private void updContactDB(String uid){
        String name, phone;
        name = mFullName.getText().toString();
        phone = mPhone.getText().toString();
        reference.child("users").child(uid).child("fullName").setValue(name);
        reference.child("users").child(uid).child("phone").setValue(phone);
    }

    private void myQRCode(){
        Query query =reference.child("users").child(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Contact contact = snapshot.getValue(Contact.class);
                Toast.makeText(appCompatActivity,contact.getFullName(),Toast.LENGTH_LONG).show();
                JSONObject userObj = new JSONObject();
                try {
                    userObj.put("fullname", contact.getFullName());
                    userObj.put("email", contact.getEmail());
                    userObj.put("phone", contact.getPhone());
                    userObj.put("image", contact.getImage());
                    userObj.put("latitude", contact.getLatitude());
                    userObj.put("longtitude", contact.getLongitude());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    generateQRCode(userObj);
                } catch (WriterException | IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
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

        }
    }

}