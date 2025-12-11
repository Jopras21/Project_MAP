package com.example.project_map.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.project_map.Product
import com.example.project_map.StockHistory
import com.example.project_map.User
import com.example.project_map.repository.HomeRepository

class HomeViewModel : ViewModel() {

    private val repo = HomeRepository()

    private val _products = MutableLiveData<List<Product>>(emptyList())
    val products: LiveData<List<Product>> get() = _products

    private val _history = MutableLiveData<List<StockHistory>>(emptyList())
    val history: LiveData<List<StockHistory>> get() = _history

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> get() = _user

    fun start(context: Context) {
        repo.listenProducts(
            context,
            onChanged = { list -> _products.postValue(list) },
            onError = { }
        )

        repo.listenStockHistory(
            context,
            onChanged = { list -> _history.postValue(list) },
            onError = { }
        )

        repo.listenUser(
            context,
            onChanged = { u -> _user.postValue(u) }
        )
    }

    override fun onCleared() {
        super.onCleared()
        repo.clear()
    }

    fun getTotalProduk(): Int {
        val list = _products.value ?: return 0
        return list.size
    }

    fun getRataHarga(): Double {
        val list = _products.value ?: return 0.0
        if (list.isEmpty()) return 0.0
        return list.sumOf { it.listedPrice } / list.size
    }

    fun getTotalStok(): Int {
        val list = _products.value ?: return 0
        return list.sumOf { it.stok }
    }

    fun getPromoAktif(): Int {
        val list = _products.value ?: return 0
        return list.count { it.promoAktif }
    }

    fun getTotalMasuk(): Int {
        val list = _history.value ?: return 0
        return list.filter { it.jenis == "MASUK" }.sumOf { it.jumlah }
    }

    fun getTotalKeluar(): Int {
        val list = _history.value ?: return 0
        return list.filter { it.jenis == "KELUAR" }.sumOf { it.jumlah }
    }

    fun getProdukTerlaris(): String {
        val list = _history.value ?: return "-"
        val hasil = list.filter { it.jenis == "KELUAR" }
            .groupBy { it.namaProduk }
            .mapValues { entry -> entry.value.sumOf { h -> h.jumlah } }
            .maxByOrNull { it.value }

        return hasil?.key ?: "-"
    }

    fun getStokTerendah(): String {
        val list = _products.value ?: return "-"
        val item = list.minByOrNull { it.stok }
        return item?.let { "${it.nama} (${it.stok})" } ?: "-"
    }
}
