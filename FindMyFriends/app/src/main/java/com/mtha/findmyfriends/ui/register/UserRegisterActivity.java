package com.mtha.findmyfriends.ui.register;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.mtha.findmyfriends.MainActivity;
import com.mtha.findmyfriends.R;

public class UserRegisterActivity extends AppCompatActivity {

    Button btnRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);
        btnRegister = findViewById(R.id.buttonAcount);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserRegisterActivity.this, MainActivity.class));
           //     UserRegisterActivity.this.finish();
            }
        });

    }
}