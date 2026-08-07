package com.example.atlasinvest.data.repository

import com.example.atlasinvest.data.local.dao.UsuarioDao
import com.example.atlasinvest.data.local.entity.Usuario
import com.example.atlasinvest.util.PasswordHasher
import kotlinx.coroutines.flow.Flow

class UsuarioRepository(private val usuarioDao: UsuarioDao) {

    sealed class ResultadoAuth {
        data class Sucesso(val usuario: Usuario) : ResultadoAuth()
        data class Erro(val mensagem: String) : ResultadoAuth()
    }

    suspend fun cadastrar(nome: String, email: String, senha: String, telefone: String?): ResultadoAuth {
        if (usuarioDao.buscarPorEmail(email) != null) {
            return ResultadoAuth.Erro("Já existe uma conta cadastrada com este e-mail.")
        }
        val salt = PasswordHasher.gerarSalt()
        val hash = PasswordHasher.gerarHash(senha, salt)
        val usuario = Usuario(
                nome = nome,
                email = email,
                senhaHash = hash,
                senhaSalt = salt,
                telefone = telefone
        )
        val id = usuarioDao.inserir(usuario)
        return ResultadoAuth.Sucesso(usuario.copy(id = id))
    }

    suspend fun login(email: String, senha: String): ResultadoAuth {
        val usuario = usuarioDao.buscarPorEmail(email)
                ?: return ResultadoAuth.Erro("E-mail ou senha inválidos.")
        val hashInformado = PasswordHasher.gerarHash(senha, usuario.senhaSalt)
        return if (hashInformado == usuario.senhaHash) {
            ResultadoAuth.Sucesso(usuario)
        } else {
            ResultadoAuth.Erro("E-mail ou senha inválidos.")
        }
    }

    fun observarUsuario(id: Long): Flow<Usuario?> = usuarioDao.observarPorId(id)
}