package com.mtha.findmyfriends.ui.home;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.huawei.hmf.tasks.OnCompleteListener;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.LocationCallback;
import com.huawei.hms.location.LocationRequest;
import com.huawei.hms.location.LocationServices;
import com.huawei.hms.maps.CameraUpdate;
import com.huawei.hms.maps.CameraUpdateFactory;
import com.huawei.hms.maps.HuaweiMap;
import com.huawei.hms.maps.LocationSource;
import com.huawei.hms.maps.MapView;
import com.huawei.hms.maps.MapsInitializer;
import com.huawei.hms.maps.OnMapReadyCallback;
import com.huawei.hms.maps.model.BitmapDescriptorFactory;
import com.huawei.hms.maps.model.LatLng;
import com.huawei.hms.maps.model.MarkerOptions;
import com.huawei.hms.maps.model.PolylineOptions;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;
import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.request.AccountAuthParamsHelper;
import com.huawei.hms.support.account.service.AccountAuthService;
import com.mtha.findmyfriends.MainActivity;
import com.mtha.findmyfriends.R;
import com.mtha.findmyfriends.data.model.Contact;
import com.mtha.findmyfriends.data.model.ContactDbHelper;
import com.mtha.findmyfriends.ui.login.LoginActivity;
import com.mtha.findmyfriends.utils.CheckLocationSetting;
import com.mtha.findmyfriends.utils.Contants;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private AppCompatActivity appCompatActivity;
    private MapView mapView;
    private HuaweiMap mHuaweiMap;
    private LocationSource.OnLocationChangedListener locationChangedListener;
    private HomeViewModel friendsViewModel;
    private final static String MAPVIEW_BUNDLE_KEY = "mapview";
    private FusedLocationProviderClient fusedLocationProviderClient;

    CheckLocationSetting checkLocationSetting;

    FirebaseDatabase database;
    DatabaseReference reference;

    //custom marker
    private final static String ImageUrl = "https://png.pngtree.com/png-clipart/20190924/original/pngtree-businessman-user-avatar-free-vector-png-image_4827807.jpg";
    private View mCustomMarkerView;
    private ImageView mMarkerImageView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        friendsViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        MapsInitializer.setApiKey("CgB6e3x9dyJbX/s6uu5v+gaHs+baK3W8R172cby8T5mjldTWvJNSzhG4xJSTeehzt/bmcDAwYTgRhGU0RpCSvcIR");

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(appCompatActivity);
        checkLocationSetting = new CheckLocationSetting(appCompatActivity,appCompatActivity.getApplicationContext());
        checkLocationSetting.requestLocation();
        initViews(root, savedInstanceState);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
       /* reference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                for (DataSnapshot data: snapshot.getChildren()) {
                    Contact contact = data.getValue(Contact.class);
                    mHuaweiMap.addMarker(new MarkerOptions()
                            .title(contact.getFullName())
                            .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(mCustomMarkerView, R.drawable.avatar)))
                            .position(new LatLng(
                                    contact.getLatitude(),
                                    contact.getLongitude()
                            ))
                    );
                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.w("TAG", "loadPost:onCancelled", error.toException());
            }
        });*/
        return root;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity)
            appCompatActivity = (AppCompatActivity) context;
    }

    @Override
    public void onMapReady(HuaweiMap huaweiMap) {
        mHuaweiMap = huaweiMap;
        mHuaweiMap.setMyLocationEnabled(true);
        mHuaweiMap.getUiSettings().setZoomControlsEnabled(true);
        mHuaweiMap.getUiSettings().setMyLocationButtonEnabled(true);
        mHuaweiMap.setLocationSource(new LocationSource() {
            @Override
            public void activate(OnLocationChangedListener onLocationChangedListener) {
                locationChangedListener = onLocationChangedListener;
            }

            @Override
            public void deactivate() {

            }
        });

       try {
            Task<Location> lastLocation = fusedLocationProviderClient.getLastLocation();
            if(lastLocation!=null)
                lastLocation.addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        locationChangedListener.onLocationChanged(location);
                        if (locationChangedListener != null && location!=null) {
                            locationChangedListener.onLocationChanged(location);
                            addCustomeMarker(new LatLng(location.getLatitude(), location.getLongitude()));


                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                    }
                });
          createMarkersFromJson();
        } catch (Exception e) {
            Log.d("last Location ", e.getMessage());
        }

    }

    private void initViews(View root, Bundle savedInstanceState) {

        mCustomMarkerView = ((LayoutInflater) appCompatActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custome_view_marker, null);
        mMarkerImageView = (ImageView) mCustomMarkerView.findViewById(R.id.profile_image);
        mapView = root.findViewById(R.id.map);
        Bundle bundle = null;
        if (savedInstanceState != null) {
            bundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mapView.onCreate(bundle);
        mapView.getMapAsync(this);

    }


    private void addCustomeMarker(LatLng latLng) {
        Glide.with(appCompatActivity).
                load(ImageUrl)
                .asBitmap()
                .fitCenter()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                        Log.i(Contants.TAG,"Marker "+ImageUrl);
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                                latLng, 12f);
                        mHuaweiMap.animateCamera(cameraUpdate);
                        mHuaweiMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title("user name")
                                .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(mCustomMarkerView, bitmap))));

                    }
                });
        Log.i(Contants.TAG,"Marker no load image");
    }

    private Bitmap getMarkerBitmapFromView(View view, @DrawableRes int resId) {

        mMarkerImageView.setImageResource(resId);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = view.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        view.draw(canvas);
        return returnedBitmap;
    }
    private void createMarkersFromJson() throws JSONException {
        // De-serialize the JSON string into an array of city objects

        reference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                for (DataSnapshot data: snapshot.getChildren()) {
                    Contact contact = data.getValue(Contact.class);
                    mHuaweiMap.addMarker(new MarkerOptions()
                            .title(contact.getFullName())
                            .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(mCustomMarkerView, R.drawable.avatar)))
                            .position(new LatLng(
                                    contact.getLatitude(),
                                    contact.getLongitude()
                            ))
                    );
                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.w("TAG", "loadPost:onCancelled", error.toException());
            }
        });

    }
    private Bitmap getMarkerBitmapFromView(View view, Bitmap bitmap) {

        mMarkerImageView.setImageBitmap(bitmap);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = view.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        view.draw(canvas);
        return returnedBitmap;
    }

    @Override
    public void onStart() {
        super.onStart();
        checkLocationSetting.requestLocation();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkLocationSetting.requestLocationUpdatesWithCallback();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        checkLocationSetting.removeLocationUpdatesWithCallback();
        mapView.onDestroy();
    }



}