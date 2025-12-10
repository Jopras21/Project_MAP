package com.example.project_map

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.project_map.viewmodel.ProfileViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.security.MessageDigest

class ProfilFragment : Fragment() {

    private lateinit var viewModel: ProfileViewModel
    private lateinit var sharedPref: SharedPreferences

    private lateinit var tvNama: TextView
    private lateinit var tvEmail: TextView
    private lateinit var etNama: TextInputEditText
    private lateinit var layoutEtNama: TextInputLayout

    private lateinit var layoutView: LinearLayout
    private lateinit var layoutEdit: LinearLayout
    private lateinit var layoutPassword: LinearLayout

    private var userEmail: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_profil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        sharedPref = requireActivity()
            .getSharedPreferences(PrefConstants.PREF_NAME, AppCompatActivity.MODE_PRIVATE)

        userEmail = sharedPref.getString(PrefConstants.KEY_EMAIL, "") ?: ""

        tvNama = view.findViewById(R.id.tvNama)
        tvEmail = view.findViewById(R.id.tvEmail)
        etNama = view.findViewById(R.id.etNama)
        layoutEtNama = view.findViewById(R.id.layoutEtNama)

        layoutView = view.findViewById(R.id.layoutViewMode)
        layoutEdit = view.findViewById(R.id.layoutEditMode)
        layoutPassword = view.findViewById(R.id.layoutChangePasswordMode)

        val btnEdit = view.findViewById<Button>(R.id.btnEdit)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)
        val btnChangePassword = view.findViewById<Button>(R.id.btnChangePassword)
        val btnSave = view.findViewById<Button>(R.id.btnSimpan)
        val btnCancel = view.findViewById<Button>(R.id.btnBatal)
        val btnPassBack = view.findViewById<Button>(R.id.btnPasswordBack)
        val btnPassSave = view.findViewById<Button>(R.id.btnPasswordSave)

        viewModel.observeUser(userEmail)

        viewModel.userLiveData.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                tvNama.text = user.name
                tvEmail.text = user.email

                sharedPref.edit()
                    .putString(PrefConstants.KEY_USERNAME, user.name)
                    .apply()
            }
        }

        viewModel.updateStatus.observe(viewLifecycleOwner) { ok ->
            if (ok) {
                Toast.makeText(requireContext(), "Berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                exitModes()
            } else {
                Toast.makeText(requireContext(), "Gagal memperbarui!", Toast.LENGTH_SHORT).show()
            }
        }

        btnEdit.setOnClickListener { enterEditMode() }
        btnCancel.setOnClickListener { exitModes() }
        btnSave.setOnClickListener { saveNewName() }

        btnChangePassword.setOnClickListener { enterPasswordMode() }
        btnPassBack.setOnClickListener { exitModes() }
        btnPassSave.setOnClickListener { saveNewPassword() }

        btnLogout.setOnClickListener { logoutUser() }
    }

    private fun enterEditMode() {
        layoutView.isVisible = false
        layoutPassword.isVisible = false
        layoutEdit.isVisible = true
        layoutEtNama.isVisible = true
        etNama.setText(tvNama.text)
    }

    private fun enterPasswordMode() {
        layoutView.isVisible = false
        layoutEdit.isVisible = false
        layoutPassword.isVisible = true
    }

    private fun exitModes() {
        layoutView.isVisible = true
        layoutEdit.isVisible = false
        layoutPassword.isVisible = false
        layoutEtNama.isVisible = false
    }

    private fun saveNewName() {
        val newName = etNama.text.toString().trim()
        if (newName.isEmpty()) {
            Toast.makeText(requireContext(), "Nama tidak boleh kosong!", Toast.LENGTH_SHORT).show()
            return
        }
        viewModel.updateName(userEmail, newName)
    }

    private fun saveNewPassword() {
        val etCurrent = view?.findViewById<EditText>(R.id.etCurrentPassword)
        val etNew = view?.findViewById<EditText>(R.id.etNewPassword)
        val etConfirm = view?.findViewById<EditText>(R.id.etConfirmPassword)

        val current = etCurrent?.text.toString()
        val newPass = etNew?.text.toString()
        val confirm = etConfirm?.text.toString()

        val storedHash = sharedPref.getString(PrefConstants.KEY_PASSWORD, "")

        if (hash(current) != storedHash) {
            etCurrent?.error = "Password salah"
            return
        }
        if (newPass.length < 8) {
            etNew?.error = "Minimal 8 karakter"
            return
        }
        if (newPass != confirm) {
            etConfirm?.error = "Konfirmasi tidak cocok"
            return
        }

        val newHash = hash(newPass)
        viewModel.updatePassword(userEmail, newHash)
        sharedPref.edit().putString(PrefConstants.KEY_PASSWORD, newHash).apply()
    }

    private fun logoutUser() {
        sharedPref.edit().putBoolean(PrefConstants.KEY_IS_LOGGED_IN, false).apply()
        startActivity(Intent(requireContext(), LoginActivity::class.java))
        requireActivity().finish()
    }

    private fun hash(text: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        return md.digest(text.toByteArray())
            .joinToString("") { "%02x".format(it) }
    }
}
