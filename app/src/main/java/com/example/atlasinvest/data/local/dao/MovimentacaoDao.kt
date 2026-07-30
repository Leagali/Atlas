package com.example.atlasinvest.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.atlasinvest.data.local.entity.Movimentacao
import com.example.atlasinvest.data.local.entity.TipoMovimentacao
import kotlinx.coroutines.flow.Flow

@Dao
interface MovimentacaoDao {
    @Insert
    suspend fun inserir(movimentacao: Movimentacao): Long

    @Update
    suspend fun atualizar(movimentacao: Movimentacao)

    @Delete
    suspend fun excluir(movimentacao: Movimentacao)

    @Query("SELECT * FROM movimentacoes WHERE usuarioId = :usuarioId ORDER BY data DESC")
    fun observarPorUsuario(usuarioId: Long): Flow<List<Movimentacao>>

    @Query("""
        SELECT * FROM movimentacoes
        WHERE usuarioId = :usuarioId AND data BETWEEN :inicio AND :fim
        ORDER BY data DESC
    """)
    fun observarPorPeriodo(usuarioId: Long, inicio: Long, fim: Long): Flow<List<Movimentacao>>

    @Query("""
        SELECT COALESCE(SUM(CASE WHEN tipo = 'RECEITA' THEN valor ELSE -valor END), 0)
        FROM movimentacoes WHERE usuarioId = :usuarioId
    """)
    fun observarSaldo(usuarioId: Long): Flow<Double>

    @Query("""
        SELECT COALESCE(SUM(valor), 0) FROM movimentacoes
        WHERE usuarioId = :usuarioId AND tipo = :tipo AND data BETWEEN :inicio AND :fim
    """)
    suspend fun somarPorTipoEPeriodo(usuarioId: Long, tipo: TipoMovimentacao, inicio: Long, fim: Long): Double
}