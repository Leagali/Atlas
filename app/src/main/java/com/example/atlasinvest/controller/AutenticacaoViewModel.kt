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
        if (!validarEmail(email)) {
            _estado.value = EstadoAutenticacao.Erro("Por favor, insira um e-mail válido.")
            return
        }

        if (telefone != null && !validarTelefone(telefone)) {
            _estado.value = EstadoAutenticacao.Erro("O telefone deve conter 10 ou 11 dígitos numéricos.")
            return
        }

        if (!validarSenha(senha)) {
            _estado.value = EstadoAutenticacao.Erro("A senha deve ter pelo menos 8 caracteres, uma letra maiúscula, um número e um caractere especial.")
            return
        }

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

    private fun validarEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun validarTelefone(telefone: String): Boolean {
        val apenasDigitos = telefone.filter { it.isDigit() }
        return apenasDigitos.length in 10..11
    }

    private fun validarSenha(senha: String): Boolean {
        // Pelo menos 8 caracteres, 1 maiúscula, 1 número, 1 caractere especial
        val regex = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$".toRegex()
        return regex.matches(senha)
    }

    fun resetarEstado() {
        _estado.value = EstadoAutenticacao.Ocioso
    }
}