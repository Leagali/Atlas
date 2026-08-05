@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.atlasinvest.ui.relatorios

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
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.atlasinvest.AtlasInvestApplication
import com.example.atlasinvest.controller.CategoriaViewModel
import com.example.atlasinvest.controller.FabricaViewModel
import com.example.atlasinvest.controller.MovimentacaoViewModel
import com.example.atlasinvest.data.local.entity.Categoria
import com.example.atlasinvest.data.local.entity.Movimentacao
import com.example.atlasinvest.data.local.entity.TipoMovimentacao
import com.example.atlasinvest.ui.navigation.Destino
import com.example.atlasinvest.ui.theme.BarraInferiorAtlas
import com.example.atlasinvest.ui.theme.CabecalhoAtlas
import com.example.atlasinvest.ui.theme.FundoTela
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RelatoriosScreen(app: AtlasInvestApplication, usuarioId: Long, navController: NavHostController) {
    val viewModel: MovimentacaoViewModel = viewModel(factory = FabricaViewModel(app, usuarioId))
    val categoriaViewModel: CategoriaViewModel = viewModel(factory = FabricaViewModel(app, usuarioId))

    val movimentacoes by viewModel.movimentacoes.collectAsState()
    val categorias by categoriaViewModel.categorias.collectAsState()
    val formatoMoeda = remember { NumberFormat.getCurrencyInstance(Locale("pt", "BR")) }
    val formatoData = remember { SimpleDateFormat("dd/MM/yyyy — HH:mm", Locale("pt", "BR")) }

    var mostrarDialogo by remember { mutableStateOf(false) }

    // Despesas fixas ainda não têm ViewModel próprio — placeholder estático por enquanto.
    val despesasFixasExemplo = listOf(
        "Aluguel" to 1500.0,
        "IPTU (parcela)" to 100.0,
        "Plano Celular" to 90.0,
        "Plano de Saúde" to 850.0
    )

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
                aoClicarCarteira = { navController.navigate("${Destino.Carteira.rota}/$usuarioId") },
                aoClicarRelatorios = {},
                aoClicarMetas = { navController.navigate("${Destino.Metas.rota}/$usuarioId") },
                rotaAtual = "relatorios"
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
            Text("Relatórios", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(Modifier.height(12.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text("Nome", modifier = Modifier.weight(1.2f), fontSize = 12.sp, color = Color.Gray)
                        Text("Data", modifier = Modifier.weight(1.3f), fontSize = 12.sp, color = Color.Gray)
                        Text("Valor", modifier = Modifier.weight(1f), fontSize = 12.sp, color = Color.Gray)
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))

                    LazyColumn(modifier = Modifier.height(220.dp)) {
                        items(movimentacoes) { mov ->
                            LinhaRelatorio(mov, formatoMoeda, formatoData)
                            HorizontalDivider()
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = { mostrarDialogo = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50)
            ) {
                Text("Adicionar movimentação")
            }

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Despesas Fixas", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                IconButton(onClick = { /* TODO: tela de despesas fixas */ }) {
                    Icon(Icons.Default.Add, contentDescription = "Adicionar despesa fixa")
                }
            }
            Spacer(Modifier.height(8.dp))

            despesasFixasExemplo.forEach { (nome, valor) ->
                Card(
                    shape = RoundedCornerShape(50),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(nome, fontSize = 14.sp)
                        Text(formatoMoeda.format(valor), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }

    if (mostrarDialogo) {
        DialogoNovaMovimentacaoRapida(
            categorias = categorias,
            aoConfirmar = { descricao, valor, categoriaId, tipo ->
                viewModel.registrar(
                    valor = valor,
                    categoriaId = categoriaId,
                    descricao = descricao,
                    tipo = tipo
                )
                mostrarDialogo = false
            },
            aoCancelar = { mostrarDialogo = false }
        )
    }
}

@Composable
private fun LinhaRelatorio(mov: Movimentacao, formatoMoeda: NumberFormat, formatoData: SimpleDateFormat) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(mov.descricao, modifier = Modifier.weight(1.2f), fontSize = 13.sp)
        Text(formatoData.format(Date(mov.data)), modifier = Modifier.weight(1.3f), fontSize = 12.sp)
        Text(formatoMoeda.format(mov.valor), modifier = Modifier.weight(1f), fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun DialogoNovaMovimentacaoRapida(
    categorias: List<Categoria>,
    aoConfirmar: (descricao: String, valor: Double, categoriaId: Long, tipo: TipoMovimentacao) -> Unit,
    aoCancelar: () -> Unit
) {
    var descricao by remember { mutableStateOf("") }
    var valorTexto by remember { mutableStateOf("") }
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
                    OutlinedButton(
                        onClick = { tipoSelecionado = TipoMovimentacao.RECEITA },
                        modifier = Modifier.weight(1f)
                    ) { Text(if (tipoSelecionado == TipoMovimentacao.RECEITA) "✓ Receita" else "Receita") }

                    OutlinedButton(
                        onClick = { tipoSelecionado = TipoMovimentacao.DESPESA },
                        modifier = Modifier.weight(1f)
                    ) { Text(if (tipoSelecionado == TipoMovimentacao.DESPESA) "✓ Despesa" else "Despesa") }
                }
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = descricao,
                    onValueChange = { descricao = it },
                    label = { Text("Descrição") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = valorTexto,
                    onValueChange = { valorTexto = it },
                    label = { Text("Valor (R$)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))

                // RN02: toda movimentação precisa de categoria — sem fallback nem valor fixo.
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
                        if (categorias.isEmpty()) {
                            DropdownMenuItem(text = { Text("Nenhuma categoria cadastrada") }, onClick = {})
                        }
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
                if (descricao.isNotBlank() && valor != null && valor > 0 && categoriaId != null) {
                    aoConfirmar(descricao, valor, categoriaId, tipoSelecionado)
                }
            }) { Text("Salvar") }
        },
        dismissButton = { TextButton(onClick = aoCancelar) { Text("Cancelar") } }
    )
}