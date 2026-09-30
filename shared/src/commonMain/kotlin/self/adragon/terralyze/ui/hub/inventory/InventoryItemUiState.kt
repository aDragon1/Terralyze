package self.adragon.terralyze.ui.hub.inventory

import self.adragon.terralyze.domain.model.Item

sealed interface InventoryItemUiState {
    data object Empty : InventoryItemUiState

    data class Unknown(
        val id: Int,
    ) : InventoryItemUiState

    data class Filled(
        val item: Item,
        val stack: Int,
        val favorited: Boolean,
    ) : InventoryItemUiState
}