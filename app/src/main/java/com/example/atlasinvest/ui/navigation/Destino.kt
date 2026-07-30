package com.example.atlasinvest.ui.navigation

sealed class Destino(val rota: String) {
    data object Login : Destino("login")
    data object Cadastro : Destino("cadastro")
    data object Dashboard : Destino("dashboard")
    data object Movimentacoes : Destino("movimentacoes")
    data object Metas : Destino("metas")
    data object Carteira : Destino("carteira")
    data object Relatorios : Destino("relatorios")
}