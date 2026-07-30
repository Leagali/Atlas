package com.example.atlasinvest.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.atlasinvest.data.repository.CategoriaRepository
import com.example.atlasinvest.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Equivale ao "Controle de Autenticação" da Figura 10 do TCC.
 */
sealed interface EstadoAutenticacao {
    data object Ocioso : EstadoAutenticacao
    data object Carregando : EstadoAutenticacao
    data class Sucesso(val usuarioId: Long) : EstadoAutenticacao
    data class Erro(val mensagem: String) : EstadoAutenticacao
}

        class AutenticacaoViewModel(
                private val usuarioRepository: UsuarioRepository,
                private val categoriaRepository: CategoriaRepository
        ) : ViewModel() {

    private val _estado = MutableStateFlow<EstadoAutenticacao>(EstadoAutenticacao.Ocioso)
            val estado: StateFlow<EstadoAutenticacao> = _estado.asStateFlow()

    fun login(email: String, senha: String) {
        viewModelScope.launch {
            _estado.value = EstadoAutenticacao.Carregando
            when (val resultado = usuarioRepository.login(email, senha)) {
                is UsuarioRepository.ResultadoAuth.Sucesso ->
                _estado.value = EstadoAutenticacao.Sucesso(resultado.usuario.id)
                is UsuarioRepository.ResultadoAuth.Erro ->
                _estado.value = EstadoAutenticacao.Erro(resultado.mensagem)
            }
        }
    }

    fun cadastrar(nome: String, email: String, senha: String, telefone: String?) {
        viewModelScope.launch {
            _estado.value = EstadoAutenticacao.Carregando
            when (val resultado = usuarioRepository.cadastrar(nome, email, senha, telefone)) {
                is UsuarioRepository.ResultadoAuth.Sucesso -> {
                    categoriaRepository.criarCategoriasPadrao(resultado.usuario.id)
                    _estado.value = EstadoAutenticacao.Sucesso(resultado.usuario.id)
                }
                is UsuarioRepository.ResultadoAuth.Erro ->
                _estado.value = EstadoAutenticacao.Erro(resultado.mensagem)
            }
        }
    }

    fun resetarEstado() {
        _estado.value = EstadoAutenticacao.Ocioso
    }
}