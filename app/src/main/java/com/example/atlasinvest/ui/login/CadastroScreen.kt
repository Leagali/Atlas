package com.example.atlasinvest.ui.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import com.example.atlasinvest.ui.theme.NavyInvest
import com.example.atlasinvest.ui.theme.VermelhoAtlas

@Composable
fun CadastroScreen(
    app: AtlasInvestApplication,
    aoCadastrar: (Long) -> Unit
) {
    val viewModel: AutenticacaoViewModel = viewModel(factory = FabricaViewModel(app))
    val estado by viewModel.estado.collectAsState()

    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }

    LaunchedEffect(estado) {
        val estadoAtual = estado
        if (estadoAtual is EstadoAutenticacao.Sucesso) {
            app.sessionManager.usuarioLogadoId = estadoAtual.usuarioId
            aoCadastrar(estadoAtual.usuarioId)
            viewModel.resetarEstado()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(32.dp))
        Text(
            text = "Criar conta",
            style = MaterialTheme.typography.headlineMedium,
            color = VermelhoAtlas,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Preencha os dados abaixo para começar",
            style = MaterialTheme.typography.bodyMedium,
            color = NavyInvest,
            modifier = Modifier.padding(top = 4.dp)
        )
        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome completo") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = VermelhoAtlas,
                focusedLabelColor = VermelhoAtlas
            )
        )
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("E-mail") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = VermelhoAtlas,
                focusedLabelColor = VermelhoAtlas
            )
        )
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = telefone,
            onValueChange = { telefone = it },
            label = { Text("Telefone (opcional)") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = VermelhoAtlas,
                focusedLabelColor = VermelhoAtlas
            )
        )
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = senha,
            onValueChange = { senha = it },
            label = { Text("Senha") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = VermelhoAtlas,
                focusedLabelColor = VermelhoAtlas
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
            onClick = {
                viewModel.cadastrar(nome.trim(), email.trim(), senha, telefone.ifBlank { null })
            },
            enabled = estado !is EstadoAutenticacao.Carregando,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VermelhoAtlas),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                if (estado is EstadoAutenticacao.Carregando) "Criando..." else "Cadastrar",
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(24.dp))
    }
}