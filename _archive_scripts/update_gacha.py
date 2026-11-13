import re

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'r') as f:
    content = f.read()

replacement = """                    if (stars < 6) {
                        currentCommanders[existingIdx] = "$heroName:${stars + 1}"
                        _gachaResult.value = "Ascension! $heroName naik ke Bintang ${stars + 1}"
                    } else {
                        shardTokensToAdd = when {
                            heroName.contains("(Mythical)") || heroName.contains("(Legend)") -> 20
                            heroName.contains("(Very Rare)") -> 10
                            heroName.contains("(Rare)") -> 1
                            else -> 1
                        }
                        _gachaResult.value = "$heroName (Maksimal B6) -> Dikonversi menjadi $shardTokensToAdd Shard Token!"
                    }"""

target = """                    if (stars < 6) {
                        currentCommanders[existingIdx] = "$heroName:${stars + 1}"
                        _gachaResult.value = "Ascension! $heroName naik ke Bintang ${stars + 1}"
                    } else {
                        shardTokensToAdd = 5
                        _gachaResult.value = "$heroName (Maksimal B6) -> Dikonversi menjadi 5 Shard Token!"
                    }"""

content = content.replace(target, replacement)

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'w') as f:
    f.write(content)
