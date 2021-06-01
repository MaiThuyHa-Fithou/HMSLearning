package com.mtha.findmyfriends.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.huawei.hms.maps.MapView;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<MapView> mMap;

    public HomeViewModel() {
        mMap = new MutableLiveData<>();

    }

    public LiveData<MapView> getMapView() {
        return mMap;
    }
}