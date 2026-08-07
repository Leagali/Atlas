package com.example.atlasinvest.data.remote

import com.google.gson.annotations.SerializedName

data class CriptoApiResponse(
    @SerializedName("coins") val coins: List<CriptoResultDto> = emptyList(),
)

data class CriptoResultDto(
    @SerializedName("coin") val coin: String,
    @SerializedName("regularMarketPrice") val precoAtual: Double? = null,
    @SerializedName("regularMarketChangePercent") val variacaoPercentual: Double? = null,
    @SerializedName("historicalDataPrice") val historico: List<PontoHistoricoDto>? = null,
)