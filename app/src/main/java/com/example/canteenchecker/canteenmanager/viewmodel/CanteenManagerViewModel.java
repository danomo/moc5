package com.example.canteenchecker.canteenmanager.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.canteenchecker.canteenmanager.domainobjects.Canteen;

public class CanteenManagerViewModel extends ViewModel {

    private MutableLiveData<Canteen> canteen = new MutableLiveData();

    public CanteenManagerViewModel() {
    }

    public CanteenManagerViewModel(MutableLiveData<Canteen> canteen) {
        this.canteen = canteen;
    }

    public LiveData<Canteen> getCanteen() {
        return canteen;
    }

    public void setCanteen(Canteen c) {

        this.canteen.setValue(c);
    }
}
