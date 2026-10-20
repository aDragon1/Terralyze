package self.adragon.terralyze.ui.hub

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.serialization.Serializable
import self.adragon.terralyze.data.repository.ItemCatalog
import self.adragon.terralyze.domain.model.Difficulty
import self.adragon.terralyze.domain.model.Player
import self.adragon.terralyze.ui.hub.research.ResearchScreen
import self.adragon.terralyze.ui.hub.storage.StorageScreen

@Composable
fun PlayerHub(player: Player,catalog: ItemCatalog) {
    var currentDestination by rememberSaveable { mutableStateOf(HubDestination.Overview) }

    val destinations = buildList {
        add(HubDestination.Overview)
        add(HubDestination.Storage)
        if (player.difficulty == Difficulty.JOURNEY && player.researchEntries != null)
            add(HubDestination.Researches)
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                destinations.forEach { destination ->
                    NavigationBarItem(
                        selected = currentDestination == destination,
                        onClick = { currentDestination = destination },
                        icon = { },
                        label = { Text(destination.name) }
                    )
                }
            }
        }
    ) { pad ->
        Box(Modifier.padding(pad).fillMaxSize()) {
            when (currentDestination) {
                HubDestination.Overview -> OverviewScreen(player)
                HubDestination.Storage -> StorageScreen(player)
                HubDestination.Researches -> ResearchScreen(player, catalog)
            }
        }
    }
}

@Serializable
enum class HubDestination { Overview, Storage, Researches, }