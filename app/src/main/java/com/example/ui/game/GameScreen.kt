package com.example.ui.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.R
import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GameState
import kotlin.math.sin
import kotlin.math.cos
import kotlin.math.abs

data class WildTree(val nx: Float, val ny: Float, val type: Int)
data class WildStone(val nx: Float, val ny: Float)

fun formatNumber(n: Long): String = when {
    n >= 1_000_000_000L -> "%.1fB".format(n / 1_000_000_000.0)
    n >= 1_000_000L -> "%.1fM".format(n / 1_000_000.0)
    n >= 1_000L -> "%.1fK".format(n / 1_000.0)
    else -> n.toString()
}

fun generateWilderness(): Pair<List<WildTree>, List<WildStone>> {
    val trees = mutableListOf<WildTree>()
    val stones = mutableListOf<WildStone>()
    val random = java.util.Random(42) // Stable seed
    
    var tCount = 0
    while(tCount < 26) {
        val nx = random.nextFloat() * 1.8f - 0.9f // -0.9 to +0.9 (full canvas spread)
        val ny = random.nextFloat() * 1.8f - 0.9f
        if (nx * nx + ny * ny > 0.08f) { // keep center clear for campfire plaza
            trees.add(WildTree(nx, ny, random.nextInt(3)))
            tCount++
        }
    }
    
    var sCount = 0
    while(sCount < 14) {
        val nx = random.nextFloat() * 1.8f - 0.9f
        val ny = random.nextFloat() * 1.8f - 0.9f
        if (nx * nx + ny * ny > 0.08f) {
            stones.add(WildStone(nx, ny))
            sCount++
        }
    }
    return Pair(trees, stones)
}

@Composable
fun GameScreen(viewModel: GameViewModel) {
    val state by viewModel.uiState.collectAsState()
    val afkResult by viewModel.afkResult.collectAsState()
    val lootResult by viewModel.dungeonLootResult.collectAsState()

    var showTalentTreeModal by remember { mutableStateOf(false) }
    var showKomandanFullscreen by remember { mutableStateOf(false) }
    var showTavernDialog by remember { mutableStateOf(false) }
    var selectedEraOverride by remember { mutableStateOf<Int?>(null) }

    val gachaResultList by viewModel.gachaResultList.collectAsState()

    if (state.gamePhase == "prolog_api" && state.isUnlocked("percikan_api")) {
        Dialog(
            onDismissRequest = {},
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF090514).copy(alpha = 0.96f))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B)),
                    border = BorderStroke(2.dp, Color(0xFFFBBF24)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 620.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "🔥 CAHAYA PERTAMA PERADABAN",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFBBF24)
                            ),
                            textAlign = TextAlign.Center
                        )
                        
                        Text(
                            text = "Sebelum kota-kota megah berdiri, sebelum kerajaan menguasai dunia, bahkan sebelum manusia mengenal logam dan tulisan, hanya ada kegelapan, alam liar, dan perjuangan untuk bertahan hidup.\n\n" +
                                   "Di antara dinginnya malam dan ancaman para predator, manusia menemukan sebuah keajaiban yang mengubah takdir mereka selamanya.\n\n" +
                                   "Api.\n\n" +
                                   "Api bukan sekadar nyala yang menghangatkan tubuh. Ia adalah cahaya pertama yang mengusir ketakutan, senjata pertama yang melindungi, dan alat pertama yang memungkinkan manusia memasak makanan, mengeraskan kayu, serta membentuk kehidupan yang lebih baik.\n\n" +
                                   "Dari satu percikan kecil, lahirlah harapan.\n\n" +
                                   "Di sekeliling api unggun, manusia mulai berkumpul. Mereka berbagi makanan, belajar bekerja sama, menciptakan alat, dan mewariskan pengetahuan dari satu generasi ke generasi berikutnya.\n\n" +
                                   "Sejak saat itu, roda peradaban mulai berputar.\n\n" +
                                   "Kini, perjalanan itu berada di tanganmu.\n\n" +
                                   "Mulailah dari sebuah pulau kecil yang belum tersentuh peradaban. Kumpulkan sumber daya, bangun tempat tinggal, teliti teknologi baru, kalahkan monster yang mengancam, dan bimbing umat manusia melewati setiap zaman.\n\n" +
                                   "Dari api pertama yang menyala...\n" +
                                   "...hingga lahirnya peradaban yang mampu menaklukkan bintang-bintang.\n\n" +
                                   "Selamat datang di Zenith Cosmos.\n\n" +
                                   "Setiap peradaban besar selalu berawal dari satu percikan api.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                lineHeight = 19.sp,
                                color = Color(0xFFE2E8F0)
                            ),
                            textAlign = TextAlign.Justify
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Button(
                            onClick = { viewModel.advancePrologToAnimasi() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA580C)),
                            modifier = Modifier.fillMaxWidth().testTag("continue_prolog_button")
                        ) {
                            Text("Lanjutkan", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (state.gamePhase == "animasi_api" && state.isUnlocked("panggil_manusia")) {
        Dialog(
            onDismissRequest = {},
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF090514).copy(alpha = 0.96f))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B)),
                    border = BorderStroke(2.dp, Color(0xFF22C55E)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 380.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "🚶‍♂️ AWAL PERJALANAN MANUSIA",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF22C55E)
                            ),
                            textAlign = TextAlign.Center
                        )
                        
                        Text(
                            text = "Manusia bukan makhluk terkuat, tetapi mereka memiliki akal dan rasa ingin tahu. Dari kehidupan sederhana sebagai pemburu dan pengumpul, mereka memulai perjalanan panjang yang akhirnya melahirkan peradaban dunia.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                lineHeight = 19.sp,
                                color = Color(0xFFE2E8F0)
                            ),
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Button(
                            onClick = { viewModel.startMainGame() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            modifier = Modifier.fillMaxWidth().testTag("start_civilization_button")
                        ) {
                            Text("Mulai Peradaban", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (afkResult != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearAfkResult() },
            title = { Text("AFK Reward") },
            text = { Text(afkResult!!) },
            confirmButton = {
                Button(onClick = { viewModel.clearAfkResult() }) {
                    Text("OK")
                }
            }
        )
    }

    if (lootResult != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearDungeonLootResult() },
            title = { Text("Dungeon Loot") },
            text = { Text(lootResult!!) },
            confirmButton = {
                Button(onClick = { viewModel.clearDungeonLootResult() }) {
                    Text("OK")
                }
            }
        )
    }

    if (showTalentTreeModal) {
        TalentTreeDialog(state = state, viewModel = viewModel, onDismiss = { showTalentTreeModal = false })
    }

    if (showKomandanFullscreen) {
        KomandanFullscreenDialog(state = state, viewModel = viewModel, onDismiss = { showKomandanFullscreen = false })
    }

    if (showTavernDialog) {
        TavernDialog(state = state, viewModel = viewModel, onDismiss = { showTavernDialog = false })
    }

    if (gachaResultList != null) {
        GachaAnimationOverlay(results = gachaResultList!!, onDismiss = { viewModel.clearGachaResultList() })
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.safeDrawing
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(if (state.isDungeonActive) 0.dp else 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (state.isDungeonActive) {
                DungeonCanvas(
                    state = state,
                    viewModel = viewModel,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF111827))
                )
            } else if (state.isRaidBattleActive) {
                RaidCanvas(
                    state = state,
                    viewModel = viewModel,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF1E1B4B))
                )
            } else {
                if (state.gamePhase == "main_game") {
                    HeaderSection(state, viewModel)

                    // Era Selector Bar
                    val thLevel = state.getBuildingLevel("town_hall")
                    val naturalEra = when {
                        thLevel >= 10 -> 4 // Cyberpunk
                        thLevel >= 7 -> 3  // Modern
                        thLevel >= 3 -> 2  // Kerajaan
                        else -> 1          // Prasejarah
                    }
                    val currentDisplayEra = selectedEraOverride ?: naturalEra

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = when (currentDisplayEra) {
                                4 -> "🌌 Zaman Cyberpunk"
                                3 -> "🏙️ Zaman Modern"
                                2 -> "⚔️ Zaman Kerajaan"
                                else -> "🗿 Zaman Prasejarah"
                            },
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = when (currentDisplayEra) {
                                    4 -> Color(0xFF22D3EE)
                                    3 -> Color(0xFF38BDF8)
                                    2 -> Color(0xFFFBBF24)
                                    else -> Color(0xFF4ADE80)
                                }
                            )
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val eraOptions = listOf(
                                Pair(1, "🗿"),
                                Pair(2, "🏰"),
                                Pair(3, "🏢"),
                                Pair(4, "⚡")
                            )
                            eraOptions.forEach { (eraId, icon) ->
                                val isSelected = currentDisplayEra == eraId
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        selectedEraOverride = if (selectedEraOverride == eraId && naturalEra == eraId) null else eraId
                                    },
                                    label = { Text(icon, fontSize = 12.sp) },
                                    modifier = Modifier.height(28.dp)
                                )
                            }
                            if (selectedEraOverride != null) {
                                TextButton(
                                    onClick = { selectedEraOverride = null },
                                    modifier = Modifier.height(28.dp),
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                                ) {
                                    Text("Auto", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }
                
                // Canvas section for visual representation
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .heightIn(max = 400.dp)
                ) {
                    GameCanvas(
                        viewModel = viewModel, 
                        state = state,
                        overrideEra = selectedEraOverride,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(4.dp))
                            .border(4.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp))
                            .background(Color(0xFF020617))
                    )
                    
                    if (state.gamePhase == "main_game") {
                        if (state.isUnlocked("dungeon")) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(end = 12.dp, bottom = 80.dp)
                                    .size(64.dp)
                                    .clickable { viewModel.startDungeonBattle(state.maxDungeonLevel) }
                                    .testTag("floating_dungeon_btn"),
                                contentAlignment = Alignment.Center
                            ) {
                                val infiniteTransition = androidx.compose.animation.core.rememberInfiniteTransition()
                                val rotation by infiniteTransition.animateFloat(
                                    initialValue = 0f,
                                    targetValue = 360f,
                                    animationSpec = androidx.compose.animation.core.infiniteRepeatable(
                                        animation = androidx.compose.animation.core.tween(3000, easing = androidx.compose.animation.core.LinearEasing),
                                        repeatMode = androidx.compose.animation.core.RepeatMode.Restart
                                    )
                                )
                                val pulse by infiniteTransition.animateFloat(
                                    initialValue = 0.8f,
                                    targetValue = 1.2f,
                                    animationSpec = androidx.compose.animation.core.infiniteRepeatable(
                                        animation = androidx.compose.animation.core.tween(1500, easing = androidx.compose.animation.core.FastOutSlowInEasing),
                                        repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
                                    )
                                )

                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val cx = size.width / 2f
                                    val cy = size.height / 2f
                                    val radius = size.width / 2f - 4f
                                    
                                    // Outer metallic ring
                                    drawCircle(
                                        color = Color(0xFF334155),
                                        radius = radius,
                                        center = Offset(cx, cy),
                                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 6f)
                                    )
                                    drawCircle(
                                        color = Color(0xFF94A3B8),
                                        radius = radius - 2f,
                                        center = Offset(cx, cy),
                                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
                                    )
                                    
                                    // Vortex
                                    withTransform({
                                        rotate(rotation, Offset(cx, cy))
                                    }) {
                                        for (i in 0 until 5) {
                                            val ang = i * (360f / 5f)
                                            val rad = Math.toRadians(ang.toDouble())
                                            val px = cx + kotlin.math.cos(rad).toFloat() * (radius * 0.4f)
                                            val py = cy + kotlin.math.sin(rad).toFloat() * (radius * 0.4f)
                                            drawCircle(
                                                color = Color(0xFF8B5CF6).copy(alpha = 0.6f),
                                                radius = radius * 0.6f * pulse,
                                                center = Offset(px, py)
                                            )
                                            drawCircle(
                                                color = Color(0xFF06B6D4).copy(alpha = 0.8f),
                                                radius = radius * 0.3f * pulse,
                                                center = Offset(px, py)
                                            )
                                        }
                                        drawCircle(
                                            color = Color.Black,
                                            radius = radius * 0.2f,
                                            center = Offset(cx, cy)
                                        )
                                    }
                                    
                                    // Lightning particles
                                    val t = System.currentTimeMillis() / 1000f
                                    for (i in 0 until 6) {
                                        val offset = (i * 13f) % 360f
                                        val speed = 2f
                                        val ang = (t * speed * 50f + offset) % 360f
                                        val rad = Math.toRadians(ang.toDouble())
                                        val dist = radius * 0.8f * (0.5f + 0.5f * kotlin.math.sin(t * 3f + i))
                                        val px = cx + kotlin.math.cos(rad).toFloat() * dist
                                        val py = cy + kotlin.math.sin(rad).toFloat() * dist
                                        drawCircle(
                                            color = Color(0xFF38BDF8),
                                            radius = 3f * pulse,
                                            center = Offset(px, py)
                                        )
                                    }
                                }
                            }
                        }
                        
                        IconButton(
                            onClick = { showTalentTreeModal = true },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(12.dp)
                                .size(56.dp)
                                .background(Color(0xFF7C3AED), CircleShape)
                                .border(2.dp, Color(0xFFFBBF24), CircleShape)
                                .testTag("floating_talent_tree_btn")
                        ) {
                            Text("🧬", fontSize = 24.sp)
                        }
                    }
                }
                
                // Tabs and Actions
                TabsSection(
                    state = state,
                    viewModel = viewModel,
                    modifier = Modifier.weight(1f),
                    onShowKomandan = { showKomandanFullscreen = true },
                    onShowTavern = { showTavernDialog = true },
                    onShowTalentTree = { showTalentTreeModal = true }
                )
            }
        }
    }
}

@Composable
fun HeaderSection(state: GameState, viewModel: GameViewModel) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Baris atas: Zenith + tombol debug
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "✨ ${formatNumber(state.totalZenith.toLong())} Z",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFFBBF24)
                )
            )
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                OutlinedButton(
                    onClick = { viewModel.sandboxGodMode() },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.height(28.dp),
                    border = BorderStroke(1.dp, Color(0xFF9333EA))
                ) {
                    Text("🛠️ Dewa", fontSize = 10.sp, color = Color(0xFFA855F7))
                }
                OutlinedButton(
                    onClick = { viewModel.activateMaxAllMode() },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.height(28.dp),
                    border = BorderStroke(1.dp, Color(0xFFEF4444))
                ) {
                    Text("⚡ MAX", fontSize = 10.sp, color = Color(0xFFEF4444))
                }
            }
        }

        // Baris status: Stats ringkas dalam satu row yang adaptif
        val weaponName: String
        val damage: Long
        when {
            state.isUnlocked("planet_breaker") -> { weaponName = "Planet Breaker"; damage = 400000L }
            state.isUnlocked("energy_katana") -> { weaponName = "Energy Katana"; damage = 76000L }
            state.isUnlocked("assault_rifle") -> { weaponName = "Assault Rifle"; damage = 12500L }
            state.isUnlocked("musket") -> { weaponName = "Musket"; damage = 6000L }
            state.isUnlocked("pedang_besi") -> { weaponName = "Pedang Besi"; damage = 980L }
            state.isUnlocked("tongkat_kayu") -> { weaponName = "Tongkat Kayu"; damage = 180L }
            else -> { weaponName = "Tangan Kosong"; damage = 100L }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "⚔️ ${formatNumber(damage)}",
                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFF87171)),
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (state.isUnlocked("tembok")) {
                Text(
                    text = "⏱️ Raid: ${state.raidTimer}s",
                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFFBBF24)),
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "🪙 ${formatNumber(state.gameCoins)}",
                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFFBBF24)),
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

fun DrawScope.drawMonster(x: Float, y: Float, name: String, time: Float) {
    val walkOffset = sin(time * 0.4f) * 3f
    val shadowColor = Color(0x33000000)
    drawOval(shadowColor, topLeft = Offset(x - 6f, y + 20f), size = Size(24f, 8f))

    when {
        name.contains("Serigala") -> {
            val bodyColor = Color(0xFF4B5563)
            val eyeColor = Color(0xFFEF4444)
            drawRect(bodyColor, topLeft = Offset(x - 8f, y + 4f + walkOffset), size = Size(22f, 12f))
            drawRect(bodyColor, topLeft = Offset(x - 12f, y - 4f + walkOffset), size = Size(10f, 10f))
            drawRect(bodyColor, topLeft = Offset(x - 16f, y + 1f + walkOffset), size = Size(5f, 4f))
            drawRect(bodyColor, topLeft = Offset(x - 11f, y - 8f + walkOffset), size = Size(3f, 4f))
            drawRect(bodyColor, topLeft = Offset(x - 6f, y - 8f + walkOffset), size = Size(3f, 4f))
            drawRect(eyeColor, topLeft = Offset(x - 10f, y - 2f + walkOffset), size = Size(2f, 2f))
            drawRect(bodyColor, topLeft = Offset(x - 6f, y + 14f), size = Size(3f, 6f))
            drawRect(bodyColor, topLeft = Offset(x + 4f, y + 14f), size = Size(3f, 6f))
            drawRect(bodyColor, topLeft = Offset(x + 12f, y + 6f + walkOffset), size = Size(6f, 4f))
        }
        name.contains("Goblin") -> {
            val skinColor = Color(0xFF22C55E)
            val eyesColor = Color(0xFFFFEB3B)
            drawRect(skinColor, topLeft = Offset(x, y - 10f + walkOffset), size = Size(12f, 10f))
            drawRect(skinColor, topLeft = Offset(x - 4f, y - 8f + walkOffset), size = Size(4f, 3f))
            drawRect(skinColor, topLeft = Offset(x + 12f, y - 8f + walkOffset), size = Size(4f, 3f))
            drawRect(eyesColor, topLeft = Offset(x + 2f, y - 6f + walkOffset), size = Size(2f, 2f))
            drawRect(eyesColor, topLeft = Offset(x + 8f, y - 6f + walkOffset), size = Size(2f, 2f))
            drawRect(Color(0xFF78350F), topLeft = Offset(x - 1f, y + walkOffset), size = Size(14f, 14f))
            drawRect(skinColor, topLeft = Offset(x + 1f, y + 14f), size = Size(3f, 6f))
            drawRect(skinColor, topLeft = Offset(x + 8f, y + 14f), size = Size(3f, 6f))
            drawRect(Color(0xFF5C4033), topLeft = Offset(x - 4f, y + 2f + walkOffset), size = Size(4f, 12f))
        }
        name.contains("Orc") || name.contains("Ogre") -> {
            val skinColor = if (name.contains("Ogre")) Color(0xFF3B82F6) else Color(0xFF15803D)
            val eyeColor = Color(0xFFEF4444)
            drawRect(skinColor, topLeft = Offset(x - 2f, y - 14f + walkOffset), size = Size(16f, 14f))
            drawRect(eyeColor, topLeft = Offset(x + 1f, y - 9f + walkOffset), size = Size(2f, 2f))
            drawRect(eyeColor, topLeft = Offset(x + 9f, y - 9f + walkOffset), size = Size(2f, 2f))
            drawRect(Color.White, topLeft = Offset(x + 3f, y - 4f + walkOffset), size = Size(2f, 2f))
            drawRect(Color.White, topLeft = Offset(x + 7f, y - 4f + walkOffset), size = Size(2f, 2f))
            drawRect(Color(0xFF1E293B), topLeft = Offset(x - 6f, y + walkOffset), size = Size(24f, 18f))
            drawRect(Color(0xFF94A3B8), topLeft = Offset(x - 8f, y - 2f + walkOffset), size = Size(6f, 4f))
            drawRect(Color(0xFF94A3B8), topLeft = Offset(x + 14f, y - 2f + walkOffset), size = Size(6f, 4f))
            drawRect(skinColor, topLeft = Offset(x - 2f, y + 18f), size = Size(5f, 6f))
            drawRect(skinColor, topLeft = Offset(x + 9f, y + 18f), size = Size(5f, 6f))
            drawRect(Color(0xFF475569), topLeft = Offset(x - 12f, y - 6f + walkOffset), size = Size(6f, 16f))
            drawRect(Color(0xFF78350F), topLeft = Offset(x - 10f, y - 12f + walkOffset), size = Size(2f, 32f))
        }
        else -> {
            val bodyColor = Color(0xFF111827)
            val glowColor = Color(0xFFF43F5E)
            drawRect(bodyColor, topLeft = Offset(x - 4f, y - 18f + walkOffset), size = Size(20f, 18f))
            drawRect(glowColor, topLeft = Offset(x, y - 12f + walkOffset), size = Size(3f, 3f))
            drawRect(glowColor, topLeft = Offset(x + 9f, y - 12f + walkOffset), size = Size(3f, 3f))
            drawRect(Color.Black, topLeft = Offset(x - 6f, y - 24f + walkOffset), size = Size(4f, 8f))
            drawRect(Color.Black, topLeft = Offset(x + 14f, y - 24f + walkOffset), size = Size(4f, 8f))
            drawRect(bodyColor, topLeft = Offset(x - 10f, y + walkOffset), size = Size(32f, 22f))
            drawRect(Color(0xFFA855F7), topLeft = Offset(x - 2f, y + 4f + walkOffset), size = Size(16f, 12f))
            drawRect(bodyColor, topLeft = Offset(x - 4f, y + 22f), size = Size(6f, 6f))
            drawRect(bodyColor, topLeft = Offset(x + 10f, y + 22f), size = Size(6f, 6f))
            drawRect(Color(0xFF6B21A8), topLeft = Offset(x - 24f, y - 10f + walkOffset), size = Size(14f, 16f))
            drawRect(Color(0xFF6B21A8), topLeft = Offset(x + 22f, y - 10f + walkOffset), size = Size(14f, 16f))
        }
    }
}

fun DrawScope.drawRPGPlayer(
    x: Float,
    y: Float,
    heroName: String,
    time: Float,
    isAttacking: Boolean,
    attackProgress: Float
) {
    val walkOffset = if (!isAttacking) sin(time * 0.4f) * 3f else 0f
    val attackOffset = if (isAttacking) attackProgress * 15f else 0f
    
    val shadowColor = Color(0x33000000)
    drawOval(shadowColor, topLeft = Offset(x - 20f, y + 25f), size = Size(40f, 10f))
    
    withTransform({
        translate(x + attackOffset - size.width / 2f, y + walkOffset - size.height / 2f)
    }) {
        drawChibiHero(heroName, true, time)
    }
}

fun DrawScope.drawRPGTroop(
    x: Float,
    y: Float,
    time: Float,
    isAttacking: Boolean,
    attackProgress: Float,
    troopIndex: Int = 0,
    era: Int = 1
) {
    val walkOffset = if (!isAttacking) sin(time * 0.4f + x) * 3f else 0f
    val shadowColor = Color(0x33000000)
    drawOval(shadowColor, topLeft = Offset(x - 4f, y + 20f), size = Size(18f, 6f))

    if (era <= 2) {
        // Prehistoric Era Troops: Pasukan Kapak Batu (Hunter) & Pasukan Tombak (Gatherer)
        val isAxeHunter = (troopIndex % 2 == 0)
        val skinColor = Color(0xFFF6C59E)
        
        if (isAxeHunter) {
            // 🪓 PASUKAN KAPAK BATU (Stone Axe Hunter)
            val hairColor = Color(0xFF2A1F1D)
            val peltColor = Color(0xFFD97706)
            val peltSpot = Color(0xFF451A03)
            
            // Wild shaggy hair
            drawRect(hairColor, topLeft = Offset(x - 2f, y - 16f + walkOffset), size = Size(15f, 6f))
            drawRect(hairColor, topLeft = Offset(x - 3f, y - 12f + walkOffset), size = Size(4f, 8f))
            // Face
            drawRect(skinColor, topLeft = Offset(x + 2f, y - 11f + walkOffset), size = Size(8f, 7f))
            // Beard/stubble
            drawRect(hairColor, topLeft = Offset(x + 2f, y - 4f + walkOffset), size = Size(8f, 3f))
            // Eye
            drawRect(Color.Black, topLeft = Offset(x + 6f, y - 9f + walkOffset), size = Size(2f, 2f))
            // Animal pelt tunic with spots
            drawRect(peltColor, topLeft = Offset(x, y - 1f + walkOffset), size = Size(12f, 13f))
            drawRect(peltSpot, topLeft = Offset(x + 3f, y + 2f + walkOffset), size = Size(2f, 2f))
            drawRect(peltSpot, topLeft = Offset(x + 7f, y + 6f + walkOffset), size = Size(2f, 2f))
            // Legs
            drawRect(Color(0xFFD79A6D), topLeft = Offset(x + 1f, y + 12f), size = Size(3f, 8f))
            drawRect(Color(0xFFD79A6D), topLeft = Offset(x + 7f, y + 12f), size = Size(3f, 8f))

            // Weapon: Heavy Prehistoric Stone Axe
            val axeHaftX = x + 11f + (if (isAttacking) attackProgress * 8f else 0f)
            val axeHaftY = y + 2f + walkOffset
            
            // Wooden haft
            drawLine(
                color = Color(0xFF78471D),
                start = Offset(axeHaftX, axeHaftY + 12f),
                end = Offset(axeHaftX + 4f, axeHaftY - 8f),
                strokeWidth = 3f
            )
            // Knapped grey flint stone head
            val axeHeadX = axeHaftX + 4f
            val axeHeadY = axeHaftY - 8f
            drawRect(Color(0xFF5F6368), topLeft = Offset(axeHeadX - 2f, axeHeadY - 4f), size = Size(8f, 8f))
            drawRect(Color(0xFF9AA0A6), topLeft = Offset(axeHeadX, axeHeadY - 3f), size = Size(5f, 6f))
            // Raw cord binding
            drawLine(Color(0xFFF3EFE0), Offset(axeHeadX - 1f, axeHeadY), Offset(axeHeadX + 2f, axeHeadY + 2f), 1.5f)

            if (isAttacking && attackProgress > 0.3f) {
                // Melee chop swing slash
                drawArc(
                    color = Color(0xFFE2E8F0).copy(alpha = 0.7f),
                    startAngle = 300f,
                    sweepAngle = 75f,
                    useCenter = false,
                    topLeft = Offset(axeHeadX - 4f, axeHeadY - 10f),
                    size = Size(20f, 20f),
                    style = Stroke(width = 2.5f)
                )
            }
        } else {
            // 🗡️ PASUKAN TOMBAK (Spear Gatherer)
            val hairColor = Color(0xFFC25227)
            val leafColor = Color(0xFF489436)
            
            // Topknot bun with bone hairpin
            drawCircle(hairColor, radius = 4f, center = Offset(x + 4f, y - 16f + walkOffset))
            drawLine(Color(0xFFF3EFE0), Offset(x - 1f, y - 18f + walkOffset), Offset(x + 9f, y - 14f + walkOffset), 2f)
            // Hair frame
            drawRect(hairColor, topLeft = Offset(x, y - 13f + walkOffset), size = Size(10f, 5f))
            // Face
            drawRect(skinColor, topLeft = Offset(x + 2f, y - 9f + walkOffset), size = Size(7f, 6f))
            // Eye
            drawRect(Color.Black, topLeft = Offset(x + 6f, y - 7f + walkOffset), size = Size(1.5f, 1.5f))
            // Leaf tunic
            drawRect(leafColor, topLeft = Offset(x + 1f, y - 1f + walkOffset), size = Size(10f, 13f))
            drawRect(Color(0xFF27611A), topLeft = Offset(x + 1f, y + 9f + walkOffset), size = Size(10f, 3f))
            // Legs
            drawRect(skinColor, topLeft = Offset(x + 2f, y + 12f), size = Size(3f, 8f))
            drawRect(skinColor, topLeft = Offset(x + 6f, y + 12f), size = Size(3f, 8f))

            // Weapon: Long Flint Stone Spear
            val spearThrustX = if (isAttacking) attackProgress * 16f else 0f
            val shaftStartX = x + 7f + spearThrustX
            val shaftStartY = y + 8f + walkOffset
            val shaftEndX = x + 24f + spearThrustX
            val shaftEndY = y - 8f + walkOffset

            // Wooden Spear shaft
            drawLine(
                color = Color(0xFF78471D),
                start = Offset(shaftStartX, shaftStartY),
                end = Offset(shaftEndX, shaftEndY),
                strokeWidth = 2.5f
            )
            // Pointed stone spearhead
            val spearTip = Path().apply {
                moveTo(shaftEndX + 6f, shaftEndY - 3f)
                lineTo(shaftEndX - 2f, shaftEndY + 3f)
                lineTo(shaftEndX + 1f, shaftEndY - 5f)
                close()
            }
            drawPath(spearTip, Color(0xFF9AA0A6))
            drawCircle(Color(0xFF5F6368), radius = 2f, center = Offset(shaftEndX + 2f, shaftEndY - 2f))
            drawLine(Color(0xFFF3EFE0), Offset(shaftEndX - 2f, shaftEndY + 1f), Offset(shaftEndX + 1f, shaftEndY - 2f), 1.5f)
        }
    } else {
        // Later Era Armed Infantry
        val skinColor = Color(0xFFFFB6C1)
        val helmColor = Color(0xFF64748B)
        val bodyColor = Color(0xFF059669)
        drawRect(helmColor, topLeft = Offset(x, y - 12f + walkOffset), size = Size(10f, 10f))
        drawRect(helmColor, topLeft = Offset(x - 1f, y - 14f + walkOffset), size = Size(12f, 3f))
        drawRect(skinColor, topLeft = Offset(x + 4f, y - 8f + walkOffset), size = Size(6f, 6f))
        drawRect(Color.Black, topLeft = Offset(x + 7f, y - 6f + walkOffset), size = Size(1.5f, 1.5f))
        drawRect(bodyColor, topLeft = Offset(x - 1f, y + walkOffset), size = Size(12f, 12f))
        drawRect(Color(0xFF1E293B), topLeft = Offset(x + 1f, y + 12f), size = Size(3f, 8f))
        drawRect(Color(0xFF1E293B), topLeft = Offset(x + 6f, y + 12f), size = Size(3f, 8f))
        val spearThrustX = if (isAttacking) attackProgress * 12f else 0f
        drawLine(
            color = Color(0xFF94A3B8),
            start = Offset(x + 10f + spearThrustX, y + 4f + walkOffset),
            end = Offset(x + 22f + spearThrustX, y - 6f + walkOffset),
            strokeWidth = 2f
        )
        drawCircle(Color(0xFFCBD5E1), radius = 2.5f, center = Offset(x + 22f + spearThrustX, y - 6f + walkOffset))
    }
}

@Composable
fun DungeonCanvas(state: GameState, viewModel: GameViewModel, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition()
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    
    val logs by viewModel.dungeonLogs.collectAsState()
    val listState = rememberLazyListState()
    
    LaunchedEffect(logs.size) {
        if (logs.isNotEmpty()) {
            listState.animateScrollToItem(logs.size - 1)
        }
    }

    var weaponId = "none"
    if (state.isUnlocked("planet_breaker")) { weaponId = "planet_breaker" }
    else if (state.isUnlocked("energy_katana")) { weaponId = "energy_katana" }
    else if (state.isUnlocked("assault_rifle")) { weaponId = "assault_rifle" }
    else if (state.isUnlocked("musket")) { weaponId = "musket" }
    else if (state.isUnlocked("pedang_besi")) { weaponId = "pedang_besi" }
    else if (state.isUnlocked("tongkat_kayu")) { weaponId = "tongkat_kayu" }

    var armorId = "none"
    if (state.isUnlocked("cyber_body")) { armorId = "cyber_body" }
    else if (state.isUnlocked("nano_armor")) { armorId = "nano_armor" }
    else if (state.isUnlocked("exo_suit")) { armorId = "exo_suit" }
    else if (state.isUnlocked("armor_kevlar")) { armorId = "armor_kevlar" }
    else if (state.isUnlocked("armor_besi")) { armorId = "armor_besi" }
    else if (state.isUnlocked("kulit_hewan")) { armorId = "kulit_hewan" }

    val monsterInfo = com.example.ui.game.getMonsterInfo(state.currentDungeonLevel)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF030712))
    ) {
        Box(
            modifier = Modifier
                .weight(0.6f)
                .fillMaxWidth()
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cx = size.width / 2
                val groundY = size.height * 0.75f
                
                drawRect(Color(0xFF38BDF8), topLeft = Offset(0f, 0f), size = Size(size.width, groundY))
                
                val cloud1X = (time * 1.5f) % (size.width + 120f) - 60f
                drawCircle(Color.White.copy(alpha = 0.85f), radius = 22f, center = Offset(cloud1X, groundY * 0.2f))
                drawCircle(Color.White.copy(alpha = 0.85f), radius = 30f, center = Offset(cloud1X + 25f, groundY * 0.25f))
                drawCircle(Color.White.copy(alpha = 0.85f), radius = 22f, center = Offset(cloud1X + 50f, groundY * 0.2f))
                
                val cloud2X = ((time * 0.8f) + 300f) % (size.width + 160f) - 80f
                drawCircle(Color.White.copy(alpha = 0.6f), radius = 35f, center = Offset(cloud2X, groundY * 0.4f))
                drawCircle(Color.White.copy(alpha = 0.6f), radius = 45f, center = Offset(cloud2X + 35f, groundY * 0.45f))
                
                val seaHeight = groundY * 0.25f
                val seaTop = groundY - seaHeight
                drawRect(Color(0xFF0284C7), topLeft = Offset(0f, seaTop), size = Size(size.width, seaHeight))
                
                val waveOffset = (time * 2f) % 120f
                for (wx in -120..size.width.toInt() step 120) {
                    val finalWx = wx.toFloat() + waveOffset
                    drawLine(Color.White.copy(alpha = 0.4f), start = Offset(finalWx, seaTop + 15f), end = Offset(finalWx + 40f, seaTop + 15f), strokeWidth = 2f)
                    drawLine(Color.White.copy(alpha = 0.3f), start = Offset(finalWx - 60f, seaTop + seaHeight - 20f), end = Offset(finalWx - 20f, seaTop + seaHeight - 20f), strokeWidth = 1.5f)
                }
                
                drawRect(Color(0xFF22C55E), topLeft = Offset(0f, groundY), size = Size(size.width, size.height - groundY))
                
                val pathHeight = 45f
                drawRect(Color(0xFFEAB308).copy(alpha = 0.85f), topLeft = Offset(0f, groundY), size = Size(size.width, pathHeight))
                
                for (fx in 0..size.width.toInt() step 80) {
                    drawRect(Color(0xFF78350F), topLeft = Offset(fx.toFloat(), groundY - 14f), size = Size(5f, 14f))
                    drawRect(Color(0xFF78350F), topLeft = Offset(fx.toFloat() - 2f, groundY - 10f), size = Size(9f, 3f))
                    drawLine(Color(0xFFB45309), start = Offset(fx.toFloat(), groundY - 8f), end = Offset(fx.toFloat() + 80f, groundY - 8f), strokeWidth = 2f)
                }
                
                val battleCycle = (time * 1.5f) % 100f
                val startX = 60f
                val endX = size.width - 60f
                val playerClashX = cx - 45f
                val enemyClashX = cx + 45f
                
                var playerX = startX
                var enemyX = endX
                var isMoving = true
                var isAttacking = false
                var playerAttackProgress = 0f
                var enemyAttackProgress = 0f
                
                when {
                    battleCycle < 40f -> {
                        val t = (battleCycle / 40f).coerceIn(0f, 1f)
                        playerX = startX + (playerClashX - startX) * t
                        enemyX = endX - (endX - enemyClashX) * t
                        isMoving = true
                        isAttacking = false
                    }
                    battleCycle >= 40f && battleCycle <= 80f -> {
                        playerX = playerClashX
                        enemyX = enemyClashX
                        isMoving = false
                        isAttacking = true
                        
                        if (battleCycle in 43f..55f) {
                            playerAttackProgress = (battleCycle - 43f) / 12f
                        }
                        if (battleCycle in 58f..70f) {
                            enemyAttackProgress = (battleCycle - 58f) / 12f
                        }
                    }
                    else -> {
                        val t = ((battleCycle - 80f) / 20f).coerceIn(0f, 1f)
                        playerX = playerClashX + (startX - playerClashX) * t
                        enemyX = enemyClashX + (endX - enemyClashX) * t
                        isMoving = true
                        isAttacking = false
                    }
                }
                
                val activeHeroName = if (state.garrisonedHeroes.isNotEmpty()) {
                    state.garrisonedHeroes[0]
                } else if (state.ownedCommanders.isNotEmpty()) {
                    state.ownedCommanders[0].split(":")[0]
                } else {
                    "prajurit_biasa"
                }
                
                val heroY = groundY - 18f
                drawRPGPlayer(
                    x = playerX,
                    y = heroY,
                    heroName = activeHeroName,
                    time = time,
                    isAttacking = isAttacking && playerAttackProgress > 0f,
                    attackProgress = playerAttackProgress
                )
                
                val maxPasukan = minOf(state.currentPasukan, 4L).toInt()
                val currentEra = if (state.getBuildingLevel("town_hall") >= 5) 3 else 1
                for (i in 0 until maxPasukan) {
                    val troopX = playerX - 35f - (i * 22f)
                    val troopY = heroY + 8f + (i % 2 * 6f)
                    if (troopX > 0f) {
                        drawRPGTroop(
                            x = troopX,
                            y = troopY,
                            time = time,
                            isAttacking = isAttacking && playerAttackProgress > 0f,
                            attackProgress = playerAttackProgress,
                            troopIndex = i,
                            era = currentEra
                        )
                    }
                }
                
                val isMonsterDefeated = state.monsterHp <= 0
                val enemyY = groundY + 15f
                if (!isMonsterDefeated || battleCycle < 75f) {
                    drawMonster(
                        x = enemyX,
                        y = enemyY,
                        name = monsterInfo.name,
                        time = time
                    )
                }
                
                if (isAttacking) {
                    if (battleCycle in 48f..56f) {
                        val sa = (battleCycle - 48f) / 8f
                        drawCircle(Color.White.copy(alpha = 1f - sa), radius = 20f * sa, center = Offset(enemyClashX - 5f, enemyY))
                        drawCircle(Color(0xFF22D3EE).copy(alpha = 0.8f * (1f - sa)), radius = 30f * sa, center = Offset(enemyClashX - 5f, enemyY), style = Stroke(width = 3f))
                    }
                    if (battleCycle in 62f..70f) {
                        val sa = (battleCycle - 62f) / 8f
                        drawCircle(Color(0xFFF43F5E).copy(alpha = 1f - sa), radius = 18f * sa, center = Offset(playerClashX + 10f, heroY))
                        drawLine(
                            color = Color.White.copy(alpha = 1f - sa),
                            start = Offset(playerClashX - 10f, heroY - 10f),
                            end = Offset(playerClashX + 20f, heroY + 10f),
                            strokeWidth = 4f
                        )
                    }
                }
                
                val paint = Paint().apply {
                    textAlign = Paint.Align.CENTER
                    typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
                }
                
                if (battleCycle in 48f..70f) {
                    val p = (battleCycle - 48f) / 22f
                    val alpha = ((1f - p) * 255).toInt().coerceIn(0, 255)
                    paint.color = android.graphics.Color.argb(alpha, 239, 68, 68)
                    paint.textSize = 34f
                    drawContext.canvas.nativeCanvas.drawText("-${state.damage}", enemyClashX, enemyY - 25f - p * 50f, paint)
                }
                
                if (battleCycle in 63f..85f) {
                    val p = (battleCycle - 63f) / 22f
                    val alpha = ((1f - p) * 255).toInt().coerceIn(0, 255)
                    paint.color = android.graphics.Color.argb(alpha, 244, 63, 94)
                    paint.textSize = 34f
                    drawContext.canvas.nativeCanvas.drawText("-${monsterInfo.atk}", playerClashX, heroY - 25f - p * 50f, paint)
                }
                
                if (battleCycle in 75f..96f) {
                    val p = (battleCycle - 75f) / 21f
                    val alpha = ((1f - p) * 255).toInt().coerceIn(0, 255)
                    
                    paint.color = android.graphics.Color.argb(alpha, 251, 191, 36)
                    paint.textSize = 30f
                    drawContext.canvas.nativeCanvas.drawText("+${state.currentDungeonLevel * 10} EXP", enemyClashX, enemyY - 30f - p * 45f, paint)
                    
                    drawCircle(
                        color = Color.LightGray.copy(alpha = 1f - p),
                        radius = 8f + p * 20f,
                        center = Offset(enemyClashX + sin(p * 10f) * 15f, enemyY - 5f - p * 30f)
                    )
                    drawCircle(
                        color = Color.Gray.copy(alpha = (1f - p) * 0.7f),
                        radius = 5f + p * 15f,
                        center = Offset(enemyClashX - cos(p * 12f) * 12f, enemyY - p * 25f)
                    )
                }
            }
            
            Text(
                text = "⚔️ DUNGEON FL. ${state.currentDungeonLevel} ⚔️",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 12.sp,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(8.dp)
                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
        
        Column(
            modifier = Modifier
                .weight(0.4f)
                .fillMaxWidth()
                .background(Color(0xFF0F172A))
                .border(BorderStroke(1.dp, Color(0xFF334155)))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    val hpPct = (state.playerDungeonHp.toFloat() / Math.max(state.maxGerbangHp.toFloat(), 1f)).coerceIn(0f, 1f)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("❤️ TOTAL HP TIM", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                        Text("${state.playerDungeonHp} / ${state.maxGerbangHp}", fontSize = 10.sp, color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(14.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.DarkGray)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(hpPct)
                                .fillMaxHeight()
                                .background(Color(0xFFEF4444))
                        )
                    }
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    val stamina = ((time * 3f) % 100f) / 100f
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("💧 MANA PASUKAN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3B82F6))
                        Text("${(stamina * 100).toInt()}%", fontSize = 10.sp, color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(14.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.DarkGray)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(stamina)
                                .fillMaxHeight()
                                .background(Color(0xFF3B82F6))
                        )
                    }
                }
            }
            
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF020617))
                    .border(BorderStroke(1.dp, Color(0xFF1E293B)), RoundedCornerShape(6.dp))
                    .padding(8.dp)
            ) {
                LazyColumn(state = listState) {
                    items(logs) { log ->
                        Text(
                            text = "💬 $log",
                            color = if (log.contains("menang", ignoreCase = true)) Color(0xFFFBBF24)
                                    else if (log.contains("kalah", ignoreCase = true)) Color(0xFFEF4444)
                                    else Color.LightGray,
                            fontSize = 11.sp,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { viewModel.stopDungeonBattle() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text("🏳️ Mundur", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.weight(1f)
                ) {
                    androidx.compose.material3.Checkbox(
                        checked = state.autoFarmDungeon,
                        onCheckedChange = { viewModel.toggleAutoFarm(it) },
                        colors = androidx.compose.material3.CheckboxDefaults.colors(checkedColor = Color(0xFF10B981))
                    )
                    Text("🔄 Otomatis Next Round", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
                
                if (state.hpPotions > 0) {
                    Button(
                        onClick = { viewModel.useHpPotion() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text("🧪 Potion (${state.hpPotions})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun GameCanvas(
    state: GameState, 
    viewModel: GameViewModel, 
    modifier: Modifier = Modifier,
    overrideEra: Int? = null
) {
    val upgradingBuildings by viewModel.upgradingBuildings.collectAsState()
    val infiniteTransition = rememberInfiniteTransition(label = "canvas_animation")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    val fireTransition = rememberInfiniteTransition(label = "fire_animation")
    val fireTime by fireTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "fireTime"
    )

    val thLevel = state.getBuildingLevel("town_hall")
    val naturalEra = when {
        thLevel >= 10 -> 4 // Cyberpunk / Cosmos
        thLevel >= 7 -> 3  // Modern
        thLevel >= 3 -> 2  // Kerajaan
        else -> 1          // Prasejarah
    }
    val activeEra = overrideEra ?: naturalEra

    val (wildTrees, wildStones) = remember { generateWilderness() }

    Canvas(
        modifier = modifier.pointerInput(state, activeEra) {
            detectTapGestures { offset ->
                if (state.gamePhase != "main_game") return@detectTapGestures
                val nx = offset.x / size.width
                val ny = offset.y / size.height

                if (kotlin.math.hypot(nx - 0.50f, ny - 0.42f) < 0.10f) {
                    if (state.isUnlocked("town_hall")) {
                        val thTitle = when (activeEra) {
                            4 -> "🏙️ Cyber Spire Citadel"
                            3 -> "🏛️ Balai Kota Metropolitan"
                            2 -> "🏰 Kastil Kerajaan Megah"
                            else -> "🏛️ Town Hall Prasejarah"
                        }
                        val thDesc = when (activeEra) {
                            4 -> "Pusat Megapolis & Inti Kristal Quantum Energi!"
                            3 -> "Pusat Kebijakan Publik & Layanan Kota Modern!"
                            2 -> "Takhta Utama dan Pusat Pemerintahan Kerajaan!"
                            else -> "Pusat Kepemimpinan dan Musyawarah Suku!"
                        }
                        viewModel.showAlert("$thTitle (Lv. ${state.getBuildingLevel("town_hall")}): $thDesc")
                    }
                } else if (kotlin.math.hypot(nx - 0.50f, ny - 0.52f) < 0.08f) {
                    if (state.isUnlocked("barter_purba")) {
                        val plazaMsg = when (activeEra) {
                            4 -> "💎 Air Mancur Hologram Quantum: Kristal holo berputar memproyeksikan data bursa koin."
                            3 -> "🗽 Monumen & Air Mancur Sipil: Tempat berkumpul warga kota yang asri dan modern."
                            2 -> "⚔️ Monumen Ksatria & Air Mancur: Patung ksatria agung dan air mancur batu bertingkat."
                            else -> "🔥 Api Unggun Suku: Warga berkumpul saling bertukar cerita dan menghangatkan diri."
                        }
                        viewModel.showAlert(plazaMsg)
                    }
                } else if (kotlin.math.hypot(nx - 0.23f, ny - 0.15f) < 0.08f) {
                    if (state.isUnlocked("penebang_kayu")) {
                        val woodTitle = when (activeEra) {
                            4 -> "⚡ Kilang Fusi Plasma"
                            3 -> "🚛 Depo Kayu & Logistik Industri"
                            2 -> "🪵 Kilang Kayu Kerajaan"
                            else -> "🪓 Kamp Penebang Kayu"
                        }
                        val woodDesc = when (activeEra) {
                            4 -> "Memproses reaktor energi amber & jalur monorail."
                            3 -> "Gudang material kayu & distribusi forklift modern."
                            2 -> "Memotong balok kayu istana & perisai tempur."
                            else -> "Memproduksi kayu gelondongan untuk suku."
                        }
                        viewModel.showAlert("$woodTitle (Lv. ${state.getBuildingLevel("penebang_kayu")}): $woodDesc")
                    }
                } else if (kotlin.math.hypot(nx - 0.78f, ny - 0.15f) < 0.08f) {
                    if (state.isUnlocked("pemecah_batu")) {
                        val stoneTitle = when (activeEra) {
                            4 -> "💎 Tambang Kristal Siber"
                            3 -> "🏗️ Tambang Agregat Modern"
                            2 -> "⛏️ Tambang Batu Kerajaan"
                            else -> "⛏️ Tambang Batu Purba"
                        }
                        val stoneDesc = when (activeEra) {
                            4 -> "Ekskavasi kristal neon cyan bertenaga laser."
                            3 -> "Ekskavator hidrolik & konveyor pemecah batu."
                            2 -> "Memahat batu ashlar kokoh dinding benteng."
                            else -> "Memahat batu tebing kokoh untuk perkakas."
                        }
                        viewModel.showAlert("$stoneTitle (Lv. ${state.getBuildingLevel("pemecah_batu")}): $stoneDesc")
                    }
                } else if (kotlin.math.hypot(nx - 0.18f, ny - 0.31f) < 0.08f) {
                    if (state.isUnlocked("pertanian")) {
                        val farmTitle = when (activeEra) {
                            4 -> "🧬 Menara Bio-Hydroponik"
                            3 -> "🌱 Greenhouse Hidroponik"
                            2 -> "🌾 Ladang Gandum & Kincir Angin"
                            else -> "🌾 Ladang Pertanian"
                        }
                        val farmDesc = when (activeEra) {
                            4 -> "Pertanian vertikal dengan lampu LED luminous."
                            3 -> "Tanaman pangan modern terkontrol suhu."
                            2 -> "Lumbung gandum & tepung roti kerajaan."
                            else -> "Pasokan gandum dan sayuran suku."
                        }
                        viewModel.showAlert("$farmTitle (Lv. ${state.getBuildingLevel("pertanian")}): $farmDesc")
                    }
                } else if (kotlin.math.hypot(nx - 0.82f, ny - 0.31f) < 0.08f) {
                    if (state.isUnlocked("perkebunan")) {
                        val orchTitle = when (activeEra) {
                            4 -> "🍏 Kebun Buah Bio-Sintesis"
                            3 -> "🍎 Kebun Apel & Buah Segar"
                            2 -> "🍎 Kebun Apel Kerajaan"
                            else -> "🍎 Kebun Buah & Berry"
                        }
                        val orchDesc = when (activeEra) {
                            4 -> "Nutrisi buah segar kaya vitamin siber."
                            3 -> "Panen buah manis untuk pasokan toko kota."
                            2 -> "Pasokan buah segar pesta bangsawan istana."
                            else -> "Buah berry segar untuk nutrisi dan ramuan."
                        }
                        viewModel.showAlert("$orchTitle (Lv. ${state.getBuildingLevel("perkebunan")}): $orchDesc")
                    }
                } else if (kotlin.math.hypot(nx - 0.82f, ny - 0.49f) < 0.08f) {
                    if (state.isUnlocked("peternakan")) {
                        val ranchTitle = when (activeEra) {
                            4 -> "🐂 Bio-Synth Livestock Dome"
                            3 -> "🥛 Peternakan Sapi Perah Modern"
                            2 -> "🐄 Peternakan Sapi & Domba"
                            else -> "🐑 Padang Gembala Suku"
                        }
                        val ranchDesc = when (activeEra) {
                            4 -> "Kloning sapi siber berkerah neon dalam kubah."
                            3 -> "Silo stainless & sapi perah Holstein modern."
                            2 -> "Pasokan daging, susu, dan wol ksatria istana."
                            else -> "Domba dan babi hutan penghasil daging."
                        }
                        viewModel.showAlert("$ranchTitle (Lv. ${state.getBuildingLevel("peternakan")}): $ranchDesc")
                    }
                } else if (kotlin.math.hypot(nx - 0.18f, ny - 0.49f) < 0.08f) {
                    if (state.isUnlocked("hospital")) {
                        val hospTitle = when (activeEra) {
                            4 -> "🩺 Cyber Med-Bay Trauma Center"
                            3 -> "🏥 Rumah Sakit Umum Modern"
                            2 -> "🏥 Balai Pengobatan Kerajaan"
                            else -> "🧙 Pondok Tabib Shaman"
                        }
                        val hospDesc = when (activeEra) {
                            4 -> "Kapsul regenerasi seluler & ambulans melayang."
                            3 -> "UGD darurat, armada ambulans & helipad helikopter."
                            2 -> "Perawatan ksatria terluka & ramuan taman lavender."
                            else -> "Meracik ramuan herbal penyembuh prajurit terluka."
                        }
                        viewModel.showAlert("$hospTitle (Lv. ${state.getBuildingLevel("hospital")}): $hospDesc")
                    }
                } else if (kotlin.math.hypot(nx - 0.20f, ny - 0.67f) < 0.08f) {
                    if (state.isUnlocked("barak_militer")) {
                        val barakTitle = when (activeEra) {
                            4 -> "🤖 Markas Mech & Divisi Tempur"
                            3 -> "🪖 Pangkalan Militer Taktis"
                            2 -> "⚔️ Barak Ksatria Kerajaan"
                            else -> "⚔️ Barak Militer Purba"
                        }
                        val barakDesc = when (activeEra) {
                            4 -> "Mech tempur lapis baja, jet siluman VTOL & prajurit exo-suit!"
                            3 -> "Pasukan infanteri modern, truk tempur & helikopter taktis!"
                            2 -> "Pelatihan prajurit pedang & perisai berpanji singa!"
                            else -> "Pusat pelatihan Pasukan Kapak Batu & Tombak."
                        }
                        viewModel.showAlert("$barakTitle (Lv. ${state.getBuildingLevel("barak_militer")}): $barakDesc")
                    }
                } else if (kotlin.math.hypot(nx - 0.50f, ny - 0.66f) < 0.08f) {
                    if (state.isUnlocked("market")) {
                        val mktTitle = when (activeEra) {
                            4 -> "💠 Holographic Cyber Bazaar"
                            3 -> "🛍️ Kawasan Bisnis & Pertokoan"
                            2 -> "🎪 Pasar Raya Kerajaan"
                            else -> "🎪 Pasar Barter Suku"
                        }
                        val mktDesc = when (activeEra) {
                            4 -> "Pertukaran bursa kripto data & komoditas siber."
                            3 -> "Pusat perbelanjaan, butik dan kafe pejalan kaki."
                            2 -> "Kios kain sutra, rempah-rempah, dan perhiasan emas."
                            else -> "Pertukaran komoditas dan hasil bumi."
                        }
                        viewModel.showAlert("$mktTitle (Lv. ${state.getBuildingLevel("market")}): $mktDesc")
                    }
                } else if (kotlin.math.hypot(nx - 0.80f, ny - 0.65f) < 0.08f) {
                    if (state.isUnlocked("tavern")) {
                        val tvTitle = when (activeEra) {
                            4 -> "🍸 Neon Cyber Lounge"
                            3 -> "☕ Kafe & Bistro Modern"
                            2 -> "🍻 Kedai Tuak Kerajaan"
                            else -> "🍻 Kedai Tuak & Tavern"
                        }
                        val tvDesc = when (activeEra) {
                            4 -> "Koktail hologram neon, musik synthwave & rekrut Hero!"
                            3 -> "Kopi espresso nikmat, santai outdoor & rekrut Hero!"
                            2 -> "Bersulang bir gandum dingin, musik harpa & rekrut Hero!"
                            else -> "Tempat bersulang dan merekrut Hero!"
                        }
                        viewModel.showAlert("$tvTitle (Lv. ${state.getBuildingLevel("tavern")}): $tvDesc")
                    }
                } else if (kotlin.math.hypot(nx - 0.78f, ny - 0.81f) < 0.08f) {
                    if (state.isUnlocked("dungeon")) {
                        viewModel.startDungeonBattle(state.maxDungeonLevel)
                    }
                } else if (kotlin.math.hypot(nx - 0.50f, ny - 0.14f) < 0.08f) {
                    if (state.isUnlocked("temple")) {
                        val tmpTitle = when (activeEra) {
                            4 -> "🔮 Quantum Levitation Shrine"
                            3 -> "🏛️ Katedral / Monumen Spiritual"
                            2 -> "⛪ Katedral Gothic Agung"
                            else -> "⛩️ Altar Leluhur"
                        }
                        val tmpDesc = when (activeEra) {
                            4 -> "Cincin gravitasi kosmis & berkah energi siber."
                            3 -> "Kedamaian batin dan harmoni warga kota."
                            2 -> "Kaca patri rose window dan doa berkah kerajaan."
                            else -> "Memohon berkah para dewa untuk kejayaan suku."
                        }
                        viewModel.showAlert("$tmpTitle (Lv. ${state.getBuildingLevel("temple")}): $tmpDesc")
                    }
                } else if (kotlin.math.hypot(nx - 0.50f, ny - 0.92f) < 0.08f) {
                    if (state.isUnlocked("tembok")) {
                        val wTitle = when (activeEra) {
                            4 -> "🛡️ Dinding Benteng Nano-Alloy"
                            3 -> "🚧 Pagar Keamanan & Pos Penjagaan"
                            2 -> "🛡️ Benteng Batu Kerajaan"
                            else -> "🛡️ Gerbang Utama Suku"
                        }
                        val wDesc = when (activeEra) {
                            4 -> "Dinding anti-balistik dengan pintu laser forcefield!"
                            3 -> "Perimeter beton modern & palang portal otomatis!"
                            2 -> "Dinding batu tebal, menara panji singa & jembatan angkat!"
                            else -> "Menjaga perbatasan dari binatang buas dan serbuan raid!"
                        }
                        viewModel.showAlert("$wTitle (Lv. ${state.getBuildingLevel("tembok")}): $wDesc")
                    }
                }
            }
        }
    ) {
        if (state.gamePhase != "main_game") {
            // Draw dark background
            drawRect(Color(0xFF07050F), size = size)
            
            if (state.gamePhase == "animasi_api") {
                val cx = size.width / 2
                val cy = size.height / 2 + 30f
                
                // Pulsing ambient campfire glow
                val pulse = kotlin.math.sin(fireTime * 0.1f)
                drawCircle(
                    color = Color(0xFFEA580C).copy(alpha = 0.22f + 0.06f * pulse),
                    radius = 95f + 12f * pulse,
                    center = Offset(cx, cy)
                )
                
                // Draw wooden logs
                drawLine(
                    color = Color(0xFF451A03),
                    start = Offset(cx - 32f, cy + 22f),
                    end = Offset(cx + 32f, cy + 16f),
                    strokeWidth = 11f
                )
                drawLine(
                    color = Color(0xFF78350F),
                    start = Offset(cx - 18f, cy + 13f),
                    end = Offset(cx + 22f, cy + 24f),
                    strokeWidth = 9f
                )
                
                // Rising fire particles (procedural based on fireTime)
                for (i in 0 until 40) {
                    val seed = (i * 37) % 100
                    val particleTime = (fireTime * 0.25f + seed) % 100f
                    val progress = particleTime / 100f
                    
                    val startX = cx - 22f + ((seed * 7) % 44)
                    val drift = kotlin.math.sin(progress * 6.28f + seed) * 20f
                    val pX = startX + drift
                    val pY = (cy + 15f) - (progress * 170f)
                    
                    val pColor = when {
                        progress < 0.25f -> Color(0xFFFDE047) // Bright Yellow
                        progress < 0.6f -> Color(0xFFF97316)  // Bright Orange
                        progress < 0.85f -> Color(0xFFEF4444) // Fire Red
                        else -> Color(0xFF991B1B)             // Deep glowing red ember
                    }
                    val radius = (1f - progress) * (11f + (seed % 6))
                    
                    if (pY < cy + 25f && radius > 0.5f) {
                        drawCircle(
                            color = pColor,
                            radius = radius,
                            center = Offset(pX, pY)
                        )
                    }
                }
            }
            return@Canvas
        }

        // 1. Panggil VillageRenderer berdasarkan era aktif
        // Renderer ini menggambar desa secara prosedural dan menampilkan bangunan
        // sesuai state yang sudah terbuka (fog-of-war built-in).
        when (activeEra) {
            4 -> CyberpunkVillageRenderer.drawVillage(this, state, time, size.width, size.height)
            3 -> ModernVillageRenderer.drawVillage(this, state, time, size.width, size.height)
            2 -> KingdomVillageRenderer.drawVillage(this, state, time, size.width, size.height)
            else -> PrehistoricVillageRenderer.drawVillage(this, state, time, size.width, size.height)
        }

        // 3. Living Ambient Lighting over Real Artwork
        val pulse = (sin(time * 3f) + 1f) * 0.15f
        val glowColor = when (activeEra) {
            4 -> Color(0xFF22D3EE)
            3 -> Color(0xFF38BDF8)
            2 -> Color(0xFFFBBF24)
            else -> Color(0xFFF97316)
        }
        drawCircle(
            color = glowColor.copy(alpha = 0.18f + pulse),
            radius = 26f + pulse * 12f,
            center = Offset(0.50f * size.width, 0.53f * size.height)
        )

        if (state.isUnlocked("dungeon")) {
            val portalGlow = (sin(time * 4f) + 1f) * 0.2f
            drawCircle(
                color = Color(0xFFA855F7).copy(alpha = 0.22f + portalGlow),
                radius = 28f + portalGlow * 10f,
                center = Offset(0.80f * size.width, 0.76f * size.height)
            )
        }

    }
}

fun DrawScope.drawWoodCamp(x: Float, y: Float, level: Int = 1, era: Int = 1) {
    val shadowColor = Color(0x4D000000)
    drawOval(shadowColor, topLeft = Offset(x - 8f, y + 16f), size = Size(65f, 14f))

    when (era) {
        1 -> { // Prehistoric: Raw wood shelter, stone axes
            drawRect(Color(0xFF0F172A), topLeft = Offset(x, y), size = Size(32f, 20f))
            drawRect(Color(0xFF78350F), topLeft = Offset(x + 1f, y + 1f), size = Size(30f, 18f))
            // Straw roof hatch lines
            drawRect(Color(0xFFD2B48C), topLeft = Offset(x + 1f, y + 1f), size = Size(30f, 3f))
            // Logs stacked
            for (i in 0..1) {
                drawRect(Color(0xFF0F172A), topLeft = Offset(x + 34f, y + 12f - (i * 6f)), size = Size(16f, 5f))
                drawRect(Color(0xFFB45309), topLeft = Offset(x + 35f, y + 13f - (i * 6f)), size = Size(14f, 3f))
            }
        }
        2 -> { // Classical: Roman-style wood station, bronze saws
            drawRect(Color(0xFF0F172A), topLeft = Offset(x, y - 4f), size = Size(34f, 24f))
            drawRect(Color(0xFF8B5A2B), topLeft = Offset(x + 1f, y - 3f), size = Size(32f, 22f))
            // Clay tiled roof edge
            drawRect(Color(0xFFCD5C5C), topLeft = Offset(x, y - 4f), size = Size(34f, 4f))
            // Bronze saw detail
            drawRect(Color(0xFFCD7F32), topLeft = Offset(x + 6f, y + 10f), size = Size(12f, 3f))
        }
        3 -> { // Medieval: Waterwheel lumber saw mill
            drawRect(Color(0xFF0F172A), topLeft = Offset(x - 5f, y - 5f), size = Size(42f, 26f))
            drawRect(Color(0xFF5C4033), topLeft = Offset(x - 4f, y - 4f), size = Size(40f, 24f))
            // Roof
            drawRect(Color(0xFFB22222), topLeft = Offset(x - 5f, y - 5f), size = Size(42f, 4f))
            // Moving waterwheel visual
            drawCircle(Color(0xFF0F172A), radius = 10f, center = Offset(x + 40f, y + 12f))
            drawCircle(Color(0xFF8B4513), radius = 8f, center = Offset(x + 40f, y + 12f))
            drawRect(Color(0xFF0F172A), topLeft = Offset(x + 38f, y + 4f), size = Size(4f, 16f))
        }
        4 -> { // Industrial: Steam powered mill, massive gears
            drawRect(Color(0xFF0F172A), topLeft = Offset(x, y - 8f), size = Size(36f, 28f))
            drawRect(Color(0xFF3E2723), topLeft = Offset(x + 1f, y - 7f), size = Size(34f, 26f))
            // Metal piping
            drawRect(Color(0xFFB0BEC5), topLeft = Offset(x + 28f, y - 18f), size = Size(4f, 12f)) // Chimney
            drawCircle(Color(0xFF78909C), radius = 3f, center = Offset(x + 30f, y - 20f)) // Steam puff
            // Rotating gear
            drawCircle(Color(0xFFCD7F32), radius = 7f, center = Offset(x + 12f, y + 8f))
        }
        5 -> { // Modern: Concrete yard, electric steel saws
            drawRect(Color(0xFF0F172A), topLeft = Offset(x, y - 5f), size = Size(36f, 25f))
            drawRect(Color(0xFFECEFF1), topLeft = Offset(x + 1f, y - 4f), size = Size(34f, 23f))
            // Cyan glass windows
            drawRect(Color(0xFF00E5FF), topLeft = Offset(x + 5f, y + 2f), size = Size(8f, 6f))
            // Forklift mockup
            drawRect(Color(0xFFFFD54F), topLeft = Offset(x + 25f, y + 12f), size = Size(12f, 8f))
        }
        else -> { // Cyberpunk: Plasma laser logging, hover pads
            drawRect(Color(0xFF0F172A), topLeft = Offset(x, y - 6f), size = Size(38f, 26f))
            drawRect(Color(0xFF1A237E), topLeft = Offset(x + 1f, y - 5f), size = Size(36f, 24f))
            // Glowing neon trims
            drawRect(Color(0xFF00E5FF), topLeft = Offset(x + 1f, y - 5f), size = Size(36f, 2f))
            // Plasma cutter laser beam (glowing orange line)
            drawRect(Color(0xFFFF9100), topLeft = Offset(x + 8f, y + 14f), size = Size(20f, 2f))
            // Hover loading pad
            drawOval(Color(0xFFD500F9), topLeft = Offset(x + 28f, y + 15f), size = Size(16f, 5f))
        }
    }
}

fun DrawScope.drawStoneCamp(x: Float, y: Float, level: Int = 1, era: Int = 1) {
    val shadowColor = Color(0x4D000000)
    drawOval(shadowColor, topLeft = Offset(x - 12f, y + 14f), size = Size(75f, 18f))

    when (era) {
        1 -> { // Prehistoric: Simple digging hole, flint hammers
            drawOval(Color(0xFF0F172A), topLeft = Offset(x, y + 2f), size = Size(42f, 18f))
            drawOval(Color(0xFF4E3629), topLeft = Offset(x + 1f, y + 3f), size = Size(40f, 16f))
            // Crude stone hammer
            drawRect(Color(0xFF78350F), topLeft = Offset(x + 15f, y - 2f), size = Size(2f, 12f))
            drawRect(Color(0xFF6B7280), topLeft = Offset(x + 12f, y - 4f), size = Size(8f, 4f))
        }
        2 -> { // Classical: Marble block carving station
            drawRect(Color(0xFF0F172A), topLeft = Offset(x, y), size = Size(36f, 20f))
            drawRect(Color(0xFFE2E8F0), topLeft = Offset(x + 1f, y + 1f), size = Size(34f, 18f)) // Marble base
            // Carving chisel lines
            drawRect(Color(0xFF94A3B8), topLeft = Offset(x + 8f, y + 6f), size = Size(20f, 2f))
        }
        3 -> { // Medieval: Pulley hoist pit
            drawOval(Color(0xFF0F172A), topLeft = Offset(x - 4f, y + 4f), size = Size(48f, 20f))
            drawOval(Color(0xFF334155), topLeft = Offset(x - 3f, y + 5f), size = Size(46f, 18f))
            // Pulley frame
            drawRect(Color(0xFF8B4513), topLeft = Offset(x + 10f, y - 10f), size = Size(4f, 15f))
            drawRect(Color(0xFF475569), topLeft = Offset(x + 6f, y - 12f), size = Size(12f, 3f))
        }
        4 -> { // Industrial: Steam rock crusher, conveyor mockup
            drawRect(Color(0xFF0F172A), topLeft = Offset(x, y - 5f), size = Size(40f, 24f))
            drawRect(Color(0xFF37474F), topLeft = Offset(x + 1f, y - 4f), size = Size(38f, 22f))
            // Smoke exhaust stack
            drawRect(Color(0xFF78909C), topLeft = Offset(x + 30f, y - 14f), size = Size(4f, 10f))
            // Conveyor belt loop
            drawRect(Color(0xFF212121), topLeft = Offset(x + 4f, y + 14f), size = Size(24f, 4f))
        }
        5 -> { // Modern: Heavy hydraulic drilling rig
            drawRect(Color(0xFF0F172A), topLeft = Offset(x, y - 6f), size = Size(38f, 26f))
            drawRect(Color(0xFFECEFF1), topLeft = Offset(x + 1f, y - 5f), size = Size(36f, 24f))
            // Heavy bright yellow drill column
            drawRect(Color(0xFFFFEB3B), topLeft = Offset(x + 16f, y - 12f), size = Size(6f, 24f))
            drawRect(Color(0xFF757575), topLeft = Offset(x + 17f, y + 12f), size = Size(4f, 12f)) // Heavy bit
        }
        else -> { // Cyberpunk: Gravity-levitated sonic disintegrator
            drawOval(Color(0xFF00E5FF), topLeft = Offset(x - 2f, y + 2f), size = Size(44f, 20f))
            drawOval(Color(0xFF0B132B), topLeft = Offset(x - 1f, y + 3f), size = Size(42f, 18f))
            // Levitating glowing purple shards
            drawRect(Color(0xFFD500F9), topLeft = Offset(x + 10f, y - 5f), size = Size(6f, 6f))
            drawRect(Color(0xFFD500F9), topLeft = Offset(x + 24f, y - 8f), size = Size(5f, 5f))
            // Sonic ray laser (glowing cyan neon beam)
            drawRect(Color(0xFF00E5FF), topLeft = Offset(x + 18f, y - 12f), size = Size(2f, 18f))
        }
    }
}

fun DrawScope.drawCampfire(x: Float, y: Float, time: Float, era: Int = 1) {
    val shadowColor = Color(0x33000000)
    drawOval(shadowColor, topLeft = Offset(x - 14f, y + 6f), size = Size(28f, 10f))

    when (era) {
        1 -> { // Prehistoric: Stone circle ring campfire with stacked logs and dancing flames
            // Ambient warm fire glow
            val glowRadius = 26f + sin(time * 8f) * 3f
            drawCircle(Color(0xFFEA580C).copy(alpha = 0.22f), radius = glowRadius, center = Offset(x, y))
            
            // Circular Ring of Stones around the fire
            for (st in 0 until 8) {
                val ang = st * (360f / 8f)
                val rad = Math.toRadians(ang.toDouble())
                val sx = x + (cos(rad) * 14f).toFloat()
                val sy = y + (sin(rad) * 7f).toFloat()
                drawOval(Color(0xFF5F6368), topLeft = Offset(sx - 3.5f, sy - 2.5f), size = Size(7f, 5f))
                drawOval(Color(0xFF9AA0A6), topLeft = Offset(sx - 2.5f, sy - 1.5f), size = Size(5f, 3f))
            }

            // Stacked campfire logs
            drawLine(Color(0xFF4A2810), Offset(x - 9f, y + 2f), Offset(x + 9f, y - 2f), 3.5f)
            drawLine(Color(0xFF78471D), Offset(x - 7f, y - 2f), Offset(x + 7f, y + 2f), 3f)

            // Animated roaring flames
            val fireOffset = sin(time * 12f) * 2f
            val flamePath = Path().apply {
                moveTo(x, y - 18f + fireOffset)
                lineTo(x + 7f, y + 2f)
                lineTo(x - 7f, y + 2f)
                close()
            }
            drawPath(flamePath, Color(0xFFEF4444)) // Red outer fire

            val midPath = Path().apply {
                moveTo(x, y - 13f + fireOffset)
                lineTo(x + 5f, y + 2f)
                lineTo(x - 5f, y + 2f)
                close()
            }
            drawPath(midPath, Color(0xFFFB8C00)) // Orange mid fire

            val corePath = Path().apply {
                moveTo(x, y - 8f + fireOffset)
                lineTo(x + 3f, y + 2f)
                lineTo(x - 3f, y + 2f)
                close()
            }
            drawPath(corePath, Color(0xFFFFF176)) // Yellow core
        }
        2 -> { // Classical: Roman stone fire-brazier
            drawRect(Color(0xFF0F172A), topLeft = Offset(x - 6f, y - 4f), size = Size(12f, 12f))
            drawRect(Color(0xFFE2E8F0), topLeft = Offset(x - 5f, y - 3f), size = Size(10f, 10f)) // White pillar pedestal
            drawRect(Color(0xFF475569), topLeft = Offset(x - 8f, y - 8f), size = Size(16f, 4f)) // Iron dish
            
            val fireOffset = sin(time * 15f) * 1.5f
            drawCircle(Color(0xFFEF4444), radius = 4f + fireOffset, center = Offset(x, y - 12f))
            drawCircle(Color(0xFFF59E0B), radius = 2f + fireOffset, center = Offset(x, y - 11f))
        }
        3 -> { // Medieval: Stone hearth and boiling cauldron kettle
            drawOval(Color(0xFF4B5563), topLeft = Offset(x - 10f, y - 2f), size = Size(20f, 10f))
            drawRect(Color(0xFF1E293B), topLeft = Offset(x - 5f, y - 6f), size = Size(10f, 6f)) // Iron pot cauldron
            
            val fireOffset = sin(time * 10f) * 2f
            drawRect(Color(0xFFF97316), topLeft = Offset(x - 4f, y + 2f - fireOffset), size = Size(8f, 4f))
        }
        4 -> { // Industrial: Steam mechanical boiler burner
            drawRect(Color(0xFF0F172A), topLeft = Offset(x - 8f, y - 8f), size = Size(16f, 16f))
            drawRect(Color(0xFF5C4033), topLeft = Offset(x - 7f, y - 7f), size = Size(14f, 14f)) // Copper body
            drawRect(Color(0xFFEF4444), topLeft = Offset(x - 3f, y - 3f), size = Size(6f, 6f)) // Red hot glowing fuel grate window
        }
        5 -> { // Modern: Pristine steel gas fire column
            drawRect(Color(0xFF0F172A), topLeft = Offset(x - 5f, y - 10f), size = Size(10f, 18f))
            drawRect(Color(0xFF94A3B8), topLeft = Offset(x - 4f, y - 9f), size = Size(8f, 16f)) // Steel cylinder
            // Cyan clean blue gas flame
            drawCircle(Color(0xFF06B6D4), radius = 3f + sin(time * 20f) * 1f, center = Offset(x, y - 13f))
        }
        else -> { // Cyberpunk: Neon holographic laser column
            drawRect(Color(0xFF0B132B), topLeft = Offset(x - 6f, y - 2f), size = Size(12f, 10f)) // Base
            drawRect(Color(0xFF00E5FF), topLeft = Offset(x - 5f, y - 1f), size = Size(10f, 2f)) // Cyan neon frame ring
            // Rising vertical laser neon light pillar
            val beamHeight = 24f + sin(time * 8f) * 4f
            drawRect(Color(0xFFD500F9).copy(alpha = 0.8f), topLeft = Offset(x - 3f, y - beamHeight), size = Size(6f, beamHeight))
            drawRect(Color(0xFFFFFFFF), topLeft = Offset(x - 1f, y - beamHeight), size = Size(2f, beamHeight)) // White plasma beam center
        }
    }

    // Common Smoke/Plasma particles rising
    for (i in 0 until 4) {
        val particleTime = time * 2.5f + i * 0.5f
        val progress = particleTime - particleTime.toInt()
        val px = x - 2f + sin(particleTime * 6f) * 8f * progress
        val py = y - 8f - progress * 35f
        val alpha = (1f - progress).coerceIn(0f, 1f)
        
        val particleColor = when (era) {
            6 -> Color(0xFFD500F9).copy(alpha = alpha) // Magenta plasma spark
            5 -> Color(0xFF22D3EE).copy(alpha = alpha) // Aqua steam spark
            else -> Color(0xFF9CA3AF).copy(alpha = alpha * 0.6f) // Classic grey smoke
        }
        drawRect(particleColor, topLeft = Offset(px, py), size = Size(3f, 3f))
    }
}

fun DrawScope.drawCharacter(x: Float, y: Float, name: String, isMale: Boolean, hasWeapon: Boolean, hasArmor: Boolean) {
    val shadowColor = Color(0x33000000)
    drawOval(shadowColor, topLeft = Offset(x - 4f, y + 20f), size = Size(20f, 8f))

    val skinColor = Color(0xFFFFB6C1)
    val hairColor = if (isMale) Color(0xFF4A3728) else Color(0xFF8B4513)
    val shirtColor = if (hasArmor) Color(0xFF8B4513) else if (isMale) Color(0xFF2563EB) else Color(0xFFDB2777)
    val pantsColor = Color(0xFF1E293B)
    
    // Head
    drawRect(skinColor, topLeft = Offset(x, y - 12f), size = Size(12f, 12f))
    // Hair
    drawRect(hairColor, topLeft = Offset(x - 1f, y - 14f), size = Size(14f, 4f))
    if (!isMale) {
        drawRect(hairColor, topLeft = Offset(x - 1f, y - 10f), size = Size(3f, 10f))
        drawRect(hairColor, topLeft = Offset(x + 10f, y - 10f), size = Size(3f, 10f))
    }
    // Eyes
    drawRect(Color.Black, topLeft = Offset(x + 2f, y - 8f), size = Size(2f, 2f))
    drawRect(Color.Black, topLeft = Offset(x + 8f, y - 8f), size = Size(2f, 2f))
    
    // Body
    drawRect(shirtColor, topLeft = Offset(x - 2f, y), size = Size(16f, 14f))
    
    if (hasArmor) {
        drawRect(Color(0xFFD2B48C), topLeft = Offset(x, y + 2f), size = Size(12f, 10f))
    }
    
    // Legs
    drawRect(pantsColor, topLeft = Offset(x, y + 14f), size = Size(4f, 10f))
    drawRect(pantsColor, topLeft = Offset(x + 8f, y + 14f), size = Size(4f, 10f))
    
    // Arms
    drawRect(skinColor, topLeft = Offset(x - 6f, y + 2f), size = Size(4f, 10f))
    drawRect(skinColor, topLeft = Offset(x + 14f, y + 2f), size = Size(4f, 10f))
    
    // Weapon
    if (hasWeapon) {
        drawRect(Color(0xFF5C4033), topLeft = Offset(x - 8f, y - 4f), size = Size(4f, 24f))
    }
}

data class TalentNode(
    val id: String,
    val name: String,
    val zenithCost: Double = 0.0,
    val resourceCosts: Map<String, Long> = emptyMap(),
    val desc: String,
    val type: String,
    val tier: Int = 1,
    val reqs: List<String> = emptyList(),
    val story: String = "",
    val gridX: Int = 0,
    val gridY: Int = 0,
    val normX: Float = 0f,
    val normY: Float = 0f
)

data class CoinShopItem(val id: String, val name: String, val cost: Long, val desc: String, val minMarketLvl: Int)

val GAME_COIN_SHOP = listOf(
    CoinShopItem("shield_1h", "Shield 1 Jam Kota", 300, "Imun penjarahan raid.", 1),
    CoinShopItem("trap_city", "Jebakan Kota", 200, "Instan kurangi 10% HP musuh raid.", 1),
    CoinShopItem("cannon_gate", "Meriam Gerbang", 500, "Otomatis +300 Atk per 3 detik saat raid.", 1),
    CoinShopItem("eq_wood_sword", "Pedang Kayu (Hero)", 300, "+200 Attack Hero.", 1),
    CoinShopItem("eq_iron_sword", "Pedang Besi (Hero)", 1000, "+700 Attack Hero.", 1),
    
    CoinShopItem("crossbow_gate", "Crossbow Gerbang", 1200, "Otomatis +200 Atk per detik saat raid.", 2),
    CoinShopItem("eq_gold_sword", "Pedang Emas (Hero)", 2000, "+1200 Attack Hero.", 2),
    CoinShopItem("eq_pistol", "Pistol (Hero)", 5000, "+2500 Attack Hero.", 2),
    
    CoinShopItem("eq_ak47", "AK47 (Hero)", 10000, "+5000 Attack Hero.", 3),
    CoinShopItem("eq_carbine", "Special Carbine (Hero)", 25000, "+12000 Attack Hero.", 3),
    
    CoinShopItem("eq_laser", "Senjata Laser (Hero)", 50000, "+25000 Attack Hero.", 4),
    CoinShopItem("eq_cyber", "Senjata Cyber (Hero)", 90000, "+50000 Attack Hero.", 4)
)

val allNodes = listOf(
    // I PROLOG PRASEJARAH
    TalentNode("percikan_api", "Nyalakan Api Pertama", 1.0, emptyMap(), "Satu percikan untuk mengusir kegelapan awal zaman.", "skill", tier = 1, gridX = 0, gridY = 0, normX = 0.391f, normY = 0.059f),
    TalentNode("memasak_makanan", "Memasak Makanan", 5.0, emptyMap(), "Api membuat makanan lebih aman dikonsumsi", "skill", tier = 1, reqs = listOf("percikan_api"), story = "Api membuat daging tidak mentah lagi dan jauh lebih aman dikonsumsi.", gridX = -2, gridY = 1, normX = 0.246f, normY = 0.110f),
    TalentNode("panggil_manusia", "Panggil Manusia Pertama", 10.0, emptyMap(), "Manusia memiliki akal dan rasa ingin tahu.", "skill", tier = 1, reqs = listOf("percikan_api"), gridX = 2, gridY = 1, normX = 0.543f, normY = 0.110f),
    
    // CABANG KIRI (Dari Memasak Makanan): PERKAKAS & BERTAHAN HIDUP
    TalentNode("perkakas_batu", "Perkakas Batu", 20.0, emptyMap(), "Pembuatan alat dasar dari batu", "skill", tier = 1, reqs = listOf("memasak_makanan"), story = "Peralatan batu membantu manusia purba berburu dan bertahan hidup.", gridX = -3, gridY = 2, normX = 0.176f, normY = 0.154f),
    TalentNode("budidaya", "Budidaya Semak", 108.0, emptyMap(), "Membuka produksi otomatis 🧺 Berry (+2/s)", "skill", tier = 1, reqs = listOf("perkakas_batu"), gridX = -3, gridY = 3, normX = 0.155f, normY = 0.196f),
    TalentNode("pengasapan_daging", "Pengasapan Daging", 324.0, emptyMap(), "Mengurangi konsumsi makanan populasi sebesar 10%", "skill", tier = 1, reqs = listOf("budidaya"), gridX = -3, gridY = 4, normX = 0.155f, normY = 0.241f),
    TalentNode("pertahanan_primitif", "Pertahanan Primitif", 756.0, emptyMap(), "Menambah Damage dasar manusia sebesar +50 secara instan", "skill", tier = 1, reqs = listOf("pengasapan_daging"), gridX = -3, gridY = 5, normX = 0.155f, normY = 0.285f),
    
    // CABANG KANAN (Dari Panggil Manusia): KOGNISI & SOSIAL
    TalentNode("otak_kognitif", "Otak Kognitif", 30.0, emptyMap(), "Evolusi kapasitas kognitif", "skill", tier = 1, reqs = listOf("panggil_manusia"), story = "Kapasitas kognitif Homo sapiens melonjak pesat.", gridX = 2, gridY = 2, normX = 0.556f, normY = 0.154f),
    TalentNode("emosi_jiwa", "Emosi & Jiwa", 80.0, emptyMap(), "Kesadaran akan rasa dan ikatan", "skill", tier = 1, reqs = listOf("otak_kognitif"), story = "Rasa empati dan kasih sayang mempererat kelompok suku.", gridX = 3, gridY = 3, normX = 0.599f, normY = 0.196f),
    TalentNode("moralitas_komunitas", "Moralitas Komunitas", 150.0, emptyMap(), "Aturan universal kelompok", "skill", tier = 1, reqs = listOf("emosi_jiwa"), story = "Norma sosial mengatur ketertiban masyarakat berkembang.", gridX = 3, gridY = 4, normX = 0.599f, normY = 0.241f),
    TalentNode("penyelesaian_masalah", "Penyelesaian Masalah", 300.0, emptyMap(), "Berpikir kritis mencari solusi (Syarat Balai Kota)", "skill", tier = 1, reqs = listOf("moralitas_komunitas"), story = "Manusia mampu merencanakan pembangunan desa secara sistematis.", gridX = 3, gridY = 5, normX = 0.599f, normY = 0.285f),
    
    // CABANG TENGAH: KELANGSUNGAN HIDUP & BERBURU
    TalentNode("naluri_berburu", "Naluri Berburu", 100.0, emptyMap(), "Membuka produksi otomatis 🥩 Daging (+1/s)", "skill", tier = 1, reqs = listOf("otak_kognitif"), story = "Melacak jejak satwa untuk kebutuhan pangan suku.", gridX = 0, gridY = 3, normX = 0.396f, normY = 0.196f),
    TalentNode("alat_pemburu", "Alat Pemburu", 200.0, emptyMap(), "Mempermudah perburuan hewan", "skill", tier = 1, reqs = listOf("naluri_berburu"), story = "Tombak dan perangkap memperbesar hasil buruan.", gridX = -1, gridY = 4, normX = 0.332f, normY = 0.247f),
    TalentNode("penyamakan_kulit", "Penyamakan Kulit", 180.0, emptyMap(), "Memanfaatkan kulit buruan", "skill", tier = 1, reqs = listOf("naluri_berburu"), story = "Mengolah kulit hewan menjadi pakaian hangat pelindung tubuh.", gridX = 1, gridY = 4, normX = 0.460f, normY = 0.247f),
    
    // II ERA AGRARIS KERAJAAN
    TalentNode("domestikasi_hewan", "Domestikasi Hewan", 2592.0, emptyMap(), "Produksi Pertanian & Perkebunan naik +25%", "skill", tier = 2, reqs = listOf("naluri_berburu", "pertahanan_primitif"), normX = 0.401f, normY = 0.288f),
    TalentNode("seni_memasak", "Seni Memasak Klasik", 7560.0, emptyMap(), "Olah Makanan Matang (+2% Zenith Global)", "skill", tier = 2, reqs = listOf("domestikasi_hewan"), normX = 0.422f, normY = 0.324f),
    TalentNode("manufaktur_roti", "Manufaktur Roti", 18360.0, emptyMap(), "Gandum jadi Roti, efisiensi gizi kelaparan +30%", "skill", tier = 2, reqs = listOf("seni_memasak"), normX = 0.401f, normY = 0.354f),
    TalentNode("spiritualitas_suku", "Spiritualitas Suku", 32400.0, emptyMap(), "Meningkatkan batas maksimal Happiness global sebesar +15%", "skill", tier = 2, reqs = listOf("penyelesaian_masalah"), normX = 0.599f, normY = 0.354f),
    
    // III ERA INDUSTRI BANGSA (Baris 1)
    TalentNode("pengalengan_makanan", "Pengalengan Makanan", 140400.0, emptyMap(), "Membuka Pabrik Instan, output energi makanan melonjak +50%", "skill", tier = 3, reqs = listOf("manufaktur_roti"), normX = 0.230f, normY = 0.398f),
    TalentNode("kerja_shift", "Manajemen Kerja Shift", 324000.0, emptyMap(), "Kecepatan ekstraksi Kayu, Batu, dan Besi naik +30%", "skill", tier = 3, reqs = listOf("manufaktur_roti"), normX = 0.316f, normY = 0.398f),
    TalentNode("metalurgi_militer", "Metalurgi Militer", 756000.0, emptyMap(), "Diskon 15% Besi/Emas untuk upgrade senjata", "skill", tier = 3, reqs = listOf("manufaktur_roti"), normX = 0.406f, normY = 0.398f),
    TalentNode("kedaulatan_bangsa", "Kedaulatan Bangsa", 3000000.0, emptyMap(), "Membuka Fitur Kebangsaan di Town Hall level 6", "skill", tier = 3, reqs = listOf("spiritualitas_suku"), normX = 0.497f, normY = 0.398f),
    TalentNode("kerja_paksa", "Kerja Paksa", 1500000.0, emptyMap(), "Kebijakan Aktif: +100% Produksi, -30% Kebahagiaan", "skill", tier = 3, reqs = listOf("spiritualitas_suku"), normX = 0.588f, normY = 0.398f),
    
    // III ERA INDUSTRI BANGSA (Baris 2)
    TalentNode("overclock_tubuh", "Overclock Tubuh", 9720000.0, emptyMap(), "Stimulan medis memicu seluruh produksi bahan baku naik 2x lipat", "skill", tier = 3, reqs = listOf("pengalengan_makanan", "kerja_shift"), normX = 0.283f, normY = 0.460f),
    TalentNode("logistik_komputer", "Logistik Komputerisasi", 25920000.0, emptyMap(), "Transfer logistik otomatis tanpa risiko kehilangan material", "skill", tier = 3, reqs = listOf("kerja_shift", "metalurgi_militer"), normX = 0.412f, normY = 0.460f),
    TalentNode("drone_swarm", "Drone Swarm Gathering", 97200000.0, emptyMap(), "Panen otomatis Minyak, Titanium, Uranium +5/s", "skill", tier = 3, reqs = listOf("metalurgi_militer", "kedaulatan_bangsa"), normX = 0.588f, normY = 0.460f),
    
    // IV & V ERA MODERN & CYBERPUNK
    TalentNode("implan_sibernetik", "Implan Sibernetik", 324000000.0, emptyMap(), "Damage pasif +25.000 dan HP gerbang +5.000", "skill", tier = 4, reqs = listOf("overclock_tubuh"), normX = 0.251f, normY = 0.526f),
    TalentNode("nano_repair", "Nano Repair System", 1296000000.0, emptyMap(), "Nanobot menyembuhkan sel tubuh, HP maksimal melonjak +100.000", "skill", tier = 4, reqs = listOf("logistik_komputer"), normX = 0.396f, normY = 0.526f),
    TalentNode("brain_link", "Singularity Brain Link", 5400000000.0, emptyMap(), "Kesadaran kosmis menyatu, seluruh pendapatan Zenith dikali 5x lipat!", "skill", tier = 5, reqs = listOf("drone_swarm", "implan_sibernetik", "nano_repair"), normX = 0.567f, normY = 0.526f),
    
    // Bangunan (Raw Materials)
    TalentNode("rumah_warga", "Rumah Rakyat", 0.0, mapOf("kayu" to 132L, "batu" to 88L), "Populasi maksimal +10", "bangunan", tier = 2),
    TalentNode("town_hall", "Balai Kota (Town Hall)", 0.0, mapOf("kayu" to 28L, "batu" to 28L), "Pusat peradaban & penentu zaman", "bangunan", tier = 1),
    TalentNode("hospital", "Balai Pengobatan", 0.0, mapOf("kayu" to 150L, "batu" to 100L), "Menyembuhkan pasukan yang terluka", "bangunan", tier = 1),
    TalentNode("gudang", "Gudang Logistik", 0.0, mapOf("kayu" to 36L), "Kapasitas maksimal item +100", "bangunan", tier = 1),
    TalentNode("pertanian", "Ladang Pertanian", 0.0, mapOf("kayu" to 48L, "batu" to 17L), "Produksi Gandum (+2/s)", "bangunan", tier = 1),
    TalentNode("perkebunan", "Kebun Buah & Berry", 0.0, mapOf("kayu" to 36L, "batu" to 12L), "Produksi Berry (+2/s)", "bangunan", tier = 2),
    TalentNode("peternakan", "Padang Peternakan", 0.0, mapOf("kayu" to 60L, "batu" to 36L), "Produksi Daging (+1/s)", "bangunan", tier = 2),
    TalentNode("pelabuhan", "Pelabuhan (Dermaga)", 0.0, mapOf("kayu" to 96L, "batu" to 48L), "Mencari ikan otomatis", "bangunan", tier = 3),
    TalentNode("barak_militer", "Barak Militer", 0.0, mapOf("kayu" to 40L, "batu" to 20L), "Rekrut & latih prajurit suku (Max Pasukan +20)", "bangunan", tier = 1),
    TalentNode("tembok", "Tembok & Gerbang", 0.0, mapOf("batu" to 60L, "besi" to 24L), "HP Gerbang +100", "bangunan", tier = 3),
    TalentNode("market", "Pasar (Market)", 0.0, mapOf("kayu" to 145L, "besi" to 72L), "Buka Tab Toko Koin & Barter", "bangunan", tier = 2),
    TalentNode("temple", "Kuil / Katedral (Altar)", 0.0, mapOf("batu" to 181L, "emas" to 60L), "Buka Doktrin Agama & Berkah", "bangunan", tier = 2),
    TalentNode("taman_kota", "Taman Kota", 0.0, mapOf("kayu" to 84L, "batu" to 84L), "Kebahagiaan Warga +20%", "bangunan", tier = 2),
    TalentNode("dungeon", "Portal Dungeon", 0.0, mapOf("batu" to 242L, "besi" to 60L), "Akses pertempuran dungeon", "bangunan", tier = 3),
    TalentNode("tavern", "Kedai (Tavern)", 0.0, mapOf("kayu" to 275L, "besi" to 165L, "emas" to 55L), "Gacha Komandan Pahlawan", "bangunan", tier = 2),
    TalentNode("penebang_kayu", "Kamp Pemburu Kayu", 0.0, mapOf("kayu" to 12L), "Produksi Kayu (+1/s)", "bangunan", tier = 1),
    TalentNode("pemecah_batu", "Area Penambang Batu", 0.0, mapOf("batu" to 12L), "Produksi Batu (+1/s)", "bangunan", tier = 1),
    TalentNode("peleburan_besi", "Peleburan Besi", 0.0, mapOf("batu" to 60L, "kayu" to 60L), "Produksi Besi (+2/s)", "bangunan", tier = 2),
    TalentNode("tambang_emas", "Tambang Emas", 0.0, mapOf("besi" to 60L, "kayu" to 60L), "Produksi Emas (+2/s)", "bangunan", tier = 2),
    TalentNode("pengeboran_minyak", "Pengeboran Minyak", 0.0, mapOf("besi" to 242L, "batu" to 121L), "Produksi Minyak (+5/s)", "bangunan", tier = 4),
    TalentNode("pembangkit_listrik", "Pembangkit Listrik", 0.0, mapOf("besi" to 605L, "emas" to 121L), "Produksi Listrik (+10/s)", "bangunan", tier = 4),
    TalentNode("tambang_titanium", "Tambang Titanium", 0.0, mapOf("listrik" to 1210L, "besi" to 1210L), "Produksi Titanium (+8/s)", "bangunan", tier = 5),
    TalentNode("reaktor_uranium", "Reaktor Uranium", 0.0, mapOf("titanium" to 6050L, "listrik" to 6050L), "Produksi Uranium (+5/s)", "bangunan", tier = 5),
    
    // Filsafat & Kebijakan
    TalentNode("hukum_kodeks", "Hukum & Tata Pura", 1760.0, emptyMap(), "Konsumsi makanan warga berkurang 10%", "filsafat", tier = 1),
    TalentNode("barter_purba", "Perdagangan Suku", 40.0, emptyMap(), "Pendapatan pasif Zenith (+50%)", "filsafat", tier = 1),
    TalentNode("monarki", "Monarki Kodoks", 440.0, emptyMap(), "Batas Kebahagiaan peradaban +15%", "filsafat", tier = 2),
    TalentNode("feodalisme", "Feodalisme", 7040.0, emptyMap(), "Produksi Kayu & Batu meningkat +40%", "filsafat", tier = 2),
    TalentNode("meritokrasi", "Meritokrasi", 26400.0, emptyMap(), "Produksi Emas dan efisiensi pejabat +100%", "filsafat", tier = 3),
    TalentNode("teokrasi", "Teokrasi", 99000.0, emptyMap(), "Seluruh Damage tempur militer +30%", "filsafat", tier = 3),
    TalentNode("kapitalisme", "Kapitalisme", 330000.0, emptyMap(), "Pendapatan Zenith melonjak +100%", "filsafat", tier = 4),
    TalentNode("sosialisme", "Sosialisme", 1320000.0, emptyMap(), "Gizi pangan optimal, kapasitas warga +50", "filsafat", tier = 4),
    TalentNode("otoritarianisme", "Otoritarianisme", 5500000.0, emptyMap(), "Produksi Minyak & Listrik berlipat +100%", "filsafat", tier = 5),
    TalentNode("transhumanisme", "Transhumanisme", 22000000.0, emptyMap(), "Pengali evolusi global dikali 5x lipat!", "filsafat", tier = 5),

    // Senjata & Armor
    TalentNode("tongkat_kayu", "Tombak Kayu", 30.0, mapOf("kayu" to 100L), "Damage: 180", "senjata/armor", tier = 1),
    TalentNode("kulit_hewan", "Armor Kulit Hewan", 45.0, mapOf("kayu" to 50L), "Defense +20, HP +200", "senjata/armor", tier = 1),
    TalentNode("pedang_besi", "Pedang Besi", 440.0, mapOf("besi" to 264L), "Damage: 980", "senjata/armor", tier = 2),
    TalentNode("armor_besi", "Baju Besi", 660.0, mapOf("besi" to 396L), "Defense +150, HP +1000", "senjata/armor", tier = 2),
    TalentNode("musket", "Musket", 5500.0, mapOf("besi" to 1100L, "emas" to 110L), "Damage: 6000", "senjata/armor", tier = 3),
    TalentNode("armor_kevlar", "Senapan Railgun", 8800.0, mapOf("besi" to 2200L, "emas" to 220L), "Damage: 8500", "senjata/armor", tier = 3),
    TalentNode("assault_rifle", "Assault Rifle", 49500.0, mapOf("besi" to 20000L, "emas" to 5000L, "item_rare" to 10L), "Damage: 12500", "senjata/armor", tier = 4),
    TalentNode("exo_suit", "Exo-Suit Tempur", 66000.0, mapOf("besi" to 25000L, "emas" to 7000L, "item_rare" to 15L), "Defense +8000, HP +25000", "senjata/armor", tier = 4),
    TalentNode("energy_katana", "Nano Kirana", 330000.0, mapOf("besi" to 100000L, "titanium" to 5000L, "item_sr" to 5L), "Damage: 76000", "senjata/armor", tier = 5),
    TalentNode("nano_armor", "Plasma Cannon", 495000.0, mapOf("besi" to 150000L, "titanium" to 8000L, "item_sr" to 8L), "Damage: 150000", "senjata/armor", tier = 5),
    TalentNode("planet_breaker", "Laser Blade", 8800000.0, mapOf("titanium" to 50000L, "uranium" to 10000L, "item_ssr" to 2L), "Damage: 400000", "senjata/armor", tier = 6),
    TalentNode("cyber_body", "Cyber Body", 13200000.0, mapOf("titanium" to 80000L, "uranium" to 15000L, "item_ssr" to 3L), "Defense +300000, HP +1000000", "senjata/armor", tier = 6)
)

@Composable
fun TabsSection(
    state: GameState,
    viewModel: GameViewModel,
    modifier: Modifier = Modifier,
    onShowKomandan: () -> Unit = {},
    onShowTavern: () -> Unit = {},
    onShowTalentTree: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = mutableListOf<String>()
    if (state.gamePhase == "main_game") {
        tabs.addAll(listOf("Bangunan", "🔬 Riset", "Filsafat"))
        if (state.isUnlocked("market")) {
            tabs.add("Market")
        }
        if (state.isUnlocked("tavern")) {
            tabs.add("Komandan Suku")
        }
        if (state.isUnlocked("kedaulatan_bangsa") && state.getBuildingLevel("town_hall") >= 6) {
            tabs.add("🌐 Kebangsaan")
        }
        tabs.add("📖 Kamus Max (Deff)")
    } else {
        tabs.add("Skill")
    }
    
    var showStatDialog by remember { mutableStateOf(false) }
    var showAgamaDialog by remember { mutableStateOf(false) }
    var showNationalityDialog by remember { mutableStateOf(false) }
    var showGudangDialog by remember { mutableStateOf(false) }
    var showGerbangDialog by remember { mutableStateOf(false) }
    var showHospitalDialog by remember { mutableStateOf(false) }
    var showPrehistoricTroopsDialog by remember { mutableStateOf(false) }
        val gachaResult by viewModel.gachaResult.collectAsState()
    val alertMessage by viewModel.alertMessage.collectAsState()
    
    if (alertMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearAlertMessage() },
            title = { Text("⚠️ Peringatan") },
            text = { Text(alertMessage ?: "", fontWeight = FontWeight.Bold) },
            confirmButton = { TextButton(onClick = { viewModel.clearAlertMessage() }) { Text("OK") } }
        )
    }
    
    if (gachaResult != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearGachaResult() },
            title = { Text("\uD83C\uDF8A Hasil Gacha Pahlawan") },
            text = { Text("Anda mendapatkan: $gachaResult", fontWeight = FontWeight.Bold) },
            confirmButton = { TextButton(onClick = { viewModel.clearGachaResult() }) { Text("OK") } }
        )
    }
    
if (showStatDialog) {
        AlertDialog(
            onDismissRequest = { showStatDialog = false },
            title = { Text("Statistik Peradaban") },
            text = { 
                val keamanan = (state.currentPasukan * 2 + state.maxGerbangHp / 50).coerceIn(0, 100)
                Text("Populasi: ${state.currentPasukan}/${state.maxPasukan}\nHappiness: ${state.happiness}%\nKapasitas Gudang: ${state.maxStorage}\nTingkat Keamanan Kota: $keamanan%") 
            },
            confirmButton = { TextButton(onClick = { showStatDialog = false }) { Text("Tutup") } }
        )
    }

    if (showNationalityDialog) {
        AlertDialog(
            onDismissRequest = { showNationalityDialog = false },
            title = { Text("Pilih Bangsa") },
            text = { 
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 380.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Button(onClick = { viewModel.pilihNationality("Nusantara"); showNationalityDialog = false }, modifier = Modifier.fillMaxWidth()) { Text("Nusantara (+50% Kayu, +20% Pangan)") }
                        Button(onClick = { viewModel.pilihNationality("Sparta"); showNationalityDialog = false }, modifier = Modifier.fillMaxWidth()) { Text("Sparta (+50% Atk, -20% Emas)") }
                        Button(onClick = { viewModel.pilihNationality("Romawi"); showNationalityDialog = false }, modifier = Modifier.fillMaxWidth()) { Text("Romawi (+50% HP Tembok, -10% Cost Bangunan)") }
                        Button(onClick = { viewModel.pilihNationality("Mesir Kuno"); showNationalityDialog = false }, modifier = Modifier.fillMaxWidth()) { Text("Mesir Kuno (+50% Zenith, -10% Pangan)") }
                        Button(onClick = { viewModel.pilihNationality("Jepang Shogunate"); showNationalityDialog = false }, modifier = Modifier.fillMaxWidth()) { Text("Jepang Shogunate (+30% Atk, +30% Besi)") }
                        Button(onClick = { viewModel.pilihNationality("Nordik"); showNationalityDialog = false }, modifier = Modifier.fillMaxWidth()) { Text("Nordik (+30% Kayu, +30% Emas)") }
                        Button(onClick = { viewModel.pilihNationality("Britania"); showNationalityDialog = false }, modifier = Modifier.fillMaxWidth()) { Text("Britania (+50% Emas, +20% HP Tembok)") }
                        Button(onClick = { viewModel.pilihNationality("Cyber-Neo"); showNationalityDialog = false }, modifier = Modifier.fillMaxWidth()) { Text("Cyber-Neo (+50% Titanium, +50% Uranium, -30% Pangan)") }
                        Button(onClick = { viewModel.pilihNationality("Atlantis"); showNationalityDialog = false }, modifier = Modifier.fillMaxWidth()) { Text("Atlantis (+100% Zenith, -50% Besi)") }
                        Button(onClick = { viewModel.pilihNationality("Mongol"); showNationalityDialog = false }, modifier = Modifier.fillMaxWidth()) { Text("Mongol (+40% Atk, -30% Cost Barak)") }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showNationalityDialog = false }) { Text("Batal") } }
        )
    }

    if (showAgamaDialog) {
        val levelTemple = state.getBuildingLevel("temple")
        AlertDialog(
            onDismissRequest = { showAgamaDialog = false },
            title = { Text("🙏 Pilihan Doktrin Keagamaan") },
            text = { 
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (levelTemple >= 1) {
                        Text("Kuil Level 1:", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        val sel1 = state.selectedReligions[1]
                        if (sel1 != null) {
                            val desc = if (sel1 == "Agama Alam") "+20% Produksi Kayu" else "+20% Keuntungan Zenith"
                            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer), modifier = Modifier.fillMaxWidth()) {
                                Text("✅ Terpilih: $sel1\n(Buff: $desc)", modifier = Modifier.padding(10.dp), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        } else {
                            Button(onClick = { viewModel.pilihAgama(1, "Agama Alam"); showAgamaDialog = false }, modifier = Modifier.fillMaxWidth()) { Text("Agama Alam (+20% Kayu)") }
                            Button(onClick = { viewModel.pilihAgama(1, "Agama Langit"); showAgamaDialog = false }, modifier = Modifier.fillMaxWidth()) { Text("Agama Langit (+20% Zenith)") }
                        }
                    }
                    if (levelTemple >= 2) {
                        Text("Kuil Level 2:", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        val sel2 = state.selectedReligions[2]
                        if (sel2 != null) {
                            val desc = if (sel2 == "Kepercayaan Leluhur") "+30% Batu, -10% Speed Barak" else "+25% Pangan, -5% Zenith"
                            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer), modifier = Modifier.fillMaxWidth()) {
                                Text("✅ Terpilih: $sel2\n(Buff: $desc)", modifier = Modifier.padding(10.dp), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        } else {
                            Button(onClick = { viewModel.pilihAgama(2, "Kepercayaan Leluhur"); showAgamaDialog = false }, modifier = Modifier.fillMaxWidth()) { Text("Kepercayaan Leluhur (+30% Batu, -10% Speed Barak)") }
                            Button(onClick = { viewModel.pilihAgama(2, "Kultus Kesuburan"); showAgamaDialog = false }, modifier = Modifier.fillMaxWidth()) { Text("Kultus Kesuburan (+25% Pangan, -5% Zenith)") }
                        }
                    }
                    if (levelTemple >= 3) {
                        Text("Kuil Level 3:", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        val sel3 = state.selectedReligions[3]
                        if (sel3 != null) {
                            val desc = if (sel3 == "Ordo Cahaya Murni") "+20% Happiness, -15% Besi/Emas" else "+100% Zenith, -25% Happiness"
                            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer), modifier = Modifier.fillMaxWidth()) {
                                Text("✅ Terpilih: $sel3\n(Buff: $desc)", modifier = Modifier.padding(10.dp), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        } else {
                            Button(onClick = { viewModel.pilihAgama(3, "Ordo Cahaya Murni"); showAgamaDialog = false }, modifier = Modifier.fillMaxWidth()) { Text("Ordo Cahaya Murni (+20% Happiness, -15% Besi/Emas)") }
                            Button(onClick = { viewModel.pilihAgama(3, "Penyembah Setan"); showAgamaDialog = false }, modifier = Modifier.fillMaxWidth()) { Text("Penyembah Setan (+100% Zenith, -25% Happiness)") }
                        }
                    }
                    if (levelTemple >= 4) {
                        Text("Kuil Level 4:", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        val sel4 = state.selectedReligions[4]
                        if (sel4 != null) {
                            val desc = if (sel4 == "Aliran Meditasi") "+30% Potion, -10% Atk" else "+25% Atk, +15% Cost"
                            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer), modifier = Modifier.fillMaxWidth()) {
                                Text("✅ Terpilih: $sel4\n(Buff: $desc)", modifier = Modifier.padding(10.dp), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        } else {
                            Button(onClick = { viewModel.pilihAgama(4, "Aliran Meditasi"); showAgamaDialog = false }, modifier = Modifier.fillMaxWidth()) { Text("Aliran Meditasi (+30% Potion, -10% Atk)") }
                            Button(onClick = { viewModel.pilihAgama(4, "Doktrin Militer Suci"); showAgamaDialog = false }, modifier = Modifier.fillMaxWidth()) { Text("Doktrin Militer Suci (+25% Atk, +15% Cost)") }
                        }
                    }
                    if (levelTemple >= 5) {
                        Text("Kuil Level 5:", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        val sel5 = state.selectedReligions[5]
                        if (sel5 != null) {
                            val desc = if (sel5 == "Kosmisme Agung") "+40% HP Tembok, -20% Oil/Uranium" else "+35% Titanium/Uranium, -10% Gudang"
                            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer), modifier = Modifier.fillMaxWidth()) {
                                Text("✅ Terpilih: $sel5\n(Buff: $desc)", modifier = Modifier.padding(10.dp), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        } else {
                            Button(onClick = { viewModel.pilihAgama(5, "Kosmisme Agung"); showAgamaDialog = false }, modifier = Modifier.fillMaxWidth()) { Text("Kosmisme Agung (+40% HP Tembok, -20% Oil/Uranium)") }
                            Button(onClick = { viewModel.pilihAgama(5, "Sinkretisme Logam"); showAgamaDialog = false }, modifier = Modifier.fillMaxWidth()) { Text("Sinkretisme Logam (+35% Titanium/Uranium, -10% Gudang)") }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showAgamaDialog = false }) { Text("Tutup") } }
        )
    }

    if (showGudangDialog) {
        AlertDialog(
            onDismissRequest = { showGudangDialog = false },
            title = { Text("📦 Isi Gudang (Max: ${state.maxStorage})") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("🪵 Material Dasar:", fontWeight = FontWeight.Bold)
                    Text("Kayu: ${state.kayu}/${state.maxStorage} | Batu: ${state.batu}/${state.maxStorage}\nBesi: ${state.besi}/${state.maxStorage} | Emas: ${state.emas}/${state.maxStorage}")
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("⚙️ Material Lanjut:", fontWeight = FontWeight.Bold)
                    Text("Oil: ${state.oil}/${state.maxStorage} | Listrik: ${state.listrik}/${state.maxStorage}\nTitanium: ${state.titanium}/${state.maxStorage} | Uranium: ${state.uranium}/${state.maxStorage}")
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("🍞 Makanan:", fontWeight = FontWeight.Bold)
                    Text("Daging: ${state.daging}/${state.maxStorage} | Berry: ${state.berry}/${state.maxStorage}\nGandum: ${state.gandum}/${state.maxStorage} | Roti: ${state.roti}/${state.maxStorage}\nKalengan: ${state.kalengan}/${state.maxStorage} | Ikan: ${state.ikan}/${state.maxStorage}")
                }
            },
            confirmButton = { TextButton(onClick = { showGudangDialog = false }) { Text("Tutup") } }
        )
    }
    
    if (showHospitalDialog) {
        val level = state.getBuildingLevel("hospital")
        val cap = viewModel.getHospitalCapacity(level)
        val cost = viewModel.getHospitalBerryCost(level)
        
        AlertDialog(
            onDismissRequest = { showHospitalDialog = false },
            title = { Text("🏥 Rumah Sakit (Lv. $level)") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Kapasitas: $cap Pasukan Terluka")
                    Text("Biaya Perawatan: $cost Berry / Pasukan")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Jumlah Terluka: ${state.woundedPasukan}", fontWeight = FontWeight.Bold, color = Color.Red)
                    Text("Berry Anda: ${state.berry}", fontWeight = FontWeight.Bold, color = Color.Green)
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Button(
                        onClick = { viewModel.healPasukan(1) },
                        enabled = state.woundedPasukan >= 1 && state.berry >= cost,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Sembuhkan 1 Pasukan") }
                    
                    Button(
                        onClick = { viewModel.healPasukan(10) },
                        enabled = state.woundedPasukan >= 10 && state.berry >= cost * 10,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Sembuhkan 10 Pasukan") }
                    
                    Button(
                        onClick = { viewModel.healPasukan(state.woundedPasukan) },
                        enabled = state.woundedPasukan > 0 && state.berry >= cost,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Sembuhkan Maksimal (Sesuai Berry)") }
                }
            },
            confirmButton = { TextButton(onClick = { showHospitalDialog = false }) { Text("Tutup") } }
        )
    }

    if (showGerbangDialog) {
        val level = state.getBuildingLevel("tembok")
        val capacity = viewModel.getGateCapacity(level)
        val maxHeroes = capacity.first
        val maxPasukan = capacity.second
        
        var sliderValue by remember { mutableFloatStateOf(state.garrisonedPasukan.toFloat()) }
        
        AlertDialog(
            onDismissRequest = { showGerbangDialog = false },
            title = { Text("🛡️ Penugasan Gerbang") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Slot Hero: ${state.garrisonedHeroes.size} / $maxHeroes")
                    Text("Hero Bertugas: ${state.garrisonedHeroesStr.ifEmpty { "Tidak ada" }}")
                    Text("(Tugaskan Hero melalui tab Komandan Suku)", fontSize = 12.sp, color = Color.Gray)
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text("Pasukan Gerbang: ${state.garrisonedPasukan} / $maxPasukan")
                    Text("Pasukan Aktif (Barak): ${state.currentPasukan}")
                    
                    val maxPossible = (state.currentPasukan + state.garrisonedPasukan).coerceAtMost(maxPasukan)
                    
                    Slider(
                        value = sliderValue,
                        onValueChange = { sliderValue = it },
                        valueRange = 0f..(maxPossible.toFloat().coerceAtLeast(1f)),
                        steps = if (maxPossible > 1) (maxPossible - 1).toInt() else 0
                    )
                    Text("Atur Pasukan: ${sliderValue.toLong()}", fontWeight = FontWeight.Bold)
                    
                    Button(
                        onClick = { 
                            viewModel.setGarrisonPasukan(sliderValue.toLong()) 
                            showGerbangDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Simpan Penugasan") }
                }
            },
            confirmButton = { TextButton(onClick = { showGerbangDialog = false }) { Text("Batal") } }
        )
    }

    if (showPrehistoricTroopsDialog) {
        PrehistoricTroopsDialog(
            state = state,
            viewModel = viewModel,
            onDismiss = { showPrehistoricTroopsDialog = false }
        )
    }

    Column(modifier = modifier.fillMaxSize()) {
        ScrollableTabRow(
            selectedTabIndex = if (selectedTab >= tabs.size) { selectedTab = 0; 0 } else selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            edgePadding = 0.dp
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = {
                        selectedTab = index
                    },
                    text = { Text(title) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        val currentType = tabs[selectedTab].lowercase()
        if (currentType.contains("riset")) {
            RisetTab(state, viewModel, onOpenCosmicMap = onShowTalentTree)
        } else if (currentType == "market") {
            MarketTab(state, viewModel)
        } else if (currentType == "🌐 kebangsaan") {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (state.selectedNationality != "Belum Memilih") {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                "🌐 Kebangsaan Kota: ${state.selectedNationality}",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                textAlign = TextAlign.Center
                            )
                            val buffDesc = when (state.selectedNationality) {
                                "Nusantara" -> "🔥 Buff Aktif: +50% Produksi Kayu, +20% Produksi Pangan"
                                "Sparta" -> "🔥 Buff Aktif: +50% Attack Pasukan, -20% Produksi Emas"
                                "Romawi" -> "🔥 Buff Aktif: +50% Max HP Tembok, -10% Biaya Konstruksi Bangunan"
                                "Mesir Kuno" -> "🔥 Buff Aktif: +50% Keuntungan Zenith, -10% Produksi Pangan"
                                "Jepang Shogunate" -> "🔥 Buff Aktif: +30% Attack Pasukan, +30% Produksi Besi"
                                "Nordik" -> "🔥 Buff Aktif: +30% Produksi Kayu, +30% Produksi Emas"
                                "Britania" -> "🔥 Buff Aktif: +50% Produksi Emas, +20% Max HP Tembok"
                                "Cyber-Neo" -> "🔥 Buff Aktif: +50% Produksi Titanium, +50% Produksi Uranium, -30% Produksi Pangan"
                                "Atlantis" -> "🔥 Buff Aktif: +100% Keuntungan Zenith, -50% Produksi Besi"
                                "Mongol" -> "🔥 Buff Aktif: +40% Attack Pasukan, -30% Biaya Konstruksi Barak"
                                else -> "Kekuatan peradaban aktif."
                            }
                            Text(
                                buffDesc,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    Text("Pilih Kebangsaan Kota Anda", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Setiap kebangsaan memberikan bonus stat peradaban yang unik dan permanen.", fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { showNationalityDialog = true },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        Text("Pilih Kebangsaan")
                    }
                }
            }
        } else if (currentType.contains("kamus max")) {
            KamusMaxTab()
        } else if (currentType == "filsafat") {
            FilsafatTab(state, viewModel)
        } else if (currentType.contains("komandan")) {
            KomandanTab(state, viewModel)
        } else {
            GenericTab(state, viewModel, currentType, 
                onShowStat = { showStatDialog = true }, 
                onShowAgama = { showAgamaDialog = true }, 
                onShowGudang = { showGudangDialog = true },
                onShowGerbang = { showGerbangDialog = true },
                onShowHospital = { showHospitalDialog = true },
                onShowTavern = onShowTavern,
                onShowPasukanPurba = { showPrehistoricTroopsDialog = true }
            )
        }
    }
}

fun getPhilosophyIconRes(itemId: String): Int {
    return when (itemId) {
        "barter_purba" -> R.drawable.ic_fil_perdagangan
        "hukum_kodeks" -> R.drawable.ic_fil_hukum
        "monarki" -> R.drawable.ic_fil_monarki
        "feodalisme" -> R.drawable.ic_fil_feodalisme
        "meritokrasi" -> R.drawable.ic_fil_meritokrasi
        "teokrasi" -> R.drawable.ic_fil_teokrasi
        "kapitalisme" -> R.drawable.ic_fil_kapitalisme
        "sosialisme" -> R.drawable.ic_fil_sosialisme
        "otoritarianisme" -> R.drawable.ic_fil_otoritarianisme
        "transhumanisme" -> R.drawable.ic_fil_transhumanisme
        else -> R.drawable.ic_fil_hukum
    }
}

@Composable
fun FilsafatTab(state: GameState, viewModel: GameViewModel) {
    val philosophyNodes = remember { allNodes.filter { it.type == "filsafat" } }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "📜 DOKTRIN & FILSAFAT PERADABAN",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD700)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Adopsi doktrin filsafat untuk memberikan peningkatan permanen pada evolusi kebudayaan, ekonomi, dan pertahanan suku Anda.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Total Zenith: ⭐ ${state.totalZenith.toLong()} Z",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFBBF24)
                    )
                }
            }
        }
        
        items(philosophyNodes) { node ->
            val isUnlocked = state.isUnlocked(node.id)
            val canAfford = state.totalZenith >= node.zenithCost
            val iconRes = getPhilosophyIconRes(node.id)
            
            val eraTag = when (node.tier) {
                1 -> "Era I • Prasejarah"
                2 -> "Era II • Kerajaan"
                3 -> "Era III • Bangsa"
                4 -> "Era IV • Modern"
                5 -> "Era V • Cyberpunk"
                else -> "Era ${node.tier}"
            }
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isUnlocked) Color(0xFF064E3B).copy(alpha = 0.6f) else MaterialTheme.colorScheme.surfaceVariant
                ),
                border = BorderStroke(
                    width = if (isUnlocked) 1.5.dp else 1.dp,
                    color = if (isUnlocked) Color(0xFF10B981) else Color(0xFF334155)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = iconRes),
                        contentDescription = node.name,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1A102E))
                            .border(1.dp, if (isUnlocked) Color(0xFF10B981) else Color(0xFF4A3E72), RoundedCornerShape(8.dp))
                            .padding(4.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = node.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (isUnlocked) Color(0xFFFFD700) else MaterialTheme.colorScheme.onSurface
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF334155))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(eraTag, fontSize = 9.sp, color = Color.White)
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = node.desc,
                            fontSize = 11.sp,
                            color = if (isUnlocked) Color(0xFF34D399) else Color(0xFF94A3B8),
                            fontWeight = FontWeight.SemiBold
                        )
                        if (!isUnlocked) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Biaya: ⭐ ${node.zenithCost.toLong()} Zenith",
                                fontSize = 10.sp,
                                color = if (canAfford) Color(0xFFFBBF24) else Color(0xFFEF4444),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { viewModel.buyNode(node.id) },
                        enabled = !isUnlocked && canAfford,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF7C3AED),
                            disabledContainerColor = if (isUnlocked) Color(0xFF065F46) else Color(0xFF334155)
                        )
                    ) {
                        Text(
                            text = if (isUnlocked) "✅ Teradopsi" else if (canAfford) "📜 Terapkan" else "🔒 Zenith Kurang",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isUnlocked) Color(0xFF34D399) else Color.White
                        )
                    }
                }
            }
        }
    }
}

fun getWeaponIconRes(itemId: String): Int {
    return when (itemId) {
        "tongkat_kayu" -> R.drawable.ic_wp_tombak
        "kulit_hewan" -> R.drawable.ic_wp_armor_kulit
        "pedang_besi" -> R.drawable.ic_wp_pedang
        "armor_besi" -> R.drawable.ic_wp_baju_besi
        "musket" -> R.drawable.ic_wp_musket
        "armor_kevlar" -> R.drawable.ic_wp_railgun
        "assault_rifle" -> R.drawable.ic_wp_assault_rifle
        "exo_suit" -> R.drawable.ic_wp_exosuit
        "energy_katana" -> R.drawable.ic_wp_nano_kirana
        "nano_armor" -> R.drawable.ic_wp_plasma_cannon
        "planet_breaker" -> R.drawable.ic_wp_laser_blade
        "cyber_body" -> R.drawable.ic_wp_cyber_body
        else -> R.drawable.ic_wp_tombak
    }
}

@Composable
fun KomandanTab(state: GameState, viewModel: GameViewModel) {
    var komandanSubTab by remember { mutableIntStateOf(0) }
    
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        TabRow(
            selectedTabIndex = komandanSubTab,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Tab(
                selected = komandanSubTab == 0,
                onClick = { komandanSubTab = 0 },
                text = { Text("👥 Pahlawan & Shard", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
            )
            Tab(
                selected = komandanSubTab == 1,
                onClick = { komandanSubTab = 1 },
                text = { Text("⚔️ Inventori Peralatan", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
            )
        }
        
        Spacer(modifier = Modifier.height(10.dp))
        
        if (komandanSubTab == 0) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("💎 Shard Hero (Combine)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            val inv = state.inventory
                            
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("Rare Shard: ${inv["shard_r"] ?: 0}/20")
                                Button(onClick = { viewModel.combineShards("r") }, enabled = (inv["shard_r"] ?: 0) >= 20) { Text("💥 Combine") }
                            }
                            
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("SR Shard: ${inv["shard_sr"] ?: 0}/50")
                                Button(onClick = { viewModel.combineShards("sr") }, enabled = (inv["shard_sr"] ?: 0) >= 50) { Text("💥 Combine") }
                            }
                            
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("SSR Shard: ${inv["shard_ssr"] ?: 0}/100")
                                Button(onClick = { viewModel.combineShards("ssr") }, enabled = (inv["shard_ssr"] ?: 0) >= 100) { Text("💥 Combine") }
                            }
                            
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("UR Shard: ${inv["shard_ur"] ?: 0}/150")
                                Button(onClick = { viewModel.combineShards("ur") }, enabled = (inv["shard_ur"] ?: 0) >= 150) { Text("💥 Combine") }
                            }
                        }
                    }
                }
                val commanders = state.ownedCommanders
                if (commanders.isEmpty()) {
                    item {
                        Text(
                            "Anda belum memiliki Komandan Suku. Lakukan Gacha di Kedai (Tavern) atau beli di Market!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                items(commanders) { commanderEntry ->
                    val parts = commanderEntry.split(":")
                    val heroName = parts[0]
                    val stars = if (parts.size > 1) parts[1].toIntOrNull() ?: 1 else 1
                    
                    val rarityColor = when {
                        heroName.contains("(Mythical)") -> getRarityColor("Mythical")
                        heroName.contains("(Legend)") -> getRarityColor("Legend")
                        heroName.contains("(Very Rare)") -> getRarityColor("Very Rare")
                        heroName.contains("(Rare)") -> getRarityColor("Rare")
                        else -> Color.Gray
                    }
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "👤",
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(heroName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = rarityColor)
                                Text("⭐ Bintang $stars", style = MaterialTheme.typography.bodyMedium, color = Color(0xFFFFD700))
                                
                                var heroBaseDamage = 0L
                                var heroBaseHp = 0L
                                if (heroName.contains("(Mythical)")) { heroBaseDamage = 50000L; heroBaseHp = 200000L }
                                else if (heroName.contains("(Legend)")) { heroBaseDamage = 10000L; heroBaseHp = 40000L }
                                else if (heroName.contains("(Very Rare)")) { heroBaseDamage = 2500L; heroBaseHp = 10000L }
                                else if (heroName.contains("(Rare)")) { heroBaseDamage = 500L; heroBaseHp = 2000L }
                                else { heroBaseDamage = 50L; heroBaseHp = 200L }
                                
                                val multiplier = 1.0 + (stars - 1) * 0.1
                                val actualDamage = (heroBaseDamage * multiplier).toLong()
                                val actualHp = (heroBaseHp * multiplier).toLong()
                                
                                Text("Pasif: +$actualDamage Attack, +$actualHp HP", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                val isGarrisoned = state.garrisonedHeroes.contains(heroName)
                                Button(
                                    onClick = { viewModel.toggleGarrisonHero(heroName) },
                                    colors = ButtonDefaults.buttonColors(containerColor = if (isGarrisoned) Color.Red else Color.Green)
                                ) {
                                    Text(if (isGarrisoned) "Lepas Jaga" else "Tugaskan Jaga")
                                }
                            }
                        }
                    }
                }
            }
        } else {
            ArmorySection(state, viewModel)
        }
    }
}

@Composable
fun ArmorySection(state: GameState, viewModel: GameViewModel) {
    val weaponsList = remember { allNodes.filter { it.type == "senjata/armor" } }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("⚔️ INVENTORI PERALATAN & TEMPA", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("Tempa persenjataan dan armor untuk memperkuat komandan suku dan prajurit pertahanan gerbang.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        
        items(weaponsList) { itemNode ->
            val isUnlocked = state.isUnlocked(itemNode.id)
            val iconRes = getWeaponIconRes(itemNode.id)
            val canAfford = canAffordNode(state, itemNode.zenithCost, itemNode.resourceCosts)
            val costStr = formatCost(itemNode.zenithCost, itemNode.resourceCosts)
            
            val isArmor = itemNode.id.contains("armor") || itemNode.id == "kulit_hewan" || itemNode.id == "exo_suit" || itemNode.id == "cyber_body"
            val typeBadge = if (isArmor) "🛡️ Armor" else "🗡️ Senjata"
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isUnlocked) Color(0xFF0F2B1D) else MaterialTheme.colorScheme.surfaceVariant
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (isUnlocked) Color(0xFF10B981) else Color(0xFF334155)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = iconRes),
                        contentDescription = itemNode.name,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1E1035))
                            .padding(4.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(itemNode.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = if (isUnlocked) Color(0xFFFFD700) else MaterialTheme.colorScheme.onSurface)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF334155))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(typeBadge, fontSize = 9.sp, color = Color.White)
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(itemNode.desc, fontSize = 12.sp, color = if (isUnlocked) Color(0xFF34D399) else Color(0xFF94A3B8), fontWeight = FontWeight.SemiBold)
                        if (!isUnlocked) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Biaya: $costStr", fontSize = 10.sp, color = if (canAfford) Color(0xFFFBBF24) else Color(0xFFEF4444))
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { viewModel.buyNode(itemNode.id) },
                        enabled = !isUnlocked && canAfford,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = if (isUnlocked) Color(0xFF065F46) else Color.DarkGray
                        )
                    ) {
                        Text(
                            text = if (isUnlocked) "✅ Dikuasai" else if (canAfford) "🔨 Tempa" else "🔒 Bahan Kurang",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RisetTab(
    state: GameState,
    viewModel: GameViewModel,
    onOpenCosmicMap: () -> Unit
) {
    var selectedEra by remember { mutableIntStateOf(0) }
    val allSkills = remember { allNodes.filter { it.type == "skill" } }
    
    val filteredSkills = remember(selectedEra) {
        if (selectedEra == 0) allSkills
        else allSkills.filter { it.tier == selectedEra }
    }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("🔬 POHON RISET PERADABAN", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text("Total Zenith: ⭐ ${state.totalZenith.toLong()} Z", style = MaterialTheme.typography.bodySmall, color = Color(0xFFFBBF24), fontWeight = FontWeight.SemiBold)
                        }
                        Button(
                            onClick = onOpenCosmicMap,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))
                        ) {
                            Text("🌌 Peta Kosmis", fontSize = 12.sp)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    val eraList = listOf("Semua", "I. Prasejarah", "II. Agraris", "III. Industri", "IV. Modern", "V. Cyberpunk")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        itemsIndexed(eraList) { index, eraTitle ->
                            val isSelected = selectedEra == index
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedEra = index },
                                label = { Text(eraTitle, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) }
                            )
                        }
                    }
                }
            }
        }
        
        items(filteredSkills) { node ->
            val isUnlocked = state.isUnlocked(node.id)
            val isAvailable = node.reqs.isEmpty() || node.reqs.all { state.isUnlocked(it) }
            val canAfford = state.totalZenith >= node.zenithCost
            
            val eraName = when (node.tier) {
                1 -> "Era I: Prasejarah"
                2 -> "Era II: Agraris Kerajaan"
                3 -> "Era III: Industri Bangsa"
                4 -> "Era IV: Modern Teknologi"
                5 -> "Era V: Cyberpunk Singularity"
                else -> "Era ${node.tier}"
            }
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isUnlocked) Color(0xFF064E3B).copy(alpha = 0.5f)
                                     else if (isAvailable) MaterialTheme.colorScheme.surfaceVariant
                                     else Color(0xFF1E293B).copy(alpha = 0.5f)
                ),
                border = BorderStroke(
                    width = if (isUnlocked) 1.5.dp else if (isAvailable) 1.5.dp else 1.dp,
                    color = if (isUnlocked) Color(0xFF10B981)
                            else if (isAvailable) Color(0xFF00E5FF).copy(alpha = 0.8f)
                            else Color(0xFF334155)
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = node.name,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isUnlocked) Color(0xFFFBBF24) else if (isAvailable) Color.White else Color.Gray
                            )
                            Text(
                                text = eraName,
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                        
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (isUnlocked) Color(0xFF10B981).copy(alpha = 0.2f)
                                    else if (isAvailable) Color(0xFF00E5FF).copy(alpha = 0.2f)
                                    else Color.DarkGray.copy(alpha = 0.3f)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (isUnlocked) "✅ Dikuasai" else if (isAvailable) "⚡ Siap Riset" else "🔒 Terkunci",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isUnlocked) Color(0xFF34D399) else if (isAvailable) Color(0xFF00E5FF) else Color.LightGray
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    Text(
                        text = node.desc,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    if (node.story.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "\"${node.story}\"",
                            fontSize = 11.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            color = Color(0xFF94A3B8)
                        )
                    }
                    
                    if (node.reqs.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Prasyarat:", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                            node.reqs.forEach { reqId ->
                                val parent = allSkills.find { it.id == reqId }
                                val reqMet = state.isUnlocked(reqId)
                                Text(
                                    text = if (reqMet) "✓ ${parent?.name ?: reqId}" else "🔒 ${parent?.name ?: reqId}",
                                    fontSize = 10.sp,
                                    color = if (reqMet) Color(0xFF34D399) else Color(0xFFEF4444),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Biaya: ⭐ ${node.zenithCost.toLong()} Zenith",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isUnlocked) Color.Gray else if (canAfford) Color(0xFFFBBF24) else Color(0xFFEF4444)
                        )
                        
                        Button(
                            onClick = { viewModel.buyNode(node.id) },
                            enabled = isAvailable && !isUnlocked && canAfford,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00E5FF),
                                contentColor = Color.Black,
                                disabledContainerColor = if (isUnlocked) Color(0xFF065F46) else Color(0xFF334155),
                                disabledContentColor = if (isUnlocked) Color(0xFF34D399) else Color.LightGray
                            )
                        ) {
                            Text(
                                text = if (isUnlocked) "✅ Selesai"
                                       else if (!isAvailable) "🔒 Prasyarat Belum"
                                       else if (!canAfford) "⚠️ Zenith Kurang"
                                       else "🔬 Riset Sekarang",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

data class DungeonShopItem(
    val id: String,
    val name: String,
    val itemId: String,
    val cost: Long,
    val origCost: Long? = null,
    val desc: String,
    val minMarketLvl: Int,
    val emoji: String,
    val rarity: String,
    val tierInfo: String
)

data class RaidShopItem(
    val id: String,
    val name: String,
    val cost: Long,
    val desc: String,
    val minMarketLvl: Int,
    val emoji: String,
    val rarity: String,
    val tierInfo: String
)

@Composable
fun ShopItemCard(
    name: String,
    desc: String,
    cost: Long,
    origCost: Long?,
    currencyEmoji: String,
    emojiIcon: String,
    rarity: String,
    tierInfo: String?,
    isLocked: Boolean,
    lockMessage: String,
    onBuyClick: () -> Unit,
    isEnabled: Boolean
) {
    val rarityColor = when (rarity) {
        "Rare" -> Color(0xFF2563EB)
        "SR" -> Color(0xFF7C3AED)
        "SSR" -> Color(0xFFEA580C)
        "UR" -> Color(0xFFDC2626)
        else -> Color(0xFF64748B)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(165.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0F172A).copy(alpha = 0.8f))
            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Section (Icon and Details)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Item Icon Box
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(rarityColor, RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(emojiIcon, fontSize = 22.sp)
                }

                // Name & Desc Column
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = desc,
                        fontSize = 10.sp,
                        color = Color.LightGray,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 12.sp
                    )
                }
            }

            // Bottom Section (Price & Buy Button)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Currency & Cost
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(currencyEmoji, fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "$cost",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = Color(0xFFFBBF24)
                        )
                    }
                    if (origCost != null) {
                        Text(
                            text = "$origCost",
                            fontSize = 10.sp,
                            color = Color.Gray,
                            style = TextStyle(textDecoration = TextDecoration.LineThrough)
                        )
                    }
                }

                // Mini Buy Button
                Button(
                    onClick = onBuyClick,
                    enabled = isEnabled,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF10B981),
                        disabledContainerColor = Color(0xFF1E293B)
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                    modifier = Modifier
                        .height(30.dp)
                        .testTag("buy_shop_item_${name.lowercase().replace(" ", "_")}")
                ) {
                    Text(
                        text = "Beli",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isEnabled) Color.White else Color.Gray
                    )
                }
            }
        }

        // Tier Ribbon / Ribbon Tag (top-right corner)
        if (tierInfo != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .background(
                        color = if (tierInfo.contains("Off")) Color(0xFFDC2626) else rarityColor,
                        shape = RoundedCornerShape(bottomStart = 8.dp, topEnd = 12.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = tierInfo,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // Locked Overlay State
        if (isLocked) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.82f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("🔒", fontSize = 24.sp)
                    Text(
                        text = lockMessage,
                        color = Color(0xFFEF4444),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun MarketTab(state: GameState, viewModel: GameViewModel) {
    val marketLvl = state.getBuildingLevel("market")
    var selectedSubTab by remember { mutableIntStateOf(0) }
    val subTabs = listOf("💰 Toko Dungeon", "🪙 Toko Raid", "💎 Tukar Shard")
    
    val inv = state.inventory
    
    // Define Dungeon Shop items list
    val dungeonShopItems = listOf(
        DungeonShopItem("hp_pot", "Potion HP Lv.1", "hp_potion", 200, null, "Sembuhkan hero saat perang", 1, "🧪", "Rare", "Lv. 1"),
        DungeonShopItem("atk_pot", "Potion Atk Lv.1", "atk_potion", 200, null, "Tambah daya serang hero", 1, "⚡", "Rare", "Lv. 1"),
        DungeonShopItem("def_pot", "Potion Def Lv.1", "def_potion", 250, null, "Tambah pertahanan hero", 1, "🛡️", "Rare", "Lv. 1"),
        DungeonShopItem("wood_100", "100 Kayu", "kayu_100", 200, null, "Bahan bangunan dasar", 1, "🪵", "Rare", "Pack"),
        DungeonShopItem("stone_100", "100 Batu", "batu_100", 200, null, "Bahan bangunan kokoh", 1, "🪨", "Rare", "Pack"),
        DungeonShopItem("meat_100", "100 Daging", "daging_100", 250, null, "Konsumsi pahlawan", 1, "🍖", "Rare", "Pack"),
        
        DungeonShopItem("wood_250", "250 Kayu", "kayu_250", 450, 500, "Paket hemat bahan kayu", 2, "🪵", "SR", "10% Off"),
        DungeonShopItem("stone_250", "250 Batu", "batu_250", 450, 500, "Paket hemat bahan batu", 2, "🪨", "SR", "10% Off"),
        DungeonShopItem("iron_250", "250 Besi", "besi_250", 500, null, "Logam kuat peradaban", 2, "⛓️", "SR", "SR"),
        DungeonShopItem("gold_250", "250 Emas", "emas_250", 600, null, "Emas mulia berharga", 2, "🪙", "SR", "SR"),
        
        DungeonShopItem("shard_r_item", "Shard Rare", "shard_r", 50, null, "Bahan panggil pahlawan", 3, "💎", "Rare", "Rare"),
        DungeonShopItem("shard_sr_item", "Shard SR", "shard_sr", 200, null, "Bahan panggil pahlawan unggul", 3, "💎", "SR", "SR"),
        DungeonShopItem("shard_ssr_item", "Shard SSR", "shard_ssr", 500, null, "Bahan panggil pahlawan legenda", 3, "💎", "SSR", "SSR"),
        
        DungeonShopItem("ai_core", "Inti AI", "item_sr", 8000, null, "Teknologi AI canggih", 4, "🧠", "SR", "SR"),
        DungeonShopItem("nano_part", "Partikel Nano", "item_ssr", 30000, null, "Teknologi skala molekul", 4, "⚛️", "SSR", "SSR")
    )
    
    // Define Raid Shop items based on GAME_COIN_SHOP
    val raidShopItems = GAME_COIN_SHOP.map { item ->
        val (emoji, rarity, tierInfo) = when {
            item.id.contains("sword") || item.id.contains("laser") || item.id.contains("cyber") || item.id.contains("ak47") || item.id.contains("pistol") || item.id.contains("carbine") -> Triple("🗡️", "SSR", "Weapon")
            item.id.contains("shield") -> Triple("🛡️", "Rare", "Shield")
            item.id.contains("trap") -> Triple("🕸️", "Rare", "Trap")
            item.id.contains("cannon") || item.id.contains("crossbow") -> Triple("💣", "SR", "Defense")
            else -> Triple("📦", "Rare", "Item")
        }
        val actualRarity = when (item.minMarketLvl) {
            1 -> "Rare"
            2 -> "SR"
            3 -> "SSR"
            else -> "UR"
        }
        RaidShopItem(
            id = item.id,
            name = item.name,
            cost = item.cost,
            desc = item.desc,
            minMarketLvl = item.minMarketLvl,
            emoji = emoji,
            rarity = actualRarity,
            tierInfo = tierInfo
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- HEADER CURRENCY DISPLAY ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.9f)),
                border = BorderStroke(2.dp, Color(0xFF1E293B))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "🏪 KIOS PERADABAN ZENITH", 
                        style = MaterialTheme.typography.titleLarge, 
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFFBBF24)
                    )
                    Text(
                        "Pusat perdagangan & pertukaran barang antardimensi.", 
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    // Currencies Grid Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Dungeon Coins
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B).copy(alpha = 0.6f)),
                            border = BorderStroke(1.dp, Color(0xFFD97706).copy(alpha = 0.5f))
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("💰 Koin Dungeon", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("${state.dungeonCoins}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFBBF24))
                            }
                        }
                        
                        // Game Coins (Raid Shop)
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B).copy(alpha = 0.6f)),
                            border = BorderStroke(1.dp, Color(0xFFEAB308).copy(alpha = 0.5f))
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("🪙 Koin Game", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("${state.gameCoins}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFACC15))
                            }
                        }
                        
                        // Shard Tokens
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B).copy(alpha = 0.6f)),
                            border = BorderStroke(1.dp, Color(0xFF06B6D4).copy(alpha = 0.5f))
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("💎 Shard Token", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("${state.shardTokens}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF22D3EE))
                            }
                        }
                    }
                }
            }
        }
        
        // --- EXPANDABLE INVENTORY & SELL LOOT CARD ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B).copy(alpha = 0.8f)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                var expanded by remember { mutableStateOf(false) }
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("🎒", fontSize = 20.sp)
                            Column {
                                Text("Tas Inventaris", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = Color.White)
                                Text("Lihat sisa loot monster & jual langsung", fontSize = 11.sp, color = Color.LightGray)
                            }
                        }
                        IconButton(onClick = { expanded = !expanded }) {
                            Text(if (expanded) "🔼" else "🔽", color = Color.White)
                        }
                    }
                    
                    if (expanded) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(color = Color.White.copy(alpha = 0.1f))
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Inventory details list
                        Text("Bahan & Ramuan:", fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8), fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text("🧪 HP Potion: ${state.hpPotions}", color = Color.White, fontSize = 12.sp)
                            Text("⚡ Atk Potion: ${inv["atk_potion"] ?: 0}", color = Color.White, fontSize = 12.sp)
                            Text("🛡️ Def Potion: ${inv["def_potion"] ?: 0}", color = Color.White, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text("🔮 Shard SR: ${inv["shard_sr"] ?: 0}", color = Color.White, fontSize = 12.sp)
                            Text("🔮 Shard SSR: ${inv["shard_ssr"] ?: 0}", color = Color.White, fontSize = 12.sp)
                            Text("🔮 Shard UR: ${inv["shard_ur"] ?: 0}", color = Color.White, fontSize = 12.sp)
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Jual Loot Berharga (Tukar Koin Dungeon):", fontWeight = FontWeight.Bold, color = Color(0xFFFBBF24), fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Rare Item Row
                            val rareCount = inv["item_rare"] ?: 0
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("📦 Loot Langka (Rare)", color = Color(0xFF60A5FA), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Text("Milik: $rareCount", color = Color.White, fontSize = 12.sp)
                                    Button(
                                        onClick = { viewModel.jualItemDungeon("item_rare") },
                                        enabled = rareCount > 0,
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text("Jual (+10 💰)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            
                            // SR Item Row
                            val srCount = inv["item_sr"] ?: 0
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("📦 Loot Superior (SR)", color = Color(0xFFC084FC), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Text("Milik: $srCount", color = Color.White, fontSize = 12.sp)
                                    Button(
                                        onClick = { viewModel.jualItemDungeon("item_sr") },
                                        enabled = srCount > 0,
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text("Jual (+50 💰)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            
                            // SSR Item Row
                            val ssrCount = inv["item_ssr"] ?: 0
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("📦 Loot Legendaris (SSR)", color = Color(0xFFF97316), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Text("Milik: $ssrCount", color = Color.White, fontSize = 12.sp)
                                    Button(
                                        onClick = { viewModel.jualItemDungeon("item_ssr") },
                                        enabled = ssrCount > 0,
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA580C)),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text("Jual (+150 💰)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // --- SUB-TAB CATEGORY SELECTOR ---
        item {
            ScrollableTabRow(
                selectedTabIndex = selectedSubTab,
                containerColor = Color.Transparent,
                contentColor = Color(0xFFFBBF24),
                edgePadding = 0.dp,
                divider = {}
            ) {
                subTabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedSubTab == index,
                        onClick = { selectedSubTab = index },
                        text = { 
                            Text(
                                title, 
                                fontWeight = if (selectedSubTab == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            ) 
                        }
                    )
                }
            }
        }
        
        // --- SHOP CONTENT GRID (2 COLUMNS) ---
        if (selectedSubTab == 0) {
            // --- DUNGEON SHOP ---
            val rows = dungeonShopItems.chunked(2)
            rows.forEach { rowItems ->
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowItems.forEach { item ->
                            val isLocked = marketLvl < item.minMarketLvl
                            Box(modifier = Modifier.weight(1f)) {
                                ShopItemCard(
                                    name = item.name,
                                    desc = item.desc,
                                    cost = item.cost,
                                    origCost = item.origCost,
                                    currencyEmoji = "💰",
                                    emojiIcon = item.emoji,
                                    rarity = item.rarity,
                                    tierInfo = item.tierInfo,
                                    isLocked = isLocked,
                                    lockMessage = "Market Lv. ${item.minMarketLvl}",
                                    onBuyClick = { viewModel.buyMarketItem(item.itemId, item.cost) },
                                    isEnabled = state.dungeonCoins >= item.cost
                                )
                            }
                        }
                        if (rowItems.size < 2) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        } else if (selectedSubTab == 1) {
            // --- RAID SHOP ---
            val rows = raidShopItems.chunked(2)
            rows.forEach { rowItems ->
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowItems.forEach { item ->
                            val isLocked = marketLvl < item.minMarketLvl
                            val owned = state.inventory[item.id] ?: 0
                            Box(modifier = Modifier.weight(1f)) {
                                ShopItemCard(
                                    name = "${item.name} ($owned)",
                                    desc = item.desc,
                                    cost = item.cost,
                                    origCost = null,
                                    currencyEmoji = "🪙",
                                    emojiIcon = item.emoji,
                                    rarity = item.rarity,
                                    tierInfo = item.tierInfo,
                                    isLocked = isLocked,
                                    lockMessage = "Market Lv. ${item.minMarketLvl}",
                                    onBuyClick = { viewModel.buyCoinShopItem(item.id, item.cost) },
                                    isEnabled = state.gameCoins >= item.cost
                                )
                            }
                        }
                        if (rowItems.size < 2) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        } else {
            // --- SHARD EXCHANGE ---
            val shardExchangeItems = listOf(
                Triple("shard_r_random", "Random Shard Rare", 3L),
                Triple("shard_sr_random", "Random Shard SR", 10L),
                Triple("shard_ssr_random", "Random Shard SSR", 25L),
                Triple("shard_ur_random", "Random Shard UR", 150L)
            )
            
            val rows = shardExchangeItems.chunked(2)
            rows.forEach { rowItems ->
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowItems.forEach { item ->
                            val (id, name, cost) = item
                            val rarity = when (id) {
                                "shard_r_random" -> "Rare"
                                "shard_sr_random" -> "SR"
                                "shard_ssr_random" -> "SSR"
                                else -> "UR"
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                ShopItemCard(
                                    name = name,
                                    desc = "Dapatkan 1 pecahan acak pahlawan.",
                                    cost = cost,
                                    origCost = null,
                                    currencyEmoji = "💎",
                                    emojiIcon = "🔮",
                                    rarity = rarity,
                                    tierInfo = rarity,
                                    isLocked = false,
                                    lockMessage = "",
                                    onBuyClick = { viewModel.exchangeShardToken(id, cost.toInt()) },
                                    isEnabled = state.shardTokens >= cost
                                )
                            }
                        }
                        if (rowItems.size < 2) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun PrehistoricTroopsDialog(
    state: GameState,
    viewModel: GameViewModel,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF020617).copy(alpha = 0.94f))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                border = BorderStroke(2.dp, Color(0xFFF59E0B)),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 680.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "🪓 PASUKAN ZAMAN PRASEJARAH",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFF59E0B)
                                )
                            )
                            Text(
                                "Genesis Pixel • Satuan Tempur Purba",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8))
                            )
                        }
                        IconButton(onClick = onDismiss) {
                            Text("✕", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        }
                    }

                    // Status Pasukan
                    Surface(
                        color = Color(0xFF1E293B),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Status Garnisun / Pasukan Aktif", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8)))
                                Text(
                                    "${state.currentPasukan} / ${state.maxPasukan} Prajurit Purba",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF22C55E))
                                )
                                Text(
                                    "Biaya Latih: 15 Kayu, 10 Batu, 5 Berry / Pasukan",
                                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFE2E8F0), fontSize = 11.sp)
                                )
                            }
                            Button(
                                onClick = { viewModel.latihPasukan() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                enabled = state.currentPasukan < state.maxPasukan
                            ) {
                                Text("⚔️ Latih (+1)")
                            }
                        }
                    }

                    // Unit 1: Pasukan Kapak Batu (Stone Axe Hunter)
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B)),
                        border = BorderStroke(1.5.dp, Color(0xFFD97706)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            // High-res pixel art asset
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF0F172A))
                                    .border(1.dp, Color(0xFFD97706), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.img_unit_stone_axe),
                                    contentDescription = "Pasukan Kapak Batu",
                                    modifier = Modifier.size(64.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Pasukan Kapak Batu",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFBBF24)
                                    )
                                )
                                Text(
                                    "Peran: 🛡️ Barisan Depan (Heavy Melee)",
                                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFCBD5E1))
                                )
                                Text(
                                    "Senjata: Kapak Batu Serpih & Gagang Kayu",
                                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF94A3B8))
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Pemburu purba berbalut kulit macan tutul yang mengayunkan kapak batu berikat serat. Memiliki pertahanan fisik tangguh dan tebasan kapak mematikan untuk memecah formasi musuh di Dungeon.",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp,
                                        color = Color(0xFFE2E8F0)
                                    )
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Badge(containerColor = Color(0xFFB45309)) { Text("⚔️ ATK: +25", color = Color.White) }
                                    Badge(containerColor = Color(0xFF1E3A8A)) { Text("❤️ HP: +120", color = Color.White) }
                                    Badge(containerColor = Color(0xFF047857)) { Text("🛡️ DEF: +15", color = Color.White) }
                                }
                            }
                        }
                    }

                    // Unit 2: Pasukan Tombak (Spear Gatherer)
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF064E3B)),
                        border = BorderStroke(1.5.dp, Color(0xFF10B981)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            // High-res pixel art asset
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF0F172A))
                                    .border(1.dp, Color(0xFF10B981), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.img_unit_spear),
                                    contentDescription = "Pasukan Tombak",
                                    modifier = Modifier.size(64.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Pasukan Tombak Purba",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF34D399)
                                    )
                                )
                                Text(
                                    "Peran: ⚡ Penusuk Cepat (Piercing Vanguard)",
                                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFCBD5E1))
                                )
                                Text(
                                    "Senjata: Tombak Runcing Batu & Pin Tulang",
                                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF94A3B8))
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Pengumpul dan pejuang wanita lincah dengan sanggul rambut bertusuk tulang. Menyerang dengan kecepatan tinggi menggunakan tombak batu panjang dari jarak aman lini kedua.",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp,
                                        color = Color(0xFFE2E8F0)
                                    )
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Badge(containerColor = Color(0xFFB45309)) { Text("⚔️ ATK: +35", color = Color.White) }
                                    Badge(containerColor = Color(0xFF1E3A8A)) { Text("❤️ HP: +80", color = Color.White) }
                                    Badge(containerColor = Color(0xFF047857)) { Text("💨 SPD: +20", color = Color.White) }
                                }
                            }
                        }
                    }

                    // Galeri Aset Prasejarah
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                "🏛️ Galeri Aset Permukiman Purba",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF38BDF8)
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.img_building_chief_hut),
                                        contentDescription = "Balai Kepala Suku",
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFF0F172A))
                                    )
                                    Text("Balai Suku", fontSize = 10.sp, color = Color(0xFFCBD5E1))
                                }
                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.img_campfire_stones),
                                        contentDescription = "Api Unggun Batu",
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFF0F172A))
                                    )
                                    Text("Api Batu", fontSize = 10.sp, color = Color(0xFFCBD5E1))
                                }
                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.img_prehistoric_settlement),
                                        contentDescription = "Desa Purba",
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFF0F172A))
                                    )
                                    Text("Desa Purba", fontSize = 10.sp, color = Color(0xFFCBD5E1))
                                }
                            }
                        }
                    }

                    // Upgrade Gen Militer
                    Button(
                        onClick = { viewModel.upgradeGenMiliter() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("🧬 Upgrade Gen Militer (100 Koin Dungeon)")
                    }

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF475569)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Tutup")
                    }
                }
            }
        }
    }
}

@Composable
fun GenericTab(
    state: GameState, 
    viewModel: GameViewModel, 
    type: String,
    onShowStat: () -> Unit = {},
    onShowAgama: () -> Unit = {},
    onShowGudang: () -> Unit = {},
    onShowGerbang: () -> Unit = {},
    onShowHospital: () -> Unit = {},
    onShowTavern: () -> Unit = {},
    onShowPasukanPurba: () -> Unit = {}
) {
    val upgradingBuildings by viewModel.upgradingBuildings.collectAsState()
    val nodes = allNodes.filter { 
        if (it.type != type) return@filter false
        
        // Handle tutorial phases
        if (state.gamePhase == "prolog_api") {
            return@filter it.id == "percikan_api"
        }
        if (state.gamePhase == "animasi_api") {
            return@filter it.id == "panggil_manusia"
        }
        
        // Hide tutorial nodes in main game
        if (it.id == "percikan_api" || it.id == "panggil_manusia") {
            return@filter false
        }
        
        // Pembukaan Bangunan berdasarkan level Balai Kota (Town Hall) persis seperti bagan
        if (it.type == "bangunan") {
            val thLevel = state.getBuildingLevel("town_hall")
            if (it.id == "town_hall") return@filter true
            if (thLevel <= 0) return@filter false
            
            val reqThLvl = when (it.id) {
                "gudang", "penebang_kayu", "pemecah_batu", "pertanian", "barak_militer", "hospital" -> 1
                "perkebunan", "peternakan", "rumah_warga", "market", "temple", "tavern", "taman_kota", "peleburan_besi", "tambang_emas" -> 2
                "tembok", "pelabuhan", "dungeon" -> 3
                "pengeboran_minyak", "pembangkit_listrik" -> 4
                "tambang_titanium", "reaktor_uranium" -> 5
                else -> it.tier
            }
            return@filter thLevel >= reqThLvl
        }
        true
    }
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(nodes) { node ->
            val isUnlocked = state.isUnlocked(node.id)
            val currentLevel = state.getBuildingLevel(node.id)
            
            val isBangunan = node.type == "bangunan"
            
            val displayTitle = if (isUnlocked && isBangunan) {
                "${node.name} (Lv. $currentLevel)"
            } else if ((node.id == "percikan_api" && state.gamePhase == "prolog_api") || 
                       (node.id == "panggil_manusia" && state.gamePhase == "animasi_api")) {
                "👉 ${node.name} 👈"
            } else {
                node.name
            }
            
            var displayDesc = node.desc
            if (isUnlocked && isBangunan) {
                when (node.id) {
                    "penebang_kayu" -> displayDesc = "Produksi Kayu (+${currentLevel * 1}/s)"
                    "pemecah_batu" -> displayDesc = "Produksi Batu (+${currentLevel * 1}/s)"
                    "peleburan_besi" -> displayDesc = "Produksi Besi (+${currentLevel * 2}/s)"
                    "tambang_emas" -> displayDesc = "Produksi Emas (+${currentLevel * 2}/s)"
                    "pengeboran_minyak" -> displayDesc = "Produksi Oil (+${currentLevel * 5}/s)"
                    "pembangkit_listrik" -> displayDesc = "Produksi Listrik (+${currentLevel * 10}/s)"
                    "tambang_titanium" -> displayDesc = "Produksi Titanium (+${currentLevel * 8}/s)"
                    "reaktor_uranium" -> displayDesc = "Produksi Uranium (+${currentLevel * 5}/s)"
                    "peternakan" -> displayDesc = "Produksi Daging (+${currentLevel * 1}/s)"
                    "perkebunan" -> displayDesc = "Produksi Berry (+${currentLevel * 2}/s)"
                    "pertanian", "pertanian_massal" -> displayDesc = "Produksi Gandum (+${currentLevel * 2}/s)"
                    "pelabuhan" -> displayDesc = "Mencari Ikan (+${currentLevel * 1}/s)"
                    "gudang" -> displayDesc = "Kapasitas maksimal item +${100 + currentLevel * 500}"
                    "barak_militer" -> displayDesc = "⚔️ Jumlah Pasukan: ${state.currentPasukan} / ${state.maxPasukan}"
                    "rumah_warga" -> displayDesc = "Populasi maksimal +${currentLevel * 10}"
                }
            }
            
            val costLevel = if (isUnlocked && isBangunan) currentLevel else 0
            val costMultiplier = Math.pow(1.5, costLevel.toDouble())
            
            var relCostBangunan = 1.0
            if (state.selectedReligions.values.contains("Doktrin Militer Suci")) relCostBangunan += 0.15
            var natCostBangunan = 1.0
            if (state.selectedNationality == "Romawi") natCostBangunan -= 0.10
            
            val totalCostBangunanMod = if (node.type == "bangunan") relCostBangunan * natCostBangunan else 1.0

            val actualZenithCost = node.zenithCost * costMultiplier * totalCostBangunanMod
            val actualResCosts = node.resourceCosts.mapValues { 
                var cost = (it.value * costMultiplier * totalCostBangunanMod).toLong()
                if (state.isUnlocked("metalurgi_militer") && node.type == "senjata/armor" && (it.key == "besi" || it.key == "emas")) {
                    cost = (cost * 0.85).toLong()
                }
                if (node.tier >= 3 && (node.type == "bangunan" || node.type == "filsafat")) {
                    cost = (cost * 1.8).toLong()
                }
                cost
            }
            
            val costStr = formatCost(actualZenithCost, actualResCosts)
            val canAfford = canAffordNode(state, actualZenithCost, actualResCosts)
            
            var actionText: String? = null
            var onActionClick: (() -> Unit)? = null
            var isActionEnabled = true
            var secondaryActionText: String? = null
            var onSecondaryActionClick: (() -> Unit)? = null
            
            if (isUnlocked && isBangunan) {
                when (node.id) {
                    "town_hall" -> {
                        actionText = "📊 Buka Stat Peradaban"
                        onActionClick = onShowStat
                    }
                    "gudang" -> {
                        actionText = "📦 Cek Isi Gudang"
                        onActionClick = onShowGudang
                    }
                    "barak_militer" -> {
                        actionText = "⚔️ Latih Pasukan (${state.currentPasukan}/${state.maxPasukan})"
                        onActionClick = { viewModel.latihPasukan() }
                        
                        secondaryActionText = "👥 Info Pasukan Purba (Kapak & Tombak)"
                        onSecondaryActionClick = onShowPasukanPurba
                    }
                    "temple" -> {
                        actionText = "\uD83D\uDE4F Pilih Agama"
                        onActionClick = onShowAgama
                    }
                    "tavern" -> {
                        actionText = "🍻 Rekrut Pahlawan (Tavern)"
                        onActionClick = onShowTavern
                        isActionEnabled = true
                    }
                    "dungeon" -> {
                        actionText = "⚔️ Masuk Dungeon (Lv.${state.maxDungeonLevel})"
                        onActionClick = { viewModel.startDungeonBattle(state.maxDungeonLevel) }
                    }
                    "tembok" -> {
                        actionText = "🛡️ Atur Pertahanan Gerbang"
                        onActionClick = onShowGerbang
                    }
                    "hospital" -> {
                        actionText = "🏥 Rumah Sakit (${state.woundedPasukan} Terluka)"
                        onActionClick = onShowHospital
                    }
                }
            }
            
            if (isUnlocked && !isBangunan) {
                if (node.id == "kerja_paksa") {
                    actionText = if (state.kerjaPaksaActive) "🛑 Matikan Kerja Paksa" else "⚠️ Aktifkan Kerja Paksa"
                    onActionClick = { viewModel.toggleKerjaPaksa() }
                }
            }
            
            val townHallLvl = state.getBuildingLevel("town_hall")
            var lockMsg = if (isUnlocked && isBangunan && node.id != "town_hall" && currentLevel >= townHallLvl) {
                "🔒 Town Hall Level ${currentLevel + 1} Diperlukan!"
            } else {
                null
            }
            if (node.id == "town_hall" && !isUnlocked && !state.isUnlocked("penyelesaian_masalah")) {
                lockMsg = "🔒 Butuh riset Penyelesaian Masalah"
            }
            
            val isHighlighted = (node.id == "percikan_api" && state.gamePhase == "prolog_api") || 
                                (node.id == "panggil_manusia" && state.gamePhase == "animasi_api")
            val isUpgrading = upgradingBuildings.containsKey(node.id)
            val upgradeEndTime = upgradingBuildings[node.id] ?: 0L

            val nodeIcon = when (node.id) {
                // Filsafat & Kebijakan
                "barter_purba" -> R.drawable.ic_fil_perdagangan
                "hukum_kodeks" -> R.drawable.ic_fil_hukum
                "monarki" -> R.drawable.ic_fil_monarki
                "feodalisme" -> R.drawable.ic_fil_feodalisme
                "meritokrasi" -> R.drawable.ic_fil_meritokrasi
                "teokrasi" -> R.drawable.ic_fil_teokrasi
                "kapitalisme" -> R.drawable.ic_fil_kapitalisme
                "sosialisme" -> R.drawable.ic_fil_sosialisme
                "otoritarianisme" -> R.drawable.ic_fil_otoritarianisme
                "transhumanisme" -> R.drawable.ic_fil_transhumanisme
                
                // Senjata & Armor
                "tongkat_kayu" -> R.drawable.ic_wp_tombak
                "kulit_hewan" -> R.drawable.ic_wp_armor_kulit
                "pedang_besi" -> R.drawable.ic_wp_pedang
                "armor_besi" -> R.drawable.ic_wp_baju_besi
                "musket" -> R.drawable.ic_wp_musket
                "armor_kevlar" -> R.drawable.ic_wp_railgun
                "assault_rifle" -> R.drawable.ic_wp_assault_rifle
                "exo_suit" -> R.drawable.ic_wp_exosuit
                "energy_katana" -> R.drawable.ic_wp_nano_kirana
                "nano_armor" -> R.drawable.ic_wp_plasma_cannon
                "planet_breaker" -> R.drawable.ic_wp_laser_blade
                "cyber_body" -> R.drawable.ic_wp_cyber_body
                
                else -> null
            }
            
            NodeCard(
                title = displayTitle,
                desc = displayDesc,
                costStr = costStr,
                isUnlocked = isUnlocked,
                isUpgradable = isBangunan,
                canAfford = if (lockMsg != null) false else canAfford,
                onClick = { viewModel.buyNode(node.id) },
                iconResId = nodeIcon,
                actionText = actionText,
                onActionClick = onActionClick,
                isActionEnabled = isActionEnabled,
                secondaryActionText = secondaryActionText,
                onSecondaryActionClick = onSecondaryActionClick,
                isHighlighted = isHighlighted,
                lockMessage = lockMsg
            )
        }
    }
}

@Composable
fun NodeCard(
    title: String,
    desc: String,
    costStr: String,
    isUnlocked: Boolean,
    isUpgradable: Boolean,
    canAfford: Boolean,
    onClick: () -> Unit,
    iconResId: Int? = null,
    isUpgrading: Boolean = false,
    upgradeEndTime: Long = 0L,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    isActionEnabled: Boolean = true,
    secondaryActionText: String? = null,
    onSecondaryActionClick: (() -> Unit)? = null,
    isHighlighted: Boolean = false,
    lockMessage: String? = null
) {
    val infiniteTransition = rememberInfiniteTransition(label = "highlight_blink")
    val highlightAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "highlight_alpha"
    )
    val borderStroke = when {
        isHighlighted -> BorderStroke(3.dp, Color(0xFFFBBF24).copy(alpha = highlightAlpha))
        isUnlocked && isUpgradable -> BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
        isUnlocked && !isUpgradable -> BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f))
        else -> null
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isHighlighted)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f)
            else
                MaterialTheme.colorScheme.surfaceVariant
        ),
        border = borderStroke,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Baris atas: ikon + judul + deskripsi
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Top
            ) {
                if (iconResId != null) {
                    Image(
                        painter = painterResource(id = iconResId),
                        contentDescription = title,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF130E26))
                            .padding(2.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = desc,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Baris biaya (jika relevan)
            if (!isUnlocked || isUpgradable) {
                if (lockMessage != null) {
                    // Tampilkan pesan kunci sebagai badge rapi, bukan tombol
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF7F1D1D).copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("🔒", fontSize = 12.sp)
                        Text(
                            text = lockMessage.removePrefix("🔒 "),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFFCA5A5),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                } else {
                    // Tampilkan biaya sebagai teks, lalu tombol aksi proporsional
                    Text(
                        text = if (isUnlocked) "💰 Upgrade: $costStr" else "💰 Biaya: $costStr",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (canAfford) MaterialTheme.colorScheme.onSurfaceVariant else Color(0xFFEF4444),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    // Tombol aksi utama (unlock/upgrade)
                    Button(
                        onClick = onClick,
                        enabled = canAfford,
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 6.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isUnlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                            disabledContainerColor = Color(0xFF374151),
                            disabledContentColor = Color(0xFF9CA3AF)
                        )
                    ) {
                        Text(
                            text = if (isUnlocked) "⬆️ Upgrade" else "🔓 Riset",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                // Sudah terbuka dan tidak bisa di-upgrade lagi
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("✅", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Aktif",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Tombol aksi sekunder (action dari bangunan seperti "Lihat Stat", "Latih Pasukan", dll.)
            if (isUnlocked && actionText != null && onActionClick != null) {
                OutlinedButton(
                    onClick = onActionClick,
                    enabled = isActionEnabled,
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 6.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f))
                ) {
                    Text(
                        actionText,
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Tombol aksi tambahan (secondary)
            if (isUnlocked && secondaryActionText != null && onSecondaryActionClick != null) {
                TextButton(
                    onClick = onSecondaryActionClick,
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 2.dp)
                ) {
                    Text(
                        secondaryActionText,
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.tertiary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

fun canAffordNode(state: GameState, zenithCost: Double, resourceCosts: Map<String, Long>): Boolean {
    if (state.totalZenith < zenithCost) return false
    for ((res, amount) in resourceCosts) {
        val currentAmount = when(res) {
            "kayu" -> state.kayu
            "batu" -> state.batu
            "besi" -> state.besi
            "emas" -> state.emas
            "oil" -> state.oil
            "listrik" -> state.listrik
            "titanium" -> state.titanium
            "uranium" -> state.uranium
            else -> 0L
        }
        if (currentAmount < amount) return false
    }
    return true
}

@Composable
fun KamusMaxTab() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "📖 Kamus Progresi Maksimal Peradaban",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Batas akhir pencapaian peradaban Anda dalam game ini:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("⚔️ Maksimal Dungeon", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Level Maksimal Dungeon: Level 100", style = MaterialTheme.typography.bodyMedium)
                    Text("Bos Akhir: Zenith Primordial", style = MaterialTheme.typography.bodyMedium, color = Color.Red)
                }
            }
        }
        
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("🗡️ Senjata Tertinggi", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Tier 49 - Planet Breaker Prototype", style = MaterialTheme.typography.bodyMedium)
                    Text("Damage Maksimal: 400.000", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
        
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("🛡️ Armor Tertinggi", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Tier 25 - Singularity Armor", style = MaterialTheme.typography.bodyMedium)
                    Text("Stats: +120.000 Def, +800.000 HP", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
        
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("🧪 Material Akhir Era Cyberpunk", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Listrik, Titanium, Uranium, Cosmic Crystal, Dark Matter Shard", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

fun formatCost(zenithCost: Double, resourceCosts: Map<String, Long>): String {
    val parts = mutableListOf<String>()
    if (zenithCost > 0) parts.add("\uD83D\uDC8E ${zenithCost.toInt()}")
    for ((res, amount) in resourceCosts) {
        val icon = when(res) {
            "kayu" -> "\uD83E\uDE93"
            "batu" -> "\uD83E\uDEA8"
            "besi" -> "⚒️"
            "emas" -> "\uD83D\uDCB0"
            "oil" -> "\uD83D\uDEE2️"
            "listrik" -> "⚡"
            "titanium" -> "⚙️"
            "uranium" -> "☢️"
            else -> res
        }
        parts.add("$icon $amount")
    }
    return parts.joinToString(" | ")
}

fun getRarityColor(rarity: String): Color {
    return when (rarity.lowercase()) {
        "unrare" -> Color(0xFFFFFFFF)
        "rare" -> Color(0xFF60A5FA)
        "very rare" -> Color(0xFFEAB308)
        "legend" -> Color(0xFFF97316)
        "mythical" -> Color(0xFFEF4444)
        "cosmos" -> Color(0xFF1D4ED8)
        "dark" -> Color(0xFF000000)
        "white" -> Color(0xFFF8FAFC)
        else -> Color(0xFFFFFFFF)
    }
}

fun DrawScope.drawSmokeParticles(x: Float, y: Float, time: Float) {
    for (i in 0..2) {
        val offset = (time * 2f + i * 15f) % 40f
        val alpha = kotlin.math.max(0f, 1f - (offset / 40f))
        val driftX = kotlin.math.sin(time + i) * 5f
        drawCircle(
            color = Color.DarkGray.copy(alpha = alpha * 0.5f),
            radius = 3f + (offset * 0.2f),
            center = Offset(x + 10f + driftX, y - offset)
        )
    }
}

fun DrawScope.drawCyberTraffic(cx: Float, cy: Float, time: Float, size: Size) {
    // 3 lanes of traffic
    for (i in 0..2) {
        val y = cy - 20f + (i * 30f)
        val speed = if (i % 2 == 0) 300f else -250f
        val px = (time * speed) % (size.width + 100f) - 50f
        
        val color = if (i % 2 == 0) Color(0xFF22D3EE) else Color(0xFFD946EF)
        
        // Draw the vehicle body
        drawRect(color, topLeft = Offset(px, y), size = Size(6f, 3f))
        // Draw the trail
        drawRect(color.copy(alpha=0.3f), topLeft = Offset(px + (if (speed > 0) -8f else 6f), y), size = Size(8f, 3f))
    }
}

fun DrawScope.drawScaffolding(x: Float, y: Float, era: Int, time: Float) {
    val isCyber = era >= 5
    val scaffoldColor = if (isCyber) Color(0xFF22D3EE) else Color(0xFF92400E)
    val beamColor = if (isCyber) Color(0xFF38BDF8).copy(alpha=0.5f) else Color(0xFFB45309)
    
    // Draw base foundation
    drawRect(Color(0xFF334155), topLeft = Offset(x - 5f, y + 25f), size = Size(50f, 10f))
    
    // Draw vertical and horizontal beams
    for (i in 0..4) {
        val px = x + i * 10f
        drawRect(scaffoldColor, topLeft = Offset(px, y), size = Size(2f, 25f)) // Vertical
        val py = y + i * 6f
        drawRect(scaffoldColor, topLeft = Offset(x, py), size = Size(40f, 2f)) // Horizontal
    }
    
    // Draw diagonals
    drawLine(beamColor, start = Offset(x, y), end = Offset(x + 40f, y + 25f), strokeWidth = 1.5f)
    drawLine(beamColor, start = Offset(x + 40f, y), end = Offset(x, y + 25f), strokeWidth = 1.5f)
    
    // Draw blinking hammer/wrench particles
    val blink = ((time * 5f) % 2f) > 1f
    if (blink) {
        val hammerColor = if (isCyber) Color(0xFFD946EF) else Color(0xFFFDE047)
        drawRect(hammerColor, topLeft = Offset(x + 15f, y - 10f), size = Size(10f, 4f))
        drawRect(Color(0xFF94A3B8), topLeft = Offset(x + 19f, y - 6f), size = Size(2f, 8f))
    }
}

fun DrawScope.drawTownHall(x: Float, y: Float, level: Int = 1, era: Int = 1) {
    val shadowColor = Color(0x4D000000)
    drawOval(shadowColor, topLeft = Offset(x - 12f, y + 36f), size = Size(80f, 18f))

    when (era) {
        1 -> { // Prehistoric: Chieftain Longhouse with Skull Crest, Twin Red Bone Banners & Torches
            val bw = 58f
            // Stone base footing
            for (st in 0..5) {
                drawOval(Color(0xFF5F6368), topLeft = Offset(x - 4f + st * 11f, y + 28f), size = Size(10f, 6f))
                drawOval(Color(0xFF9AA0A6), topLeft = Offset(x - 3f + st * 11f, y + 29f), size = Size(8f, 4f))
            }
            // Timber log framing
            drawRect(Color(0xFF4A2810), topLeft = Offset(x - 2f, y + 6f), size = Size(bw + 4f, 24f))
            drawRect(Color(0xFF78471D), topLeft = Offset(x, y + 8f), size = Size(bw, 20f))
            // Thatched conical straw roof
            val roofPath = Path().apply {
                moveTo(x - 8f, y + 8f)
                lineTo(x + bw / 2f, y - 20f)
                lineTo(x + bw + 8f, y + 8f)
                close()
            }
            drawPath(roofPath, Color(0xFFC5942B))
            // Straw layer highlights
            val innerRoof = Path().apply {
                moveTo(x - 3f, y + 6f)
                lineTo(x + bw / 2f, y - 18f)
                lineTo(x + bw + 3f, y + 6f)
                close()
            }
            drawPath(innerRoof, Color(0xFFECC057))
            // Crossed apex poles
            drawLine(Color(0xFF78471D), Offset(x + bw / 2f - 6f, y - 24f), Offset(x + bw / 2f + 6f, y - 16f), 3f)
            drawLine(Color(0xFF78471D), Offset(x + bw / 2f + 6f, y - 24f), Offset(x + bw / 2f - 6f, y - 16f), 3f)
            // Big horned skull on roof
            val skullX = x + bw / 2f
            val skullY = y - 12f
            drawOval(Color(0xFFF3EFE0), topLeft = Offset(skullX - 6f, skullY - 4f), size = Size(12f, 9f))
            drawLine(Color(0xFFF3EFE0), Offset(skullX - 5f, skullY), Offset(skullX - 11f, skullY - 5f), 2.5f)
            drawLine(Color(0xFFF3EFE0), Offset(skullX + 5f, skullY), Offset(skullX + 11f, skullY - 5f), 2.5f)
            drawRect(Color.Black, topLeft = Offset(skullX - 3.5f, skullY - 1f), size = Size(2f, 2f))
            drawRect(Color.Black, topLeft = Offset(skullX + 1.5f, skullY - 1f), size = Size(2f, 2f))
            // Arched cave entrance
            drawRect(Color(0xFF1E1B18), topLeft = Offset(skullX - 7f, y + 16f), size = Size(14f, 14f))
            // Twin Red Banners with bone cross
            // Left Banner
            drawRect(Color(0xFF9C2525), topLeft = Offset(x - 6f, y + 10f), size = Size(8f, 16f))
            drawLine(Color(0xFFF3EFE0), Offset(x - 4f, y + 13f), Offset(x, y + 21f), 1.5f)
            drawLine(Color(0xFFF3EFE0), Offset(x, y + 13f), Offset(x - 4f, y + 21f), 1.5f)
            // Right Banner
            drawRect(Color(0xFF9C2525), topLeft = Offset(x + bw - 2f, y + 10f), size = Size(8f, 16f))
            drawLine(Color(0xFFF3EFE0), Offset(x + bw, y + 13f), Offset(x + bw + 4f, y + 21f), 1.5f)
            drawLine(Color(0xFFF3EFE0), Offset(x + bw + 4f, y + 13f), Offset(x + bw, y + 21f), 1.5f)
            // Torch braziers on posts
            drawRect(Color(0xFF78471D), topLeft = Offset(x - 12f, y + 8f), size = Size(3f, 18f))
            drawCircle(Color(0xFFFB8C00), radius = 4f, center = Offset(x - 10.5f, y + 7f))
            drawRect(Color(0xFF78471D), topLeft = Offset(x + bw + 9f, y + 8f), size = Size(3f, 18f))
            drawCircle(Color(0xFFFB8C00), radius = 4f, center = Offset(x + bw + 10.5f, y + 7f))
        }
        2 -> { // Classical: Roman Stone Colosseum Hall with marble white pillars
            drawRect(Color(0xFF0F172A), topLeft = Offset(x - 4f, y - 12f), size = Size(58f, 52f))
            drawRect(Color(0xFFE2E8F0), topLeft = Offset(x - 3f, y - 11f), size = Size(56f, 50f))
            // Pillars
            for (px in 2..48 step 12) {
                drawRect(Color(0xFF0F172A), topLeft = Offset(x + px, y - 2f), size = Size(6f, 42f))
                drawRect(Color(0xFFFFFFFF), topLeft = Offset(x + px + 1f, y - 1f), size = Size(4f, 40f))
            }
            // Golden pediment roof triangle
            val roofPath = Path().apply {
                moveTo(x - 6f, y - 12f)
                lineTo(x + 25f, y - 26f)
                lineTo(x + 56f, y - 12f)
                close()
            }
            drawPath(roofPath, Color(0xFFF59E0B))
        }
        3 -> { // Medieval: Castle Keep with battlements, brick towers, and royal flags
            drawRect(Color(0xFF0F172A), topLeft = Offset(x - 6f, y - 15f), size = Size(62f, 55f))
            drawRect(Color(0xFF475569), topLeft = Offset(x - 5f, y - 14f), size = Size(60f, 53f))
            // Battlements crenellations
            for (bx in -4..52 step 14) {
                drawRect(Color(0xFF0F172A), topLeft = Offset(x + bx, y - 20f), size = Size(10f, 10f))
                drawRect(Color(0xFF64748B), topLeft = Offset(x + bx + 1f, y - 19f), size = Size(8f, 9f))
            }
            // Royal Crest Banner
            drawRect(Color(0xFF3B82F6), topLeft = Offset(x + 22f, y - 5f), size = Size(16f, 25f))
            drawRect(Color(0xFFF59E0B), topLeft = Offset(x + 26f, y - 1f), size = Size(8f, 17f))
            // Iron Gate
            drawRect(Color(0xFF1E293B), topLeft = Offset(x + 18f, y + 20f), size = Size(24f, 20f))
        }
        4 -> { // Industrial: Smokey Red-Brick Factory with massive gear & exhaust
            drawRect(Color(0xFF0F172A), topLeft = Offset(x, y - 10f), size = Size(50f, 50f))
            drawRect(Color(0xFF7C2D12), topLeft = Offset(x + 1f, y - 9f), size = Size(48f, 48f)) // Terracotta red brick
            // Brick seam lines
            drawRect(Color(0xFF9A3412), topLeft = Offset(x + 4f, y + 10f), size = Size(42f, 2f))
            drawRect(Color(0xFF9A3412), topLeft = Offset(x + 4f, y + 26f), size = Size(42f, 2f))
            // Massive steel chimney
            drawRect(Color(0xFF0F172A), topLeft = Offset(x + 36f, y - 32f), size = Size(10f, 24f))
            drawRect(Color(0xFF4B5563), topLeft = Offset(x + 37f, y - 31f), size = Size(8f, 23f))
            // Revolving golden clockwork gear
            drawCircle(Color(0xFFB45309), radius = 12f, center = Offset(x + 18f, y + 12f))
            drawCircle(Color(0xFFF59E0B), radius = 8f, center = Offset(x + 18f, y + 12f))
        }
        5 -> { // Modern: Pristine white & blue-glass skyscrapers with solar panel array
            drawRect(Color(0xFF0F172A), topLeft = Offset(x, y - 25f), size = Size(50f, 65f))
            drawRect(Color(0xFFF8FAFC), topLeft = Offset(x + 1f, y - 24f), size = Size(48f, 63f)) // Pristine white concrete
            // Blue glass curtain panels
            for (row in 0..3) {
                for (col in 0..1) {
                    val wx = x + 6f + (col * 22f)
                    val wy = y - 15f + (row * 14f)
                    drawRect(Color(0xFF0F172A), topLeft = Offset(wx, wy), size = Size(16f, 10f))
                    drawRect(Color(0xFF0EA5E9), topLeft = Offset(wx + 1f, wy + 1f), size = Size(14f, 8f)) // Glowing cyan glass
                    drawRect(Color(0xFF38BDF8), topLeft = Offset(wx + 1f, wy + 1f), size = Size(3f, 8f)) // Shine
                }
            }
        }
        else -> { // Cyberpunk: Mega-Skyscraper Citadel
            // Taller base
            drawRect(Color(0xFF0F172A), topLeft = Offset(x, y - 80f), size = Size(52f, 120f))
            drawRect(Color(0xFF0B132B), topLeft = Offset(x + 1f, y - 79f), size = Size(50f, 118f)) // Sleek cyber navy
            
            // Glowing neon borders (Cyan and Magenta pink)
            drawRect(Color(0xFF00E5FF), topLeft = Offset(x + 1f, y - 79f), size = Size(50f, 3f))
            drawRect(Color(0xFFD500F9), topLeft = Offset(x + 1f, y + 36f), size = Size(50f, 2f))
            
            // Laser Antenna
            drawRect(Color(0xFF00E5FF), topLeft = Offset(x + 24f, y - 110f), size = Size(4f, 30f))
            drawCircle(Color(0xFFD500F9).copy(alpha = 0.8f), radius = 6f, center = Offset(x + 26f, y - 110f))
            
            // Holographic floating neon globe above citadel
            drawCircle(Color(0xFF00E5FF).copy(alpha = 0.6f), radius = 18f, center = Offset(x + 26f, y - 130f))
            drawCircle(Color(0xFFFFFFFF), radius = 6f, center = Offset(x + 26f, y - 130f))
            
            // Active digital matrix window grid, extending down
            for (row in 0..8) {
                val my = y - 70f + (row * 12f)
                drawRect(Color(0xFF00E5FF), topLeft = Offset(x + 10f, my), size = Size(8f, 6f))
                drawRect(Color(0xFFD500F9), topLeft = Offset(x + 34f, my), size = Size(8f, 6f))
            }
        }
    }
}

fun DrawScope.drawGudang(x: Float, y: Float, level: Int = 1, era: Int = 1, time: Float = 0f) {
    val shadowColor = Color(0x4D000000)
    drawOval(shadowColor, topLeft = Offset(x - 6f, y + 26f), size = Size(52f, 12f))

    when (era) {
        1 -> { // Prehistoric: Meat and grain pit covered in dry animal skins
            drawOval(Color(0xFF0F172A), topLeft = Offset(x, y + 5f), size = Size(42f, 22f))
            drawOval(Color(0xFF5C4033), topLeft = Offset(x + 1f, y + 6f), size = Size(40f, 20f)) // Animal leather
            drawRect(Color(0xFFB45309), topLeft = Offset(x + 14f, y - 4f), size = Size(14f, 14f)) // Bound logs on top
        }
        2 -> { // Classical: Stone granary storage silo with clay tile roof
            drawRect(Color(0xFF0F172A), topLeft = Offset(x + 2f, y - 8f), size = Size(36f, 38f))
            drawRect(Color(0xFFECEFF1), topLeft = Offset(x + 3f, y - 7f), size = Size(34f, 36f)) // Classic stone white
            // Sandstone trim lines
            drawRect(Color(0xFFD2B48C), topLeft = Offset(x + 3f, y + 4f), size = Size(34f, 4f))
            drawRect(Color(0xFFD2B48C), topLeft = Offset(x + 3f, y + 18f), size = Size(34f, 4f))
            // Red clay dome roof
            drawCircle(Color(0xFFE2E8F0), radius = 18f, center = Offset(x + 20f, y - 8f))
            drawCircle(Color(0xFFCD5C5C), radius = 16f, center = Offset(x + 20f, y - 8f))
        }
        3 -> { // Medieval: Castle granary fortress storage
            drawRect(Color(0xFF0F172A), topLeft = Offset(x - 2f, y - 10f), size = Size(44f, 40f))
            drawRect(Color(0xFF475569), topLeft = Offset(x - 1f, y - 9f), size = Size(42f, 38f))
            // Thick iron-banded heavy gate door
            drawRect(Color(0xFF0F172A), topLeft = Offset(x + 10f, y + 8f), size = Size(22f, 22f))
            drawRect(Color(0xFF78350F), topLeft = Offset(x + 11f, y + 9f), size = Size(20f, 20f))
            drawRect(Color(0xFF94A3B8), topLeft = Offset(x + 11f, y + 14f), size = Size(20f, 2f)) // Iron lock bands
            drawRect(Color(0xFF94A3B8), topLeft = Offset(x + 11f, y + 21f), size = Size(20f, 2f))
        }
        4 -> { // Industrial: Brick warehouse with huge steam boilers and pipes
            drawRect(Color(0xFF0F172A), topLeft = Offset(x, y - 8f), size = Size(40f, 38f))
            drawRect(Color(0xFF3E2723), topLeft = Offset(x + 1f, y - 7f), size = Size(38f, 36f))
            // Brass copper pressure pipes winding
            drawRect(Color(0xFFCD7F32), topLeft = Offset(x + 4f, y - 16f), size = Size(4f, 10f))
            drawCircle(Color(0xFF78909C), radius = 4f, center = Offset(x + 6f, y - 18f)) // Pressure dial
            // Heavy cargo shutter door
            drawRect(Color(0xFF0F172A), topLeft = Offset(x + 10f, y + 10f), size = Size(20f, 20f))
            drawRect(Color(0xFF546E7A), topLeft = Offset(x + 11f, y + 11f), size = Size(18f, 19f))
        }
        5 -> { // Modern: Pristine logistics hub hangar with delivery trucks
            drawRect(Color(0xFF0F172A), topLeft = Offset(x, y - 8f), size = Size(40f, 38f))
            drawRect(Color(0xFFCFD8DC), topLeft = Offset(x + 1f, y - 7f), size = Size(38f, 36f)) // Metallic silver siding
            // Neon sign "HUB"
            drawRect(Color(0xFF00E5FF), topLeft = Offset(x + 10f, y - 4f), size = Size(20f, 6f))
            // Shutter bay doors
            drawRect(Color(0xFF37474F), topLeft = Offset(x + 6f, y + 8f), size = Size(28f, 22f))
        }
        else -> { // Cyberpunk: Inverted Pyramid Cyber Mall
            // Inverted pyramid shape
            val path = Path().apply {
                moveTo(x - 10f, y - 60f)
                lineTo(x + 50f, y - 60f)
                lineTo(x + 35f, y + 20f)
                lineTo(x + 5f, y + 20f)
                close()
            }
            drawPath(path, Color(0xFF0F172A))
            
            val innerPath = Path().apply {
                moveTo(x - 8f, y - 58f)
                lineTo(x + 48f, y - 58f)
                lineTo(x + 33f, y + 18f)
                lineTo(x + 7f, y + 18f)
                close()
            }
            drawPath(innerPath, Color(0xFF1E1E2F))
            
            // Running text billboard
            drawRect(Color(0xFFD500F9), topLeft = Offset(x - 12f, y - 40f), size = Size(64f, 16f))
            drawRect(Color(0xFF000000), topLeft = Offset(x - 10f, y - 38f), size = Size(60f, 12f))
            
            // Neon text effect (moving based on time)
            val offset = (time * 20f) % 60f
            drawRect(Color(0xFF00E5FF), topLeft = Offset(x - 10f + offset, y - 35f), size = Size(8f, 6f))
            drawRect(Color(0xFF00E5FF), topLeft = Offset(x - 10f + ((offset + 15f) % 60f), y - 35f), size = Size(12f, 6f))
            
            // Bottom glowing core
            drawCircle(Color(0xFF00E5FF), radius = 8f, center = Offset(x + 20f, y + 20f))
        }
    }
}

fun DrawScope.drawTemple(x: Float, y: Float, level: Int = 1, era: Int = 1, time: Float = 0f) {
    val shadowColor = Color(0x4D000000)
    drawOval(shadowColor, topLeft = Offset(x - 6f, y + 41f), size = Size(57f, 14f))

    when (era) {
        1 -> { // Prehistoric: Monolithic Stonehenge circle, floating mystical orb sparks
            drawOval(Color(0xFF0F172A), topLeft = Offset(x - 4f, y + 6f), size = Size(54f, 28f))
            drawOval(Color(0xFF374151), topLeft = Offset(x - 2f, y + 8f), size = Size(50f, 24f))
            // Stonehenge pillars
            for (px in 0..40 step 12) {
                drawRect(Color(0xFF0F172A), topLeft = Offset(x + px, y - 14f), size = Size(8f, 28f))
                drawRect(Color(0xFF6B7280), topLeft = Offset(x + px + 1f, y - 13f), size = Size(6f, 26f))
            }
            // Mystical glowing yellow sparks floating in the center circle
            drawCircle(Color(0xFFFDE047), radius = 4f, center = Offset(x + 22f, y - 2f))
        }
        2 -> { // Classical: Majestic marble Greek Parthenon with tall gold statue inside
            drawRect(Color(0xFF0F172A), topLeft = Offset(x - 4f, y - 15f), size = Size(54f, 60f))
            drawRect(Color(0xFFF1F5F9), topLeft = Offset(x - 3f, y - 14f), size = Size(52f, 58f)) // White marble
            // Pediment triangular golden roof
            val roofPath = Path().apply {
                moveTo(x - 6f, y - 15f)
                lineTo(x + 23f, y - 30f)
                lineTo(x + 52f, y - 15f)
                close()
            }
            drawPath(roofPath, Color(0xFFF59E0B)) // Gold roof
            // Pillars
            for (px in 2..44 step 10) {
                drawRect(Color(0xFF0F172A), topLeft = Offset(x + px, y - 5f), size = Size(5f, 48f))
                drawRect(Color(0xFFFFFFFF), topLeft = Offset(x + px + 1f, y - 4f), size = Size(3f, 46f))
            }
        }
        3 -> { // Medieval: Grand Gothic Cathedral with vibrant stained-glass windows
            drawRect(Color(0xFF0F172A), topLeft = Offset(x, y - 18f), size = Size(46f, 62f))
            drawRect(Color(0xFF334155), topLeft = Offset(x + 1f, y - 17f), size = Size(44f, 60f))
            // Tall spire steeple roof
            val spirePath = Path().apply {
                moveTo(x, y - 18f)
                lineTo(x + 23f, y - 48f)
                lineTo(x + 46f, y - 18f)
                close()
            }
            drawPath(spirePath, Color(0xFF1E293B))
            // Cross on top
            drawRect(Color(0xFF94A3B8), topLeft = Offset(x + 22f, y - 58f), size = Size(2f, 12f))
            drawRect(Color(0xFF94A3B8), topLeft = Offset(x + 19f, y - 53f), size = Size(8f, 2f))
            // Stained glass rose window
            drawCircle(Color(0xFF0F172A), radius = 10f, center = Offset(x + 23f, y - 2f))
            drawCircle(Color(0xFFE11D48), radius = 8f, center = Offset(x + 23f, y - 2f)) // Red stained-glass
            drawCircle(Color(0xFF2563EB), radius = 5f, center = Offset(x + 23f, y - 2f)) // Blue center core
        }
        4 -> { // Industrial: Clocktower Cathedral with gold clock gears and soot brickwork
            drawRect(Color(0xFF0F172A), topLeft = Offset(x, y - 22f), size = Size(46f, 66f))
            drawRect(Color(0xFF451A03), topLeft = Offset(x + 1f, y - 21f), size = Size(44f, 64f)) // Terracotta masonry
            // Massive mechanical brass clock
            drawCircle(Color(0xFF0F172A), radius = 11f, center = Offset(x + 23f, y - 8f))
            drawCircle(Color(0xFFF59E0B), radius = 9f, center = Offset(x + 23f, y - 8f)) // Gold clockface
            drawRect(Color(0xFF0F172A), topLeft = Offset(x + 22f, y - 8f), size = Size(2f, 6f)) // Hands
        }
        5 -> { // Modern: Pristine eco glass dome sanctuary with hanging white light
            drawCircle(Color(0xFF0F172A), radius = 24f, center = Offset(x + 23f, y + 10f))
            drawCircle(Color(0xFF0EA5E9).copy(alpha = 0.5f), radius = 22f, center = Offset(x + 23f, y + 10f)) // Glass dome
            // Modern support arches
            drawRect(Color(0xFFF8FAFC), topLeft = Offset(x + 10f, y - 10f), size = Size(4f, 40f))
            drawRect(Color(0xFFF8FAFC), topLeft = Offset(x + 32f, y - 10f), size = Size(4f, 40f))
            drawCircle(Color(0xFFFFFFFF), radius = 5f, center = Offset(x + 23f, y + 6f)) // Warm hanging light orb
        }
        else -> { // Cyberpunk: Synth-wave Synthetic Cathedral with high-powered neon plasma pylon beams
            drawRect(Color(0xFF0F172A), topLeft = Offset(x + 4f, y - 25f), size = Size(38f, 68f))
            drawRect(Color(0xFF0B132B), topLeft = Offset(x + 5f, y - 24f), size = Size(36f, 66f)) // Deep neon core
            // Holographic floating light cross/monolith
            val neonHoloColor = if ((time * 12f).toInt() % 2 == 0) Color(0xFFD500F9) else Color(0xFF00E5FF)
            drawRect(neonHoloColor.copy(alpha = 0.7f), topLeft = Offset(x + 20f, y - 50f), size = Size(6f, 22f))
            drawRect(neonHoloColor.copy(alpha = 0.7f), topLeft = Offset(x + 13f, y - 42f), size = Size(20f, 6f))
            // Side pylons beaming upward
            drawRect(Color(0xFF00E5FF), topLeft = Offset(x - 4f, y - 10f), size = Size(4f, 50f))
            drawRect(Color(0xFFD500F9), topLeft = Offset(x + 46f, y - 10f), size = Size(4f, 50f))
        }
    }
}

fun DrawScope.drawPort(x: Float, y: Float, era: Int = 1) {
    val shadowColor = Color(0x4D000000)
    drawOval(shadowColor, topLeft = Offset(x - 6f, y + 12f), size = Size(52f, 10f))

    when (era) {
        1 -> { // Prehistoric: Mud & Driftwood Pier with birch bark canoe
            drawRect(Color(0xFF0F172A), topLeft = Offset(x, y), size = Size(40f, 12f))
            drawRect(Color(0xFF8B4513), topLeft = Offset(x + 1f, y + 1f), size = Size(38f, 10f))
            // Birch canoe floated
            drawOval(Color(0xFF0F172A), topLeft = Offset(x + 15f, y + 10f), size = Size(22f, 8f))
            drawOval(Color(0xFFF5E0C3), topLeft = Offset(x + 16f, y + 11f), size = Size(20f, 6f))
        }
        2 -> { // Classical: Stone paved harbor with roman cargo trireme sailboat
            drawRect(Color(0xFF0F172A), topLeft = Offset(x, y - 2f), size = Size(42f, 16f))
            drawRect(Color(0xFFE2E8F0), topLeft = Offset(x + 1f, y - 1f), size = Size(40f, 14f)) // White pavers
            // Sailing cargo ship details
            drawRect(Color(0xFF0F172A), topLeft = Offset(x + 18f, y + 14f), size = Size(24f, 10f))
            drawRect(Color(0xFF8B5A2B), topLeft = Offset(x + 19f, y + 15f), size = Size(22f, 8f)) // Wooden hull
            drawRect(Color(0xFFFFFFFF), topLeft = Offset(x + 28f, y + 6f), size = Size(8f, 9f)) // Roman sail
        }
        3 -> { // Medieval: Oak trading pier with massive double-mast wooden galleon
            drawRect(Color(0xFF0F172A), topLeft = Offset(x - 2f, y - 4f), size = Size(46f, 20f))
            drawRect(Color(0xFF5C4033), topLeft = Offset(x - 1f, y - 3f), size = Size(44f, 18f)) // Oak logs
            // Double-mast galleon
            drawRect(Color(0xFF0F172A), topLeft = Offset(x + 22f, y + 15f), size = Size(28f, 12f))
            drawRect(Color(0xFF3E2723), topLeft = Offset(x + 23f, y + 16f), size = Size(26f, 10f)) // Dark wood hull
            drawRect(Color(0xFFF1F5F9), topLeft = Offset(x + 28f, y + 4f), size = Size(6f, 12f)) // Sails
            drawRect(Color(0xFFF1F5F9), topLeft = Offset(x + 38f, y + 2f), size = Size(6f, 14f))
        }
        4 -> { // Industrial: Metal sheet pier with automated heavy cargo crane
            drawRect(Color(0xFF0F172A), topLeft = Offset(x, y - 4f), size = Size(40f, 18f))
            drawRect(Color(0xFF475569), topLeft = Offset(x + 1f, y - 3f), size = Size(38f, 16f)) // Iron plates
            // Red crane structural arm
            drawRect(Color(0xFF0F172A), topLeft = Offset(x + 4f, y - 14f), size = Size(6f, 18f))
            drawRect(Color(0xFFEF4444), topLeft = Offset(x + 5f, y - 13f), size = Size(4f, 16f)) // Tower
            drawRect(Color(0xFFEF4444), topLeft = Offset(x + 5f, y - 13f), size = Size(18f, 4f)) // Arm jib
        }
        5 -> { // Modern: Concrete freight harbor with yellow container crane & cargo carrier
            drawRect(Color(0xFF0F172A), topLeft = Offset(x, y - 5f), size = Size(42f, 20f))
            drawRect(Color(0xFF94A3B8), topLeft = Offset(x + 1f, y - 4f), size = Size(40f, 18f)) // Concrete harbor
            // Yellow crane loader
            drawRect(Color(0xFFFFEB3B), topLeft = Offset(x + 22f, y - 16f), size = Size(12f, 20f))
            // Freight container box loaded
            drawRect(Color(0xFF0F172A), topLeft = Offset(x + 4f, y + 14f), size = Size(16f, 10f))
            drawRect(Color(0xFF3B82F6), topLeft = Offset(x + 5f, y + 15f), size = Size(14f, 8f)) // Blue container
        }
        else -> { // Cyberpunk: Floating hyper-tech docks with gravity pads and landing spaceship shuttles
            drawRect(Color(0xFF0F172A), topLeft = Offset(x, y - 6f), size = Size(44f, 22f))
            drawRect(Color(0xFF0B132B), topLeft = Offset(x + 1f, y - 5f), size = Size(42f, 20f))
            // Neon cyan levitation light ring pads
            drawOval(Color(0xFF00E5FF), topLeft = Offset(x + 4f, y + 15f), size = Size(36f, 6f))
            // Levitating glowing landing ship shuttle hover model
            drawRect(Color(0xFFD500F9), topLeft = Offset(x + 14f, y + 6f), size = Size(16f, 6f)) // Synthwave pink ship
            drawRect(Color(0xFF00E5FF), topLeft = Offset(x + 18f, y + 8f), size = Size(8f, 2f))  // Thrust light glow
        }
    }
}

fun DrawScope.drawFarm(x: Float, y: Float, isBerry: Boolean = false, level: Int = 1, era: Int = 1, time: Float = 0f) {
    val shadowColor = Color(0x4D000000)
    drawOval(shadowColor, topLeft = Offset(x - 6f, y + 18f), size = Size(48f, 12f))

    when (era) {
        1 -> { // Prehistoric: Hand-tilled mud patches with raw berries/wild wheat clumps
            drawRect(Color(0xFF0F172A), topLeft = Offset(x - 1f, y + 4f), size = Size(37f, 17f))
            drawRect(Color(0xFF78350F), topLeft = Offset(x, y + 5f), size = Size(35f, 15f)) // Mud
            for (i in 0..3) {
                val px = x + 4f + (i * 8f)
                drawCircle(Color(0xFF15803D), radius = 4f, center = Offset(px, y + 10f)) // Green leaf clump
                if (isBerry) {
                    drawCircle(Color(0xFFEF4444), radius = 1.5f, center = Offset(px, y + 9f)) // Wild red berry dots
                } else {
                    drawRect(Color(0xFFF59E0B), topLeft = Offset(px - 1f, y + 6f), size = Size(2f, 5f)) // Primitive wheat
                }
            }
        }
        2 -> { // Classical: Symmetrical paved grid irrigation channels with neat roman rows
            drawRect(Color(0xFF0F172A), topLeft = Offset(x - 1f, y + 2f), size = Size(37f, 19f))
            drawRect(Color(0xFFD2B48C), topLeft = Offset(x, y + 3f), size = Size(35f, 17f)) // Sandstone soil base
            // Blue irrigation canals
            drawRect(Color(0xFF38BDF8), topLeft = Offset(x + 16f, y + 3f), size = Size(3f, 17f))
            for (i in 0..2) {
                val px = x + 4f + (i * 12f)
                drawRect(Color(0xFF16A34A), topLeft = Offset(px, y + 6f), size = Size(4f, 11f))
            }
        }
        3 -> { // Medieval: Feudal strip-farming layout with protective scarecrow dummy
            drawRect(Color(0xFF0F172A), topLeft = Offset(x - 1f, y + 4f), size = Size(37f, 17f))
            drawRect(Color(0xFF451A03), topLeft = Offset(x, y + 5f), size = Size(35f, 15f))
            // Straw-stuffed scarecrow dummy details
            drawRect(Color(0xFF78350F), topLeft = Offset(x + 16f, y - 2f), size = Size(2f, 15f)) // Cross shaft pole
            drawRect(Color(0xFFD2B48C), topLeft = Offset(x + 13f, y + 1f), size = Size(8f, 4f)) // Arms
            drawRect(Color(0xFFF59E0B), topLeft = Offset(x + 14f, y - 4f), size = Size(6f, 5f)) // Straw pumpkin head
        }
        4 -> { // Industrial: Coal-steam heated soil beds with automated metal valves
            drawRect(Color(0xFF0F172A), topLeft = Offset(x - 1f, y + 3f), size = Size(37f, 18f))
            drawRect(Color(0xFF1E293B), topLeft = Offset(x, y + 4f), size = Size(35f, 16f)) // Coal soot soil
            // Brass valves
            drawRect(Color(0xFFCD7F32), topLeft = Offset(x + 16f, y - 4f), size = Size(3f, 8f))
            drawCircle(Color(0xFFB45309), radius = 2.5f, center = Offset(x + 17.5f, y - 5f))
        }
        5 -> { // Modern: Transparent glass greenhouse rows under bright neon UV growlights
            drawRect(Color(0xFF0F172A), topLeft = Offset(x, y), size = Size(36f, 22f))
            drawRect(Color(0xFFE2E8F0), topLeft = Offset(x + 1f, y + 1f), size = Size(34f, 20f)) // White concrete frame base
            // Blue/Green neon UV glowlights ceiling
            drawRect(Color(0xFFD500F9).copy(alpha = 0.5f), topLeft = Offset(x + 4f, y + 2f), size = Size(28f, 3f)) // Violet grow-laser
            for (i in 0..2) {
                drawRect(Color(0xFF4ADE80), topLeft = Offset(x + 4f + (i * 10f), y + 8f), size = Size(6f, 11f))
            }
        }
        else -> { // Cyberpunk: Levitating glowing Hydroponic nutrient tubes with cybernetic synthetic crop pods
            drawRect(Color(0xFF0F172A), topLeft = Offset(x, y + 2f), size = Size(36f, 18f))
            drawRect(Color(0xFF0B132B), topLeft = Offset(x + 1f, y + 3f), size = Size(34f, 16f))
            // Cyan glowing water tubes containing bioluminescent pods
            drawRect(Color(0xFF00E5FF), topLeft = Offset(x + 4f, y + 6f), size = Size(28f, 4f))
            drawRect(Color(0xFFD500F9), topLeft = Offset(x + 4f, y + 12f), size = Size(28f, 4f))
            // Glow sparkles
            drawCircle(Color(0xFFFFFFFF), radius = 1.5f, center = Offset(x + 10f, y + 8f))
            drawCircle(Color(0xFFFFFFFF), radius = 1.5f, center = Offset(x + 24f, y + 14f))
        }
    }
}

fun DrawScope.drawBarak(x: Float, y: Float, level: Int = 1, era: Int = 1) {
    val shadowColor = Color(0x4D000000)
    drawOval(shadowColor, topLeft = Offset(x - 6f, y + 23f), size = Size(57f, 14f))

    when (era) {
        1 -> { // Prehistoric: Chieftain mud wrestling training pitch with raw wood target dummies
            drawOval(Color(0xFF0F172A), topLeft = Offset(x, y + 4f), size = Size(46f, 22f))
            drawOval(Color(0xFF78350F), topLeft = Offset(x + 1f, y + 5f), size = Size(44f, 20f)) // Wrestling mud
            // Archery Target Dummy
            drawRect(Color(0xFF0F172A), topLeft = Offset(x + 30f, y - 6f), size = Size(6f, 16f))
            drawRect(Color(0xFFD2B48C), topLeft = Offset(x + 31f, y - 5f), size = Size(4f, 14f)) // Wooden post
            drawCircle(Color(0xFFEF4444), radius = 4f, center = Offset(x + 33f, y - 3f)) // Bullseye target red
        }
        2 -> { // Classical: Legatus stone fort with red banners and spear weapon racks
            drawRect(Color(0xFF0F172A), topLeft = Offset(x - 2f, y - 8f), size = Size(49f, 35f))
            drawRect(Color(0xFFECEFF1), topLeft = Offset(x - 1f, y - 7f), size = Size(47f, 33f))
            // Weapon racks with spears
            drawRect(Color(0xFF0F172A), topLeft = Offset(x + 4f, y + 8f), size = Size(14f, 16f))
            drawRect(Color(0xFFB45309), topLeft = Offset(x + 5f, y + 9f), size = Size(12f, 14f))
            drawRect(Color(0xFF94A3B8), topLeft = Offset(x + 8f, y + 10f), size = Size(2f, 12f)) // Bronze spears
            drawRect(Color(0xFF94A3B8), topLeft = Offset(x + 12f, y + 10f), size = Size(2f, 12f))
        }
        3 -> { // Medieval: Castle stone tower garrison with defensive high arrow slits
            drawRect(Color(0xFF0F172A), topLeft = Offset(x, y - 15f), size = Size(45f, 42f))
            drawRect(Color(0xFF475569), topLeft = Offset(x + 1f, y - 14f), size = Size(43f, 40f))
            // Iron security gatehouse
            drawRect(Color(0xFF0F172A), topLeft = Offset(x + 12f, y + 10f), size = Size(21f, 18f))
            drawRect(Color(0xFF1E293B), topLeft = Offset(x + 13f, y + 11f), size = Size(19f, 17f)) // Portcullis grid
            // Arrow slits
            drawRect(Color(0xFF0F172A), topLeft = Offset(x + 8f, y - 4f), size = Size(3f, 8f))
            drawRect(Color(0xFF0F172A), topLeft = Offset(x + 34f, y - 4f), size = Size(3f, 8f))
        }
        4 -> { // Industrial: Steam powered ammunition munitions depot with metal smokestacks
            drawRect(Color(0xFF0F172A), topLeft = Offset(x, y - 8f), size = Size(45f, 35f))
            drawRect(Color(0xFF37474F), topLeft = Offset(x + 1f, y - 7f), size = Size(43f, 33f)) // Heavy sheet iron
            // Ammo crates stacked
            drawRect(Color(0xFF0F172A), topLeft = Offset(x + 28f, y + 14f), size = Size(12f, 12f))
            drawRect(Color(0xFFB45309), topLeft = Offset(x + 29f, y + 15f), size = Size(10f, 10f)) // Green-painted chest
        }
        5 -> { // Modern: Reinforced concrete compound barracks with satellite telemetry dishes
            drawRect(Color(0xFF0F172A), topLeft = Offset(x, y - 10f), size = Size(45f, 37f))
            drawRect(Color(0xFFCFD8DC), topLeft = Offset(x + 1f, y - 9f), size = Size(43f, 35f)) // Tactical steel
            // Satellite communication disk
            drawRect(Color(0xFF37474F), topLeft = Offset(x + 30f, y - 20f), size = Size(4f, 12f)) // Mount
            drawCircle(Color(0xFFFFFFFF), radius = 6f, center = Offset(x + 32f, y - 20f)) // Dish circle
        }
        else -> { // Cyberpunk: Nanotech Cloning Pod Mecha Chambers with active neural glow pipes
            drawRect(Color(0xFF0F172A), topLeft = Offset(x, y - 12f), size = Size(46f, 39f))
            drawRect(Color(0xFF0B132B), topLeft = Offset(x + 1f, y - 11f), size = Size(44f, 37f)) // Advanced polymer
            // Active mecha cybernetic capsule pods glowing green
            drawRect(Color(0xFF0F172A), topLeft = Offset(x + 8f, y + 4f), size = Size(12f, 22f))
            drawRect(Color(0xFF00E5FF), topLeft = Offset(x + 9f, y + 5f), size = Size(10f, 20f)) // Plasma capsule
            // Holographic training target dummy
            drawCircle(Color(0xFFD500F9).copy(alpha = 0.6f), radius = 5f, center = Offset(x + 34f, y + 12f))
        }
    }
}

fun DrawScope.drawHouse(x: Float, y: Float, level: Int = 1, era: Int = 1, time: Float = 0f) {
    val shadowColor = Color(0x4D000000)
    drawOval(shadowColor, topLeft = Offset(x - 5f, y + 18f), size = Size(42f, 12f))

    when (era) {
        1 -> { // Prehistoric: Traditional wood & straw shelter with leather binding cords
            drawRect(Color(0xFF0F172A), topLeft = Offset(x, y + 2f), size = Size(30f, 18f))
            drawRect(Color(0xFF78350F), topLeft = Offset(x + 1f, y + 3f), size = Size(28f, 16f)) // Primitive clay mud
            // Straw thatched roofing
            val roofPath = Path().apply {
                moveTo(x - 4f, y + 2f)
                lineTo(x + 15f, y - 10f)
                lineTo(x + 34f, y + 2f)
                close()
            }
            drawPath(roofPath, Color(0xFFD2B48C)) // Light straw straw yellow
        }
        2 -> { // Classical: Elegant roman villa cottage with beautiful red clay tile roofing
            drawRect(Color(0xFF0F172A), topLeft = Offset(x, y - 2f), size = Size(32f, 22f))
            drawRect(Color(0xFFF1F5F9), topLeft = Offset(x + 1f, y - 1f), size = Size(30f, 20f)) // Sandstone white brick
            // Clay tile red roof
            val roofPath = Path().apply {
                moveTo(x - 5f, y - 2f)
                lineTo(x + 16f, y - 14f)
                lineTo(x + 37f, y - 2f)
                close()
            }
            drawPath(roofPath, Color(0xFFCD5C5C)) // Clay terracotta red
        }
        3 -> { // Medieval: Half-timbered Tudor stone cottage with cozy stone chimney
            drawRect(Color(0xFF0F172A), topLeft = Offset(x, y - 4f), size = Size(32f, 24f))
            drawRect(Color(0xFFE2E8F0), topLeft = Offset(x + 1f, y - 3f), size = Size(30f, 22f))
            // Timber wooden support beans crossing visually
            drawRect(Color(0xFF5C4033), topLeft = Offset(x + 4f, y + 2f), size = Size(4f, 18f))
            drawRect(Color(0xFF5C4033), topLeft = Offset(x + 22f, y + 2f), size = Size(4f, 18f))
            // Tudor thatch roof
            val roofPath = Path().apply {
                moveTo(x - 5f, y - 4f)
                lineTo(x + 16f, y - 15f)
                lineTo(x + 37f, y - 4f)
                close()
            }
            drawPath(roofPath, Color(0xFF8B4513))
        }
        4 -> { // Industrial: Smokey red-brick industrial rowhouse tenement
            drawRect(Color(0xFF0F172A), topLeft = Offset(x, y - 6f), size = Size(32f, 26f))
            drawRect(Color(0xFF9A3412), topLeft = Offset(x + 1f, y - 5f), size = Size(30f, 24f)) // Terracotta red brick
            // Cozy window with iron pane grid
            drawRect(Color(0xFF1E293B), topLeft = Offset(x + 5f, y + 3f), size = Size(8f, 8f))
            // Industrial chimneys
            drawRect(Color(0xFF475569), topLeft = Offset(x + 24f, y - 14f), size = Size(4f, 9f))
        }
        5 -> { // Modern: Minimalist sleek white concrete cube duplex with cyan window paning
            drawRect(Color(0xFF0F172A), topLeft = Offset(x, y - 8f), size = Size(32f, 28f))
            drawRect(Color(0xFFF8FAFC), topLeft = Offset(x + 1f, y - 7f), size = Size(30f, 26f)) // Clean white render
            // Horizontal modern wide glass windows
            drawRect(Color(0xFF0F172A), topLeft = Offset(x + 4f, y + 4f), size = Size(22f, 8f))
            drawRect(Color(0xFF00E5FF), topLeft = Offset(x + 5f, y + 5f), size = Size(20f, 6f)) // Light cyan glass reflecting
        }
        else -> { // Cyberpunk: Vertikal Kapsul Apartemen
            // Tinggi dan ramping
            drawRect(Color(0xFF0F172A), topLeft = Offset(x + 4f, y - 60f), size = Size(26f, 80f))
            drawRect(Color(0xFF1A1A24), topLeft = Offset(x + 5f, y - 59f), size = Size(24f, 78f))
            
            // Jendela kapsul kecil yang memancarkan neon
            val neonColors = listOf(Color(0xFF00E5FF), Color(0xFFD500F9), Color(0xFFFF007F))
            for (row in 0..7) {
                for (col in 0..1) {
                    val colorIndex = ((x.toInt() + row * 3 + col * 7) % 3)
                    val neonColor = neonColors[colorIndex]
                    val isLit = ((time * 2f).toInt() + row + col) % 3 != 0
                    
                    if (isLit) {
                        drawRect(neonColor, topLeft = Offset(x + 8f + col * 10f, y - 50f + row * 9f), size = Size(6f, 4f))
                    } else {
                        drawRect(Color(0xFF111111), topLeft = Offset(x + 8f + col * 10f, y - 50f + row * 9f), size = Size(6f, 4f))
                    }
                }
            }
            
            // Antena / Detail atap
            drawRect(Color(0xFF37474F), topLeft = Offset(x + 16f, y - 70f), size = Size(2f, 10f))
            drawCircle(Color(0xFFFF007F), radius = 2f, center = Offset(x + 17f, y - 70f))
        }
    }
}

fun DrawScope.drawTavern(x: Float, y: Float, level: Int = 1, era: Int = 1, time: Float = 0f) {
    val shadowColor = Color(0x4D000000)
    drawOval(shadowColor, topLeft = Offset(x - 6f, y + 26f), size = Size(52f, 14f))

    when (era) {
        1 -> { // Prehistoric: Chieftain mud drinking fire pit circle
            drawOval(Color(0xFF0F172A), topLeft = Offset(x - 2f, y + 4f), size = Size(44f, 22f))
            drawOval(Color(0xFF8B4513), topLeft = Offset(x - 1f, y + 5f), size = Size(42f, 20f))
            // Roasting spit
            drawRect(Color(0xFF78350F), topLeft = Offset(x + 10f, y - 4f), size = Size(20f, 2f)) // Spit shaft
            drawRect(Color(0xFFEF4444), topLeft = Offset(x + 15f, y - 8f), size = Size(10f, 10f)) // Roasted meat
        }
        2 -> { // Classical: Roman Wine Bar Thermopolium with terracotta jars
            drawRect(Color(0xFF0F172A), topLeft = Offset(x, y - 2f), size = Size(40f, 32f))
            drawRect(Color(0xFFF5E0C3), topLeft = Offset(x + 1f, y - 1f), size = Size(38f, 30f)) // Classical sand wall
            // Terracotta jars lined up
            drawCircle(Color(0xFFD97706), radius = 4f, center = Offset(x + 10f, y + 18f))
            drawCircle(Color(0xFFD97706), radius = 4f, center = Offset(x + 30f, y + 18f))
        }
        3 -> { // Medieval: Half-timbered Tudor tavern with wooden flag boards
            drawRect(Color(0xFF0F172A), topLeft = Offset(x - 2f, y - 8f), size = Size(44f, 38f))
            drawRect(Color(0xFF5C4033), topLeft = Offset(x - 1f, y - 7f), size = Size(42f, 36f))
            // Glowing golden beer mug sign
            drawRect(Color(0xFF0F172A), topLeft = Offset(x + 6f, y + 4f), size = Size(10f, 10f))
            drawRect(Color(0xFFFBBF24), topLeft = Offset(x + 7f, y + 5f), size = Size(8f, 8f)) // Mug
            drawRect(Color(0xFFFFFFFF), topLeft = Offset(x + 7f, y + 5f), size = Size(6f, 2f)) // Beer foam
        }
        4 -> { // Industrial: Red-brick Pub with soot chimneys & coppery draft gas piping
            drawRect(Color(0xFF0F172A), topLeft = Offset(x, y - 10f), size = Size(40f, 40f))
            drawRect(Color(0xFF451A03), topLeft = Offset(x + 1f, y - 9f), size = Size(38f, 38f)) // Dark red soot brick
            // Copper exhaust pipes
            drawRect(Color(0xFFCD7F32), topLeft = Offset(x + 32f, y - 16f), size = Size(4f, 14f))
        }
        5 -> { // Modern: Retro cocktail lounge with neon cursive signage
            drawRect(Color(0xFF0F172A), topLeft = Offset(x, y - 8f), size = Size(40f, 38f))
            drawRect(Color(0xFF1E293B), topLeft = Offset(x + 1f, y - 7f), size = Size(38f, 36f))
            // Neon sign "BAR"
            drawRect(Color(0xFFD500F9), topLeft = Offset(x + 10f, y - 2f), size = Size(20f, 6f))
        }
        else -> { // Cyberpunk: Synth-wave Cyber Club with flashing hologram neon dancers
            drawRect(Color(0xFF0F172A), topLeft = Offset(x, y - 12f), size = Size(42f, 42f))
            drawRect(Color(0xFF0B132B), topLeft = Offset(x + 1f, y - 11f), size = Size(40f, 40f))
            // Holographic dancing neon projection lines
            val neonColor = if ((time * 15f).toInt() % 2 == 0) Color(0xFFD500F9) else Color(0xFF00E5FF)
            drawCircle(neonColor.copy(alpha = 0.5f), radius = 6f, center = Offset(x + 21f, y - 18f))
            drawRect(neonColor.copy(alpha = 0.5f), topLeft = Offset(x + 18f, y - 12f), size = Size(6f, 12f))
        }
    }
}

fun DrawScope.drawHospital(x: Float, y: Float, level: Int = 1, era: Int = 1, time: Float = 0f) {
    val shadowColor = Color(0x4D000000)
    drawOval(shadowColor, topLeft = Offset(x - 6f, y + 31f), size = Size(52f, 14f))

    when (era) {
        1 -> { // Prehistoric: Witch Doctor tribal healing bonfire tent with skeletal totems
            drawRect(Color(0xFF0F172A), topLeft = Offset(x, y + 2f), size = Size(40f, 33f))
            drawRect(Color(0xFF8B4513), topLeft = Offset(x + 1f, y + 3f), size = Size(38f, 31f)) // Animal hides
            // Skeletal totem stick
            drawRect(Color(0xFFF1F5F9), topLeft = Offset(x + 18f, y - 14f), size = Size(4f, 16f))
            drawCircle(Color(0xFFF1F5F9), radius = 4f, center = Offset(x + 20f, y - 14f)) // Animal skull totem head
        }
        2 -> { // Classical: Stone paved roman bathhouse sanitarium with marble pillars
            drawRect(Color(0xFF0F172A), topLeft = Offset(x - 2f, y - 6f), size = Size(44f, 41f))
            drawRect(Color(0xFFECEFF1), topLeft = Offset(x - 1f, y - 5f), size = Size(42f, 39f))
            // Pillars
            drawRect(Color(0xFFD2B48C), topLeft = Offset(x + 4f, y - 4f), size = Size(6f, 38f))
            drawRect(Color(0xFFD2B48C), topLeft = Offset(x + 32f, y - 4f), size = Size(6f, 38f))
        }
        3 -> { // Medieval: Apothecary cathedral hospice with a detailed stained glass herbal cross
            drawRect(Color(0xFF0F172A), topLeft = Offset(x, y - 10f), size = Size(40f, 45f))
            drawRect(Color(0xFF475569), topLeft = Offset(x + 1f, y - 9f), size = Size(38f, 43f))
            // Green herbal cross window
            drawRect(Color(0xFF0F172A), topLeft = Offset(x + 14f, y - 22f), size = Size(12f, 12f))
            drawRect(Color(0xFFFFFFFF), topLeft = Offset(x + 15f, y - 21f), size = Size(10f, 10f)) // White background
            drawRect(Color(0xFF10B981), topLeft = Offset(x + 19f, y - 20f), size = Size(2f, 8f)) // Green cross vertical
            drawRect(Color(0xFF10B981), topLeft = Offset(x + 16f, y - 17f), size = Size(8f, 2f)) // Green cross horizontal
        }
        4 -> { // Industrial: Smoggy brick hospital ward with steam heat valves & piping
            drawRect(Color(0xFF0F172A), topLeft = Offset(x, y - 8f), size = Size(40f, 43f))
            drawRect(Color(0xFF451A03), topLeft = Offset(x + 1f, y - 7f), size = Size(38f, 41f))
            // Red cross metal shield signboard
            drawRect(Color(0xFF0F172A), topLeft = Offset(x + 14f, y - 20f), size = Size(12f, 12f))
            drawRect(Color(0xFFFFFFFF), topLeft = Offset(x + 15f, y - 19f), size = Size(10f, 10f))
            drawRect(Color(0xFFEF4444), topLeft = Offset(x + 19f, y - 18f), size = Size(2f, 8f)) // Red cross vertical
            drawRect(Color(0xFFEF4444), topLeft = Offset(x + 16f, y - 15f), size = Size(8f, 2f))
        }
        5 -> { // Modern: Pristine white & light-reflecting glass sanitarium clinic
            drawRect(Color(0xFF0F172A), topLeft = Offset(x, y - 10f), size = Size(40f, 45f))
            drawRect(Color(0xFFF8FAFC), topLeft = Offset(x + 1f, y - 9f), size = Size(38f, 43f)) // Sterile white concrete
            // Red cross neon glowing sign
            drawRect(Color(0xFF0F172A), topLeft = Offset(x + 15f, y - 22f), size = Size(10f, 10f))
            drawRect(Color(0xFFFFFFFF), topLeft = Offset(x + 16f, y - 21f), size = Size(8f, 8f))
            drawRect(Color(0xFFEF4444), topLeft = Offset(x + 19f, y - 20f), size = Size(2f, 6f)) // Red cross
            drawRect(Color(0xFFEF4444), topLeft = Offset(x + 17f, y - 18f), size = Size(6f, 2f))
            // Clean glass windows reflecting sky
            drawRect(Color(0xFF38BDF8), topLeft = Offset(x + 6f, y + 6f), size = Size(10f, 10f))
            drawRect(Color(0xFFFFFFFF), topLeft = Offset(x + 6f, y + 6f), size = Size(2f, 10f)) // Reflective sheen
        }
        else -> { // Cyberpunk: Laboratorium Medis Regenerasi Siber
            // Tinggi dan ramping
            drawRect(Color(0xFF0F172A), topLeft = Offset(x, y - 50f), size = Size(42f, 85f))
            drawRect(Color(0xFF0B132B), topLeft = Offset(x + 1f, y - 49f), size = Size(40f, 83f))
            
            // Silinder kaca transparan berisi cairan penyembuh
            val liquidHeight = 20f + kotlin.math.sin(time * 3f).toFloat() * 5f
            drawRect(Color(0xFF1E293B), topLeft = Offset(x + 10f, y - 30f), size = Size(22f, 40f)) // Kaca belakang
            drawRect(Color(0xFF00FF88).copy(alpha = 0.6f), topLeft = Offset(x + 10f, y + 10f - liquidHeight), size = Size(22f, liquidHeight)) // Cairan hijau
            
            // Gelembung di dalam cairan
            val offset1 = (time * 15f) % liquidHeight
            val offset2 = (time * 12f + 10f) % liquidHeight
            drawCircle(Color(0xFFFFFFFF).copy(alpha = 0.7f), radius = 2f, center = Offset(x + 15f, y + 10f - offset1))
            drawCircle(Color(0xFFFFFFFF).copy(alpha = 0.7f), radius = 1.5f, center = Offset(x + 25f, y + 10f - offset2))
            
            // Futuristic cyan neon health cross glowing
            drawRect(Color(0xFF00E5FF), topLeft = Offset(x + 15f, y - 65f), size = Size(12f, 12f))
            drawRect(Color(0xFF0B132B), topLeft = Offset(x + 16f, y - 64f), size = Size(10f, 10f))
            drawRect(Color(0xFF00E5FF), topLeft = Offset(x + 20f, y - 63f), size = Size(2f, 8f)) // Glowing cyan cross
            drawRect(Color(0xFF00E5FF), topLeft = Offset(x + 17f, y - 60f), size = Size(8f, 2f))
        }
    }
}

@Composable
fun RaidCanvas(state: GameState, viewModel: GameViewModel, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition()
    val time by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1000f,
        animationSpec = infiniteRepeatable(tween(10000, easing = LinearEasing), RepeatMode.Restart)
    )
    
    val logs by viewModel.raidLogs.collectAsState()
    val listState = rememberLazyListState()
    
    LaunchedEffect(logs.size) {
        if (logs.isNotEmpty()) {
            listState.animateScrollToItem(logs.size - 1)
        }
    }

    var weaponId = "none"
    if (state.isUnlocked("planet_breaker")) { weaponId = "planet_breaker" }
    else if (state.isUnlocked("energy_katana")) { weaponId = "energy_katana" }
    else if (state.isUnlocked("assault_rifle")) { weaponId = "assault_rifle" }
    else if (state.isUnlocked("musket")) { weaponId = "musket" }
    else if (state.isUnlocked("pedang_besi")) { weaponId = "pedang_besi" }
    else if (state.isUnlocked("tongkat_kayu")) { weaponId = "tongkat_kayu" }

    var armorId = "none"
    if (state.isUnlocked("cyber_body")) { armorId = "cyber_body" }
    else if (state.isUnlocked("nano_armor")) { armorId = "nano_armor" }
    else if (state.isUnlocked("exo_suit")) { armorId = "exo_suit" }
    else if (state.isUnlocked("armor_kevlar")) { armorId = "armor_kevlar" }
    else if (state.isUnlocked("armor_besi")) { armorId = "armor_besi" }
    else if (state.isUnlocked("kulit_hewan")) { armorId = "kulit_hewan" }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF02021E))
    ) {
        Box(
            modifier = Modifier
                .weight(0.6f)
                .fillMaxWidth()
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cx = size.width / 2
                val groundY = size.height * 0.75f
                
                drawRect(Color(0xFF0B132B), topLeft = Offset(0f, 0f), size = Size(size.width, groundY))
                
                val starProgress = (time * 0.05f) % 2f
                drawCircle(Color(0xFFFBBF24).copy(alpha = 0.5f + sin(time * 0.1f) * 0.3f), radius = 3f, center = Offset(cx * 0.3f, groundY * 0.3f))
                drawCircle(Color(0xFF00E5FF).copy(alpha = 0.5f + cos(time * 0.12f) * 0.3f), radius = 2.5f, center = Offset(cx * 1.4f, groundY * 0.2f))
                drawCircle(Color.White.copy(alpha = 0.6f), radius = 2f, center = Offset(cx * 0.8f, groundY * 0.4f))
                
                val warningAlpha = (sin(time * 0.15f) * 0.15f + 0.15f)
                drawRect(Color.Red.copy(alpha = warningAlpha), topLeft = Offset(0f, 0f), size = Size(size.width, groundY))
                
                val seaHeight = groundY * 0.25f
                val seaTop = groundY - seaHeight
                drawRect(Color(0xFF1E1B4B), topLeft = Offset(0f, seaTop), size = Size(size.width, seaHeight))
                
                val waveOffset = (time * 1.5f) % 150f
                for (wx in -150..size.width.toInt() step 150) {
                    val finalWx = wx.toFloat() + waveOffset
                    drawLine(Color(0xFFF43F5E).copy(alpha = 0.25f), start = Offset(finalWx, seaTop + 10f), end = Offset(finalWx + 50f, seaTop + 10f), strokeWidth = 1.5f)
                }
                
                drawRect(Color(0xFF0F172A), topLeft = Offset(0f, groundY), size = Size(size.width, size.height - groundY))
                drawLine(Color(0xFF334155), start = Offset(0f, groundY), end = Offset(size.width, groundY), strokeWidth = 3f)
                
                val battleCycle = (time * 1.5f) % 100f
                val gateEndX = 85f
                val startX = gateEndX + 20f
                val endX = size.width - 60f
                val playerClashX = cx - 45f
                val enemyClashX = cx + 45f
                
                var playerX = startX
                var enemyX = endX
                var isMoving = true
                var isAttacking = false
                var playerAttackProgress = 0f
                var enemyAttackProgress = 0f
                
                when {
                    battleCycle < 40f -> {
                        val t = (battleCycle / 40f).coerceIn(0f, 1f)
                        playerX = startX + (playerClashX - startX) * t
                        enemyX = endX - (endX - enemyClashX) * t
                        isMoving = true
                        isAttacking = false
                    }
                    battleCycle >= 40f && battleCycle <= 80f -> {
                        playerX = playerClashX
                        enemyX = enemyClashX
                        isMoving = false
                        isAttacking = true
                        
                        if (battleCycle in 43f..55f) {
                            playerAttackProgress = (battleCycle - 43f) / 12f
                        }
                        if (battleCycle in 58f..70f) {
                            enemyAttackProgress = (battleCycle - 58f) / 12f
                        }
                    }
                    else -> {
                        val t = ((battleCycle - 80f) / 20f).coerceIn(0f, 1f)
                        playerX = playerClashX + (startX - playerClashX) * t
                        enemyX = enemyClashX + (endX - enemyClashX) * t
                        isMoving = true
                        isAttacking = false
                    }
                }
                
                drawRect(Color(0xFF374151), topLeft = Offset(0f, groundY - 130f), size = Size(gateEndX, 130f))
                drawRect(Color(0xFF1F2937), topLeft = Offset(gateEndX - 10f, groundY - 130f), size = Size(10f, 130f))
                
                for (bx in 0..gateEndX.toInt() step 20) {
                    drawRect(Color(0xFF111827), topLeft = Offset(bx.toFloat(), groundY - 142f), size = Size(10f, 12f))
                }
                
                drawRect(Color(0xFF78350F), topLeft = Offset(0f, groundY - 55f), size = Size(40f, 55f))
                drawRect(Color(0xFF451A03), topLeft = Offset(35f, groundY - 55f), size = Size(6f, 55f))
                drawCircle(Color(0xFFFBBF24), radius = 2.5f, center = Offset(25f, groundY - 25f))
                
                val hasCannon = state.inventory.containsKey("cannon_gate")
                val hasCrossbow = state.inventory.containsKey("crossbow_gate")
                
                if (hasCannon) {
                    val towerCannonX = gateEndX - 20f
                    val towerCannonY = groundY - 146f
                    drawRect(Color(0xFF1E293B), topLeft = Offset(towerCannonX - 8f, towerCannonY - 12f), size = Size(20f, 12f))
                    drawRect(Color(0xFF0F172A), topLeft = Offset(towerCannonX + 12f, towerCannonY - 10f), size = Size(14f, 8f))
                    drawCircle(Color.DarkGray, radius = 5f, center = Offset(towerCannonX - 8f, towerCannonY - 6f))
                    
                    if (isAttacking && battleCycle in 42f..55f) {
                        val cNorm = (battleCycle - 42f) / 13f
                        val pX = towerCannonX + 26f + (cNorm * (enemyX - (towerCannonX + 26f)))
                        val pHeight = 85f
                        val pY = (towerCannonY - 6f) - (4f * pHeight * cNorm * (1f - cNorm))
                        
                        if (battleCycle in 42f..45f) {
                            drawCircle(Color(0xFFF97316), radius = 10f, center = Offset(towerCannonX + 28f, towerCannonY - 6f))
                            drawCircle(Color(0xFFFFEB3B), radius = 6f, center = Offset(towerCannonX + 28f, towerCannonY - 6f))
                        }
                        
                        drawCircle(Color(0xFF334155), radius = 7f, center = Offset(pX, pY))
                        drawCircle(Color(0xFFFF5722), radius = 4f, center = Offset(pX - 10f, pY + 2f))
                    }
                }
                
                if (hasCrossbow) {
                    val cbX = gateEndX - 45f
                    val cbY = groundY - 138f
                    drawLine(Color(0xFFB45309), start = Offset(cbX - 8f, cbY), end = Offset(cbX + 10f, cbY), strokeWidth = 3f)
                    drawLine(Color(0xFFD97706), start = Offset(cbX + 2f, cbY - 10f), end = Offset(cbX + 2f, cbY + 10f), strokeWidth = 2.5f)
                    
                    if (isAttacking && battleCycle in 45f..58f) {
                        val cbNorm = (battleCycle - 45f) / 13f
                        val pX = cbX + 8f + (cbNorm * (enemyX - (cbX + 8f)))
                        val pY = cbY
                        
                        drawLine(Color(0xFFFBBF24), start = Offset(pX, pY), end = Offset(pX - 18f, pY), strokeWidth = 3f)
                        drawCircle(Color(0xFFFFD700), radius = 3f, center = Offset(pX, pY))
                    }
                }
                
                val activeHeroName = if (state.garrisonedHeroes.isNotEmpty()) {
                    state.garrisonedHeroes[0]
                } else if (state.ownedCommanders.isNotEmpty()) {
                    state.ownedCommanders[0].split(":")[0]
                } else {
                    "prajurit_biasa"
                }
                
                val heroY = groundY - 18f
                drawRPGPlayer(
                    x = playerX,
                    y = heroY,
                    heroName = activeHeroName,
                    time = time,
                    isAttacking = isAttacking && playerAttackProgress > 0f,
                    attackProgress = playerAttackProgress
                )
                
                val maxPasukan = minOf(state.garrisonedPasukan, 4L).toInt()
                val currentEra = if (state.getBuildingLevel("town_hall") >= 5) 3 else 1
                for (i in 0 until maxPasukan) {
                    val troopX = playerX - 35f - (i * 22f)
                    val troopY = heroY + 8f + (i % 2 * 6f)
                    if (troopX > gateEndX) {
                        drawRPGTroop(
                            x = troopX,
                            y = troopY,
                            time = time,
                            isAttacking = isAttacking && playerAttackProgress > 0f,
                            attackProgress = playerAttackProgress,
                            troopIndex = i,
                            era = currentEra
                        )
                    }
                }
                
                val isMonsterDefeated = state.raidEnemyHp <= 0
                val enemyY = groundY + 15f
                if (!isMonsterDefeated || battleCycle < 75f) {
                    drawMonster(
                        x = enemyX,
                        y = enemyY,
                        name = "Raja Primordial",
                        time = time
                    )
                }
                
                if (isAttacking) {
                    if (battleCycle in 48f..56f) {
                        val sa = (battleCycle - 48f) / 8f
                        drawCircle(Color(0xFF00E5FF).copy(alpha = 1f - sa), radius = 24f * sa, center = Offset(enemyClashX - 5f, enemyY))
                        drawCircle(Color.White.copy(alpha = 0.8f * (1f - sa)), radius = 34f * sa, center = Offset(enemyClashX - 5f, enemyY), style = Stroke(width = 3.5f))
                        if (hasCannon) {
                            drawCircle(Color(0xFFEF4444).copy(alpha = 0.9f * (1f - sa)), radius = 40f * sa, center = Offset(enemyClashX - 5f, enemyY))
                        }
                    }
                    if (battleCycle in 62f..70f) {
                        val sa = (battleCycle - 62f) / 8f
                        drawCircle(Color(0xFFF43F5E).copy(alpha = 1f - sa), radius = 22f * sa, center = Offset(playerClashX + 10f, heroY))
                        drawLine(
                            color = Color.White.copy(alpha = 1f - sa),
                            start = Offset(playerClashX - 10f, heroY - 12f),
                            end = Offset(playerClashX + 22f, heroY + 12f),
                            strokeWidth = 5f
                        )
                    }
                }
                
                val paint = Paint().apply {
                    textAlign = Paint.Align.CENTER
                    typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
                }
                
                val enemyAtk = (state.raidLevel * 500L).coerceAtLeast(100L)
                val garrisonDamageTotal = (state.ownedCommanders.size * 500L + state.garrisonedPasukan * 25L).coerceAtLeast(10L)
                var playerCounterAtk = (garrisonDamageTotal * 0.25).toLong().coerceAtLeast(10L)
                if (hasCannon) playerCounterAtk += 300L
                if (hasCrossbow) playerCounterAtk += 200L
                
                if (battleCycle in 48f..70f) {
                    val p = (battleCycle - 48f) / 22f
                    val alpha = ((1f - p) * 255).toInt().coerceIn(0, 255)
                    paint.color = android.graphics.Color.argb(alpha, 0, 229, 255)
                    paint.textSize = 36f
                    drawContext.canvas.nativeCanvas.drawText("-${playerCounterAtk}", enemyClashX, enemyY - 25f - p * 50f, paint)
                }
                
                if (battleCycle in 63f..85f) {
                    val p = (battleCycle - 63f) / 22f
                    val alpha = ((1f - p) * 255).toInt().coerceIn(0, 255)
                    paint.color = android.graphics.Color.argb(alpha, 244, 63, 94)
                    paint.textSize = 36f
                    drawContext.canvas.nativeCanvas.drawText("-${enemyAtk}", playerClashX, heroY - 25f - p * 50f, paint)
                }
                
                if (battleCycle in 75f..96f) {
                    val p = (battleCycle - 75f) / 21f
                    val alpha = ((1f - p) * 255).toInt().coerceIn(0, 255)
                    
                    paint.color = android.graphics.Color.argb(alpha, 251, 191, 36)
                    paint.textSize = 32f
                    drawContext.canvas.nativeCanvas.drawText("+100 GOLD", enemyClashX, enemyY - 30f - p * 45f, paint)
                    
                    drawCircle(
                        color = Color.LightGray.copy(alpha = 1f - p),
                        radius = 12f + p * 25f,
                        center = Offset(enemyClashX, enemyY - 10f - p * 20f),
                        style = Stroke(width = 3f)
                    )
                }
            }
            
            Text(
                text = "🚨 RAID SIEGE LEVEL ${state.raidLevel} 🚨",
                color = Color.Red,
                fontWeight = FontWeight.Black,
                fontSize = 12.sp,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(8.dp)
                    .background(Color.Black.copy(alpha = 0.75f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            )
        }
        
        Column(
            modifier = Modifier
                .weight(0.4f)
                .fillMaxWidth()
                .background(Color(0xFF020214))
                .border(BorderStroke(1.dp, Color(0xFF1E293B)))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    val hpPct = (state.raidPlayerHp.toFloat() / Math.max(state.maxGerbangHp.toFloat(), 1f)).coerceIn(0f, 1f)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("❤️ HP GERBANG KOTA", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                        Text("${state.raidPlayerHp} / ${state.maxGerbangHp}", fontSize = 10.sp, color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(14.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.DarkGray)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(hpPct)
                                .fillMaxHeight()
                                .background(Color(0xFFEF4444))
                        )
                    }
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    val charge = ((time * 2.5f) % 100f) / 100f
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("⚡ CAKRAM PERTAHANAN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00E5FF))
                        Text("${(charge * 100).toInt()}% READY", fontSize = 10.sp, color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(14.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.DarkGray)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(charge)
                                .fillMaxHeight()
                                .background(Color(0xFF00E5FF))
                        )
                    }
                }
            }
            
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.Black)
                    .border(BorderStroke(1.dp, Color(0xFF1E1B4B)), RoundedCornerShape(6.dp))
                    .padding(8.dp)
            ) {
                LazyColumn(state = listState) {
                    items(logs) { log ->
                        Text(
                            text = "🛡️ $log",
                            color = if (log.contains("kemenangan", ignoreCase = true)) Color(0xFFFBBF24)
                                    else if (log.contains("kekalahan", ignoreCase = true)) Color(0xFFEF4444)
                                    else Color.LightGray,
                            fontSize = 11.sp,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
            
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "💂 GARRISON: ${state.garrisonedPasukan} Prj | 🦸 HEROES: ${state.garrisonedHeroes.size}",
                    color = Color.LightGray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "👾 BOSS HP: ${state.raidEnemyHp} / ${state.raidEnemyMaxHp}",
                    color = Color(0xFFEF4444),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun TalentTreeDialog(
    state: GameState,
    viewModel: GameViewModel,
    onDismiss: () -> Unit
) {
    val treeNodes = remember {
        allNodes.filter { it.type == "skill" }
    }

    var selectedNode by remember { mutableStateOf<TalentNode?>(null) }
    var isListView by remember { mutableStateOf(false) }
    val shownStories = remember { mutableStateListOf<String>() }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF070410)
        ) {
            val infiniteTransition = rememberInfiniteTransition(label = "pulse_trans")
            val pulseAlpha by infiniteTransition.animateFloat(
                initialValue = 0.35f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(900, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "pulseAlpha"
            )

            val vScrollState = rememberScrollState()
            val hScrollState = rememberScrollState()

            Box(modifier = Modifier.fillMaxSize()) {
                if (isListView) {
                    // Clean List View with direct research buttons
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 80.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B).copy(alpha = 0.8f)),
                                border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("📋 DAFTAR RISET TEKNOLOGI & EVOLUSI", fontWeight = FontWeight.Bold, color = Color(0xFF00E5FF), fontSize = 13.sp)
                                    Text("Klik tombol '🔬 Riset' pada teknologi yang tersedia untuk langsung menguasainya.", fontSize = 11.sp, color = Color.LightGray)
                                }
                            }
                        }
                        
                        items(treeNodes) { node ->
                            val isUnlocked = state.isUnlocked(node.id)
                            val isAvailable = node.reqs.isEmpty() || node.reqs.all { state.isUnlocked(it) }
                            val canAfford = state.totalZenith >= node.zenithCost
                            
                            val eraName = when (node.tier) {
                                1 -> "Era I • Prasejarah"
                                2 -> "Era II • Kerajaan"
                                3 -> "Era III • Industri"
                                4 -> "Era IV • Modern"
                                5 -> "Era V • Cyberpunk"
                                else -> "Era ${node.tier}"
                            }
                            
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isUnlocked) Color(0xFF064E3B).copy(alpha = 0.6f)
                                                     else if (isAvailable) Color(0xFF1E293B)
                                                     else Color(0xFF0F172A).copy(alpha = 0.5f)
                                ),
                                border = BorderStroke(
                                    width = if (isUnlocked) 1.5.dp else if (isAvailable) 1.5.dp else 1.dp,
                                    color = if (isUnlocked) Color(0xFF10B981)
                                            else if (isAvailable) Color(0xFF00E5FF).copy(alpha = 0.7f)
                                            else Color(0xFF334155)
                                )
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(node.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = if (isUnlocked) Color(0xFFFFD700) else Color.White)
                                            Text(eraName, fontSize = 10.sp, color = Color(0xFF94A3B8))
                                        }
                                        
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(
                                                    if (isUnlocked) Color(0xFF10B981).copy(alpha = 0.2f)
                                                    else if (isAvailable) Color(0xFF00E5FF).copy(alpha = 0.2f)
                                                    else Color.DarkGray.copy(alpha = 0.3f)
                                                )
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = if (isUnlocked) "✅ Dikuasai" else if (isAvailable) "⚡ Siap Riset" else "🔒 Terkunci",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isUnlocked) Color(0xFF34D399) else if (isAvailable) Color(0xFF00E5FF) else Color.LightGray
                                            )
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(node.desc, fontSize = 12.sp, color = Color(0xFFE2E8F0))
                                    
                                    if (node.reqs.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Text("Prasyarat:", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                            node.reqs.forEach { reqId ->
                                                val parent = treeNodes.find { it.id == reqId }
                                                val reqMet = state.isUnlocked(reqId)
                                                Text(
                                                    text = if (reqMet) "✓ ${parent?.name ?: reqId}" else "🔒 ${parent?.name ?: reqId}",
                                                    fontSize = 10.sp,
                                                    color = if (reqMet) Color(0xFF34D399) else Color(0xFFEF4444)
                                                )
                                            }
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            "Biaya: ⭐ ${node.zenithCost.toLong()} Zenith",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isUnlocked) Color.Gray else if (canAfford) Color(0xFFFBBF24) else Color(0xFFEF4444)
                                        )
                                        
                                        Button(
                                            onClick = { viewModel.buyNode(node.id) },
                                            enabled = isAvailable && !isUnlocked && canAfford,
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFF00E5FF),
                                                contentColor = Color.Black,
                                                disabledContainerColor = if (isUnlocked) Color(0xFF065F46) else Color(0xFF334155),
                                                disabledContentColor = if (isUnlocked) Color(0xFF34D399) else Color.LightGray
                                            )
                                        ) {
                                            Text(
                                                text = if (isUnlocked) "✅ Dikuasai"
                                                       else if (!isAvailable) "🔒 Prasyarat Belum"
                                                       else if (!canAfford) "⚠️ Zenith Kurang"
                                                       else "🔬 Riset Sekarang",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Interactive Scrollable Map Canvas
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(vScrollState)
                            .horizontalScroll(hScrollState)
                    ) {
                        val mapWidth = 850.dp
                        val mapHeight = 1530.dp

                        Box(modifier = Modifier.size(mapWidth, mapHeight)) {
                            // Background Master Cosmic Tree Illustration
                            Image(
                                painter = painterResource(id = R.drawable.img_jejaring_kosmis),
                                contentDescription = "Jejaring Kosmis",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = androidx.compose.ui.layout.ContentScale.FillBounds
                            )

                            // Interactive Touch Nodes directly on top of each illustrated circle!
                            treeNodes.forEach { node ->
                                val isUnlocked = state.isUnlocked(node.id)
                                val isAvailable = node.reqs.isEmpty() || node.reqs.all { state.isUnlocked(it) }
                                val posX = mapWidth * node.normX
                                val posY = mapHeight * node.normY

                                Box(
                                    modifier = Modifier
                                        .offset(x = posX - 26.dp, y = posY - 26.dp)
                                        .size(52.dp)
                                        .clip(CircleShape)
                                        .clickable { selectedNode = node },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isUnlocked) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .align(Alignment.BottomEnd)
                                                .background(Color(0xFF10B981), CircleShape)
                                                .border(1.5.dp, Color.White, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("✓", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                                        }
                                    } else if (isAvailable) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .border(3.dp, Color(0xFF00E5FF).copy(alpha = pulseAlpha), CircleShape)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(22.dp)
                                                .align(Alignment.BottomEnd)
                                                .background(Color(0xFFF59E0B), CircleShape)
                                                .border(1.dp, Color.White, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("⚡", fontSize = 11.sp)
                                        }
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(Color.Black.copy(alpha = 0.55f), CircleShape)
                                        )
                                        Text("🔒", fontSize = 14.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                // Top Header HUD Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.TopCenter)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xE6090514))
                        .border(1.dp, Color(0xFF00E5FF).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "🌌 JEJARING KOSMIS",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF00E5FF)
                        )
                        Text(
                            "⭐ ${state.totalZenith.toLong()} Zenith",
                            fontSize = 11.sp,
                            color = Color.LightGray
                        )
                    }
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Button(
                            onClick = { isListView = !isListView },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isListView) Color(0xFF00E5FF) else Color(0xFF4F46E5)
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text(
                                if (isListView) "🌌 Mode Peta" else "📋 Mode Daftar",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isListView) Color.Black else Color.White
                            )
                        }
                        
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.1f), CircleShape)
                                .size(34.dp)
                        ) {
                            Text("❌", fontSize = 12.sp)
                        }
                    }
                }
                
                if (selectedNode != null) {
                    val node = selectedNode!!
                    val isUnlocked = state.isUnlocked(node.id)
                    val isAvailable = node.reqs.isEmpty() || node.reqs.all { state.isUnlocked(it) }
                    
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF1E1B4B).copy(alpha = 0.95f))
                            .border(2.dp, Color(0xFF00E5FF), RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = node.name,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00E5FF)
                                )
                                IconButton(onClick = { selectedNode = null }, modifier = Modifier.size(24.dp)) {
                                    Text("❌", fontSize = 12.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            if (node.story.isNotEmpty() && (!isUnlocked || !shownStories.contains(node.id))) {
                                if (isUnlocked && !shownStories.contains(node.id)) {
                                    shownStories.add(node.id)
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color.Black.copy(alpha = 0.5f))
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = "📜 \"${node.story}\"",
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                        fontSize = 12.sp,
                                        color = Color(0xFFE2E8F0)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            
                            Text(
                                text = node.desc,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            if (isUnlocked) {
                                Text("✅ Riset telah dikuasai", color = Color(0xFF34D399), fontWeight = FontWeight.Bold)
                            } else if (!isAvailable) {
                                Text("🔒 Prasyarat belum terpenuhi", color = Color.Red, fontWeight = FontWeight.Bold)
                            } else {
                                val canAfford = canAffordNode(state, node.zenithCost, node.resourceCosts)
                                val costText = formatCost(node.zenithCost, node.resourceCosts)
                                Text("Biaya: $costText", color = if (canAfford) Color.White else Color.Red, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { viewModel.buyNode(node.id); selectedNode = null },
                                    enabled = canAfford,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF), contentColor = Color.Black)
                                ) {
                                    Text("Riset Sekarang", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TavernDialog(
    state: GameState,
    viewModel: GameViewModel,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF0F172A)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "🍻 KEDAI REKRUTMEN (TAVERN)",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFFFD700)
                        )
                        Text(
                            "Rekrut pahlawan sejarah & mitis terkuat untuk gerbang suku Anda",
                            fontSize = 11.sp,
                            color = Color.LightGray
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.1f), CircleShape)
                            .size(36.dp)
                    ) {
                        Text("❌", fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stats / Tickets Remaining
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    border = BorderStroke(1.dp, Color(0xFF475569))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("🔮", fontSize = 24.sp)
                            Column {
                                Text("Tiket Gacha Anda", fontSize = 11.sp, color = Color.LightGray)
                                Text("${state.gachaTickets} Tiket", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
                            }
                        }
                        
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("💎", fontSize = 24.sp)
                            Column {
                                Text("Shard Token", fontSize = 11.sp, color = Color.LightGray)
                                Text("${state.shardTokens} Shard", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF43F5E))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "📜 KOLEKSI PAHLAWAN (GRID DAFTAR HERO):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Grid of 8 Heroes
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(HERO_DATABASE) { hero ->
                        val isOwned = state.ownedCommanders.any { it.startsWith(hero.name) }
                        val stars = if (isOwned) state.getCommanderStars(hero.name) else 0
                        
                        val rColor = when (hero.rarity) {
                            "Mythical" -> Color(0xFFEF4444)
                            "SSR" -> Color(0xFFF59E0B)
                            "SR" -> Color(0xFF8B5CF6)
                            else -> Color(0xFF3B82F6)
                        }

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isOwned) Color(0xFF1E293B) else Color(0xFF0F172A).copy(alpha = 0.6f)
                            ),
                            border = BorderStroke(
                                width = if (isOwned) 1.5.dp else 1.dp,
                                color = if (isOwned) rColor else Color(0xFF334155)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Canvas drawing the pixel chibi!
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .background(Color(0xFF0F172A), RoundedCornerShape(4.dp))
                                        .border(1.dp, if (isOwned) rColor else Color.Transparent, RoundedCornerShape(4.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        drawChibiHero(hero.name, isOwned)
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                
                                Text(
                                    text = hero.name.substringBefore(" ("),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isOwned) Color.White else Color.Gray,
                                    maxLines = 1
                                )
                                
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .background(rColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = hero.rarity,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = rColor
                                        )
                                    }
                                    
                                    if (isOwned) {
                                        Text(
                                            text = "⭐ B$stars",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFFFD700)
                                        )
                                    } else {
                                        Text(
                                            text = "🔒 Locked",
                                            fontSize = 9.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action Buttons (1x vs 10x)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.gachaPahlawan(1) },
                        enabled = state.gachaTickets >= 1,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8))
                    ) {
                        Text("🔮 Gacha 1x (1 Tiket)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }

                    Button(
                        onClick = { viewModel.gachaPahlawan(10) },
                        enabled = state.gachaTickets >= 9,
                        modifier = Modifier.weight(1.2f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA855F7))
                    ) {
                        Text("🔥 Gacha 10x (9 Tiket)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF475569))
                ) {
                    Text("Tutup", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun GachaAnimationOverlay(
    results: List<GachaRollResult>,
    onDismiss: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition()
    val frameTime by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    var animationStateTime by remember { mutableStateOf(0L) }
    var isAnimationCompleted by remember { mutableStateOf(false) }
    
    val totalPull = results.size
    val animationDuration = if (totalPull == 1) 1200L else 3200L // Duration in ms

    LaunchedEffect(results) {
        val start = System.currentTimeMillis()
        while (System.currentTimeMillis() - start < animationDuration) {
            animationStateTime = System.currentTimeMillis() - start
            kotlinx.coroutines.delay(16)
        }
        isAnimationCompleted = true
    }

    Dialog(
        onDismissRequest = {}, // Do not allow dismissing during animation
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF030712)
        ) {
            if (!isAnimationCompleted) {
                // RUNNING ANIMATION
                Box(modifier = Modifier.fillMaxSize()) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        
                        // Dark storm gradient background
                        drawRect(
                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(Color(0xFF0F172A), Color(0xFF020617))
                            )
                        )
                        
                        // Storm lightning flashes
                        if ((animationStateTime / 400) % 3 == 0L && (animationStateTime % 100 < 30)) {
                            drawRect(Color.White.copy(alpha = 0.15f))
                        }
                        
                        // Ground line
                        val groundY = h * 0.75f
                        drawRect(Color(0xFF1E293B), topLeft = Offset(0f, groundY), size = Size(w, h - groundY))
                        drawRect(Color(0xFF0F172A), topLeft = Offset(0f, groundY), size = Size(w, 4f))

                        // Draw swords striking down!
                        if (totalPull == 1) {
                            val progress = (animationStateTime / 500f).coerceIn(0f, 1f)
                            val startY = -100f
                            val currentY = startY + (groundY - startY) * progress
                            
                            val isShaking = animationStateTime in 500L..800L
                            val offsetX = if (isShaking) ((Math.sin(animationStateTime.toDouble() * 0.1) * 8f).toFloat()) else 0f
                            
                            drawRect(
                                Color(0x6638BDF8),
                                topLeft = Offset(w / 2f - 4f + offsetX, 0f),
                                size = Size(8f, currentY)
                            )
                            
                            drawRect(
                                Color.White,
                                topLeft = Offset(w / 2f - 6f + offsetX, currentY - 50f),
                                size = Size(12f, 50f)
                            )
                            drawRect(
                                Color(0xFF38BDF8),
                                topLeft = Offset(w / 2f - 14f + offsetX, currentY - 15f),
                                size = Size(28f, 6f)
                            )
                            drawRect(
                                Color(0xFFFFD700),
                                topLeft = Offset(w / 2f - 3f + offsetX, currentY - 60f),
                                size = Size(6f, 10f)
                            )
                            
                            if (animationStateTime >= 500) {
                                val sparksProgress = ((animationStateTime - 500) / 400f).coerceIn(0f, 1f)
                                val sparksAlpha = 1f - sparksProgress
                                repeat(12) { idx ->
                                    val angle = idx * (2 * Math.PI / 12)
                                    val r = 10f + sparksProgress * 150f
                                    val sx = w / 2f + (Math.cos(angle) * r).toFloat()
                                    val sy = groundY + (Math.sin(angle) * r).toFloat()
                                    drawCircle(
                                        color = Color(0xFFFFD700).copy(alpha = sparksAlpha),
                                        radius = 6f * sparksAlpha,
                                        center = Offset(sx, sy)
                                    )
                                }
                            }
                        } else {
                            repeat(10) { swordIdx ->
                                val triggerTime = 100L + swordIdx * 240L
                                if (animationStateTime >= triggerTime) {
                                    val swordProgress = ((animationStateTime - triggerTime) / 300f).coerceIn(0f, 1f)
                                    val sX = w * (0.15f + 0.7f * (swordIdx * 7 % 10) / 9f)
                                    
                                    val startY = -100f
                                    val currentY = startY + (groundY - startY) * swordProgress
                                    
                                    if (swordProgress < 1f) {
                                        drawRect(
                                            Color(0x44A855F7),
                                            topLeft = Offset(sX - 3f, 0f),
                                            size = Size(6f, currentY)
                                        )
                                    }
                                    
                                    val sColor = if (swordIdx % 3 == 0) Color(0xFFFFD700) else Color.White
                                    drawRect(
                                        sColor,
                                        topLeft = Offset(sX - 4f, currentY - 40f),
                                        size = Size(8f, 40f)
                                    )
                                    drawRect(
                                        Color(0xFFA855F7),
                                        topLeft = Offset(sX - 10f, currentY - 10f),
                                        size = Size(20f, 4f)
                                    )
                                    
                                    if (animationStateTime >= triggerTime + 300) {
                                        val sparksElapsed = (animationStateTime - (triggerTime + 300))
                                        if (sparksElapsed < 300) {
                                            val sparksProgress = sparksElapsed / 300f
                                            val sparksAlpha = 1f - sparksProgress
                                            repeat(6) { sIdx ->
                                                val angle = sIdx * (2 * Math.PI / 6)
                                                val r = 5f + sparksProgress * 80f
                                                val sx = sX + (Math.cos(angle) * r).toFloat()
                                                val sy = groundY + (Math.sin(angle) * r).toFloat()
                                                drawCircle(
                                                    color = Color(0xFFF43F5E).copy(alpha = sparksAlpha),
                                                    radius = 4f * sparksAlpha,
                                                    center = Offset(sx, sy)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (totalPull == 1) "⚡ MEMANGGIL HERO MISTIS..." else "🌋 HUJAN PEDANG BUMI (10x PULL)...",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Menyalurkan kekuatan Zenith langit ke bumi...",
                            fontSize = 11.sp,
                            color = Color.LightGray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                // RESULTS SUMMARY
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🌟 HASIL REKRUTMEN (GACHA SUMMARY)",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFFFD700)
                    )
                    Text(
                        text = "Pahlawan baru siap bergabung dengan barisan pertahanan Anda!",
                        fontSize = 11.sp,
                        color = Color.LightGray
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(if (totalPull == 1) 1 else 2),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(results) { roll ->
                            val rColor = when {
                                roll.heroName.contains("(Mythical)") -> Color(0xFFEF4444)
                                roll.heroName.contains("(SSR)") -> Color(0xFFF59E0B)
                                roll.heroName.contains("(SR)") -> Color(0xFF8B5CF6)
                                else -> Color(0xFF3B82F6)
                            }

                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                                border = BorderStroke(1.5.dp, rColor),
                                modifier = Modifier.fillMaxWidth().then(
                                    if (totalPull == 1) Modifier.widthIn(max = 240.dp) else Modifier
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(80.dp)
                                            .background(Color(0xFF0F172A), RoundedCornerShape(4.dp))
                                            .border(1.dp, rColor, RoundedCornerShape(4.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Canvas(modifier = Modifier.fillMaxSize()) {
                                            drawChibiHero(roll.heroName, true)
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.height(8.dp))
                                    
                                    Text(
                                        text = roll.heroName,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = rColor,
                                        textAlign = TextAlign.Center
                                    )
                                    
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        repeat(roll.stars) {
                                            Text("⭐", fontSize = 12.sp)
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.height(4.dp))
                                    
                                    if (roll.isNew) {
                                        Box(
                                            modifier = Modifier
                                                .background(Color(0xFF22C55E), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("BARU!", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                    } else {
                                        if (roll.convertedShards > 0) {
                                            Text(
                                                text = "Duplikat Max! +${roll.convertedShards} Token Shard",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFF43F5E),
                                                textAlign = TextAlign.Center
                                            )
                                        } else {
                                            Box(
                                                modifier = Modifier
                                                    .background(Color(0xFF3B82F6), RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text("ASCENSION!", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("TUTUP & TERIMA", fontWeight = FontWeight.ExtraBold, color = Color.Black)
                    }
                }
            }
        }
    }
}

@Composable
fun KomandanFullscreenDialog(
    state: GameState,
    viewModel: GameViewModel,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF030712)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "👤 RPG KOMANDAN & PERALATAN",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFFFD700)
                        )
                        Text(
                            "Kelola pahlawan suku dan pasangkan senjata terbaik Anda",
                            fontSize = 11.sp,
                            color = Color.LightGray
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.1f), CircleShape)
                            .size(36.dp)
                    ) {
                        Text("❌", fontSize = 14.sp)
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                val commanders = state.ownedCommanders
                if (commanders.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("👤", fontSize = 64.sp)
                            Text(
                                "Anda belum memiliki Komandan Suku.\nLakukan Gacha di Kedai (Tavern) untuk merekrut pahlawan!",
                                color = Color.LightGray,
                                textAlign = TextAlign.Center,
                                fontSize = 14.sp
                            )
                            Button(onClick = onDismiss) {
                                Text("Tutup")
                            }
                        }
                    }
                    return@Surface
                }
                
                var selectedHeroIndex by remember { mutableIntStateOf(0) }
                if (selectedHeroIndex >= commanders.size) {
                    selectedHeroIndex = 0
                }
                
                val currentCommanderEntry = commanders[selectedHeroIndex]
                val parts = currentCommanderEntry.split(":")
                val heroName = parts[0]
                val stars = if (parts.size > 1) parts[1].toIntOrNull() ?: 1 else 1
                val weaponId = parts.getOrNull(2) ?: "none"
                val armorId = parts.getOrNull(3) ?: "none"
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1.1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF111827)),
                    border = BorderStroke(1.dp, Color(0xFF374151))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                    ) {
                        Text(
                            "PILIH KOMANDAN:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            itemsIndexed(commanders) { idx, entry ->
                                val hName = entry.split(":")[0]
                                val isSelected = idx == selectedHeroIndex
                                Card(
                                    modifier = Modifier
                                        .clickable { selectedHeroIndex = idx }
                                        .width(110.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) Color(0xFF1E1B4B) else Color(0xFF1F2937)
                                    ),
                                    border = BorderStroke(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) Color(0xFFFFD700) else Color.Transparent
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(6.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .background(Color(0xFF0F172A), RoundedCornerShape(4.dp))
                                        ) {
                                            Canvas(modifier = Modifier.fillMaxSize()) {
                                                drawChibiHero(hName, true)
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            hName.substringBefore(" ("),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            color = Color.White
                                        )
                                        Text(
                                            "⭐ B$stars",
                                            fontSize = 9.sp,
                                            color = Color(0xFFFFD700)
                                        )
                                    }
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        val rarityColor = when {
                            heroName.contains("(Mythical)") -> Color(0xFFEF4444)
                            heroName.contains("(Legend)") || heroName.contains("(SSR)") -> Color(0xFFF59E0B)
                            heroName.contains("(Very Rare)") || heroName.contains("(SR)") -> Color(0xFF8B5CF6)
                            heroName.contains("(Rare)") || heroName.contains("(R)") -> Color(0xFF3B82F6)
                            else -> Color.Gray
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .background(Color(0xFF1F2937), RoundedCornerShape(4.dp))
                                    .border(1.dp, rarityColor, RoundedCornerShape(4.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    drawChibiHero(heroName, true)
                                }
                            }
                            
                            Column(modifier = Modifier.weight(1f)) {
                                Text(heroName, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = rarityColor)
                                
                                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                    repeat(stars) {
                                        Text("⭐", fontSize = 12.sp)
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(4.dp))
                                
                                var heroBaseDamage = 0L
                                var heroBaseHp = 0L
                                if (heroName.contains("(Mythical)")) { heroBaseDamage = 50000L; heroBaseHp = 200000L }
                                else if (heroName.contains("(Legend)") || heroName.contains("(SSR)")) { heroBaseDamage = 10000L; heroBaseHp = 40000L }
                                else if (heroName.contains("(Very Rare)") || heroName.contains("(SR)")) { heroBaseDamage = 2500L; heroBaseHp = 10000L }
                                else if (heroName.contains("(Rare)") || heroName.contains("(R)")) { heroBaseDamage = 500L; heroBaseHp = 2000L }
                                else { heroBaseDamage = 50L; heroBaseHp = 200L }
                                
                                var eqDmg = 0L
                                var eqHp = 0L
                                when (weaponId) {
                                    "tongkat_kayu" -> eqDmg = 180L
                                    "pedang_besi" -> eqDmg = 980L
                                    "musket" -> eqDmg = 6000L
                                    "assault_rifle" -> eqDmg = 12500L
                                    "energy_katana" -> eqDmg = 76000L
                                    "planet_breaker" -> eqDmg = 400000L
                                }
                                when (armorId) {
                                    "kulit_hewan" -> eqHp = 200L
                                    "armor_besi" -> eqHp = 1000L
                                    "armor_kevlar" -> eqHp = 5000L
                                    "exo_suit" -> eqHp = 25000L
                                    "nano_armor" -> eqHp = 150000L
                                    "cyber_body" -> eqHp = 1000000L
                                }
                                
                                val multiplier = 1.0 + (stars - 1) * 0.1
                                val finalDmg = ((heroBaseDamage + eqDmg) * multiplier).toLong()
                                val finalHp = ((heroBaseHp + eqHp) * multiplier).toLong()
                                
                                Text("⚔️ Total Attack: $finalDmg (Base: ${(heroBaseDamage*multiplier).toLong()}${if (eqDmg > 0) " + ${(eqDmg*multiplier).toLong()}" else ""})", fontSize = 11.sp, color = Color.White)
                                Text("❤️ Total HP: $finalHp (Base: ${(heroBaseHp*multiplier).toLong()}${if (eqHp > 0) " + ${(eqHp*multiplier).toLong()}" else ""})", fontSize = 11.sp, color = Color.White)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            val weaponNode = allNodes.find { it.id == weaponId }
                            val weaponIcon = if (weaponId != "none") getWeaponIconRes(weaponId) else R.drawable.ic_wp_tombak
                            
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2937)),
                                border = BorderStroke(1.dp, if (weaponId != "none") Color(0xFFFFD700) else Color(0xFF374151))
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        painter = painterResource(id = weaponIcon),
                                        contentDescription = "Senjata",
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFF0F172A))
                                            .padding(2.dp),
                                        alpha = if (weaponId != "none") 1f else 0.35f
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Senjata", fontSize = 9.sp, color = Color.LightGray)
                                        Text(
                                            weaponNode?.name ?: "Belum Terpasang",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (weaponNode != null) Color(0xFFFFD700) else Color.Gray,
                                            maxLines = 1
                                        )
                                    }
                                    if (weaponId != "none") {
                                        IconButton(
                                            onClick = { viewModel.unequipItem(heroName, false) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Text("✕", fontSize = 12.sp, color = Color.Red, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                            
                            val armorNode = allNodes.find { it.id == armorId }
                            val armorIcon = if (armorId != "none") getWeaponIconRes(armorId) else R.drawable.ic_wp_armor_kulit
                            
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2937)),
                                border = BorderStroke(1.dp, if (armorId != "none") Color(0xFF34D399) else Color(0xFF374151))
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        painter = painterResource(id = armorIcon),
                                        contentDescription = "Armor",
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFF0F172A))
                                            .padding(2.dp),
                                        alpha = if (armorId != "none") 1f else 0.35f
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Armor", fontSize = 9.sp, color = Color.LightGray)
                                        Text(
                                            armorNode?.name ?: "Belum Terpasang",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (armorNode != null) Color(0xFF34D399) else Color.Gray,
                                            maxLines = 1
                                        )
                                    }
                                    if (armorId != "none") {
                                        IconButton(
                                            onClick = { viewModel.unequipItem(heroName, true) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Text("✕", fontSize = 12.sp, color = Color.Red, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        val isGarrisoned = state.garrisonedHeroes.contains(heroName)
                        Button(
                            onClick = { viewModel.toggleGarrisonHero(heroName) },
                            colors = ButtonDefaults.buttonColors(containerColor = if (isGarrisoned) Color(0xFFEF4444) else Color(0xFF10B981)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (isGarrisoned) "🛡️ Tarik dari Gerbang" else "🛡️ Tugaskan Jaga Gerbang")
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1.3f)
                ) {
                    Text(
                        "🎒 INVENTORI PERALATAN & TEMPA:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    val equipItems = listOf(
                        "tongkat_kayu", "pedang_besi", "musket", "assault_rifle", "energy_katana", "planet_breaker",
                        "kulit_hewan", "armor_besi", "armor_kevlar", "exo_suit", "nano_armor", "cyber_body"
                    )
                    
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(equipItems) { itemId ->
                            val node = allNodes.find { it.id == itemId } ?: return@items
                            val isUnlocked = state.isUnlocked(itemId)
                            val isEquipped = weaponId == itemId || armorId == itemId
                            val iconRes = getWeaponIconRes(itemId)
                            val isArmor = itemId.contains("armor") || itemId == "kulit_hewan" || itemId == "exo_suit" || itemId == "cyber_body"
                            val canAfford = canAffordNode(state, node.zenithCost, node.resourceCosts)
                            val costStr = formatCost(node.zenithCost, node.resourceCosts)
                            
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(115.dp)
                                    .clickable {
                                        if (isUnlocked) {
                                            if (isEquipped) {
                                                viewModel.unequipItem(heroName, isArmor)
                                            } else {
                                                viewModel.equipItem(heroName, itemId)
                                            }
                                        }
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isEquipped) Color(0xFF065F46)
                                                     else if (isUnlocked) Color(0xFF1E293B)
                                                     else Color(0xFF0F172A).copy(alpha = 0.7f)
                                ),
                                border = BorderStroke(
                                    width = if (isEquipped) 2.dp else 1.dp,
                                    color = if (isEquipped) Color(0xFF34D399)
                                            else if (isUnlocked) Color(0xFF4B5563)
                                            else Color(0xFF374151)
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Image(
                                            painter = painterResource(id = iconRes),
                                            contentDescription = node.name,
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color(0xFF0B0F19))
                                                .padding(2.dp)
                                        )
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                node.name,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isUnlocked) Color.White else Color.LightGray,
                                                maxLines = 1
                                            )
                                            Text(
                                                node.desc,
                                                fontSize = 8.sp,
                                                color = if (isUnlocked) Color(0xFF34D399) else Color.Gray,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                    
                                    if (isEquipped) {
                                        Button(
                                            onClick = { viewModel.unequipItem(heroName, isArmor) },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                                            modifier = Modifier.fillMaxWidth().height(26.dp)
                                        ) {
                                            Text("✅ TERPASANG", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                    } else if (isUnlocked) {
                                        Button(
                                            onClick = { viewModel.equipItem(heroName, itemId) },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                                            modifier = Modifier.fillMaxWidth().height(26.dp)
                                        ) {
                                            Text("⚔️ PASANG", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                    } else {
                                        Button(
                                            onClick = { viewModel.buyNode(itemId) },
                                            enabled = canAfford,
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFFF59E0B),
                                                disabledContainerColor = Color(0xFF334155)
                                            ),
                                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                                            modifier = Modifier.fillMaxWidth().height(26.dp)
                                        ) {
                                            Text(if (canAfford) "🔨 TEMPA" else "🔒 BAHAN KURANG", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = if (canAfford) Color.Black else Color.LightGray)
                                        }
                                        Text(costStr, fontSize = 7.sp, color = if (canAfford) Color(0xFFFBBF24) else Color(0xFFEF4444), maxLines = 1)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


data class HeroDef(val name: String, val rarity: String)

val HERO_DATABASE = listOf(
    HeroDef("Zeus (Mythical)", "Mythical"),
    HeroDef("Athena (Mythical)", "Mythical"),
    HeroDef("Odin (Mythical)", "Mythical"),
    HeroDef("Arthur (SSR)", "SSR"),
    HeroDef("Joan of Arc (SSR)", "SSR"),
    HeroDef("Alexander (SR)", "SR"),
    HeroDef("Gajah Mada (SR)", "SR"),
    HeroDef("Prajurit Biasa (R)", "R")
)

fun androidx.compose.ui.graphics.drawscope.DrawScope.drawChibiHero(
    name: String, isAlly: Boolean, time: Float = 0f
) {
    val x = 0f
    val y = 0f

    val heroColor = when {
        name.contains("Mythical") -> androidx.compose.ui.graphics.Color.Red
        name.contains("SSR") -> androidx.compose.ui.graphics.Color(0xFFF59E0B)
        name.contains("SR") -> androidx.compose.ui.graphics.Color(0xFF8B5CF6)
        else -> androidx.compose.ui.graphics.Color.Blue
    }
    
    drawRect(color = heroColor, topLeft = androidx.compose.ui.geometry.Offset(x - 6f, y), size = androidx.compose.ui.geometry.Size(12f, 16f))
    drawCircle(color = androidx.compose.ui.graphics.Color(0xFFFFCC99), radius = 8f, center = androidx.compose.ui.geometry.Offset(x, y - 4f))
}
