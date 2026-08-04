package com.example.atlasinvest.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val EsquemaClaro = lightColorScheme(
    primary = VermelhoAtlas,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDAD4),
    onPrimaryContainer = Color(0xFF410002),
    secondary = NavyInvest,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD6E2FF),
    onSecondaryContainer = Color(0xFF001A41),
    tertiary = AmareloAlerta,
    onTertiary = Color.Black,
    background = FundoTela,
    onBackground = TextoPrimario,
    surface = FundoCard,
    onSurface = TextoPrimario,
    error = VermelhoDespesa,
    onError = Color.White
)

private val EsquemaEscuro = darkColorScheme(
    primary = VermelhoAtlas,
    onPrimary = Color.White,
    secondary = NavyInvest,
    onSecondary = Color.White,
    tertiary = AmareloAlerta,
    background = BarraInferior,
    onBackground = Color.White,
    surface = Color(0xFF2B2B2B),
    onSurface = Color.White,
    error = VermelhoDespesa,
    onError = Color.White
)

@Composable
fun AtlasInvestTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> EsquemaEscuro
        else -> EsquemaClaro
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
