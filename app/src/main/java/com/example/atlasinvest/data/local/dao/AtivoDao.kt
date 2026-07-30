package com.example.atlasinvest.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.atlasinvest.data.local.entity.Ativo
import kotlinx.coroutines.flow.Flow

@Dao
interface AtivoDao {
    @Insert
    suspend fun inserir(ativo: Ativo): Long

    @Update
    suspend fun atualizar(ativo: Ativo)

    @Delete
    suspend fun excluir(ativo: Ativo)

    @Query("SELECT * FROM ativos WHERE usuarioId = :usuarioId ORDER BY nome")
    fun observarPorUsuario(usuarioId: Long): Flow<List<Ativo>>

    @Query("SELECT DISTINCT ticker FROM ativos WHERE usuarioId = :usuarioId")
    suspend fun listarTickersPorUsuario(usuarioId: Long): List<String>
}