package self.adragon.terralyze.ui.hub.storage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import self.adragon.terralyze.data.model.item.CatalogItem
import self.adragon.terralyze.data.model.item.Item
import self.adragon.terralyze.data.repository.ItemCatalog
import self.adragon.terralyze.ui.common.AppCard
import self.adragon.terralyze.ui.common.EditableContainer
import self.adragon.terralyze.ui.common.InlineDropdown
import self.adragon.terralyze.ui.common.InlineIntEditor
import self.adragon.terralyze.ui.common.ItemImage
import self.adragon.terralyze.ui.common.LabeledValue
import self.adragon.terralyze.ui.common.SecondaryText
import self.adragon.terralyze.ui.common.SectionTitle
import self.adragon.terralyze.ui.common.ValueText
import self.adragon.terralyze.ui.hub.editor.PlayerEditorIntent
import self.adragon.terralyze.ui.hub.editor.SlotLocation

@Composable
fun GridSlotEditor(
    item: ResolvedItem?, location: SlotLocation?, onIntent: (PlayerEditorIntent) -> Unit,
    catalog: ItemCatalog,
) = AppCard(Modifier.widthIn(min = 280.dp)) {
    if (item == null || location == null) return@AppCard

    fun updateItem(updatedItem: Item) =
        onIntent(PlayerEditorIntent.Storage.UpdateSlot(updatedItem, location))

    val selectableItems = remember(catalog) { listOf(null) + catalog.getAllItems() }
    val currentCatalogItem = item.catalogItem


    Column(
        Modifier.padding(16.dp), Arrangement.spacedBy(12.dp)
    ) {
        SectionTitle("Slot details")
        InlineDropdown(
            currentCatalogItem, selectableItems,
            { SlotSelectionContent(item, currentCatalogItem) },
            { selectableItem ->
                when {
                    selectableItem != null -> "${selectableItem.name} (#${selectableItem.id})"
                    item.state == ItemState.EMPTY -> "Choose item"
                    else -> "Clear slot"
                }
            },
            {
                it?.let {
                    updateItem(Item(it.id, 0, 1, false))
                } ?: if (item.state != ItemState.EMPTY) updateItem(Item()) else Unit
            }
        )

        when (item.state) {
            ItemState.EMPTY -> EmptySlotDetails()
            ItemState.UNKNOWN -> UnknownSlotDetails(item)
            ItemState.KNOWN -> KnownSlotDetails(item.item, ::updateItem)
        }
    }
}

@Composable
private fun SlotSelectionContent(item: ResolvedItem, catalogItem: CatalogItem?) = Row(
    Modifier.fillMaxWidth().padding(4.dp), Arrangement.spacedBy(12.dp),
    Alignment.CenterVertically,
) {
    when (item.state) {
        ItemState.EMPTY -> SlotPlaceholder("Choose item", "This slot is empty")
        ItemState.UNKNOWN -> SlotPlaceholder("Unknown item", "Item ID: ${item.item.id}")
        ItemState.KNOWN -> {
            val knownItem = requireNotNull(catalogItem)

            ItemImage(knownItem.name, knownItem.imageUrl, Modifier.size(48.dp))

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                ValueText(knownItem.name)
                SecondaryText("Item ID: ${knownItem.id}")
            }
        }
    }
}

@Composable
private fun SlotPlaceholder(title: String, subtitle: String) =
    Column(Modifier.padding(vertical = 8.dp), Arrangement.spacedBy(2.dp)) {
        ValueText(title)
        SecondaryText(subtitle)
    }

@Composable
private fun EmptySlotDetails() = SecondaryText("Select an item from the list to fill this slot.")

@Composable
private fun UnknownSlotDetails(item: ResolvedItem) =
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        LabeledValue("Unknown item ID", "${item.item.id}")
        SecondaryText("Select a known item to replace this slot, or choose Clear slot to remove it.")
    }

@Composable
private fun KnownSlotDetails(item: Item, onUpdate: (Item) -> Unit) =
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        InlineIntEditor(
            item.stack,
            { LabeledValue("Stack", "$it") },
            { onUpdate(item.copy(stack = it)) },
        )
        EditableContainer({ onUpdate(item.copy(favorite = !item.favorite)) }, enabled = true) {
            LabeledValue("Favorite", if (item.favorite) "Yes" else "No")
        }
    }