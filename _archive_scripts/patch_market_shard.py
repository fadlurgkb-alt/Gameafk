import re

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'r') as f:
    content = f.read()

# Add shard_r to Toko Zenith Level 3
toko_target = """                        MarketItemButton(state, viewModel, "Beli Shard SR", "shard_sr", 200)"""
toko_replacement = """                        MarketItemButton(state, viewModel, "Beli Shard Rare", "shard_r", 50)
                        MarketItemButton(state, viewModel, "Beli Shard SR", "shard_sr", 200)"""
content = content.replace(toko_target, toko_replacement)

# Add shard_r to Token exchange
toko_token = """                        Button(onClick = { viewModel.exchangeShardToken("shard_sr_random", 10) }, enabled = state.shardTokens >= 10, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) { Text("1 Random Shard SR (10 💎)") }"""
toko_token_rep = """                        Button(onClick = { viewModel.exchangeShardToken("shard_r_random", 3) }, enabled = state.shardTokens >= 3, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) { Text("1 Random Shard Rare (3 💎)") }
                        Button(onClick = { viewModel.exchangeShardToken("shard_sr_random", 10) }, enabled = state.shardTokens >= 10, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) { Text("1 Random Shard SR (10 💎)") }"""
content = content.replace(toko_token, toko_token_rep)

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'w') as f:
    f.write(content)
