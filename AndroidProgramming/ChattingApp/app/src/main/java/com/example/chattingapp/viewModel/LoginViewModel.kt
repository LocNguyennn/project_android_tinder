package com.example.chattingapp.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chattingapp.sharePreference.MySharedPreferences
import java.util.regex.Pattern

class LoginViewModel(val sharedPrefs: MySharedPreferences) : ViewModel() {
    private var _isSuccessEvent: MutableLiveData<Boolean> = MutableLiveData()
    val isSuccessEvent: LiveData<Boolean>
        get() = _isSuccessEvent

    private var _isErrorEvent: MutableLiveData<String> = MutableLiveData()
    val isErrorEvent: LiveData<String>
        get() = _isErrorEvent

    fun checkValidEmailAndPassword(email: String, password: String) {

        val isValidEmail = isEmailValid(email)
        if (!isValidEmail) {
            _isErrorEvent.postValue("Invalid email type")
            return
        }
        val isValidPassword = isPasswordValid(password)
        if (!isValidPassword) {
            _isErrorEvent.postValue("Wrong password type")
            return
        }
        _isSuccessEvent.postValue(true)
    }

    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun saveUserInfo(email: String, password: String) {
        saveEmail(email)
        savePassword(password)
    }

    private fun isPasswordValid(password: String): Boolean {
        val regex =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()])(?=\\S+$).{8,}$")
        return regex.matcher(password).matches()

    }

    fun rememberMe(isRemembered: Boolean) {
        sharedPrefs.saveRememberMe(isRemembered)
    }

    fun saveEmail(email: String) {
        sharedPrefs.saveEmail(email)
    }

    fun savePassword(password: String) {
        sharedPrefs.savePassword(password)
    }

    fun isRemember(): Boolean {
        return sharedPrefs.isRemembered()
    }
}