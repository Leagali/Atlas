@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.atlasinvest.ui.metas

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
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.atlasinvest.AtlasInvestApplication
import com.example.atlasinvest.controller.FabricaViewModel
import com.example.atlasinvest.controller.MetaViewModel
import com.example.atlasinvest.data.local.entity.Meta
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun MetasScreen(app: AtlasInvestApplication, usuarioId: Long) {
    val viewModel: MetaViewModel = viewModel(factory = FabricaViewModel(app, usuarioId))
    val metas by viewModel.metas.collectAsState()
    val formatoMoeda = remember { NumberFormat.getCurrencyInstance(Locale("pt", "BR")) }
    var mostrarDialogo by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Metas financeiras") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarDialogo = true }) {
                Icon(Icons.Default.Add, contentDescription = "Nova meta")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            items(metas) { meta ->
                CartaoMeta(meta, formatoMoeda)
                Spacer(Modifier.height(12.dp))
            }
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
private fun CartaoMeta(meta: Meta, formatoMoeda: NumberFormat) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(meta.nome, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(4.dp))
            Text("Prazo: ${SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")).format(Date(meta.prazo))}")
            Spacer(Modifier.height(8.dp))

            val progresso = if (meta.valorAlvo > 0) {
                (meta.valorAcumulado / meta.valorAlvo).toFloat().coerceIn(0f, 1f)
            } else 0f

            LinearProgressIndicator(progress = progresso, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(4.dp))
            Text("${formatoMoeda.format(meta.valorAcumulado)} de ${formatoMoeda.format(meta.valorAlvo)}")
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