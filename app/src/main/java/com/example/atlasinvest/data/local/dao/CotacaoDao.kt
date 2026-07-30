package com.example.atlasinvest.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.atlasinvest.data.local.entity.Cotacao
import kotlinx.coroutines.flow.Flow

@Dao
interface CotacaoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun salvarOuAtualizar(cotacao: Cotacao)

    @Query("SELECT * FROM cotacoes WHERE ticker = :ticker LIMIT 1")
    suspend fun buscarPorTicker(ticker: String): Cotacao?

    @Query("SELECT * FROM cotacoes WHERE ticker IN (:tickers)")
    fun observarPorTickers(tickers: List<String>): Flow<List<Cotacao>>
}