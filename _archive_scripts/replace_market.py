import re

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'r') as f:
    content = f.read()

# Replace MarketTab
market_tab = """
fun MarketTab(state: GameState, viewModel: GameViewModel) {
    val marketLvl = state.getBuildingLevel("market")
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Market & Inventory", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("Koin Dungeon Anda: 💰 ${state.dungeonCoins}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
                    Text("Shard Token Anda: 💎 ${state.shardTokens}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                    
                    val inv = state.inventory
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Inventory Loot:", fontWeight = FontWeight.Bold)
                    Text("Rare: ${inv["item_rare"] ?: 0} | SR: ${inv["item_sr"] ?: 0} | SSR: ${inv["item_ssr"] ?: 0}")
                    Text("Potion HP: ${state.hpPotions} | Atk: ${inv["atk_potion"] ?: 0} | Def: ${inv["def_potion"] ?: 0}")
                    Text("Shard SR: ${inv["shard_sr"] ?: 0} | Shard SSR: ${inv["shard_ssr"] ?: 0} | Shard UR: ${inv["shard_ur"] ?: 0}")
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Jual Loot (Tukar ke Koin):", fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { viewModel.jualItemDungeon("item_rare") }, enabled = (inv["item_rare"] ?: 0) > 0) { Text("Jual Rare (+10)") }
                        Button(onClick = { viewModel.jualItemDungeon("item_sr") }, enabled = (inv["item_sr"] ?: 0) > 0) { Text("Jual SR (+50)") }
                        Button(onClick = { viewModel.jualItemDungeon("item_ssr") }, enabled = (inv["item_ssr"] ?: 0) > 0) { Text("Jual SSR (+150)") }
                    }
                }
            }
        }
        
        // Level 1 Market
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Toko Level 1", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
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
        
        // Level 2 Market
        if (marketLvl >= 2) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Toko Level 2", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        MarketItemButton(state, viewModel, "Paket 250 Kayu", "kayu_250", 450)
                        MarketItemButton(state, viewModel, "Paket 250 Batu", "batu_250", 450)
                        MarketItemButton(state, viewModel, "Paket 150 Daging", "daging_150", 350)
                        MarketItemButton(state, viewModel, "Paket 100 Besi", "besi_100", 250)
                        MarketItemButton(state, viewModel, "Paket 75 Emas", "emas_75", 250)
                    }
                }
            }
        }
        
        // Level 3 Market
        if (marketLvl >= 3) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Toko Level 3 & Spesial", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        MarketItemButton(state, viewModel, "Paket 500 Kayu", "kayu_500", 800)
                        MarketItemButton(state, viewModel, "Paket 500 Batu", "batu_500", 800)
                        MarketItemButton(state, viewModel, "Paket 300 Daging", "daging_300", 600)
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
    }
}

@Composable
fun MarketItemButton(state: GameState, viewModel: GameViewModel, text: String, itemId: String, cost: Long) {
    Button(
        onClick = { viewModel.buyMarketItem(itemId, cost) },
        enabled = state.dungeonCoins >= cost,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Text("$text ($cost Koin)")
    }
}
"""

content = re.sub(r'fun MarketTab\(state: GameState, viewModel: GameViewModel\) \{.*?(?=\n@Composable|\nfun|\Z)', market_tab, content, flags=re.DOTALL)

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'w') as f:
    f.write(content)
