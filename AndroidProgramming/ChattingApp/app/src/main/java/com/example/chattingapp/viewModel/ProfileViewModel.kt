package com.example.chattingapp.viewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.chattingapp.sharePreference.MySharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ProfileViewModel(val sharedPrefs : MySharedPreferences) : ViewModel() {
    fun rememberMe(isRemembered: Boolean) {
        sharedPrefs.saveRememberMe(isRemembered)
    }
}