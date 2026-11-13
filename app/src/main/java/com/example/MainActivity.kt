package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.data.AppDatabase
import com.example.data.GameRepository
import com.example.ui.game.GameScreen
import com.example.ui.game.GameViewModel
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    val database = AppDatabase.getDatabase(this)
    val repository = GameRepository(database.gameStateDao())
    val factory = GameViewModel.Factory(repository)
    val viewModel = ViewModelProvider(this, factory)[GameViewModel::class.java]

    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        GameScreen(viewModel = viewModel)
      }
    }
  }
}
