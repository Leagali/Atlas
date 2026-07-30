package com.example.atlasinvest.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.atlasinvest.data.local.entity.Categoria
import com.example.atlasinvest.data.local.entity.Movimentacao
import com.example.atlasinvest.data.local.entity.TipoMovimentacao
import com.example.atlasinvest.data.repository.CategoriaRepository
import com.example.atlasinvest.data.repository.MovimentacaoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class ResumoFinanceiro(
        val totalReceitas: Double = 0.0,
        val totalDespesas: Double = 0.0,
        val gastosPorCategoria: Map<Categoria, Double> = emptyMap()
)

/**
 * Equivale ao "Controle de Relatório" da Figura 10 (RF10/RF11).
 */
class RelatorioViewModel(
        usuarioId: Long,
        movimentacaoRepository: MovimentacaoRepository,
        categoriaRepository: CategoriaRepository
) : ViewModel() {

    val resumo: StateFlow<ResumoFinanceiro> =
    combine(
            movimentacaoRepository.observarMovimentacoes(usuarioId),
            categoriaRepository.observarCategorias(usuarioId)
    ) { movimentacoes, categorias ->
            calcularResumo(movimentacoes, categorias)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ResumoFinanceiro())

    private fun calcularResumo(
            movimentacoes: List<Movimentacao>,
    categorias: List<Categoria>
    ): ResumoFinanceiro {
        val totalReceitas = movimentacoes.filter { it.tipo == TipoMovimentacao.RECEITA }.sumOf { it.valor }
        val totalDespesas = movimentacoes.filter { it.tipo == TipoMovimentacao.DESPESA }.sumOf { it.valor }

        val gastosPorCategoria = movimentacoes
                .filter { it.tipo == TipoMovimentacao.DESPESA }
            .groupBy { it.categoriaId }
            .mapNotNull { (categoriaId, lista) ->
                val categoria = categorias.find { it.id == categoriaId } ?: return@mapNotNull null
            categoria to lista.sumOf { it.valor }
        }
            .toMap()

        return ResumoFinanceiro(totalReceitas, totalDespesas, gastosPorCategoria)
    }
}