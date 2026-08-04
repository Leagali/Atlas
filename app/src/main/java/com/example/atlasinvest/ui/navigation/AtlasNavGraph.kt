package com.example.atlasinvest.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.atlasinvest.AtlasInvestApplication
import com.example.atlasinvest.ui.carteira.CarteiraScreen
import com.example.atlasinvest.ui.dashboard.DashboardScreen
import com.example.atlasinvest.ui.login.CadastroScreen
import com.example.atlasinvest.ui.login.LoginScreen
import com.example.atlasinvest.ui.metas.MetasScreen
import com.example.atlasinvest.ui.movimentacoes.MovimentacoesScreen
import com.example.atlasinvest.ui.relatorios.RelatoriosScreen

@Composable
fun AtlasNavGraph(
    app: AtlasInvestApplication,
    navController: NavHostController = rememberNavController(),
    usuarioIdInicial: Long
) {
    val destinoInicial = if (usuarioIdInicial > 0) {
        "${Destino.Dashboard.rota}/$usuarioIdInicial"
    } else {
        Destino.Login.rota
    }

    NavHost(navController = navController, startDestination = destinoInicial) {

        composable(Destino.Login.rota) {
            LoginScreen(
                app = app,
                aoLogar = { usuarioId ->
                    navController.navigate("${Destino.Dashboard.rota}/$usuarioId") {
                        popUpTo(Destino.Login.rota) { inclusive = true }
                    }
                },
                aoNavegarParaCadastro = { navController.navigate(Destino.Cadastro.rota) }
            )
        }

        composable(Destino.Cadastro.rota) {
            CadastroScreen(
                app = app,
                aoCadastrar = { usuarioId ->
                    navController.navigate("${Destino.Dashboard.rota}/$usuarioId") {
                        popUpTo(Destino.Login.rota) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = "${Destino.Dashboard.rota}/{usuarioId}",
            arguments = listOf(navArgument("usuarioId") { type = NavType.LongType })
        ) { backStackEntry ->
            val usuarioId = backStackEntry.arguments?.getLong("usuarioId") ?: usuarioIdInicial
            DashboardScreen(app = app, usuarioId = usuarioId, navController = navController)
        }

        composable(
            route = "${Destino.Movimentacoes.rota}/{usuarioId}",
            arguments = listOf(navArgument("usuarioId") { type = NavType.LongType })
        ) { backStackEntry ->
            val usuarioId = backStackEntry.arguments?.getLong("usuarioId") ?: usuarioIdInicial
            MovimentacoesScreen(app = app, usuarioId = usuarioId, navController = navController)
        }

        composable(
            route = "${Destino.Metas.rota}/{usuarioId}",
            arguments = listOf(navArgument("usuarioId") { type = NavType.LongType })
        ) { backStackEntry ->
            val usuarioId = backStackEntry.arguments?.getLong("usuarioId") ?: usuarioIdInicial
            MetasScreen(app = app, usuarioId = usuarioId, navController = navController)
        }

        composable(
            route = "${Destino.Carteira.rota}/{usuarioId}",
            arguments = listOf(navArgument("usuarioId") { type = NavType.LongType })
        ) { backStackEntry ->
            val usuarioId = backStackEntry.arguments?.getLong("usuarioId") ?: usuarioIdInicial
            CarteiraScreen(app = app, usuarioId = usuarioId, navController = navController)
        }

        composable(
            route = "${Destino.Relatorios.rota}/{usuarioId}",
            arguments = listOf(navArgument("usuarioId") { type = NavType.LongType })
        ) { backStackEntry ->
            val usuarioId = backStackEntry.arguments?.getLong("usuarioId") ?: usuarioIdInicial
            RelatoriosScreen(app = app, usuarioId = usuarioId, navController = navController)
        }
    }
}