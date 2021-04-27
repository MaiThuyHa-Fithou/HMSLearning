package com.mtha.findmyfriends.ui.friends;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.huawei.hms.maps.MapView;

public class FriendsViewModel extends ViewModel {

    private MutableLiveData<MapView> mMap;

    public FriendsViewModel() {
        mMap = new MutableLiveData<>();

    }

    public LiveData<MapView> getMapView() {
        return mMap;
    }
}