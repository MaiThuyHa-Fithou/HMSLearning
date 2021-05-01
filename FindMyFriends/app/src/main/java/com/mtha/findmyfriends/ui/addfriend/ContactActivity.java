package com.mtha.findmyfriends.ui.addfriend;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.huawei.hms.ml.scan.HmsScan;
import com.mtha.findmyfriends.MainActivity;
import com.mtha.findmyfriends.R;

public class ContactActivity extends AppCompatDialogFragment {
    public ContactActivity() {
        super();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }
}