package com.example.chattingapp.sharePreference

import android.content.Context
import android.content.SharedPreferences

class MySharedPreferences {
    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        private const val PREF_NAME = "shared_preference"
    }

    enum class KEY(val value: String) {
        KEY_USERNAME("KEY_USERNAME"),
        KEY_PASSWORD("KEY_PASSWORD"),
        KEY_EMAIL("KEY_EMAIL"),
        KEY_REMEMBER_ME("KEY_REMEMBER_ME")
    }

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveUserName(userName: String) {
        sharedPreferences.edit().putString(KEY.KEY_USERNAME.value, userName).apply()
    }

    fun saveEmail(email: String) {
        sharedPreferences.edit().putString(KEY.KEY_EMAIL.value, email).apply()
    }

    fun savePassword(password: String) {
        sharedPreferences.edit().putString(KEY.KEY_PASSWORD.value, password).apply()
    }

    fun saveRememberMe(isRemember: Boolean) {
        sharedPreferences.edit().putBoolean(KEY.KEY_REMEMBER_ME.value, isRemember).apply()
    }

    fun getUserName(): String? {
        return sharedPreferences.getString(KEY.KEY_USERNAME.value, null)
    }

    fun getEmail(): String? {
        return sharedPreferences.getString(KEY.KEY_EMAIL.value, null)
    }

    fun getPassword(): String? {
        return sharedPreferences.getString(KEY.KEY_PASSWORD.value, null)
    }

    fun isRemembered(): Boolean {
        return sharedPreferences.getBoolean(KEY.KEY_REMEMBER_ME.value, false)
    }

}