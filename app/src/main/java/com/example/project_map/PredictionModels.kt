package com.example.project_map

data class PredictionRequest(
    val listed_price: Double,
    val discounted_price: Double,
    val price_gap: Double,
    val stok: Int,
    val promo_aktif: Int
)

data class PredictionResponse(
    val prediksi_penjualan: Double
)
