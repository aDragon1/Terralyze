package self.adragon.terralyze.ui.hub.research

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import self.adragon.terralyze.data.repository.ItemCatalog
import self.adragon.terralyze.domain.model.Player
import self.adragon.terralyze.ui.common.CaptionText
import self.adragon.terralyze.ui.common.ItemImage
import self.adragon.terralyze.ui.common.SecondaryText
import self.adragon.terralyze.ui.common.StatBar

/*
    Also ~~stole~~ took heavy inspiration from the UI of https://terraria-research-tracker.free.nf
        Thank's, I suppose? x2
*/

private enum class ResearchFilter(val title: String) {
    All("All"),
    NotStarted("Not started"),
    InProgress("In progress"),
    Completed("Completed"),
    Unknown("Unknown"),
}

@Composable
fun ResearchScreen(player: Player, catalog: ItemCatalog, modifier: Modifier = Modifier) {
    val entries = player.researchEntries!! // Just to shut up the warning

    val researchItems = remember(entries) {
        buildResearchUiState(
            catalog.getAllItems(),
            entries,
        )
    }
    val identifiedItems = researchItems.filterIsInstance<ResearchItemUiState.Identified>()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(ResearchFilter.All) }

    val filteredItems = researchItems.filter { item ->
        val matchesSearch = when (item) {
            is ResearchItemUiState.Unknown ->
                item.internalName.contains(searchQuery, ignoreCase = true)

            is ResearchItemUiState.Identified -> {
                val nameCompare = item.info.name.contains(searchQuery, ignoreCase = true)
                val idCompare = item.info.id.toString().contains(searchQuery)
                val internalNameCompare =
                    item.info.internalName.contains(searchQuery, ignoreCase = true)

                nameCompare || idCompare || internalNameCompare
            }
        }

        val matchesFilter = when (selectedFilter) {
            ResearchFilter.All -> true
            ResearchFilter.NotStarted -> item is ResearchItemUiState.NotStarted
            ResearchFilter.InProgress -> item is ResearchItemUiState.InProgress
            ResearchFilter.Completed -> item is ResearchItemUiState.Completed
            ResearchFilter.Unknown -> item is ResearchItemUiState.Unknown
        }

        matchesSearch && matchesFilter
    }

    Column(modifier.fillMaxSize(), Arrangement.spacedBy(12.dp)) {
        StatBar(
            "Researched", identifiedItems.count { it.isCompleted },
            identifiedItems.size, Color.Yellow
        )

        OutlinedTextField(
            searchQuery, { searchQuery = it }, Modifier.fillMaxWidth(),
            placeholder = { Text("Name or ID") },
            leadingIcon = { Text("⌕") },
            trailingIcon = {
                if (searchQuery.isNotEmpty())
                    IconButton(onClick = { searchQuery = "" }) { Text("×") }
            },
            singleLine = true,
        )
        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ResearchFilter.entries.forEach { filter ->
                FilterChip(
                    selectedFilter == filter, { selectedFilter = filter }, { Text(filter.title) }
                )
            }
        }

        LazyVerticalGrid(
            GridCells.Adaptive(minSize = 220.dp),
            Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(filteredItems) {
                when (it) {
                    is ResearchItemUiState.Unknown -> UnknownResearchItemCard(it)
                    is ResearchItemUiState.Identified -> ResearchItemCard(it)
                }
            }
        }
    }
}

@Composable
private fun BaseResearchCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) =
    Card(
        modifier, RoundedCornerShape(10.dp),
        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) { content() }

@Composable
fun ResearchItemCard(item: ResearchItemUiState.Identified, modifier: Modifier = Modifier) {
    BaseResearchCard(modifier) {
        Column(Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(12.dp), Alignment.CenterVertically) {
                ItemImage(item.info.name, item.info.imageUrl, Modifier.size(40.dp))

                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    SecondaryText(item.info.name)
                    CaptionText("ID: ${item.info.id}")
                }
            }

            val label = if (item.isCompleted) "Completed" else "Research"
            val statColor =
                if (item.isCompleted) MaterialTheme.colorScheme.tertiary else Color.Yellow
            StatBar(label, item.amount, item.info.neededForResearch, statColor)
        }
    }
}

@Composable
private fun UnknownResearchItemCard(
    item: ResearchItemUiState.Unknown, modifier: Modifier = Modifier
) = BaseResearchCard(modifier) {
    Column(
        Modifier.fillMaxWidth().padding(12.dp), Arrangement.spacedBy(8.dp),
    ) {
        SecondaryText(item.internalName)
        CaptionText("Неизвестный предмет")
        SecondaryText("Количество: ${item.amount}")
    }
}