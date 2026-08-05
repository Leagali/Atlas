@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.atlasinvest.ui.carteira

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.atlasinvest.controller.CarteiraViewModel
import com.example.atlasinvest.controller.FabricaViewModel
import com.example.atlasinvest.data.local.entity.Ativo
import com.example.atlasinvest.data.local.entity.Cotacao
import com.example.atlasinvest.data.local.entity.TipoAtivo
import com.example.atlasinvest.ui.navigation.Destino
import com.example.atlasinvest.ui.theme.BarraInferiorAtlas
import com.example.atlasinvest.ui.theme.CabecalhoAtlas
import com.example.atlasinvest.ui.theme.FundoTela
import com.example.atlasinvest.ui.theme.VerdeReceita
import com.example.atlasinvest.ui.theme.VermelhoDespesa
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CarteiraScreen(app: AtlasInvestApplication, usuarioId: Long, navController: NavHostController) {
    val viewModel: CarteiraViewModel = viewModel(factory = FabricaViewModel(app, usuarioId))
    val ativos by viewModel.ativos.collectAsState()
    val cotacoes by viewModel.cotacoes.collectAsState()
    val formatoMoeda = remember { NumberFormat.getCurrencyInstance(Locale("pt", "BR")) }

    var filtroTipo by remember { mutableStateOf<TipoAtivo?>(null) }
    var mostrarDialogo by remember { mutableStateOf(false) }

    val ativosFiltrados = if (filtroTipo == null) ativos else ativos.filter { it.tipo == filtroTipo }
    val totalCarteira = ativos.sumOf { it.quantidade * it.precoCompra }

    Scaffold(
        containerColor = FundoTela,
        topBar = {
            CabecalhoAtlas(
                nomeUsuario = "Luiz",
                iniciais = "LL",
                aoLogout = {
                    app.sessionManager.encerrarSessao()
                    navController.navigate(Destino.Login.rota) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        },
        bottomBar = {
            BarraInferiorAtlas(
                aoClicarInicio = { navController.navigate("${Destino.Dashboard.rota}/$usuarioId") },
                aoClicarMovimentos = { navController.navigate("${Destino.Movimentacoes.rota}/$usuarioId") },
                aoClicarCarteira = {},
                aoClicarRelatorios = { navController.navigate("${Destino.Relatorios.rota}/$usuarioId") },
                aoClicarMetas = { navController.navigate("${Destino.Metas.rota}/$usuarioId") },
                rotaAtual = "carteira"
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            Spacer(Modifier.height(8.dp))

            // Placeholder de gráfico — ver observação abaixo do código
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Carteira total",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            formatoMoeda.format(totalCarteira),
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = { mostrarDialogo = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.height(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Adicionar ou remover investimento")
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FiltroChip("Todos", filtroTipo == null) { filtroTipo = null }
                FiltroChip("FIIs", filtroTipo == TipoAtivo.FII) { filtroTipo = TipoAtivo.FII }
                FiltroChip("Renda Fixa", filtroTipo == TipoAtivo.RENDA_FIXA) { filtroTipo = TipoAtivo.RENDA_FIXA }
                FiltroChip("Ações", filtroTipo == TipoAtivo.ACAO) { filtroTipo = TipoAtivo.ACAO }
            }

            Spacer(Modifier.height(12.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text("Ativo", modifier = Modifier.weight(1.3f), fontSize = 12.sp, color = Color.Gray)
                        Text("Qtd.", modifier = Modifier.weight(0.8f), fontSize = 12.sp, color = Color.Gray)
                        Text("Preço", modifier = Modifier.weight(1f), fontSize = 12.sp, color = Color.Gray)
                        Text("Var.", modifier = Modifier.weight(0.8f), fontSize = 12.sp, color = Color.Gray)
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))

                    LazyColumn(modifier = Modifier.height(260.dp)) {
                        items(ativosFiltrados) { ativo ->
                            val cotacao = cotacoes.find { it.ticker == ativo.ticker }
                            LinhaAtivo(ativo, cotacao, formatoMoeda)
                            HorizontalDivider()
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }

    if (mostrarDialogo) {
        DialogoNovoAtivo(
            aoConfirmar = { nome, ticker, tipo, quantidade, precoCompra ->
                viewModel.cadastrarAtivo(nome, ticker, tipo, quantidade, precoCompra)
                mostrarDialogo = false
            },
            aoCancelar = { mostrarDialogo = false }
        )
    }
}

@Composable
private fun FiltroChip(texto: String, selecionado: Boolean, aoClicar: () -> Unit) {
    val corFundo = if (selecionado) Color(0xFF3A3A3A) else Color(0xFFE4E4E4)
    val corTexto = if (selecionado) Color.White else Color.Black
    androidx.compose.material3.Surface(
        shape = RoundedCornerShape(50),
        color = corFundo,
        modifier = Modifier
            .height(32.dp)
            .clickable { aoClicar() }
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(texto, color = corTexto, fontSize = 12.sp)
        }
    }
}

@Composable
private fun LinhaAtivo(ativo: Ativo, cotacao: Cotacao?, formatoMoeda: NumberFormat) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(ativo.ticker, modifier = Modifier.weight(1.3f), fontSize = 13.sp, fontWeight = FontWeight.Medium)
        Text("${ativo.quantidade.toInt()}", modifier = Modifier.weight(0.8f), fontSize = 13.sp)
        Text(
            cotacao?.let { formatoMoeda.format(it.precoAtual) } ?: "—",
            modifier = Modifier.weight(1f),
            fontSize = 13.sp
        )
        val variacao = cotacao?.variacao ?: 0.0
        val cor = if (variacao >= 0) VerdeReceita else VermelhoDespesa
        Text(
            "${"%.1f".format(variacao)}%",
            modifier = Modifier.weight(0.8f),
            fontSize = 13.sp,
            color = cor,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun DialogoNovoAtivo(
    aoConfirmar: (nome: String, ticker: String, tipo: TipoAtivo, quantidade: Double, precoCompra: Double) -> Unit,
    aoCancelar: () -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var ticker by remember { mutableStateOf("") }
    var tipoSelecionado by remember { mutableStateOf(TipoAtivo.ACAO) }
    var quantidadeTexto by remember { mutableStateOf("") }
    var precoTexto by remember { mutableStateOf("") }
    var expandido by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = aoCancelar,
        title = { Text("Novo ativo") },
        text = {
            Column {
                OutlinedTextField(
                    value = ticker,
                    onValueChange = { ticker = it.uppercase() },
                    label = { Text("Ticker (ex: PETR4)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome do ativo") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))

                Box {
                    OutlinedTextField(
                        value = tipoSelecionado.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            TextButton(onClick = { expandido = true }) { Text("▾") }
                        }
                    )
                    DropdownMenu(expanded = expandido, onDismissRequest = { expandido = false }) {
                        TipoAtivo.values().forEach { tipo ->
                            DropdownMenuItem(
                                text = { Text(tipo.name) },
                                onClick = {
                                    tipoSelecionado = tipo
                                    expandido = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = quantidadeTexto,
                    onValueChange = { quantidadeTexto = it },
                    label = { Text("Quantidade") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = precoTexto,
                    onValueChange = { precoTexto = it },
                    label = { Text("Preço médio de compra (R$)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val quantidade = quantidadeTexto.replace(",", ".").toDoubleOrNull()
                val preco = precoTexto.replace(",", ".").toDoubleOrNull()
                if (ticker.isNotBlank() && nome.isNotBlank() && quantidade != null && quantidade > 0 && preco != null && preco > 0) {
                    aoConfirmar(nome, ticker, tipoSelecionado, quantidade, preco)
                }
            }) { Text("Adicionar") }
        },
        dismissButton = { TextButton(onClick = aoCancelar) { Text("Cancelar") } }
    )
}