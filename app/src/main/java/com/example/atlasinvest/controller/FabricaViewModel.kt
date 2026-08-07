package com.example.atlasinvest.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.atlasinvest.AtlasInvestApplication

class FabricaViewModel(
    private val app: AtlasInvestApplication,
    private val usuarioId: Long = -1L,
    private val ativoId: Long = -1L,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AutenticacaoViewModel::class.java) ->
                AutenticacaoViewModel(app.usuarioRepository, app.categoriaRepository) as T

            modelClass.isAssignableFrom(MovimentacaoViewModel::class.java) ->
                MovimentacaoViewModel(usuarioId, app.movimentacaoRepository, app.metaRepository, app.usuarioRepository) as T

            modelClass.isAssignableFrom(MetaViewModel::class.java) ->
                MetaViewModel(usuarioId, app.metaRepository, app.usuarioRepository) as T

            modelClass.isAssignableFrom(CarteiraViewModel::class.java) ->
                CarteiraViewModel(usuarioId, app.ativoRepository) as T

            modelClass.isAssignableFrom(CategoriaViewModel::class.java) ->
                CategoriaViewModel(usuarioId, app.categoriaRepository) as T

            modelClass.isAssignableFrom(RelatorioViewModel::class.java) ->
                RelatorioViewModel(usuarioId, app.movimentacaoRepository, app.categoriaRepository) as T

            modelClass.isAssignableFrom(AtivoDetalheViewModel::class.java) ->
                AtivoDetalheViewModel(ativoId, app.ativoRepository) as T

            else -> throw IllegalArgumentException("ViewModel desconhecida: ${modelClass.name}")
        }
    }
}