package com.e.martineetalk

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

    private lateinit var remoteConfig: FirebaseRemoteConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(0)
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.default_config)

        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val updated = task.getResult()
//                    Log.d(TAG, "Config params updated: $updated")
                } else {

                }
                displayMessage()
            }
    }

    private fun displayMessage() {
        val splashBackground:String = remoteConfig.getString("splash_background")
        val caps:Boolean = remoteConfig.getBoolean("splash_message_caps")
        val splashMessage:String = remoteConfig.getString("splash_message")

        splashactivity_linearlayout.setBackgroundColor(Color.parseColor(splashBackground))

        if (caps) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(splashMessage).setPositiveButton("확인") {
                    _: DialogInterface?, _: Int -> splashMessage
                finish()
            }
            builder.create().show()
        } else {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
            finish()
        }
    }
}
