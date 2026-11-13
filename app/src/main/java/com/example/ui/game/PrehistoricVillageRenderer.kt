package com.example.ui.game

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import com.example.data.GameState
import kotlin.math.*

/**
 * PrehistoricVillageRenderer
 * 
 * Renders the high-fidelity 16-bit pixel art prehistoric settlement exactly matching
 * the reference image. Buildings appear dynamically one by one as they are unlocked,
 * culminating in the complete, vibrant tribal village with all districts active.
 */
object PrehistoricVillageRenderer {

    // Palettes
    val grassDark = Color(0xFF1E8A38)
    val grassMid = Color(0xFF2EA043)
    val grassLight = Color(0xFF48C764)
    val dirtBase = Color(0xFFC89B60)
    val dirtDark = Color(0xFFA67840)
    val dirtHighlight = Color(0xFFDEC088)
    val riverDeep = Color(0xFF2563EB)
    val riverShallow = Color(0xFF60A5FA)
    val riverFoam = Color(0xFFBAE6FD)
    val woodDark = Color(0xFF451A03)
    val woodMid = Color(0xFF78350F)
    val woodLight = Color(0xFFB45309)
    val thatchBase = Color(0xFFD97706)
    val thatchHighlight = Color(0xFFFDE68A)
    val thatchDark = Color(0xFF92400E)
    val stoneGrey = Color(0xFF64748B)
    val stoneDark = Color(0xFF334155)
    val stoneLight = Color(0xFF94A3B8)
    val tribalRed = Color(0xFFDC2626)
    val boneWhite = Color(0xFFF8FAFC)
    val shadowColor = Color(0x40000000)

    fun drawVillage(
        drawScope: DrawScope,
        state: GameState,
        time: Float,
        w: Float,
        h: Float,
        era: Int = 1
    ) {
        with(drawScope) {
            // Helper coordinate functions
            fun px(nx: Float): Float = nx * w
            fun py(ny: Float): Float = ny * h

            // 1. BASE TERRAIN (Grassland with textured pixel dithering)
            drawBaseSavanna(w, h)

            // 2. WEST RIVER (Flowing water with lilies & stones)
            drawWestRiver(w, h, time)

            // 3. FOREST PERIMETER (Conifer & Broadleaf trees outside wall)
            drawOuterForest(w, h, time)

            // 4. ROAD NETWORK (Warm dirt paths connecting all districts)
            drawRoadNetwork(w, h)

            // 5. PALISADE DEFENSE WALL & SOUTH GATE (Tembok)
            drawPalisadeWallAndGate(w, h, time, state.isUnlocked("tembok"), state.getBuildingLevel("tembok"))

            // 6. BUILDINGS & DISTRICTS (Appear when unlocked; show wild meadow if locked)

            // (A) Top-Center: Temple / Ancestral Altar
            if (state.isUnlocked("temple")) {
                drawAncestralAltar(px(0.50f), py(0.14f), time, state.getBuildingLevel("temple"))
            } else {
                drawWildMeadowPlot(px(0.50f), py(0.14f), 0.12f * w, 0.10f * h, seed = 101)
            }

            // (B) Top-Left: Lumberjack Camp (Penebang Kayu)
            if (state.isUnlocked("penebang_kayu")) {
                drawWoodcutterCamp(px(0.23f), py(0.15f), time, state.getBuildingLevel("penebang_kayu"))
            } else {
                drawWildForestPlot(px(0.23f), py(0.15f), 0.14f * w, 0.10f * h, seed = 202)
            }

            // (C) Top-Right: Stone Quarry (Pemecah Batu)
            if (state.isUnlocked("pemecah_batu")) {
                drawStoneQuarry(px(0.78f), py(0.15f), time, state.getBuildingLevel("pemecah_batu"))
            } else {
                drawWildStonePlot(px(0.78f), py(0.15f), 0.14f * w, 0.10f * h, seed = 303)
            }

            // (D) Upper-West: Wheat & Vegetable Farm (Pertanian)
            if (state.isUnlocked("pertanian")) {
                drawAgricultureFarm(px(0.18f), py(0.31f), time, state.getBuildingLevel("pertanian"))
            } else {
                drawWildMeadowPlot(px(0.18f), py(0.31f), 0.14f * w, 0.11f * h, seed = 404)
            }

            // (E) Upper-East: Berry & Fruit Orchard (Perkebunan)
            if (state.isUnlocked("perkebunan")) {
                drawFruitOrchard(px(0.82f), py(0.31f), time, state.getBuildingLevel("perkebunan"))
            } else {
                drawWildBerryPlot(px(0.82f), py(0.31f), 0.14f * w, 0.11f * h, seed = 505)
            }

            // (F) Center Landmark: Town Hall (Chieftain Longhouse)
            if (state.isUnlocked("town_hall")) {
                drawChieftainHall(px(0.50f), py(0.42f), time, state.getBuildingLevel("town_hall"))
            } else {
                drawWildMeadowPlot(px(0.50f), py(0.42f), 0.18f * w, 0.12f * h, seed = 606)
            }

            // (G) Center Plaza: Campfire (Barter Purba)
            if (state.isUnlocked("barter_purba")) {
                drawCampfireCircle(px(0.50f), py(0.52f), time)
            } else {
                drawWildMeadowPlot(px(0.50f), py(0.52f), 0.10f * w, 0.08f * h, seed = 707)
            }

            // (H) Mid-West: Shaman Medicine Hut (Hospital)
            if (state.isUnlocked("hospital")) {
                drawShamanHut(px(0.18f), py(0.49f), time, state.getBuildingLevel("hospital"))
            } else {
                drawWildMeadowPlot(px(0.18f), py(0.49f), 0.13f * w, 0.10f * h, seed = 808)
            }

            // (I) Mid-East: Livestock Pasture (Peternakan)
            if (state.isUnlocked("peternakan")) {
                drawAnimalPasture(px(0.82f), py(0.49f), time, state.getBuildingLevel("peternakan"))
            } else {
                drawWildMeadowPlot(px(0.82f), py(0.49f), 0.14f * w, 0.11f * h, seed = 909)
            }

            // (J) Lower-West: Military Barracks & Training Field (Barak Militer)
            if (state.isUnlocked("barak_militer")) {
                drawMilitaryBarracks(px(0.20f), py(0.67f), time, state.getBuildingLevel("barak_militer"))
            } else {
                drawWildMeadowPlot(px(0.20f), py(0.67f), 0.15f * w, 0.12f * h, seed = 1010)
            }

            // (K) South-Center: Tribal Market Bazaar (Market)
            if (state.isUnlocked("market")) {
                drawTribalMarket(px(0.50f), py(0.66f), time, state.getBuildingLevel("market"))
            } else {
                drawWildMeadowPlot(px(0.50f), py(0.66f), 0.16f * w, 0.10f * h, seed = 1111)
            }

            // (L) Lower-East: Tribal Tavern & Alehouse (Tavern)
            if (state.isUnlocked("tavern")) {
                drawTribalTavern(px(0.80f), py(0.65f), time, state.getBuildingLevel("tavern"))
            } else {
                drawWildMeadowPlot(px(0.80f), py(0.65f), 0.15f * w, 0.11f * h, seed = 1212)
            }

            // (M) Bottom-Right: Dungeon Menhir Portal (Dungeon)
            if (state.isUnlocked("dungeon")) {
                drawDungeonPortal(px(0.78f), py(0.81f), time)
            } else {
                drawWildStonePlot(px(0.78f), py(0.81f), 0.14f * w, 0.10f * h, seed = 1313)
            }

            // (N) Storage Warehouse (Gudang)
            if (state.isUnlocked("gudang")) {
                drawStorageWarehouse(px(0.35f), py(0.41f), time, state.getBuildingLevel("gudang"))
            }

            // (O) Residential Thatched Huts (Rumah Warga)
            // Progressive unlock: Up to 8 huts appear around the central district
            val houseLevel = state.getBuildingLevel("rumah_warga")
            val isHouseUnlocked = state.isUnlocked("rumah_warga")
            val maxHuts = if (!isHouseUnlocked) 0 else when {
                houseLevel >= 4 -> 8
                houseLevel >= 3 -> 6
                houseLevel >= 2 -> 4
                else -> 2
            }

            val hutCoords = listOf(
                Pair(0.36f, 0.28f), // Hut 1: NW Inner
                Pair(0.64f, 0.28f), // Hut 2: NE Inner
                Pair(0.44f, 0.28f), // Hut 3: N Mid-Left
                Pair(0.56f, 0.28f), // Hut 4: N Mid-Right
                Pair(0.35f, 0.53f), // Hut 5: SW Inner
                Pair(0.65f, 0.53f), // Hut 6: SE Inner
                Pair(0.34f, 0.35f), // Hut 7: West Flank
                Pair(0.66f, 0.42f)  // Hut 8: East Flank
            )

            hutCoords.forEachIndexed { index, (hx, hy) ->
                if (index < maxHuts) {
                    drawThatchedHut(px(hx), py(hy), time, index)
                } else {
                    drawWildMeadowPlot(px(hx), py(hy), 0.07f * w, 0.06f * h, seed = 1400 + index)
                }
            }

            // 7. ROAMING CHARACTERS & TRIBAL CITIZENS
            drawRoamingCitizens(w, h, time, state)
        }
    }

    // ==========================================
    // 1. BASE SAVANNA & WILDFLOWERS
    // ==========================================
    private fun DrawScope.drawBaseSavanna(w: Float, h: Float) {
        drawRect(grassMid, topLeft = Offset.Zero, size = Size(w, h))

        // Subtle grass shading & wild flowers
        val step = 16f
        var x = 0f
        while (x < w) {
            var y = 0f
            while (y < h) {
                val rand = ((x * 19 + y * 31).toInt() % 100).absoluteValue
                if (rand < 18) {
                    drawRect(grassDark, topLeft = Offset(x, y), size = Size(step * 0.8f, step * 0.8f))
                } else if (rand > 85) {
                    drawRect(grassLight, topLeft = Offset(x, y), size = Size(step * 0.6f, step * 0.6f))
                }
                // Small wildflowers
                if (rand == 42) {
                    drawCircle(Color.White, radius = 2f, center = Offset(x + 4f, y + 4f)) // White Daisy
                    drawCircle(Color(0xFFFDE047), radius = 1f, center = Offset(x + 4f, y + 4f))
                } else if (rand == 77) {
                    drawCircle(Color(0xFFEF4444), radius = 2f, center = Offset(x + 4f, y + 4f)) // Red Poppy
                } else if (rand == 93) {
                    drawCircle(Color(0xFFFBBF24), radius = 2f, center = Offset(x + 4f, y + 4f)) // Yellow Buttercup
                }
                y += step
            }
            x += step
        }
    }

    // ==========================================
    // 2. WEST RIVER WITH LILY PADS & STONES
    // ==========================================
    private fun DrawScope.drawWestRiver(w: Float, h: Float, time: Float) {
        val riverW = w * 0.075f
        val riverPath = Path().apply {
            moveTo(0f, 0f)
            lineTo(riverW * 0.8f, 0f)
            cubicTo(riverW * 1.3f, h * 0.3f, riverW * 0.6f, h * 0.7f, riverW * 1.1f, h)
            lineTo(0f, h)
            close()
        }
        drawPath(riverPath, riverDeep)

        // Shallows & shoreline wave foam
        val foamPath = Path().apply {
            moveTo(riverW * 0.65f, 0f)
            cubicTo(riverW * 1.15f, h * 0.3f, riverW * 0.48f, h * 0.7f, riverW * 0.95f, h)
            lineTo(riverW * 0.85f, h)
            cubicTo(riverW * 0.38f, h * 0.7f, riverW * 1.05f, h * 0.3f, riverW * 0.55f, 0f)
            close()
        }
        drawPath(foamPath, riverShallow.copy(alpha = 0.5f))

        // Animated water ripples
        val waveOffset = (sin(time * 2f) * 4f)
        for (i in 1..8) {
            val ry = (h * (i / 9f)) + waveOffset
            val rx = riverW * (0.2f + 0.3f * (i % 3))
            drawLine(
                color = riverFoam.copy(alpha = 0.7f),
                start = Offset(rx, ry),
                end = Offset(rx + 14f, ry),
                strokeWidth = 2f
            )
        }

        // River rocks & Lily pads
        drawCircle(stoneGrey, radius = 5f, center = Offset(riverW * 0.35f, h * 0.22f))
        drawCircle(stoneDark, radius = 4f, center = Offset(riverW * 0.55f, h * 0.58f))

        // Lily pads
        drawCircle(Color(0xFF15803D), radius = 6f, center = Offset(riverW * 0.45f, h * 0.38f))
        drawCircle(Color(0xFFF472B6), radius = 2f, center = Offset(riverW * 0.45f, h * 0.38f)) // Pink flower

        drawCircle(Color(0xFF15803D), radius = 5f, center = Offset(riverW * 0.30f, h * 0.75f))
    }

    // ==========================================
    // 3. FOREST PERIMETER (Trees outside wall)
    // ==========================================
    private fun DrawScope.drawOuterForest(w: Float, h: Float, time: Float) {
        val treePoints = listOf(
            // North border
            Offset(w * 0.12f, h * 0.03f), Offset(w * 0.25f, h * 0.02f), Offset(w * 0.38f, h * 0.03f),
            Offset(w * 0.62f, h * 0.02f), Offset(w * 0.75f, h * 0.03f), Offset(w * 0.88f, h * 0.02f),
            // East border
            Offset(w * 0.97f, h * 0.15f), Offset(w * 0.98f, h * 0.32f), Offset(w * 0.97f, h * 0.50f),
            Offset(w * 0.98f, h * 0.68f), Offset(w * 0.97f, h * 0.84f),
            // South border
            Offset(w * 0.12f, h * 0.97f), Offset(w * 0.25f, h * 0.96f), Offset(w * 0.35f, h * 0.97f),
            Offset(w * 0.65f, h * 0.97f), Offset(w * 0.78f, h * 0.96f), Offset(w * 0.88f, h * 0.97f),
            // West river bank
            Offset(w * 0.08f, h * 0.12f), Offset(w * 0.07f, h * 0.45f), Offset(w * 0.08f, h * 0.85f)
        )

        treePoints.forEachIndexed { i, pt ->
            val isPine = i % 2 == 0
            if (isPine) {
                // Evergreen Pine Tree
                drawOval(shadowColor, topLeft = Offset(pt.x - 8f, pt.y + 6f), size = Size(16f, 6f))
                drawRect(woodDark, topLeft = Offset(pt.x - 2f, pt.y), size = Size(4f, 8f))
                val pine = Path().apply {
                    moveTo(pt.x, pt.y - 18f)
                    lineTo(pt.x + 10f, pt.y + 2f)
                    lineTo(pt.x - 10f, pt.y + 2f)
                    close()
                }
                drawPath(pine, Color(0xFF14532D))
                val pineTip = Path().apply {
                    moveTo(pt.x, pt.y - 18f)
                    lineTo(pt.x + 6f, pt.y - 7f)
                    lineTo(pt.x - 6f, pt.y - 7f)
                    close()
                }
                drawPath(pineTip, Color(0xFF166534))
            } else {
                // Lush Oak Tree
                drawOval(shadowColor, topLeft = Offset(pt.x - 10f, pt.y + 6f), size = Size(20f, 7f))
                drawRect(woodMid, topLeft = Offset(pt.x - 3f, pt.y), size = Size(6f, 10f))
                drawCircle(Color(0xFF065F46), radius = 12f, center = Offset(pt.x, pt.y - 4f))
                drawCircle(Color(0xFF047857), radius = 10f, center = Offset(pt.x - 2f, pt.y - 6f))
                drawCircle(Color(0xFF10B981), radius = 5f, center = Offset(pt.x - 3f, pt.y - 8f))
            }
        }
    }

    // ==========================================
    // 4. ROAD NETWORK (Warm dirt paths)
    // ==========================================
    private fun DrawScope.drawRoadNetwork(w: Float, h: Float) {
        val pathPaint = Stroke(width = w * 0.052f)
        val pathFine = Stroke(width = w * 0.038f)

        // 1. South Main Entrance Avenue (Gate -> Market -> Campfire)
        drawLine(dirtBase, Offset(w * 0.50f, h * 0.95f), Offset(w * 0.50f, h * 0.52f), strokeWidth = w * 0.065f)

        // 2. North Avenue (Town Hall -> Ancestral Temple)
        drawLine(dirtBase, Offset(w * 0.50f, h * 0.42f), Offset(w * 0.50f, h * 0.16f), strokeWidth = w * 0.055f)

        // 3. Central Campfire Plaza (Circular clearing)
        drawCircle(dirtBase, radius = w * 0.09f, center = Offset(w * 0.50f, h * 0.52f))
        drawCircle(dirtDark.copy(alpha = 0.4f), radius = w * 0.09f, center = Offset(w * 0.50f, h * 0.52f), style = Stroke(2f))

        // 4. Market Plaza (Expanded trading crossroads)
        drawOval(dirtBase, topLeft = Offset(w * 0.38f, h * 0.60f), size = Size(w * 0.24f, h * 0.12f))

        // 5. Diagonal Road NW (to Lumberjack & Farm)
        drawLine(dirtBase, Offset(w * 0.50f, h * 0.42f), Offset(w * 0.23f, h * 0.18f), strokeWidth = w * 0.045f)
        drawLine(dirtBase, Offset(w * 0.35f, h * 0.30f), Offset(w * 0.20f, h * 0.31f), strokeWidth = w * 0.040f)

        // 6. Diagonal Road NE (to Stone Quarry & Orchard)
        drawLine(dirtBase, Offset(w * 0.50f, h * 0.42f), Offset(w * 0.77f, h * 0.18f), strokeWidth = w * 0.045f)
        drawLine(dirtBase, Offset(w * 0.65f, h * 0.30f), Offset(w * 0.80f, h * 0.31f), strokeWidth = w * 0.040f)

        // 7. West Road (to Shaman & Barracks)
        drawLine(dirtBase, Offset(w * 0.40f, h * 0.52f), Offset(w * 0.20f, h * 0.50f), strokeWidth = w * 0.040f)
        drawLine(dirtBase, Offset(w * 0.38f, h * 0.66f), Offset(w * 0.22f, h * 0.67f), strokeWidth = w * 0.045f)

        // 8. East Road (to Pasture, Tavern & Dungeon Portal)
        drawLine(dirtBase, Offset(w * 0.60f, h * 0.52f), Offset(w * 0.80f, h * 0.50f), strokeWidth = w * 0.040f)
        drawLine(dirtBase, Offset(w * 0.62f, h * 0.66f), Offset(w * 0.78f, h * 0.65f), strokeWidth = w * 0.045f)
        drawLine(dirtBase, Offset(w * 0.60f, h * 0.72f), Offset(w * 0.76f, h * 0.81f), strokeWidth = w * 0.040f)

        // Residential hut connections
        drawLine(dirtBase, Offset(w * 0.50f, h * 0.32f), Offset(w * 0.36f, h * 0.28f), strokeWidth = w * 0.035f)
        drawLine(dirtBase, Offset(w * 0.50f, h * 0.32f), Offset(w * 0.64f, h * 0.28f), strokeWidth = w * 0.035f)
    }

    // ==========================================
    // 5. PALISADE DEFENSE WALL & SOUTH GATE
    // ==========================================
    private fun DrawScope.drawPalisadeWallAndGate(
        w: Float,
        h: Float,
        time: Float,
        isUnlocked: Boolean,
        level: Int
    ) {
        val leftX = w * 0.09f
        val rightX = w * 0.95f
        val topY = h * 0.06f
        val botY = h * 0.91f
        val gateLeft = w * 0.44f
        val gateRight = w * 0.56f

        // Palisade logs spacing
        val logW = 7f
        val logH = 16f

        // Draw Palisade stakes along North, East, South, West
        fun drawLogSpikes(startX: Float, startY: Float, endX: Float, endY: Float) {
            val dist = hypot(endX - startX, endY - startY)
            val count = (dist / logW).toInt()
            val dx = (endX - startX) / count
            val dy = (endY - startY) / count

            for (i in 0..count) {
                val lx = startX + dx * i
                val ly = startY + dy * i
                // Pointed log
                drawRect(woodMid, topLeft = Offset(lx, ly - logH * 0.5f), size = Size(logW * 0.9f, logH))
                val spike = Path().apply {
                    moveTo(lx, ly - logH * 0.5f)
                    lineTo(lx + logW * 0.45f, ly - logH * 0.5f - 5f)
                    lineTo(lx + logW * 0.9f, ly - logH * 0.5f)
                    close()
                }
                drawPath(spike, if (isUnlocked) Color(0xFFD2B48C) else woodDark)
                // Binding rope line
                drawLine(Color(0xFF5C3818), Offset(lx, ly), Offset(lx + logW, ly), strokeWidth = 1.5f)
            }
        }

        // North wall
        drawLogSpikes(leftX, topY, rightX, topY)
        // East wall
        drawLogSpikes(rightX, topY, rightX, botY)
        // West wall
        drawLogSpikes(leftX, topY, leftX, botY)
        // South wall (Left segment & Right segment flanking gate)
        drawLogSpikes(leftX, botY, gateLeft, botY)
        drawLogSpikes(gateRight, botY, rightX, botY)

        // 4 Corner Watchtowers
        drawCornerWatchtower(leftX, topY, time, isUnlocked)
        drawCornerWatchtower(rightX, topY, time, isUnlocked)
        drawCornerWatchtower(leftX, botY, time, isUnlocked)
        drawCornerWatchtower(rightX, botY, time, isUnlocked)

        // THE GRAND SOUTH GATE
        drawGrandSouthGate(gateLeft, gateRight, botY, time, isUnlocked)
    }

    private fun DrawScope.drawCornerWatchtower(x: Float, y: Float, time: Float, isUnlocked: Boolean) {
        val towerSize = 22f
        drawOval(shadowColor, topLeft = Offset(x - 12f, y + 2f), size = Size(24f, 10f))
        // Stilt legs
        drawRect(woodDark, topLeft = Offset(x - 9f, y - 12f), size = Size(4f, 18f))
        drawRect(woodDark, topLeft = Offset(x + 5f, y - 12f), size = Size(4f, 18f))
        // Platform
        drawRect(woodMid, topLeft = Offset(x - 11f, y - 16f), size = Size(towerSize, 6f))
        // Thatched roof
        val roof = Path().apply {
            moveTo(x, y - 28f)
            lineTo(x + 13f, y - 16f)
            lineTo(x - 13f, y - 16f)
            close()
        }
        drawPath(roof, thatchBase)
        // Flaming Torch
        if (isUnlocked) {
            val fOffset = sin(time * 6f + x) * 1.5f
            drawCircle(Color(0xFFF97316), radius = 3f, center = Offset(x, y - 30f + fOffset))
            drawCircle(Color(0xFFFDE047), radius = 1.5f, center = Offset(x, y - 30f + fOffset))
        }
    }

    private fun DrawScope.drawGrandSouthGate(
        gateLeft: Float,
        gateRight: Float,
        y: Float,
        time: Float,
        isUnlocked: Boolean
    ) {
        val gateWidth = gateRight - gateLeft
        val gateMid = (gateLeft + gateRight) / 2f

        // Twin Watchtowers flanking the gate
        listOf(gateLeft, gateRight).forEach { tx ->
            // Heavy timber tower
            drawRect(woodDark, topLeft = Offset(tx - 10f, y - 26f), size = Size(20f, 32f))
            drawRect(woodMid, topLeft = Offset(tx - 8f, y - 24f), size = Size(16f, 28f))
            // Platform
            drawRect(woodLight, topLeft = Offset(tx - 11f, y - 28f), size = Size(22f, 5f))
            // Red Tribal Banner
            drawRect(tribalRed, topLeft = Offset(tx - 6f, y - 14f), size = Size(12f, 16f))
            // White claw/spear insignia on banner
            drawLine(boneWhite, Offset(tx, y - 12f), Offset(tx, y - 2f), strokeWidth = 2f)
            drawLine(boneWhite, Offset(tx - 3f, y - 9f), Offset(tx, y - 12f), strokeWidth = 1.5f)
            drawLine(boneWhite, Offset(tx + 3f, y - 9f), Offset(tx, y - 12f), strokeWidth = 1.5f)

            // Blazing Fire Brazier on top
            val fOffset = sin(time * 5f + tx) * 1.5f
            drawCircle(stoneDark, radius = 4f, center = Offset(tx, y - 30f))
            drawCircle(Color(0xFFEA580C), radius = 4f, center = Offset(tx, y - 33f + fOffset))
            drawCircle(Color(0xFFFDE047), radius = 2f, center = Offset(tx, y - 33f + fOffset))
        }

        // Heavy Wooden Gate Doors (Double leaf)
        drawRect(Color(0xFF5C2C10), topLeft = Offset(gateLeft + 6f, y - 18f), size = Size(gateWidth - 12f, 22f))
        // Vertical planks
        val plankW = (gateWidth - 12f) / 6f
        for (p in 0..5) {
            drawLine(Color(0xFF2E1304), Offset(gateLeft + 6f + p * plankW, y - 18f), Offset(gateLeft + 6f + p * plankW, y + 4f), strokeWidth = 1f)
        }
        // Heavy diagonal crossbeams
        drawLine(Color(0xFF8B4513), Offset(gateLeft + 8f, y - 16f), Offset(gateMid - 2f, y + 2f), strokeWidth = 3f)
        drawLine(Color(0xFF8B4513), Offset(gateRight - 8f, y - 16f), Offset(gateMid + 2f, y + 2f), strokeWidth = 3f)
        // Center seam line
        drawLine(Color.Black, Offset(gateMid, y - 18f), Offset(gateMid, y + 4f), strokeWidth = 2f)
    }

    // ==========================================
    // 6. BUILDINGS & DISTRICTS (High Fidelity)
    // ==========================================

    /**
     * (F) TOWN HALL (Grand Chieftain Longhouse)
     * Direct center of village. Thatch roof, mammoth tusks, horned skull, red banners.
     */
    fun DrawScope.drawChieftainHall(x: Float, y: Float, time: Float, level: Int) {
        val bw = 64f
        val bh = 42f

        // Shadow
        drawOval(shadowColor, topLeft = Offset(x - bw * 0.55f, y + bh * 0.35f), size = Size(bw * 1.1f, 18f))

        // Timber Main Walls
        drawRect(woodDark, topLeft = Offset(x - bw * 0.45f, y - bh * 0.1f), size = Size(bw * 0.9f, bh * 0.6f))
        drawRect(woodMid, topLeft = Offset(x - bw * 0.42f, y - bh * 0.08f), size = Size(bw * 0.84f, bh * 0.54f))

        // Grand Layered Thatched Straw Roof
        val roof = Path().apply {
            moveTo(x, y - bh * 0.75f) // Apex peak
            lineTo(x + bw * 0.55f, y + bh * 0.05f)
            lineTo(x - bw * 0.55f, y + bh * 0.05f)
            close()
        }
        drawPath(roof, thatchBase)

        // Thatch texture ridges
        val thatchRidge = Path().apply {
            moveTo(x, y - bh * 0.75f)
            lineTo(x + bw * 0.35f, y - bh * 0.15f)
            lineTo(x - bw * 0.35f, y - bh * 0.15f)
            close()
        }
        drawPath(thatchRidge, thatchHighlight)

        // Huge Mammoth Bone Tusks arching at entrance
        val leftTusk = Path().apply {
            moveTo(x - 18f, y + bh * 0.4f)
            cubicTo(x - 26f, y + 6f, x - 28f, y - 12f, x - 18f, y - 22f)
            cubicTo(x - 24f, y - 8f, x - 22f, y + 10f, x - 15f, y + bh * 0.4f)
            close()
        }
        drawPath(leftTusk, boneWhite)

        val rightTusk = Path().apply {
            moveTo(x + 18f, y + bh * 0.4f)
            cubicTo(x + 26f, y + 6f, x + 28f, y - 12f, x + 18f, y - 22f)
            cubicTo(x + 24f, y - 8f, x + 22f, y + 10f, x + 15f, y + bh * 0.4f)
            close()
        }
        drawPath(rightTusk, boneWhite)

        // Colossal Horned Skull at apex
        drawCircle(boneWhite, radius = 6f, center = Offset(x, y - bh * 0.65f))
        drawCircle(stoneDark, radius = 1.5f, center = Offset(x - 2.5f, y - bh * 0.65f)) // Left eye
        drawCircle(stoneDark, radius = 1.5f, center = Offset(x + 2.5f, y - bh * 0.65f)) // Right eye
        // Skull horns
        val leftHorn = Path().apply {
            moveTo(x - 4f, y - bh * 0.68f)
            cubicTo(x - 10f, y - bh * 0.80f, x - 14f, y - bh * 0.70f, x - 16f, y - bh * 0.82f)
            lineTo(x - 12f, y - bh * 0.72f)
            close()
        }
        drawPath(leftHorn, boneWhite)
        val rightHorn = Path().apply {
            moveTo(x + 4f, y - bh * 0.68f)
            cubicTo(x + 10f, y - bh * 0.80f, x + 14f, y - bh * 0.70f, x + 16f, y - bh * 0.82f)
            lineTo(x + 12f, y - bh * 0.72f)
            close()
        }
        drawPath(rightHorn, boneWhite)

        // Twin Crimson Tribal Banners
        drawRect(tribalRed, topLeft = Offset(x - 14f, y - bh * 0.1f), size = Size(8f, 22f))
        drawRect(tribalRed, topLeft = Offset(x + 6f, y - bh * 0.1f), size = Size(8f, 22f))
        drawLine(boneWhite, Offset(x - 10f, y - bh * 0.05f), Offset(x - 10f, y + 14f), strokeWidth = 1.5f)
        drawLine(boneWhite, Offset(x + 10f, y - bh * 0.05f), Offset(x + 10f, y + 14f), strokeWidth = 1.5f)

        // Wooden Entrance Steps
        drawRect(Color(0xFF5C2C10), topLeft = Offset(x - 6f, y + 4f), size = Size(12f, 16f)) // Doorway
        drawRect(woodLight, topLeft = Offset(x - 9f, y + bh * 0.35f), size = Size(18f, 4f)) // Step 1
        drawRect(woodLight, topLeft = Offset(x - 7f, y + bh * 0.25f), size = Size(14f, 4f)) // Step 2

        // Flanking Torches
        val f1 = sin(time * 6f) * 1.5f
        val f2 = cos(time * 5.5f) * 1.5f
        drawRect(woodDark, topLeft = Offset(x - 22f, y + 6f), size = Size(3f, 14f))
        drawCircle(Color(0xFFEA580C), radius = 3.5f, center = Offset(x - 20.5f, y + 4f + f1))
        drawCircle(Color(0xFFFDE047), radius = 1.5f, center = Offset(x - 20.5f, y + 4f + f1))

        drawRect(woodDark, topLeft = Offset(x + 19f, y + 6f), size = Size(3f, 14f))
        drawCircle(Color(0xFFEA580C), radius = 3.5f, center = Offset(x + 20.5f, y + 4f + f2))
        drawCircle(Color(0xFFFDE047), radius = 1.5f, center = Offset(x + 20.5f, y + 4f + f2))

        // Level Badge
        drawCircle(Color(0xFFB45309), radius = 7f, center = Offset(x, y - bh * 0.85f))
        drawCircle(Color(0xFFF59E0B), radius = 5.5f, center = Offset(x, y - bh * 0.85f))
    }

    /**
     * (G) CAMPFIRE CIRCLE (Barter Purba)
     * In front of Town Hall. 8 standing stones, fire pit, stacked logs, embers.
     */
    fun DrawScope.drawCampfireCircle(x: Float, y: Float, time: Float) {
        val radius = 22f

        // Warm radial ground glow
        drawCircle(Color(0xFFF59E0B).copy(alpha = 0.20f), radius = radius * 1.6f, center = Offset(x, y))

        // 8 Standing river stones in ring
        for (i in 0..7) {
            val angle = i * (PI.toFloat() / 4f)
            val sx = x + cos(angle) * radius
            val sy = y + sin(angle) * (radius * 0.75f)
            drawOval(shadowColor, topLeft = Offset(sx - 4f, sy + 2f), size = Size(8f, 4f))
            drawCircle(stoneGrey, radius = 4f, center = Offset(sx, sy))
            drawCircle(stoneLight, radius = 2f, center = Offset(sx - 1f, sy - 1f))
        }

        // Criss-cross wood logs
        drawRect(woodDark, topLeft = Offset(x - 9f, y - 2f), size = Size(18f, 4f))
        drawRect(woodMid, topLeft = Offset(x - 2f, y - 8f), size = Size(4f, 16f))

        // Animated dancing fire layers
        val fAnim = sin(time * 8f) * 2.5f
        val fAnim2 = cos(time * 7f) * 2f

        val fireBase = Path().apply {
            moveTo(x - 8f, y + 4f)
            cubicTo(x - 12f, y - 6f, x - 4f, y - 18f + fAnim, x, y - 22f + fAnim)
            cubicTo(x + 4f, y - 18f + fAnim, x + 12f, y - 6f, x + 8f, y + 4f)
            close()
        }
        drawPath(fireBase, Color(0xFFDC2626))

        val fireMid = Path().apply {
            moveTo(x - 6f, y + 2f)
            cubicTo(x - 8f, y - 4f, x - 2f, y - 14f + fAnim2, x, y - 16f + fAnim2)
            cubicTo(x + 2f, y - 14f + fAnim2, x + 8f, y - 4f, x + 6f, y + 2f)
            close()
        }
        drawPath(fireMid, Color(0xFFF97316))

        drawCircle(Color(0xFFFDE047), radius = 4f, center = Offset(x, y - 2f + fAnim2))

        // Floating sparks & smoke
        val sparkY = y - 24f - ((time * 25f) % 20f)
        val sparkX = x + sin(time * 4f) * 5f
        drawCircle(Color(0xFFFBBF24), radius = 1.2f, center = Offset(sparkX, sparkY))
    }

    /**
     * (A) TEMPLE / ANCESTRAL ALTAR
     * Top-Center. Stepped stone pyramid, colossal skull, runic menhirs, braziers.
     */
    fun DrawScope.drawAncestralAltar(x: Float, y: Float, time: Float, level: Int) {
        val w = 56f
        val h = 38f

        // Shadow
        drawOval(shadowColor, topLeft = Offset(x - w * 0.55f, y + h * 0.35f), size = Size(w * 1.1f, 14f))

        // 3-Tier Stepped Stone Pyramid Plinth
        // Tier 1 (Base)
        drawRect(stoneDark, topLeft = Offset(x - w * 0.45f, y + h * 0.1f), size = Size(w * 0.9f, 10f))
        drawRect(stoneGrey, topLeft = Offset(x - w * 0.43f, y + h * 0.12f), size = Size(w * 0.86f, 8f))
        // Tier 2 (Mid)
        drawRect(stoneDark, topLeft = Offset(x - w * 0.32f, y - h * 0.1f), size = Size(w * 0.64f, 10f))
        drawRect(stoneLight, topLeft = Offset(x - w * 0.30f, y - h * 0.08f), size = Size(w * 0.60f, 8f))
        // Tier 3 (Top summit)
        drawRect(stoneDark, topLeft = Offset(x - w * 0.18f, y - h * 0.3f), size = Size(w * 0.36f, 10f))
        drawRect(stoneLight, topLeft = Offset(x - w * 0.16f, y - h * 0.28f), size = Size(w * 0.32f, 8f))

        // Colossal Horned Beast Skull atop the altar
        val skullY = y - h * 0.48f
        drawCircle(boneWhite, radius = 7f, center = Offset(x, skullY))
        drawCircle(stoneDark, radius = 1.8f, center = Offset(x - 3f, skullY))
        drawCircle(stoneDark, radius = 1.8f, center = Offset(x + 3f, skullY))
        // Mammoth curved horns
        val lHorn = Path().apply {
            moveTo(x - 5f, skullY - 2f)
            cubicTo(x - 14f, skullY - 12f, x - 18f, skullY, x - 22f, skullY - 8f)
            lineTo(x - 16f, skullY - 4f)
            close()
        }
        drawPath(lHorn, boneWhite)
        val rHorn = Path().apply {
            moveTo(x + 5f, skullY - 2f)
            cubicTo(x + 14f, skullY - 12f, x + 18f, skullY, x + 22f, skullY - 8f)
            lineTo(x + 16f, skullY - 4f)
            close()
        }
        drawPath(rHorn, boneWhite)

        // Runic Menhirs flanking the steps
        listOf(x - 22f, x + 22f).forEach { mx ->
            drawRect(stoneDark, topLeft = Offset(mx - 4f, y - 8f), size = Size(8f, 22f))
            drawRect(stoneLight, topLeft = Offset(mx - 3f, y - 6f), size = Size(6f, 18f))
            // Glowing Purple Rune
            val runeGlow = (sin(time * 3f + mx) * 0.4f + 0.6f)
            drawLine(
                Color(0xFFA855F7).copy(alpha = runeGlow),
                Offset(mx, y - 2f),
                Offset(mx, y + 6f),
                strokeWidth = 2f
            )
        }

        // Fire Braziers on pillars
        listOf(x - 14f, x + 14f).forEach { bx ->
            drawRect(stoneDark, topLeft = Offset(bx - 3f, y + 6f), size = Size(6f, 12f))
            val fOffset = sin(time * 6f + bx) * 1.5f
            drawCircle(Color(0xFFEA580C), radius = 3.5f, center = Offset(bx, y + 4f + fOffset))
            drawCircle(Color(0xFFFDE047), radius = 1.5f, center = Offset(bx, y + 4f + fOffset))
        }
    }

    /**
     * (B) LUMBERJACK CAMP (Penebang Kayu)
     * Top-Left. Timber shed, stacks of logs, tree stumps with axes, lumberjack.
     */
    fun DrawScope.drawWoodcutterCamp(x: Float, y: Float, time: Float, level: Int) {
        val w = 54f
        val h = 34f

        drawOval(shadowColor, topLeft = Offset(x - w * 0.5f, y + h * 0.35f), size = Size(w, 14f))

        // Open timber workstation shed with fabric canopy
        drawRect(woodDark, topLeft = Offset(x - 18f, y - 10f), size = Size(4f, 18f)) // Left stilt
        drawRect(woodDark, topLeft = Offset(x + 10f, y - 10f), size = Size(4f, 18f)) // Right stilt
        // Canopy
        val canopy = Path().apply {
            moveTo(x - 22f, y - 8f)
            lineTo(x - 2f, y - 20f)
            lineTo(x + 16f, y - 8f)
            close()
        }
        drawPath(canopy, Color(0xFFD4B996))
        drawPath(canopy, color = woodMid, style = Stroke(1.5f))

        // Stacked piles of cylindrical logs
        val logStackX = x + 14f
        val logStackY = y + 4f
        // Bottom tier (3 logs)
        for (i in 0..2) {
            val lx = logStackX + i * 8f
            drawRect(woodMid, topLeft = Offset(lx, logStackY), size = Size(14f, 6f))
            drawCircle(woodLight, radius = 3f, center = Offset(lx + 14f, logStackY + 3f)) // End grain
        }
        // Top tier (2 logs)
        for (i in 0..1) {
            val lx = logStackX + 4f + i * 8f
            drawRect(woodMid, topLeft = Offset(lx, logStackY - 5f), size = Size(14f, 6f))
            drawCircle(woodLight, radius = 3f, center = Offset(lx + 14f, logStackY - 2f))
        }

        // Tree Stump with embedded Stone Axe
        val stumpX = x - 14f
        val stumpY = y + 8f
        drawCircle(woodMid, radius = 5f, center = Offset(stumpX, stumpY))
        drawCircle(woodLight, radius = 3.5f, center = Offset(stumpX, stumpY))
        // Stone Axe wedged in stump
        drawLine(woodDark, Offset(stumpX, stumpY), Offset(stumpX + 6f, stumpY - 10f), strokeWidth = 2f)
        drawRect(stoneGrey, topLeft = Offset(stumpX + 4f, stumpY - 12f), size = Size(4f, 4f))

        // Animated Lumberjack Worker chopping
        val chopAnim = sin(time * 4f) * 8f
        val workerX = x - 4f
        val workerY = y + 4f
        drawCircle(Color(0xFFD97706), radius = 3f, center = Offset(workerX, workerY - 8f)) // Head
        drawRect(Color(0xFF92400E), topLeft = Offset(workerX - 2f, workerY - 5f), size = Size(4f, 8f)) // Body
        // Swinging axe
        drawLine(woodDark, Offset(workerX + 1f, workerY - 4f), Offset(workerX + 6f, workerY - 6f + chopAnim), strokeWidth = 1.5f)
    }

    /**
     * (C) STONE QUARRY (Pemecah Batu)
     * Top-Right. Grey cliff mountain, timber mine entrance, stacked stone cubes, crane hoist.
     */
    fun DrawScope.drawStoneQuarry(x: Float, y: Float, time: Float, level: Int) {
        val w = 58f
        val h = 36f

        drawOval(shadowColor, topLeft = Offset(x - w * 0.5f, y + h * 0.35f), size = Size(w, 14f))

        // Rocky Mountain Cliff Face
        val cliff = Path().apply {
            moveTo(x - 26f, y + 10f)
            lineTo(x - 22f, y - 16f)
            lineTo(x - 8f, y - 24f)
            lineTo(x + 12f, y - 26f)
            lineTo(x + 28f, y - 14f)
            lineTo(x + 26f, y + 10f)
            close()
        }
        drawPath(cliff, stoneDark)
        val cliffFront = Path().apply {
            moveTo(x - 22f, y + 8f)
            lineTo(x - 18f, y - 12f)
            lineTo(x - 6f, y - 20f)
            lineTo(x + 10f, y - 22f)
            lineTo(x + 24f, y - 10f)
            lineTo(x + 22f, y + 8f)
            close()
        }
        drawPath(cliffFront, stoneGrey)

        // Mine Shaft Tunnel framed with dark timbers
        drawRect(Color(0xFF0F172A), topLeft = Offset(x - 8f, y - 10f), size = Size(16f, 18f))
        drawRect(woodDark, topLeft = Offset(x - 10f, y - 12f), size = Size(3f, 20f)) // Left frame
        drawRect(woodDark, topLeft = Offset(x + 7f, y - 12f), size = Size(3f, 20f))  // Right frame
        drawRect(woodMid, topLeft = Offset(x - 11f, y - 14f), size = Size(22f, 4f))  // Top lintel

        // Stacks of Chiseled Stone Cubes
        val cubeX = x + 16f
        val cubeY = y + 4f
        for (row in 0..1) {
            for (col in 0..2) {
                drawRect(stoneDark, topLeft = Offset(cubeX + col * 7f, cubeY - row * 7f), size = Size(6f, 6f))
                drawRect(stoneLight, topLeft = Offset(cubeX + col * 7f, cubeY - row * 7f), size = Size(5f, 5f))
            }
        }

        // Wooden Crane / Derrick Hoist
        val craneX = x - 18f
        val craneY = y + 2f
        drawRect(woodDark, topLeft = Offset(craneX, craneY - 18f), size = Size(3f, 22f)) // Mast
        drawLine(woodMid, Offset(craneX, craneY - 16f), Offset(craneX + 12f, craneY - 8f), strokeWidth = 2.5f) // Jib boom
        // Rope and hanging bucket
        drawLine(Color(0xFFD4B996), Offset(craneX + 12f, craneY - 8f), Offset(craneX + 12f, craneY + 2f), strokeWidth = 1f)
        drawRect(woodDark, topLeft = Offset(craneX + 10f, craneY + 2f), size = Size(5f, 4f))

        // Miner Worker with Pickaxe
        val minerX = x + 4f
        val minerY = y + 8f
        drawCircle(Color(0xFFF59E0B), radius = 3f, center = Offset(minerX, minerY - 8f)) // Helmet
        drawRect(stoneDark, topLeft = Offset(minerX - 2f, minerY - 5f), size = Size(4f, 8f)) // Body
        // Pickaxe swing
        val pickAnim = cos(time * 4f) * 6f
        drawLine(woodDark, Offset(minerX + 1f, minerY - 4f), Offset(minerX + 6f, minerY - 8f + pickAnim), strokeWidth = 1.5f)
        drawLine(stoneLight, Offset(minerX + 4f, minerY - 10f + pickAnim), Offset(minerX + 8f, minerY - 6f + pickAnim), strokeWidth = 2f)
    }

    /**
     * (D) WHEAT & VEGETABLE FARM (Pertanian)
     * Upper-West. Wooden post-and-rail fence, golden wheat rows, carrot/cabbage garden, well, shed.
     */
    fun DrawScope.drawAgricultureFarm(x: Float, y: Float, time: Float, level: Int) {
        val w = 54f
        val h = 36f

        drawOval(shadowColor, topLeft = Offset(x - w * 0.5f, y + h * 0.35f), size = Size(w, 14f))

        // Split-rail wooden fence perimeter
        drawRect(woodMid, topLeft = Offset(x - w * 0.45f, y - h * 0.45f), size = Size(w * 0.9f, h * 0.9f), style = Stroke(1.5f))

        // Golden Wheat Field (Left sector)
        val wheatX = x - w * 0.40f
        val wheatY = y - h * 0.35f
        drawRect(Color(0xFFFEF08A), topLeft = Offset(wheatX, wheatY), size = Size(20f, 22f))
        for (wx in 0..3) {
            val sway = sin(time * 3f + wx) * 1.5f
            drawLine(Color(0xFFCA8A04), Offset(wheatX + wx * 5f, wheatY + 22f), Offset(wheatX + wx * 5f + sway, wheatY + 4f), strokeWidth = 2f)
            drawCircle(Color(0xFFEAB308), radius = 2f, center = Offset(wheatX + wx * 5f + sway, wheatY + 4f))
        }

        // Tilled Soil Patch with Carrots & Cabbages (Right sector)
        val vegX = x + 2f
        val vegY = y - h * 0.35f
        drawRect(Color(0xFF451A03), topLeft = Offset(vegX, vegY), size = Size(20f, 14f)) // Soil bed
        // Carrots & Cabbages
        for (vx in 0..2) {
            drawCircle(Color(0xFFEA580C), radius = 2f, center = Offset(vegX + 3f + vx * 6f, vegY + 4f)) // Carrot
            drawCircle(Color(0xFF16A34A), radius = 1.2f, center = Offset(vegX + 3f + vx * 6f, vegY + 2f)) // Leaves
            drawCircle(Color(0xFF22C55E), radius = 3f, center = Offset(vegX + 3f + vx * 6f, vegY + 10f)) // Cabbage
        }

        // Stone Water Well with roof
        val wellX = x + 8f
        val wellY = y + 8f
        drawCircle(stoneGrey, radius = 5f, center = Offset(wellX, wellY))
        drawCircle(Color(0xFF2563EB), radius = 3f, center = Offset(wellX, wellY))
        // Well roof
        drawRect(woodDark, topLeft = Offset(wellX - 4f, wellY - 12f), size = Size(2f, 10f))
        drawRect(woodDark, topLeft = Offset(wellX + 2f, wellY - 12f), size = Size(2f, 10f))
        val wellRoof = Path().apply {
            moveTo(wellX, wellY - 16f)
            lineTo(wellX + 6f, wellY - 11f)
            lineTo(wellX - 6f, wellY - 11f)
            close()
        }
        drawPath(wellRoof, woodMid)
    }

    /**
     * (E) BERRY & FRUIT ORCHARD (Perkebunan)
     * Upper-East. Wooden fence, fruit trees bursting with red berries, harvest crates, gatherer hut.
     */
    fun DrawScope.drawFruitOrchard(x: Float, y: Float, time: Float, level: Int) {
        val w = 54f
        val h = 36f

        drawOval(shadowColor, topLeft = Offset(x - w * 0.5f, y + h * 0.35f), size = Size(w, 14f))

        // Split-rail wooden fence
        drawRect(woodMid, topLeft = Offset(x - w * 0.45f, y - h * 0.45f), size = Size(w * 0.9f, h * 0.9f), style = Stroke(1.5f))

        // 3 Fruit Trees laden with bright red berries
        val treeLocs = listOf(
            Offset(x - 12f, y - 4f),
            Offset(x + 4f, y - 8f),
            Offset(x + 14f, y + 4f)
        )
        treeLocs.forEachIndexed { i, pos ->
            drawRect(woodDark, topLeft = Offset(pos.x - 2f, pos.y), size = Size(4f, 10f)) // Trunk
            drawCircle(Color(0xFF15803D), radius = 10f, center = Offset(pos.x, pos.y - 4f))
            drawCircle(Color(0xFF22C55E), radius = 8f, center = Offset(pos.x - 1f, pos.y - 6f))

            // Ripe red berries
            val berryOffsets = listOf(
                Offset(-3f, -4f), Offset(4f, -2f), Offset(-2f, -8f), Offset(2f, -6f), Offset(0f, 0f)
            )
            berryOffsets.forEach { bOff ->
                drawCircle(Color(0xFFEF4444), radius = 2f, center = Offset(pos.x + bOff.x, pos.y - 4f + bOff.y))
            }
        }

        // Harvest Storage Hut & Baskets
        val hutX = x - 14f
        val hutY = y + 8f
        drawRect(woodDark, topLeft = Offset(hutX - 6f, hutY - 6f), size = Size(12f, 10f))
        val hRoof = Path().apply {
            moveTo(hutX, hutY - 12f)
            lineTo(hutX + 8f, hutY - 6f)
            lineTo(hutX - 8f, hutY - 6f)
            close()
        }
        drawPath(hRoof, thatchBase)

        // Wooden crate overflowing with berries
        drawRect(woodLight, topLeft = Offset(hutX + 8f, hutY), size = Size(6f, 5f))
        drawCircle(Color(0xFFEF4444), radius = 1.5f, center = Offset(hutX + 10f, hutY))
        drawCircle(Color(0xFFEF4444), radius = 1.5f, center = Offset(hutX + 12f, hutY - 1f))
    }

    /**
     * (H) SHAMAN MEDICINE HUT (Hospital)
     * Mid-West. Bone & hide round hut, skull crest, tall carved totem pole, bubbling ritual cauldron.
     */
    fun DrawScope.drawShamanHut(x: Float, y: Float, time: Float, level: Int) {
        val w = 50f
        val h = 34f

        drawOval(shadowColor, topLeft = Offset(x - w * 0.5f, y + h * 0.35f), size = Size(w, 14f))

        // Circular bone & mud hut
        drawOval(woodDark, topLeft = Offset(x - 16f, y - 8f), size = Size(32f, 22f))
        // Conical thatched straw roof
        val roof = Path().apply {
            moveTo(x, y - 22f)
            lineTo(x + 18f, y - 6f)
            lineTo(x - 18f, y - 6f)
            close()
        }
        drawPath(roof, thatchBase)
        drawCircle(boneWhite, radius = 4f, center = Offset(x, y - 22f)) // Skull apex

        // Wooden doorway
        drawRect(Color(0xFF1E1004), topLeft = Offset(x - 5f, y + 2f), size = Size(10f, 12f))

        // Tall Carved Totem Pole with painted faces & horns
        val totemX = x + 18f
        val totemY = y + 2f
        drawRect(woodDark, topLeft = Offset(totemX - 3f, totemY - 24f), size = Size(6f, 30f))
        drawRect(Color(0xFF0284C7), topLeft = Offset(totemX - 4f, totemY - 20f), size = Size(8f, 6f)) // Blue face
        drawRect(tribalRed, topLeft = Offset(totemX - 4f, totemY - 12f), size = Size(8f, 6f)) // Red face
        drawCircle(boneWhite, radius = 2f, center = Offset(totemX, totemY - 24f)) // Horned crest
        // Feathers
        drawLine(Color(0xFFF59E0B), Offset(totemX, totemY - 25f), Offset(totemX - 5f, totemY - 30f), strokeWidth = 1.5f)
        drawLine(Color(0xFFF59E0B), Offset(totemX, totemY - 25f), Offset(totemX + 5f, totemY - 30f), strokeWidth = 1.5f)

        // Stone hearth with Bubbling Ritual Cauldron
        val cauldX = x - 16f
        val cauldY = y + 6f
        drawCircle(stoneDark, radius = 5f, center = Offset(cauldX, cauldY))
        drawCircle(Color(0xFF0F172A), radius = 4.5f, center = Offset(cauldX, cauldY)) // Iron pot
        drawCircle(Color(0xFF10B981), radius = 3.5f, center = Offset(cauldX, cauldY)) // Potion liquid

        // Animated steam bubbles
        val bubbleY = cauldY - 6f - ((time * 15f) % 12f)
        val bubbleX = cauldX + sin(time * 5f) * 2f
        drawCircle(Color(0xFF34D399).copy(alpha = 0.7f), radius = 2f, center = Offset(bubbleX, bubbleY))
    }

    /**
     * (I) LIVESTOCK PASTURE (Peternakan)
     * Mid-East. Fenced corral, fluffy sheep, dark wild boar, shelter, hay bales.
     */
    fun DrawScope.drawAnimalPasture(x: Float, y: Float, time: Float, level: Int) {
        val w = 54f
        val h = 36f

        drawOval(shadowColor, topLeft = Offset(x - w * 0.5f, y + h * 0.35f), size = Size(w, 14f))

        // Fenced corral perimeter
        drawRect(woodMid, topLeft = Offset(x - w * 0.45f, y - h * 0.45f), size = Size(w * 0.9f, h * 0.9f), style = Stroke(1.5f))

        // Animal Shelter hut
        val sX = x + 12f
        val sY = y - 6f
        drawRect(woodDark, topLeft = Offset(sX - 8f, sY - 6f), size = Size(16f, 14f))
        val sRoof = Path().apply {
            moveTo(sX, sY - 14f)
            lineTo(sX + 10f, sY - 6f)
            lineTo(sX - 10f, sY - 6f)
            close()
        }
        drawPath(sRoof, thatchBase)

        // Golden Hay Bales
        drawRect(Color(0xFFEAB308), topLeft = Offset(x + 10f, y + 6f), size = Size(8f, 6f))

        // Fluffy White Sheep (grazing & animated)
        val sheepBob = sin(time * 3f) * 1.5f
        val sheep1X = x - 12f
        val sheep1Y = y - 2f
        drawOval(boneWhite, topLeft = Offset(sheep1X - 7f, sheep1Y - 5f + sheepBob), size = Size(14f, 10f)) // Wool body
        drawCircle(stoneDark, radius = 3f, center = Offset(sheep1X - 7f, sheep1Y - 1f + sheepBob)) // Head
        // Legs
        drawLine(stoneDark, Offset(sheep1X - 3f, sheep1Y + 5f), Offset(sheep1X - 3f, sheep1Y + 9f), strokeWidth = 1.5f)
        drawLine(stoneDark, Offset(sheep1X + 3f, sheep1Y + 5f), Offset(sheep1X + 3f, sheep1Y + 9f), strokeWidth = 1.5f)

        // Wild Boar / Pig
        val boarX = x - 4f
        val boarY = y + 8f
        drawOval(Color(0xFF451A03), topLeft = Offset(boarX - 6f, boarY - 4f), size = Size(12f, 8f)) // Body
        drawCircle(Color(0xFF78350F), radius = 3f, center = Offset(boarX + 6f, boarY - 1f)) // Head & snout
        drawCircle(boneWhite, radius = 1.2f, center = Offset(boarX + 8f, boarY - 2f)) // White tusk
    }

    /**
     * (J) MILITARY BARRACKS (Barak Militer)
     * Lower-West. Timber barracks, red war shields, archery targets, weapon racks, dummies.
     */
    fun DrawScope.drawMilitaryBarracks(x: Float, y: Float, time: Float, level: Int) {
        val w = 58f
        val h = 38f

        drawOval(shadowColor, topLeft = Offset(x - w * 0.5f, y + h * 0.35f), size = Size(w, 14f))

        // Main Barracks Timber Hall
        val bHallX = x - 10f
        val bHallY = y - 4f
        drawRect(woodDark, topLeft = Offset(bHallX - 16f, bHallY - 6f), size = Size(32f, 20f))
        val bRoof = Path().apply {
            moveTo(bHallX, bHallY - 18f)
            lineTo(bHallX + 18f, bHallY - 6f)
            lineTo(bHallX - 18f, bHallY - 6f)
            close()
        }
        drawPath(bRoof, thatchBase)

        // Red War Shields on barracks front
        drawCircle(tribalRed, radius = 3f, center = Offset(bHallX - 8f, bHallY + 2f))
        drawCircle(tribalRed, radius = 3f, center = Offset(bHallX + 8f, bHallY + 2f))

        // Archery Bullseye Target on tripod
        val targetX = x + 16f
        val targetY = y - 4f
        // Tripod legs
        drawLine(woodDark, Offset(targetX, targetY), Offset(targetX - 4f, targetY + 8f), strokeWidth = 1.5f)
        drawLine(woodDark, Offset(targetX, targetY), Offset(targetX + 4f, targetY + 8f), strokeWidth = 1.5f)
        // Bullseye rings
        drawCircle(boneWhite, radius = 6f, center = Offset(targetX, targetY))
        drawCircle(tribalRed, radius = 4f, center = Offset(targetX, targetY))
        drawCircle(Color(0xFFFDE047), radius = 2f, center = Offset(targetX, targetY))

        // Weapon Rack with Flint Spears & Stone Axes
        val rackX = x + 14f
        val rackY = y + 8f
        drawRect(woodMid, topLeft = Offset(rackX - 6f, rackY), size = Size(12f, 4f)) // Rack frame
        for (sp in 0..2) {
            val sx = rackX - 4f + sp * 4f
            drawLine(woodDark, Offset(sx, rackY + 4f), Offset(sx, rackY - 8f), strokeWidth = 1.5f) // Spear shaft
            drawCircle(stoneLight, radius = 1.5f, center = Offset(sx, rackY - 9f)) // Spear tip
        }

        // Sparring Training Dummy
        val dummyX = x - 12f
        val dummyY = y + 10f
        drawRect(woodDark, topLeft = Offset(dummyX - 1f, dummyY - 10f), size = Size(2f, 14f)) // Post
        drawRect(woodLight, topLeft = Offset(dummyX - 4f, dummyY - 8f), size = Size(8f, 3f))  // Cross arm
        drawCircle(Color(0xFFCA8A04), radius = 3f, center = Offset(dummyX, dummyY - 12f)) // Straw head
    }

    /**
     * (K) TRIBAL MARKET BAZAAR (Market)
     * South-Center. 4 colorful stalls, tribal banner totem pole, goods tables, traders.
     */
    fun DrawScope.drawTribalMarket(x: Float, y: Float, time: Float, level: Int) {
        val w = 62f
        val h = 38f

        drawOval(shadowColor, topLeft = Offset(x - w * 0.5f, y + h * 0.35f), size = Size(w, 14f))

        // Centerpiece: Tall Carved Wooden Totem Pole with flying Red Tribal Banner
        drawRect(woodDark, topLeft = Offset(x - 2f, y - 26f), size = Size(4f, 36f))
        // Flying Red Tribal Banner with waving motion
        val wave = sin(time * 3f) * 2f
        val banner = Path().apply {
            moveTo(x + 2f, y - 24f)
            lineTo(x + 16f, y - 24f + wave)
            lineTo(x + 12f, y - 14f + wave)
            lineTo(x + 2f, y - 14f)
            close()
        }
        drawPath(banner, tribalRed)

        // 4 Market Stalls with colorful canopies:
        // 1. Top-Left: Striped Red/White
        drawMarketStall(x - 18f, y - 8f, tribalRed, boneWhite, "pottery")
        // 2. Top-Right: Striped Blue/White
        drawMarketStall(x + 14f, y - 8f, Color(0xFF2563EB), boneWhite, "fish")
        // 3. Bottom-Left: Striped Green/Gold
        drawMarketStall(x - 18f, y + 8f, Color(0xFF16A34A), Color(0xFFFDE047), "fruit")
        // 4. Bottom-Right: Tan/Amber
        drawMarketStall(x + 14f, y + 8f, Color(0xFFB45309), Color(0xFFFDE68A), "tools")
    }

    private fun DrawScope.drawMarketStall(
        sx: Float,
        sy: Float,
        c1: Color,
        c2: Color,
        type: String
    ) {
        // Table
        drawRect(woodDark, topLeft = Offset(sx - 8f, sy - 2f), size = Size(16f, 8f))
        drawRect(woodLight, topLeft = Offset(sx - 9f, sy - 4f), size = Size(18f, 3f))

        // Canopy Awning
        val canopy = Path().apply {
            moveTo(sx - 10f, sy - 12f)
            lineTo(sx, sy - 16f)
            lineTo(sx + 10f, sy - 12f)
            lineTo(sx + 9f, sy - 8f)
            lineTo(sx - 9f, sy - 8f)
            close()
        }
        drawPath(canopy, c1)

        // Display goods
        when (type) {
            "fruit" -> {
                drawCircle(Color(0xFFEF4444), radius = 2f, center = Offset(sx - 3f, sy - 5f))
                drawCircle(Color(0xFFF59E0B), radius = 2f, center = Offset(sx + 3f, sy - 5f))
            }
            "fish" -> {
                drawLine(Color(0xFF38BDF8), Offset(sx - 4f, sy - 5f), Offset(sx + 4f, sy - 5f), strokeWidth = 2f)
            }
            else -> {
                drawCircle(Color(0xFFD97706), radius = 2f, center = Offset(sx, sy - 5f))
            }
        }
    }

    /**
     * (L) TRIBAL TAVERN & ALEHOUSE (Tavern)
     * Lower-East. Circular log lodge, thatched roof, beer mug emblem, barrels, outdoor beer garden.
     */
    fun DrawScope.drawTribalTavern(x: Float, y: Float, time: Float, level: Int) {
        val w = 56f
        val h = 38f

        drawOval(shadowColor, topLeft = Offset(x - w * 0.5f, y + h * 0.35f), size = Size(w, 14f))

        // Circular Log Tavern Lodge
        val tX = x - 6f
        val tY = y - 2f
        drawOval(woodDark, topLeft = Offset(tX - 16f, tY - 8f), size = Size(32f, 22f))
        // High conical straw thatched roof
        val roof = Path().apply {
            moveTo(tX, tY - 24f)
            lineTo(tX + 18f, tY - 6f)
            lineTo(tX - 18f, tY - 6f)
            close()
        }
        drawPath(roof, thatchBase)

        // Carved wooden Beer Mug crest above door
        drawCircle(woodLight, radius = 5f, center = Offset(tX, tY - 10f))
        drawCircle(Color(0xFFFDE047), radius = 3f, center = Offset(tX, tY - 10f)) // Golden ale
        drawCircle(boneWhite, radius = 1.5f, center = Offset(tX, tY - 12f))      // Froth

        // Stacked Wooden Oak Barrels
        val barrelX = tX - 18f
        val barrelY = tY + 4f
        for (b in 0..1) {
            drawOval(woodMid, topLeft = Offset(barrelX + b * 6f, barrelY), size = Size(6f, 8f))
            drawLine(stoneDark, Offset(barrelX + b * 6f, barrelY + 2f), Offset(barrelX + b * 6f + 6f, barrelY + 2f), strokeWidth = 1f)
            drawLine(stoneDark, Offset(barrelX + b * 6f, barrelY + 6f), Offset(barrelX + b * 6f + 6f, barrelY + 6f), strokeWidth = 1f)
        }

        // Outdoor Beer Garden (Picnic tables & benches)
        val tableX = x + 16f
        val tableY = y + 4f
        // Wooden bench
        drawRect(woodMid, topLeft = Offset(tableX - 8f, tableY - 5f), size = Size(16f, 3f))
        // Wooden table
        drawRect(woodLight, topLeft = Offset(tableX - 7f, tableY - 1f), size = Size(14f, 5f))
        // Wooden mugs on table
        drawCircle(woodDark, radius = 1.5f, center = Offset(tableX - 3f, tableY - 2f))
        drawCircle(woodDark, radius = 1.5f, center = Offset(tableX + 3f, tableY - 2f))
    }

    /**
     * (M) DUNGEON MENHIR PORTAL (Dungeon)
     * Bottom-Right. 7 tall menhirs with purple runes, swirling arcane vortex, glowing mist.
     */
    fun DrawScope.drawDungeonPortal(x: Float, y: Float, time: Float) {
        val radius = 18f

        // Shadow & magical outer aura
        drawCircle(Color(0xFFA855F7).copy(alpha = 0.25f), radius = radius * 1.5f, center = Offset(x, y))

        // Ring of 7 Ancient Runic Menhirs
        for (i in 0..6) {
            val angle = i * (2f * PI.toFloat() / 7f)
            val mx = x + cos(angle) * radius
            val my = y + sin(angle) * (radius * 0.75f)

            // Standing megalith
            drawRect(stoneDark, topLeft = Offset(mx - 3f, my - 12f), size = Size(6f, 16f))
            drawRect(stoneGrey, topLeft = Offset(mx - 2f, my - 10f), size = Size(4f, 12f))

            // Glowing Arcane Rune
            val glow = (sin(time * 4f + i) * 0.4f + 0.6f)
            drawCircle(Color(0xFFC084FC).copy(alpha = glow), radius = 1.5f, center = Offset(mx, my - 4f))
        }

        // Swirling Arcane Violet Vortex Portal in center
        val pSpin = time * 3f
        withTransform({
            rotate(pSpin * 40f, Offset(x, y))
        }) {
            drawCircle(Color(0xFF581C87), radius = 10f, center = Offset(x, y))
            drawCircle(Color(0xFF7E22CE), radius = 7.5f, center = Offset(x, y))
            drawCircle(Color(0xFFA855F7), radius = 5f, center = Offset(x, y))
            drawCircle(Color(0xFFE9D5FF), radius = 2.5f, center = Offset(x, y)) // Bright core
        }

        // Floating arcane energy sparks
        val sparkY = y - 10f - ((time * 20f) % 16f)
        val sparkX = x + cos(time * 3f) * 4f
        drawCircle(Color(0xFFE879F9), radius = 1.2f, center = Offset(sparkX, sparkY))
    }

    /**
     * (N) STORAGE WAREHOUSE (Gudang)
     */
    fun DrawScope.drawStorageWarehouse(x: Float, y: Float, time: Float, level: Int) {
        val w = 32f
        val h = 24f

        drawOval(shadowColor, topLeft = Offset(x - w * 0.5f, y + h * 0.35f), size = Size(w, 10f))
        drawRect(woodDark, topLeft = Offset(x - 12f, y - 6f), size = Size(24f, 16f))
        val roof = Path().apply {
            moveTo(x, y - 14f)
            lineTo(x + 14f, y - 6f)
            lineTo(x - 14f, y - 6f)
            close()
        }
        drawPath(roof, thatchBase)

        // Stacked grain sacks outside
        drawOval(Color(0xFFD4B996), topLeft = Offset(x + 8f, y + 2f), size = Size(6f, 4f))
        drawOval(Color(0xFFD4B996), topLeft = Offset(x + 8f, y - 1f), size = Size(5f, 4f))
    }

    /**
     * (O) RESIDENTIAL THATCHED HUTS (Rumah Warga)
     * Cozy round prehistoric cottages with conical straw roofs.
     */
    fun DrawScope.drawThatchedHut(x: Float, y: Float, time: Float, index: Int) {
        val radius = 14f

        drawOval(shadowColor, topLeft = Offset(x - radius * 1.1f, y + radius * 0.5f), size = Size(radius * 2.2f, 8f))

        // Round timber & mud wall
        drawOval(woodDark, topLeft = Offset(x - radius, y - radius * 0.4f), size = Size(radius * 2f, radius * 1.2f))
        drawOval(woodMid, topLeft = Offset(x - radius * 0.85f, y - radius * 0.3f), size = Size(radius * 1.7f, radius * 1.0f))

        // Conical Thatched Straw Roof
        val roof = Path().apply {
            moveTo(x, y - radius * 1.6f) // Peak
            lineTo(x + radius * 1.2f, y - radius * 0.2f)
            lineTo(x - radius * 1.2f, y - radius * 0.2f)
            close()
        }
        drawPath(roof, thatchBase)

        // Roof highlight ridge
        val ridge = Path().apply {
            moveTo(x, y - radius * 1.6f)
            lineTo(x + radius * 0.7f, y - radius * 0.3f)
            lineTo(x - radius * 0.7f, y - radius * 0.3f)
            close()
        }
        drawPath(ridge, thatchHighlight)

        // Small wooden door
        drawRect(Color(0xFF1E1004), topLeft = Offset(x - 3.5f, y), size = Size(7f, 9f))

        // Ambient prop (alternate: clay jar or small bench)
        if (index % 2 == 0) {
            drawCircle(Color(0xFFB45309), radius = 2f, center = Offset(x - 8f, y + 4f)) // Clay jar
        } else {
            drawRect(woodMid, topLeft = Offset(x + 6f, y + 3f), size = Size(5f, 2.5f)) // Stool
        }
    }

    // ==========================================
    // 7. WILD PLOTS (Displayed when building is locked)
    // ==========================================
    fun DrawScope.drawWildMeadowPlot(x: Float, y: Float, w: Float, h: Float, seed: Int) {
        // Natural clover & flower tufts
        drawOval(grassDark.copy(alpha = 0.5f), topLeft = Offset(x - w * 0.45f, y - h * 0.45f), size = Size(w * 0.9f, h * 0.9f))
        // Small rocks
        drawCircle(stoneGrey, radius = 3f, center = Offset(x - w * 0.2f, y + h * 0.1f))
        drawCircle(stoneLight, radius = 1.5f, center = Offset(x - w * 0.2f - 1f, y + h * 0.1f - 1f))
        // Daisy flowers
        drawCircle(Color.White, radius = 2f, center = Offset(x + w * 0.2f, y - h * 0.1f))
        drawCircle(Color(0xFFFDE047), radius = 1f, center = Offset(x + w * 0.2f, y - h * 0.1f))
    }

    fun DrawScope.drawWildForestPlot(x: Float, y: Float, w: Float, h: Float, seed: Int) {
        // Natural pine sapling
        drawOval(shadowColor, topLeft = Offset(x - 6f, y + 4f), size = Size(12f, 5f))
        val pine = Path().apply {
            moveTo(x, y - 14f)
            lineTo(x + 8f, y + 2f)
            lineTo(x - 8f, y + 2f)
            close()
        }
        drawPath(pine, Color(0xFF14532D))
        drawCircle(Color(0xFFEF4444), radius = 2f, center = Offset(x + 10f, y + 2f)) // Wild poppy
    }

    fun DrawScope.drawWildStonePlot(x: Float, y: Float, w: Float, h: Float, seed: Int) {
        // Natural mossy boulder
        drawOval(shadowColor, topLeft = Offset(x - 8f, y + 4f), size = Size(16f, 6f))
        drawCircle(stoneGrey, radius = 6f, center = Offset(x, y))
        drawCircle(stoneLight, radius = 4f, center = Offset(x - 2f, y - 2f))
        drawCircle(Color(0xFF15803D), radius = 2f, center = Offset(x + 2f, y + 2f)) // Moss
    }

    fun DrawScope.drawWildBerryPlot(x: Float, y: Float, w: Float, h: Float, seed: Int) {
        // Natural berry bush
        drawCircle(Color(0xFF15803D), radius = 7f, center = Offset(x, y))
        drawCircle(Color(0xFF22C55E), radius = 5.5f, center = Offset(x - 1f, y - 1f))
        drawCircle(Color(0xFFEF4444), radius = 1.5f, center = Offset(x - 2f, y - 2f))
        drawCircle(Color(0xFFEF4444), radius = 1.5f, center = Offset(x + 2f, y + 1f))
    }

    // ==========================================
    // 8. ROAMING CITIZENS & PATROLLING TROOPS
    // ==========================================
    private fun DrawScope.drawRoamingCitizens(w: Float, h: Float, time: Float, state: GameState) {
        // 1. Adam & Hawa at central plaza
        val adamX = w * 0.50f - 14f + sin(time * 0.8f) * 10f
        val adamY = h * 0.52f + cos(time * 0.8f) * 6f
        drawTribalCharacter(adamX, adamY, isMale = true, skinColor = Color(0xFFD97706), clothColor = Color(0xFF92400E))

        val hawaX = w * 0.50f + 14f - sin(time * 0.7f) * 10f
        val hawaY = h * 0.52f - cos(time * 0.7f) * 5f
        drawTribalCharacter(hawaX, hawaY, isMale = false, skinColor = Color(0xFFFBBF24), clothColor = Color(0xFFB45309))

        // 2. Patrolling Pasukan Kapak Batu (North road)
        if (state.currentPasukan > 0 || state.isUnlocked("barak_militer")) {
            val axeX = w * 0.50f - 10f + sin(time * 1.0f) * 18f
            val axeY = h * 0.30f + cos(time * 0.5f) * 6f
            drawStoneAxeWarrior(axeX, axeY, time)

            // 3. Patrolling Pasukan Tombak (South Avenue towards Gate)
            val spearX = w * 0.50f + 10f - cos(time * 0.9f) * 18f
            val spearY = h * 0.80f + sin(time * 0.5f) * 8f
            drawSpearWarrior(spearX, spearY, time)
        }
    }

    private fun DrawScope.drawTribalCharacter(
        x: Float,
        y: Float,
        isMale: Boolean,
        skinColor: Color,
        clothColor: Color
    ) {
        drawOval(shadowColor, topLeft = Offset(x - 4f, y + 4f), size = Size(8f, 3f))
        // Head
        drawCircle(skinColor, radius = 2.5f, center = Offset(x, y - 6f))
        // Hair
        drawCircle(Color(0xFF1E1004), radius = 2.7f, center = Offset(x, y - 7f))
        // Body / Tunic
        drawRect(clothColor, topLeft = Offset(x - 2.5f, y - 3.5f), size = Size(5f, 6.5f))
        // Legs
        drawLine(skinColor, Offset(x - 1.5f, y + 3f), Offset(x - 1.5f, y + 6f), strokeWidth = 1f)
        drawLine(skinColor, Offset(x + 1.5f, y + 3f), Offset(x + 1.5f, y + 6f), strokeWidth = 1f)
    }

    private fun DrawScope.drawStoneAxeWarrior(x: Float, y: Float, time: Float) {
        drawTribalCharacter(x, y, isMale = true, skinColor = Color(0xFFD97706), clothColor = Color(0xFFB45309))
        // Stone Axe in hand
        val swing = sin(time * 2f) * 3f
        drawLine(woodDark, Offset(x + 3f, y - 2f), Offset(x + 7f, y - 6f + swing), strokeWidth = 1.5f)
        drawRect(stoneGrey, topLeft = Offset(x + 5f, y - 8f + swing), size = Size(4f, 4f))
    }

    private fun DrawScope.drawSpearWarrior(x: Float, y: Float, time: Float) {
        drawTribalCharacter(x, y, isMale = false, skinColor = Color(0xFFFBBF24), clothColor = Color(0xFF047857))
        // Flint Spear in hand
        drawLine(woodDark, Offset(x - 3f, y + 4f), Offset(x - 3f, y - 10f), strokeWidth = 1.5f)
        drawCircle(stoneLight, radius = 1.5f, center = Offset(x - 3f, y - 11f))
    }
}
