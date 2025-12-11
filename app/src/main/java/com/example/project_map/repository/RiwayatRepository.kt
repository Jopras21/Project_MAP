package com.example.project_map.repository

import android.content.Context
import com.example.project_map.FirestoreService
import com.example.project_map.StockHistory
import com.google.firebase.firestore.ListenerRegistration

class RiwayatRepository {

    private var historyListener: ListenerRegistration? = null

    fun listenRiwayat(
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

    fun clear() {
        historyListener?.remove()
    }
}
