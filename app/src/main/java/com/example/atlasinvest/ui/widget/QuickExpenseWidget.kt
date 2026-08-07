package com.example.atlasinvest.ui.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.height
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.glance.appwidget.cornerRadius
import com.example.atlasinvest.ui.theme.AzulAtlas
import com.example.atlasinvest.ui.theme.AzulProfundo
import com.example.atlasinvest.ui.theme.CianoInvest

class QuickExpenseWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            WidgetContent()
        }
    }

    @Composable
    private fun WidgetContent() {
        // Container principal Square (2x2)
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ColorProvider(AzulProfundo))
                .cornerRadius(24.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ícone de Marca maior e centralizado
                Box(
                    modifier = GlanceModifier
                        .size(64.dp)
                        .background(ColorProvider(CianoInvest.copy(alpha = 0.15f)))
                        .cornerRadius(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$",
                        style = TextStyle(
                            color = ColorProvider(CianoInvest),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = GlanceModifier.height(12.dp))

                Text(
                    text = "Atlas Invest",
                    style = TextStyle(
                        color = ColorProvider(Color.White),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = GlanceModifier.height(16.dp))

                // Botão "Pill" Customizado (Muito mais bonito que o padrão)
                Box(
                    modifier = GlanceModifier
                        .background(ColorProvider(AzulAtlas))
                        .cornerRadius(50.dp)
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                        .clickable(actionStartActivity<QuickExpenseActivity>()),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Gastar",
                        style = TextStyle(
                            color = ColorProvider(Color.White),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

class QuickExpenseWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = QuickExpenseWidget()
}
