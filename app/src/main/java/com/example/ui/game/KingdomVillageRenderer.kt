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
 * KingdomVillageRenderer (Zaman Kerajaan / Medieval Era)
 *
 * Renders the vibrant, high-fidelity 16-bit pixel art medieval kingdom exactly matching
 * the reference image:
 * - Grand blue-roofed Gothic Cathedral at top-center
 * - Majestic Royal Castle with blue turrets, gold fleur-de-lis, lion banners at center
 * - Medieval Lumber Mill with waterwheel & log piles at top-left
 * - Stone Quarry & Masonry with crane & mining shaft at top-right
 * - Golden wheat fields, windmill & crop rows at upper-west
 * - Apple orchard & fenced livestock with red barn at upper-east
 * - Hospital/Infirmary with red-tiled roof & herb garden at mid-west
 * - Castle Military Barracks with lion banners & training knights at lower-west
 * - Cobblestone Grand Market with striped stalls, central fountain & knight statue
 * - Half-timbered Tavern with foaming beer sign & outdoor casks at lower-right
 * - Glowing purple rune portal (Dungeon) surrounded by stone megaliths at bottom-right
 * - Heavy stone battlements with blue-roofed towers, lion crests & wooden gate
 */
object KingdomVillageRenderer {

    // Palettes matching Medieval Kingdom (Image 3)
    val grassDark = Color(0xFF1E722D)
    val grassMid = Color(0xFF2E9A3E)
    val grassLight = Color(0xFF48C55A)
    val grassHighlight = Color(0xFF7DE48B)
    
    val cobbleBase = Color(0xFF94A3B8)
    val cobbleDark = Color(0xFF64748B)
    val cobbleLight = Color(0xFFCBD5E1)
    
    val royalBlueRoof = Color(0xFF1E3A8A)
    val royalBlueMid = Color(0xFF2563EB)
    val royalBlueLight = Color(0xFF60A5FA)
    
    val redTileRoof = Color(0xFFB91C1C)
    val redTileMid = Color(0xFFDC2626)
    val redTileLight = Color(0xFFEF4444)
    
    val plasterWall = Color(0xFFF1F5F9)
    val plasterShadow = Color(0xFFCBD5E1)
    val timberBeam = Color(0xFF451A03)
    val timberBeamMid = Color(0xFF78350F)
    
    val stoneWallBase = Color(0xFF64748B)
    val stoneWallDark = Color(0xFF334155)
    val stoneWallLight = Color(0xFF94A3B8)
    
    val goldAccent = Color(0xFFFBBF24)
    val goldDark = Color(0xFFD97706)
    
    val riverBlue = Color(0xFF2563EB)
    val riverShallow = Color(0xFF60A5FA)
    val riverFoam = Color(0xFFBFDBFE)
    
    val wheatGold = Color(0xFFFCD34D)
    val wheatBrown = Color(0xFFD97706)
    
    val shadowColor = Color(0x40000000)

    fun drawVillage(
        drawScope: DrawScope,
        state: GameState,
        time: Float,
        w: Float,
        h: Float
    ) {
        with(drawScope) {
            fun px(nx: Float): Float = nx * w
            fun py(ny: Float): Float = ny * h

            // 1. BASE MEDIEVAL TERRAIN (Lush Grassland)
            drawBaseMeadow(w, h)

            // 2. WEST RIVER & WOODEN/STONE ARCH BRIDGES
            drawWestRiverAndBridges(w, h, time)

            // 3. SURROUNDING PINE FORESTS & MOUNTAINS
            drawSurroundingKingdomForest(w, h, time)

            // 4. COBBLESTONE ROAD NETWORK
            drawCobblestoneRoads(w, h)

            // 5. MEDIEVAL STONE BATTLEMENT WALLS, TOWERS & SOUTH GATE (Tembok)
            drawKingdomWallsAndGate(w, h, time, state.isUnlocked("tembok"), state.getBuildingLevel("tembok"))

            // 6. BUILDINGS & DISTRICTS (Unlockable one by one)

            // (A) Top-Center: Grand Gothic Cathedral (Temple)
            if (state.isUnlocked("temple")) {
                drawGrandCathedral(px(0.50f), py(0.14f), time, state.getBuildingLevel("temple"))
            } else {
                drawVacantMeadow(px(0.50f), py(0.14f), 0.13f * w, 0.10f * h, seed = 101)
            }

            // (B) Top-Left: Medieval Lumber Mill (Penebang Kayu)
            if (state.isUnlocked("penebang_kayu")) {
                drawMedievalLumberMill(px(0.23f), py(0.15f), time, state.getBuildingLevel("penebang_kayu"))
            } else {
                drawVacantMeadow(px(0.23f), py(0.15f), 0.14f * w, 0.10f * h, seed = 202)
            }

            // (C) Top-Right: Royal Stone Quarry & Masonry (Pemecah Batu)
            if (state.isUnlocked("pemecah_batu")) {
                drawRoyalStoneQuarry(px(0.78f), py(0.15f), time, state.getBuildingLevel("pemecah_batu"))
            } else {
                drawVacantMeadow(px(0.78f), py(0.15f), 0.14f * w, 0.10f * h, seed = 303)
            }

            // (D) Upper-West: Wheat Fields & Windmill (Pertanian)
            if (state.isUnlocked("pertanian")) {
                drawWheatFieldsAndWindmill(px(0.18f), py(0.31f), time, state.getBuildingLevel("pertanian"))
            } else {
                drawVacantMeadow(px(0.18f), py(0.31f), 0.14f * w, 0.11f * h, seed = 404)
            }

            // (E) Center: Royal Citadel Castle (Town Hall)
            if (state.isUnlocked("town_hall")) {
                drawRoyalCitadelCastle(px(0.50f), py(0.38f), time, state.getBuildingLevel("town_hall"))
            } else {
                drawVacantMeadow(px(0.50f), py(0.38f), 0.18f * w, 0.14f * h, seed = 505)
            }

            // (F) Upper-East: Apple Orchard & Livestock Pasture (Perkebunan & Peternakan)
            if (state.isUnlocked("perkebunan")) {
                drawAppleOrchard(px(0.82f), py(0.31f), time, state.getBuildingLevel("perkebunan"))
            } else {
                drawVacantMeadow(px(0.82f), py(0.31f), 0.14f * w, 0.10f * h, seed = 606)
            }
            if (state.isUnlocked("peternakan")) {
                drawPastureAndBarn(px(0.82f), py(0.48f), time, state.getBuildingLevel("peternakan"))
            } else {
                drawVacantMeadow(px(0.82f), py(0.48f), 0.14f * w, 0.10f * h, seed = 707)
            }

            // (G) Mid-West: Infirmary / Hospital (Hospital)
            if (state.isUnlocked("hospital")) {
                drawMedievalInfirmary(px(0.18f), py(0.48f), time, state.getBuildingLevel("hospital"))
            } else {
                drawVacantMeadow(px(0.18f), py(0.48f), 0.14f * w, 0.10f * h, seed = 808)
            }

            // (H) Lower-West: Castle Military Garrison (Barak Militer)
            if (state.isUnlocked("barak_militer")) {
                drawKnightsBarracks(px(0.20f), py(0.66f), time, state.getBuildingLevel("barak_militer"))
            } else {
                drawVacantMeadow(px(0.20f), py(0.66f), 0.14f * w, 0.11f * h, seed = 909)
            }

            // (I) Lower-Center: Cobblestone Grand Market & Plaza
            if (state.isUnlocked("market") || state.isUnlocked("barter_purba")) {
                drawMedievalMarketSquare(px(0.50f), py(0.66f), time, state.getBuildingLevel("market"))
            } else {
                drawVacantMeadow(px(0.50f), py(0.66f), 0.18f * w, 0.12f * h, seed = 1010)
            }

            // Plaza Statue / Fountain (Between Castle & Market)
            drawKnightStatueAndFountain(px(0.50f), py(0.52f), time)

            // (J) Lower-Right: Medieval Tavern (Tavern)
            if (state.isUnlocked("tavern")) {
                drawMedievalTavern(px(0.82f), py(0.64f), time, state.getBuildingLevel("tavern"))
            } else {
                drawVacantMeadow(px(0.82f), py(0.64f), 0.14f * w, 0.10f * h, seed = 1111)
            }

            // (K) Bottom-Right: Ancient Rune Portal (Dungeon)
            if (state.isUnlocked("dungeon")) {
                drawRuneDungeonPortal(px(0.80f), py(0.76f), time, state.maxDungeonLevel)
            } else {
                drawVacantMeadow(px(0.80f), py(0.76f), 0.13f * w, 0.10f * h, seed = 1212)
            }

            // (L) North of Center: Royal Granary / Storehouse (Gudang)
            if (state.isUnlocked("gudang")) {
                drawRoyalStorehouse(px(0.50f), py(0.26f), time, state.getBuildingLevel("gudang"))
            }

            // (M) Residential Houses (Rumah Warga) around the castle
            drawMedievalHouses(w, h, state)

            // (N) Patrolling Knights & Wandering Villagers
            drawKingdomPatrols(w, h, time, state)
        }
    }

    // 1. BASE MEADOW
    private fun DrawScope.drawBaseMeadow(w: Float, h: Float) {
        drawRect(grassMid, Offset.Zero, Size(w, h))
        val dotSize = 8f
        val cols = (w / dotSize).toInt()
        val rows = (h / dotSize).toInt()
        for (y in 0..rows) {
            for (x in 0..cols) {
                val r = (x * 37 + y * 19) % 100
                if (r < 15) {
                    drawRect(grassDark, Offset(x * dotSize, y * dotSize), Size(dotSize, dotSize))
                } else if (r > 82) {
                    drawRect(grassLight, Offset(x * dotSize, y * dotSize), Size(dotSize, dotSize))
                } else if (r == 50) {
                    drawCircle(Color(0xFFFEF08A), 1.5f, Offset(x * dotSize + 4f, y * dotSize + 4f)) // Buttercup
                } else if (r == 72) {
                    drawCircle(Color(0xFFF43F5E), 1.5f, Offset(x * dotSize + 4f, y * dotSize + 4f)) // Wild rose
                }
            }
        }
    }

    // 2. WEST RIVER & BRIDGES
    private fun DrawScope.drawWestRiverAndBridges(w: Float, h: Float, time: Float) {
        val riverPath = Path().apply {
            moveTo(0f, 0f)
            lineTo(w * 0.11f, 0f)
            cubicTo(w * 0.14f, h * 0.35f, w * 0.05f, h * 0.65f, w * 0.09f, h)
            lineTo(0f, h)
            close()
        }
        drawPath(riverPath, riverBlue)

        // River highlight wavelets
        for (i in 0 until 18) {
            val y = (h * (i * 0.055f) + sin(time * 0.8f + i) * 6f) % h
            val x = w * (0.02f + 0.05f * sin(i * 1.3f))
            drawLine(riverShallow, Offset(x, y), Offset(x + 16f, y), 2.5f)
        }

        // Stone Arch Bridge on River (upper-west)
        val bridgeY = h * 0.10f
        drawRect(cobbleDark, Offset(0f, bridgeY - 7f), Size(w * 0.11f, 20f))
        drawRect(cobbleBase, Offset(0f, bridgeY - 5f), Size(w * 0.11f, 16f))
        // Bridge parapet
        drawLine(stoneWallDark, Offset(0f, bridgeY - 7f), Offset(w * 0.11f, bridgeY - 7f), 3f)
        drawLine(stoneWallDark, Offset(0f, bridgeY + 13f), Offset(w * 0.11f, bridgeY + 13f), 3f)
    }

    // 3. SURROUNDING PINE FORESTS
    private fun DrawScope.drawSurroundingKingdomForest(w: Float, h: Float, time: Float) {
        // Outer border dense pines
        fun drawPine(x: Float, y: Float, scale: Float = 1f) {
            drawOval(shadowColor, Offset(x - 9f * scale, y + 10f * scale), Size(18f * scale, 7f * scale))
            drawRect(timberBeam, Offset(x - 2f * scale, y + 2f * scale), Size(4f * scale, 10f * scale))
            // Tier 3
            val p3 = Path().apply {
                moveTo(x, y - 18f * scale)
                lineTo(x - 12f * scale, y + 2f * scale)
                lineTo(x + 12f * scale, y + 2f * scale)
                close()
            }
            drawPath(p3, Color(0xFF14532D))
            // Tier 2
            val p2 = Path().apply {
                moveTo(x, y - 24f * scale)
                lineTo(x - 9f * scale, y - 7f * scale)
                lineTo(x + 9f * scale, y - 7f * scale)
                close()
            }
            drawPath(p2, Color(0xFF166534))
            // Tier 1
            val p1 = Path().apply {
                moveTo(x, y - 30f * scale)
                lineTo(x - 6f * scale, y - 16f * scale)
                lineTo(x + 6f * scale, y - 16f * scale)
                close()
            }
            drawPath(p1, Color(0xFF22C55E))
        }

        // Top forest edge
        for (i in 0..12) {
            val px = w * (0.12f + i * 0.07f)
            drawPine(px, 12f, 0.8f)
        }
        // Right mountain & forest edge
        for (i in 0..10) {
            val py = h * (0.08f + i * 0.09f)
            drawPine(w * 0.95f, py, 0.85f)
        }
        // Bottom forest edge
        for (i in 0..5) {
            drawPine(w * (0.05f + i * 0.07f), h * 0.96f, 0.85f)
            drawPine(w * (0.65f + i * 0.06f), h * 0.96f, 0.85f)
        }
    }

    // 4. COBBLESTONE ROADS
    private fun DrawScope.drawCobblestoneRoads(w: Float, h: Float) {
        val cx = w * 0.50f
        val cy = h * 0.52f

        // Central Plaza Circle
        drawCircle(cobbleBase, radius = 0.12f * w, center = Offset(cx, cy))
        drawCircle(cobbleDark, radius = 0.12f * w, center = Offset(cx, cy), style = Stroke(3f))

        // Main North-South Highway
        drawRect(cobbleBase, Offset(cx - 16f, 0.06f * h), Size(32f, 0.88f * h))
        drawRect(cobbleDark, Offset(cx - 16f, 0.06f * h), Size(32f, 0.88f * h), style = Stroke(2f))

        // East-West Crossways
        drawRect(cobbleBase, Offset(0.12f * w, h * 0.40f), Size(0.76f * w, 24f))
        drawRect(cobbleBase, Offset(0.14f * w, h * 0.58f), Size(0.72f * w, 22f))
        drawRect(cobbleBase, Offset(0.12f * w, h * 0.22f), Size(0.76f * w, 22f))

        // Cobblestone textured specks
        for (i in 0..60) {
            val rx = cx - 12f + (i * 17) % 24
            val ry = 0.08f * h + (i * 23) % (0.84f * h)
            drawRect(cobbleLight, Offset(rx, ry), Size(3f, 3f))
        }
    }

    // 5. MEDIEVAL STONE WALLS & GATE
    private fun DrawScope.drawKingdomWallsAndGate(w: Float, h: Float, time: Float, isUnlocked: Boolean, level: Int) {
        val wallColor = if (level >= 3) stoneWallLight else stoneWallBase
        val strokeW = 10f

        // Outer rectangular boundary
        val left = w * 0.11f
        val right = w * 0.89f
        val top = h * 0.06f
        val bottom = h * 0.88f
        val gateW = 44f
        val cx = w * 0.50f

        // North wall
        drawLine(wallColor, Offset(left, top), Offset(right, top), strokeW)
        // West wall
        drawLine(wallColor, Offset(left, top), Offset(left, bottom), strokeW)
        // East wall
        drawLine(wallColor, Offset(right, top), Offset(right, bottom), strokeW)
        // South wall (with gate opening)
        drawLine(wallColor, Offset(left, bottom), Offset(cx - gateW / 2, bottom), strokeW)
        drawLine(wallColor, Offset(cx + gateW / 2, bottom), Offset(right, bottom), strokeW)

        // Corner Towers
        fun drawCornerTower(tx: Float, ty: Float) {
            drawRect(Color(0xFF334155), Offset(tx - 14f, ty - 14f), Size(28f, 28f))
            drawRect(wallColor, Offset(tx - 12f, ty - 12f), Size(24f, 24f))
            // Blue conical roof
            val roofPath = Path().apply {
                moveTo(tx, ty - 28f)
                lineTo(tx - 15f, ty - 12f)
                lineTo(tx + 15f, ty - 12f)
                close()
            }
            drawPath(roofPath, royalBlueRoof)
            // Golden finial
            drawCircle(goldAccent, 2.5f, Offset(tx, ty - 29f))
            // Lion heraldic banner
            drawRect(royalBlueMid, Offset(tx - 4f, ty - 8f), Size(8f, 14f))
            drawCircle(goldAccent, 2f, Offset(tx, ty - 1f))
        }

        drawCornerTower(left, top)
        drawCornerTower(right, top)
        drawCornerTower(left, bottom)
        drawCornerTower(right, bottom)

        // South Gate Towers & Wooden Portcullis
        val gx = cx
        val gy = bottom
        // Twin Gate Towers
        drawRect(wallColor, Offset(gx - gateW / 2 - 14f, gy - 20f), Size(16f, 32f))
        drawRect(wallColor, Offset(gx + gateW / 2 - 2f, gy - 20f), Size(16f, 32f))
        // Tower Blue Pyramidal Roofs
        val leftRoof = Path().apply {
            moveTo(gx - gateW / 2 - 6f, gy - 34f)
            lineTo(gx - gateW / 2 - 16f, gy - 20f)
            lineTo(gx - gateW / 2 + 4f, gy - 20f)
            close()
        }
        drawPath(leftRoof, royalBlueRoof)
        val rightRoof = Path().apply {
            moveTo(gx + gateW / 2 + 6f, gy - 34f)
            lineTo(gx + gateW / 2 - 4f, gy - 20f)
            lineTo(gx + gateW / 2 + 16f, gy - 20f)
            close()
        }
        drawPath(rightRoof, royalBlueRoof)

        // Oak Double Gate & Iron Grating
        drawRect(Color(0xFF78350F), Offset(gx - gateW / 2 + 2f, gy - 12f), Size(gateW - 4f, 20f))
        for (i in 1..4) {
            drawLine(timberBeam, Offset(gx - gateW / 2 + i * 8f, gy - 12f), Offset(gx - gateW / 2 + i * 8f, gy + 8f), 2f)
        }
        // Wooden Drawbridge extending South
        drawRect(Color(0xFF92400E), Offset(gx - 16f, gy + 8f), Size(32f, 26f))
        drawLine(Color(0xFF451A03), Offset(gx - 14f, gy + 8f), Offset(gx - 14f, gy + 34f), 2f)
        drawLine(Color(0xFF451A03), Offset(gx + 14f, gy + 8f), Offset(gx + 14f, gy + 34f), 2f)
    }

    // (A) GRAND GOTHIC CATHEDRAL (Temple)
    private fun DrawScope.drawGrandCathedral(x: Float, y: Float, time: Float, level: Int) {
        drawOval(shadowColor, Offset(x - 36f, y + 26f), Size(72f, 16f))
        // Stone Main Nave
        drawRect(plasterWall, Offset(x - 28f, y - 10f), Size(56f, 36f))
        drawRect(plasterShadow, Offset(x - 28f, y + 16f), Size(56f, 10f))

        // High Central Gable Roof
        val centralRoof = Path().apply {
            moveTo(x, y - 48f)
            lineTo(x - 32f, y - 10f)
            lineTo(x + 32f, y - 10f)
            close()
        }
        drawPath(centralRoof, royalBlueRoof)

        // Soaring Center Spire with Golden Cross
        val spire = Path().apply {
            moveTo(x, y - 62f)
            lineTo(x - 8f, y - 42f)
            lineTo(x + 8f, y - 42f)
            close()
        }
        drawPath(spire, royalBlueMid)
        // Golden Cross at the apex
        drawLine(goldAccent, Offset(x, y - 68f), Offset(x, y - 60f), 2.5f)
        drawLine(goldAccent, Offset(x - 4f, y - 65f), Offset(x + 4f, y - 65f), 2f)

        // Twin Flanking Spires
        val leftSpire = Path().apply {
            moveTo(x - 24f, y - 42f)
            lineTo(x - 32f, y - 16f)
            lineTo(x - 16f, y - 16f)
            close()
        }
        drawPath(leftSpire, royalBlueRoof)
        val rightSpire = Path().apply {
            moveTo(x + 24f, y - 42f)
            lineTo(x + 16f, y - 16f)
            lineTo(x + 32f, y - 16f)
            close()
        }
        drawPath(rightSpire, royalBlueRoof)

        // Rose Window (Stained Glass)
        drawCircle(stoneWallDark, 9f, Offset(x, y - 22f))
        drawCircle(Color(0xFF38BDF8), 7f, Offset(x, y - 22f))
        drawCircle(Color(0xFFE0E7FF), 3f, Offset(x, y - 22f))

        // Arched Grand Portal Door
        drawRect(timberBeam, Offset(x - 8f, y + 4f), Size(16f, 22f))
        val doorArch = Path().apply {
            moveTo(x - 8f, y + 4f)
            quadraticTo(x, y - 2f, x + 8f, y + 4f)
            close()
        }
        drawPath(doorArch, timberBeam)

        // Holy Light Glow & Torches
        val glow = (sin(time * 2f) + 1f) * 0.15f
        drawCircle(Color(0xFFFDE047).copy(alpha = 0.25f + glow), 18f, Offset(x, y + 10f))
    }

    // (B) MEDIEVAL LUMBER MILL (Penebang Kayu)
    private fun DrawScope.drawMedievalLumberMill(x: Float, y: Float, time: Float, level: Int) {
        drawOval(shadowColor, Offset(x - 32f, y + 18f), Size(64f, 14f))
        // Carpenter Workshop Shed
        drawRect(Color(0xFF78350F), Offset(x - 22f, y - 8f), Size(44f, 26f))
        // Timber beams
        for (i in 0..4) {
            drawLine(timberBeam, Offset(x - 22f + i * 11f, y - 8f), Offset(x - 22f + i * 11f, y + 18f), 2.5f)
        }
        // Clay-tiled gable roof
        val roof = Path().apply {
            moveTo(x, y - 24f)
            lineTo(x - 26f, y - 8f)
            lineTo(x + 26f, y - 8f)
            close()
        }
        drawPath(roof, redTileRoof)

        // Piles of Pine Logs
        for (row in 0..2) {
            for (col in 0..(3 - row)) {
                val lx = x - 26f + col * 9f + row * 4f
                val ly = y + 16f - row * 6f
                drawOval(Color(0xFF92400E), Offset(lx, ly), Size(12f, 6f))
                drawCircle(Color(0xFFFDE68A), 2.5f, Offset(lx + 2f, ly + 3f))
            }
        }

        // Sawhorse with Log
        drawLine(timberBeam, Offset(x + 12f, y + 12f), Offset(x + 22f, y + 22f), 2f)
        drawLine(timberBeam, Offset(x + 22f, y + 12f), Offset(x + 12f, y + 22f), 2f)
        drawRect(Color(0xFFB45309), Offset(x + 10f, y + 14f), Size(18f, 4f))
    }

    // (C) ROYAL STONE QUARRY (Pemecah Batu)
    private fun DrawScope.drawRoyalStoneQuarry(x: Float, y: Float, time: Float, level: Int) {
        drawOval(shadowColor, Offset(x - 30f, y + 20f), Size(60f, 14f))
        // Mountain Rocky Backwall
        val rockPath = Path().apply {
            moveTo(x - 28f, y + 12f)
            lineTo(x - 24f, y - 20f)
            lineTo(x - 10f, y - 26f)
            lineTo(x + 14f, y - 22f)
            lineTo(x + 28f, y + 12f)
            close()
        }
        drawPath(rockPath, stoneWallDark)

        // Dark Mine Shaft Entrance with timber frame
        drawRect(timberBeam, Offset(x - 12f, y - 8f), Size(24f, 20f))
        drawRect(Color(0xFF0F172A), Offset(x - 9f, y - 5f), Size(18f, 17f))

        // Wooden Crane with Pulley & Stone Block
        val craneBaseX = x + 16f
        val craneBaseY = y + 6f
        drawLine(timberBeam, Offset(craneBaseX, craneBaseY), Offset(craneBaseX, craneBaseY - 32f), 3.5f)
        drawLine(timberBeam, Offset(craneBaseX, craneBaseY - 32f), Offset(craneBaseX - 18f, craneBaseY - 26f), 3f)
        // Rope & Suspended Stone
        val ropeSway = sin(time * 1.5f) * 2f
        drawLine(Color(0xFFFDE68A), Offset(craneBaseX - 18f, craneBaseY - 26f), Offset(craneBaseX - 18f + ropeSway, craneBaseY - 10f), 1.5f)
        drawRect(stoneWallLight, Offset(craneBaseX - 22f + ropeSway, craneBaseY - 10f), Size(8f, 8f))

        // Stacked Ashlar Stone Blocks on Ground
        for (i in 0..4) {
            val bx = x - 26f + (i % 3) * 9f
            val by = y + 12f + (i / 3) * 7f
            drawRect(stoneWallLight, Offset(bx, by), Size(8f, 6f))
            drawRect(stoneWallDark, Offset(bx, by), Size(8f, 6f), style = Stroke(1f))
        }
    }

    // (D) WHEAT FIELDS & WINDMILL (Pertanian)
    private fun DrawScope.drawWheatFieldsAndWindmill(x: Float, y: Float, time: Float, level: Int) {
        drawOval(shadowColor, Offset(x - 30f, y + 20f), Size(60f, 12f))
        // Golden Wheat Rows
        for (row in 0..3) {
            val ry = y - 10f + row * 8f
            drawRect(wheatBrown, Offset(x - 30f, ry), Size(36f, 6f))
            for (wCol in 0..6) {
                val wx = x - 28f + wCol * 5f
                drawLine(wheatGold, Offset(wx, ry + 6f), Offset(wx, ry - 1f), 2f)
            }
        }

        // Windmill Tower (Right side of plot)
        val wx = x + 14f
        val wy = y + 4f
        val millTower = Path().apply {
            moveTo(wx - 8f, wy + 16f)
            lineTo(wx - 5f, wy - 14f)
            lineTo(wx + 5f, wy - 14f)
            lineTo(wx + 8f, wy + 16f)
            close()
        }
        drawPath(millTower, plasterWall)
        // Conical roof
        val millRoof = Path().apply {
            moveTo(wx, wy - 24f)
            lineTo(wx - 7f, wy - 14f)
            lineTo(wx + 7f, wy - 14f)
            close()
        }
        drawPath(millRoof, royalBlueRoof)

        // Spinning Windmill Blades
        val angle = (time * 1.8f) % (2 * PI.toFloat())
        val bladeLen = 16f
        for (b in 0..3) {
            val a = angle + b * (PI.toFloat() / 2f)
            val bx = wx + cos(a) * bladeLen
            val by = wy - 14f + sin(a) * bladeLen
            drawLine(timberBeam, Offset(wx, wy - 14f), Offset(bx, by), 2f)
            // Canvas blade cloth
            val nx = -sin(a) * 3f
            val ny = cos(a) * 3f
            drawLine(Color(0xFFF8FAFC), Offset(wx + nx, wy - 14f + ny), Offset(bx + nx, by + ny), 1.5f)
        }
        drawCircle(goldAccent, 2.5f, Offset(wx, wy - 14f))
    }

    // (E) ROYAL CITADEL CASTLE (Town Hall / Balai Kota Kerajaan)
    private fun DrawScope.drawRoyalCitadelCastle(x: Float, y: Float, time: Float, level: Int) {
        drawOval(shadowColor, Offset(x - 44f, y + 30f), Size(88f, 20f))

        // Main Castle Keep (Heavy Ashlar Stone)
        drawRect(stoneWallBase, Offset(x - 32f, y - 8f), Size(64f, 40f))
        // Stone brick textures
        for (row in 0..3) {
            for (col in 0..4) {
                val bx = x - 30f + col * 12f + (row % 2) * 6f
                val by = y - 4f + row * 9f
                drawRect(stoneWallLight, Offset(bx, by), Size(10f, 7f), style = Stroke(1f))
            }
        }

        // Crenellated Battlements across the top
        for (i in 0..5) {
            drawRect(stoneWallDark, Offset(x - 32f + i * 11f, y - 15f), Size(7f, 7f))
        }

        // Four Corner Turrets (Towers)
        fun drawCastleTurret(tx: Float, ty: Float) {
            drawRect(stoneWallDark, Offset(tx - 7f, ty - 22f), Size(14f, 48f))
            // Conical royal blue roof
            val troof = Path().apply {
                moveTo(tx, ty - 40f)
                lineTo(tx - 9f, ty - 22f)
                lineTo(tx + 9f, ty - 22f)
                close()
            }
            drawPath(troof, royalBlueRoof)
            // Golden finial & flag
            drawCircle(goldAccent, 2f, Offset(tx, ty - 41f))
            val flagWave = sin(time * 2f + tx) * 2f
            val flag = Path().apply {
                moveTo(tx, ty - 41f)
                lineTo(tx + 8f + flagWave, ty - 44f)
                lineTo(tx + 8f + flagWave, ty - 38f)
                close()
            }
            drawPath(flag, redTileRoof)
        }

        drawCastleTurret(x - 32f, y + 2f)
        drawCastleTurret(x + 32f, y + 2f)
        drawCastleTurret(x - 14f, y - 8f)
        drawCastleTurret(x + 14f, y - 8f)

        // Center High Keep Tower & Blue Roof
        val highRoof = Path().apply {
            moveTo(x, y - 36f)
            lineTo(x - 14f, y - 18f)
            lineTo(x + 14f, y - 18f)
            close()
        }
        drawPath(highRoof, royalBlueRoof)

        // Grand Stone Stairs & Arched Main Gate
        drawRect(stoneWallLight, Offset(x - 16f, y + 16f), Size(32f, 16f))
        drawRect(timberBeam, Offset(x - 10f, y + 10f), Size(20f, 22f))
        val doorArch = Path().apply {
            moveTo(x - 10f, y + 10f)
            quadraticTo(x, y + 2f, x + 10f, y + 10f)
            close()
        }
        drawPath(doorArch, timberBeam)

        // Royal Lion Crest Banner Hanging on Front
        drawRect(redTileRoof, Offset(x - 6f, y - 2f), Size(12f, 18f))
        drawCircle(goldAccent, 3f, Offset(x, y + 5f)) // Rampant lion emblem
        // Torches beside entrance
        val torchFlicker = sin(time * 4f) * 1.5f
        drawCircle(Color(0xFFF97316), 3f + torchFlicker, Offset(x - 13f, y + 12f))
        drawCircle(Color(0xFFF97316), 3f - torchFlicker, Offset(x + 13f, y + 12f))
    }

    // (F) APPLE ORCHARD & LIVESTOCK PASTURE
    private fun DrawScope.drawAppleOrchard(x: Float, y: Float, time: Float, level: Int) {
        drawOval(shadowColor, Offset(x - 28f, y + 18f), Size(56f, 12f))
        // Apple Trees with red apples
        for (i in 0..2) {
            val tx = x - 18f + i * 18f
            val ty = y + (i % 2) * 6f
            // Tree trunk
            drawRect(timberBeam, Offset(tx - 2f, ty + 2f), Size(4f, 10f))
            // Foliage
            drawCircle(Color(0xFF15803D), 12f, Offset(tx, ty - 4f))
            drawCircle(Color(0xFF22C55E), 10f, Offset(tx, ty - 6f))
            // Red Apples
            drawCircle(Color(0xFFDC2626), 2f, Offset(tx - 4f, ty - 6f))
            drawCircle(Color(0xFFDC2626), 2f, Offset(tx + 4f, ty - 4f))
            drawCircle(Color(0xFFDC2626), 2f, Offset(tx, ty - 10f))
            drawCircle(Color(0xFFDC2626), 2f, Offset(tx + 2f, ty))
        }
    }

    private fun DrawScope.drawPastureAndBarn(x: Float, y: Float, time: Float, level: Int) {
        drawOval(shadowColor, Offset(x - 30f, y + 20f), Size(60f, 14f))
        // Wooden Post Fence
        drawRect(Color(0xFF92400E), Offset(x - 26f, y - 6f), Size(52f, 26f), style = Stroke(2f))
        for (i in 0..5) {
            val fx = x - 26f + i * 10f
            drawLine(timberBeam, Offset(fx, y - 8f), Offset(fx, y + 22f), 2f)
        }

        // Red Barn
        drawRect(redTileRoof, Offset(x + 6f, y - 14f), Size(20f, 18f))
        val barnRoof = Path().apply {
            moveTo(x + 16f, y - 24f)
            lineTo(x + 4f, y - 14f)
            lineTo(x + 28f, y - 14f)
            close()
        }
        drawPath(barnRoof, Color(0xFF78350F))

        // Grazing Cow (Black & White Holstein)
        val cowBob = sin(time * 0.8f) * 1.5f
        drawOval(Color.White, Offset(x - 20f, y + 4f + cowBob), Size(14f, 8f))
        drawCircle(Color.Black, 3f, Offset(x - 17f, y + 6f + cowBob))
        drawCircle(Color.Black, 2.5f, Offset(x - 13f, y + 8f + cowBob))
        drawCircle(Color.White, 3f, Offset(x - 22f, y + 5f + cowBob)) // Head

        // Grazing Fluffy Sheep
        val sheepBob = sin(time * 1.1f + 1f) * 1.2f
        drawCircle(Color(0xFFF8FAFC), 4.5f, Offset(x - 6f, y + 10f + sheepBob))
        drawCircle(Color(0xFF475569), 2f, Offset(x - 9f, y + 10f + sheepBob)) // Head
    }

    // (G) MEDIEVAL INFIRMARY / HOSPITAL
    private fun DrawScope.drawMedievalInfirmary(x: Float, y: Float, time: Float, level: Int) {
        drawOval(shadowColor, Offset(x - 26f, y + 18f), Size(52f, 12f))
        // White Plaster Building with Red-Tiled Roof
        drawRect(plasterWall, Offset(x - 20f, y - 6f), Size(40f, 24f))
        val roof = Path().apply {
            moveTo(x, y - 22f)
            lineTo(x - 24f, y - 6f)
            lineTo(x + 24f, y - 6f)
            close()
        }
        drawPath(roof, redTileRoof)

        // Red Cross Symbol on Wall
        drawRect(redTileMid, Offset(x - 2f, y), Size(4f, 10f))
        drawRect(redTileMid, Offset(x - 5f, y + 3f), Size(10f, 4f))

        // Herb & Lavender Garden in Front
        drawRect(Color(0xFF475569), Offset(x - 18f, y + 16f), Size(36f, 6f))
        for (i in 0..4) {
            drawCircle(Color(0xFFA855F7), 2f, Offset(x - 14f + i * 7f, y + 18f)) // Lavender
        }
    }

    // (H) CASTLE MILITARY GARRISON (Barak Militer)
    private fun DrawScope.drawKnightsBarracks(x: Float, y: Float, time: Float, level: Int) {
        drawOval(shadowColor, Offset(x - 30f, y + 20f), Size(60f, 14f))
        // Heavy Stone Barracks
        drawRect(stoneWallBase, Offset(x - 24f, y - 10f), Size(48f, 26f))
        val roof = Path().apply {
            moveTo(x, y - 24f)
            lineTo(x - 28f, y - 10f)
            lineTo(x + 28f, y - 10f)
            close()
        }
        drawPath(roof, redTileRoof)

        // Crossed Swords Red Flag
        drawRect(redTileRoof, Offset(x - 22f, y - 26f), Size(10f, 8f))
        drawLine(Color.White, Offset(x - 20f, y - 24f), Offset(x - 14f, y - 20f), 1.5f)
        drawLine(Color.White, Offset(x - 14f, y - 24f), Offset(x - 20f, y - 20f), 1.5f)

        // Training Dummy (Straw Target)
        val dummyX = x - 14f
        val dummyY = y + 16f
        drawRect(timberBeam, Offset(dummyX - 1f, dummyY), Size(2f, 10f))
        drawCircle(Color(0xFFFDE68A), 3.5f, Offset(dummyX, dummyY + 2f)) // Straw torso
        drawLine(timberBeam, Offset(dummyX - 5f, dummyY + 4f), Offset(dummyX + 5f, dummyY + 4f), 1.5f)

        // Weapons Rack (Swords & Shields)
        drawRect(Color(0xFF78350F), Offset(x + 10f, dummyY), Size(12f, 8f))
        drawLine(Color(0xFFCBD5E1), Offset(x + 12f, dummyY - 2f), Offset(x + 12f, dummyY + 8f), 2f)
        drawLine(Color(0xFFCBD5E1), Offset(x + 16f, dummyY - 2f), Offset(x + 16f, dummyY + 8f), 2f)
        drawCircle(royalBlueRoof, 3f, Offset(x + 20f, dummyY + 4f)) // Lion shield
    }

    // (I) COBBLESTONE GRAND MARKET (Market)
    private fun DrawScope.drawMedievalMarketSquare(x: Float, y: Float, time: Float, level: Int) {
        // Merchant Stalls with Striped Cloth Canopies
        fun drawStall(sx: Float, sy: Float, color1: Color, color2: Color) {
            drawOval(shadowColor, Offset(sx - 12f, sy + 10f), Size(24f, 6f))
            // Wooden counter table
            drawRect(Color(0xFF92400E), Offset(sx - 10f, sy + 2f), Size(20f, 8f))
            // Striped awning
            for (i in 0..3) {
                val c = if (i % 2 == 0) color1 else color2
                drawRect(c, Offset(sx - 10f + i * 5f, sy - 8f), Size(5f, 10f))
            }
        }

        drawStall(x - 24f, y - 6f, redTileMid, Color.White)
        drawStall(x + 24f, y - 6f, royalBlueMid, Color.White)
        drawStall(x - 20f, y + 14f, Color(0xFFF59E0B), redTileMid)
        drawStall(x + 20f, y + 14f, Color(0xFF10B981), Color.White)
    }

    // Central Statue & Fountain (Between Castle & Market)
    private fun DrawScope.drawKnightStatueAndFountain(x: Float, y: Float, time: Float) {
        // Tiered Fountain with flowing blue water
        drawCircle(cobbleDark, 12f, Offset(x, y))
        drawCircle(riverShallow, 10f, Offset(x, y))
        // Central stone pedestal
        drawRect(stoneWallLight, Offset(x - 4f, y - 8f), Size(8f, 8f))

        // Knight Statue atop Pedestal
        drawRect(stoneWallLight, Offset(x - 3f, y - 18f), Size(6f, 10f)) // Armor
        drawCircle(stoneWallLight, 2.5f, Offset(x, y - 20f)) // Helmet
        drawLine(goldAccent, Offset(x + 3f, y - 22f), Offset(x + 3f, y - 10f), 1.5f) // Sword
        drawRect(redTileMid, Offset(x - 5f, y - 16f), Size(3f, 6f)) // Shield
    }

    // (J) MEDIEVAL TAVERN (Kedai Minum)
    private fun DrawScope.drawMedievalTavern(x: Float, y: Float, time: Float, level: Int) {
        drawOval(shadowColor, Offset(x - 26f, y + 18f), Size(52f, 14f))
        // Half-timbered Two-Story Building
        drawRect(plasterWall, Offset(x - 20f, y - 8f), Size(40f, 26f))
        // Timber framing beams
        for (i in 0..3) {
            drawLine(timberBeam, Offset(x - 20f + i * 13f, y - 8f), Offset(x - 20f + i * 13f, y + 18f), 2f)
        }
        drawLine(timberBeam, Offset(x - 20f, y + 5f), Offset(x + 20f, y + 5f), 2f)
        // Red Shingle Roof
        val troof = Path().apply {
            moveTo(x, y - 24f)
            lineTo(x - 24f, y - 8f)
            lineTo(x + 24f, y - 8f)
            close()
        }
        drawPath(troof, Color(0xFF991B1B))

        // Hanging Wooden Beer Mug Sign
        drawRect(goldAccent, Offset(x - 24f, y + 2f), Size(6f, 6f))
        drawLine(timberBeam, Offset(x - 26f, y - 2f), Offset(x - 20f, y - 2f), 2f)

        // Outdoor Oak Beer Casks & Benches
        drawOval(Color(0xFF78350F), Offset(x + 14f, y + 12f), Size(8f, 6f))
        drawOval(Color(0xFF78350F), Offset(x + 20f, y + 14f), Size(8f, 6f))
    }

    // (K) ANCIENT RUNE PORTAL (Dungeon)
    private fun DrawScope.drawRuneDungeonPortal(x: Float, y: Float, time: Float, maxLevel: Int) {
        drawOval(shadowColor, Offset(x - 28f, y + 18f), Size(56f, 12f))
        // Megalithic Standing Stones
        drawRect(stoneWallDark, Offset(x - 20f, y - 18f), Size(8f, 36f))
        drawRect(stoneWallDark, Offset(x + 12f, y - 18f), Size(8f, 36f))
        drawRect(stoneWallDark, Offset(x - 22f, y - 24f), Size(44f, 8f)) // Lintel

        // Swirling Purple Vortex
        val spin = time * 2f
        drawOval(Color(0xFF3B0764), Offset(x - 12f, y - 14f), Size(24f, 30f))
        drawOval(Color(0xFFA855F7), Offset(x - 8f, y - 10f), Size(16f, 22f))
        drawOval(Color(0xFFF3E8FF), Offset(x - 4f, y - 6f), Size(8f, 14f))

        // Pulsing Runes
        val pulse = (sin(time * 3f) + 1f) * 0.2f
        drawCircle(Color(0xFFC084FC).copy(alpha = 0.4f + pulse), 16f, Offset(x, y))
    }

    // (L) ROYAL STOREHOUSE (Gudang)
    private fun DrawScope.drawRoyalStorehouse(x: Float, y: Float, time: Float, level: Int) {
        drawOval(shadowColor, Offset(x - 20f, y + 12f), Size(40f, 10f))
        drawRect(stoneWallBase, Offset(x - 16f, y - 4f), Size(32f, 16f))
        val roof = Path().apply {
            moveTo(x, y - 16f)
            lineTo(x - 18f, y - 4f)
            lineTo(x + 18f, y - 4f)
            close()
        }
        drawPath(roof, royalBlueRoof)
        drawRect(timberBeam, Offset(x - 5f, y + 2f), Size(10f, 10f))
    }

    // (M) MEDIEVAL HOUSES (Rumah Warga)
    private fun DrawScope.drawMedievalHouses(w: Float, h: Float, state: GameState) {
        val level = state.getBuildingLevel("rumah_warga")
        fun drawSmallHouse(hx: Float, hy: Float, roofColor: Color) {
            drawOval(shadowColor, Offset(hx - 12f, hy + 8f), Size(24f, 6f))
            drawRect(plasterWall, Offset(hx - 10f, hy - 4f), Size(20f, 12f))
            drawLine(timberBeam, Offset(hx - 10f, hy + 2f), Offset(hx + 10f, hy + 2f), 1.5f)
            val r = Path().apply {
                moveTo(hx, hy - 14f)
                lineTo(hx - 12f, hy - 4f)
                lineTo(hx + 12f, hy - 4f)
                close()
            }
            drawPath(r, roofColor)
            drawRect(timberBeam, Offset(hx - 2f, hy + 2f), Size(4f, 6f)) // Door
        }

        // Surrounding cottage positions from reference image
        drawSmallHouse(w * 0.32f, h * 0.28f, royalBlueRoof)
        drawSmallHouse(w * 0.68f, h * 0.28f, redTileRoof)
        if (level >= 2) {
            drawSmallHouse(w * 0.34f, h * 0.44f, redTileRoof)
            drawSmallHouse(w * 0.66f, h * 0.44f, royalBlueRoof)
        }
        if (level >= 3) {
            drawSmallHouse(w * 0.34f, h * 0.58f, royalBlueRoof)
            drawSmallHouse(w * 0.66f, h * 0.58f, Color(0xFFD97706))
        }
    }

    // (N) PATROLLING KNIGHTS & CITIZENS
    private fun DrawScope.drawKingdomPatrols(w: Float, h: Float, time: Float, state: GameState) {
        val cx = w * 0.50f
        val cy = h * 0.52f

        // Patrolling Royal Guard Knight with lion shield
        val knightWalk = sin(time * 1.5f) * 20f
        val kx = cx + knightWalk
        val ky = cy + 30f
        // Armor
        drawRect(stoneWallLight, Offset(kx - 3f, ky - 8f), Size(6f, 10f))
        drawCircle(stoneWallLight, 2.5f, Offset(kx, ky - 10f)) // Helmet
        // Red Tabard
        drawRect(redTileMid, Offset(kx - 2f, ky - 6f), Size(4f, 6f))
        // Lion Shield & Spear
        drawRect(royalBlueRoof, Offset(kx - 5f, ky - 6f), Size(3f, 6f))
        drawLine(stoneWallLight, Offset(kx + 4f, ky - 12f), Offset(kx + 4f, ky + 4f), 1.5f)

        // Citizen walking in town square
        val citWalk = cos(time * 1.2f) * 16f
        val citX = cx - 18f + citWalk
        val citY = cy - 20f
        drawCircle(Color(0xFFFBCFE8), 2f, Offset(citX, citY - 7f)) // Face
        drawRect(Color(0xFF2563EB), Offset(citX - 2f, citY - 4f), Size(4f, 7f)) // Blue tunic
    }

    // Vacant lot for locked buildings
    private fun DrawScope.drawVacantMeadow(x: Float, y: Float, rw: Float, rh: Float, seed: Int) {
        drawOval(grassDark.copy(alpha = 0.5f), Offset(x - rw / 2, y - rh / 2), Size(rw, rh))
        for (i in 0..4) {
            val fx = x - rw / 3 + ((seed * 17 + i * 29) % rw.toInt())
            val fy = y - rh / 3 + ((seed * 31 + i * 23) % rh.toInt())
            drawCircle(Color(0xFFFEF08A), 2f, Offset(fx, fy)) // Wildflowers
        }
    }
}
