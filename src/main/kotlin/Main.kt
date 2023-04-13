@file:OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.loadSvgPainter
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import snake.Snake
import java.awt.FileDialog
import java.awt.Frame
import java.io.IOException
import java.net.URL

val kiwiColor = Color(0xff01a991)

private data class AppState(
   val isDemoDialogShown: Boolean = false,
   val isMaterialDialogShown: Boolean = false,
   val isFileDialogShown: Boolean = false,
   val isSnakeWindowVisible: Boolean = false,
   val isWindowUndecorated: Boolean = false,
)

fun main() = application {
   MaterialTheme {
      var appState by remember { mutableStateOf(AppState()) }

      val trayState = rememberTrayState()
      val notification = rememberNotification("Notification", "Hi Kiwi!")

      val windowState = rememberWindowState(width = 800.dp, height = 700.dp)
      var lastKeyEvent by mutableStateOf<KeyEvent?>(null)

      AppTray(trayState, ::exitApplication)

      Window(
         onCloseRequest = ::exitApplication,
         title = "Compose demo",
         state = windowState,
         resizable = true,
         onKeyEvent = {
            lastKeyEvent = it
            false
         },
      ) {
         AppMenu(
            onPlaySnake = { appState = appState.copy(isSnakeWindowVisible = true) },
            onOpenFileDialog = { appState = appState.copy(isFileDialogShown = true) },
            onExit = ::exitApplication,
         )

         if (appState.isDemoDialogShown) {
            DemoDialog(onCloseRequest = { appState = appState.copy(isDemoDialogShown = false) })
         }
         if (appState.isMaterialDialogShown) {
            MaterialDialog(onCloseRequest = { appState = appState.copy(isMaterialDialogShown = false) })
         }
         if (appState.isFileDialogShown) {
            FileDialog(onCloseRequest = { appState = appState.copy(isFileDialogShown = false) })
         }
         if (appState.isSnakeWindowVisible) {
            Snake(onCloseRequest = { appState = appState.copy(isSnakeWindowVisible = false) })
         }

         Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
         ) {
            Column(
               verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
               InfoPanel(windowState, lastKeyEvent)
               DemoInput()
               Column(
                  modifier = Modifier.width(IntrinsicSize.Max)
               ) {
                  Button(
                     modifier = Modifier.fillMaxWidth(),
                     onClick = { appState = appState.copy(isDemoDialogShown = true) }
                  ) {
                     Text("Show dialog")
                  }
                  Button(
                     modifier = Modifier.fillMaxWidth(),
                     onClick = { appState = appState.copy(isMaterialDialogShown = true) }
                  ) {
                     Text("Show material dialog")
                  }
                  TooltipArea(
                     tooltip = {
                        Surface(
                           modifier = Modifier.shadow(4.dp),
                           shape = RoundedCornerShape(4.dp)
                        ) {
                           Text(
                              modifier = Modifier.padding(8.dp),
                              text = "This button will display a native file dialog"
                           )
                        }
                     }
                  ) {
                     OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { appState = appState.copy(isFileDialogShown = true) }
                     ) {
                        Text("Show file dialog")
                     }
                  }
               }
            }

            val density = LocalDensity.current
            Column(
               verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
               Button(onClick = { trayState.sendNotification(notification) }) {
                  Text("Show notification")
               }
               Text("I am an image from network!")
               AsyncImage(
                  load = {
                     loadSvgPainter(
                        "https://github.com/JetBrains/compose-multiplatform/raw/master/artwork/idea-logo.svg",
                        density
                     )
                  },
                  painterFor = { it },
                  contentDescription = "Idea logo",
                  contentScale = ContentScale.FillWidth,
                  modifier = Modifier.width(200.dp)
               )
            }
         }
      }
   }
}

@Composable
private fun DemoDialog(
   onCloseRequest: () -> Unit,
) {
   Dialog(
      onCloseRequest = onCloseRequest,
      title = "Demo dialog",
      resizable = false,
   ) {
      Text("Hello")
   }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun MaterialDialog(
   onCloseRequest: () -> Unit,
) {
   AlertDialog(
      modifier = Modifier.width(400.dp),
      title = { Text("Alert Dialog") },
      text = { Text("This is a Material dialog") },
      confirmButton = { Button(onClick = onCloseRequest) { Text("OK") } },
      dismissButton = { Button(onClick = onCloseRequest) { Text("Cancel") } },
      onDismissRequest = onCloseRequest,
   )
}

@Composable
private fun FileDialog(
   parent: Frame? = null,
   onCloseRequest: (result: String?) -> Unit
) = AwtWindow(
   create = {
      object : FileDialog(parent, "Choose a file", LOAD) {
         override fun setVisible(value: Boolean) {
            super.setVisible(value)
            if (value) {
               onCloseRequest(file)
            }
         }
      }
   },
   dispose = FileDialog::dispose
)

@Composable
private fun InfoPanel(
   windowState: WindowState,
   lastKey: KeyEvent?,
) {
   var lastPointerEvent by remember { mutableStateOf<Offset?>(null) }
   Column(
      modifier = Modifier.onPointerEvent(PointerEventType.Move) {
         lastPointerEvent = it.changes.first().position
      }
   ) {
      Text("Window size: ${windowState.size}")
      Text("Window position: ${windowState.position.x} ${windowState.position.y}")
      Text("Window placement: ${windowState.placement}")
      Text("Last pressed key: ${lastKey?.key}")
      Text("Cursor location: $lastPointerEvent")
   }
}

@Composable
fun DemoInput() {
   var text by remember { mutableStateOf("") }
   ContextMenuDataProvider(
      items = {
         listOf(
            ContextMenuItem("Translate") { },
            ContextMenuItem("Share") { }
         )
      }
   ) {
      TextField(
         value = text,
         onValueChange = { text = it },
         label = { Text(text = "You can try context menu here:") }
      )

      Spacer(Modifier.height(16.dp))

      OutlinedTextField(
         value = text,
         onValueChange = { text = it },
         label = { Text(text = "You can try context menu here:") }
      )

      Spacer(Modifier.height(16.dp))

      var active by remember { mutableStateOf(false) }
      SelectionContainer {
         Text(
            modifier = Modifier
               .background(if (active) kiwiColor else Color.Transparent)
               .onPointerEvent(PointerEventType.Enter) { active = true }
               .onPointerEvent(PointerEventType.Exit) { active = false },
            text = "Hello Kiwi! You can select me"
         )
      }
   }
}


fun loadSvgPainter(url: String, density: Density): Painter =
   URL(url).openStream().buffered().use { loadSvgPainter(it, density) }

@Composable
fun <T> AsyncImage(
   load: suspend () -> T,
   painterFor: @Composable (T) -> Painter,
   contentDescription: String,
   modifier: Modifier = Modifier,
   contentScale: ContentScale = ContentScale.Fit,
) {
   val image: T? by produceState<T?>(null) {
      value = withContext(Dispatchers.IO) {
         try {
            load()
         } catch (e: IOException) {
            // instead of printing to console, you can also write this to log,
            // or show some error placeholder
            e.printStackTrace()
            null
         }
      }
   }

   if (image != null) {
      Image(
         painter = painterFor(image!!),
         contentDescription = contentDescription,
         contentScale = contentScale,
         modifier = modifier
      )
   }
}

@Composable
private fun ApplicationScope.AppTray(
   trayState: TrayState,
   onExit: () -> Unit,
) {
   Tray(
      icon = rememberVectorPainter(Icons.Default.Info),
      tooltip = "Demo tray",
      state = trayState,
      menu = {
         Item("Demo") {}
         Separator()
         Item("Exit", onClick = onExit)
      }
   )
}

@Composable
private fun FrameWindowScope.AppMenu(
   onPlaySnake: () -> Unit,
   onOpenFileDialog: () -> Unit,
   onExit: () -> Unit,
) {
   MenuBar {
      Menu("File") {
         Item("Open", onClick = onOpenFileDialog, shortcut = KeyShortcut(Key.O, ctrl = true))
         Item("Save", onClick = { })
         Separator()
         Item("Properties", onClick = {})
      }
      Menu("Demo app") {
         Menu("Settings") {
            Item("Setting 1", onClick = {})
            Item("Setting 2", onClick = {})
         }
         Item("Exit", onClick = onExit)
         Separator()
         Item("Snake", onClick = onPlaySnake)
      }
      Menu("About") {
         Item("v 0.1", enabled = false, onClick = {})
      }
   }
}
