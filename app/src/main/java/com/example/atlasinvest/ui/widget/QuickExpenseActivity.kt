package com.example.atlasinvest.ui.widget

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.atlasinvest.AtlasInvestApplication
import com.example.atlasinvest.data.local.entity.Movimentacao
import com.example.atlasinvest.data.local.entity.TipoMovimentacao
import com.example.atlasinvest.ui.theme.AtlasInvestTheme
import com.example.atlasinvest.ui.theme.VermelhoAtlas
import kotlinx.coroutines.launch

class QuickExpenseActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AtlasInvestTheme {
                QuickExpenseDialog(
                    onDismiss = { finish() },
                    onConfirm = { name, amount ->
                        saveExpense(name, amount)
                    }
                )
            }
        }
    }

    private fun saveExpense(name: String, amount: Double) {
        val app = application as AtlasInvestApplication
        val userId = app.sessionManager.usuarioLogadoId

        if (userId == -1L) {
            Toast.makeText(this, "Usuário não logado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        lifecycleScope.launch {
            val categoriaId = 7L // ID da categoria 'Outros' no script padrão do CategoriaRepository

            app.movimentacaoRepository.registrar(
                Movimentacao(
                    usuarioId = userId,
                    categoriaId = categoriaId,
                    descricao = name,
                    valor = amount,
                    tipo = TipoMovimentacao.DESPESA,
                    data = System.currentTimeMillis()
                )
            )
            
            Toast.makeText(this@QuickExpenseActivity, "Gasto registrado!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}

@Composable
fun QuickExpenseDialog(onDismiss: () -> Unit, onConfirm: (String, Double) -> Unit) {
    var name by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Registrar Gasto",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = VermelhoAtlas
            )
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nome do gasto") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it },
                label = { Text("Valor (R$)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar")
                }
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = {
                        val amount = amountText.toDoubleOrNull()
                        if (name.isNotBlank() && amount != null && amount > 0) {
                            onConfirm(name, amount)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VermelhoAtlas)
                ) {
                    Text("Salvar")
                }
            }
        }
    }
}
