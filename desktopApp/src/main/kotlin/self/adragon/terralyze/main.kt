package self.adragon.terralyze

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import self.adragon.terralyze.ui.initScreen.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "terralyze",
    ) {
        App()
    }
}