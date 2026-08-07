package com.example.atlasinvest.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.atlasinvest.data.local.entity.Movimentacao
import com.example.atlasinvest.data.local.entity.TipoMovimentacao
import com.example.atlasinvest.data.local.entity.Usuario
import com.example.atlasinvest.data.repository.MetaRepository
import com.example.atlasinvest.data.repository.MovimentacaoRepository
import com.example.atlasinvest.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MovimentacaoViewModel(
        private val usuarioId: Long,
        private val movimentacaoRepository: MovimentacaoRepository,
        private val metaRepository: MetaRepository,
        private val usuarioRepository: UsuarioRepository,
) : ViewModel() {

    val usuario: StateFlow<Usuario?> =
        usuarioRepository.observarUsuario(usuarioId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val movimentacoes: StateFlow<List<Movimentacao>> =
    movimentacaoRepository.observarMovimentacoes(usuarioId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val saldo: StateFlow<Double> =
    movimentacaoRepository.observarSaldo(usuarioId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // RF02/RF03/RN02: toda movimentação precisa de categoria e data
    fun registrar(
            valor: Double,
            categoriaId: Long,
            descricao: String,
            tipo: TipoMovimentacao,
            metaId: Long? = null,
            data: Long = System.currentTimeMillis()
    ) {
        viewModelScope.launch {
            movimentacaoRepository.registrar(
                    Movimentacao(
                            usuarioId = usuarioId,
                            categoriaId = categoriaId,
                            metaId = metaId,
                            valor = valor,
                            data = data,
                            descricao = descricao,
                            tipo = tipo
                    )
            )
            // RN05: progresso da meta é atualizado automaticamente
            if (metaId != null && tipo == TipoMovimentacao.RECEITA) {
                metaRepository.atualizarProgresso(metaId, valor)
            }
        }
    }

    // RF04 / RN10: edição e exclusão impactam imediatamente o saldo
    fun editar(movimentacao: Movimentacao) {
        viewModelScope.launch { movimentacaoRepository.editar(movimentacao) }
    }

    fun excluir(movimentacao: Movimentacao) {
        viewModelScope.launch { movimentacaoRepository.excluir(movimentacao) }
    }
}