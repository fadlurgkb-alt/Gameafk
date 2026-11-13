with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'r') as f:
    content = f.read()

bad_braces = """                    }
                }
            }
        }
            }
        }
    }
}
@Composable"""

good_braces = """                    }
                }
            }
        }
    }
}

@Composable"""

content = content.replace(bad_braces, good_braces)
with open('app/src/main/java/com/example/ui/game/GameScreen.kt', 'w') as f:
    f.write(content)
