import re

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'r') as f:
    content = f.read()

# Replace the body of MarketTab
regex = r'// Exotic Market \(Game Coins\).*?if \(marketLvl >= 3\) \{.*?\}\s*\}\s*\}'

replacement = """
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
                        MarketItemButton(state, viewModel, "Beli Shard SR", "shard_sr", 200)
                        MarketItemButton(state, viewModel, "Beli Shard SSR", "shard_ssr", 500)
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Toko Shard Token (Tukar 💎)", fontWeight = FontWeight.Bold)
                        Button(onClick = { viewModel.exchangeShardToken("shard_sr_random", 10) }, enabled = state.shardTokens >= 10, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) { Text("1 Random Shard SR (10 💎)") }
                        Button(onClick = { viewModel.exchangeShardToken("shard_ssr_random", 25) }, enabled = state.shardTokens >= 25, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) { Text("1 Random Shard SSR (25 💎)") }
                        Button(onClick = { viewModel.exchangeShardToken("shard_ur_random", 150) }, enabled = state.shardTokens >= 150, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) { Text("1 Random Shard UR (150 💎)") }
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
"""

content = re.sub(regex, replacement.strip(), content, flags=re.DOTALL)

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'w') as f:
    f.write(content)
