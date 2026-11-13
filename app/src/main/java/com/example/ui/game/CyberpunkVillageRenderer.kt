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
 * CyberpunkVillageRenderer (Zaman Cyberpunk / Neon Cosmos)
 *
 * Renders the breathtaking, high-fidelity 16-bit pixel art cyberpunk metropolis matching
 * the reference image (Image 1):
 * - Colossal Cyber Citadel / Neon Spire with glowing cyan crystal core & cyber-monk statues at center
 * - Quantum Levitating Shrine with glowing purple holographic ring at top-center
 * - Cyber Fusion Refinery with glowing amber plasma reactors & maglev monorail at top-left
 * - Cyber Crystal Mining Cavern with glowing neon-cyan energy crystals at top-right
 * - Vertical LED Hydroponic Towers & bio-domes at upper-west
 * - Cyber Med-Bay Trauma Center with glowing red neon cross & hover-ambulance at mid-left
 * - Cyber Skyscraper Metropolis with holographic billboards & skybridges at upper-right
 * - Bio-Synth Livestock Dome with glowing neon bull sign at mid-east
 * - Heavy Armored Mech Military Garrison with VTOL dropships & laser turrets at lower-west
 * - Holographic Cyber Bazaar with rotating cyan Ethereum/diamond hologram at lower-center
 * - Cyber Lounge Nightclub with glowing magenta cocktail neon sign at lower-right
 * - Quantum Grav-Warp Portal with swirling ultraviolet vortex at bottom-right
 * - Dark alloy fortress mega-walls with glowing cyan conduits & forcefield blast gate
 * - Glowing synthetic blue waterways with hovering neon speeders
 */
object CyberpunkVillageRenderer {

    // Palettes matching Cyberpunk Neon Metropolis (Image 1)
    val cyberDarkFloor = Color(0xFF090D16)
    val cyberPlateGrid = Color(0xFF0F172A)
    val cyberConduitCyan = Color(0xFF06B6D4)
    val cyberNeonCyan = Color(0xFF22D3EE)
    val cyberBrightCyan = Color(0xFFA5F3FC)
    
    val cyberNeonMagenta = Color(0xFFEC4899)
    val cyberHotPink = Color(0xFFF43F5E)
    val cyberDeepPurple = Color(0xFF581C87)
    val cyberNeonPurple = Color(0xFFA855F7)
    val cyberVioletGlow = Color(0xFFC084FC)
    
    val cyberEnergyAmber = Color(0xFFF59E0B)
    val cyberNeonYellow = Color(0xFFFDE047)
    val cyberHydroGreen = Color(0xFF10B981)
    val cyberNeonGreen = Color(0xFF4ADE80)
    
    val chromeWhite = Color(0xFFF8FAFC)
    val chromeSilver = Color(0xFFCBD5E1)
    val darkAlloy = Color(0xFF1E293B)
    val deepDarkAlloy = Color(0xFF0A0F1D)
    
    val syntheticWater = Color(0xFF1E40AF)
    val syntheticWaterGlow = Color(0xFF3B82F6)
    val shadowColor = Color(0x60000000)

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

            // 1. BASE CYBER TERRAIN (Dark Metallic Plating & Glowing Neon Grid)
            drawBaseCyberGrid(w, h, time)

            // 2. SYNTHETIC NEON WATERWAY & MAGLEV SKYWAYS
            drawWestSyntheticWaterAndMaglev(w, h, time)

            // 3. CYBER MEGAPOLIS PERIMETER SKYLINE
            drawSurroundingCyberSkyline(w, h, time)

            // 4. NEON DATA CONDUIT ROADS & SKYWALKS
            drawCyberConduitHighways(w, h, time)

            // 5. CYBER MEGA-WALLS, LASER BLAST GATE & POWER BASTIONS (Tembok)
            drawCyberMegaWallsAndGate(w, h, time, state.isUnlocked("tembok"), state.getBuildingLevel("tembok"))

            // 6. BUILDINGS & DISTRICTS (Unlockable one by one)

            // (A) Center: Cyber Citadel / Neon Spire (Town Hall)
            if (state.isUnlocked("town_hall")) {
                drawCyberCitadelSpire(px(0.50f), py(0.39f), time, state.getBuildingLevel("town_hall"))
            } else {
                drawCyberEmptyPlot(px(0.50f), py(0.39f), 0.20f * w, 0.16f * h, seed = 101)
            }

            // (B) Top-Center: Quantum Levitating Shrine (Temple)
            if (state.isUnlocked("temple")) {
                drawQuantumCyberShrine(px(0.50f), py(0.14f), time, state.getBuildingLevel("temple"))
            } else {
                drawCyberEmptyPlot(px(0.50f), py(0.14f), 0.16f * w, 0.11f * h, seed = 202)
            }

            // (C) Top-Left: Cyber Fusion Refinery (Penebang Kayu)
            if (state.isUnlocked("penebang_kayu")) {
                drawCyberFusionRefinery(px(0.23f), py(0.15f), time, state.getBuildingLevel("penebang_kayu"))
            } else {
                drawCyberEmptyPlot(px(0.23f), py(0.15f), 0.14f * w, 0.10f * h, seed = 303)
            }

            // (D) Top-Right: Cyber Crystal Mine & Excavator (Pemecah Batu)
            if (state.isUnlocked("pemecah_batu")) {
                drawCyberCrystalMine(px(0.78f), py(0.15f), time, state.getBuildingLevel("pemecah_batu"))
            } else {
                drawCyberEmptyPlot(px(0.78f), py(0.15f), 0.14f * w, 0.10f * h, seed = 404)
            }

            // (E) Upper-West: Vertical Hydroponic Bio-Towers (Pertanian)
            if (state.isUnlocked("pertanian")) {
                drawVerticalHydroponicTowers(px(0.18f), py(0.31f), time, state.getBuildingLevel("pertanian"))
            } else {
                drawCyberEmptyPlot(px(0.18f), py(0.31f), 0.14f * w, 0.11f * h, seed = 505)
            }

            // (F) Mid-East: Bio-Synth Cattle Dome (Peternakan)
            if (state.isUnlocked("peternakan")) {
                drawBioSynthCattleDome(px(0.82f), py(0.48f), time, state.getBuildingLevel("peternakan"))
            } else {
                drawCyberEmptyPlot(px(0.82f), py(0.48f), 0.14f * w, 0.10f * h, seed = 606)
            }

            // (G) Mid-Left: Cyber Med-Bay Trauma Center (Hospital)
            if (state.isUnlocked("hospital")) {
                drawCyberMedBayTraumaCenter(px(0.18f), py(0.48f), time, state.getBuildingLevel("hospital"))
            } else {
                drawCyberEmptyPlot(px(0.18f), py(0.48f), 0.14f * w, 0.10f * h, seed = 707)
            }

            // (H) Lower-West: Cyber Military & Mech Garrison (Barak Militer)
            if (state.isUnlocked("barak_militer")) {
                drawCyberMechGarrison(px(0.20f), py(0.66f), time, state.getBuildingLevel("barak_militer"))
            } else {
                drawCyberEmptyPlot(px(0.20f), py(0.66f), 0.14f * w, 0.11f * h, seed = 808)
            }

            // (I) Lower-Center: Holographic Cyber Bazaar (Market)
            if (state.isUnlocked("market") || state.isUnlocked("barter_purba")) {
                drawHolographicCyberBazaar(px(0.50f), py(0.66f), time, state.getBuildingLevel("market"))
            } else {
                drawCyberEmptyPlot(px(0.50f), py(0.66f), 0.18f * w, 0.12f * h, seed = 909)
            }

            // Central Holographic Fountain (North of Bazaar)
            drawCentralHoloFountain(px(0.50f), py(0.53f), time)

            // (J) Lower-Right: Cyber Neon Lounge & Nightclub (Tavern)
            if (state.isUnlocked("tavern")) {
                drawCyberNightclubLounge(px(0.82f), py(0.64f), time, state.getBuildingLevel("tavern"))
            } else {
                drawCyberEmptyPlot(px(0.82f), py(0.64f), 0.14f * w, 0.10f * h, seed = 1010)
            }

            // (K) Bottom-Right: Quantum Warp Gate (Dungeon)
            if (state.isUnlocked("dungeon")) {
                drawQuantumWarpGate(px(0.80f), py(0.76f), time, state.maxDungeonLevel)
            } else {
                drawCyberEmptyPlot(px(0.80f), py(0.76f), 0.13f * w, 0.10f * h, seed = 1111)
            }

            // (L) North of Center: Cyber Data Vault (Gudang)
            if (state.isUnlocked("gudang")) {
                drawCyberDataVault(px(0.50f), py(0.26f), time, state.getBuildingLevel("gudang"))
            }

            // (M) Cyber Skyscraper Residential Metropolis (Rumah Warga)
            drawCyberResidentialSkyscrapers(w, h, time, state)

            // (N) Hover Vehicles & Flying Maglev Patrols
            drawHoverVehiclesAndDrones(w, h, time)
        }
    }

    // 1. BASE CYBER GRID
    private fun DrawScope.drawBaseCyberGrid(w: Float, h: Float, time: Float) {
        drawRect(cyberDarkFloor, Offset.Zero, Size(w, h))
        val dot = 10f
        val cols = (w / dot).toInt()
        val rows = (h / dot).toInt()

        for (y in 0..rows) {
            for (x in 0..cols) {
                val isLineX = (x % 6 == 0)
                val isLineY = (y % 6 == 0)
                if (isLineX || isLineY) {
                    val pulse = ((x + y + (time * 3f).toInt()) % 12 == 0)
                    val c = if (pulse) cyberNeonCyan.copy(alpha = 0.6f) else cyberConduitCyan.copy(alpha = 0.18f)
                    drawRect(c, Offset(x * dot, y * dot), Size(dot, dot))
                } else {
                    drawRect(cyberPlateGrid, Offset(x * dot, y * dot), Size(dot, dot))
                }
            }
        }
    }

    // 2. SYNTHETIC WATERWAY & MAGLEV
    private fun DrawScope.drawWestSyntheticWaterAndMaglev(w: Float, h: Float, time: Float) {
        val riverPath = Path().apply {
            moveTo(0f, 0f)
            lineTo(w * 0.11f, 0f)
            cubicTo(w * 0.13f, h * 0.35f, w * 0.05f, h * 0.65f, w * 0.09f, h)
            lineTo(0f, h)
            close()
        }
        drawPath(riverPath, syntheticWater)
        // Glowing Neon Blue Shoreline
        drawPath(riverPath, cyberNeonCyan, style = Stroke(2.5f))

        // Synthetic Water Pulse Lines
        for (i in 0 until 16) {
            val y = (h * (i * 0.065f) + sin(time * 2f + i) * 8f) % h
            val x = w * (0.02f + 0.04f * sin(i * 1.5f))
            drawLine(cyberBrightCyan, Offset(x, y), Offset(x + 14f, y), 2f)
        }

        // Elevated Maglev Track Bridge (upper-west)
        val bridgeY = h * 0.10f
        drawRect(darkAlloy, Offset(0f, bridgeY - 6f), Size(w * 0.11f, 18f))
        drawLine(cyberNeonMagenta, Offset(0f, bridgeY - 6f), Offset(w * 0.11f, bridgeY - 6f), 2f)
        drawLine(cyberNeonCyan, Offset(0f, bridgeY + 12f), Offset(w * 0.11f, bridgeY + 12f), 2f)
        // Floating Monorail Car passing by
        val trainSpeed = (time * 60f) % (w * 0.11f + 30f) - 15f
        drawRoundRect(chromeWhite, Offset(trainSpeed, bridgeY - 4f), Size(22f, 8f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f, 3f))
        drawRect(cyberNeonCyan, Offset(trainSpeed + 4f, bridgeY - 2f), Size(14f, 4f)) // Window strip
    }

    // 3. CYBER SKYLINE PERIMETER
    private fun DrawScope.drawSurroundingCyberSkyline(w: Float, h: Float, time: Float) {
        // High-Tech Cyber Antennae & Neon Spires
        fun drawCyberPylon(px: Float, py: Float, color: Color) {
            drawLine(darkAlloy, Offset(px, py), Offset(px, py - 20f), 2f)
            drawCircle(color, 2.5f, Offset(px, py - 20f))
        }

        for (i in 0..12) {
            val c = if (i % 2 == 0) cyberNeonCyan else cyberNeonMagenta
            drawCyberPylon(w * (0.12f + i * 0.07f), 10f, c)
        }
        for (i in 0..10) {
            val c = if (i % 2 == 0) cyberNeonMagenta else cyberNeonPurple
            drawCyberPylon(w * 0.95f, h * (0.08f + i * 0.09f), c)
        }
    }

    // 4. NEON CONDUIT HIGHWAYS
    private fun DrawScope.drawCyberConduitHighways(w: Float, h: Float, time: Float) {
        val cx = w * 0.50f
        val roadW = 30f

        // Central Illuminated Cyber Highway
        drawRect(deepDarkAlloy, Offset(cx - roadW / 2, 0.06f * h), Size(roadW, 0.88f * h))
        drawLine(cyberNeonCyan, Offset(cx - roadW / 2, 0.06f * h), Offset(cx - roadW / 2, 0.94f * h), 2.5f)
        drawLine(cyberNeonCyan, Offset(cx + roadW / 2, 0.06f * h), Offset(cx + roadW / 2, 0.94f * h), 2.5f)

        // Pulsing Neon Center Conduit Line
        for (i in 0..28) {
            val ly = 0.08f * h + i * 16f
            val pulse = ((i + (time * 4f).toInt()) % 6 == 0)
            val c = if (pulse) chromeWhite else cyberNeonMagenta
            drawLine(c, Offset(cx, ly), Offset(cx, ly + 8f), 2f)
        }

        // East-West Connecting Skybridges
        fun drawCrossway(y: Float) {
            drawRect(deepDarkAlloy, Offset(0.11f * w, y - 10f), Size(0.78f * w, 20f))
            drawLine(cyberNeonPurple, Offset(0.11f * w, y - 10f), Offset(0.89f * w, y - 10f), 2f)
            drawLine(cyberNeonPurple, Offset(0.11f * w, y + 10f), Offset(0.89f * w, y + 10f), 2f)
        }
        drawCrossway(h * 0.22f)
        drawCrossway(h * 0.40f)
        drawCrossway(h * 0.58f)
    }

    // 5. CYBER MEGA-WALLS & LASER BLAST GATE
    private fun DrawScope.drawCyberMegaWallsAndGate(w: Float, h: Float, time: Float, isUnlocked: Boolean, level: Int) {
        val left = w * 0.11f
        val right = w * 0.89f
        val top = h * 0.06f
        val bottom = h * 0.88f
        val cx = w * 0.50f
        val gateW = 46f

        // Heavy Armored Dark Alloy Fortress Walls
        val wallStroke = 9f
        drawLine(darkAlloy, Offset(left, top), Offset(right, top), wallStroke)
        drawLine(darkAlloy, Offset(left, top), Offset(left, bottom), wallStroke)
        drawLine(darkAlloy, Offset(right, top), Offset(right, bottom), wallStroke)
        drawLine(darkAlloy, Offset(left, bottom), Offset(cx - gateW / 2, bottom), wallStroke)
        drawLine(darkAlloy, Offset(cx + gateW / 2, bottom), Offset(right, bottom), wallStroke)

        // Glowing Blue/Purple Neon Conduits Embedded in Walls
        drawLine(cyberNeonCyan, Offset(left, top), Offset(right, top), 2f)
        drawLine(cyberNeonCyan, Offset(left, top), Offset(left, bottom), 2f)
        drawLine(cyberNeonCyan, Offset(right, top), Offset(right, bottom), 2f)

        // Corner Bastion Power Towers with Glowing Crystals
        fun drawBastionTower(tx: Float, ty: Float) {
            drawRect(deepDarkAlloy, Offset(tx - 12f, ty - 12f), Size(24f, 24f))
            drawRect(darkAlloy, Offset(tx - 9f, ty - 9f), Size(18f, 18f))
            drawRect(cyberNeonMagenta, Offset(tx - 9f, ty - 9f), Size(18f, 18f), style = Stroke(1.5f))
            // Glowing Purple Energy Crystal atop Bastion
            val pulse = (sin(time * 3f) + 1f) * 0.2f
            drawCircle(cyberNeonPurple.copy(alpha = 0.5f + pulse), 6f, Offset(tx, ty - 4f))
            drawCircle(chromeWhite, 2f, Offset(tx, ty - 4f))
        }

        drawBastionTower(left, top)
        drawBastionTower(right, top)
        drawBastionTower(left, bottom)
        drawBastionTower(right, bottom)

        // South Gate Blast Doors with Cyan Forcefield
        val gx = cx
        val gy = bottom
        drawRect(darkAlloy, Offset(gx - gateW / 2 - 12f, gy - 16f), Size(14f, 26f))
        drawRect(darkAlloy, Offset(gx + gateW / 2 - 2f, gy - 16f), Size(14f, 26f))

        // Glowing Cyan Forcefield Barrier
        val ffPulse = (sin(time * 6f) + 1f) * 0.15f
        drawRect(cyberNeonCyan.copy(alpha = 0.35f + ffPulse), Offset(gx - gateW / 2 + 2f, gy - 10f), Size(gateW - 4f, 18f))
        drawRect(cyberBrightCyan, Offset(gx - gateW / 2 + 2f, gy - 10f), Size(gateW - 4f, 18f), style = Stroke(1.5f))
        // Vertical Laser Grids
        for (i in 1..4) {
            drawLine(chromeWhite.copy(alpha = 0.8f), Offset(gx - gateW / 2 + i * 9f, gy - 10f), Offset(gx - gateW / 2 + i * 9f, gy + 8f), 1.5f)
        }
    }

    // (A) CYBER CITADEL / NEON SPIRE (Town Hall)
    private fun DrawScope.drawCyberCitadelSpire(x: Float, y: Float, time: Float, level: Int) {
        drawOval(shadowColor, Offset(x - 40f, y + 28f), Size(80f, 18f))

        // Cyber Skyscraper Stepped Base
        drawRect(deepDarkAlloy, Offset(x - 30f, y - 10f), Size(60f, 40f))
        drawRect(darkAlloy, Offset(x - 24f, y - 26f), Size(48f, 28f))
        drawRect(cyberPlateGrid, Offset(x - 16f, y - 44f), Size(32f, 26f))

        // Glowing Cyan Crystal Obelisk Core
        val obelisk = Path().apply {
            moveTo(x, y - 68f)
            lineTo(x - 10f, y - 44f)
            lineTo(x + 10f, y - 44f)
            close()
        }
        drawPath(obelisk, cyberBrightCyan)
        val pulse = (sin(time * 4f) + 1f) * 0.25f
        drawCircle(cyberNeonCyan.copy(alpha = 0.4f + pulse), 16f, Offset(x, y - 56f))

        // Vertical Laser Light Beams to Sky
        drawLine(cyberBrightCyan.copy(alpha = 0.8f), Offset(x, y - 68f), Offset(x, y - 88f), 2.5f)

        // Holographic Neon Emblems & Kanji Banners
        drawRect(cyberNeonMagenta, Offset(x - 26f, y - 6f), Size(6f, 18f))
        drawRect(cyberNeonMagenta, Offset(x + 20f, y - 6f), Size(6f, 18f))

        // Giant Hooded Cyber-Monk Statues holding energy rods
        fun drawCyberMonk(mx: Float, my: Float) {
            drawRect(chromeWhite, Offset(mx - 4f, my - 16f), Size(8f, 22f))
            drawCircle(cyberBrightCyan, 3f, Offset(mx, my - 18f)) // Visor glow
            drawLine(cyberNeonCyan, Offset(mx + 5f, my - 22f), Offset(mx + 5f, my + 6f), 1.5f) // Energy rod
        }
        drawCyberMonk(x - 18f, y + 4f)
        drawCyberMonk(x + 18f, y + 4f)

        // Grand Illuminated Cyber Entrance Stairs
        drawRect(darkAlloy, Offset(x - 14f, y + 16f), Size(28f, 16f))
        for (s in 0..3) {
            drawLine(cyberNeonCyan, Offset(x - 14f, y + 16f + s * 4f), Offset(x + 14f, y + 16f + s * 4f), 1.5f)
        }
    }

    // (B) QUANTUM LEVITATING SHRINE (Temple)
    private fun DrawScope.drawQuantumCyberShrine(x: Float, y: Float, time: Float, level: Int) {
        drawOval(shadowColor, Offset(x - 32f, y + 24f), Size(64f, 14f))

        // Chrome Pedestal Base
        drawRect(darkAlloy, Offset(x - 22f, y - 6f), Size(44f, 28f))
        drawRect(chromeWhite, Offset(x - 16f, y - 18f), Size(32f, 18f))

        // Levitating Floating Purple Holographic Energy Ring
        val levitate = sin(time * 2f) * 3f
        val ringY = y - 32f + levitate
        drawOval(cyberNeonPurple, Offset(x - 14f, ringY), Size(28f, 16f), style = Stroke(2.5f))
        drawCircle(chromeWhite, 4f, Offset(x, ringY + 8f))

        // Digital Cherry Blossom Cyber Trees (Neon Pink)
        fun drawCyberSakura(tx: Float, ty: Float) {
            drawLine(darkAlloy, Offset(tx, ty), Offset(tx, ty - 10f), 2f)
            drawCircle(cyberNeonMagenta, 7f, Offset(tx, ty - 12f))
            drawCircle(cyberBrightCyan, 2f, Offset(tx, ty - 12f))
        }
        drawCyberSakura(x - 22f, y + 4f)
        drawCyberSakura(x + 22f, y + 4f)
    }

    // (C) CYBER FUSION REFINERY (Penebang Kayu)
    private fun DrawScope.drawCyberFusionRefinery(x: Float, y: Float, time: Float, level: Int) {
        drawOval(shadowColor, Offset(x - 30f, y + 20f), Size(60f, 14f))

        // Heavy Industrial Complex with Cooling Stacks
        drawRect(darkAlloy, Offset(x - 24f, y - 10f), Size(48f, 26f))
        drawRect(deepDarkAlloy, Offset(x - 20f, y - 24f), Size(10f, 16f)) // Reactor cooling stack
        drawRect(deepDarkAlloy, Offset(x + 10f, y - 24f), Size(10f, 16f))

        // Glowing Amber Plasma Reactor Core
        val pulse = (sin(time * 5f) + 1f) * 0.2f
        drawCircle(cyberEnergyAmber, 8f + pulse * 2f, Offset(x - 2f, y))
        drawCircle(cyberNeonYellow, 4f, Offset(x - 2f, y))

        // Glowing Yellow Energy Crystals on Ground
        for (i in 0..3) {
            val cx = x - 22f + i * 8f
            val cy = y + 14f
            drawCircle(cyberEnergyAmber, 2.5f, Offset(cx, cy))
        }
    }

    // (D) CYBER CRYSTAL MINE (Pemecah Batu)
    private fun DrawScope.drawCyberCrystalMine(x: Float, y: Float, time: Float, level: Int) {
        drawOval(shadowColor, Offset(x - 30f, y + 20f), Size(60f, 14f))

        // Dark Rocky Excavation Backing
        val cave = Path().apply {
            moveTo(x - 28f, y + 14f)
            lineTo(x - 24f, y - 20f)
            lineTo(x + 22f, y - 24f)
            lineTo(x + 28f, y + 14f)
            close()
        }
        drawPath(cave, deepDarkAlloy)

        // Bright Glowing Neon-Cyan Energy Crystals
        fun drawCrystalCluster(cx: Float, cy: Float) {
            val cry = Path().apply {
                moveTo(cx, cy - 12f)
                lineTo(cx - 4f, cy)
                lineTo(cx + 4f, cy)
                close()
            }
            drawPath(cry, cyberBrightCyan)
            drawCircle(cyberNeonCyan.copy(alpha = 0.5f), 7f, Offset(cx, cy - 6f))
        }
        drawCrystalCluster(x - 14f, y + 2f)
        drawCrystalCluster(x - 4f, y - 6f)
        drawCrystalCluster(x + 8f, y + 4f)

        // Automated Robotic Mining Laser Crane Arm
        val armX = x + 16f
        val armY = y - 4f
        drawLine(chromeSilver, Offset(armX, armY), Offset(armX - 10f, armY - 14f), 2.5f)
        drawLine(chromeSilver, Offset(armX - 10f, armY - 14f), Offset(armX - 18f, armY - 8f), 2f)
        // Red Laser beam cutting rock
        drawLine(cyberHotPink, Offset(armX - 18f, armY - 8f), Offset(x - 4f, y - 6f), 1.5f)
    }

    // (E) VERTICAL HYDROPONIC TOWERS (Pertanian)
    private fun DrawScope.drawVerticalHydroponicTowers(x: Float, y: Float, time: Float, level: Int) {
        drawOval(shadowColor, Offset(x - 30f, y + 20f), Size(60f, 12f))

        // Multi-tier Vertical Farm Tower with luminous green LED trays
        fun drawHydroTower(tx: Float, ty: Float) {
            drawRect(darkAlloy, Offset(tx - 9f, ty - 22f), Size(18f, 36f))
            for (tier in 0..3) {
                val ry = ty - 18f + tier * 8f
                drawRect(cyberHydroGreen, Offset(tx - 7f, ry), Size(14f, 4f))
                drawRect(cyberNeonGreen, Offset(tx - 7f, ry), Size(14f, 1.5f)) // LED grow light
            }
        }
        drawHydroTower(x - 14f, y)
        drawHydroTower(x + 12f, y)
    }

    // (F) BIO-SYNTH CATTLE DOME (Peternakan)
    private fun DrawScope.drawBioSynthCattleDome(x: Float, y: Float, time: Float, level: Int) {
        drawOval(shadowColor, Offset(x - 30f, y + 20f), Size(60f, 14f))

        // Geodesic Biosafety Glass Dome
        drawOval(cyberNeonCyan.copy(alpha = 0.25f), Offset(x - 26f, y - 16f), Size(52f, 32f))
        drawOval(cyberNeonCyan, Offset(x - 26f, y - 16f), Size(52f, 32f), style = Stroke(1.5f))

        // Large Glowing Neon Bull Head Hologram Sign ("🐮")
        drawCircle(cyberNeonCyan, 6f, Offset(x + 16f, y - 18f))
        drawLine(cyberBrightCyan, Offset(x + 12f, y - 22f), Offset(x + 14f, y - 18f), 2f) // Horn
        drawLine(cyberBrightCyan, Offset(x + 20f, y - 22f), Offset(x + 18f, y - 18f), 2f)

        // Cloned Cyber Cattle with luminescent data collar
        val cowBob = sin(time * 1.2f) * 1.5f
        drawOval(chromeWhite, Offset(x - 14f, y + 2f + cowBob), Size(14f, 8f))
        drawCircle(cyberNeonCyan, 2f, Offset(x - 12f, y + 3f + cowBob)) // Neon collar
        drawCircle(chromeWhite, 3f, Offset(x - 16f, y + 3f + cowBob)) // Head
    }

    // (G) CYBER MED-BAY TRAUMA CENTER (Hospital)
    private fun DrawScope.drawCyberMedBayTraumaCenter(x: Float, y: Float, time: Float, level: Int) {
        drawOval(shadowColor, Offset(x - 30f, y + 20f), Size(60f, 14f))

        // Sleek Chrome & Dark Alloy Hospital Complex
        drawRect(chromeWhite, Offset(x - 24f, y - 10f), Size(48f, 26f))
        drawRect(deepDarkAlloy, Offset(x - 26f, y - 18f), Size(52f, 10f)) // Upper deck

        // Brilliant Glowing Red Holographic Medical Cross
        val pulse = (sin(time * 4f) + 1f) * 0.2f
        drawRect(cyberHotPink, Offset(x - 2.5f, y - 28f), Size(5f, 14f))
        drawRect(cyberHotPink, Offset(x - 7f, y - 23.5f), Size(14f, 5f))
        drawCircle(cyberHotPink.copy(alpha = 0.4f + pulse), 10f, Offset(x, y - 21f))

        // Cyber Hover-Ambulance with strobes
        drawRoundRect(chromeSilver, Offset(x + 4f, y + 10f), Size(18f, 8f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f, 3f))
        val strobe = if ((time * 6f).toInt() % 2 == 0) cyberHotPink else cyberNeonCyan
        drawCircle(strobe, 2f, Offset(x + 13f, y + 9f))
    }

    // (H) CYBER MECH GARRISON (Barak Militer)
    private fun DrawScope.drawCyberMechGarrison(x: Float, y: Float, time: Float, level: Int) {
        drawOval(shadowColor, Offset(x - 30f, y + 20f), Size(60f, 14f))

        // Heavy Fortified Mech Hangar Base
        drawRect(deepDarkAlloy, Offset(x - 26f, y - 10f), Size(52f, 30f))
        drawRect(darkAlloy, Offset(x - 22f, y - 20f), Size(44f, 12f))
        // Red Military Neon Banners
        drawRect(cyberHotPink, Offset(x - 22f, y - 8f), Size(5f, 14f))
        drawRect(cyberHotPink, Offset(x + 17f, y - 8f), Size(5f, 14f))

        // Combat Mech on Helipad Pad
        val mx = x - 8f
        val my = y + 10f
        drawRect(darkAlloy, Offset(mx - 5f, my - 12f), Size(10f, 14f)) // Mech torso
        drawCircle(cyberHotPink, 2f, Offset(mx, my - 10f)) // Visor glow
        drawLine(chromeSilver, Offset(mx - 7f, my - 6f), Offset(mx - 10f, my + 4f), 2.5f) // Arm cannon
        drawLine(chromeSilver, Offset(mx + 7f, my - 6f), Offset(mx + 10f, my + 4f), 2.5f)

        // VTOL Stealth Fighter on Pad
        drawRect(Color(0xFF0F172A), Offset(x + 8f, y + 8f), Size(14f, 8f))
        drawLine(cyberNeonCyan, Offset(x + 8f, y + 12f), Offset(x + 22f, y + 12f), 1.5f)
    }

    // (I) HOLOGRAPHIC CYBER BAZAAR (Market)
    private fun DrawScope.drawHolographicCyberBazaar(x: Float, y: Float, time: Float, level: Int) {
        // High-Tech Neon Market Kiosks with Digital Billboards
        fun drawCyberKiosk(kx: Float, ky: Float, neonColor: Color) {
            drawRect(deepDarkAlloy, Offset(kx - 10f, ky - 6f), Size(20f, 16f))
            drawRect(neonColor, Offset(kx - 11f, ky - 8f), Size(22f, 3f)) // Neon roof strip
            drawRect(chromeWhite.copy(alpha = 0.8f), Offset(kx - 7f, ky + 2f), Size(14f, 6f)) // Digital screen
        }

        drawCyberKiosk(x - 24f, y - 4f, cyberNeonMagenta)
        drawCyberKiosk(x + 24f, y - 4f, cyberNeonCyan)
        drawCyberKiosk(x - 18f, y + 14f, cyberEnergyAmber)
        drawCyberKiosk(x + 18f, y + 14f, cyberNeonPurple)
    }

    // Central Rotating Holographic Fountain (North of Bazaar)
    private fun DrawScope.drawCentralHoloFountain(x: Float, y: Float, time: Float) {
        drawCircle(deepDarkAlloy, 12f, Offset(x, y))
        drawCircle(cyberNeonCyan, 10f, Offset(x, y), style = Stroke(2f))

        // Floating Rotating Holographic Diamond / Ethereum Crystal
        val angle = time * 2.5f
        val diaPath = Path().apply {
            moveTo(x, y - 16f)
            lineTo(x - 6f * cos(angle), y - 7f)
            lineTo(x, y + 2f)
            lineTo(x + 6f * cos(angle), y - 7f)
            close()
        }
        drawPath(diaPath, cyberBrightCyan)
        drawCircle(cyberNeonCyan.copy(alpha = 0.5f), 12f, Offset(x, y - 7f))
    }

    // (J) CYBER NIGHTCLUB LOUNGE (Tavern)
    private fun DrawScope.drawCyberNightclubLounge(x: Float, y: Float, time: Float, level: Int) {
        drawOval(shadowColor, Offset(x - 26f, y + 18f), Size(52f, 14f))

        // Curved Ultra-Modern Nightclub Building
        drawRoundRect(deepDarkAlloy, Offset(x - 22f, y - 10f), Size(44f, 26f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f))
        drawRoundRect(cyberNeonMagenta, Offset(x - 22f, y - 10f), Size(44f, 26f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f), style = Stroke(2f))

        // Giant Glowing Magenta Cocktail Glass Neon Sign ("🍸")
        val signX = x - 12f
        val signY = y - 22f
        val glass = Path().apply {
            moveTo(signX - 6f, signY)
            lineTo(signX + 6f, signY)
            lineTo(signX, signY + 7f)
            close()
        }
        drawPath(glass, cyberNeonMagenta)
        drawLine(cyberNeonMagenta, Offset(signX, signY + 7f), Offset(signX, signY + 12f), 2f)
        drawLine(cyberNeonMagenta, Offset(signX - 4f, signY + 12f), Offset(signX + 4f, signY + 12f), 2f)
        drawCircle(cyberBrightCyan, 1.5f, Offset(signX + 2f, signY + 2f)) // Olive
    }

    // (K) QUANTUM WARP GATE (Dungeon)
    private fun DrawScope.drawQuantumWarpGate(x: Float, y: Float, time: Float, maxLevel: Int) {
        drawOval(shadowColor, Offset(x - 28f, y + 18f), Size(56f, 12f))

        // Spinning Gravity Acceleration Ring
        val spin = time * 3f
        drawCircle(deepDarkAlloy, 18f, Offset(x, y))
        drawCircle(cyberNeonPurple, 16f, Offset(x, y), style = Stroke(2.5f))

        // Swirling Ultraviolet Vortex Event Horizon
        val pulse = (sin(time * 4f) + 1f) * 0.25f
        drawCircle(cyberDeepPurple, 12f + pulse * 2f, Offset(x, y))
        drawCircle(cyberNeonMagenta, 8f, Offset(x, y))
        drawCircle(chromeWhite, 4f, Offset(x, y))

        // Peripheral Energy Emitters
        for (i in 0..3) {
            val a = spin + i * (PI.toFloat() / 2f)
            val ex = x + cos(a) * 16f
            val ey = y + sin(a) * 16f
            drawCircle(cyberNeonCyan, 2.5f, Offset(ex, ey))
        }
    }

    // (L) CYBER DATA VAULT (Gudang)
    private fun DrawScope.drawCyberDataVault(x: Float, y: Float, time: Float, level: Int) {
        drawOval(shadowColor, Offset(x - 20f, y + 12f), Size(40f, 10f))
        drawRect(deepDarkAlloy, Offset(x - 16f, y - 6f), Size(32f, 18f))
        drawRect(cyberNeonCyan, Offset(x - 16f, y - 6f), Size(32f, 18f), style = Stroke(1.5f))
        drawRect(cyberNeonMagenta, Offset(x - 6f, y - 1f), Size(12f, 8f)) // Hologram lock
    }

    // (M) CYBER SKYSCRAPERS (Rumah Warga)
    private fun DrawScope.drawCyberResidentialSkyscrapers(w: Float, h: Float, time: Float, state: GameState) {
        val level = state.getBuildingLevel("rumah_warga")

        fun drawSkyscraper(sx: Float, sy: Float, height: Float, neonColor: Color) {
            drawOval(shadowColor, Offset(sx - 12f, sy + 10f), Size(24f, 8f))
            drawRect(deepDarkAlloy, Offset(sx - 10f, sy - height), Size(20f, height + 8f))
            drawRect(neonColor, Offset(sx - 10f, sy - height), Size(20f, height + 8f), style = Stroke(1.5f))
            // Glowing Window Grid
            for (r in 0..3) {
                val wy = sy - height + 6f + r * 6f
                if (wy < sy) {
                    drawRect(cyberNeonYellow, Offset(sx - 7f, wy), Size(5f, 3f))
                    drawRect(cyberBrightCyan, Offset(sx + 2f, wy), Size(5f, 3f))
                }
            }
        }

        drawSkyscraper(w * 0.32f, h * 0.28f, 26f, cyberNeonCyan)
        drawSkyscraper(w * 0.68f, h * 0.28f, 32f, cyberNeonMagenta)
        if (level >= 2) {
            drawSkyscraper(w * 0.34f, h * 0.44f, 28f, cyberNeonPurple)
            drawSkyscraper(w * 0.66f, h * 0.44f, 30f, cyberNeonCyan)
        }
        if (level >= 3) {
            drawSkyscraper(w * 0.34f, h * 0.58f, 34f, cyberNeonMagenta)
            drawSkyscraper(w * 0.66f, h * 0.58f, 28f, cyberEnergyAmber)
        }
    }

    // (N) HOVER VEHICLES & FLYING DRONES
    private fun DrawScope.drawHoverVehiclesAndDrones(w: Float, h: Float, time: Float) {
        val cx = w * 0.50f

        // Cruising Hover Speeder on Central Highway
        val speederY = 0.12f * h + ((time * 60f) % (0.74f * h))
        drawRoundRect(chromeWhite, Offset(cx - 6f, speederY), Size(6f, 14f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f, 3f))
        drawCircle(cyberNeonCyan, 1.5f, Offset(cx - 3f, speederY + 12f)) // Cyan thruster trail
        drawCircle(cyberHotPink, 1f, Offset(cx - 3f, speederY + 1f)) // Headlight

        // Surveillance Drone Hovering
        val droneBob = sin(time * 3f) * 4f
        val dx = cx + 22f
        val dy = h * 0.38f + droneBob
        drawCircle(deepDarkAlloy, 3f, Offset(dx, dy))
        drawCircle(cyberHotPink, 1.5f, Offset(dx, dy))
    }

    private fun DrawScope.drawCyberEmptyPlot(x: Float, y: Float, rw: Float, rh: Float, seed: Int) {
        drawOval(deepDarkAlloy, Offset(x - rw / 2, y - rh / 2), Size(rw, rh))
        drawOval(cyberNeonCyan.copy(alpha = 0.3f), Offset(x - rw / 2, y - rh / 2), Size(rw, rh), style = Stroke(1f))
    }
}
