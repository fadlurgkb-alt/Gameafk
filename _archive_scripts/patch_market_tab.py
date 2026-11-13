import re

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'r') as f:
    content = f.read()

# Add CoinShopItem struct
coin_shop_struct = """
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
"""

if "val GAME_COIN_SHOP = listOf" not in content:
    content = content.replace("val allNodes = listOf(", coin_shop_struct.strip() + "\n\nval allNodes = listOf(")

# Rewrite Market tab
market_tab_regex = r'"Market" -> \{\n\s*val marketLvl = state\.getBuildingLevel\("market"\).*?// Komandan Suku'

market_tab_replacement = """
"Market" -> {
    val marketLvl = state.getBuildingLevel("market")
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFF451A03))) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("🛡️ Raid Shop (Toko Koin Game)", fontWeight = FontWeight.Bold, color = Color(0xFFFBBF24), style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    GAME_COIN_SHOP.filter { it.minMarketLvl <= marketLvl }.forEach { item ->
                        val owned = state.inventory[item.id] ?: 0
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF78350F))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("${item.name} (Dimiliki: $owned)", fontWeight = FontWeight.Bold, color = Color.White)
                                Text(item.desc, style = MaterialTheme.typography.bodySmall, color = Color(0xFFFDE68A))
                                Spacer(modifier = Modifier.height(6.dp))
                                Button(
                                    onClick = { viewModel.buyCoinShopItem(item.id, item.cost) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706))
                                ) {
                                    Text("Beli - 🪙 ${item.cost}")
                                }
                            }
                        }
                    }
                }
            }
        }
        
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Toko Zenith Level 1", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    MarketItemButton(state, viewModel, "Potion HP Lv1", "hp_potion", 200)
                    MarketItemButton(state, viewModel, "Potion Atk Lv1", "atk_potion", 200)
                    MarketItemButton(state, viewModel, "Potion Def Lv1", "def_potion", 250)
                    MarketItemButton(state, viewModel, "Paket 100 Kayu", "kayu_100", 200)
                    MarketItemButton(state, viewModel, "Paket 100 Batu", "batu_100", 200)
                    MarketItemButton(state, viewModel, "Paket 100 Daging", "daging_100", 250)
                }
            }
        }
        
        if (marketLvl >= 2) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Toko Zenith Level 2", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        MarketItemButton(state, viewModel, "Paket 250 Kayu", "kayu_250", 450)
                        MarketItemButton(state, viewModel, "Paket 250 Batu", "batu_250", 450)
                        MarketItemButton(state, viewModel, "Paket 250 Besi", "besi_250", 500)
                        MarketItemButton(state, viewModel, "Paket 250 Emas", "emas_250", 600)
                    }
                }
            }
        }
        if (marketLvl >= 3) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Toko Zenith Level 3", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        MarketItemButton(state, viewModel, "Blueprint Senjata", "item_rare", 2000)
                    }
                }
            }
        }
        if (marketLvl >= 4) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Toko Zenith Level 4", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        MarketItemButton(state, viewModel, "Inti AI", "item_sr", 8000)
                        MarketItemButton(state, viewModel, "Partikel Nano", "item_ssr", 30000)
                    }
                }
            }
        }
    }
}
// Komandan Suku"""

content = re.sub(market_tab_regex, market_tab_replacement, content, flags=re.DOTALL)

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'w') as f:
    f.write(content)
