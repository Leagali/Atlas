package com.example.atlasinvest.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cotacoes")
data class Cotacao(
        @PrimaryKey
        val ticker: String,
        val precoAtual: Double,
        val variacao: Double,
        val dividendYield: Double? = null,
        val ultimaAtualizacao: Long = System.currentTimeMillis()
)