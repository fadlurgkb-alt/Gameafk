import re

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'r') as f:
    content = f.read()

target = """                if (barakLevel >= 10) {
                    costDaging = 500L
                    costKayu = 0L
                    costBesi = 0L
                    costUranium = 100L
                    costListrik = 50L
                } else if (barakLevel >= 5) {
                    costDaging = 150L
                    costKayu = 0L
                    costBesi = 50L
                }
                
                if (state.daging >= costDaging && state.kayu >= costKayu && state.besi >= costBesi && state.uranium >= costUranium && state.listrik >= costListrik) {
                    val newState = state.copy(
                        daging = state.daging - costDaging,
                        kayu = state.kayu - costKayu,
                        besi = state.besi - costBesi,
                        uranium = state.uranium - costUranium,
                        listrik = state.listrik - costListrik,
                        currentPasukan = state.currentPasukan + 1
                    )"""

new_code = """                if (barakLevel >= 10) {
                    costDaging = 500L
                    costKayu = 0L
                    costBesi = 0L
                    costUranium = 100L
                    costListrik = 50L
                } else if (barakLevel >= 5) {
                    costDaging = 150L
                    costKayu = 0L
                    costBesi = 50L
                }
                
                var natCostBarak = 1.0
                if (state.selectedNationality == "Mongol") natCostBarak -= 0.30
                
                costDaging = (costDaging * natCostBarak).toLong()
                costKayu = (costKayu * natCostBarak).toLong()
                costBesi = (costBesi * natCostBarak).toLong()
                costUranium = (costUranium * natCostBarak).toLong()
                costListrik = (costListrik * natCostBarak).toLong()

                if (state.daging >= costDaging && state.kayu >= costKayu && state.besi >= costBesi && state.uranium >= costUranium && state.listrik >= costListrik) {
                    val newState = state.copy(
                        daging = state.daging - costDaging,
                        kayu = state.kayu - costKayu,
                        besi = state.besi - costBesi,
                        uranium = state.uranium - costUranium,
                        listrik = state.listrik - costListrik,
                        currentPasukan = state.currentPasukan + 1
                    )"""

content = content.replace(target, new_code)

with open('app/src/main/java/com/example/ui/game/GameViewModel.kt', 'w') as f:
    f.write(content)
