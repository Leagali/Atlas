package com.example.atlasinvest.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.atlasinvest.data.local.entity.Meta
import com.example.atlasinvest.data.repository.MetaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MetaViewModel(
        private val usuarioId: Long,
        private val metaRepository: MetaRepository
) : ViewModel() {

    val metas: StateFlow<List<Meta>> =
    metaRepository.observarMetas(usuarioId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // RF06 / RN04: toda meta deve ter valor-alvo e prazo
    fun criar(nome: String, valorAlvo: Double, prazo: Long) {
        viewModelScope.launch {
            metaRepository.criar(
                    Meta(usuarioId = usuarioId, nome = nome, valorAlvo = valorAlvo, prazo = prazo)
            )
        }
    }

    fun excluir(meta: Meta) {
        viewModelScope.launch { metaRepository.excluir(meta) }
    }
}