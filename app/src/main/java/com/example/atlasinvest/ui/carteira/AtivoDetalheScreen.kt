@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.atlasinvest.ui.carteira

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.atlasinvest.AtlasInvestApplication
import com.example.atlasinvest.controller.AtivoDetalheViewModel
import com.example.atlasinvest.controller.FabricaViewModel
import com.example.atlasinvest.data.repository.PontoHistorico
import com.example.atlasinvest.ui.theme.VerdeReceita
import com.example.atlasinvest.ui.theme.VermelhoDespesa
import java.text.NumberFormat
import java.util.Locale

@Composable
fun AtivoDetalheScreen(
    app: AtlasInvestApplication,
    usuarioId: Long,
    ativoId: Long,
    navController: NavHostController,
) {
    val viewModel: AtivoDetalheViewModel = viewModel(factory = FabricaViewModel(app, usuarioId, ativoId))
    val estado by viewModel.estado.collectAsState()
    val formatoMoeda = remember { NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(estado.ativo?.ticker ?: "Ativo") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        if (estado.carregando) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
            return@Scaffold
        }

        val ativo = estado.ativo
        if (ativo == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { Text("Ativo não encontrado.") }
            return@Scaffold
        }

        val precoAtual = estado.cotacaoAtual?.precoAtual ?: ativo.precoCompra
        val valorPosicaoAtual = ativo.quantidade * precoAtual
        val valorInvestido = ativo.quantidade * ativo.precoCompra
        val lucro = valorPosicaoAtual - valorInvestido
        val corLucro = if (lucro >= 0) VerdeReceita else VermelhoDespesa

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text(ativo.nome, fontSize = 14.sp, color = Color.Gray)
            Text(formatoMoeda.format(precoAtual), fontWeight = FontWeight.Bold, fontSize = 26.sp)
            estado.cotacaoAtual?.let { cotacao ->
                Text(
                    "${"%.2f".format(cotacao.variacao)}% hoje",
                    color = if (cotacao.variacao >= 0) VerdeReceita else VermelhoDespesa,
                    fontSize = 13.sp
                )
            }

            Spacer(Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Evolução (últimos 6 meses)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(Modifier.height(8.dp))
                    GraficoLinha(estado.historico, corLucro)
                }
            }

            Spacer(Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Sua posição", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(Modifier.height(10.dp))

                    LinhaInfo("Quantidade", ativo.quantidade.toString())
                    LinhaInfo("Preço médio de compra", formatoMoeda.format(ativo.precoCompra))
                    LinhaInfo("Valor investido", formatoMoeda.format(valorInvestido))
                    LinhaInfo("Valor atual da posição", formatoMoeda.format(valorPosicaoAtual))

                    Spacer(Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text("Resultado", modifier = Modifier.weight(1f), fontSize = 13.sp)
                        Text(
                            (if (lucro >= 0) "+" else "") + formatoMoeda.format(lucro),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = corLucro
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LinhaInfo(rotulo: String, valor: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
        Text(rotulo, modifier = Modifier.weight(1f), fontSize = 13.sp, color = Color.Gray)
        Text(valor, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun GraficoLinha(pontos: List<PontoHistorico>, corLinha: Color) {
    if (pontos.size < 2) {
        Box(
            modifier = Modifier.fillMaxWidth().height(180.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Sem dados históricos suficientes para exibir o gráfico.", fontSize = 12.sp, color = Color.Gray)
        }
        return
    }

    val valores = pontos.map { it.fechamento }
    val minValor = valores.min()
    val maxValor = valores.max()
    val amplitude = (maxValor - minValor).let { if (it > 0) it else 1.0 }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
    ) {
        val larguraPasso = size.width / (pontos.size - 1)
        val path = Path()
        pontos.forEachIndexed { index, ponto ->
            val x = index * larguraPasso
            val fracao = ((ponto.fechamento - minValor) / amplitude).toFloat()
            val y = size.height - (fracao * size.height)
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(path = path, color = corLinha, style = Stroke(width = 5f))
    }
}