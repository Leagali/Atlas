@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.atlasinvest.ui.carteira

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.atlasinvest.AtlasInvestApplication
import com.example.atlasinvest.controller.CarteiraViewModel
import com.example.atlasinvest.controller.FabricaViewModel
import com.example.atlasinvest.data.local.entity.Ativo
import com.example.atlasinvest.data.local.entity.Cotacao
import com.example.atlasinvest.data.local.entity.TipoAtivo
import com.example.atlasinvest.ui.theme.VerdeReceita
import com.example.atlasinvest.ui.theme.VermelhoDespesa
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CarteiraScreen(app: AtlasInvestApplication, usuarioId: Long) {
    val viewModel: CarteiraViewModel = viewModel(factory = FabricaViewModel(app, usuarioId))
    val ativos by viewModel.ativos.collectAsState()
    val cotacoes by viewModel.cotacoes.collectAsState()
    val formatoMoeda = remember { NumberFormat.getCurrencyInstance(Locale("pt", "BR")) }
    var mostrarDialogo by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Carteira de investimentos") },
                actions = {
                    TextButton(onClick = { viewModel.atualizarCotacoes() }) { Text("Atualizar") }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarDialogo = true }) {
                Icon(Icons.Default.Add, contentDescription = "Novo ativo")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize()) {
            items(ativos) { ativo ->
                val cotacao = cotacoes.find { it.ticker == ativo.ticker }
                CartaoAtivo(ativo, cotacao, formatoMoeda)
                HorizontalDivider()
            }
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
private fun CartaoAtivo(ativo: Ativo, cotacao: Cotacao?, formatoMoeda: NumberFormat) {
    ListItem(
        headlineContent = { Text("${ativo.ticker} — ${ativo.nome}") },
        supportingContent = {
            Text("Qtd: ${ativo.quantidade} · Preço médio: ${formatoMoeda.format(ativo.precoCompra)}")
        },
        trailingContent = {
            if (cotacao != null) {
                val valorizacao = cotacao.precoAtual - ativo.precoCompra
                val cor = if (valorizacao >= 0) VerdeReceita else VermelhoDespesa
                Column(horizontalAlignment = Alignment.End) {
                    Text(formatoMoeda.format(cotacao.precoAtual), fontWeight = FontWeight.Bold)
                    Text("${"%.2f".format(cotacao.variacao)}%", color = cor)
                }
            } else {
                Text("—")
            }
        }
    )
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