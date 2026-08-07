package com.example.atlasinvest.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CotacaoApiService {
    @GET("api/quote/{tickers}")
    suspend fun buscarCotacoes(
        @Path("tickers") tickers: String,
        @Query("token") token: String? = null,
        @Query("range") range: String? = null,
        @Query("interval") interval: String? = null
    ): CotacaoApiResponse
}