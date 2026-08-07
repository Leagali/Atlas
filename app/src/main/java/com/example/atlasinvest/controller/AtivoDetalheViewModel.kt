package com.example.atlasinvest.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.atlasinvest.data.local.entity.Ativo
import com.example.atlasinvest.data.local.entity.Cotacao
import com.example.atlasinvest.data.repository.AtivoRepository
import com.example.atlasinvest.data.repository.PontoHistorico
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EstadoDetalheAtivo(
    val carregando: Boolean = true,
    val ativo: Ativo? = null,
    val cotacaoAtual: Cotacao? = null,
    val historico: List<PontoHistorico> = emptyList(),
)

class AtivoDetalheViewModel(
    private val ativoId: Long,
    private val ativoRepository: AtivoRepository,
) : ViewModel() {

    private val _estado = MutableStateFlow(EstadoDetalheAtivo())
    val estado: StateFlow<EstadoDetalheAtivo> = _estado.asStateFlow()

    init {
        carregar()
    }

    fun carregar() {
        viewModelScope.launch {
            _estado.value = _estado.value.copy(carregando = true)

            val ativo = ativoRepository.buscarAtivoPorId(ativoId)
            if (ativo == null) {
                _estado.value = _estado.value.copy(carregando = false)
                return@launch
            }

            val cotacao = ativoRepository.buscarCotacao(ativo.ticker)
            val historico = ativoRepository.buscarHistorico(ativo)

            _estado.value = EstadoDetalheAtivo(
                carregando = false,
                ativo = ativo,
                cotacaoAtual = cotacao,
                historico = historico
            )
        }
    }
}