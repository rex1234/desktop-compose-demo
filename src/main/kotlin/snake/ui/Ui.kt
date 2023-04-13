package snake.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import snake.model.Game

@Composable
fun Ui(
   game: Game,
   onReset: () -> Unit,
) {
   Row(
      verticalAlignment = Alignment.CenterVertically,
   ) {
      Text(
         modifier = Modifier
            .background(Color.Black)
            .border(2.dp, Color.White)
            .padding(16.dp),
         text = "Score: ${game.score}",
         fontSize = 20.sp,
         color = Color.White,
      )

      Button(
         modifier = Modifier.height(IntrinsicSize.Max),
         onClick = onReset,
      ) {
         Text("Restart")
      }
   }
}
