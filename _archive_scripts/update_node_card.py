import re

with open("app/src/main/java/com/example/ui/game/GameScreen.kt", "r") as f:
    content = f.read()

# Update signature
old_sig = """@Composable
fun NodeCard(
    title: String,
    desc: String,
    costStr: String,
    isUnlocked: Boolean,
    isUpgradable: Boolean,
    canAfford: Boolean,
    onClick: () -> Unit,"""

new_sig = """@Composable
fun NodeCard(
    title: String,
    desc: String,
    costStr: String,
    isUnlocked: Boolean,
    isUpgradable: Boolean,
    canAfford: Boolean,
    onClick: () -> Unit,
    isUpgrading: Boolean = false,
    upgradeEndTime: Long = 0L,"""

content = content.replace(old_sig, new_sig)

# Add logic for countdown and disabled button
# We need to find the "Beli" / "Tingkatkan" button part.

# In NodeCard, there's a button at the bottom:
#                     if (!isUnlocked || isUpgradable) {
#                        Button(
#                            onClick = onClick,
#                            enabled = canAfford && lockMessage == null,

old_btn = """                    if (!isUnlocked || isUpgradable) {
                        Button(
                            onClick = onClick,
                            enabled = canAfford && lockMessage == null,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isHighlighted) Color(0xFFFBBF24).copy(alpha = highlightAlpha) else MaterialTheme.colorScheme.primary,
                                contentColor = if (isHighlighted) Color.Black else MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text(if (isUnlocked) "Tingkatkan" else "Beli")
                        }
                    }"""

new_btn = """                    if (!isUnlocked || isUpgradable) {
                        if (isUpgrading) {
                            var timeLeft by remember { mutableStateOf(kotlin.math.max(0, ((upgradeEndTime - System.currentTimeMillis()) / 1000).toInt())) }
                            LaunchedEffect(upgradeEndTime) {
                                while (timeLeft > 0) {
                                    kotlinx.coroutines.delay(1000)
                                    timeLeft = kotlin.math.max(0, ((upgradeEndTime - System.currentTimeMillis()) / 1000).toInt())
                                }
                            }
                            Button(
                                onClick = {},
                                enabled = false,
                                colors = ButtonDefaults.buttonColors(disabledContainerColor = Color.Gray, disabledContentColor = Color.White)
                            ) {
                                Text("🚧 Sedang Dibangun... ${timeLeft}s")
                            }
                        } else {
                            Button(
                                onClick = onClick,
                                enabled = canAfford && lockMessage == null,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isHighlighted) Color(0xFFFBBF24).copy(alpha = highlightAlpha) else MaterialTheme.colorScheme.primary,
                                    contentColor = if (isHighlighted) Color.Black else MaterialTheme.colorScheme.onPrimary
                                )
                            ) {
                                Text(if (isUnlocked) "Tingkatkan" else "Beli")
                            }
                        }
                    }"""

content = content.replace(old_btn, new_btn)

# Pass isUpgrading and upgradeEndTime to NodeCard from NodeList
# Oh wait, we already added them to the NodeList loop, but didn't pass them in the NodeCard call.
# Let's fix the NodeCard call in NodeList.

old_call = """            NodeCard(
                title = displayTitle,
                desc = displayDesc,
                costStr = costStr,
                isUnlocked = isUnlocked,
                isUpgradable = isBangunan,
                canAfford = canAfford,
                onClick = { viewModel.buyNode(node.id) },
                actionText = actionText,
                onActionClick = onActionClick,
                secondaryActionText = secondaryActionText,
                onSecondaryActionClick = onSecondaryActionClick,
                isHighlighted = isHighlighted,
                lockMessage = lockMessage
            )"""

new_call = """            NodeCard(
                title = displayTitle,
                desc = displayDesc,
                costStr = costStr,
                isUnlocked = isUnlocked,
                isUpgradable = isBangunan,
                canAfford = canAfford,
                onClick = { viewModel.buyNode(node.id) },
                isUpgrading = isUpgrading,
                upgradeEndTime = upgradeEndTime,
                actionText = actionText,
                onActionClick = onActionClick,
                secondaryActionText = secondaryActionText,
                onSecondaryActionClick = onSecondaryActionClick,
                isHighlighted = isHighlighted,
                lockMessage = lockMessage
            )"""

content = content.replace(old_call, new_call)

with open("app/src/main/java/com/example/ui/game/GameScreen.kt", "w") as f:
    f.write(content)

