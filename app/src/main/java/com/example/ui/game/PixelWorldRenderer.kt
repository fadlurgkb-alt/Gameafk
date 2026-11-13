package com.example.ui.game

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.example.data.GameState
import kotlin.math.*

/**
 * PixelWorldRenderer
 * 
 * Mesin render visual pixel art berkualitas tinggi untuk Zenith Cosmos.
 * Menggabungkan lukisan latar 1254x1254 era-spesifik beresolusi tinggi dengan:
 * 1. Awan berarak halus (Clouds) & bayangan bergerak di tanah
 * 2. Air sungai mengalir beriak (Living River) dengan kilau air
 * 3. Manusia & prajurit beraktivitas (Walking Citizens & Guards) dengan langkah 2-frame
 * 4. Hewan ternak (Sheep & Cattle) yang merumput dengan animasi bernapas
 * 5. Partikel api unggun & asap cerobong (Campfire Fire & Smoke)
 * 6. Kabut penjelajahan alami (Dreamy Mist) untuk plot terkunci (bukan kotak hitam)
 * 7. Efek atmosfer era (lentera kerajaan, lampu kota modern, hologram cyberpunk)
 */
object PixelWorldRenderer {

    // Palet Warna Alam & Atmosfer Pixel Art
    private val cloudWhite = Color(0xFFFFFFFF)
    private val cloudShade = Color(0xFFE2E8F0)
    private val cloudDark = Color(0xFFCBD5E1)
    private val cloudShadow = Color(0x28000000)

    private val riverFoam = Color(0xFFBFDBFE)
    private val riverSparkle = Color(0xFFE0F2FE)
    private val waterRipples = Color(0x6660A5FA)

    private val fireYellow = Color(0xFFFDE047)
    private val fireOrange = Color(0xFFF97316)
    private val fireRed = Color(0xFFEF4444)
    private val smokePuff = Color(0x6694A3B8)

    private val mistLight = Color(0x88F8FAFC)
    private val mistDark = Color(0x77CBD5E1)
    private val mistCyber = Color(0x660F172A)
    private val cyberGridLine = Color(0x4422D3EE)

    fun drawWorld(
        drawScope: DrawScope,
        state: GameState,
        activeEra: Int,
        time: Float,
        w: Float,
        h: Float,
        eraBitmap: ImageBitmap,
        imgUnitAxe: ImageBitmap?,
        imgUnitSpear: ImageBitmap?
    ) {
        with(drawScope) {
            // -------------------------------------------------------------
            // 1. GAMBAR UTAMA: Full Resolution 1254x1254 Era Pixel Art
            // -------------------------------------------------------------
            drawImage(
                image = eraBitmap,
                dstOffset = IntOffset.Zero,
                dstSize = IntSize(w.roundToInt(), h.roundToInt()),
                filterQuality = FilterQuality.Low // Preserves authentic crisp pixel edges
            )

            // -------------------------------------------------------------
            // 2. SUNGAI HIDUP (Animated Living River Ripples & Sparkles)
            // -------------------------------------------------------------
            drawRiverCurrents(w, h, time, activeEra)

            // -------------------------------------------------------------
            // 3. TUMBUHAN BERGERAK (Swaying Crops & Fruit Glints)
            // -------------------------------------------------------------
            if (state.isUnlocked("pertanian")) {
                drawSwayingWheat(w, h, time, activeEra)
            }
            if (state.isUnlocked("perkebunan")) {
                drawOrchardSparkles(w, h, time)
            }

            // -------------------------------------------------------------
            // 4. HEWAN TERNAK & SATWA LIAR (Grazing Animals & Birds)
            // -------------------------------------------------------------
            if (state.isUnlocked("peternakan")) {
                drawPastureAnimals(w, h, time, activeEra)
            }
            drawFlyingBirds(w, h, time, activeEra)

            // -------------------------------------------------------------
            // 5. API UNGGUN & ASAP CEROBONG (Fire, Embers & Smoke)
            // -------------------------------------------------------------
            drawChimneysAndCampfire(w, h, time, activeEra, state)

            // -------------------------------------------------------------
            // 6. MANUSIA & PRAJURIT HIDUP (Roaming Pixel Citizens)
            // -------------------------------------------------------------
            drawLivingCitizens(w, h, time, state, activeEra, imgUnitAxe, imgUnitSpear)

            // -------------------------------------------------------------
            // 7. KABUT PENJELAJAHAN UNTUK BANGUNAN TERKUNCI (Natural Mist)
            // -------------------------------------------------------------
            drawExplorationMist(w, h, time, state, activeEra)

            // -------------------------------------------------------------
            // 8. EFEK KHUSUS ERA (Lentera, Lampu Kota, Hologram Cyber)
            // -------------------------------------------------------------
            drawEraAtmosphere(w, h, time, activeEra, state)

            // -------------------------------------------------------------
            // 9. AWAN BERARAK & BAYANGAN (Drifting Pixel Clouds & Shadows)
            // -------------------------------------------------------------
            drawDriftingClouds(w, h, time, activeEra)
        }
    }

    // =========================================================================
    // 2. SUNGAI MENGALIR DENGAN KILAUAN AIR
    // =========================================================================
    private fun DrawScope.drawRiverCurrents(w: Float, h: Float, time: Float, era: Int) {
        // Alur sungai di sisi kiri peta (x ~ 0.03w - 0.09w)
        val riverX = w * 0.055f
        val cycle = (time * 12f)

        for (i in 0 until 12) {
            val progress = ((cycle + i * 45f) % h) / h
            val y = progress * h
            val drift = sin(progress * 8f) * (w * 0.02f)
            val rx = riverX + drift

            if (era == 4) {
                // Neon energy waterway for Cyberpunk
                drawRect(
                    color = Color(0xFF22D3EE).copy(alpha = 0.5f + 0.3f * sin(time * 4f + i)),
                    topLeft = Offset(rx - 4f, y),
                    size = Size(10f, 2f)
                )
            } else {
                // Natural water foam & specular glint
                drawLine(
                    color = riverFoam.copy(alpha = 0.65f),
                    start = Offset(rx - 5f, y),
                    end = Offset(rx + 6f, y),
                    strokeWidth = 1.8f
                )
                if (i % 3 == 0) {
                    drawRect(
                        color = riverSparkle,
                        topLeft = Offset(rx + 2f, y - 1f),
                        size = Size(2f, 2f)
                    )
                }
            }
        }
    }

    // =========================================================================
    // 3. TUMBUHAN (Gandum Bergoyang & Buah Berkilau)
    // =========================================================================
    private fun DrawScope.drawSwayingWheat(w: Float, h: Float, time: Float, era: Int) {
        val farmX = w * 0.18f
        val farmY = h * 0.31f
        val sway = sin(time * 3f) * 2f

        for (row in 0 until 4) {
            val ry = farmY - 10f + (row * 6f)
            for (col in 0 until 5) {
                val rx = farmX - 16f + (col * 8f)
                val stalkColor = if (era == 4) Color(0xFF34D399) else Color(0xFFFCD34D)
                // Small wheat stalk with sway
                drawLine(
                    color = stalkColor,
                    start = Offset(rx, ry + 4f),
                    end = Offset(rx + sway, ry - 3f),
                    strokeWidth = 1.5f
                )
            }
        }
    }

    private fun DrawScope.drawOrchardSparkles(w: Float, h: Float, time: Float) {
        val orchX = w * 0.82f
        val orchY = h * 0.31f
        // Apple & berry glints
        val glint = (sin(time * 4f) + 1f) * 0.5f
        drawCircle(Color(0xFFEF4444), radius = 2f, center = Offset(orchX - 8f, orchY - 6f))
        drawCircle(Color(0xFFFDE047).copy(alpha = glint), radius = 1f, center = Offset(orchX - 8f, orchY - 6f))

        drawCircle(Color(0xFFEF4444), radius = 2f, center = Offset(orchX + 6f, orchY - 8f))
        drawCircle(Color(0xFFA855F7), radius = 2f, center = Offset(orchX - 2f, orchY + 5f))
    }

    // =========================================================================
    // 4. HEWAN TERNAK (Domba & Sapi) & BURUNG TERBANG
    // =========================================================================
    private fun DrawScope.drawPastureAnimals(w: Float, h: Float, time: Float, era: Int) {
        val pastureX = w * 0.82f
        val pastureY = h * 0.49f
        val grazeBob = (sin(time * 2f) * 1.5f).roundToInt().toFloat()

        // 1. Domba Putih (Sheep)
        val sX = pastureX - 12f
        val sY = pastureY - 2f
        // Shadow
        drawOval(Color(0x33000000), topLeft = Offset(sX - 4f, sY + 6f), size = Size(14f, 4f))
        // Body (fluffy white wool)
        drawRect(Color(0xFFF8FAFC), topLeft = Offset(sX - 3f, sY), size = Size(11f, 7f))
        drawRect(Color(0xFFE2E8F0), topLeft = Offset(sX - 1f, sY + 4f), size = Size(7f, 3f))
        // Head (grazing up and down)
        drawRect(Color(0xFF1E293B), topLeft = Offset(sX - 5f, sY + 1f + grazeBob), size = Size(3f, 4f))
        // Tiny legs
        drawRect(Color(0xFF334155), topLeft = Offset(sX - 2f, sY + 7f), size = Size(1.5f, 3f))
        drawRect(Color(0xFF334155), topLeft = Offset(sX + 5f, sY + 7f), size = Size(1.5f, 3f))

        // 2. Sapi Cokelat / Holstein Cow
        val cX = pastureX + 8f
        val cY = pastureY + 2f
        drawOval(Color(0x33000000), topLeft = Offset(cX - 5f, cY + 8f), size = Size(18f, 5f))
        val cowColor = if (era == 4) Color(0xFF06B6D4) else Color(0xFF78350F)
        drawRect(cowColor, topLeft = Offset(cX - 4f, cY), size = Size(15f, 9f))
        // White patch
        drawRect(Color(0xFFF8FAFC), topLeft = Offset(cX - 1f, cY + 2f), size = Size(5f, 4f))
        // Head
        drawRect(cowColor, topLeft = Offset(cX + 11f, cY + 1f - grazeBob), size = Size(4f, 6f))
        // Legs
        drawRect(Color(0xFF451A03), topLeft = Offset(cX - 3f, cY + 9f), size = Size(2f, 4f))
        drawRect(Color(0xFF451A03), topLeft = Offset(cX + 8f, cY + 9f), size = Size(2f, 4f))
    }

    private fun DrawScope.drawFlyingBirds(w: Float, h: Float, time: Float, era: Int) {
        val birdCycle = (time * 25f) % (w + 140f) - 70f
        val birdBaseY = h * 0.12f + sin(time * 1.5f) * 12f
        val wingFlap = (sin(time * 8f) > 0f)

        for (b in 0 until 3) {
            val bx = birdCycle - (b * 18f)
            val by = birdBaseY + (b * 9f)

            if (bx in 0f..w) {
                val birdColor = if (era == 4) Color(0xFF22D3EE) else Color(0xFF1E293B)
                if (wingFlap) {
                    // Wings Up \_/
                    drawLine(birdColor, Offset(bx - 3f, by - 2f), Offset(bx, by), strokeWidth = 1.5f)
                    drawLine(birdColor, Offset(bx, by), Offset(bx + 3f, by - 2f), strokeWidth = 1.5f)
                } else {
                    // Wings Down /-\
                    drawLine(birdColor, Offset(bx - 3f, by + 1f), Offset(bx, by), strokeWidth = 1.5f)
                    drawLine(birdColor, Offset(bx, by), Offset(bx + 3f, by + 1f), strokeWidth = 1.5f)
                }
            }
        }
    }

    // =========================================================================
    // 5. API UNGGUN, BARA & ASAP CEROBONG
    // =========================================================================
    private fun DrawScope.drawChimneysAndCampfire(w: Float, h: Float, time: Float, era: Int, state: GameState) {
        // Api Unggun Plaza Tengah
        if (state.isUnlocked("barter_purba")) {
            val cx = w * 0.50f
            val cy = h * 0.52f
            val firePulse = sin(time * 10f)

            // Warm glow aura
            drawCircle(
                color = fireOrange.copy(alpha = 0.22f + 0.08f * firePulse),
                radius = 18f + 3f * firePulse,
                center = Offset(cx, cy)
            )

            // Dynamic flame blocks
            drawRect(fireRed, topLeft = Offset(cx - 3f, cy - 3f), size = Size(6f, 6f))
            drawRect(fireOrange, topLeft = Offset(cx - 2f, cy - 5f + firePulse * 1.5f), size = Size(4f, 5f))
            drawRect(fireYellow, topLeft = Offset(cx - 1f, cy - 7f + firePulse * 2f), size = Size(2f, 4f))

            // Rising sparks / embers
            for (p in 0 until 5) {
                val seed = p * 23
                val pProgress = ((time * 30f + seed) % 60f) / 60f
                val px = cx + sin(pProgress * 6.28f + seed) * 8f
                val py = (cy - 6f) - (pProgress * 30f)
                drawRect(
                    color = fireYellow.copy(alpha = 1f - pProgress),
                    topLeft = Offset(px, py),
                    size = Size(1.5f, 1.5f)
                )
            }
        }

        // Asap Cerobong Pabrik / Barak
        if (state.isUnlocked("penebang_kayu") || era >= 3) {
            val smkX = w * 0.23f + 4f
            val smkY = h * 0.15f - 8f

            for (s in 0 until 4) {
                val sProg = ((time * 15f + s * 25f) % 70f) / 70f
                val sx = smkX + (sProg * 14f) + sin(sProg * 5f) * 4f
                val sy = smkY - (sProg * 35f)
                val rad = 2f + (sProg * 6f)
                drawCircle(
                    color = smokePuff.copy(alpha = 0.5f * (1f - sProg)),
                    radius = rad,
                    center = Offset(sx, sy)
                )
            }
        }
    }

    // =========================================================================
    // 6. MANUSIA & PRAJURIT BERJALAN DENGAN LANGKAH 2-FRAME
    // =========================================================================
    private fun DrawScope.drawLivingCitizens(
        w: Float,
        h: Float,
        time: Float,
        state: GameState,
        era: Int,
        imgAxe: ImageBitmap?,
        imgSpear: ImageBitmap?
    ) {
        val stepCadence = ((time * 4f).toInt() % 2)
        val stepBob = if (stepCadence == 0) 0f else -1.5f

        // 1. Prajurit Patroli Gerbang Selatan
        if (state.isUnlocked("barak_militer")) {
            val patrolProgress = (sin(time * 0.6f) + 1f) * 0.5f
            val gx = w * 0.35f + (patrolProgress * (w * 0.30f))
            val gy = h * 0.88f + stepBob

            if (imgSpear != null) {
                val pSize = (w * 0.08f).roundToInt()
                drawImage(
                    image = imgSpear,
                    dstOffset = IntOffset((gx - pSize / 2).roundToInt(), (gy - pSize).roundToInt()),
                    dstSize = IntSize(pSize, pSize),
                    filterQuality = FilterQuality.Low
                )
            } else {
                drawPixelChibi(gx, gy, era, isGuard = true, stepCadence)
            }
        }

        // 2. Penebang Kayu / Pekerja Tambang
        if (state.isUnlocked("penebang_kayu")) {
            val wx = w * 0.23f + 6f
            val wy = h * 0.18f + stepBob
            if (imgAxe != null) {
                val aSize = (w * 0.075f).roundToInt()
                drawImage(
                    image = imgAxe,
                    dstOffset = IntOffset((wx - aSize / 2).roundToInt(), (wy - aSize).roundToInt()),
                    dstSize = IntSize(aSize, aSize),
                    filterQuality = FilterQuality.Low
                )
            } else {
                drawPixelChibi(wx, wy, era, isGuard = false, stepCadence)
            }
        }

        // 3. Warga Berkumpul di Plaza Tengah (Campfire / Fountain)
        if (state.isUnlocked("barter_purba")) {
            val cx1 = w * 0.45f
            val cy1 = h * 0.53f + (if (stepCadence == 1) -1f else 0f)
            drawPixelChibi(cx1, cy1, era, isGuard = false, 0)

            val cx2 = w * 0.55f
            val cy2 = h * 0.54f + (if (stepCadence == 0) -1f else 0f)
            drawPixelChibi(cx2, cy2, era, isGuard = false, 1)
        }
    }

    private fun DrawScope.drawPixelChibi(
        x: Float,
        y: Float,
        era: Int,
        isGuard: Boolean,
        step: Int
    ) {
        // Shadow
        drawOval(Color(0x44000000), topLeft = Offset(x - 5f, y + 2f), size = Size(10f, 4f))

        // Skin & Clothing
        val skin = Color(0xFFFBBF24)
        val cloth = when {
            isGuard -> if (era == 4) Color(0xFF06B6D4) else if (era == 2) Color(0xFFDC2626) else Color(0xFF78350F)
            era == 4 -> Color(0xFFA855F7)
            era == 3 -> Color(0xFF2563EB)
            era == 2 -> Color(0xFF059669)
            else -> Color(0xFFD97706)
        }

        // Head
        drawRect(skin, topLeft = Offset(x - 2.5f, y - 9f), size = Size(5f, 5f))
        // Hair / Helmet
        val hairColor = if (isGuard && era >= 2) Color(0xFF94A3B8) else Color(0xFF1E1B4B)
        drawRect(hairColor, topLeft = Offset(x - 3f, y - 11f), size = Size(6f, 3f))

        // Body / Tunic
        drawRect(cloth, topLeft = Offset(x - 3f, y - 4f), size = Size(6f, 6f))

        // Animated 2-Frame Pixel Feet
        if (step == 0) {
            drawRect(Color(0xFF0F172A), topLeft = Offset(x - 2.5f, y + 2f), size = Size(2f, 2.5f))
            drawRect(Color(0xFF0F172A), topLeft = Offset(x + 0.5f, y + 1.5f), size = Size(2f, 2f))
        } else {
            drawRect(Color(0xFF0F172A), topLeft = Offset(x - 2.5f, y + 1.5f), size = Size(2f, 2f))
            drawRect(Color(0xFF0F172A), topLeft = Offset(x + 0.5f, y + 2f), size = Size(2f, 2.5f))
        }

        // Weapon for Guard
        if (isGuard) {
            val wepColor = if (era == 4) Color(0xFF22D3EE) else Color(0xFFE2E8F0)
            drawLine(wepColor, Offset(x + 4f, y - 6f), Offset(x + 4f, y + 3f), strokeWidth = 1.5f)
        }
    }

    // =========================================================================
    // 7. KABUT PENJELAJAHAN UNTUK BANGUNAN TERKUNCI (Natural Mist, Not Black Box)
    // =========================================================================
    private fun DrawScope.drawExplorationMist(w: Float, h: Float, time: Float, state: GameState, era: Int) {
        val lockedPlots = mutableListOf<Pair<Float, Float>>()

        if (!state.isUnlocked("temple")) lockedPlots.add(Pair(0.50f, 0.14f))
        if (!state.isUnlocked("penebang_kayu")) lockedPlots.add(Pair(0.23f, 0.15f))
        if (!state.isUnlocked("pemecah_batu")) lockedPlots.add(Pair(0.78f, 0.15f))
        if (!state.isUnlocked("pertanian")) lockedPlots.add(Pair(0.18f, 0.31f))
        if (!state.isUnlocked("perkebunan")) lockedPlots.add(Pair(0.82f, 0.31f))
        if (!state.isUnlocked("town_hall")) lockedPlots.add(Pair(0.50f, 0.40f))
        if (!state.isUnlocked("hospital")) lockedPlots.add(Pair(0.18f, 0.49f))
        if (!state.isUnlocked("peternakan")) lockedPlots.add(Pair(0.82f, 0.49f))
        if (!state.isUnlocked("barak_militer")) lockedPlots.add(Pair(0.20f, 0.67f))
        if (!state.isUnlocked("market") && !state.isUnlocked("barter_purba")) lockedPlots.add(Pair(0.50f, 0.66f))
        if (!state.isUnlocked("tavern")) lockedPlots.add(Pair(0.82f, 0.64f))
        if (!state.isUnlocked("dungeon")) lockedPlots.add(Pair(0.80f, 0.76f))
        if (!state.isUnlocked("tembok")) lockedPlots.add(Pair(0.50f, 0.90f))

        lockedPlots.forEach { (nx, ny) ->
            val px = nx * w
            val py = ny * h
            val pw = w * 0.16f
            val ph = h * 0.12f

            val pulse = (sin(time * 2f + (nx * 10f)) + 1f) * 0.1f

            if (era == 4) {
                // Cyberpunk: Holographic blueprint security grid
                drawRect(
                    color = mistCyber.copy(alpha = 0.75f + pulse),
                    topLeft = Offset(px - pw / 2, py - ph / 2),
                    size = Size(pw, ph)
                )
                drawRect(
                    color = Color(0xFF22D3EE).copy(alpha = 0.6f + pulse),
                    topLeft = Offset(px - pw / 2, py - ph / 2),
                    size = Size(pw, ph),
                    style = Stroke(width = 1.5f)
                )
            } else {
                // Natural dreamy mountain mist
                drawOval(
                    color = mistLight.copy(alpha = 0.72f + pulse),
                    topLeft = Offset(px - pw * 0.55f, py - ph * 0.55f),
                    size = Size(pw * 1.1f, ph * 1.1f)
                )
                drawOval(
                    color = mistDark.copy(alpha = 0.5f),
                    topLeft = Offset(px - pw * 0.45f, py - ph * 0.45f),
                    size = Size(pw * 0.9f, ph * 0.9f)
                )
            }

            // Subtle floating badge icon
            val badgeBob = sin(time * 3f + (ny * 10f)) * 2f
            drawCircle(
                color = Color(0xAA0F172A),
                radius = 7f,
                center = Offset(px, py + badgeBob)
            )
            drawCircle(
                color = if (era == 4) Color(0xFF22D3EE) else Color(0xFFFBBF24),
                radius = 7f,
                center = Offset(px, py + badgeBob),
                style = Stroke(width = 1f)
            )
        }
    }

    // =========================================================================
    // 8. EFEK ATMOSFER KHUSUS ERA (Lentera, Sinar Menara, Hologram)
    // =========================================================================
    private fun DrawScope.drawEraAtmosphere(w: Float, h: Float, time: Float, era: Int, state: GameState) {
        when (era) {
            2 -> {
                // Zaman Kerajaan: Lentera benteng & bendera singa
                val torchGlow = (sin(time * 5f) + 1f) * 0.15f
                drawCircle(
                    color = Color(0xFFFBBF24).copy(alpha = 0.25f + torchGlow),
                    radius = 12f,
                    center = Offset(w * 0.50f, h * 0.42f)
                )
            }
            3 -> {
                // Zaman Modern: Lampu mercusuar/beacon merah di atap gedung
                val beaconBlink = if ((time * 2f).toInt() % 2 == 0) 0.8f else 0.1f
                drawCircle(
                    color = Color(0xFFEF4444).copy(alpha = beaconBlink),
                    radius = 3.5f,
                    center = Offset(w * 0.50f, h * 0.36f)
                )
            }
            4 -> {
                // Zaman Cyberpunk: Rotating quantum core & neon grid conduits
                val cyberPulse = (sin(time * 6f) + 1f) * 0.25f
                drawCircle(
                    color = Color(0xFF00E5FF).copy(alpha = 0.35f + cyberPulse),
                    radius = 22f + cyberPulse * 6f,
                    center = Offset(w * 0.50f, h * 0.42f)
                )
            }
        }

        // Dungeon Portal Glow
        if (state.isUnlocked("dungeon")) {
            val portalGlow = (sin(time * 4f) + 1f) * 0.2f
            drawCircle(
                color = Color(0xFFA855F7).copy(alpha = 0.35f + portalGlow),
                radius = 24f + portalGlow * 8f,
                center = Offset(w * 0.80f, h * 0.76f)
            )
        }
    }

    // =========================================================================
    // 9. AWAN BERARAK & BAYANGAN BERGERAK (Clouds & Ground Shadows)
    // =========================================================================
    private fun DrawScope.drawDriftingClouds(w: Float, h: Float, time: Float, era: Int) {
        // 3 awan berarak bergerak dari kiri ke kanan dengan kecepatan berbeda
        val clouds = listOf(
            Triple(0.08f, 0.06f, 0.08f),  // speed, height %, size scale
            Triple(0.05f, 0.18f, 0.12f),
            Triple(0.065f, 0.32f, 0.09f)
        )

        clouds.forEachIndexed { i, (speed, ny, scale) ->
            val cloudW = w * scale * 2.2f
            val cloudH = cloudW * 0.48f

            val totalSpan = w + cloudW * 2f
            val rawX = (time * (speed * 120f) + (i * (totalSpan / 3f))) % totalSpan
            val cx = rawX - cloudW
            val cy = h * ny

            if (cx + cloudW > 0 && cx - cloudW < w) {
                // 1. Bayangan Awan di Tanah (Shadow on terrain)
                val shadowOffset = h * 0.08f
                drawOval(
                    color = cloudShadow,
                    topLeft = Offset(cx - cloudW / 2 + 10f, cy + shadowOffset),
                    size = Size(cloudW * 1.1f, cloudH * 0.8f)
                )

                // 2. Bentuk Awan Bertingkat Pixel Art (Stepped Rectangles)
                val cColor = if (era == 4) Color(0xCC0E1A2E) else cloudWhite.copy(alpha = 0.92f)
                val sColor = if (era == 4) Color(0x8806B6D4) else cloudShade.copy(alpha = 0.85f)

                // Bottom shading layer
                drawRoundRect(
                    color = sColor,
                    topLeft = Offset(cx - cloudW * 0.45f, cy + cloudH * 0.1f),
                    size = Size(cloudW * 0.9f, cloudH * 0.45f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
                )

                // Main body
                drawRoundRect(
                    color = cColor,
                    topLeft = Offset(cx - cloudW * 0.5f, cy),
                    size = Size(cloudW, cloudH * 0.45f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
                )

                // Cloud puffs / domes
                drawRoundRect(
                    color = cColor,
                    topLeft = Offset(cx - cloudW * 0.28f, cy - cloudH * 0.40f),
                    size = Size(cloudW * 0.40f, cloudH * 0.55f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f)
                )
                drawRoundRect(
                    color = cColor,
                    topLeft = Offset(cx + cloudW * 0.04f, cy - cloudH * 0.25f),
                    size = Size(cloudW * 0.32f, cloudH * 0.45f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
                )
            }
        }
    }
}
