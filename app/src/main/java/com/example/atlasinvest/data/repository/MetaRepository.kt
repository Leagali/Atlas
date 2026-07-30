package com.example.atlasinvest.data.repository

import com.example.atlasinvest.data.local.dao.MetaDao
import com.example.atlasinvest.data.local.entity.Meta
import com.example.atlasinvest.data.local.entity.StatusMeta
import kotlinx.coroutines.flow.Flow

class MetaRepository(private val metaDao: MetaDao) {

    fun observarMetas(usuarioId: Long): Flow<List<Meta>> = metaDao.observarPorUsuario(usuarioId)

    suspend fun criar(meta: Meta): Long = metaDao.inserir(meta)

    suspend fun excluir(meta: Meta) = metaDao.excluir(meta)

    // RN05: progresso da meta atualizado automaticamente conforme novas movimentações
    suspend fun atualizarProgresso(metaId: Long, valorAdicional: Double) {
        val meta = metaDao.buscarPorId(metaId) ?: return
                val novoValor = meta.valorAcumulado + valorAdicional
        val novoStatus = if (novoValor >= meta.valorAlvo) StatusMeta.CONCLUIDA else meta.status
        metaDao.atualizar(meta.copy(valorAcumulado = novoValor, status = novoStatus))
    }
}