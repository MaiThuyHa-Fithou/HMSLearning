package com.mtha.findmyfriends.ui.friends;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.common.ResolvableApiException;
import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.LocationAvailability;
import com.huawei.hms.location.LocationCallback;
import com.huawei.hms.location.LocationRequest;
import com.huawei.hms.location.LocationResult;
import com.huawei.hms.location.LocationServices;
import com.huawei.hms.location.LocationSettingsRequest;
import com.huawei.hms.location.LocationSettingsResponse;
import com.huawei.hms.location.LocationSettingsStatusCodes;
import com.huawei.hms.location.SettingsClient;
import com.huawei.hms.maps.CameraUpdate;
import com.huawei.hms.maps.CameraUpdateFactory;
import com.huawei.hms.maps.HuaweiMap;
import com.huawei.hms.maps.LocationSource;
import com.huawei.hms.maps.MapView;
import com.huawei.hms.maps.MapsInitializer;
import com.huawei.hms.maps.OnMapReadyCallback;
import com.huawei.hms.maps.SupportMapFragment;
import com.huawei.hms.maps.model.BitmapDescriptor;
import com.huawei.hms.maps.model.BitmapDescriptorFactory;
import com.huawei.hms.maps.model.LatLng;
import com.huawei.hms.maps.model.MarkerOptions;
import com.huawei.hms.maps.model.PolylineOptions;
import com.mtha.findmyfriends.R;
import com.mtha.findmyfriends.utils.Contants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FriendsFragment extends Fragment implements OnMapReadyCallback {

    private AppCompatActivity appCompatActivity;
    private MapView mapView;
    private HuaweiMap mHuaweiMap;
    private PolylineOptions polylineOptions;
    private LocationSource.OnLocationChangedListener locationChangedListener;
    private FriendsViewModel friendsViewModel;
    private final static String MAPVIEW_BUNDLE_KEY = "mapview";
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private boolean isRunning = false;
    private double latitude = 0, longitude = 0;

    //custom marker
    private final static String ImageUrl = "https://png.pngtree.com/png-clipart/20190924/original/pngtree-businessman-user-avatar-free-vector-png-image_4827807.jpg";
    private View mCustomMarkerView;
    private ImageView mMarkerImageView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        friendsViewModel =
                new ViewModelProvider(this).get(FriendsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_friends, container, false);

        MapsInitializer.setApiKey("CgB6e3x9dyJbX/s6uu5v+gaHs+baK3W8R172cby8T5mjldTWvJNSzhG4xJSTeehzt/bmcDAwYTgRhGU0RpCSvcIR");

        checkPermissions(appCompatActivity);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(appCompatActivity);
        initViews(root, savedInstanceState);
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
            createMarkersFromJson(loadJSONFromRaw());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //获取当前定位，更新地图Camera
        try {
            Task<Location> lastLocation = fusedLocationProviderClient.getLastLocation();
            if(lastLocation!=null)
                lastLocation.addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        locationChangedListener.onLocationChanged(location);
                        if (locationChangedListener != null) {
                            locationChangedListener.onLocationChanged(location);
                            addCustomeMarker(new LatLng(location.getLatitude(), location.getLongitude()));
                            /*
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(location.getLatitude(), location.getLongitude()), 15f);
                            mHuaweiMap.animateCamera(cameraUpdate);
                            mHuaweiMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.findmyfriends))
                                    .position(new LatLng(location.getLatitude(),location.getLongitude())));*/
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                    }
                });
        } catch (Exception e) {

        }

    }
    private String loadJSONFromRaw()  {
        final StringBuilder json = new StringBuilder();

        try{

            InputStream is = appCompatActivity.getResources().openRawResource(R.raw.logginuser);
            InputStreamReader in = new InputStreamReader(is);
            int size = is.available();
            int read;
            char[] buffer = new char[1024];
            while ((read = in.read(buffer)) != -1) {
                json.append(buffer, 0, read);
            }
            is.close();
            in.close();
        }catch (IOException ex){
            ex.printStackTrace();
            return null;
        }
        return json.toString();
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

    private void addCustomeMarker(String json, LatLng latLng){

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
    private void createMarkersFromJson(String json) throws JSONException {
        // De-serialize the JSON string into an array of city objects
        Log.d(Contants.TAG,"Json {" + json+ "}" );
        JSONObject jsonObject = new JSONObject(json);
        JSONArray jsonArray = jsonObject.getJSONArray("user");
        for (int i = 0; i < jsonArray.length(); i++) {
            // Create a marker for each city in the JSON data.
            JSONObject jsonObj = jsonArray.getJSONObject(i);
            Log.d(Contants.TAG, jsonObj.getString("username"));
            mHuaweiMap.addMarker(new MarkerOptions()
                    .title(jsonObj.getString("username"))
                    .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(mCustomMarkerView, R.drawable.avatar)))
                    .position(new LatLng(
                            jsonObj.getDouble("latitude"),
                            jsonObj.getDouble("longitude")
                    ))
            );
        }
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