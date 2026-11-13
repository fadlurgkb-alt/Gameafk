import re

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'r') as f:
    content = f.read()

market_check = """            if (node.type == "bangunan" && id == "market") {"""

temple_check = """            if (node.type == "bangunan" && id == "temple") {
                if (currentLevel >= 5) {
                    _alertMessage.value = "❌ Gagal! Kuil sudah mencapai level maksimal (Lv 5)."
                    return@update currentState
                }
                val thLevel = currentState.getBuildingLevel("town_hall")
                val reqTh = (currentLevel + 1) * 2 - 1
                if (thLevel < reqTh) {
                    _alertMessage.value = "❌ Gagal! Kuil Lv ${currentLevel + 1} membutuhkan Town Hall Lv $reqTh."
                    return@update currentState
                }
            }

            if (node.type == "bangunan" && id == "market") {"""

content = content.replace(market_check, temple_check)

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'w') as f:
    f.write(content)
