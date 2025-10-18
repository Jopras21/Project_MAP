package com.example.project_map

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash) // layout splash

        val sharedPref = getSharedPreferences(PrefConstants.PREF_NAME, MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false) // cek login

        // delay sebentar lalu arahkan
        Handler(Looper.getMainLooper()).postDelayed({
            if (isLoggedIn) {
                startActivity(Intent(this, MainActivity::class.java)) // ke main
            } else {
                startActivity(Intent(this, LoginActivity::class.java)) // ke login
            }
            finish() // tutup splash
        }, 1500)
    }
}
