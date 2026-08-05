@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.atlasinvest.ui.movimentacoes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.atlasinvest.AtlasInvestApplication
import com.example.atlasinvest.controller.CategoriaViewModel
import com.example.atlasinvest.controller.FabricaViewModel
import com.example.atlasinvest.controller.MovimentacaoViewModel
import com.example.atlasinvest.data.local.entity.Categoria
import com.example.atlasinvest.data.local.entity.TipoMovimentacao
import com.example.atlasinvest.ui.navigation.Destino
import com.example.atlasinvest.ui.theme.BarraInferiorAtlas
import com.example.atlasinvest.ui.theme.CabecalhoAtlas
import com.example.atlasinvest.ui.theme.FundoTela
import java.text.NumberFormat
import java.util.Locale

@Composable
fun MovimentacoesScreen(app: AtlasInvestApplication, usuarioId: Long, navController: NavHostController) {
    val viewModel: MovimentacaoViewModel = viewModel(factory = FabricaViewModel(app, usuarioId))
    val categoriaViewModel: CategoriaViewModel = viewModel(factory = FabricaViewModel(app, usuarioId))

    val movimentacoes by viewModel.movimentacoes.collectAsState()
    val categorias by categoriaViewModel.categorias.collectAsState()
    val formatoMoeda = remember { NumberFormat.getCurrencyInstance(Locale("pt", "BR")) }

    var mostrarDialogo by remember { mutableStateOf(false) }

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
                aoClicarMovimentos = {},
                aoClicarCarteira = { navController.navigate("${Destino.Carteira.rota}/$usuarioId") },
                aoClicarRelatorios = { navController.navigate("${Destino.Relatorios.rota}/$usuarioId") },
                aoClicarMetas = { navController.navigate("${Destino.Metas.rota}/$usuarioId") },
                rotaAtual = "movimentacoes"
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarDialogo = true }) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar movimentação")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize()) {
            items(movimentacoes) { mov ->
                MovimentacaoItem(mov, formatoMoeda)
                HorizontalDivider()
            }
        }
    }

    if (mostrarDialogo) {
        DialogoNovaMovimentacao(
            categorias = categorias,
            aoConfirmar = { valor, categoriaId, descricao, tipo ->
                viewModel.registrar(valor = valor, categoriaId = categoriaId, descricao = descricao, tipo = tipo)
                mostrarDialogo = false
            },
            aoCancelar = { mostrarDialogo = false }
        )
    }
}

@Composable
private fun DialogoNovaMovimentacao(
    categorias: List<Categoria>,
    aoConfirmar: (valor: Double, categoriaId: Long, descricao: String, tipo: TipoMovimentacao) -> Unit,
    aoCancelar: () -> Unit
) {
    var valorTexto by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var tipoSelecionado by remember { mutableStateOf(TipoMovimentacao.DESPESA) }
    var categoriaSelecionada by remember { mutableStateOf(categorias.firstOrNull()) }
    var expandido by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = aoCancelar,
        title = { Text("Nova movimentação") },
        text = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { tipoSelecionado = TipoMovimentacao.RECEITA },
                        modifier = Modifier.weight(1f)
                    ) { Text(if (tipoSelecionado == TipoMovimentacao.RECEITA) "✓ Receita" else "Receita") }

                    Button(
                        onClick = { tipoSelecionado = TipoMovimentacao.DESPESA },
                        modifier = Modifier.weight(1f)
                    ) { Text(if (tipoSelecionado == TipoMovimentacao.DESPESA) "✓ Despesa" else "Despesa") }
                }
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = valorTexto,
                    onValueChange = { valorTexto = it },
                    label = { Text("Valor (R$)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = descricao,
                    onValueChange = { descricao = it },
                    label = { Text("Descrição") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))

                Box {
                    OutlinedTextField(
                        value = categoriaSelecionada?.nome ?: "Selecione uma categoria",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoria") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            TextButton(onClick = { expandido = true }) { Text("▾") }
                        }
                    )
                    DropdownMenu(expanded = expandido, onDismissRequest = { expandido = false }) {
                        categorias.forEach { categoria ->
                            DropdownMenuItem(
                                text = { Text(categoria.nome) },
                                onClick = {
                                    categoriaSelecionada = categoria
                                    expandido = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val valor = valorTexto.replace(",", ".").toDoubleOrNull()
                val categoriaId = categoriaSelecionada?.id
                if (valor != null && valor > 0 && categoriaId != null && descricao.isNotBlank()) {
                    aoConfirmar(valor, categoriaId, descricao, tipoSelecionado)
                }
            }) { Text("Salvar") }
        },
        dismissButton = {
            TextButton(onClick = aoCancelar) { Text("Cancelar") }
        }
    )
}