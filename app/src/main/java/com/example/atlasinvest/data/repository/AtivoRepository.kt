package com.example.atlasinvest.data.repository

import com.example.atlasinvest.data.local.dao.AtivoDao
import com.example.atlasinvest.data.local.dao.CotacaoDao
import com.example.atlasinvest.data.local.entity.Ativo
import com.example.atlasinvest.data.local.entity.Cotacao
import com.example.atlasinvest.data.remote.CotacaoApiService
import kotlinx.coroutines.flow.Flow

class AtivoRepository(
        private val ativoDao: AtivoDao,
        private val cotacaoDao: CotacaoDao,
        private val cotacaoApiService: CotacaoApiService
) {
    fun observarAtivos(usuarioId: Long): Flow<List<Ativo>> = ativoDao.observarPorUsuario(usuarioId)

    suspend fun cadastrarAtivo(ativo: Ativo): Long = ativoDao.inserir(ativo)

    suspend fun removerAtivo(ativo: Ativo) = ativoDao.excluir(ativo)

    fun observarCotacoes(tickers: List<String>): Flow<List<Cotacao>> =
            cotacaoDao.observarPorTickers(tickers)

    // RF09 / RN07: cotações obtidas exclusivamente via API externa
    suspend fun atualizarCotacoes(usuarioId: Long) {
        val tickers = ativoDao.listarTickersPorUsuario(usuarioId)
        if (tickers.isEmpty()) return
        try {
            val resposta = cotacaoApiService.buscarCotacoes(tickers.joinToString(","))
            resposta.results.forEach { resultado ->
                    val preco = resultado.precoAtual ?: return@forEach
                    cotacaoDao.salvarOuAtualizar(
                    Cotacao(
                            ticker = resultado.symbol,
                            precoAtual = preco,
                            variacao = resultado.variacaoPercentual ?: 0.0
                    )
                )
            }
        } catch (e: Exception) {
            // Falha de rede/API: mantém as últimas cotações salvas localmente (cache).
        }
    }
}