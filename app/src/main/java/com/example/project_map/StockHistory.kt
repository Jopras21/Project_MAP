package com.example.project_map

data class StockHistory(
    var id: String? = null,
    var userId: String = "",
    var productId: String = "",
    var namaProduk: String = "",
    var jumlah: Int = 0,
    var jenis: String = "",
    var tanggal: String = ""
)
