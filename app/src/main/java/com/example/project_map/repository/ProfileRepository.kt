package com.example.project_map.repository

import com.example.project_map.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfileRepository {

    private val db = Firebase.firestore

    fun listenUser(email: String, callback: (User?) -> Unit) {
        db.collection("users").document(email)
            .addSnapshotListener { snapshot, _ ->
                callback(snapshot?.toObject(User::class.java))
            }
    }

    fun updateName(email: String, newName: String, onResult: (Boolean) -> Unit) {
        db.collection("users").document(email)
            .update("name", newName)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun updatePassword(email: String, newHash: String, onResult: (Boolean) -> Unit) {
        db.collection("users").document(email)
            .update("passwordHash", newHash)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }
}
