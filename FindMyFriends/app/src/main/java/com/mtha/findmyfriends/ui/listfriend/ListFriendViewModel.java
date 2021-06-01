package com.mtha.findmyfriends.ui.listfriend;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ListFriendViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ListFriendViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}