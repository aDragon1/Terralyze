package self.adragon.terralyze.ui.hub.storage

import self.adragon.terralyze.domain.model.Item

sealed interface ItemUiState {
    data object Empty : ItemUiState
    data class Unknown(val id: Int) : ItemUiState
    data class Filled(val item: Item, val stack: Int = 1, val favorited: Boolean = false) :
        ItemUiState
}

data class EquipmentUiState(
    val armor: List<EquipmentSlotUiState>,
    val accessories: List<EquipmentSlotUiState>,
    val misc: List<EquipmentSlotUiState>
)

data class EquipmentSlotUiState(
    val main: ItemUiState, val vanity: ItemUiState?, val dye: ItemUiState
)


