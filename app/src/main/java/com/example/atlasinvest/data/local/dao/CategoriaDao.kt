package com.example.atlasinvest.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.atlasinvest.data.local.entity.Categoria
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriaDao {
    @Insert
    suspend fun inserir(categoria: Categoria): Long

    @Query("SELECT * FROM categorias WHERE usuarioId = :usuarioId ORDER BY nome")
    fun observarPorUsuario(usuarioId: Long): Flow<List<Categoria>>

    @Update
    suspend fun atualizar(categoria: Categoria)

    @Delete
    suspend fun excluir(categoria: Categoria)

    @Query("SELECT COUNT(*) FROM categorias WHERE usuarioId = :usuarioId")
    suspend fun contarPorUsuario(usuarioId: Long): Int
}