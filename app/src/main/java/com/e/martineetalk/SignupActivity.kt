package com.e.martineetalk

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.android.synthetic.main.activity_signup.*

class SignupActivity : AppCompatActivity() {

    private lateinit var remoteConfig: FirebaseRemoteConfig
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        remoteConfig = FirebaseRemoteConfig.getInstance()
        val buttonBackground:String = remoteConfig.getString("button_background")
        window.statusBarColor = Color.parseColor(buttonBackground)

        signup_btn_signup.setOnClickListener {
           performResgister()

//
        }

        already_have_account_textview.setOnClickListener {
            Log.d("SignupActivity", "Try to show login activity")

            // launch the login activity somehow
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }

    private fun performResgister() {
        val email = signup_edittext_email.text.toString()
        val password = signup_edittext_pw.text.toString()

        auth = FirebaseAuth.getInstance()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "모든 칸을 채워주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("SignupActivity", "email: $email")
        Log.d("SignupActivity", "password: $password")

        // Firebase Authentication to create a user with email and password
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (!it.isSuccessful) return@addOnCompleteListener

                //else if successful
                Log.d("RegisterActivity", "Successfully created user with uid: ${it.result?.user?.uid}")
            }
            .addOnFailureListener {
                Log.d("SignupActivity", "Failed to create user: ${it.message}")
                Toast.makeText(this, "회원가입 실패: ${it.message}.", Toast.LENGTH_SHORT).show()
            }
    }
}
