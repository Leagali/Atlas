package com.example.atlasinvest.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.atlasinvest.data.local.entity.Categoria
import com.example.atlasinvest.data.repository.CategoriaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class CategoriaViewModel(
        usuarioId: Long,
        private val categoriaRepository: CategoriaRepository
) : ViewModel() {

    val categorias: StateFlow<List<Categoria>> =
    categoriaRepository.observarCategorias(usuarioId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}