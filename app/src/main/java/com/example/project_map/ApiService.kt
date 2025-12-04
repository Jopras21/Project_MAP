package com.example.project_map

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {

    @Headers("Content-Type: application/json")
    @POST("/predict")
    fun predictSales(
        @Body request: PredictionRequest
    ): Call<PredictionResponse>
}
