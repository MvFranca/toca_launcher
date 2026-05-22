package com.example.domo.ui.pin

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domo.MainActivity
import com.example.domo.service.AppBlockerService
import com.example.domo.ui.theme.DomoTheme

class PinLockActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val blockedPackage = intent.getStringExtra(EXTRA_BLOCKED_PACKAGE) ?: ""
        setContent {
            DomoTheme {
                PinLockScreen(
                    blockedPackage = blockedPackage,
                    onCorrectPin = {
                        // Avisa o serviço que este app foi liberado temporariamente
                        val allowIntent = Intent(this, AppBlockerService::class.java).apply {
                            action = AppBlockerService.ACTION_ALLOW_PACKAGE
                            putExtra(AppBlockerService.EXTRA_PACKAGE, blockedPackage)
                        }
                        startService(allowIntent)

                        // Abre o app que estava bloqueado
                        val launchIntent = packageManager.getLaunchIntentForPackage(blockedPackage)
                        if (launchIntent != null) {
                            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(launchIntent)
                        }
                        finish()
                    },
                    onCancel = {
                        // Volta para o launcher
                        val homeIntent = Intent(this, MainActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        }
                        startActivity(homeIntent)
                        finish()
                    },
                )
            }
        }
    }

    override fun onBackPressed() {
        // Bloqueia o botão voltar — usuário precisa digitar PIN ou cancelar
    }

    companion object {
        const val EXTRA_BLOCKED_PACKAGE = "blocked_package"
        const val CORRECT_PIN = "1234"
    }
}

@Composable
private fun PinLockScreen(
    blockedPackage: String,
    onCorrectPin: () -> Unit,
    onCancel: () -> Unit,
) {
    var entered by remember { mutableStateOf("") }
    var errorShake by remember { mutableStateOf(false) }

    val appName = blockedPackage.substringAfterLast(".").replaceFirstChar { it.uppercaseChar() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("🔒", fontSize = 48.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "App protegido",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = appName,
            color = Color(0xFF94A3B8),
            fontSize = 14.sp,
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Indicadores de dígitos
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            repeat(4) { index ->
                val filled = index < entered.length
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(if (filled) Color(0xFFE8335A) else Color(0xFF1E293B))
                        .border(2.dp, Color(0xFF334155), CircleShape),
                )
            }
        }

        if (errorShake) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("PIN incorreto", color = Color(0xFFE8335A), fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(36.dp))

        // Teclado numérico
        val keys = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf("cancel", "0", "del"),
        )

        keys.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.padding(vertical = 8.dp),
            ) {
                row.forEach { key ->
                    when (key) {
                        "del" -> PinKey(
                            content = {
                                Icon(
                                    Icons.Rounded.Backspace,
                                    contentDescription = "Apagar",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp),
                                )
                            },
                            onClick = {
                                if (entered.isNotEmpty()) entered = entered.dropLast(1)
                                errorShake = false
                            },
                        )
                        "cancel" -> PinKey(
                            content = {
                                Text("✕", color = Color(0xFF94A3B8), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            },
                            onClick = onCancel,
                        )
                        else -> PinKey(
                            content = {
                                Text(key, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Medium)
                            },
                            onClick = {
                                if (entered.length < 4) {
                                    entered += key
                                    errorShake = false
                                    if (entered.length == 4) {
                                        if (entered == PinLockActivity.CORRECT_PIN) {
                                            onCorrectPin()
                                        } else {
                                            errorShake = true
                                            entered = ""
                                        }
                                    }
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PinKey(
    content: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1E293B))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}
