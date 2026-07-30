@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.atlasinvest.ui.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.atlasinvest.AtlasInvestApplication
import com.example.atlasinvest.controller.FabricaViewModel
import com.example.atlasinvest.controller.MovimentacaoViewModel
import com.example.atlasinvest.ui.movimentacoes.MovimentacaoItem
import com.example.atlasinvest.ui.navigation.Destino
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DashboardScreen(
    app: AtlasInvestApplication,
    usuarioId: Long,
    navController: NavHostController
) {
    val viewModel: MovimentacaoViewModel = viewModel(factory = FabricaViewModel(app, usuarioId))
    val saldo by viewModel.saldo.collectAsState()
    val movimentacoes by viewModel.movimentacoes.collectAsState()
    val formatoMoeda = remember { NumberFormat.getCurrencyInstance(Locale("pt", "BR")) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Icon(Icons.Default.Home, contentDescription = "Início") },
                    label = { Text("Início") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("${Destino.Movimentacoes.rota}/$usuarioId") },
                    icon = { Icon(Icons.Default.List, contentDescription = "Movimentações") },
                    label = { Text("Movimentos") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("${Destino.Metas.rota}/$usuarioId") },
                    icon = { Icon(Icons.Default.Flag, contentDescription = "Metas") },
                    label = { Text("Metas") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("${Destino.Carteira.rota}/$usuarioId") },
                    icon = { Icon(Icons.Default.AccountBalance, contentDescription = "Carteira") },
                    label = { Text("Carteira") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("${Destino.Relatorios.rota}/$usuarioId") },
                    icon = { Icon(Icons.Default.PieChart, contentDescription = "Relatórios") },
                    label = { Text("Relatórios") }
                )
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("Saldo atual", style = MaterialTheme.typography.labelSmall)
            Text(formatoMoeda.format(saldo), style = MaterialTheme.typography.titleLarge)

            Spacer(Modifier.height(24.dp))
            Text("Últimas movimentações", style = MaterialTheme.typography.labelSmall)
            Spacer(Modifier.height(8.dp))

            LazyColumn {
                items(movimentacoes.take(5)) { mov ->
                    MovimentacaoItem(mov, formatoMoeda)
                }
            }
        }
    }
}