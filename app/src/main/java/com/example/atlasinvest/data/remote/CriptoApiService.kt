package com.example.atlasinvest.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Endpoint separado da brapi.dev para criptomoedas — NÃO é o mesmo
 * /api/quote usado para ações e FIIs, porque cripto não é negociada na B3.
 * Documentação: https://brapi.dev/docs/criptomoedas
 */
interface CriptoApiService {
    @GET("api/v2/crypto")
    suspend fun buscarCotacao(
        @Query("coin") coin: String,          // ex: "BTC", "ETH"
        @Query("currency") currency: String = "BRL",
        @Query("token") token: String? = null,
        @Query("range") range: String? = null,
        @Query("interval") interval: String? = null,
    ): CriptoApiResponse
}