package com.example.project_map.repository

import com.google.firebase.firestore.FirebaseFirestore

class ProfileRepository {

    private val db = FirebaseFirestore.getInstance()

    fun observeUser(
        email: String,
        onResult: (Map<String, Any>?) -> Unit
    ) {
        db.collection("users").document(email)
            .addSnapshotListener { snap, _ ->
                onResult(snap?.data)
            }
    }

    fun updateName(email: String, name: String, cb: (Boolean) -> Unit) {
        db.collection("users").document(email)
            .update("name", name)
            .addOnSuccessListener { cb(true) }
            .addOnFailureListener { cb(false) }
    }

    fun updatePassword(email: String, hash: String, cb: (Boolean) -> Unit) {
        db.collection("users").document(email)
            .update("passwordHash", hash)
            .addOnSuccessListener { cb(true) }
            .addOnFailureListener { cb(false) }
    }

    fun updatePhoto(email: String, base64: String, cb: (Boolean) -> Unit) {
        db.collection("users").document(email)
            .update("photoBase64", base64)
            .addOnSuccessListener { cb(true) }
            .addOnFailureListener { cb(false) }
    }
}
