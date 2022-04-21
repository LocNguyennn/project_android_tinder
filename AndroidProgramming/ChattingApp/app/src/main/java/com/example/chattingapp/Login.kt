package com.example.chattingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class Login : AppCompatActivity() {
    private lateinit var login_Email:EditText
    private lateinit var login_Password:EditText
    private lateinit var btnLogin: Button
    private lateinit var btnSignup: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_Email = findViewById(R.id.login_Email)
        login_Password = findViewById(R.id.login_Password)
        btnSignup = findViewById<Button>(R.id.btnSignup)
        btnLogin = findViewById<Button>(R.id.btnLogin)

        btnSignup.setOnClickListener{
            val intent = Intent(this@Login, Signup::class.java)
            startActivity(intent)
        }
    }
}