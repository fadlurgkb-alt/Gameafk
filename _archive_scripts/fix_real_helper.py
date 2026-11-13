import re

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'r') as f:
    content = f.read()

helper = """
    private fun getHappinessAttackMultiplier(happiness: Long): Double {
        if (happiness > 100) {
            val extraHappiness = happiness - 100
            return 1.10 + (Math.floor(extraHappiness / 10.0) * 0.03)
        } else if (happiness < 100) {
            return 0.95
        }
        return 1.0
    }
    
    private fun gameTick() {
"""
content = content.replace("    private fun gameTick() {", helper)

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'w') as f:
    f.write(content)
