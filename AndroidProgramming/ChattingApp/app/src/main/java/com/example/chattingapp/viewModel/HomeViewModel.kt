package com.example.chattingapp.viewModel

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chattingapp.Model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HomeViewModel : ViewModel() {
    private var _listOfData: MutableLiveData<ArrayList<User>> = MutableLiveData()
    val listOfData: LiveData<ArrayList<User>>
        get() = _listOfData

    fun loadData(listUser: ArrayList<User>) {
        _listOfData.postValue(listUser)
    }
}