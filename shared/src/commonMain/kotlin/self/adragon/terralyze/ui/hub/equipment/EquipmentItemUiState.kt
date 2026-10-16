package self.adragon.terralyze.ui.hub.equipment

import self.adragon.terralyze.domain.model.Item

sealed interface EquipmentItemUiState {
    data object Empty : EquipmentItemUiState

    data class Unknown(
        val id: Int,
    ) : EquipmentItemUiState

    data class Filled(
        val item: Item,
        val favorited: Boolean,
    ) : EquipmentItemUiState
}

data class EquipmentUiState(
    val armor: List<EquipmentSlotUiState>,
    val accessories: List<EquipmentSlotUiState>,
    val misc: List<MiscEquipmentSlotUiState>,
)

data class EquipmentSlotUiState(
    val main: EquipmentItemUiState,
    val vanity: EquipmentItemUiState,
    val dye: EquipmentItemUiState,
)

data class MiscEquipmentSlotUiState(
    val main: EquipmentItemUiState,
    val dye: EquipmentItemUiState,
)