package self.adragon.terralyze.ui.hub

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable
import self.adragon.terralyze.data.repository.ItemCatalog
import self.adragon.terralyze.domain.model.Difficulty
import self.adragon.terralyze.domain.model.Player
import self.adragon.terralyze.ui.common.FloatingCard
import self.adragon.terralyze.ui.common.SectionTitle
import self.adragon.terralyze.ui.common.ValueText
import self.adragon.terralyze.ui.hub.editor.PlayerEditorIntent
import self.adragon.terralyze.ui.hub.editor.PlayerEditorStateHolder
import self.adragon.terralyze.ui.hub.research.ResearchScreen
import self.adragon.terralyze.ui.hub.storage.StorageScreen

@Serializable
enum class HubDestination { Overview, Storage, Researches, }

@Composable
fun PlayerHub(player: Player, catalog: ItemCatalog) {
    val scope = rememberCoroutineScope()
    val playerEditorStateHolder =
        remember(player) { PlayerEditorStateHolder(player, catalog, scope) }

    var currentDestination by rememberSaveable { mutableStateOf(HubDestination.Overview) }
    val destinations = buildList {
        add(HubDestination.Overview)
        add(HubDestination.Storage)
        if (player.difficulty == Difficulty.JOURNEY && player.researchEntries != null)
            add(HubDestination.Researches)
    }

    val hasUnsavedChanges by playerEditorStateHolder.hasUnsavedChanges.collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar {
                destinations.forEach { destination ->
                    NavigationBarItem(
                        currentDestination == destination, { currentDestination = destination },
                        { }, label = { Text(destination.name) }
                    )
                }
            }
        }
    ) { pad ->
        Box(Modifier.padding(pad).fillMaxSize()) {
            rememberSaveableStateHolder().SaveableStateProvider(currentDestination.name) {
                when (currentDestination) {
                    HubDestination.Overview -> OverviewScreen(playerEditorStateHolder)
                    HubDestination.Storage -> StorageScreen(playerEditorStateHolder, catalog)

                    HubDestination.Researches -> ResearchScreen(playerEditorStateHolder, catalog)
                }
            }
            FloatingCard(hasUnsavedChanges, Modifier.fillMaxWidth(), Alignment.BottomEnd) {
                Row(
                    Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    Arrangement.spacedBy(12.dp), Alignment.CenterVertically
                ) {
                    SectionTitle("Unsaved changes", Modifier.weight(1f))
                    TextButton({ playerEditorStateHolder.onIntent(PlayerEditorIntent.ResetChanges) })
                    { ValueText("Cancel") }
                    TextButton({ playerEditorStateHolder.onIntent(PlayerEditorIntent.SavePlayer) })
                    { ValueText("Save") }
                }
            }
        }
    }
}
