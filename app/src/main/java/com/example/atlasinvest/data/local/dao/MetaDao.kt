package com.example.atlasinvest.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.atlasinvest.data.local.entity.Meta
import kotlinx.coroutines.flow.Flow

@Dao
interface MetaDao {
    @Insert
    suspend fun inserir(meta: Meta): Long

    @Update
    suspend fun atualizar(meta: Meta)

    @Delete
    suspend fun excluir(meta: Meta)

    @Query("SELECT * FROM metas WHERE usuarioId = :usuarioId ORDER BY prazo ASC")
    fun observarPorUsuario(usuarioId: Long): Flow<List<Meta>>

    @Query("SELECT * FROM metas WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: Long): Meta?
}