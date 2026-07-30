package com.example.atlasinvest.data.repository

import com.example.atlasinvest.data.local.dao.CategoriaDao
import com.example.atlasinvest.data.local.entity.Categoria
import com.example.atlasinvest.data.local.entity.TipoCategoria
import kotlinx.coroutines.flow.Flow

class CategoriaRepository(private val categoriaDao: CategoriaDao) {

    fun observarCategorias(usuarioId: Long): Flow<List<Categoria>> =
            categoriaDao.observarPorUsuario(usuarioId)

    suspend fun criar(categoria: Categoria): Long = categoriaDao.inserir(categoria)

    suspend fun excluir(categoria: Categoria) = categoriaDao.excluir(categoria)

    // RF12: categorização personalizada — cria um conjunto inicial de categorias
    // para que o usuário não comece com a lista vazia.
    suspend fun criarCategoriasPadrao(usuarioId: Long) {
        if (categoriaDao.contarPorUsuario(usuarioId) > 0) return
                val padrao = listOf(
                Categoria(usuarioId = usuarioId, nome = "Salário", cor = "#2F9E44", tipo = TipoCategoria.RECEITA),
                Categoria(usuarioId = usuarioId, nome = "Alimentação", cor = "#E8590C", tipo = TipoCategoria.DESPESA),
                Categoria(usuarioId = usuarioId, nome = "Moradia", cor = "#1971C2", tipo = TipoCategoria.DESPESA),
                Categoria(usuarioId = usuarioId, nome = "Transporte", cor = "#F08C00", tipo = TipoCategoria.DESPESA),
                Categoria(usuarioId = usuarioId, nome = "Lazer", cor = "#9C36B5", tipo = TipoCategoria.DESPESA),
                Categoria(usuarioId = usuarioId, nome = "Investimentos", cor = "#0CA678", tipo = TipoCategoria.AMBOS),
                Categoria(usuarioId = usuarioId, nome = "Outros", cor = "#868E96", tipo = TipoCategoria.AMBOS)
        )
        padrao.forEach { criar(it) }
    }
}