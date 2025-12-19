package com.example.project_map

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.project_map.viewmodel.ProfileViewModel
import java.io.ByteArrayOutputStream

class ProfilFragment : Fragment() {

    private lateinit var vm: ProfileViewModel
    private lateinit var email: String

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                openCamera()
            } else {
                Toast.makeText(requireContext(), "Izin kamera diperlukan untuk fitur ini", Toast.LENGTH_SHORT).show()
            }
        }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {

                val data = result.data
                val bitmap = data?.extras?.get("data") as? Bitmap

                if (bitmap != null) {
                    val stream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                    val base64 =
                        Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT)

                    view?.findViewById<ImageView>(R.id.ivProfile)
                        ?.setImageBitmap(bitmap)

                    vm.updatePhoto(email, base64)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Gagal mengambil foto",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_profil, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        vm = ViewModelProvider(this)[ProfileViewModel::class.java]

        val sp = requireActivity()
            .getSharedPreferences(PrefConstants.PREF_NAME, AppCompatActivity.MODE_PRIVATE)

        email = sp.getString(PrefConstants.KEY_EMAIL, "") ?: ""

        val tvNama = view.findViewById<TextView>(R.id.tvNama)
        val tvEmail = view.findViewById<TextView>(R.id.tvEmail)
        val ivProfile = view.findViewById<ImageView>(R.id.ivProfile)

        val layoutView = view.findViewById<LinearLayout>(R.id.layoutViewMode)
        val layoutEdit = view.findViewById<LinearLayout>(R.id.layoutEditMode)
        val layoutPass = view.findViewById<LinearLayout>(R.id.layoutChangePasswordMode)
        val layoutEtNama = view.findViewById<View>(R.id.layoutEtNama)

        fun showView() {
            layoutView.isVisible = true
            layoutEdit.isVisible = false
            layoutPass.isVisible = false
            layoutEtNama.isVisible = false
        }

        vm.observeUser(email)
        vm.userData.observe(viewLifecycleOwner) { data ->
            if (data != null) {
                tvNama.text = data["name"] as? String ?: ""
                tvEmail.text = data["email"] as? String ?: ""

                val photo64 = data["photoBase64"] as? String
                if (!photo64.isNullOrEmpty()) {
                    try {
                        val bytes = Base64.decode(photo64, Base64.DEFAULT)
                        ivProfile.setImageBitmap(
                            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        vm.updateResult.observe(viewLifecycleOwner) { ok ->
            if (ok) {
                Toast.makeText(
                    requireContext(),
                    "Perubahan berhasil",
                    Toast.LENGTH_SHORT
                ).show()
                showView()
            }
        }

        view.findViewById<Button>(R.id.btnEdit).setOnClickListener {
            layoutView.isVisible = false
            layoutEdit.isVisible = true
            layoutEtNama.isVisible = true
        }

        view.findViewById<Button>(R.id.btnBatal).setOnClickListener {
            showView()
        }

        view.findViewById<Button>(R.id.btnSimpan).setOnClickListener {
            val newName =
                view.findViewById<EditText>(R.id.etNama).text.toString().trim()
            if (newName.isNotEmpty()) {
                vm.updateName(email, newName)
            }
        }

        view.findViewById<Button>(R.id.btnChangePhoto).setOnClickListener {
            checkAndOpenCamera()
        }

        view.findViewById<Button>(R.id.btnChangePassword).setOnClickListener {
            layoutEdit.isVisible = false
            layoutPass.isVisible = true
        }

        view.findViewById<Button>(R.id.btnPasswordBack).setOnClickListener {
            showView()
        }

        view.findViewById<Button>(R.id.btnPasswordSave).setOnClickListener {
            val newPass =
                view.findViewById<EditText>(R.id.etNewPassword).text.toString()
            val confirm =
                view.findViewById<EditText>(R.id.etConfirmPassword).text.toString()

            if (newPass == confirm && newPass.length >= 8) {
                vm.updatePassword(email, newPass)
            } else {
                Toast.makeText(requireContext(), "Password tidak cocok / kurang dari 8 karakter", Toast.LENGTH_SHORT).show()
            }
        }

        view.findViewById<Button>(R.id.btnLogout).setOnClickListener {
            sp.edit().clear().apply()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }
    }

    private fun checkAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            openCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun openCamera() {
        try {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraLauncher.launch(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), "Aplikasi kamera tidak ditemukan", Toast.LENGTH_SHORT).show()
        } catch (e: SecurityException) {
            Toast.makeText(requireContext(), "Akses kamera ditolak oleh sistem", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}