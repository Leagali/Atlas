package com.example.atlasinvest.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.atlasinvest.data.local.entity.DespesaFixa
import kotlinx.coroutines.flow.Flow

@Dao
interface DespesaFixaDao {
    @Insert
    suspend fun inserir(despesaFixa: DespesaFixa): Long

    @Update
    suspend fun atualizar(despesaFixa: DespesaFixa)

    @Delete
    suspend fun excluir(despesaFixa: DespesaFixa)

    @Query("SELECT * FROM despesas_fixas WHERE usuarioId = :usuarioId ORDER BY nome")
    fun observarPorUsuario(usuarioId: Long): Flow<List<DespesaFixa>>
}