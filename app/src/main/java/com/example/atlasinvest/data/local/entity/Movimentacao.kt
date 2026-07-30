package com.example.atlasinvest.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class TipoMovimentacao { RECEITA, DESPESA }

@Entity(
        tableName = "movimentacoes",
        foreignKeys = [
ForeignKey(entity = Usuario::class, parentColumns = ["id"], childColumns = ["usuarioId"], onDelete = ForeignKey.CASCADE),
ForeignKey(entity = Categoria::class, parentColumns = ["id"], childColumns = ["categoriaId"], onDelete = ForeignKey.RESTRICT),
ForeignKey(entity = Meta::class, parentColumns = ["id"], childColumns = ["metaId"], onDelete = ForeignKey.SET_NULL)
    ],
indices = [Index("usuarioId"), Index("categoriaId"), Index("metaId")]
        )
data class Movimentacao(
        @PrimaryKey(autoGenerate = true)
        val id: Long = 0L,
        val usuarioId: Long,
        val categoriaId: Long,
        val metaId: Long? = null,
        val valor: Double,
        val data: Long,
        val descricao: String,
        val tipo: TipoMovimentacao
)