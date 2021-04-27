package com.mtha.findmyfriends.ui.login;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.request.AccountAuthParamsHelper;
import com.huawei.hms.support.account.result.AuthAccount;
import com.huawei.hms.support.account.service.AccountAuthService;
import com.mtha.findmyfriends.MainActivity;
import com.mtha.findmyfriends.R;
import com.mtha.findmyfriends.utils.Contants;

import java.io.Serializable;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    AccountAuthParams authParams;
    AccountAuthService authService;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getViews();

    }

    private void signInID(){
        authParams = new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
                .setAuthorizationCode()
                .createParams();
        authService = AccountAuthManager.getService(LoginActivity.this,authParams);
        startActivityForResult(authService.getSignInIntent(), Contants.SIGN_IN_ID);


    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login:
                signInID();
                break;
            case R.id.register:
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Contants.SIGN_IN_ID){
            Task<AuthAccount> authAccountTask = AccountAuthManager.parseAuthResultFromIntent(data);
            //check login success or not
            if(authAccountTask.isSuccessful()){
                //call MainActivity class
                AuthAccount authAccount = authAccountTask.getResult();
                checkPermissions(LoginActivity.this);
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                Log.e(Contants.TAG, "serverAuthCode:" + authAccount.getAuthorizationCode());
                Log.e(Contants.TAG, "serverAuthCode:" + authAccount.getDisplayName());
                this.finish();
            }else {
                Log.e(Contants.TAG, "sign in failed : " +((ApiException) authAccountTask.getException())
                        .getStatusCode());
            }
        }
    }

    private void getViews(){

        findViewById(R.id.register).setOnClickListener(this);
        findViewById(R.id.login).setOnClickListener(this);
    }

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
                ActivityCompat.requestPermissions(activityCompat, strings, 1);
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
                ActivityCompat.requestPermissions(activityCompat, strings, 2);
            }
        }
    }
}