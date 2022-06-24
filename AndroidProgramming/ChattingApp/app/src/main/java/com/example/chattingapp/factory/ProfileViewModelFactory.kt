package com.example.chattingapp.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.chattingapp.ChattingApp
import com.example.chattingapp.viewModel.HomeViewModel
import com.example.chattingapp.viewModel.ProfileViewModel

class ProfileViewModelFactory(val app: ChattingApp) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(app.prefs) as T
        }
        throw IllegalArgumentException("unknown view model")
    }
}