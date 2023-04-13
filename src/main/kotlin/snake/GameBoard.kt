package snake

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import snake.model.Game
import snake.ui.LostDialog
import snake.ui.PlayField
import snake.ui.Ui

@Composable
@Preview
fun GameBoard(
   game: Game,
) {
   val resetGame = {
      gameFlow.value = Game.newGame()
   }

   if (game.isLost) {
      LostDialog(game.score, resetGame)
   }

   MaterialTheme {
      Column(
         modifier = Modifier.padding(16.dp),
         verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
         Ui(game, resetGame)
         PlayField(game)
      }
   }
}