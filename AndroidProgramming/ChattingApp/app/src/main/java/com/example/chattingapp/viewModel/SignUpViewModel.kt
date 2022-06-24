package com.example.chattingapp.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.regex.Pattern

class SignUpViewModel : ViewModel() {
    private var _isSuccessEvent: MutableLiveData<Boolean> = MutableLiveData()
    val isSuccessEvent: LiveData<Boolean>
        get() = _isSuccessEvent

    private var _isErrorEvent: MutableLiveData<String> = MutableLiveData()
    val isErrorEvent: LiveData<String>
        get() = _isErrorEvent

    fun checkValidEmailAndPassword(email: String, password: String, confirmPassword: String) {
        val isValidEmail = isEmailValid(email)
        if (!isValidEmail) {
            _isErrorEvent.postValue("Invalid email type")
            return
        }
        val isValidPassword = isPasswordValid(password)
        val isValidConfirmPassword = isPasswordValid(confirmPassword)
        if (!isValidPassword || !isValidConfirmPassword) {
            _isErrorEvent.postValue("Password must have at least 8 character (including uppercase, lowercase, special character)")
            return
        } else if (!password.equals(confirmPassword)) {
            _isErrorEvent.postValue("Wrong confirm password")
            return
        }
        _isSuccessEvent.postValue(true)
    }

    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isPasswordValid(password: String): Boolean {
        val regex =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()])(?=\\S+$).{8,}$")
        return regex.matcher(password).matches()
    }
}