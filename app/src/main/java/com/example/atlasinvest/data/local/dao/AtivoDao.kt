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

    @Query("SELECT * FROM ativos WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: Long): Ativo?

    // Ações e FIIs vão pro endpoint /api/quote da brapi.dev
    @Query("SELECT DISTINCT ticker FROM ativos WHERE usuarioId = :usuarioId AND tipo IN ('ACAO', 'FII')")
    suspend fun listarTickersNegociadosPorUsuario(usuarioId: Long): List<String>

    // Criptomoedas vão pro endpoint /api/v2/crypto, separado
    @Query("SELECT DISTINCT ticker FROM ativos WHERE usuarioId = :usuarioId AND tipo = 'CRIPTO'")
    suspend fun listarTickersCriptoPorUsuario(usuarioId: Long): List<String>
}