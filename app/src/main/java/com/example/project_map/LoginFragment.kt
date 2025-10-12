package com.example.project_map

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment

class LoginFragment : Fragment(R.layout.fragment_login) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loginButton = view.findViewById<Button>(R.id.btn_login_submit)
        val forgotPassword = view.findViewById<TextView>(R.id.tv_forgot_password)

        loginButton.setOnClickListener {
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            activity?.finishAffinity()
        }

        forgotPassword.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Forgot Password")
                .setMessage("A password reset link has been sent to your email (simulation).")
                .setPositiveButton("OK", null)
                .show()
        }
    }
}