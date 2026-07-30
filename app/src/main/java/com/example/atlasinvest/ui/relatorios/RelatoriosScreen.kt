@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.atlasinvest.ui.relatorios

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.atlasinvest.AtlasInvestApplication
import com.example.atlasinvest.controller.FabricaViewModel
import com.example.atlasinvest.controller.RelatorioViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun RelatoriosScreen(app: AtlasInvestApplication, usuarioId: Long) {
    val viewModel: RelatorioViewModel = viewModel(factory = FabricaViewModel(app, usuarioId))
    val resumo by viewModel.resumo.collectAsState()
    val formatoMoeda = remember { NumberFormat.getCurrencyInstance(Locale("pt", "BR")) }

    val maiorGasto = resumo.gastosPorCategoria.values.maxOrNull() ?: 0.0

    Scaffold(topBar = { TopAppBar(title = { Text("Relatórios") }) }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("Resumo geral", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Text("Receitas: ${formatoMoeda.format(resumo.totalReceitas)}")
            Text("Despesas: ${formatoMoeda.format(resumo.totalDespesas)}")
            Text("Saldo: ${formatoMoeda.format(resumo.totalReceitas - resumo.totalDespesas)}")

            Spacer(Modifier.height(24.dp))
            Text("Despesas por categoria", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(12.dp))

            LazyColumn {
                items(resumo.gastosPorCategoria.entries.toList()) { (categoria, total) ->
                    val fracao = if (maiorGasto > 0) (total / maiorGasto).toFloat() else 0f

                    Column(modifier = Modifier.padding(vertical = 6.dp)) {
                        Text("${categoria.nome} — ${formatoMoeda.format(total)}")
                        Spacer(Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fracao.coerceIn(0.02f, 1f))
                                .height(10.dp)
                                .clip(MaterialTheme.shapes.small)
                                .background(Color(android.graphics.Color.parseColor(categoria.cor)))
                        )
                    }
                }
            }
        }
    }
}