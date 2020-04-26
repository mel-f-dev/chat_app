package com.e.martineetalk

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_signup.*
import java.util.*

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

        signup_selectphoto_btn.setOnClickListener {
            Log.d("SignupActivity", "Try to show photo selector")

            val photoIntent = Intent(Intent.ACTION_PICK)
            photoIntent.type = "image/*"
            startActivityForResult(photoIntent, 0)
        }
    }

    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            // proceed and check what the selected image was...
            Log.d("SignupActivity", "Photo was selected")

            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            signup_selectphoto_imgview.setImageBitmap(bitmap)
            signup_selectphoto_btn.alpha = 0f
//            val bitmapDrawable = BitmapDrawable(bitmap)
//            signup_selectphoto_btn.setBackgroundDrawable(bitmapDrawable)

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
                Log.d("SignupActivity", "Successfully created user with uid: ${it.result?.user?.uid}")

                uploadImageToFirebaseStorage()
            }
            .addOnFailureListener {
                Log.d("SignupActivity", "Failed to create user: ${it.message}")
                Toast.makeText(this, "회원가입 실패: ${it.message}.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("SignupActivity", "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    it.toString()
                    Log.d("SignupActivity", "File Location: $it")

                    saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener{
                //do some logging here
            }
    }

    private fun saveUserToFirebaseDatabase(profileImageUri: String) {
        val uid = auth.uid?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = User(uid, signup_edittext_name.text.toString(), profileImageUri)

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("SignupActivity", "Finally we saved the user to Firebase Database")
            }
    }
}

class User(val uid: String, val username: String, val profileImageUri: String)
