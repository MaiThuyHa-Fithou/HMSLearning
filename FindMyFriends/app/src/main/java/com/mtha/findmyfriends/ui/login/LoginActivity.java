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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectAuthCredential;
import com.huawei.agconnect.auth.AGConnectUser;
import com.huawei.agconnect.auth.EmailAuthProvider;
import com.huawei.agconnect.auth.SignInResult;
import com.huawei.hmf.tasks.Continuation;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
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

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Iterator;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    AGConnectUser user;
    String userID;
    TextView txtUserRegister;
    EditText txtEmail, txtPassword;
    Button btnLogin;
    String email="", password="", uid ="";
    final static int CREATE_USER=101;
    private static int SPLASH_TIME_OUT = 3000;
    FirebaseDatabase database;
    DatabaseReference reference;
    //silenty signIn
    AccountAuthParams authParams;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getViews();
        user = AGConnectAuth.getInstance().getCurrentUser();

        ////////////////////////////////////////////////////////////////////////////
        // We use the Yoyo to make our app logo to bounce app and down.
        //There is a lot of Attension Techniques styles
        // example Flash, Pulse, RubberBand, Shake, Swing, Wobble, Bounce, Tada, StandUp, Wave.
        // Your can change the techniques to fit your liking.

        YoYo.with(Techniques.Bounce)
                .duration(7000) // Time it for logo takes to bounce up and down
                .playOn(findViewById(R.id.logo));
        /////////////////////////////////////////////////////////////////////////////
        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("users");
        //silent signIn

        authParams = new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).createParams();

    }

    private void silentySignIn(){
        AccountAuthService service = AccountAuthManager.getService(LoginActivity.this, authParams);
        Task<AuthAccount> task = service.silentSignIn();
        task.addOnSuccessListener(new OnSuccessListener<AuthAccount>() {
            @Override
            public void onSuccess(AuthAccount authAccount) {

            }
        });
       Task <AGConnectAuth> task1 = service.silentSignIn().continueWith(new Continuation<AuthAccount, AGConnectAuth>() {
           @Override
           public AGConnectAuth then(Task<AuthAccount> task) throws Exception {
               return null;
           }
       });

    }


    private void signInID(String email, String password){

        AGConnectAuthCredential credential = EmailAuthProvider.credentialWithPassword(email, password);
        AGConnectAuth.getInstance().signIn(credential)
                .addOnSuccessListener(new OnSuccessListener<SignInResult>() {
                    @Override
                    public void onSuccess(SignInResult signInResult) {
                        // Obtain sign-in information.

                        user =AGConnectAuth.getInstance().getCurrentUser();
                        userID = user.getUid();
                        callMainActivity(userID);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e("login error: ", e.getMessage());
                    }
                });


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
                if(email==""|| email==null){
                   email = txtEmail.getText().toString();
                   password = txtPassword.getText().toString();
                }

                if(user!=null){
                    callMainActivity(user.getUid());
                }else {
                    signInID(email, password);
                }

                break;
        }

    }

    private void callMainActivity(String userID){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent1 = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent1);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CREATE_USER && resultCode==RESULT_OK){
            uid = data.getStringExtra("uid");
            email = data.getStringExtra("email");
            password = data.getStringExtra("password");
        }
    }

    private void getViews(){
            txtEmail = findViewById(R.id.loginEmail);
            txtPassword = findViewById(R.id.loginPassword);
            txtUserRegister = findViewById(R.id.createnewac);
            txtUserRegister.setOnClickListener(this);
            btnLogin = findViewById(R.id.btnLogin);
            btnLogin.setOnClickListener(this);
    }


}