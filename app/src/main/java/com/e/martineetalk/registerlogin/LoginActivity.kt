package com.e.martineetalk.registerlogin

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.e.martineetalk.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var remoteConfig: FirebaseRemoteConfig
    private var auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        remoteConfig = FirebaseRemoteConfig.getInstance()
        val buttonBackground:String = remoteConfig.getString("button_background")
        window.statusBarColor = Color.parseColor(buttonBackground)

        signup_btn.setBackgroundColor(Color.parseColor(buttonBackground))
        login_btn.setBackgroundColor(Color.parseColor(buttonBackground))

        login_btn.setOnClickListener {
            val email = login_edittext_email.text.toString()
            val password = login_edittext_pw.text.toString()

            Log.d("Login", "Attempt login with email/pw: $email/***")

            auth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener()
        }

        signup_btn.setOnClickListener {
            val registerIntent = Intent(this, RegisterActivity::class.java)
            startActivity(registerIntent)
        }
    }
}
