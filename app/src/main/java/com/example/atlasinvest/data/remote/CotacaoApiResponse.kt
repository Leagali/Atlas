package com.example.atlasinvest.data.remote

import com.google.gson.annotations.SerializedName

data class CotacaoApiResponse(
    @SerializedName("results") val results: List<CotacaoResultDto> = emptyList()
)

data class CotacaoResultDto(
    @SerializedName("symbol") val symbol: String,
    @SerializedName("regularMarketPrice") val precoAtual: Double? = null,
    @SerializedName("regularMarketChangePercent") val variacaoPercentual: Double? = null,
    @SerializedName("historicalDataPrice") val historico: List<PontoHistoricoDto>? = null
)

data class PontoHistoricoDto(
    @SerializedName("date") val data: Long? = null,
    @SerializedName("close") val fechamento: Double? = null
)