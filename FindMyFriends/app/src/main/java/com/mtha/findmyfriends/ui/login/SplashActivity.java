package com.mtha.findmyfriends.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectAuthCredential;
import com.huawei.agconnect.auth.AGConnectUser;
import com.huawei.agconnect.auth.EmailAuthProvider;
import com.huawei.hmf.tasks.Continuation;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.request.AccountAuthParamsHelper;
import com.huawei.hms.support.account.result.AuthAccount;
import com.huawei.hms.support.account.service.AccountAuthService;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams;
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService;
import com.mtha.findmyfriends.MainActivity;
import com.mtha.findmyfriends.R;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    private HuaweiIdAuthService authService;
    private HuaweiIdAuthParams authParams;
    private TimerTask timerTask;
    private LottieAnimationView lottieAnimationView;
    private static int SPLASH_TIME_OUT = 3000;
    private static final String TAG = "SplashActivity";
    public String userEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        lottieAnimationView = findViewById(R.id.animation_view);
        AGConnectUser user = AGConnectAuth.getInstance().getCurrentUser();

        if(user==null)
            silentlySignIn();
        else
            userEmail = user.getEmail();
        Timer RunSplash = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (userEmail!=null) {
                            lottieAnimationView.setVisibility(View.INVISIBLE);
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(intent);
                            Toast.makeText(SplashActivity.this," Signing you In",
                                    Toast.LENGTH_LONG).show();
                            finish();
                        }else{
                            Toast.makeText(SplashActivity.this, "Sign-In to your account",Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
            }
        };
        RunSplash.schedule(timerTask, SPLASH_TIME_OUT);
    }

    private void silentlySignIn(){
        AccountAuthParams authParams = new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).createParams();
        AccountAuthService service = AccountAuthManager.getService(this, authParams);
        Task<AGConnectAuth> task = service.silentSignIn().continueWithTask(new Continuation<AuthAccount, Task<AGConnectAuth>>() {
            @Override
            public Task<AGConnectAuth> then(Task<AuthAccount> task) throws Exception {
                return null;
            }
        });
        task.addOnSuccessListener(new OnSuccessListener<AGConnectAuth>() {

            @Override
            public void onSuccess(AGConnectAuth agConnectAuth) {
                userEmail = agConnectAuth.getCurrentUser().getEmail();
                Log.e(TAG, "user Email [" + userEmail+"]");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.e("login error: ", e.getMessage());
            }
        });
       /* task.addOnSuccessListener(new OnSuccessListener<AGConnectAuth>() {
            @Override
            public void onSuccess(AuthAccount authAccount) {
                userEmail = authAccount.getEmail();
                Log.e(TAG, "user Email [" + userEmail+"]");
            }
        });*/
    }
}
