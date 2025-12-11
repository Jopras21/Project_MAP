package com.example.project_map.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.project_map.StockHistory
import com.example.project_map.repository.RiwayatRepository

class RiwayatViewModel : ViewModel() {

    private val repo = RiwayatRepository()

    private val _riwayat = MutableLiveData<List<StockHistory>>(emptyList())
    val riwayat: LiveData<List<StockHistory>> get() = _riwayat

    fun start(context: Context) {
        repo.listenRiwayat(
            context,
            { list -> _riwayat.postValue(list) },
            { /* ignore */ }
        )
    }

    override fun onCleared() {
        super.onCleared()
        repo.clear()
    }
}
