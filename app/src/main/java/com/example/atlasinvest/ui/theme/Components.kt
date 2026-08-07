package com.example.atlasinvest.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

/**
 * Cabeçalho reutilizado nas telas principais (Dashboard, Carteira, Relatórios).
 */
@Composable
fun CabecalhoAtlas(
    nomeUsuario: String,
    iniciais: String,
    aoLogout: () -> Unit,
    acaoExtra: (@Composable () -> Unit)? = null,
) {
    var expandido by remember { mutableStateOf(value = false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(iniciais, color = Color.White, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.width(12.dp))

        Text(
            text = "Olá, $nomeUsuario",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )

        acaoExtra?.invoke()

        Box {
            IconButton(onClick = { expandido = true }) {
                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = MaterialTheme.colorScheme.primary)
            }
            DropdownMenu(
                expanded = expandido,
                onDismissRequest = { expandido = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Sair da conta") },
                    onClick = {
                        expandido = false
                        aoLogout()
                    }
                )
            }
        }
    }
}

/**
 * Barra inferior escura com os 5 atalhos principais.
 */
@Composable
fun BarraInferiorAtlas(
    aoClicarInicio: () -> Unit,
    aoClicarMovimentos: () -> Unit,
    aoClicarCarteira: () -> Unit,
    aoClicarRelatorios: () -> Unit,
    aoClicarMetas: () -> Unit,
    rotaAtual: String = ""
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BarraInferior)
            .padding(vertical = 10.dp),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BotaoIconeBarra(
            icon = Icons.Default.Home,
            contentDescription = "Início",
            onClick = aoClicarInicio,
            tint = if (rotaAtual == "dashboard") MaterialTheme.colorScheme.primary else Color.White
        )
        BotaoIconeBarra(
            icon = Icons.AutoMirrored.Filled.List,
            contentDescription = "Movimentações",
            onClick = aoClicarMovimentos,
            tint = if (rotaAtual == "movimentacoes") MaterialTheme.colorScheme.primary else Color.White
        )
        BotaoIconeBarra(
            icon = Icons.Default.Savings,
            contentDescription = "Carteira",
            onClick = aoClicarCarteira,
            tint = if (rotaAtual == "carteira") MaterialTheme.colorScheme.tertiary else Color.White
        )
        BotaoIconeBarra(
            icon = Icons.Default.PieChart,
            contentDescription = "Relatórios",
            onClick = aoClicarRelatorios,
            tint = if (rotaAtual == "relatorios") MaterialTheme.colorScheme.primary else Color.White
        )
        BotaoIconeBarra(
            icon = Icons.Default.AccountBalance,
            contentDescription = "Metas",
            onClick = aoClicarMetas,
            tint = if (rotaAtual == "metas") VerdeReceita else Color.White
        )
    }
}

@Composable
private fun BotaoIconeBarra(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    tint: Color
) {
    IconButton(onClick = onClick) {
        Icon(icon, contentDescription = contentDescription, tint = tint, modifier = Modifier.size(26.dp))
    }
}
