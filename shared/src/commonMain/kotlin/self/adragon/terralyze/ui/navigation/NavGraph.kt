package self.adragon.terralyze.ui.navigation

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
import self.adragon.terralyze.ui.playerLoadScreen.PlayerLoadScreen
import self.adragon.terralyze.ui.playerOverviewScreen.PlayerOverview

@Composable
fun NavGraph(loadPlayer: LoadPlayerUseCase) {
    val navController = rememberNavController()
    var player by remember { mutableStateOf<Player?>(null) }

    NavHost(
        navController = navController,
        startDestination = Routes.PlayerLoad
    ) {
        composable<Routes.PlayerLoad> {
            PlayerLoadScreen(loadPlayer) {
                player = it
                navController.navigate(Routes.PlayerOverview)
            }
        }
        composable<Routes.PlayerOverview> { player?.let { PlayerOverview(it) } }
    }
}