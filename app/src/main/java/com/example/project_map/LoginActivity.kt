package com.example.project_map

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.security.MessageDigest

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login) // layout login

        // view
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvToRegister = findViewById<TextView>(R.id.tvToRegister)

        val sharedPref = getSharedPreferences(PrefConstants.PREF_NAME, MODE_PRIVATE)

        // auto-skip jika sudah login
        val isLoggedIn = sharedPref.getBoolean(PrefConstants.KEY_IS_LOGGED_IN, false)
        if (isLoggedIn) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        // tombol login
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // validasi dasar
            if (email.isEmpty() || password.isEmpty()) {
                showToast("Email dan password harus diisi!")
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showToast("Format email tidak valid!")
                return@setOnClickListener
            }

            // ambil data tersimpan
            val savedEmail = sharedPref.getString(PrefConstants.KEY_EMAIL, "")
            val savedPasswordHash = sharedPref.getString(PrefConstants.KEY_PASSWORD, "")
            val savedName = sharedPref.getString(PrefConstants.KEY_USERNAME, "Pengguna")

            val passwordInputHash = hash(password) // hash input

            // cek kredensial
            if (email == savedEmail && passwordInputHash == savedPasswordHash) {
                sharedPref.edit().putBoolean(PrefConstants.KEY_IS_LOGGED_IN, true).apply()
                showToast("Selamat datang, $savedName!")
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                showToast("Email atau password salah!")
            }
        }

        // ke register
        tvToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }

    // hash SHA-256
    private fun hash(text: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(text.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }

    // helper toast
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
