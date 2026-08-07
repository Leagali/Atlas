package com.example.atlasinvest.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.atlasinvest.AtlasInvestApplication
import com.example.atlasinvest.controller.AutenticacaoViewModel
import com.example.atlasinvest.controller.EstadoAutenticacao
import com.example.atlasinvest.controller.FabricaViewModel
import com.example.atlasinvest.ui.theme.AzulAtlas
import com.example.atlasinvest.ui.theme.AzulProfundo

@Composable
fun LoginScreen(
    app: AtlasInvestApplication,
    aoLogar: (Long) -> Unit,
    aoNavegarParaCadastro: () -> Unit
) {
    val viewModel: AutenticacaoViewModel = viewModel(factory = FabricaViewModel(app))
    val estado by viewModel.estado.collectAsState()

    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }

    LaunchedEffect(estado) {
        val estadoAtual = estado
        if (estadoAtual is EstadoAutenticacao.Sucesso) {
            app.sessionManager.usuarioLogadoId = estadoAtual.usuarioId
            aoLogar(estadoAtual.usuarioId)
            viewModel.resetarEstado()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Atlas Invest",
            style = MaterialTheme.typography.headlineLarge,
            color = AzulAtlas,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp
        )
        Text(
            text = "Sua jornada financeira começa aqui",
            style = MaterialTheme.typography.bodyMedium,
            color = AzulProfundo,
            modifier = Modifier.padding(top = 4.dp)
        )
        
        Spacer(Modifier.height(48.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("E-mail") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AzulAtlas,
                focusedLabelColor = AzulAtlas
            )
        )
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = senha,
            onValueChange = { senha = it },
            label = { Text("Senha") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AzulAtlas,
                focusedLabelColor = AzulAtlas
            )
        )
        Spacer(Modifier.height(32.dp))

        if (estado is EstadoAutenticacao.Erro) {
            Text(
                text = (estado as EstadoAutenticacao.Erro).mensagem,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(Modifier.height(12.dp))
        }

        Button(
            onClick = { viewModel.login(email.trim(), senha) },
            enabled = estado !is EstadoAutenticacao.Carregando,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AzulAtlas),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = if (estado is EstadoAutenticacao.Carregando) "Entrando..." else "Entrar",
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(16.dp))

        TextButton(onClick = aoNavegarParaCadastro) {
            Text(
                "Não tem conta? Cadastre-se",
                color = AzulProfundo,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}