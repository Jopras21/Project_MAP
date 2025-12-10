package com.example.project_map

data class Product(
    var id: String? = null,
    var userId: String = "",
    var nama: String = "",
    var listedPrice: Double = 0.0,
    var discountedPrice: Double = 0.0,
    var priceGap: Double = 0.0,
    var stok: Int = 0,
    var promoAktif: Boolean = false
)
