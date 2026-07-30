package com.example.atlasinvest.ui.movimentacoes

import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.example.atlasinvest.data.local.entity.Movimentacao
import com.example.atlasinvest.data.local.entity.TipoMovimentacao
import com.example.atlasinvest.ui.theme.VerdeReceita
import com.example.atlasinvest.ui.theme.VermelhoDespesa
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MovimentacaoItem(movimentacao: Movimentacao, formatoMoeda: NumberFormat) {
    val formatoData = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
    ListItem(
        headlineContent = { Text(movimentacao.descricao) },
        supportingContent = { Text(formatoData.format(Date(movimentacao.data))) },
        trailingContent = {
            val sinal = if (movimentacao.tipo == TipoMovimentacao.RECEITA) "+ " else "- "
            val cor = if (movimentacao.tipo == TipoMovimentacao.RECEITA) VerdeReceita else VermelhoDespesa
            Text(
                text = sinal + formatoMoeda.format(movimentacao.valor),
                color = cor,
                fontWeight = FontWeight.Bold
            )
        }
    )
}