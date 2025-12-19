package com.example.project_map.viewmodel

import android.util.Base64
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.project_map.repository.ProfileRepository
import java.security.MessageDigest

class ProfileViewModel : ViewModel() {

    private val repo = ProfileRepository()

    val userData = MutableLiveData<Map<String, Any>?>()
    val updateResult = MutableLiveData<Boolean>()

    fun observeUser(email: String) {
        repo.observeUser(email) {
            userData.postValue(it)
        }
    }

    fun updateName(email: String, name: String) {
        repo.updateName(email, name) {
            updateResult.postValue(it)
        }
    }

    fun updatePassword(email: String, raw: String) {
        val hash = sha256(raw)
        repo.updatePassword(email, hash) {
            updateResult.postValue(it)
        }
    }

    fun updatePhoto(email: String, base64: String) {
        repo.updatePhoto(email, base64) {
            updateResult.postValue(it)
        }
    }

    private fun sha256(text: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        return md.digest(text.toByteArray())
            .joinToString("") { "%02x".format(it) }
    }
}
