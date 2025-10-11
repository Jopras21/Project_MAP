// File: /app/src/main/java/com/example/project_map/RegisterFragment.kt

package com.example.project_map // Sesuaikan dengan nama package Anda

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class RegisterFragment : Fragment(R.layout.fragment_register) {

    // Deklarasikan semua view dengan tipe datanya secara eksplisit
    private lateinit var tilPassword: TextInputLayout
    private lateinit var etPassword: TextInputEditText
    private lateinit var ivLengthCheck: ImageView
    private lateinit var tvLengthCheck: TextView
    private lateinit var ivUppercaseCheck: ImageView
    private lateinit var tvUppercaseCheck: TextView
    private lateinit var ivNumberCheck: ImageView
    private lateinit var tvNumberCheck: TextView
    private lateinit var tilEmail: TextInputLayout
    private lateinit var etEmail: TextInputEditText
    private var isPasswordValid = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi semua view dari layout menggunakan findViewById
        tilPassword = view.findViewById(R.id.til_register_password)
        etPassword = view.findViewById(R.id.et_register_password)
        ivLengthCheck = view.findViewById(R.id.iv_length_check)
        tvLengthCheck = view.findViewById(R.id.tv_length_check)
        ivUppercaseCheck = view.findViewById(R.id.iv_uppercase_check)
        tvUppercaseCheck = view.findViewById(R.id.tv_uppercase_check)
        ivNumberCheck = view.findViewById(R.id.iv_number_check)
        tvNumberCheck = view.findViewById(R.id.tv_number_check)
        tilEmail = view.findViewById(R.id.til_register_email)
        etEmail = view.findViewById(R.id.et_register_email)
        val registerButton = view.findViewById<Button>(R.id.btn_register_submit)

        // Tambahkan TextWatcher untuk validasi password real-time
        etPassword.addTextChangedListener(passwordWatcher)

        registerButton.setOnClickListener {
            val isEmailValid = validateEmail()
            if (isEmailValid && isPasswordValid) {
                // Jika semua valid, lanjutkan
                Toast.makeText(requireContext(), "Registration Successful!", Toast.LENGTH_SHORT).show()
                val intent = Intent(activity, MainActivity::class.java)
                startActivity(intent)
                activity?.finishAffinity()
            } else if (!isEmailValid) {
                Toast.makeText(requireContext(), "Please enter a valid email.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Please meet all password requirements.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val passwordWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            validatePassword(s.toString())
        }
    }

    private fun validatePassword(password: String) {
        val isLengthValid = password.length >= 8
        val hasUppercase = password.any { it.isUpperCase() }
        val hasNumber = password.any { it.isDigit() }

        updateCriteriaUI(ivLengthCheck, tvLengthCheck, isLengthValid)
        updateCriteriaUI(ivUppercaseCheck, tvUppercaseCheck, hasUppercase)
        updateCriteriaUI(ivNumberCheck, tvNumberCheck, hasNumber)

        isPasswordValid = isLengthValid && hasUppercase && hasNumber
    }

    private fun updateCriteriaUI(imageView: ImageView, textView: TextView, isValid: Boolean) {
        val context = requireContext()
        if (isValid) {
            imageView.setImageResource(R.drawable.ic_check_circle_green)
            textView.setTextColor(ContextCompat.getColor(context, R.color.validation_success_color))
        } else {
            imageView.setImageResource(R.drawable.ic_cancel_red)
            textView.setTextColor(ContextCompat.getColor(context, R.color.validation_fail_color))
        }
    }

    private fun validateEmail(): Boolean {
        val email = etEmail.text.toString().trim()
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.error = "Invalid Email Address"
            return false
        } else {
            tilEmail.error = null
            return true
        }
    }
}