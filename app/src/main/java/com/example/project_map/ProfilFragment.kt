package com.example.project_map

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.security.MessageDigest

class ProfilFragment : Fragment() {

    private lateinit var tvNama: TextView
    private lateinit var etNama: TextInputEditText
    private lateinit var layoutEtNama: TextInputLayout
    private lateinit var layoutEditMode: LinearLayout
    private lateinit var layoutViewMode: LinearLayout
    private lateinit var layoutPasswordMode: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvNama = view.findViewById(R.id.tvNama)
        etNama = view.findViewById(R.id.etNama)
        layoutEtNama = view.findViewById(R.id.layoutEtNama)
        layoutEditMode = view.findViewById(R.id.layoutEditMode)
        layoutViewMode = view.findViewById(R.id.layoutViewMode)
        layoutPasswordMode = view.findViewById(R.id.layoutChangePasswordMode)

        val btnEdit = view.findViewById<Button>(R.id.btnEdit)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)
        val btnChangePhoto = view.findViewById<Button>(R.id.btnChangePhoto)
        val btnChangePassword = view.findViewById<Button>(R.id.btnChangePassword)
        val btnSimpan = view.findViewById<Button>(R.id.btnSimpan)
        val btnBatal = view.findViewById<Button>(R.id.btnBatal)
        val btnPasswordBack = view.findViewById<Button>(R.id.btnPasswordBack)
        val btnPasswordSave = view.findViewById<Button>(R.id.btnPasswordSave)

        val sharedPref = requireActivity().getSharedPreferences(
            PrefConstants.PREF_NAME,
            AppCompatActivity.MODE_PRIVATE
        )

        tvNama.text = sharedPref.getString(PrefConstants.KEY_USERNAME, "Pengguna")
        exitAllModes()

        btnEdit.setOnClickListener { enterEditMode() }
        btnBatal.setOnClickListener { exitAllModes() }
        btnChangePhoto.setOnClickListener { Toast.makeText(requireContext(), "Fitur ubah foto belum diaktifkan 📸", Toast.LENGTH_SHORT).show() }
        btnLogout.setOnClickListener { showLogoutDialog(sharedPref) }
        btnChangePassword.setOnClickListener { enterPasswordMode() }
        btnPasswordBack.setOnClickListener { exitPasswordMode() }
        btnPasswordSave.setOnClickListener { saveNewPassword(sharedPref) }

        btnSimpan.setOnClickListener {
            val namaBaru = etNama.text.toString().trim()
            if (namaBaru.isEmpty()) {
                Toast.makeText(requireContext(), "Nama tidak boleh kosong!", Toast.LENGTH_SHORT).show()
            } else {
                showSaveNameDialog(sharedPref, namaBaru)
            }
        }
    }
    private fun enterEditMode() {
        layoutViewMode.isVisible = false
        tvNama.isVisible = false
        layoutPasswordMode.isVisible = false
        layoutEditMode.isVisible = true
        layoutEtNama.isVisible = true
        etNama.setText(tvNama.text)
    }
    private fun enterPasswordMode() {
        layoutViewMode.isVisible = false
        layoutEditMode.isVisible = false
        layoutEtNama.isVisible = false
        tvNama.isVisible = false
        layoutPasswordMode.isVisible = true
    }
    private fun exitPasswordMode() {
        layoutPasswordMode.isVisible = false
        enterEditMode() // Kembali ke mode edit nama
    }
    private fun exitAllModes() {
        layoutViewMode.isVisible = true
        tvNama.isVisible = true
        layoutEditMode.isVisible = false
        layoutEtNama.isVisible = false
        layoutPasswordMode.isVisible = false
    }
    private fun showSaveNameDialog(sharedPref: SharedPreferences, namaBaru: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Simpan Perubahan?")
            .setMessage("Apakah kamu yakin ingin memperbarui profil?")
            .setPositiveButton("Ya") { _, _ ->
                sharedPref.edit().putString(PrefConstants.KEY_USERNAME, namaBaru).apply()
                tvNama.text = namaBaru
                exitAllModes()
                Toast.makeText(requireContext(), "Profil berhasil diperbarui!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Tidak", null)
            .show()
    }
    private fun showLogoutDialog(sharedPref: SharedPreferences) {
        AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Apakah kamu yakin ingin keluar?")
            .setPositiveButton("Ya") { _, _ ->
                sharedPref.edit().putBoolean(PrefConstants.KEY_IS_LOGGED_IN, false).apply()
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                Toast.makeText(requireContext(), "Berhasil logout!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Tidak", null)
            .show()
    }
    private fun saveNewPassword(sharedPref: SharedPreferences) {
        val etCurrent = view?.findViewById<EditText>(R.id.etCurrentPassword)
        val etNew = view?.findViewById<EditText>(R.id.etNewPassword)
        val etConfirm = view?.findViewById<EditText>(R.id.etConfirmPassword)

        if (etCurrent == null || etNew == null || etConfirm == null) return

        val current = etCurrent.text.toString()
        val newPass = etNew.text.toString()
        val confirm = etConfirm.text.toString()
        val storedHash = sharedPref.getString(PrefConstants.KEY_PASSWORD, "")

        if (hash(current) != storedHash) {
            etCurrent.error = "Password saat ini salah"
            return
        }
        if (newPass.length < 8) {
            etNew.error = "Minimal 8 karakter"
            return
        }
        if (newPass != confirm) {
            etConfirm.error = "Konfirmasi tidak cocok"
            return
        }

        val newPasswordHash = hash(newPass)
        sharedPref.edit().putString(PrefConstants.KEY_PASSWORD, newPasswordHash).apply()
        Toast.makeText(requireContext(), "Password berhasil diubah!", Toast.LENGTH_SHORT).show()
        exitPasswordMode()
    }
    private fun hash(text: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(text.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }
}