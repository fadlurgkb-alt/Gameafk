import re

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'r') as f:
    content = f.read()

# Fix literal newlines
content = content.replace('Besi: ${state.besi}/${state.maxStorage} | Emas: ${state.emas}/${state.maxStorage}")', '\\nBesi: ${state.besi}/${state.maxStorage} | Emas: ${state.emas}/${state.maxStorage}")')
content = content.replace('Text("Kayu: ${state.kayu}/${state.maxStorage} | Batu: ${state.batu}/${state.maxStorage}\n', 'Text("Kayu: ${state.kayu}/${state.maxStorage} | Batu: ${state.batu}/${state.maxStorage}')

content = content.replace('Titanium: ${state.titanium}/${state.maxStorage} | Uranium: ${state.uranium}/${state.maxStorage}")', '\\nTitanium: ${state.titanium}/${state.maxStorage} | Uranium: ${state.uranium}/${state.maxStorage}")')
content = content.replace('Text("Oil: ${state.oil}/${state.maxStorage} | Listrik: ${state.listrik}/${state.maxStorage}\n', 'Text("Oil: ${state.oil}/${state.maxStorage} | Listrik: ${state.listrik}/${state.maxStorage}')

content = content.replace('Gandum: ${state.gandum}/${state.maxStorage} | Roti: ${state.roti}/${state.maxStorage}\nKalengan: ${state.kalengan}/${state.maxStorage} | Ikan: ${state.ikan}/${state.maxStorage}")', '\\nGandum: ${state.gandum}/${state.maxStorage} | Roti: ${state.roti}/${state.maxStorage}\\nKalengan: ${state.kalengan}/${state.maxStorage} | Ikan: ${state.ikan}/${state.maxStorage}")')
content = content.replace('Text("Daging: ${state.daging}/${state.maxStorage} | Berry: ${state.berry}/${state.maxStorage}\n', 'Text("Daging: ${state.daging}/${state.maxStorage} | Berry: ${state.berry}/${state.maxStorage}')


with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'w') as f:
    f.write(content)
