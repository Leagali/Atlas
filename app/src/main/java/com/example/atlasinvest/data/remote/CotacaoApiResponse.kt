package com.example.atlasinvest.data.remote

import com.google.gson.annotations.SerializedName

data class CotacaoApiResponse(
    @SerializedName("results") val results: List<CotacaoResultDto> = emptyList()
)

data class CotacaoResultDto(
    @SerializedName("symbol") val symbol: String,
    @SerializedName("regularMarketPrice") val precoAtual: Double? = null,
    @SerializedName("regularMarketChangePercent") val variacaoPercentual: Double? = null
)