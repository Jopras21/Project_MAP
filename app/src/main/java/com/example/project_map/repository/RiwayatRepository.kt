package com.example.project_map.repository

import android.content.Context
import com.example.project_map.FirestoreService
import com.example.project_map.StockHistory
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Calendar
import java.util.concurrent.TimeUnit

class RiwayatRepository {

    private var listener: ListenerRegistration? = null

    fun listenRiwayat(
        context: Context,
        productId: String?,
        timeFilter: TimeFilter,
        onChanged: (List<StockHistory>) -> Unit,
        onError: (Exception) -> Unit
    ) {

        listener?.remove()
        listener = null

        val userId = FirestoreService.getUserId(context) ?: return

        val (startTime, endTime) = getTimeRange(timeFilter)

        var query = Firebase.firestore.collection("stock_history")
            .whereEqualTo("userId", userId)
            .whereGreaterThanOrEqualTo("createdAt", startTime)
            .whereLessThan("createdAt", endTime)

        if (!productId.isNullOrEmpty()) {
            query = query.whereEqualTo("productId", productId)
        }

        listener = query.addSnapshotListener { snapshot, e ->
            if (e != null) {
                onError(e)
                return@addSnapshotListener
            }

            val list = snapshot?.documents
                ?.mapNotNull { it.toObject(StockHistory::class.java) }
                ?: emptyList()

            onChanged(list)
        }
    }

    private fun getTimeRange(filter: TimeFilter): Pair<Long, Long> {
        val now = System.currentTimeMillis()

        return when (filter) {
            TimeFilter.TODAY -> {
                val cal = Calendar.getInstance()
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                cal.timeInMillis to (cal.timeInMillis + TimeUnit.DAYS.toMillis(1))
            }

            TimeFilter.LAST_7_DAYS -> {
                (now - TimeUnit.DAYS.toMillis(7)) to now
            }

            TimeFilter.LAST_30_DAYS -> {
                (now - TimeUnit.DAYS.toMillis(30)) to now
            }
        }
    }

    fun clear() {
        listener?.remove()
        listener = null
    }
}
