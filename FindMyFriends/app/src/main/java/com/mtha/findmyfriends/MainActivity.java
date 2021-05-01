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
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.bottomnavigation.BottomNavigationView;
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
                R.id.navigation_friend, R.id.navigation_add_friend, R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
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
            ScanUtil.startScan(MainActivity.this, REQUEST_CODE_SCAN_ONE, options);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
               View contactDetail = LayoutInflater.from(this).inflate(R.layout.contact_dialog, null);
               builder.setView(contactDetail);
                EditText etName = (EditText) contactDetail.findViewById(R.id.etName);
                EditText etPhone = (EditText) contactDetail.findViewById(R.id.etPhone);
                EditText etEmail = (EditText)contactDetail.findViewById(R.id.etEmail);
                EditText etAddress = (EditText) contactDetail.findViewById(R.id.etAddress);


                etName.setText(obj.getContactDetail().getPeopleName().getFullName());
                etPhone.setText(obj.getContactDetail().getTelPhoneNumbers().get(0).getTelPhoneNumber());
                etEmail.setText(obj.getContactDetail().getEmailContents().get(0).getAddressInfo());

                StringBuilder addressBuilder = new StringBuilder();
                for (String addressLine : obj.getContactDetail().getAddressesInfos().get(0).getAddressDetails()) {
                    addressBuilder.append(addressLine);
                }
                etAddress.setText(addressBuilder.toString());

                final AlertDialog dialog = builder.create();


                contactDetail.findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //get data contact and save json object
                        String fullName = etName.getText().toString();
                        String phoneNumb = etPhone.getText().toString();
                        String email = etEmail.getText().toString();
                        String address = etAddress.getText().toString();
                        Contact contact = new Contact(fullName,phoneNumb,email,address);
                        new ContactDbHelper(MainActivity.this).insContact(contact);
                        dialog.dismiss();
                    }
                });

                //tao dialog
                dialog.show();

            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_qr_scan:
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                    ActivityCompat.requestPermissions(
                            this,
                            new String[]{Manifest.permission.CAMERA, 	Manifest.permission.READ_EXTERNAL_STORAGE},
                            DEFAULT_VIEW);
                }
                break;
            case R.id.item_setting:
                break;
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.item_signOut:
                authParams = new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).
                        setProfile().setAuthorizationCode().createParams();
                AccountAuthService authService = AccountAuthManager.getService(MainActivity.this,authParams);
                authService.cancelAuthorization().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Processing after a successful authorization revoking.
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            Log.i(Contants.TAG, "onSuccess: ");
                        } else {
                            // Handle the exception.
                            Exception exception = task.getException();
                            if (exception instanceof ApiException){
                                int statusCode = ((ApiException) exception).getStatusCode();
                                Log.i(Contants.TAG, "onFailure: " + statusCode);
                            }
                        }
                    }
                });
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }




    @Override
    public void onBackPressed() {
        if(getFragmentManager().getBackStackEntryCount()>0){
           getFragmentManager().popBackStack();

        }else
            super.onBackPressed();
    }
}