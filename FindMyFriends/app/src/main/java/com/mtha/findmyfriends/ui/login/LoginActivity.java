package com.mtha.findmyfriends.ui.login;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.request.AccountAuthParamsHelper;
import com.huawei.hms.support.account.result.AuthAccount;
import com.huawei.hms.support.account.service.AccountAuthService;
import com.mtha.findmyfriends.MainActivity;
import com.mtha.findmyfriends.R;
import com.mtha.findmyfriends.ui.register.UserRegisterActivity;
import com.mtha.findmyfriends.utils.Contants;

import java.io.Serializable;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    AccountAuthParams authParams;
    AccountAuthService authService;
    TextView txtUserRegister;
    Button btnLogin;
    final static int CREATE_USER=101;
    private static int SPLASH_TIME_OUT = 3000;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getViews();


        ////////////////////////////////////////////////////////////////////////////
        // We use the Yoyo to make our app logo to bounce app and down.
        //There is a lot of Attension Techniques styles
        // example Flash, Pulse, RubberBand, Shake, Swing, Wobble, Bounce, Tada, StandUp, Wave.
        // Your can change the techniques to fit your liking.

        YoYo.with(Techniques.Bounce)
                .duration(7000) // Time it for logo takes to bounce up and down
                .playOn(findViewById(R.id.logo));
        /////////////////////////////////////////////////////////////////////////////



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
            case R.id.createnewac:
                Intent intent = new Intent(LoginActivity.this, UserRegisterActivity.class);
                startActivityForResult(intent,CREATE_USER);
                this.finish();
                break;
            case R.id.btnLogin:
                new Handler().postDelayed(new Runnable() {

                    /*
                     * Showing splash screen with a timer. This will be useful when you
                     * want to show case your app logo / company
                     */

                    @Override
                    public void run() {
                        // This method will be executed once the timer is over
                        // Start your app main activity
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        finish();
                    }
                }, SPLASH_TIME_OUT);
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

                Intent intent = new Intent();
                intent.putExtra("isCheck",authAccount.isExpired());
                setResult(RESULT_OK,intent);
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
            txtUserRegister = findViewById(R.id.createnewac);
            txtUserRegister.setOnClickListener(this);
            btnLogin = findViewById(R.id.btnLogin);
            btnLogin.setOnClickListener(this);
    }


}