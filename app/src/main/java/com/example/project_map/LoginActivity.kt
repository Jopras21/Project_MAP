package com.example.project_map

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.security.MessageDigest

class LoginActivity : AppCompatActivity() {

    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvToRegister = findViewById<TextView>(R.id.tvToRegister)

        val sharedPref = getSharedPreferences(PrefConstants.PREF_NAME, MODE_PRIVATE)

        if (sharedPref.getBoolean(PrefConstants.KEY_IS_LOGGED_IN, false)) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        tvToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                showToast("Email dan password harus diisi!")
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showToast("Format email tidak valid!")
                return@setOnClickListener
            }

            val passwordHash = hash(password)

            val userDoc = db.collection("users").document(email)

            userDoc.get()
                .addOnSuccessListener { snapshot ->
                    if (!snapshot.exists()) {
                        showToast("Email tidak terdaftar!")
                        return@addOnSuccessListener
                    }

                    val user = snapshot.toObject(User::class.java)

                    if (user == null) {
                        showToast("Data user rusak, hubungi admin!")
                        return@addOnSuccessListener
                    }

                    if (user.passwordHash != passwordHash) {
                        showToast("Password salah!")
                        return@addOnSuccessListener
                    }

                    userDoc.update("lastLogin", System.currentTimeMillis())

                    sharedPref.edit().apply {
                        putString(PrefConstants.KEY_USERNAME, user.name)
                        putString(PrefConstants.KEY_EMAIL, user.email)
                        putString(PrefConstants.KEY_PASSWORD, user.passwordHash)
                        putBoolean(PrefConstants.KEY_IS_LOGGED_IN, true)
                        apply()
                    }

                    showToast("Selamat datang, ${user.name}!")
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                .addOnFailureListener { e ->
                    showToast("Gagal login: ${e.message}")
                }
        }
    }

    private fun hash(text: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(text.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
