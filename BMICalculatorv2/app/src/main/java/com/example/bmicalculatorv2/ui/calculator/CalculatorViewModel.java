package com.example.bmicalculatorv2.ui.calculator;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bmicalculatorv2.MainActivity;


public class CalculatorViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public CalculatorViewModel() {
        mText = new MutableLiveData<>();

        //mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }


}