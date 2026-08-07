package com.example.atlasinvest

import android.app.Application
import com.example.atlasinvest.data.local.AtlasInvestDatabase
import com.example.atlasinvest.data.remote.NetworkModule
import com.example.atlasinvest.data.repository.AtivoRepository
import com.example.atlasinvest.data.repository.CategoriaRepository
import com.example.atlasinvest.data.repository.MetaRepository
import com.example.atlasinvest.data.repository.MovimentacaoRepository
import com.example.atlasinvest.data.repository.UsuarioRepository
import com.example.atlasinvest.util.SessionManager

class AtlasInvestApplication : Application() {

    val database: AtlasInvestDatabase by lazy { AtlasInvestDatabase.getInstance(this) }
    val sessionManager: SessionManager by lazy { SessionManager(this) }

    val usuarioRepository: UsuarioRepository by lazy { UsuarioRepository(database.usuarioDao()) }
    val categoriaRepository: CategoriaRepository by lazy { CategoriaRepository(database.categoriaDao()) }
    val movimentacaoRepository: MovimentacaoRepository by lazy { MovimentacaoRepository(database.movimentacaoDao()) }
    val metaRepository: MetaRepository by lazy { MetaRepository(database.metaDao()) }

    val ativoRepository: AtivoRepository by lazy {
        AtivoRepository(
            database.ativoDao(),
            database.cotacaoDao(),
            NetworkModule.cotacaoApiService,
            NetworkModule.criptoApiService,
        )
    }
}