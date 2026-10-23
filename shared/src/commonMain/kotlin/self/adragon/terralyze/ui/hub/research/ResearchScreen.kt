package self.adragon.terralyze.ui.hub.research

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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import self.adragon.terralyze.domain.model.Player
import self.adragon.terralyze.ui.common.AppCard
import self.adragon.terralyze.ui.common.CaptionText
import self.adragon.terralyze.ui.common.GroupedDropDown
import self.adragon.terralyze.ui.common.ItemImage
import self.adragon.terralyze.ui.common.SecondaryText
import self.adragon.terralyze.ui.common.StatBar

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
fun ResearchScreen(player: Player, catalog: ItemCatalog, modifier: Modifier = Modifier) {
    val entries = player.researchEntries!! // Just to shut up the warning

    val researchItems = remember(catalog, entries) {
        buildResearchUiState(catalog.getAllItems(), entries)
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
            { searchQuery = it }, { selectedFilter = it }, { selectedTag = it }
        )

        LazyVerticalGrid(
            GridCells.Adaptive(220.dp), Modifier.weight(1f),
            contentPadding = PaddingValues(top = 4.dp, bottom = 16.dp),
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
private fun ResearchHeader(
    items: List<ResearchItemUiState>, searchQuery: String, filterSelected: ResearchFilter,
    tagSelection: ItemTag?, tags: List<ItemTag>, onSearchQueryChange: (String) -> Unit,
    onFilterSelect: (ResearchFilter) -> Unit, onTagSelect: (ItemTag?) -> Unit,
    modifier: Modifier = Modifier
) = AppCard(modifier.fillMaxWidth()) {
    val completedCount = items.count { it is ResearchItemUiState.Identified && it.isCompleted }
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
        FilterChipRow(
            filterSelected, ResearchFilter.entries, onFilterSelect, { it.title },
        )

    }
}

@Composable
fun ResearchItemCard(item: ResearchItemUiState.Identified, modifier: Modifier = Modifier) =
    AppCard(modifier) {
        Column(Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(12.dp), Alignment.CenterVertically) {
                ItemImage(item.catalogItem.name, item.catalogItem.imageUrl, Modifier.size(40.dp))
                SecondaryText(
                    "${item.catalogItem.name}\nID: ${item.catalogItem.id}",
                    minLines = 3
                )
            }
            val label = if (item.isCompleted) "Completed" else "Not started"
            val statColor =
                if (item.isCompleted) MaterialTheme.colorScheme.tertiary else Color.Yellow
            StatBar(label, item.amount, item.catalogItem.neededForResearch, statColor)
        }
    }

@Composable
private fun UnknownResearchItemCard(
    item: ResearchItemUiState.Unknown, modifier: Modifier = Modifier
) = AppCard(modifier) {
    Column(
        Modifier.fillMaxWidth().padding(12.dp), Arrangement.spacedBy(8.dp),
    ) {
        SecondaryText(item.internalName)
        CaptionText("Unknown item")
        SecondaryText("Amount: ${item.amount}")
    }
}

@Composable
fun SearchTextField(
    value: String, onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search...",
) = OutlinedTextField(
    value, onValueChange, modifier, placeholder = { Text(placeholder) },
    leadingIcon = { Text("⌕") }, trailingIcon = {
        if (value.isNotEmpty()) IconButton(onClick = { onValueChange("") }) { Text("×") }
    }, singleLine = true
)

@Composable
fun <T> FilterChipRow(
    selected: T, items: List<T>, onSelect: (T) -> Unit, itemLabel: (T) -> String,
    modifier: Modifier = Modifier,
) = Row(
    modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), Arrangement.spacedBy(8.dp),
) { items.forEach { FilterChip(it == selected, { onSelect(it) }, { Text(itemLabel(it)) }) } }