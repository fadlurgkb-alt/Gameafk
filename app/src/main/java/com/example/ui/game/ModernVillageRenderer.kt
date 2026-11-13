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
 * ModernVillageRenderer (Zaman Modern / Modern City)
 *
 * Renders the clean, vibrant, high-fidelity 16-bit pixel art modern metropolis matching
 * the reference image (Image 2):
 * - Neoclassical City Hall / Capitol with blue copper dome & classical columns at center
 * - Modern Hospital with rooftop helipad & ambulances at top-center
 * - Industrial Materials & Timber Depot with forklifts & trucks at top-left
 * - Modern Industrial Quarry with yellow excavators, conveyor & silos at top-right
 * - Glass Hydroponic Greenhouses & irrigated crops at upper-east
 * - Modern Dairy Cattle Pasture with silver silos & barn at mid-east
 * - Modern Military Compound with green tents, helipad & trucks at mid-left
 * - Downtown Shopping Plaza with cafes, striped awnings & civic statue at lower-center
 * - Contemporary Bistro Cafe with outdoor patio at lower-right
 * - High-Tech Dimensional Energy Portal surrounded by obelisks at bottom-right
 * - Luxury Condominiums with rooftop terraces & swimming pools
 * - Paved asphalt streets with painted road lines, street lamps & moving cars
 * - Modern perimeter security wall with guard booths & boom barrier gate
 */
object ModernVillageRenderer {

    // Palettes matching Modern Metropolis (Image 2)
    val grassPark = Color(0xFF22C55E)
    val grassParkDark = Color(0xFF15803D)
    val grassParkLight = Color(0xFF4ADE80)
    
    val asphaltRoad = Color(0xFF334155)
    val asphaltDark = Color(0xFF1E293B)
    val sidewalkGrey = Color(0xFF94A3B8)
    val roadStripeWhite = Color(0xFFF8FAFC)
    val roadStripeYellow = Color(0xFFFACC15)
    
    val modernConcrete = Color(0xFFF1F5F9)
    val modernGlassBlue = Color(0xFF38BDF8)
    val modernGlassDark = Color(0xFF0284C7)
    val domeBlue = Color(0xFF2563EB)
    val goldAccent = Color(0xFFFBBF24)
    
    val brickOrange = Color(0xFFEA580C)
    val steelGrey = Color(0xFF64748B)
    val industrialYellow = Color(0xFFEAB308)
    val militaryOlive = Color(0xFF3F6212)
    val militaryKhaki = Color(0xFF65A30D)
    
    val redMedical = Color(0xFFDC2626)
    val poolCyan = Color(0xFF06B6D4)
    val streetLampWarm = Color(0xFFFEF08A)
    val shadowColor = Color(0x38000000)

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

            // 1. BASE LANDSCAPING (City Parks & Greenery)
            drawBaseCityLandscaping(w, h)

            // 2. WEST CANAL / WATERWAY & HIGHWAY BRIDGES
            drawWestRiverAndModernBridge(w, h, time)

            // 3. SURROUNDING PINE FOREST & HIGHWAY GREENBELT
            drawSurroundingModernGreenbelt(w, h)

            // 4. ASPHALT ROAD GRID & MARKINGS
            drawAsphaltRoadNetwork(w, h, time)

            // 5. MODERN SECURITY PERIMETER WALL & BOOM GATE (Tembok)
            drawModernSecurityWallAndGate(w, h, time, state.isUnlocked("tembok"), state.getBuildingLevel("tembok"))

            // 6. BUILDINGS & DISTRICTS (Unlockable one by one)

            // (A) Center: Neoclassical Capitol / City Hall (Town Hall)
            if (state.isUnlocked("town_hall")) {
                drawModernCapitolCityHall(px(0.50f), py(0.40f), time, state.getBuildingLevel("town_hall"))
            } else {
                drawEmptyPlot(px(0.50f), py(0.40f), 0.18f * w, 0.14f * h, seed = 101)
            }

            // (B) Top-Center: Modern Hospital & Helipad (Hospital / Temple)
            if (state.isUnlocked("hospital")) {
                drawModernMedicalCenter(px(0.50f), py(0.14f), time, state.getBuildingLevel("hospital"))
            } else {
                drawEmptyPlot(px(0.50f), py(0.14f), 0.16f * w, 0.11f * h, seed = 202)
            }

            // (C) Top-Left: Industrial Lumber & Material Yard (Penebang Kayu)
            if (state.isUnlocked("penebang_kayu")) {
                drawModernMaterialDepot(px(0.23f), py(0.15f), time, state.getBuildingLevel("penebang_kayu"))
            } else {
                drawEmptyPlot(px(0.23f), py(0.15f), 0.14f * w, 0.10f * h, seed = 303)
            }

            // (D) Top-Right: Industrial Stone Quarry & Excavation (Pemecah Batu)
            if (state.isUnlocked("pemecah_batu")) {
                drawModernIndustrialQuarry(px(0.78f), py(0.15f), time, state.getBuildingLevel("pemecah_batu"))
            } else {
                drawEmptyPlot(px(0.78f), py(0.15f), 0.14f * w, 0.10f * h, seed = 404)
            }

            // (E) Upper-East: Hydroponic Greenhouses & Farm (Pertanian & Perkebunan)
            if (state.isUnlocked("pertanian")) {
                drawHydroponicGreenhouses(px(0.82f), py(0.31f), time, state.getBuildingLevel("pertanian"))
            } else {
                drawEmptyPlot(px(0.82f), py(0.31f), 0.14f * w, 0.10f * h, seed = 505)
            }
            if (state.isUnlocked("peternakan")) {
                drawModernCattleRanch(px(0.82f), py(0.48f), time, state.getBuildingLevel("peternakan"))
            } else {
                drawEmptyPlot(px(0.82f), py(0.48f), 0.14f * w, 0.10f * h, seed = 606)
            }

            // (F) Mid-Left: Modern Military Compound (Barak Militer)
            if (state.isUnlocked("barak_militer")) {
                drawModernMilitaryBase(px(0.18f), py(0.48f), time, state.getBuildingLevel("barak_militer"))
            } else {
                drawEmptyPlot(px(0.18f), py(0.48f), 0.14f * w, 0.10f * h, seed = 707)
            }

            // (G) Lower-Center: Downtown Shopping Plaza (Market)
            if (state.isUnlocked("market") || state.isUnlocked("barter_purba")) {
                drawDowntownShoppingPlaza(px(0.50f), py(0.66f), time, state.getBuildingLevel("market"))
            } else {
                drawEmptyPlot(px(0.50f), py(0.66f), 0.18f * w, 0.11f * h, seed = 808)
            }

            // Civic Monument & Fountain Plaza (North of Market)
            drawCivicStatueAndFountain(px(0.50f), py(0.53f), time)

            // (H) Lower-Right: Modern Bistro & Cafe (Tavern)
            if (state.isUnlocked("tavern")) {
                drawModernBistroCafe(px(0.82f), py(0.64f), time, state.getBuildingLevel("tavern"))
            } else {
                drawEmptyPlot(px(0.82f), py(0.64f), 0.14f * w, 0.10f * h, seed = 909)
            }

            // (I) Bottom-Right: High-Tech Science Rift (Dungeon)
            if (state.isUnlocked("dungeon")) {
                drawScienceDimensionalRift(px(0.80f), py(0.76f), time, state.maxDungeonLevel)
            } else {
                drawEmptyPlot(px(0.80f), py(0.76f), 0.13f * w, 0.10f * h, seed = 1010)
            }

            // (J) North of Center: Industrial Logistics Hub (Gudang)
            if (state.isUnlocked("gudang")) {
                drawModernLogisticsDepot(px(0.50f), py(0.26f), time, state.getBuildingLevel("gudang"))
            }

            // (K) Modern Condos & Residential Apartments (Rumah Warga)
            drawModernResidentialApartments(w, h, state)

            // (L) Moving City Cars & Pedestrians
            drawMovingTrafficAndCivilians(w, h, time)
        }
    }

    // 1. BASE CITY LANDSCAPING
    private fun DrawScope.drawBaseCityLandscaping(w: Float, h: Float) {
        drawRect(grassPark, Offset.Zero, Size(w, h))
        val dot = 8f
        val cols = (w / dot).toInt()
        val rows = (h / dot).toInt()
        for (y in 0..rows) {
            for (x in 0..cols) {
                val r = (x * 41 + y * 23) % 100
                if (r < 18) {
                    drawRect(grassParkDark, Offset(x * dot, y * dot), Size(dot, dot))
                } else if (r > 82) {
                    drawRect(grassParkLight, Offset(x * dot, y * dot), Size(dot, dot))
                }
            }
        }
    }

    // 2. WEST CANAL & BRIDGE
    private fun DrawScope.drawWestRiverAndModernBridge(w: Float, h: Float, time: Float) {
        val riverPath = Path().apply {
            moveTo(0f, 0f)
            lineTo(w * 0.11f, 0f)
            cubicTo(w * 0.13f, h * 0.35f, w * 0.05f, h * 0.65f, w * 0.09f, h)
            lineTo(0f, h)
            close()
        }
        drawPath(riverPath, Color(0xFF0284C7))
        // Concrete embankments
        drawPath(riverPath, Color(0xFF64748B), style = Stroke(3f))

        // Modern Concrete Girder Bridge
        val bridgeY = h * 0.10f
        drawRect(asphaltRoad, Offset(0f, bridgeY - 6f), Size(w * 0.11f, 20f))
        drawLine(Color.White, Offset(0f, bridgeY - 6f), Offset(w * 0.11f, bridgeY - 6f), 2f)
        drawLine(Color.White, Offset(0f, bridgeY + 14f), Offset(w * 0.11f, bridgeY + 14f), 2f)
        // Center dashed yellow line
        for (i in 0..3) {
            drawLine(roadStripeYellow, Offset(i * 12f, bridgeY + 4f), Offset(i * 12f + 6f, bridgeY + 4f), 1.5f)
        }
    }

    // 3. GREENBELT PERIMETER
    private fun DrawScope.drawSurroundingModernGreenbelt(w: Float, h: Float) {
        fun drawParkTree(x: Float, y: Float) {
            drawOval(shadowColor, Offset(x - 7f, y + 6f), Size(14f, 6f))
            drawRect(Color(0xFF78350F), Offset(x - 1.5f, y), Size(3f, 8f))
            drawCircle(Color(0xFF15803D), 9f, Offset(x, y - 4f))
            drawCircle(Color(0xFF22C55E), 7f, Offset(x, y - 6f))
        }

        for (i in 0..12) {
            drawParkTree(w * (0.12f + i * 0.07f), 12f)
        }
        for (i in 0..10) {
            drawParkTree(w * 0.95f, h * (0.08f + i * 0.09f))
        }
        for (i in 0..4) {
            drawParkTree(w * (0.04f + i * 0.06f), h * 0.96f)
            drawParkTree(w * (0.68f + i * 0.06f), h * 0.96f)
        }
    }

    // 4. ASPHALT ROAD NETWORK
    private fun DrawScope.drawAsphaltRoadNetwork(w: Float, h: Float, time: Float) {
        val cx = w * 0.50f
        val roadW = 28f

        // North-South Central Avenue
        drawRect(asphaltRoad, Offset(cx - roadW / 2, 0.06f * h), Size(roadW, 0.88f * h))
        drawRect(sidewalkGrey, Offset(cx - roadW / 2 - 3f, 0.06f * h), Size(3f, 0.88f * h))
        drawRect(sidewalkGrey, Offset(cx + roadW / 2, 0.06f * h), Size(3f, 0.88f * h))

        // East-West Boulevards
        fun drawHorizontalAvenue(y: Float) {
            drawRect(asphaltRoad, Offset(0.11f * w, y - 11f), Size(0.78f * w, 22f))
            drawRect(sidewalkGrey, Offset(0.11f * w, y - 13f), Size(0.78f * w, 2f))
            drawRect(sidewalkGrey, Offset(0.11f * w, y + 11f), Size(0.78f * w, 2f))
            // Dashed white lines
            for (i in 0..24) {
                val lx = 0.12f * w + i * 16f
                drawLine(roadStripeWhite, Offset(lx, y), Offset(lx + 8f, y), 1.5f)
            }
        }

        drawHorizontalAvenue(h * 0.22f)
        drawHorizontalAvenue(h * 0.40f)
        drawHorizontalAvenue(h * 0.58f)

        // Yellow Centerline on North-South Avenue
        for (i in 0..28) {
            val ly = 0.08f * h + i * 16f
            drawLine(roadStripeYellow, Offset(cx, ly), Offset(cx, ly + 8f), 2f)
        }

        // White Zebra Crosswalks at intersections
        fun drawCrosswalk(x: Float, y: Float) {
            for (c in 0..4) {
                drawRect(roadStripeWhite, Offset(x - 12f + c * 5f, y - 4f), Size(3f, 8f))
            }
        }
        drawCrosswalk(cx, h * 0.22f - 14f)
        drawCrosswalk(cx, h * 0.40f - 14f)
        drawCrosswalk(cx, h * 0.58f + 14f)

        // Modern Street Lamps with warm light
        fun drawStreetLamp(lx: Float, ly: Float) {
            drawLine(steelGrey, Offset(lx, ly), Offset(lx, ly - 14f), 1.5f)
            drawLine(steelGrey, Offset(lx, ly - 14f), Offset(lx + 3f, ly - 14f), 1.5f)
            drawCircle(streetLampWarm, 3f, Offset(lx + 3f, ly - 13f))
        }
        drawStreetLamp(cx - 18f, h * 0.32f)
        drawStreetLamp(cx + 18f, h * 0.32f)
        drawStreetLamp(cx - 18f, h * 0.50f)
        drawStreetLamp(cx + 18f, h * 0.50f)
    }

    // 5. MODERN SECURITY WALL & BOOM GATE
    private fun DrawScope.drawModernSecurityWallAndGate(w: Float, h: Float, time: Float, isUnlocked: Boolean, level: Int) {
        val left = w * 0.11f
        val right = w * 0.89f
        val top = h * 0.06f
        val bottom = h * 0.88f
        val cx = w * 0.50f
        val gateW = 44f

        // Concrete Security Barrier Walls
        val wallC = Color(0xFFCBD5E1)
        drawLine(wallC, Offset(left, top), Offset(right, top), 7f)
        drawLine(wallC, Offset(left, top), Offset(left, bottom), 7f)
        drawLine(wallC, Offset(right, top), Offset(right, bottom), 7f)
        drawLine(wallC, Offset(left, bottom), Offset(cx - gateW / 2, bottom), 7f)
        drawLine(wallC, Offset(cx + gateW / 2, bottom), Offset(right, bottom), 7f)

        // Corner Modern Guard Towers
        fun drawModernWatchtower(tx: Float, ty: Float) {
            drawRect(Color(0xFF475569), Offset(tx - 10f, ty - 10f), Size(20f, 20f))
            drawRect(modernConcrete, Offset(tx - 8f, ty - 8f), Size(16f, 16f))
            drawRect(domeBlue, Offset(tx - 9f, ty - 12f), Size(18f, 4f)) // Flat blue roof
            drawCircle(Color(0xFF38BDF8), 2.5f, Offset(tx, ty - 5f)) // Spotlight
        }
        drawModernWatchtower(left, top)
        drawModernWatchtower(right, top)
        drawModernWatchtower(left, bottom)
        drawModernWatchtower(right, bottom)

        // South Security Checkpoint & Boom Barrier
        val gx = cx
        val gy = bottom
        // Guard booths on left & right of avenue
        drawRect(Color(0xFF1E3A8A), Offset(gx - gateW / 2 - 10f, gy - 12f), Size(12f, 18f))
        drawRect(Color(0xFF1E3A8A), Offset(gx + gateW / 2 - 2f, gy - 12f), Size(12f, 18f))
        // Glass windows on booths
        drawRect(modernGlassBlue, Offset(gx - gateW / 2 - 8f, gy - 8f), Size(8f, 6f))
        drawRect(modernGlassBlue, Offset(gx + gateW / 2, gy - 8f), Size(8f, 6f))

        // Red-and-White Striped Boom Barrier
        val barrierY = gy - 2f
        for (i in 0..4) {
            val c = if (i % 2 == 0) redMedical else Color.White
            drawRect(c, Offset(gx - 14f + i * 6f, barrierY), Size(6f, 3f))
        }
    }

    // (A) NEOCLASSICAL CAPITOL / CITY HALL (Town Hall)
    private fun DrawScope.drawModernCapitolCityHall(x: Float, y: Float, time: Float, level: Int) {
        drawOval(shadowColor, Offset(x - 38f, y + 26f), Size(76f, 16f))

        // Main White Concrete Grand Facade
        drawRect(modernConcrete, Offset(x - 32f, y - 8f), Size(64f, 36f))
        drawRect(Color(0xFFE2E8F0), Offset(x - 32f, y + 14f), Size(64f, 14f))

        // Classical Neoclassical Columns (Pillars)
        for (i in 0..5) {
            val colX = x - 25f + i * 10f
            drawRect(Color(0xFFCBD5E1), Offset(colX, y - 6f), Size(4f, 22f))
            drawCircle(Color(0xFF94A3B8), 2.5f, Offset(colX + 2f, y - 6f))
        }

        // Triangular Pediment over Portico
        val pediment = Path().apply {
            moveTo(x, y - 18f)
            lineTo(x - 28f, y - 6f)
            lineTo(x + 28f, y - 6f)
            close()
        }
        drawPath(pediment, modernConcrete)
        drawPath(pediment, Color(0xFF94A3B8), style = Stroke(1.5f))

        // Soaring Blue Copper Dome
        drawOval(domeBlue, Offset(x - 14f, y - 36f), Size(28f, 22f))
        drawRect(Color(0xFF1D4ED8), Offset(x - 16f, y - 16f), Size(32f, 6f)) // Dome drum
        // Dome Lantern & Spire
        drawRect(goldAccent, Offset(x - 2f, y - 42f), Size(4f, 8f))
        drawCircle(goldAccent, 2.5f, Offset(x, y - 42f))

        // Large Glass Windows
        for (i in 0..3) {
            drawRect(modernGlassBlue, Offset(x - 22f + i * 12f, y + 2f), Size(7f, 9f))
        }

        // Grand Entry Staircase & Red Carpets
        drawRect(Color(0xFFCBD5E1), Offset(x - 14f, y + 18f), Size(28f, 10f))
        drawRect(redMedical, Offset(x - 4f, y + 12f), Size(8f, 16f)) // Red welcome carpet
    }

    // (B) MODERN MEDICAL CENTER & HELIPAD (Hospital)
    private fun DrawScope.drawModernMedicalCenter(x: Float, y: Float, time: Float, level: Int) {
        drawOval(shadowColor, Offset(x - 34f, y + 24f), Size(68f, 14f))

        // Multi-Story White Glass Hospital Building
        drawRect(modernConcrete, Offset(x - 28f, y - 12f), Size(56f, 36f))
        // Large Reflective Glass Windows
        for (row in 0..2) {
            for (col in 0..4) {
                val wx = x - 24f + col * 10f
                val wy = y - 8f + row * 9f
                drawRect(modernGlassBlue, Offset(wx, wy), Size(7f, 6f))
            }
        }

        // Prominent Red Cross Emblem
        drawRect(redMedical, Offset(x - 2.5f, y - 22f), Size(5f, 12f))
        drawRect(redMedical, Offset(x - 6f, y - 18.5f), Size(12f, 5f))

        // Rooftop Helipad with Blue Medevac Helicopter
        drawCircle(Color(0xFF475569), 11f, Offset(x + 16f, y - 16f))
        drawCircle(Color.White, 9f, Offset(x + 16f, y - 16f), style = Stroke(1.5f))
        // "H" marking
        drawLine(Color.White, Offset(x + 13f, y - 20f), Offset(x + 13f, y - 12f), 1.5f)
        drawLine(Color.White, Offset(x + 19f, y - 20f), Offset(x + 19f, y - 12f), 1.5f)
        drawLine(Color.White, Offset(x + 13f, y - 16f), Offset(x + 19f, y - 16f), 1.5f)

        // Medevac Helicopter Rotor Blades (Spinning)
        val rotorAngle = (time * 8f) % (2 * PI.toFloat())
        val rLen = 14f
        val rx = x + 16f
        val ry = y - 16f
        drawLine(steelGrey, Offset(rx, ry), Offset(rx + cos(rotorAngle) * rLen, ry + sin(rotorAngle) * rLen), 1.5f)
        drawLine(steelGrey, Offset(rx, ry), Offset(rx - cos(rotorAngle) * rLen, ry - sin(rotorAngle) * rLen), 1.5f)

        // Parked Emergency Ambulance on ground
        drawRect(Color.White, Offset(x - 24f, y + 14f), Size(14f, 8f))
        drawRect(redMedical, Offset(x - 24f, y + 17f), Size(14f, 2f)) // Red side stripe
        val strobe = if ((time * 4f).toInt() % 2 == 0) Color.Red else Color.Blue
        drawCircle(strobe, 1.5f, Offset(x - 17f, y + 13f)) // Flashing beacon
    }

    // (C) INDUSTRIAL LUMBER & MATERIAL DEPOT (Penebang Kayu)
    private fun DrawScope.drawModernMaterialDepot(x: Float, y: Float, time: Float, level: Int) {
        drawOval(shadowColor, Offset(x - 30f, y + 18f), Size(60f, 14f))

        // Corrugated Steel Roof Warehouse
        drawRect(Color(0xFF94A3B8), Offset(x - 24f, y - 10f), Size(48f, 24f))
        drawRect(Color(0xFF1E3A8A), Offset(x - 26f, y - 18f), Size(52f, 10f)) // Blue metal roof

        // Large Neatly Stacked Timber Logs
        for (i in 0..2) {
            val ly = y + 8f + i * 5f
            drawRoundRect(Color(0xFF92400E), Offset(x - 26f, ly), Size(24f, 4f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f, 2f))
        }

        // Yellow Industrial Forklift
        drawRect(industrialYellow, Offset(x + 6f, y + 6f), Size(12f, 8f))
        drawRect(Color.Black, Offset(x + 7f, y + 12f), Size(4f, 4f)) // Wheel
        drawRect(Color.Black, Offset(x + 13f, y + 12f), Size(4f, 4f)) // Wheel
        // Forklift Mast & Fork Tines
        drawLine(steelGrey, Offset(x + 4f, y + 2f), Offset(x + 4f, y + 14f), 2f)
        drawLine(steelGrey, Offset(x, y + 12f), Offset(x + 4f, y + 12f), 2f)
    }

    // (D) MODERN INDUSTRIAL QUARRY (Pemecah Batu)
    private fun DrawScope.drawModernIndustrialQuarry(x: Float, y: Float, time: Float, level: Int) {
        drawOval(shadowColor, Offset(x - 30f, y + 20f), Size(60f, 14f))

        // Open-Pit Excavation Rocky Mountain Backing
        val pitPath = Path().apply {
            moveTo(x - 28f, y + 12f)
            lineTo(x - 22f, y - 18f)
            lineTo(x + 22f, y - 22f)
            lineTo(x + 28f, y + 12f)
            close()
        }
        drawPath(pitPath, Color(0xFF475569))

        // Industrial Conveyor Belt (Inclined)
        drawLine(steelGrey, Offset(x - 16f, y + 14f), Offset(x + 12f, y - 8f), 3.5f)
        // Conveyor support legs
        drawLine(steelGrey, Offset(x, y + 4f), Offset(x, y + 14f), 2f)

        // Yellow Hydraulic Excavator
        val exX = x + 8f
        val exY = y + 6f
        drawRect(industrialYellow, Offset(exX, exY), Size(14f, 8f))
        drawRect(Color(0xFF1E293B), Offset(exX - 1f, exY + 6f), Size(16f, 4f)) // Caterpillar treads
        // Excavator Boom & Bucket
        drawLine(industrialYellow, Offset(exX + 2f, exY), Offset(exX - 8f, exY - 8f), 2.5f)
        drawLine(industrialYellow, Offset(exX - 8f, exY - 8f), Offset(exX - 14f, exY - 2f), 2f)
        drawCircle(Color(0xFF334155), 3f, Offset(exX - 14f, exY - 2f)) // Bucket

        // Cylindrical Aggregate Storage Silos
        drawRect(Color(0xFFCBD5E1), Offset(x - 24f, y - 14f), Size(8f, 22f))
        drawOval(Color(0xFF94A3B8), Offset(x - 24f, y - 17f), Size(8f, 6f)) // Silo top
        drawRect(Color(0xFFCBD5E1), Offset(x - 14f, y - 14f), Size(8f, 22f))
        drawOval(Color(0xFF94A3B8), Offset(x - 14f, y - 17f), Size(8f, 6f))
    }

    // (E) HYDROPONIC GREENHOUSES & MODERN CATTLE RANCH
    private fun DrawScope.drawHydroponicGreenhouses(x: Float, y: Float, time: Float, level: Int) {
        drawOval(shadowColor, Offset(x - 30f, y + 18f), Size(60f, 12f))

        // Arched Glass Greenhouses with warm grow light glow
        fun drawGreenhouse(gx: Float, gy: Float) {
            drawRoundRect(modernGlassBlue.copy(alpha = 0.7f), Offset(gx, gy), Size(18f, 14f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f))
            drawRoundRect(modernConcrete, Offset(gx, gy), Size(18f, 14f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f), style = Stroke(1.5f))
            // Interior hydroponic green rows
            drawRect(Color(0xFF22C55E), Offset(gx + 3f, gy + 5f), Size(12f, 6f))
            // Warm grow lights
            drawCircle(Color(0xFFFEF08A).copy(alpha = 0.6f), 3f, Offset(gx + 9f, gy + 3f))
        }

        drawGreenhouse(x - 26f, y - 12f)
        drawGreenhouse(x - 4f, y - 12f)

        // Irrigated Crop Furrows & Apple Trees
        for (i in 0..2) {
            val cy = y + 8f + i * 5f
            drawRect(Color(0xFF166534), Offset(x - 26f, cy), Size(44f, 3.5f))
        }
    }

    private fun DrawScope.drawModernCattleRanch(x: Float, y: Float, time: Float, level: Int) {
        drawOval(shadowColor, Offset(x - 30f, y + 20f), Size(60f, 14f))

        // Fenced Pasture with Modern Wire Fence
        drawRect(Color(0xFF64748B), Offset(x - 26f, y - 6f), Size(52f, 26f), style = Stroke(1.5f))

        // Modern Red Gambrel Barn with Silver Metal Roof
        drawRect(Color(0xFFB91C1C), Offset(x + 4f, y - 14f), Size(22f, 18f))
        drawRect(Color(0xFFCBD5E1), Offset(x + 2f, y - 20f), Size(26f, 8f)) // Metal roof

        // Stainless Steel Feed Silo
        drawRect(Color(0xFFE2E8F0), Offset(x - 24f, y - 14f), Size(8f, 24f))
        drawOval(Color(0xFF94A3B8), Offset(x - 24f, y - 17f), Size(8f, 6f))

        // Holstein Dairy Cattle (Black & White)
        val cowBob = sin(time * 0.9f) * 1.5f
        drawOval(Color.White, Offset(x - 16f, y + 4f + cowBob), Size(15f, 9f))
        drawCircle(Color.Black, 3f, Offset(x - 13f, y + 6f + cowBob))
        drawCircle(Color.Black, 2.5f, Offset(x - 9f, y + 9f + cowBob))
        drawCircle(Color.White, 3.5f, Offset(x - 18f, y + 5f + cowBob)) // Head
    }

    // (F) MODERN MILITARY BASE (Barak Militer)
    private fun DrawScope.drawModernMilitaryBase(x: Float, y: Float, time: Float, level: Int) {
        drawOval(shadowColor, Offset(x - 30f, y + 20f), Size(60f, 14f))

        // Chainlink Security Fenced Compound
        drawRect(Color(0xFF475569), Offset(x - 26f, y - 10f), Size(52f, 32f), style = Stroke(1.5f))

        // Camouflaged Military Barracks Tents
        fun drawMilitaryTent(tx: Float, ty: Float) {
            val tent = Path().apply {
                moveTo(tx, ty - 12f)
                lineTo(tx - 12f, ty + 2f)
                lineTo(tx + 12f, ty + 2f)
                close()
            }
            drawPath(tent, militaryOlive)
            drawRect(Color(0xFF1E293B), Offset(tx - 3f, ty - 3f), Size(6f, 5f)) // Tent flap
        }
        drawMilitaryTent(x - 14f, y - 2f)
        drawMilitaryTent(x + 12f, y - 2f)

        // Helipad & Tactical Combat Helicopter
        val hx = x - 8f
        val hy = y + 14f
        drawCircle(Color(0xFF334155), 9f, Offset(hx, hy))
        drawCircle(Color.White, 7f, Offset(hx, hy), style = Stroke(1.5f))

        // Green Camo Tactical Supply Truck
        drawRect(militaryKhaki, Offset(x + 6f, y + 10f), Size(16f, 8f))
        drawRect(Color(0xFF1E293B), Offset(x + 8f, y + 16f), Size(4f, 4f)) // Wheel
        drawRect(Color(0xFF1E293B), Offset(x + 16f, y + 16f), Size(4f, 4f)) // Wheel
    }

    // (G) DOWNTOWN SHOPPING PLAZA (Market)
    private fun DrawScope.drawDowntownShoppingPlaza(x: Float, y: Float, time: Float, level: Int) {
        // Modern Retail Storefronts with Striped Canopies
        fun drawShop(sx: Float, sy: Float, awningColor: Color) {
            drawRect(modernConcrete, Offset(sx - 11f, sy - 8f), Size(22f, 20f))
            drawRect(modernGlassBlue, Offset(sx - 8f, sy + 2f), Size(16f, 8f)) // Storefront window
            // Striped Awning
            drawRect(awningColor, Offset(sx - 12f, sy - 6f), Size(24f, 7f))
            drawRect(Color.White, Offset(sx - 8f, sy - 6f), Size(4f, 7f))
            drawRect(Color.White, Offset(sx + 4f, sy - 6f), Size(4f, 7f))
        }

        drawShop(x - 24f, y - 4f, redMedical)
        drawShop(x + 24f, y - 4f, Color(0xFF1D4ED8))
        drawShop(x - 18f, y + 14f, industrialYellow)
        drawShop(x + 18f, y + 14f, Color(0xFF059669))
    }

    // Civic Statue & Fountain
    private fun DrawScope.drawCivicStatueAndFountain(x: Float, y: Float, time: Float) {
        // Modern Circular Tiered Fountain
        drawCircle(Color(0xFF64748B), 11f, Offset(x, y))
        drawCircle(poolCyan, 9f, Offset(x, y))
        // Central Bronze Monument Statue
        drawRect(Color(0xFF475569), Offset(x - 3f, y - 7f), Size(6f, 8f)) // Pedestal
        drawRect(Color(0xFFD97706), Offset(x - 2f, y - 16f), Size(4f, 9f)) // Bronze statue
        drawCircle(Color(0xFFD97706), 2f, Offset(x, y - 17f)) // Head
    }

    // (H) MODERN BISTRO & CAFE (Tavern)
    private fun DrawScope.drawModernBistroCafe(x: Float, y: Float, time: Float, level: Int) {
        drawOval(shadowColor, Offset(x - 26f, y + 18f), Size(52f, 14f))

        // Contemporary Glass & Wood Bistro Building
        drawRect(modernConcrete, Offset(x - 20f, y - 8f), Size(40f, 26f))
        drawRect(Color(0xFF78350F), Offset(x - 22f, y - 14f), Size(44f, 8f)) // Wood facade
        drawRect(modernGlassBlue, Offset(x - 16f, y - 2f), Size(32f, 12f)) // Glass dining area

        // Outdoor Patio with Red Cafe Parasols (Umbrellas)
        fun drawCafeParasol(px: Float, py: Float) {
            drawLine(steelGrey, Offset(px, py), Offset(px, py - 10f), 1.5f)
            drawCircle(redMedical, 6f, Offset(px, py - 10f))
            drawCircle(Color.White, 2f, Offset(px, py - 10f))
        }
        drawCafeParasol(x + 14f, y + 12f)
        drawCafeParasol(x + 22f, y + 14f)
    }

    // (I) SCIENCE DIMENSIONAL RIFT / ENERGY PORTAL (Dungeon)
    private fun DrawScope.drawScienceDimensionalRift(x: Float, y: Float, time: Float, maxLevel: Int) {
        drawOval(shadowColor, Offset(x - 28f, y + 18f), Size(56f, 12f))

        // Concentric High-Tech Stone Energy Platform
        drawCircle(Color(0xFF475569), 16f, Offset(x, y))
        drawCircle(Color(0xFF64748B), 13f, Offset(x, y), style = Stroke(2f))

        // Four Technological Obelisks with Neon Conduits
        for (i in 0..3) {
            val a = i * (PI.toFloat() / 2f)
            val ox = x + cos(a) * 16f
            val oy = y + sin(a) * 16f
            drawRect(Color(0xFF1E293B), Offset(ox - 2.5f, oy - 12f), Size(5f, 16f))
            drawRect(Color(0xFFA855F7), Offset(ox - 1f, oy - 10f), Size(2f, 12f)) // Purple neon conduit
        }

        // Swirling Purple Plasma Vortex Core
        val pulse = (sin(time * 3f) + 1f) * 0.25f
        drawCircle(Color(0xFF581C87), 9f + pulse * 2f, Offset(x, y))
        drawCircle(Color(0xFFA855F7), 6f, Offset(x, y))
        drawCircle(Color(0xFFFAF5FF), 3f, Offset(x, y))
    }

    // (J) INDUSTRIAL LOGISTICS DEPOT (Gudang)
    private fun DrawScope.drawModernLogisticsDepot(x: Float, y: Float, time: Float, level: Int) {
        drawOval(shadowColor, Offset(x - 20f, y + 12f), Size(40f, 10f))
        drawRect(Color(0xFF64748B), Offset(x - 16f, y - 4f), Size(32f, 16f))
        drawRect(Color(0xFF1E3A8A), Offset(x - 18f, y - 10f), Size(36f, 7f)) // Blue metal roof
        drawRect(industrialYellow, Offset(x - 4f, y + 4f), Size(8f, 8f)) // Cargo container
    }

    // (K) MODERN RESIDENTIAL APARTMENTS & SWIMMING POOLS (Rumah Warga)
    private fun DrawScope.drawModernResidentialApartments(w: Float, h: Float, state: GameState) {
        val level = state.getBuildingLevel("rumah_warga")

        fun drawApartment(ax: Float, ay: Float, hasPool: Boolean = false) {
            drawOval(shadowColor, Offset(ax - 14f, ay + 12f), Size(28f, 8f))
            // Multi-tier Apartment Block
            drawRect(modernConcrete, Offset(ax - 12f, ay - 14f), Size(24f, 26f))
            // Glass balconies & windows
            for (r in 0..2) {
                drawRect(modernGlassBlue, Offset(ax - 9f, ay - 10f + r * 7f), Size(18f, 4f))
            }
            // Rooftop Green Terrace
            drawRect(Color(0xFF15803D), Offset(ax - 10f, ay - 16f), Size(20f, 3f))

            // Luxury Swimming Pool beside building
            if (hasPool) {
                drawRoundRect(poolCyan, Offset(ax + 16f, ay - 2f), Size(14f, 10f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f, 3f))
                drawRoundRect(Color.White, Offset(ax + 16f, ay - 2f), Size(14f, 10f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f, 3f), style = Stroke(1.5f))
            }
        }

        drawApartment(w * 0.32f, h * 0.28f, hasPool = false)
        drawApartment(w * 0.68f, h * 0.28f, hasPool = false)
        if (level >= 2) {
            drawApartment(w * 0.34f, h * 0.44f, hasPool = true)
            drawApartment(w * 0.66f, h * 0.44f, hasPool = false)
        }
        if (level >= 3) {
            drawApartment(w * 0.34f, h * 0.58f, hasPool = false)
            drawApartment(w * 0.66f, h * 0.58f, hasPool = true)
        }
    }

    // (L) MOVING TRAFFIC & PEDESTRIANS
    private fun DrawScope.drawMovingTrafficAndCivilians(w: Float, h: Float, time: Float) {
        val cx = w * 0.50f

        // Driving Modern Sedan on Central Avenue
        val carSpeed = (time * 40f) % (0.76f * h)
        val carY = 0.10f * h + carSpeed
        drawRect(Color(0xFF2563EB), Offset(cx - 7f, carY), Size(6f, 12f)) // Blue sedan body
        drawRect(Color(0xFF93C5FD), Offset(cx - 6f, carY + 3f), Size(4f, 5f)) // Windshield
        drawCircle(Color(0xFFFEF08A), 1f, Offset(cx - 6f, carY + 11f)) // Headlight
        drawCircle(Color(0xFFFEF08A), 1f, Offset(cx - 2f, carY + 11f))

        // Red Compact Car driving up
        val car2Speed = ((time * 30f) % (0.76f * h))
        val car2Y = (0.84f * h) - car2Speed
        drawRect(redMedical, Offset(cx + 2f, car2Y), Size(5.5f, 10f))
        drawRect(Color(0xFF93C5FD), Offset(cx + 3f, car2Y + 2f), Size(3.5f, 4f))

        // Pedestrian walking on sidewalk
        val walk = sin(time * 2f) * 12f
        val px = cx - 16f
        val py = h * 0.45f + walk
        drawCircle(Color(0xFFFBCFE8), 1.5f, Offset(px, py - 5f))
        drawRect(Color(0xFF1E293B), Offset(px - 1f, py - 3f), Size(2f, 5f))
    }

    private fun DrawScope.drawEmptyPlot(x: Float, y: Float, rw: Float, rh: Float, seed: Int) {
        drawOval(grassParkDark.copy(alpha = 0.5f), Offset(x - rw / 2, y - rh / 2), Size(rw, rh))
        for (i in 0..3) {
            val fx = x - rw / 3 + ((seed * 19 + i * 31) % rw.toInt())
            val fy = y - rh / 3 + ((seed * 29 + i * 17) % rh.toInt())
            drawCircle(Color(0xFFFEF08A), 2f, Offset(fx, fy))
        }
    }
}
