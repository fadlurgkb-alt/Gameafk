import re
with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'r') as f:
    content = f.read()

# I want to remove the extra braces after `if (marketLvl >= 4)` section.
# We have:
#         if (marketLvl >= 4) {
#             item {
# ...
#             }
#         }
#             }
#         }
#     }
# }
# @Composable

# The extra braces seem to be `} }` just before the proper `} }`.

# I will find the exact string to replace.
pattern = r"                        MarketItemButton\(state, viewModel, \"Partikel Nano\", \"item_ssr\", 30000\)\n                    \}\n                \}\n            \}\n        \}\n            \}\n        \}\n    \}\n\}\n\n@Composable"
replacement = "                        MarketItemButton(state, viewModel, \"Partikel Nano\", \"item_ssr\", 30000)\n                    }\n                }\n            }\n        }\n    }\n}\n\n@Composable"

content = re.sub(pattern, replacement, content, flags=re.DOTALL)
with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'w') as f:
    f.write(content)
