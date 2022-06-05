package com.example.chattingapp.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chattingapp.model.User
import com.example.chattingapp.sharePreference.MySharedPreferences

class HomeViewModel(val sharedPrefs : MySharedPreferences) : ViewModel() {
    private var _listOfData: MutableLiveData<ArrayList<User>> = MutableLiveData()
    val listOfData: LiveData<ArrayList<User>>
        get() = _listOfData

    fun loadData(listUser: ArrayList<User>) {
        _listOfData.postValue(listUser)
    }
    fun getEmail(): String? {
        return sharedPrefs.getEmail();
    }
    fun getPassword() : String? {
        return sharedPrefs.getPassword();
    }
    fun isRememberMe() : Boolean{
        return sharedPrefs.isRemembered()
    }

}