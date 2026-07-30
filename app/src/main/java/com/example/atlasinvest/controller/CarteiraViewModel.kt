package com.example.atlasinvest.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.atlasinvest.data.local.entity.Ativo
import com.example.atlasinvest.data.local.entity.Cotacao
import com.example.atlasinvest.data.local.entity.TipoAtivo
import com.example.atlasinvest.data.repository.AtivoRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CarteiraViewModel(
        private val usuarioId: Long,
        private val ativoRepository: AtivoRepository
) : ViewModel() {

    val ativos: StateFlow<List<Ativo>> =
    ativoRepository.observarAtivos(usuarioId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val cotacoes: StateFlow<List<Cotacao>> =
    ativos.flatMapLatest { lista ->
            ativoRepository.observarCotacoes(lista.map { it.ticker }.distinct())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        atualizarCotacoes()
    }

    fun atualizarCotacoes() {
        viewModelScope.launch { ativoRepository.atualizarCotacoes(usuarioId) }
    }

    fun cadastrarAtivo(
            nome: String,
            ticker: String,
            tipo: TipoAtivo,
            quantidade: Double,
            precoCompra: Double
    ) {
        viewModelScope.launch {
            ativoRepository.cadastrarAtivo(
                    Ativo(
                            usuarioId = usuarioId,
                            nome = nome,
                            ticker = ticker.uppercase(),
                            tipo = tipo,
                            quantidade = quantidade,
                            precoCompra = precoCompra,
                            dataAquisicao = System.currentTimeMillis()
                    )
            )
            atualizarCotacoes()
        }
    }

    fun removerAtivo(ativo: Ativo) {
        viewModelScope.launch { ativoRepository.removerAtivo(ativo) }
    }
}