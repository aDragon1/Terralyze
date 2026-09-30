package self.adragon.terralyze.ui.hub.inventory

import self.adragon.terralyze.data.model.item.InventoryItem
import self.adragon.terralyze.domain.model.ItemSlot


fun InventoryItem<ItemSlot>.toUiState(): InventoryItemUiState =
    when (val slot = item) {
        ItemSlot.Empty -> InventoryItemUiState.Empty
        is ItemSlot.UnknownById -> InventoryItemUiState.Unknown(slot.id)
        is ItemSlot.Resolved -> InventoryItemUiState.Filled(slot.item, stack, favorited)
    }