package snake.ui

import androidx.compose.foundation.layout.width
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LostDialog(
   score: Int,
   onReset: () -> Unit,
) {
   AlertDialog(
      modifier = Modifier.width(300.dp),
      onDismissRequest = {},
      title = { Text("You have lost") },
      confirmButton = {
         Button(
            onClick = onReset,
         ) { Text("Restart") }
      },
      text = { Text("Your score was: $score") }
   )
}
