// File: AuthActivity.kt
package com.example.project_map

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class AuthActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_SELECTED_TAB = "extra_selected_tab"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val btnLogin = findViewById<Button>(R.id.btn_login)
        val btnRegister = findViewById<Button>(R.id.btn_register)

        btnLogin.setOnClickListener {
            // Pindah ke LoginRegisterActivity dengan membawa data "tab login" (indeks 0)
            val intent = Intent(this, LoginRegisterActivity::class.java).apply {
                putExtra(EXTRA_SELECTED_TAB, 0)
            }
            startActivity(intent)
        }

        btnRegister.setOnClickListener {
            // Pindah ke LoginRegisterActivity dengan membawa data "tab register" (indeks 1)
            val intent = Intent(this, LoginRegisterActivity::class.java).apply {
                putExtra(EXTRA_SELECTED_TAB, 1)
            }
            startActivity(intent)
        }
    }
}