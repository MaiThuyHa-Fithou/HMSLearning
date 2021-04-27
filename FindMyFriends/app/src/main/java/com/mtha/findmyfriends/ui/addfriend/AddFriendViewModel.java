package com.mtha.findmyfriends.ui.addfriend;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AddFriendViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AddFriendViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}