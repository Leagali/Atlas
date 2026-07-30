package com.example.atlasinvest.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class TipoAtivo { ACAO, FII, RENDA_FIXA, CRIPTO, OUTRO }

@Entity(
        tableName = "ativos",
        foreignKeys = [
ForeignKey(entity = Usuario::class, parentColumns = ["id"], childColumns = ["usuarioId"], onDelete = ForeignKey.CASCADE)
    ],
indices = [Index("usuarioId"), Index("ticker")]
        )
data class Ativo(
        @PrimaryKey(autoGenerate = true)
        val id: Long = 0L,
        val usuarioId: Long,
        val nome: String,
        val ticker: String,
        val tipo: TipoAtivo,
        val quantidade: Double,
        val precoCompra: Double,
        val dataAquisicao: Long
)