import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
// Pastikan nama paket ini benar
import com.example.project_map.R

class LoginFragment : Fragment(R.layout.fragment_login) {

    // Deklarasikan semua komponen UI di sini
    private lateinit var loginButton: Button
    private lateinit var etEmailLayout: TextInputLayout
    private lateinit var etPasswordLayout: TextInputLayout
    private lateinit var forgotPasswordButton: TextView
    private lateinit var registerButton: TextView // Variabel yang menyebabkan error

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginButton = view.findViewById(R.id.btn_login_submit)
        etEmailLayout = view.findViewById(R.id.til_login_email)
        etPasswordLayout = view.findViewById(R.id.til_login_password)
        forgotPasswordButton = view.findViewById(R.id.tv_forgot_password)

        registerButton = view.findViewById(R.id.tv_register_nav)

        loginButton.setOnClickListener {
            handleLogin()
        }

        forgotPasswordButton.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Forgot Password")
                .setMessage("A password reset link has been sent to your email (simulation).")
                .setPositiveButton("OK", null)
                .show()
        }

        registerButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun handleLogin() {
        val email = etEmailLayout.editText?.text.toString().trim()
        val password = etPasswordLayout.editText?.text.toString().trim()

        val isEmailValid = !email.isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val isPasswordValid = !password.isNullOrEmpty()

        etEmailLayout.error = null
        etPasswordLayout.error = null

        if (isEmailValid && isPasswordValid) {
            Toast.makeText(requireContext(), "Login Berhasil!", Toast.LENGTH_SHORT).show()
            // Pastikan action ini ada di nav_graph.xml
            findNavController().navigate(R.id.action_loginFragment_to_mainActivity)
        } else {
            if (!isEmailValid) {
                etEmailLayout.error = "Format email tidak valid"
            }
            if (!isPasswordValid) {
                etPasswordLayout.error = "Password tidak boleh kosong"
            }
        }
    }
}