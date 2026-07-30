package com.example.atlasinvest.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Consome a API pública brapi.dev (cotações da bolsa brasileira).
 * Documentação: https://brapi.dev/docs/acoes
 * Tickers de teste que funcionam SEM token: PETR4, VALE3, MGLU3, ITUB4.
 * Para outros tickers, é necessário criar uma conta gratuita em brapi.dev
 * e informar o token no parâmetro "token".
 */
interface CotacaoApiService {
    @GET("api/quote/{tickers}")
    suspend fun buscarCotacoes(
            @Path("tickers") tickers: String, // ex: "PETR4,VALE3"
            @Query("token") token: String? = null
    ): CotacaoApiResponse
}