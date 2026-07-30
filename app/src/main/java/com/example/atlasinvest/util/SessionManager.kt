package com.example.atlasinvest.util

import android.content.Context

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("atlas_invest_session", Context.MODE_PRIVATE)

    var usuarioLogadoId: Long
    get() = prefs.getLong(CHAVE_USUARIO_ID, -1L)
    set(value) = prefs.edit().putLong(CHAVE_USUARIO_ID, value).apply()

    fun estaLogado(): Boolean = usuarioLogadoId != -1L

    fun encerrarSessao() {
        prefs.edit().remove(CHAVE_USUARIO_ID).apply()
    }

    companion object {
        private const val CHAVE_USUARIO_ID = "usuario_id"
    }
}