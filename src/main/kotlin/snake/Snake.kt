package snake

import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.key
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import snake.model.Game
import snake.model.Snake

const val gameSpeed = 100L
const val squareSize = 16
val gameFlow = MutableStateFlow(Game.newGame())

@Composable
fun Snake(
   onCloseRequest: () -> Unit,
) {
   val game: Game by gameFlow.collectAsState()

   var lastKeyEvent: KeyEvent? by remember { mutableStateOf(null) }

   val windowState = rememberWindowState()
   windowState.size = gameFlow.value.config.size.let {
      DpSize((it.x * squareSize).dp + 48.dp, (it.x * squareSize).dp + 140.dp)
   }

   val coroutineScope = rememberCoroutineScope()

   coroutineScope.launch {
      while (true) {
         loop(lastKeyEvent)
         delay(gameSpeed)
      }
   }

   Window(
      title = "Snake",
      onCloseRequest = onCloseRequest,
      state = windowState,
      onKeyEvent = {
         lastKeyEvent = it
         true
      }
   ) {
      GameBoard(game)
   }
}

private fun loop(lastKeyEvent: KeyEvent?) {
   gameFlow.value = gameFlow.value.tick(lastKeyEvent?.key?.direction)
}

@OptIn(ExperimentalComposeUiApi::class)
private val Key.direction: Snake.Direction?
   get() = when (this) {
      Key.DirectionUp -> Snake.Direction.Up
      Key.DirectionDown -> Snake.Direction.Down
      Key.DirectionLeft -> Snake.Direction.Right
      Key.DirectionRight -> Snake.Direction.Left
      else -> null
   }