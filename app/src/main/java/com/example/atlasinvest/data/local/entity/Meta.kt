package com.example.atlasinvest.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class StatusMeta { EM_ANDAMENTO, CONCLUIDA, ATRASADA }

@Entity(
        tableName = "metas",
        foreignKeys = [
ForeignKey(entity = Usuario::class, parentColumns = ["id"], childColumns = ["usuarioId"], onDelete = ForeignKey.CASCADE)
    ],
indices = [Index("usuarioId")]
        )
data class Meta(
        @PrimaryKey(autoGenerate = true)
        val id: Long = 0L,
        val usuarioId: Long,
        val nome: String,
        val valorAlvo: Double,
        val valorAcumulado: Double = 0.0,
        val prazo: Long,
        val status: StatusMeta = StatusMeta.EM_ANDAMENTO
)