package com.example.project_map.repository

import android.content.Context
import com.example.project_map.FirestoreService
import com.example.project_map.Product
import com.example.project_map.StockHistory
import com.example.project_map.User
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeRepository {

    private var productListener: ListenerRegistration? = null
    private var historyListener: ListenerRegistration? = null
    private var userListener: ListenerRegistration? = null

    fun listenProducts(
        context: Context,
        onChanged: (List<Product>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        productListener = FirestoreService.listenProducts(
            context,
            { list -> onChanged(list) },
            { e -> onError(e) }
        )
    }

    fun listenStockHistory(
        context: Context,
        onChanged: (List<StockHistory>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        historyListener = FirestoreService.listenStockHistory(
            context,
            { list -> onChanged(list) },
            { e -> onError(e) }
        )
    }

    fun listenUser(
        context: Context,
        onChanged: (User) -> Unit
    ) {
        val userId = FirestoreService.getUserId(context) ?: return

        userListener = Firebase.firestore
            .collection("users")
            .document(userId)
            .addSnapshotListener { snapshot, _ ->
                snapshot?.toObject(User::class.java)?.let { user ->
                    onChanged(user)
                }
            }
    }

    fun clear() {
        productListener?.remove()
        historyListener?.remove()
        userListener?.remove()
    }
}
