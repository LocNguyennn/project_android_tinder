package com.example.chattingapp.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.chattingapp.ChattingApp
import com.example.chattingapp.viewModel.HomeViewModel
import com.example.chattingapp.viewModel.LoginViewModel

class HomeViewModelFactory(val app: ChattingApp) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(HomeViewModel::class.java)){
            return HomeViewModel(app.prefs) as T
        }
        throw IllegalArgumentException("unknown view model")
    }

}