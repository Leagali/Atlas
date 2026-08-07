@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.atlasinvest.ui.metas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.atlasinvest.AtlasInvestApplication
import com.example.atlasinvest.controller.FabricaViewModel
import com.example.atlasinvest.controller.MetaViewModel
import com.example.atlasinvest.data.local.entity.Meta
import com.example.atlasinvest.data.local.entity.StatusMeta
import com.example.atlasinvest.ui.navigation.Destino
import com.example.atlasinvest.ui.theme.BarraInferiorAtlas
import com.example.atlasinvest.ui.theme.CabecalhoAtlas
import com.example.atlasinvest.ui.theme.FundoTela
import com.example.atlasinvest.ui.theme.VerdeReceita
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun MetasScreen(app: AtlasInvestApplication, usuarioId: Long, navController: NavHostController) {
    val viewModel: MetaViewModel = viewModel(factory = FabricaViewModel(app, usuarioId))
    val usuario by viewModel.usuario.collectAsState()
    val metas by viewModel.metas.collectAsState()
    val formatoMoeda = remember { NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")) }
    var mostrarDialogo by remember { mutableStateOf(value = false) }

    val metasEmAndamento = metas.filter { it.status == StatusMeta.EM_ANDAMENTO }
    val metasConcluidas = metas.filter { it.status == StatusMeta.CONCLUIDA }

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
                }
            )
        },
        bottomBar = {
            BarraInferiorAtlas(
                aoClicarInicio = { navController.navigate("${Destino.Dashboard.rota}/$usuarioId") },
                aoClicarMovimentos = { navController.navigate("${Destino.Movimentacoes.rota}/$usuarioId") },
                aoClicarCarteira = { navController.navigate("${Destino.Carteira.rota}/$usuarioId") },
                aoClicarRelatorios = { navController.navigate("${Destino.Relatorios.rota}/$usuarioId") },
                aoClicarMetas = {},
                rotaAtual = "metas"
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { mostrarDialogo = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nova meta")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(Modifier.height(8.dp))
                Text(
                    "Minhas Metas",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (metasEmAndamento.isNotEmpty()) {
                item {
                    Text("Em andamento", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                items(metasEmAndamento) { meta ->
                    CartaoMetaRefinado(meta, formatoMoeda)
                }
            }

            if (metasConcluidas.isNotEmpty()) {
                item {
                    Text("Concluídas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                items(metasConcluidas) { meta ->
                    CartaoMetaRefinado(meta, formatoMoeda)
                }
            }

            if (metas.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                        Text("Você ainda não cadastrou nenhuma meta.", color = Color.Gray)
                    }
                }
            }
            
            item { Spacer(Modifier.height(80.dp)) }
        }
    }

    if (mostrarDialogo) {
        DialogoNovaMeta(
            aoConfirmar = { nome, valorAlvo, prazo ->
                viewModel.criar(nome, valorAlvo, prazo)
                mostrarDialogo = false
            },
            aoCancelar = { mostrarDialogo = false }
        )
    }
}

@Composable
private fun CartaoMetaRefinado(meta: Meta, formatoMoeda: NumberFormat) {
    val progresso = if (meta.valorAlvo > 0) {
        (meta.valorAcumulado / meta.valorAlvo).toFloat().coerceIn(0f, 1f)
    } else 0f
    
    val corStatus = if (meta.status == StatusMeta.CONCLUIDA) VerdeReceita else MaterialTheme.colorScheme.primary

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        meta.nome, 
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "Prazo: ${SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("pt-BR")).format(Date(meta.prazo))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = { progresso },
                        modifier = Modifier.size(48.dp),
                        color = corStatus,
                        strokeWidth = 4.dp,
                        trackColor = corStatus.copy(alpha = 0.1f),
                    )
                    Text(
                        "${(progresso * 100).toInt()}%",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = corStatus
                    )
                }
            }
            
            Spacer(Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Acumulado", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text(formatoMoeda.format(meta.valorAcumulado), fontWeight = FontWeight.Bold, color = corStatus)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Alvo", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text(formatoMoeda.format(meta.valorAlvo), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun DialogoNovaMeta(
    aoConfirmar: (nome: String, valorAlvo: Double, prazo: Long) -> Unit,
    aoCancelar: () -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var valorTexto by remember { mutableStateOf("") }
    var mesesTexto by remember { mutableStateOf("12") }

    AlertDialog(
        onDismissRequest = aoCancelar,
        title = { Text("Nova meta financeira") },
        text = {
            Column {
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome da meta") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = valorTexto,
                    onValueChange = { valorTexto = it },
                    label = { Text("Valor-alvo (R$)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = mesesTexto,
                    onValueChange = { mesesTexto = it },
                    label = { Text("Prazo (meses a partir de hoje)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val valorAlvo = valorTexto.replace(",", ".").toDoubleOrNull()
                val meses = mesesTexto.toIntOrNull()
                if (nome.isNotBlank() && valorAlvo != null && valorAlvo > 0 && meses != null && meses > 0) {
                    val calendario = Calendar.getInstance().apply { add(Calendar.MONTH, meses) }
                    aoConfirmar(nome, valorAlvo, calendario.timeInMillis)
                }
            }) { Text("Criar") }
        },
        dismissButton = { TextButton(onClick = aoCancelar) { Text("Cancelar") } }
    )
}
