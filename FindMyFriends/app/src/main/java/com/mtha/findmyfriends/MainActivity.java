package com.mtha.findmyfriends;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.huawei.hmf.tasks.OnCanceledListener;
import com.huawei.hmf.tasks.OnCompleteListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;

import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.request.AccountAuthParamsHelper;
import com.huawei.hms.support.account.service.AccountAuthService;
import com.mtha.findmyfriends.ui.addfriend.AddFriendFragment;
import com.mtha.findmyfriends.ui.friends.FriendsFragment;
import com.mtha.findmyfriends.ui.login.LoginActivity;
import com.mtha.findmyfriends.ui.profile.ProfileFragment;
import com.mtha.findmyfriends.utils.Contants;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    AccountAuthParams authParams;



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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_qr_scan:
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