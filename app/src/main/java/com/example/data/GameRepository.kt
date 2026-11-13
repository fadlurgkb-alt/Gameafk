package com.example.data

import kotlinx.coroutines.flow.Flow

class GameRepository(private val gameStateDao: GameStateDao) {
    val gameState: Flow<GameState?> = gameStateDao.getGameState()

    suspend fun saveGameState(gameState: GameState) {
        gameStateDao.insertGameState(gameState)
    }
}
