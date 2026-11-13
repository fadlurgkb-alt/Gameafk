with open('app/src/main/java/com/example/data/AppDatabase.kt', 'r') as f:
    content = f.read()

content = content.replace("version = 10", "version = 11")
content = content.replace(".fallbackToDestructiveMigration()", ".fallbackToDestructiveMigration(dropAllTables = true)")

with open('app/src/main/java/com/example/data/AppDatabase.kt', 'w') as f:
    f.write(content)
