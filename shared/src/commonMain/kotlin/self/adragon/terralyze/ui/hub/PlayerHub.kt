package self.adragon.terralyze.ui.hub

import androidx.compose.foundation.layout.Box
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
import self.adragon.terralyze.domain.model.Player

@Composable
fun PlayerHub(player: Player) {
    var currentDestination by rememberSaveable { mutableStateOf(HubDestination.Overview) }

    /* Compose logo:
Icon(
    painter = painterResource(Res.drawable.compose_multiplatform),
    contentDescription = "Compose multiplatform logo"
)
     */

    Scaffold(
        bottomBar = {
            NavigationBar {
                HubDestination.entries.forEach { destination ->
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
        Box(modifier = Modifier.padding(pad))
        {
            when (currentDestination) {
                HubDestination.Overview -> OverviewScreen(player)
                HubDestination.Inventory -> Text("Inventory")
                HubDestination.Equipment -> Text("Equipment")
            }
        }
    }
}
@Serializable
enum class HubDestination {
    Overview,
    Inventory,
    Equipment,
}