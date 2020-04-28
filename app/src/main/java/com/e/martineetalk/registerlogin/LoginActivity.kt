package com.e.martineetalk.registerlogin

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.e.martineetalk.R
import com.e.martineetalk.messages.LatestMessagesActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private var auth = FirebaseAuth.getInstance()
    private lateinit var remoteConfig: FirebaseRemoteConfig
    var authStateListener: FirebaseAuth.AuthStateListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val email = login_edittext_email.text.toString()
        val password = login_edittext_pw.text.toString()

        remoteConfig = FirebaseRemoteConfig.getInstance()
        val buttonBackground:String = remoteConfig.getString("button_background")
        window.statusBarColor = Color.parseColor(buttonBackground)

        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            Log.e("LLPP", "로그인")


        }

        login_btn.setOnClickListener {
            Log.d("Login", "Attempt login with email/pw: $email/***")

            loginWithEmail()

//            auth.signInWithEmailAndPassword(email, password)
////                .addOnCompleteListener()
        }

        register_btn.setOnClickListener {
            val registerIntent = Intent(this, RegisterActivity::class.java)
            startActivity(registerIntent)
            finish()
        }
    }

    private fun loginWithEmail() {
        val email = login_edittext_email.text.toString()
        val password = login_edittext_pw.text.toString()

        var user = auth.currentUser
        if (user != null) {
            var intent = Intent(this, LatestMessagesActivity::class.java)
            startActivity(intent)
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
                }
            }

    }
}
