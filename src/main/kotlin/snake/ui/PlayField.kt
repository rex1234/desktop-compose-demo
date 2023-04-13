package snake.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import snake.model.Game
import snake.model.Point
import snake.model.Snake
import snake.squareSize

@Composable
fun PlayField(game: Game) {
   Box(
      modifier = Modifier
         .width((game.config.size.x * squareSize).dp)
         .height((game.config.size.y * squareSize).dp)
         .background(Color(0xff5fb356))
         .border(BorderStroke(2.dp, Color.Black))
   ) {
      Snake(game.snake)
      Apple(game.apple)
   }
}

@Composable
private fun Snake(snake: Snake) {
   snake.body.forEachIndexed { index, point ->
      SnakePart(point, isHead = index == 0)
   }
}

@Composable
private fun SnakePart(point: Point, isHead: Boolean) {
   Box(
      modifier = Modifier
         .padding(point.padding)
         .size(squareSize.dp)
         .padding(1.dp)
         .background(Color.Black),
   ) {
      if (isHead) {
         Box(
            modifier = Modifier
               .padding(4.dp)
               .fillMaxSize()
               .background(Color.Yellow)
         )
      }
   }
}

@Composable
private fun Apple(point: Point) {
   Box(
      modifier = Modifier
         .padding(point.padding)
         .size(squareSize.dp)
         .padding(1.dp)
         .background(Color.Red)
         .border(2.dp, Color(0xFF751913)),
   )
}

private val Point.padding: PaddingValues
   get() = PaddingValues(start = (x * 16).dp, top = (y * 16).dp)
