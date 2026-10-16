package self.adragon.terralyze.ui.hub.equipment

import self.adragon.terralyze.data.model.equipment.Equipment
import self.adragon.terralyze.data.model.equipment.EquipmentSlot
import self.adragon.terralyze.data.model.equipment.MiscEquipmentSlot
import self.adragon.terralyze.data.model.item.EquipmentItem
import self.adragon.terralyze.domain.model.ItemSlot

private fun ItemSlot.toUiState(favorited: Boolean = false) = when (this) {
    ItemSlot.Empty -> EquipmentItemUiState.Empty
    is ItemSlot.UnknownById -> EquipmentItemUiState.Unknown(id)
    is ItemSlot.Resolved -> EquipmentItemUiState.Filled(item, favorited)
}

fun Equipment<ItemSlot>.toUiState() = EquipmentUiState(
    armor.map { it.toUiState() },
    accessories.map { it.toUiState() },
    misc.map { it.toUiState() },
)

private fun EquipmentItem<ItemSlot>.toUiState() = item.toUiState(favorited)

private fun EquipmentSlot<ItemSlot>.toUiState() =
    EquipmentSlotUiState(main.toUiState(), vanity.toUiState(), dye.toUiState())

private fun MiscEquipmentSlot<ItemSlot>.toUiState() =
    MiscEquipmentSlotUiState(main.toUiState(), dye.toUiState())
