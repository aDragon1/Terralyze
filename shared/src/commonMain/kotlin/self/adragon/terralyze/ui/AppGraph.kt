package self.adragon.terralyze.ui

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import self.adragon.terralyze.domain.model.Player
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import self.adragon.terralyze.domain.usecase.LoadPlayerUseCase
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import self.adragon.terralyze.ui.hub.PlayerHub

@Composable
fun AppGraph(loadPlayer: LoadPlayerUseCase) {
    val navController = rememberNavController()
    var player by remember { mutableStateOf<Player?>(null) }


    NavHost(
        navController = navController,
        startDestination = Routes.Load
    ) {
        composable<Routes.Load> {
            PlayerLoadScreen(loadPlayer, onLoadComplete = { loadedPlayer ->
                player = loadedPlayer
                navController.navigate(Routes.Hub) {
                    popUpTo(Routes.Load) { inclusive = true }
                    launchSingleTop = true
                }
            })
        }
        composable<Routes.Hub> { player?.let { PlayerHub(it) } }
    }
}

@Serializable
sealed interface Routes {
    @Serializable
    data object Load : Routes

    @Serializable
    data object Hub : Routes
}

