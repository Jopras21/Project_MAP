package com.example.project_map.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.project_map.User
import com.example.project_map.repository.ProfileRepository

class ProfileViewModel : ViewModel() {

    private val repo = ProfileRepository()

    val userLiveData = MutableLiveData<User?>()
    val updateStatus = MutableLiveData<Boolean>()

    fun observeUser(email: String) {
        repo.listenUser(email) { user ->
            userLiveData.postValue(user)
        }
    }

    fun updateName(email: String, newName: String) {
        repo.updateName(email, newName) { ok ->
            updateStatus.postValue(ok)
        }
    }

    fun updatePassword(email: String, newHash: String) {
        repo.updatePassword(email, newHash) { ok ->
            updateStatus.postValue(ok)
        }
    }
}
