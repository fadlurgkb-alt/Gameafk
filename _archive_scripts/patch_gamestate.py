import re

with open('app/src/main/java/com/example/data/GameState.kt', 'r') as f:
    content = f.read()

target = """    val garrisonedHero: String = "","""

new_code = """    val garrisonedHero: String = "",
    val garrisonedHeroesStr: String = "",
    val garrisonedPasukan: Long = 0L,
    val woundedPasukan: Long = 0L,
    val isRaidBattleActive: Boolean = false,
    val raidEnemyHp: Long = 0L,
    val raidEnemyMaxHp: Long = 0L,
    val raidPlayerHp: Long = 0L,"""

content = content.replace(target, new_code)

target_getter = """    val ownedCommanders: List<String>
        get() = ownedCommandersStr.split(",").filter { it.isNotEmpty() }"""

new_getter = """    val ownedCommanders: List<String>
        get() = ownedCommandersStr.split(",").filter { it.isNotEmpty() }
        
    val garrisonedHeroes: List<String>
        get() = garrisonedHeroesStr.split(",").filter { it.isNotEmpty() }"""

content = content.replace(target_getter, new_getter)

with open('app/src/main/java/com/example/data/GameState.kt', 'w') as f:
    f.write(content)
