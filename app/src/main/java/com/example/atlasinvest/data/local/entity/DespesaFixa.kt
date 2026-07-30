package com.example.atlasinvest.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class Periodicidade { SEMANAL, MENSAL, ANUAL }

@Entity(
        tableName = "despesas_fixas",
        foreignKeys = [
ForeignKey(entity = Usuario::class, parentColumns = ["id"], childColumns = ["usuarioId"], onDelete = ForeignKey.CASCADE),
ForeignKey(entity = Categoria::class, parentColumns = ["id"], childColumns = ["categoriaId"], onDelete = ForeignKey.SET_NULL)
    ],
indices = [Index("usuarioId"), Index("categoriaId")]
        )
data class DespesaFixa(
        @PrimaryKey(autoGenerate = true)
        val id: Long = 0L,
        val usuarioId: Long,
        val categoriaId: Long? = null,
        val nome: String,
        val valor: Double,
        val periodicidade: Periodicidade,
        val diaVencimento: Int? = null
)