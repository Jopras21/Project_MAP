package com.example.project_map // Sesuaikan dengan package Anda

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment

class RegisterFragment : Fragment(R.layout.fragment_register) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val registerButton = view.findViewById<Button>(R.id.btn_register_submit)

        registerButton.setOnClickListener {
            // TODO : Make registration logic

            // Setelah sukses, pindah ke MainActivity
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            activity?.finishAffinity()
        }
    }
}