package com.example.atlasinvest.data.repository

import com.example.atlasinvest.data.local.dao.MovimentacaoDao
import com.example.atlasinvest.data.local.entity.Movimentacao
import kotlinx.coroutines.flow.Flow

class MovimentacaoRepository(private val movimentacaoDao: MovimentacaoDao) {

    fun observarMovimentacoes(usuarioId: Long): Flow<List<Movimentacao>> =
            movimentacaoDao.observarPorUsuario(usuarioId)

    fun observarSaldo(usuarioId: Long): Flow<Double> =
            movimentacaoDao.observarSaldo(usuarioId)

    suspend fun registrar(movimentacao: Movimentacao): Long =
            movimentacaoDao.inserir(movimentacao)

    suspend fun editar(movimentacao: Movimentacao) =
            movimentacaoDao.atualizar(movimentacao)

    suspend fun excluir(movimentacao: Movimentacao) =
            movimentacaoDao.excluir(movimentacao)
}