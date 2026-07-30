package com.example.atlasinvest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.atlasinvest.ui.navigation.AtlasNavGraph
import com.example.atlasinvest.ui.theme.AtlasInvestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as AtlasInvestApplication
        val usuarioIdInicial = app.sessionManager.usuarioLogadoId

        setContent {
            AtlasInvestTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AtlasNavGraph(app = app, usuarioIdInicial = usuarioIdInicial)
                }
            }
        }
    }
}