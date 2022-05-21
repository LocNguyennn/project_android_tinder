package com.example.chattingapp.viewModel

import androidx.lifecycle.ViewModel
import com.example.chattingapp.Model.User

class SharedViewModel : ViewModel() {
    private var user = User()

    fun sendData(user: User) {
        this.user = user
    }
    fun receiveData() : User{
        return this.user;
    }
}