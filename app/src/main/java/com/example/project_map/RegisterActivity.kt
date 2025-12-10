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

class RegisterActivity : AppCompatActivity() {

    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etName = findViewById<EditText>(R.id.etName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val tvToLogin = findViewById<TextView>(R.id.tvToLogin)

        tvToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        btnRegister.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                showToast("Semua kolom harus diisi!")
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showToast("Format email tidak valid!")
                return@setOnClickListener
            }
            if (password.length < 8) {
                showToast("Password minimal 8 karakter!")
                return@setOnClickListener
            }
            if (password != confirmPassword) {
                showToast("Konfirmasi password tidak cocok!")
                return@setOnClickListener
            }

            val hashedPassword = hash(password)
            val createdAt = System.currentTimeMillis()

            val userDocRef = db.collection("users").document(email)

            userDocRef.get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        showToast("Email sudah terdaftar!")
                        return@addOnSuccessListener
                    }

                    val user = User(
                        id = email,
                        name = name,
                        email = email,
                        passwordHash = hashedPassword,
                        photoUrl = "",
                        createdAt = createdAt,
                        lastLogin = createdAt
                    )

                    userDocRef.set(user)
                        .addOnSuccessListener {
                            val sharedPref = getSharedPreferences(PrefConstants.PREF_NAME, MODE_PRIVATE)
                            sharedPref.edit().apply {
                                putString(PrefConstants.KEY_USERNAME, name)
                                putString(PrefConstants.KEY_EMAIL, email)
                                putString(PrefConstants.KEY_PASSWORD, hashedPassword)
                                putBoolean(PrefConstants.KEY_IS_LOGGED_IN, false)
                                apply()
                            }

                            showToast("Registrasi berhasil! Silakan login.")
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        }
                        .addOnFailureListener { e ->
                            showToast("Gagal menyimpan user: ${e.message}")
                        }
                }
                .addOnFailureListener { e ->
                    showToast("Gagal mengecek user: ${e.message}")
                }
        }
    }
    private fun hash(text: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(text.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
