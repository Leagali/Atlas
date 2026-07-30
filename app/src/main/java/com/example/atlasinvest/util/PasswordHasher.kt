package com.example.atlasinvest.util

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Implementa hash seguro de senhas (RNF03), usando PBKDF2WithHmacSHA256,
 * que já vem nativo no Android — não precisa de biblioteca externa.
 */
object PasswordHasher {
    private const val ITERACOES = 120_000
    private const val TAMANHO_CHAVE = 256

    fun gerarSalt(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    fun gerarHash(senha: String, saltBase64: String): String {
        val salt = Base64.decode(saltBase64, Base64.NO_WRAP)
        val spec = PBEKeySpec(senha.toCharArray(), salt, ITERACOES, TAMANHO_CHAVE)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val hash = factory.generateSecret(spec).encoded
        return Base64.encodeToString(hash, Base64.NO_WRAP)
    }
}