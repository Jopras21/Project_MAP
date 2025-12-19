package com.example.project_map.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.project_map.StockHistory
import com.example.project_map.repository.HistoryTypeFilter
import com.example.project_map.repository.RiwayatRepository
import com.example.project_map.repository.TimeFilter

class RiwayatViewModel : ViewModel() {

    private val repo = RiwayatRepository()

    private val _riwayat = MutableLiveData<List<StockHistory>>(emptyList())
    val riwayat: LiveData<List<StockHistory>> = _riwayat

    fun load(
        context: Context,
        productId: String?,
        timeFilter: TimeFilter,
        typeFilter: HistoryTypeFilter
    ) {
        repo.listenRiwayat(
            context = context,
            productId = productId,
            timeFilter = timeFilter,
            onChanged = { list ->

                val filtered = when (typeFilter) {
                    HistoryTypeFilter.ALL -> list
                    HistoryTypeFilter.MASUK -> list.filter { it.jenis == "MASUK" }
                    HistoryTypeFilter.KELUAR -> list.filter { it.jenis == "KELUAR" }
                }

                _riwayat.postValue(filtered)
            },
            onError = { }
        )
    }

    override fun onCleared() {
        super.onCleared()
        repo.clear()
    }
}
