package com.example.domo.ui.home

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Diamond
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.domo.core.utils.AppBlockerUtils
import com.example.domo.core.utils.LauncherUtils
import com.example.domo.ui.components.GameButton3D
import com.example.domo.ui.components.GameButtonDefaults

private const val GOOGLE_PACKAGE = "com.google.android.googlequicksearchbox"
private const val GOOGLE_PHOTOS_PACKAGE = "com.google.android.apps.photos"

// ── Dados mock do popup Trophy ──────────────────────────────────────────────────
private data class MissionItemUi(
    val title: String,
    val progress: Float,
    val reward: Int,
)

private val mockMissions = listOf(
    MissionItemUi("PLAY 10 GAMES", 0.40f, 10),
    MissionItemUi("PLAY 10 GAMES", 0.65f, 10),
    MissionItemUi("PLAY 10 GAMES", 0.40f, 10),
)

// ── Paleta game UI ──────────────────────────────────────────────────────────────
private val CBgStart     = Color(0xFFA659FE)
private val CBgEnd       = Color(0xFF6F53FD)
private val CBlueFace    = Color(0xFF4CDAFE)
private val CBlueShadow  = Color(0xFF09B9FF)
private val CPinkFace    = Color(0xFFFC8AFF)
private val CPinkShadow  = Color(0xFFDA57F0)
private val CPurpleFace  = Color(0xFFC286FF)
private val CPurpleShadow = Color(0xFFA75CF4)
private val CYellowFace  = Color(0xFFFFDD17)
private val CYellowShadow = Color(0xFFFFB213)
private val CCardBg      = Color(0xFFFFFEFF)
private val CCardShadow  = Color(0xFFD1D8FF)
private val CTextDark    = Color(0xFF2C2C2A)
private val CTextMuted   = Color(0xFF888780)

@Composable
fun HomeScreen(
    onNavigateToGame: () -> Unit = {},
    viewModel: MainViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasUsagePermission by rememberSaveable { mutableStateOf(AppBlockerUtils.hasUsageStatsPermission(context)) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshDefaultLauncherStatus()
                hasUsagePermission = AppBlockerUtils.hasUsageStatsPermission(context)
                if (hasUsagePermission) AppBlockerUtils.startBlockerService(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.onMessageShown()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(CBgStart, CBgEnd)))
            .safeDrawingPadding(),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (!uiState.isDefaultLauncher) {
                ActivateBanner(
                    onActivateClick = { LauncherUtils.openDefaultLauncherSettings(context) },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                )
            }
            if (!hasUsagePermission) {
                UsagePermissionBanner(
                    onActivateClick = { AppBlockerUtils.openUsageStatsSettings(context) },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                )
            }

            Header(uiState.level, uiState.diamonds, uiState.currentXp)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 16.dp),
            ) {
                /*
                HeroCard(
                    greeting = uiState.heroGreeting,
                    line1 = uiState.heroCtaLine1,
                    line2 = uiState.heroCtaLine2,
                    onPlay = onNavigateToGame,
                )  */
                Spacer(modifier = Modifier.height(14.dp))
                ShortcutsSection(
                    onGoogleClick = { openAppOrUrl(context, GOOGLE_PACKAGE, "https://www.google.com") },
                    onPhotosClick = { openAppOrUrl(context, GOOGLE_PHOTOS_PACKAGE, "https://photos.google.com") },
                )
                Spacer(modifier = Modifier.height(14.dp))
                ButtonGallerySection()
                Spacer(modifier = Modifier.height(14.dp))
            }
        }

        // Bottom nav bar fixada na base
        HomeBottomBar(
            selected = uiState.selectedBottomTab,
            onSelect = viewModel::onBottomTabSelected,
            modifier = Modifier.align(Alignment.BottomCenter),
        )

        // Trophy popup — exibido ao retornar da tela de resultado
        if (uiState.showTrophyPopup) {
            TrophyPopup(onDismiss = viewModel::dismissTrophyPopup)
        }
    }
}

// ── Header ─────────────────────────────────────────────────────────────────────
@Composable
private fun Header(level: Int, diamonds: Int, currentXp: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Level pill com efeito 3D
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .background(CPurpleShadow)
                .border(3.dp, Color.White, RoundedCornerShape(50.dp))
                .padding(bottom = 3.dp),
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .background(CPurpleFace)
                    .padding(horizontal = 18.dp, vertical = 7.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "LEVEL ${level.toString().padStart(2, '0')}",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    style = TextStyle(shadow = Shadow(Color(0xFF5C00CC), offset = Offset(0f, 2f))),
                )
            }
        }

        // Moedas + diamantes
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
            // XP / moedas
            Text(
                text = "$currentXp",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                style = TextStyle(shadow = Shadow(Color.Black.copy(alpha = 0.25f), offset = Offset(0f, 1f))),
            )
            Text(
                text = "⭐",
                fontSize = 18.sp,
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = "$diamonds",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                style = TextStyle(shadow = Shadow(Color.Black.copy(alpha = 0.25f), offset = Offset(0f, 1f))),
            )
            Text(
                text = "💎",
                fontSize = 18.sp,
            )
        }
    }
}

// ── HeroCard ───────────────────────────────────────────────────────────────────
@Composable
private fun HeroCard(greeting: String, line1: String, line2: String, onPlay: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {
        // Sombra colorida
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .clip(RoundedCornerShape(21.dp))
                .background(CCardShadow)
                .height(140.dp),
        )
        // Card branco
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(21.dp))
                .background(CCardBg)
                .padding(horizontal = 20.dp, vertical = 20.dp),
        ) {
            Column {
                Text(greeting, color = CTextMuted, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "$line1\n$line2",
                    color = CTextDark,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    lineHeight = 24.sp,
                )
                Spacer(modifier = Modifier.height(14.dp))
                GameButton3D(
                    text = "JOGAR",
                    colors = GameButtonDefaults.Verde,
                    modifier = Modifier.width(140.dp),
                    onClick = onPlay,
                )
            }
        }
    }
}

// ── Atalhos ────────────────────────────────────────────────────────────────────
@Composable
private fun ShortcutsSection(onGoogleClick: () -> Unit, onPhotosClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {
        // Sombra colorida
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(CCardShadow)
                .height(100.dp),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CCardBg)
                .padding(12.dp),
        ) {
            Column {
                Text("Acessos Rapidos", fontSize = 11.sp, color = CTextMuted, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ShortcutCard(Modifier.weight(1f), "Google", Icons.Rounded.Search, onGoogleClick)
                    ShortcutCard(Modifier.weight(1f), "Google Fotos", Icons.Rounded.PhotoLibrary, onPhotosClick)
                }
            }
        }
    }
}

@Composable
private fun ShortcutCard(modifier: Modifier, title: String, icon: ImageVector, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFEFF4FF))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = title, tint = CPurpleShadow, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(title, color = CTextDark, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, textAlign = TextAlign.Center)
        }
    }
}

// ── Bottom nav bar ─────────────────────────────────────────────────────────────
@Composable
private fun HomeBottomBar(
    selected: HomeBottomTab,
    onSelect: (HomeBottomTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 8.dp)
            .background(CCardBg)
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            BottomNavItem(
                label = "Jogos",
                icon = Icons.Rounded.BarChart,
                faceColor = CBlueFace,
                shadowColor = CBlueShadow,
                selected = selected == HomeBottomTab.GAMES,
                onClick = { onSelect(HomeBottomTab.GAMES) },
            )
            BottomNavItem(
                label = "Missao",
                icon = Icons.Rounded.Schedule,
                faceColor = CPinkFace,
                shadowColor = CPinkShadow,
                selected = selected == HomeBottomTab.MISSION,
                onClick = { onSelect(HomeBottomTab.MISSION) },
            )
            BottomNavItem(
                label = "Premios",
                icon = Icons.Rounded.Star,
                faceColor = CYellowFace,
                shadowColor = CYellowShadow,
                selected = selected == HomeBottomTab.REWARDS,
                onClick = { onSelect(HomeBottomTab.REWARDS) },
            )
            BottomNavItem(
                label = "Perfil",
                icon = Icons.Rounded.Person,
                faceColor = CPurpleFace,
                shadowColor = CPurpleShadow,
                selected = selected == HomeBottomTab.PROFILE,
                onClick = { onSelect(HomeBottomTab.PROFILE) },
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    icon: ImageVector,
    faceColor: Color,
    shadowColor: Color,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val alpha = if (selected) 1f else 0.5f
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(shadowColor.copy(alpha = alpha))
                .border(2.dp, Color.White.copy(alpha = alpha), RoundedCornerShape(14.dp))
                .padding(bottom = 3.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .background(faceColor.copy(alpha = alpha)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp),
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = if (selected) Color.White else Color.White.copy(alpha = 0.5f),
            fontSize = 10.sp,
            fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.SemiBold,
        )
    }
}

// ── Galeria de botões para avaliação ───────────────────────────────────────────
@Composable
private fun ButtonGallerySection() {
    // Estrutura: Box externo reserva 4dp na base para a sombra colorida
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp),
    ) {
        // Sombra — acompanha a altura dinâmica do card via matchParentSize + offset
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = 4.dp)
                .clip(RoundedCornerShape(21.dp))
                .background(CCardShadow),
        )

        // Card branco com a galeria de botões
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(21.dp))
                .background(CCardBg)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = "Avalie os botões",
                color = CTextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
            )

            // ── Grid 2 colunas: botões compactos de tamanho igual ────────────────
            // Linha 1
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                GameButton3D(
                    text   = "JOGAR",
                    colors = GameButtonDefaults.Verde,
                    modifier = Modifier.weight(1f),
                    onClick = {},
                )
                GameButton3D(
                    text   = "PRÓXIMO",
                    colors = GameButtonDefaults.Rosa,
                    modifier = Modifier.weight(1f),
                    onClick = {},
                )
            }
            // Linha 2
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                GameButton3D(
                    text   = "CONTINUAR",
                    colors = GameButtonDefaults.Teal,
                    modifier = Modifier.weight(1f),
                    onClick = {},
                )
                GameButton3D(
                    text   = "RECOMEÇAR",
                    colors = GameButtonDefaults.Amarelo,
                    modifier = Modifier.weight(1f),
                    onClick = {},
                )
            }
            // Linha 3 — último botão centralizado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                GameButton3D(
                    text    = "SAIR",
                    colors  = GameButtonDefaults.Lilas,
                    enabled = false,
                    modifier = Modifier.weight(1f, fill = false).fillMaxWidth(0.48f),
                    onClick = {},
                )
            }
        }
    }
}

// ── Banners ────────────────────────────────────────────────────────────────────
@Composable
private fun ActivateBanner(onActivateClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFFFD76D))
            .clickable(onClick = onActivateClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Definir como tela inicial", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF5C3A00), fontWeight = FontWeight.Bold)
            Text("Ativar", color = Color(0xFF5C3A00), fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun UsagePermissionBanner(onActivateClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFFFE4E6))
            .clickable(onClick = onActivateClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("🔒 Ativar protecao de apps", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF9B1C1C), fontWeight = FontWeight.Bold)
                Text("Necessario para bloquear apps com PIN", fontSize = 11.sp, color = Color(0xFFB91C1C))
            }
            Text("Ativar", color = Color(0xFF9B1C1C), fontWeight = FontWeight.ExtraBold)
        }
    }
}

// ── Trophy Popup ───────────────────────────────────────────────────────────────

@Composable
private fun TrophyPopup(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        // Box externo: reserva espaço para sombra na base
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
        ) {
            // Sombra colorida (deslocada 4dp para baixo)
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(y = 4.dp)
                    .clip(RoundedCornerShape(21.dp))
                    .background(CCardShadow),
            )
            // Card branco
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(21.dp))
                    .background(CCardBg)
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp, bottom = 20.dp),
            ) {
                TrophyHeader(onDismiss = onDismiss)
                Spacer(modifier = Modifier.height(14.dp))
                mockMissions.forEachIndexed { index, mission ->
                    MissionRow(mission = mission)
                    if (index < mockMissions.lastIndex) {
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}

// ── Header do popup (pill roxo + botão X) ─────────────────────────────────────
@Composable
private fun TrophyHeader(onDismiss: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {
        // Pill — camada sombra
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp)
                .clip(RoundedCornerShape(50.dp))
                .background(CPurpleShadow)
                .height(52.dp),
        )
        // Pill — camada face com gradiente
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(50.dp))
                .background(Brush.horizontalGradient(listOf(CPurpleFace, CPurpleShadow)))
                .height(50.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "TROPHY",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp,
                textAlign = TextAlign.Center,
                style = TextStyle(shadow = Shadow(Color(0xFF5C00CC), offset = Offset(0f, 2f))),
            )
        }
        // Botão X circular — fundo branco, borda + ícone roxo (igual ao Figma)
        Box(
            modifier = Modifier
                .size(40.dp)
                .align(Alignment.CenterEnd)
                .offset(x = (-4).dp)
                .clip(CircleShape)
                .background(CCardBg)
                .border(2.5.dp, CPurpleShadow, CircleShape)
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = "Fechar",
                tint = CPurpleShadow,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

// ── Linha de missão ────────────────────────────────────────────────────────────
@Composable
private fun MissionRow(mission: MissionItemUi) {
    // IntrinsicSize.Min faz o SKIP button ter a mesma altura que o card azul
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Card azul (missão) — ocupa espaço restante
        Box(modifier = Modifier.weight(1f)) {
            // Sombra — matchParentSize garante que acompanha a altura do conteúdo
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(y = 3.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(CBlueShadow),
            )
            // Camada face azul — altura guiada pelo conteúdo, corners mais arredondados como Figma
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(CBlueFace)
                    .padding(horizontal = 14.dp, vertical = 14.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Título e barra de progresso
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = mission.title,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Clip,
                            style = TextStyle(
                                shadow = Shadow(Color.Black.copy(alpha = 0.25f), offset = Offset(0f, 1f)),
                            ),
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        MissionProgressBar(progress = mission.progress)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    // Recompensa
                    Text(
                        text = mission.reward.toString(),
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp,
                        style = TextStyle(
                            shadow = Shadow(Color.Black.copy(alpha = 0.2f), offset = Offset(0f, 1f)),
                        ),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    // Ícone diamante — proeminente como no Figma
                    Icon(
                        imageVector = Icons.Rounded.Diamond,
                        contentDescription = null,
                        tint = Color(0xFFDA57F0),
                        modifier = Modifier.size(30.dp),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Botão SKIP laranja 3D — fillMaxHeight para acompanhar altura do card
        SkipButton(
            onClick = {},
            modifier = Modifier.fillMaxHeight(),
        )
    }
}

// ── Barra de progresso da missão ───────────────────────────────────────────────
@Composable
private fun MissionProgressBar(progress: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(13.dp)
            .clip(RoundedCornerShape(50.dp))
            .background(Color.White.copy(alpha = 0.35f)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .height(13.dp)
                .clip(RoundedCornerShape(50.dp))
                .background(
                    Brush.horizontalGradient(listOf(CPinkFace, CPinkShadow)),
                ),
        )
    }
}

// ── Botão SKIP laranja 3D ──────────────────────────────────────────────────────
@Composable
private fun SkipButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val cOrangeFace   = Color(0xFFFFA040)
    val cOrangeShadow = Color(0xFFE07320)

    Box(
        modifier = modifier
            .width(88.dp)
            .clip(RoundedCornerShape(50.dp))
            .background(cOrangeShadow)
            .border(2.dp, Color.White, RoundedCornerShape(50.dp))
            .padding(bottom = 3.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .clip(RoundedCornerShape(50.dp))
                .background(cOrangeFace)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = "SKIP",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    style = TextStyle(
                        shadow = Shadow(Color.Black.copy(alpha = 0.2f), offset = Offset(0f, 1f)),
                    ),
                )
                Icon(
                    imageVector = Icons.Rounded.Videocam,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

private fun openAppOrUrl(context: Context, packageName: String, fallbackUrl: String) {
    val packageManager = context.packageManager
    val appIntent = packageManager.getLaunchIntentForPackage(packageName)
    if (appIntent != null) {
        context.startActivity(appIntent)
        return
    }
    val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse(fallbackUrl)).apply { addCategory(Intent.CATEGORY_BROWSABLE) }
    try {
        context.startActivity(fallbackIntent)
    } catch (_: ActivityNotFoundException) {
        // Sem app capaz de abrir web; nesse caso apenas ignora.
    }
}
