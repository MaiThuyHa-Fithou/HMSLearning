package com.mtha.findmyfriends.ui.home;

import android.Manifest;
import android.app.Activity;

import android.content.Context;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectUser;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;

import com.huawei.hms.location.FusedLocationProviderClient;

import com.huawei.hms.location.LocationAvailability;
import com.huawei.hms.location.LocationCallback;
import com.huawei.hms.location.LocationRequest;
import com.huawei.hms.location.LocationResult;
import com.huawei.hms.location.LocationServices;

import com.huawei.hms.location.SettingsClient;
import com.huawei.hms.maps.CameraUpdate;
import com.huawei.hms.maps.CameraUpdateFactory;
import com.huawei.hms.maps.HuaweiMap;
import com.huawei.hms.maps.LocationSource;
import com.huawei.hms.maps.MapView;
import com.huawei.hms.maps.MapsInitializer;
import com.huawei.hms.maps.OnMapReadyCallback;
import com.huawei.hms.maps.model.BitmapDescriptorFactory;
import com.huawei.hms.maps.model.CameraPosition;
import com.huawei.hms.maps.model.LatLng;
import com.huawei.hms.maps.model.MarkerOptions;

import com.mtha.findmyfriends.R;
import com.mtha.findmyfriends.data.model.Contact;
import com.mtha.findmyfriends.utils.CheckLocationSetting;


import org.jetbrains.annotations.NotNull;

import org.json.JSONException;

import java.util.List;


public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private AppCompatActivity appCompatActivity;
    private MapView mapView;
    private HuaweiMap mHuaweiMap;
    private LocationSource.OnLocationChangedListener locationChangedListener;
    private HomeViewModel friendsViewModel;
    private final static String MAPVIEW_BUNDLE_KEY = "mapview";
    private FusedLocationProviderClient fusedLocationProviderClient;
    AGConnectUser user;
    FirebaseDatabase database;
    DatabaseReference reference;

    String uid;
    //current location
    SettingsClient settingsClient;
    LocationRequest mLocationRequest;
    LocationCallback mLocationCallback;
    double latitude, longitude;
    int count;
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
        initViews(root, savedInstanceState);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        user = AGConnectAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        Log.e("uid", "[" + uid + "]");
        getCurrentLocation();
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
        if (ActivityCompat.checkSelfPermission(appCompatActivity, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(appCompatActivity, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mHuaweiMap.setMyLocationEnabled(true);
        mHuaweiMap.getUiSettings().setZoomControlsEnabled(true);
        mHuaweiMap.getUiSettings().setMyLocationButtonEnabled(true);
        CameraPosition build = new CameraPosition.Builder().target(new LatLng(latitude, longitude)).zoom(4).build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(build);
        mHuaweiMap.animateCamera(cameraUpdate);
        // updLocation();
        try {
            createMarkersFromJson();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getCurrentLocation(){
        //create a fusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(appCompatActivity);
        //create a settingsClient
        settingsClient = LocationServices.getSettingsClient(appCompatActivity);
        mLocationRequest = new LocationRequest();
        // set the interval for location updates, in milliseconds.
        mLocationRequest.setInterval(10000);
        // set the priority of the request
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (null == mLocationCallback) {
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult != null) {
                        List<Location> locations = locationResult.getLocations();
                        if (!locations.isEmpty()) {
                            Location loc = locations.get(0);
                            latitude = loc.getLatitude();
                            longitude = loc.getLongitude();
                            reference.child("users").child(uid).child("latitude").setValue(latitude);
                            reference.child("users").child(uid).child("longtitude").setValue(longitude);
                        }
                    }
                }
                @Override
                public void onLocationAvailability(LocationAvailability locationAvailability) {
                    if (locationAvailability != null) {
                        boolean flag = locationAvailability.isLocationAvailable();
                        Toast.makeText(appCompatActivity, "isLocationAvailable:"+flag, Toast.LENGTH_SHORT).show();
                    }
                }
            };
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

                for (DataSnapshot data : snapshot.getChildren()) {
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

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
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
        mapView.onDestroy();
    }


}