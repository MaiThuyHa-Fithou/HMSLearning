package com.mtha.findmyfriends.ui.register;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectUser;
import com.huawei.agconnect.auth.EmailUser;
import com.huawei.agconnect.auth.SignInResult;
import com.huawei.agconnect.auth.VerifyCodeResult;
import com.huawei.agconnect.auth.VerifyCodeSettings;
import com.huawei.hmf.tasks.OnCompleteListener;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hmf.tasks.TaskExecutors;
import com.mtha.findmyfriends.MainActivity;
import com.mtha.findmyfriends.R;
import com.mtha.findmyfriends.data.model.Contact;

import java.util.Locale;

import static com.huawei.agconnect.auth.VerifyCodeSettings.ACTION_REGISTER_LOGIN;

public class UserRegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private AGConnectAuth huaweiAuth;
    Button btnRegister, btnClose, btnVerify;
    EditText editFullName, editPhone, editVerifyCode, editPassword, editEmail, editConfirmPwd;
    private String email;
    private String name;
    private String password;
    private String confirmPassword;
    private String verCode;
    private String phone;
    AGConnectUser user;
    String userID;
    private String uid="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);
        getViews();
        huaweiAuth = AGConnectAuth.getInstance();
    }

    private void getViews() {
        editFullName = findViewById(R.id.editName);
        editPassword = findViewById(R.id.editPass);
        editPhone = findViewById(R.id.editPhone);
        editVerifyCode = findViewById(R.id.editVerify);
        editEmail = findViewById(R.id.editEmail);
        editConfirmPwd = findViewById(R.id.editConfirmPass);
        btnRegister = findViewById(R.id.buttonAcount);
        btnClose = findViewById(R.id.buttonClose);
        btnVerify = findViewById(R.id.btnVerify);
        btnVerify.setOnClickListener(this);
        btnClose.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonAcount:
                //register account
                signUpWithEmail();
                break;
            case R.id.btnVerify:
                sendCodeVerification();
                //send verify code
                break;
            case R.id.buttonClose:
                //close form
                finish();
                break;
        }
    }

    private void sendCodeVerification() {
        email = editEmail.getText().toString();
        if (email.isEmpty() || email == null) {
            Toast.makeText(this, "Email is empty or null ", Toast.LENGTH_LONG).show();
        } else {
            VerifyCodeSettings settings = VerifyCodeSettings.newBuilder()
                    .action(ACTION_REGISTER_LOGIN)
                    .sendInterval(30)
                    .locale(Locale.CHINA)
                    .build();
            Task<VerifyCodeResult> task = huaweiAuth.requestVerifyCode(email, settings);
            task.addOnSuccessListener(TaskExecutors.uiThread(), new OnSuccessListener<VerifyCodeResult>() {
                @Override
                public void onSuccess(VerifyCodeResult verifyCodeResult) {
                    Toast.makeText(UserRegisterActivity.this, "Check email receipt verify code", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(TaskExecutors.uiThread(), new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    Log.d("VerifyCodeErr", e.getMessage());
                }
            });


        }
    }

    private void signUpWithEmail() {
        email = editEmail.getText().toString();
        name = editFullName.getText().toString();
        phone = editPhone.getText().toString();
        password = editPassword.getText().toString();
        verCode = editVerifyCode.getText().toString();
        confirmPassword = editConfirmPwd.getText().toString();

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || name.isEmpty() || verCode.isEmpty()) {
            Toast.makeText(
                    this,
                    "All fields are mandatory!",
                    Toast.LENGTH_LONG
            ).show();
        } else {
            EmailUser emailUser = new EmailUser.Builder().setEmail(email)
                    .setVerifyCode(verCode)
                    .setPassword(password).build();
            AGConnectAuth.getInstance().createUser(emailUser)
                    .addOnCompleteListener(new OnCompleteListener<SignInResult>() {
                        @Override
                        public void onComplete(Task<SignInResult> task) {
                            if (task.isSuccessful()) {
                                user = AGConnectAuth.getInstance().getCurrentUser();
                                userID = user.getUid();
                                //insert in realtime db firebase
                                Contact contact = new Contact(name,phone,email,"avatar.jpg",0,0);
                                insContactDB(contact, userID);
                                //call back login form
                                Intent intent = new Intent();
                                intent.putExtra("uid", userID);
                                intent.putExtra("email", email);
                                intent.putExtra("password", password);
                                setResult(RESULT_OK, intent);
                                finish();//close signup form

                            } else {
                                Log.d("SignUpErr", task.getException().getMessage());
                                Toast.makeText(
                                        UserRegisterActivity.this,
                                        task.getException().getMessage(),
                                        Toast.LENGTH_LONG
                                )
                                        .show();
                            }
                        }
                    });
        }
    }

    private void insContactDB(Contact contact, String userID){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference();
       // uid = reference.push().getKey();
        reference.child("users").child(userID).setValue(contact);

    }
}