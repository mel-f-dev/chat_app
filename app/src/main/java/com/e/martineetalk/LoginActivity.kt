package com.e.martineetalk

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.view.*
import kotlinx.android.synthetic.main.activity_signup.*

class LoginActivity : AppCompatActivity() {

    private lateinit var remoteConfig: FirebaseRemoteConfig
    private lateinit var auth: FirebaseAuth

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
            val signupIntent = Intent(this, SignupActivity::class.java)
            startActivity(signupIntent)
        }
    }
}
