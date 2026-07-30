package com.example.atlasinvest.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class TipoCategoria { RECEITA, DESPESA, AMBOS }

@Entity(
        tableName = "categorias",
        foreignKeys = [
ForeignKey(
        entity = Usuario::class,
        parentColumns = ["id"],
        childColumns = ["usuarioId"],
        onDelete = ForeignKey.CASCADE
)
    ],
indices = [Index("usuarioId")]
        )
data class Categoria(
        @PrimaryKey(autoGenerate = true)
        val id: Long = 0L,
        val usuarioId: Long,
        val nome: String,
        val icone: String = "categoria_padrao",
        val cor: String = "#4C6EF5",
        val tipo: TipoCategoria = TipoCategoria.AMBOS
)