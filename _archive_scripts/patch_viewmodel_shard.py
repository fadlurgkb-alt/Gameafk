import re

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'r') as f:
    content = f.read()

content = content.replace('"shard_sr_random" -> inv["shard_sr"] = (inv["shard_sr"] ?: 0) + 1', '"shard_r_random" -> inv["shard_r"] = (inv["shard_r"] ?: 0) + 1\n                "shard_sr_random" -> inv["shard_sr"] = (inv["shard_sr"] ?: 0) + 1')

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'w') as f:
    f.write(content)
