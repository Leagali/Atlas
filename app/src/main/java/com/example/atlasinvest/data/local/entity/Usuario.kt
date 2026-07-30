package com.example.atlasinvest.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
        tableName = "usuarios",
        indices = [Index(value = ["email"], unique = true)]
        )
data class Usuario(
        @PrimaryKey(autoGenerate = true)
        val id: Long = 0L,
        val nome: String,
        val email: String,
        val senhaHash: String,
        val senhaSalt: String,
        val telefone: String? = null,
        val dataCadastro: Long = System.currentTimeMillis()
)