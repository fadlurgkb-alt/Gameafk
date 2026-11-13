import re

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'r') as f:
    lines = f.readlines()

for i, line in enumerate(lines):
    if line.strip().startswith('TalentNode('):
        # type check
        if '"skill"' in line and '"berburu"' not in line:
            # increase zenithCost by 80%
            match = re.search(r'TalentNode\("([^"]+)", "([^"]+)", ([0-9.]+)', line)
            if match:
                old_cost = float(match.group(3))
                new_cost = old_cost * 1.8
                line = line[:match.start(3)] + str(new_cost) + line[match.end(3):]
        if '"bangunan"' in line:
            # increase resources by 10%
            def repl(m):
                val = int(m.group(1))
                new_val = int(val * 1.1)
                return f'to {new_val}L'
            line = re.sub(r'to ([0-9]+)L', repl, line)
        lines[i] = line

with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'w') as f:
    f.writelines(lines)
