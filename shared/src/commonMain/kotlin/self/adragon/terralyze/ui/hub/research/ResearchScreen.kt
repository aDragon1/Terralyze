package self.adragon.terralyze.ui.hub.research

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import self.adragon.terralyze.data.repository.ItemCatalog
import self.adragon.terralyze.domain.model.ItemTag
import self.adragon.terralyze.ui.common.AppCard
import self.adragon.terralyze.ui.common.CaptionText
import self.adragon.terralyze.ui.common.FilterChipRow
import self.adragon.terralyze.ui.common.GroupedDropDown
import self.adragon.terralyze.ui.common.InlineSlider
import self.adragon.terralyze.ui.common.ItemImage
import self.adragon.terralyze.ui.common.SearchTextField
import self.adragon.terralyze.ui.common.SecondaryText
import self.adragon.terralyze.ui.common.StatBar
import self.adragon.terralyze.ui.common.ValueText
import self.adragon.terralyze.ui.hub.editor.PlayerEditorIntent
import self.adragon.terralyze.ui.hub.editor.PlayerEditorStateHolder

/*
    Also ~~stole~~ took heavy inspiration from the UI of https://terraria-research-tracker.free.nf
        Thanks, I suppose? x2
*/

private enum class ResearchFilter(val title: String) {
    All("All"),
    NotStarted("Not started"),
    InProgress("In progress"),
    Completed("Completed"),
    Unknown("Unknown"),
}

private fun ResearchItemUiState.matchesFilters(
    searchQuery: String, selectedFilter: ResearchFilter, tagSelection: ItemTag?
): Boolean {
    val matchesSearch = when (this) {
        is ResearchItemUiState.Unknown -> internalName.contains(searchQuery, true)
        is ResearchItemUiState.Identified -> {
            val nameCompare = catalogItem.name.contains(searchQuery, true)
            val idCompare = catalogItem.id.toString().contains(searchQuery)
            val internalNameCompare = catalogItem.internalName.contains(searchQuery, true)

            nameCompare || idCompare || internalNameCompare
        }
    }

    val matchesFilter = when (selectedFilter) {
        ResearchFilter.All -> true
        ResearchFilter.NotStarted -> this is ResearchItemUiState.NotStarted
        ResearchFilter.InProgress -> this is ResearchItemUiState.InProgress
        ResearchFilter.Completed -> this is ResearchItemUiState.Completed
        ResearchFilter.Unknown -> this is ResearchItemUiState.Unknown
    }

    val matchesTags = tagSelection == null || when (this) {
        is ResearchItemUiState.Unknown -> true
        is ResearchItemUiState.Identified -> catalogItem.itemTags.contains(tagSelection)
    }

    return matchesSearch && matchesFilter && matchesTags
}

@Composable
fun ResearchScreen(
    playerEditor: PlayerEditorStateHolder,
    catalog: ItemCatalog,
    modifier: Modifier = Modifier
) {
    val player by playerEditor.editedPlayer.collectAsState()

    val researchItems = remember(catalog, player.researchEntries) {
        buildResearchUiState(catalog.getAllItems(), player.researchEntries)
    }
    val tags = remember(catalog) { catalog.getAllTags() }

    var selectedTag by rememberSaveable { mutableStateOf<ItemTag?>(null) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedFilter by rememberSaveable { mutableStateOf(ResearchFilter.All) }
    val filteredItems = remember(researchItems, searchQuery, selectedFilter, selectedTag) {
        researchItems.filter { it.matchesFilters(searchQuery, selectedFilter, selectedTag) }
    }

    Column(modifier.fillMaxSize().padding(16.dp), Arrangement.spacedBy(12.dp)) {
        ResearchHeader(
            filteredItems, searchQuery, selectedFilter, selectedTag, tags,
            { searchQuery = it }, { selectedFilter = it }, { selectedTag = it },
            playerEditor::onIntent
        )

        LazyVerticalGrid(
            GridCells.Adaptive(220.dp), Modifier.weight(1f),
            contentPadding = PaddingValues(top = 4.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(filteredItems) { item ->
                when (item) {
                    is ResearchItemUiState.Unknown ->
                        UnknownResearchItemCard(item)

                    is ResearchItemUiState.Identified ->
                        ResearchItemCard(item, playerEditor::onIntent)
                }
            }
        }
    }
}

@Composable
private fun ResearchHeader(
    items: List<ResearchItemUiState>, searchQuery: String, filterSelected: ResearchFilter,
    tagSelection: ItemTag?, tags: List<ItemTag>, onSearchQueryChange: (String) -> Unit,
    onFilterSelect: (ResearchFilter) -> Unit, onTagSelect: (ItemTag?) -> Unit,
    onIntent: (PlayerEditorIntent) -> Unit, modifier: Modifier = Modifier
) = AppCard(modifier.fillMaxWidth()) {
    val completedCount =
        remember(items) { items.count { it is ResearchItemUiState.Identified && it.isCompleted } }
    val total = items.size

    Column(Modifier.padding(16.dp), Arrangement.spacedBy(12.dp)) {
        StatBar("Researched", completedCount, total, Color.Yellow, showPercent = true)
        Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp), Alignment.CenterVertically) {
            SearchTextField(
                searchQuery, onSearchQueryChange, Modifier.weight(1f),
                "Name or ID"
            )
            GroupedDropDown(
                tagSelection, listOf(null) + tags, onTagSelect,
                { it?.category ?: "ALL" }, { it?.name ?: "All tags" }
            )
        }
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            FilterChipRow(
                filterSelected, ResearchFilter.entries, onFilterSelect,
                { it.title }, modifier = Modifier.weight(1f)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                var showResetConfirmation by remember { mutableStateOf(false) }

                TextButton(onClick = { showResetConfirmation = true }) {
                    ValueText("Reset All")
                }

                TextButton(onClick = { onIntent(PlayerEditorIntent.Research.ResearchAll) }) {
                    ValueText("Research All")
                }

                if (showResetConfirmation)
                    AlertDialog(
                        onDismissRequest = { showResetConfirmation = false },
                        title = { ValueText("Reset research?") },
                        text = {
                            ValueText("This will reset all research progress. This action cannot be undone.")
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    onIntent(PlayerEditorIntent.Research.ResetAll)
                                    showResetConfirmation = false
                                }
                            ) {
                                ValueText("Reset")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showResetConfirmation = false }) {
                                ValueText("Cancel")
                            }
                        }
                    )
            }
        }
    }
}


@Composable
fun ResearchItemCard(
    item: ResearchItemUiState.Identified, onIntent: (PlayerEditorIntent) -> Unit,
    modifier: Modifier = Modifier
) = InlineSlider(item.amount, item.catalogItem.neededForResearch, onSave = {
    onIntent(PlayerEditorIntent.Research.SetAmount(item.catalogItem.internalName, it))
}, content = {
    AppCard(modifier) {
        Column(Modifier.padding(10.dp), Arrangement.spacedBy(10.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                Arrangement.spacedBy(12.dp),
                Alignment.CenterVertically
            ) {
                ItemImage(
                    item.catalogItem.name,
                    item.catalogItem.imageUrl,
                    Modifier.size(40.dp)
                )
                SecondaryText(
                    "${item.catalogItem.name}\nID: ${item.catalogItem.id}",
                    minLines = 3
                )
            }
            val label = if (item.isCompleted) "Completed" else "Not started"
            val statColor = if (item.isCompleted) MaterialTheme.colorScheme.tertiary
            else Color.Yellow
            StatBar(label, item.amount, item.catalogItem.neededForResearch, statColor)
        }
    }
})

@Composable
private fun UnknownResearchItemCard(
    item: ResearchItemUiState.Unknown, modifier: Modifier = Modifier
) = AppCard(modifier) {
    Column(Modifier.fillMaxWidth().padding(12.dp), Arrangement.spacedBy(8.dp)) {
        SecondaryText(item.internalName)
        CaptionText("Unknown item")
        SecondaryText("Amount: ${item.amount}")
    }
}
