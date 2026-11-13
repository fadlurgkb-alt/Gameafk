import re

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'r') as f:
    content = f.read()

combine_fn = """
    fun combineShards(tier: String) {
        _uiState.update { state ->
            val inv = state.inventory.toMutableMap()
            val (shardKey, reqCount, resultPool) = when(tier) {
                "r" -> Triple("shard_r", 20, listOf("Arthur (Rare)", "Leonidas (Rare)", "Robin (Rare)", "Spartacus (Rare)"))
                "sr" -> Triple("shard_sr", 50, listOf("Joan of Arc (Very Rare)", "Miyamoto (Very Rare)", "Merlin (Very Rare)"))
                "ssr" -> Triple("shard_ssr", 100, listOf("Gilgamesh (Legend)", "Odin (Legend)", "Amaterasu (Legend)"))
                "ur" -> Triple("shard_ur", 150, listOf("Zeus (Mythical)", "Ra (Mythical)", "Sun Wukong (Mythical)"))
                else -> return@update state
            }
            
            val count = inv[shardKey] ?: 0
            if (count < reqCount) {
                _alertMessage.value = "❌ Gagal! Anda membutuhkan $reqCount Shard (Saat ini: $count)."
                return@update state
            }
            
            inv[shardKey] = count - reqCount
            
            val newHero = resultPool.random()
            val cmds = state.ownedCommanders.toMutableList()
            val idx = cmds.indexOfFirst { it.startsWith(newHero) }
            if (idx != -1) {
                val p = cmds[idx].split(":")
                val currentStars = if (p.size > 1) p[1].toIntOrNull() ?: 1 else 1
                cmds[idx] = "$newHero:${currentStars + 1}"
                _alertMessage.value = "🎉 Combine Berhasil! Duplikat $newHero didapat. Bintang naik jadi ${currentStars + 1}!"
            } else {
                cmds.add("$newHero:1")
                _alertMessage.value = "🎉 Combine Berhasil! Anda mendapatkan Hero Baru: $newHero!"
            }
            
            state.copy(
                inventoryStr = inv.map { "${it.key}:${it.value}" }.joinToString(","),
                ownedCommandersStr = cmds.joinToString(",")
            )
        }
    }
"""

if "fun combineShards" not in content:
    content = content.replace("    fun exchangeShardToken(target: String, costTokens: Int) {", combine_fn + "\n    fun exchangeShardToken(target: String, costTokens: Int) {")

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'w') as f:
    f.write(content)
