package com.mtha.findmyfriends;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.hmf.tasks.OnCompleteListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;

import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;
import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.request.AccountAuthParamsHelper;
import com.huawei.hms.support.account.service.AccountAuthService;
import com.mtha.findmyfriends.data.model.Contact;
import com.mtha.findmyfriends.data.model.ContactDbHelper;
import com.mtha.findmyfriends.ui.login.LoginActivity;

import com.mtha.findmyfriends.ui.login.SplashActivity;
import com.mtha.findmyfriends.utils.Contants;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    AccountAuthParams authParams;
    private static final int REQUEST_CODE_SCAN_ONE = 0X01;
    public static final int DEFAULT_VIEW = 0x22;
    public static final String RESULT = "SCAN_RESULT";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_list_friend, R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        checkPermissions(MainActivity.this);

    }


    //add
    private static void checkPermissions(AppCompatActivity activityCompat) {
        // You must have the ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION permission. Otherwise, the location service
        // is unavailable.
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            Log.i(Contants.TAG, "android sdk < 28 Q");
            if (ActivityCompat.checkSelfPermission(activityCompat,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(activityCompat,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                String[] strings =
                        {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
                ActivityCompat.requestPermissions(activityCompat, strings, 883);
            }
        } else {
            if (ActivityCompat.checkSelfPermission(activityCompat,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(activityCompat,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(activityCompat,
                    "android.permission.ACCESS_BACKGROUND_LOCATION") != PackageManager.PERMISSION_GRANTED) {
                String[] strings = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION, "android.permission.ACCESS_BACKGROUND_LOCATION"};
                ActivityCompat.requestPermissions(activityCompat, strings, 882);
            }
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
                    setHmsScanTypes(HmsScan.ALL_SCAN_TYPE,HmsScan.QRCODE_SCAN_TYPE).create();
            ScanUtil.startScan(MainActivity.this, REQUEST_CODE_SCAN_ONE, options);
        }
        if(requestCode==882 || requestCode==883){
            if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Log.i("not permission", "onRequestPermissionsResult: apply LOCATION PERMISSSION  failed");
            }
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

                //call dialog form
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                View contactDetail = LayoutInflater.from(MainActivity.this).inflate(R.layout.contact_dialog, null);
                builder.setView(contactDetail);
                EditText etName = (EditText) contactDetail.findViewById(R.id.etName);
                EditText etPhone = (EditText) contactDetail.findViewById(R.id.etPhone);
                EditText etEmail = (EditText)contactDetail.findViewById(R.id.etEmail);
                EditText etLatitude = (EditText) contactDetail.findViewById(R.id.etLatitude);
                EditText etLongtitude = (EditText) contactDetail.findViewById(R.id.etLongtitude);
                ImageView imageView = (ImageView) contactDetail.findViewById(R.id.image);
                try {
                    JSONObject jsonObject = new JSONObject( obj.getOriginalValue());
                    String fullname = jsonObject.getString("fullName");
                    String email = jsonObject.getString("email");
                    String phone = jsonObject.getString("phone");
                    String image = jsonObject.getString("image");
                    double latitude = jsonObject.getDouble("latitude");
                    double longtitude = jsonObject.getDouble("longtitude");
                    etName.setText(fullname);
                    etEmail.setText(email);
                    etPhone.setText(phone);
      //              Glide.with(MainActivity.this).load(image).into(imageView);
                    etLatitude.setText(latitude+"");
                    etLongtitude.setText(longtitude+"");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                final AlertDialog dialog = builder.create();


                contactDetail.findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //get data contact and save json object
                        String fullName = etName.getText().toString();
                        String phoneNumb = etPhone.getText().toString();
                        String email = etEmail.getText().toString();
                        double latitude = Double.parseDouble(etLatitude.getText().toString());
                        double longtitude = Double.parseDouble(etLongtitude.getText().toString());
                        String image = imageView.getTag().toString();
                        Contact contact = new Contact(fullName,phoneNumb,email,image,latitude,longtitude);
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                        String uid = reference.push().getKey();
                        reference.child("users").child(uid).setValue(contact);
                        dialog.dismiss();
                    }
                });

                contactDetail.findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                //tao dialog
                dialog.show();

            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_qrcode,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_qr_scan:
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                    ActivityCompat.requestPermissions(
                            MainActivity.this,
                            new String[]{Manifest.permission.CAMERA, 	Manifest.permission.READ_EXTERNAL_STORAGE},
                            DEFAULT_VIEW);
                }
                return true;
            case android.R.id.home:
                MainActivity.this.onBackPressed();
                return true;
            case R.id.item_signOut:
                //todo something
                AGConnectAuth.getInstance().signOut();
                //call login form
                startActivity(new Intent(MainActivity.this, SplashActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPostResume() {

        super.onPostResume();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if(getFragmentManager().getBackStackEntryCount()>0){
           getFragmentManager().popBackStack();

        }else
            super.onBackPressed();
    }
}