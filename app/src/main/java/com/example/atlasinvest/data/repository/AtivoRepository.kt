package com.example.atlasinvest.data.repository

import com.example.atlasinvest.data.local.dao.AtivoDao
import com.example.atlasinvest.data.local.dao.CotacaoDao
import com.example.atlasinvest.data.local.entity.Ativo
import com.example.atlasinvest.data.local.entity.Cotacao
import com.example.atlasinvest.data.local.entity.TipoAtivo
import com.example.atlasinvest.data.remote.CotacaoApiService
import com.example.atlasinvest.data.remote.CriptoApiService
import com.example.atlasinvest.data.remote.PontoHistoricoDto
import kotlinx.coroutines.flow.Flow

data class PontoHistorico(val data: Long, val fechamento: Double)

class AtivoRepository(
    private val ativoDao: AtivoDao,
    private val cotacaoDao: CotacaoDao,
    private val cotacaoApiService: CotacaoApiService,
    private val criptoApiService: CriptoApiService,
) {
    fun observarAtivos(usuarioId: Long): Flow<List<Ativo>> = ativoDao.observarPorUsuario(usuarioId)

    suspend fun buscarAtivoPorId(id: Long): Ativo? = ativoDao.buscarPorId(id)

    suspend fun cadastrarAtivo(ativo: Ativo): Long = ativoDao.inserir(ativo)

    suspend fun removerAtivo(ativo: Ativo) = ativoDao.excluir(ativo)

    fun observarCotacoes(tickers: List<String>): Flow<List<Cotacao>> =
        cotacaoDao.observarPorTickers(tickers)

    suspend fun buscarCotacao(ticker: String): Cotacao? = cotacaoDao.buscarPorTicker(ticker)

    // RF09 / RN07: cotações obtidas exclusivamente via API externa
    suspend fun atualizarCotacoes(usuarioId: Long) {
        atualizarCotacoesAcoesEFiis(usuarioId)
        atualizarCotacoesCripto(usuarioId)
    }

    private suspend fun atualizarCotacoesAcoesEFiis(usuarioId: Long) {
        val tickers = ativoDao.listarTickersNegociadosPorUsuario(usuarioId)
        if (tickers.isEmpty()) return
        try {
            val resposta = cotacaoApiService.buscarCotacoes(tickers.joinToString(","))
            resposta.results.forEach { resultado ->
                val preco = resultado.precoAtual ?: return@forEach
                cotacaoDao.salvarOuAtualizar(
                    Cotacao(ticker = resultado.symbol, precoAtual = preco, variacao = resultado.variacaoPercentual ?: 0.0)
                )
            }
        } catch (e: Exception) {
            // Falha de rede/API: mantém as últimas cotações salvas localmente (cache).
        }
    }

    private suspend fun atualizarCotacoesCripto(usuarioId: Long) {
        val moedas = ativoDao.listarTickersCriptoPorUsuario(usuarioId)
        if (moedas.isEmpty()) return
        // A brapi.dev pede uma moeda por chamada nesse endpoint (diferente do /api/quote,
        // que aceita vários tickers separados por vírgula de uma vez).
        for (moeda in moedas) {
            try {
                val resposta = criptoApiService.buscarCotacao(coin = moeda)
                val resultado = resposta.coins.firstOrNull() ?: continue
                val preco = resultado.precoAtual ?: continue
                cotacaoDao.salvarOuAtualizar(
                    Cotacao(ticker = resultado.coin, precoAtual = preco, variacao = resultado.variacaoPercentual ?: 0.0)
                )
            } catch (_: Exception) {
                // idem: mantém o último valor em cache pra essa moeda
            }
        }
    }

    // Histórico de preços para o gráfico da tela de detalhe do ativo.
    suspend fun buscarHistorico(ativo: Ativo, range: String = "6mo"): List<PontoHistorico> {
        return try {
            if (ativo.tipo == TipoAtivo.CRIPTO) {
                val resposta = criptoApiService.buscarCotacao(coin = ativo.ticker, range = range, interval = "1d")
                resposta.coins.firstOrNull()?.historico?.paraPontos() ?: emptyList()
            } else {
                val resposta = cotacaoApiService.buscarCotacoes(tickers = ativo.ticker, range = range, interval = "1d")
                resposta.results.firstOrNull()?.historico?.paraPontos() ?: emptyList()
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun List<PontoHistoricoDto>.paraPontos(): List<PontoHistorico> =
        mapNotNull { ponto ->
            val dataSegundos = ponto.data ?: return@mapNotNull null
            val fechamento = ponto.fechamento ?: return@mapNotNull null
            PontoHistorico(data = dataSegundos * 1000, fechamento = fechamento)
        }
}