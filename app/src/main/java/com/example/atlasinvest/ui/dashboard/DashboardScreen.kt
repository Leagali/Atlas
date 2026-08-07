@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.atlasinvest.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.atlasinvest.AtlasInvestApplication
import com.example.atlasinvest.controller.FabricaViewModel
import com.example.atlasinvest.controller.MetaViewModel
import com.example.atlasinvest.controller.MovimentacaoViewModel
import com.example.atlasinvest.data.local.entity.Meta
import com.example.atlasinvest.ui.navigation.Destino
import com.example.atlasinvest.ui.theme.BarraInferiorAtlas
import com.example.atlasinvest.ui.theme.CabecalhoAtlas
import com.example.atlasinvest.ui.theme.FundoTela
import com.example.atlasinvest.ui.theme.VerdeReceita
import com.example.atlasinvest.ui.theme.VermelhoDespesa
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    app: AtlasInvestApplication,
    usuarioId: Long,
    navController: NavHostController
) {
    val viewModel: MovimentacaoViewModel = viewModel(factory = FabricaViewModel(app, usuarioId))
    val metaViewModel: MetaViewModel = viewModel(factory = FabricaViewModel(app, usuarioId))

    val usuario by viewModel.usuario.collectAsState()
    val saldo by viewModel.saldo.collectAsState()
    val movimentacoes by viewModel.movimentacoes.collectAsState()
    val metas by metaViewModel.metas.collectAsState()

    val formatoMoeda = remember { NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")) }

    val totalReceitas = movimentacoes
        .filter { it.tipo == com.example.atlasinvest.data.local.entity.TipoMovimentacao.RECEITA }
        .sumOf { it.valor }
    val totalDespesas = movimentacoes
        .filter { it.tipo == com.example.atlasinvest.data.local.entity.TipoMovimentacao.DESPESA }
        .sumOf { it.valor }

    var valoresVisiveis by remember { mutableStateOf(value = true) }

    Scaffold(
        containerColor = FundoTela,
        topBar = {
            CabecalhoAtlas(
                nomeUsuario = usuario?.nome ?: "Usuário",
                iniciais = usuario?.nome?.take(2)?.uppercase() ?: "US",
                aoLogout = {
                    app.sessionManager.encerrarSessao()
                    navController.navigate(Destino.Login.rota) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                acaoExtra = {
                    IconButton(onClick = { valoresVisiveis = !valoresVisiveis }) {
                        Icon(
                            if (valoresVisiveis) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Ocultar valores"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BarraInferiorAtlas(
                aoClicarInicio = {},
                aoClicarMovimentos = { navController.navigate("${Destino.Movimentacoes.rota}/$usuarioId") },
                aoClicarCarteira = { navController.navigate("${Destino.Carteira.rota}/$usuarioId") },
                aoClicarRelatorios = { navController.navigate("${Destino.Relatorios.rota}/$usuarioId") },
                aoClicarMetas = { navController.navigate("${Destino.Metas.rota}/$usuarioId") },
                rotaAtual = "dashboard"
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            CartaoSaldo(saldo, formatoMoeda, valoresVisiveis)

            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CartaoIndicador(
                    titulo = "Receita",
                    valor = totalReceitas,
                    formatoMoeda = formatoMoeda,
                    visivel = valoresVisiveis,
                    modifier = Modifier.weight(1f)
                )
                CartaoIndicador(
                    titulo = "Despesas",
                    valor = totalDespesas,
                    formatoMoeda = formatoMoeda,
                    visivel = valoresVisiveis,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(12.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Metas", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(Modifier.height(10.dp))

                    if (metas.isEmpty()) {
                        Text("Nenhuma meta cadastrada ainda.", color = Color.Gray, fontSize = 13.sp)
                    } else {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text("Meta", modifier = Modifier.weight(1.2f), fontSize = 12.sp, color = Color.Gray)
                            Text("Acumulado", modifier = Modifier.weight(1f), fontSize = 12.sp, color = Color.Gray)
                            Text("Alvo", modifier = Modifier.weight(1f), fontSize = 12.sp, color = Color.Gray)
                        }
                        Spacer(Modifier.height(6.dp))

                        LazyColumn(modifier = Modifier.height(180.dp)) {
                            items(metas) { meta ->
                                LinhaMeta(meta, formatoMoeda)
                                Spacer(Modifier.height(10.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CartaoSaldo(saldo: Double, formatoMoeda: NumberFormat, visivel: Boolean) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = RoundedCornerShape(50),
                color = Color(0xFFEDEDED)
            ) {
                Text(
                    "Saldo",
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 2.dp),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = if (visivel) formatoMoeda.format(saldo) else "R$ ***.***,**",
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            )
        }
    }
}

@Composable
private fun CartaoIndicador(
    titulo: String,
    valor: Double,
    formatoMoeda: NumberFormat,
    visivel: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(shape = RoundedCornerShape(50), color = Color(0xFFEDEDED)) {
                Text(
                    titulo,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = if (visivel) formatoMoeda.format(valor) else "***.***,**",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
    }
}

@Composable
private fun LinhaMeta(meta: Meta, formatoMoeda: NumberFormat) {
    val progresso = if (meta.valorAlvo > 0) {
        (meta.valorAcumulado / meta.valorAlvo).toFloat().coerceIn(0f, 1f)
    } else 0f

    val corProgresso = when {
        progresso >= 1f -> VerdeReceita
        progresso >= 0.5f -> Color(0xFFF2A900)
        else -> Color(0xFF8C6D1F)
    }

    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Surface(
            shape = RoundedCornerShape(50),
            color = corProgresso,
            modifier = Modifier.padding(end = 6.dp)
        ) {
            Text(
                "${(progresso * 100).toInt()}%",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
        Text(meta.nome, modifier = Modifier.weight(1.2f), fontSize = 13.sp)
        Text(formatoMoeda.format(meta.valorAcumulado), modifier = Modifier.weight(1f), fontSize = 12.sp)
        Text(formatoMoeda.format(meta.valorAlvo), modifier = Modifier.weight(1f), fontSize = 12.sp)
    }
}