package self.adragon.terralyze.ui.hub.storage.inventory

import self.adragon.terralyze.data.model.equipment.Equipment
import self.adragon.terralyze.data.model.equipment.EquipmentSlot
import self.adragon.terralyze.data.model.equipment.MiscEquipmentSlot
import self.adragon.terralyze.data.model.item.BankItem
import self.adragon.terralyze.data.model.item.EquipmentItem
import self.adragon.terralyze.data.model.item.InventoryItem
import self.adragon.terralyze.domain.model.ItemSlot

internal fun ItemSlot.toUiState(stack: Int = 1, favorited: Boolean = false): ItemUiState =
    when (this) {
        ItemSlot.Empty -> ItemUiState.Empty
        is ItemSlot.UnknownById -> ItemUiState.Unknown(id)
        is ItemSlot.Resolved -> ItemUiState.Filled(item, stack, favorited)
    }

internal fun InventoryItem<ItemSlot>.toUiState(): ItemUiState = item.toUiState(stack, favorited)

internal fun BankItem<ItemSlot>.toUiState(): ItemUiState = item.toUiState(stack)

internal fun Equipment<ItemSlot>.toUiState() = EquipmentUiState(
    armor.map { it.toUiState() },
    accessories.map { it.toUiState() },
    misc.map { it.toUiState() }
)

internal fun EquipmentSlot<ItemSlot>.toUiState() =
    EquipmentSlotUiState(main.toUiState(), vanity.toUiState(), dye.toUiState())

internal fun MiscEquipmentSlot<ItemSlot>.toUiState() =
    EquipmentSlotUiState(main.toUiState(), null, dye.toUiState())

internal fun EquipmentItem<ItemSlot>.toUiState() = item.toUiState(favorited = this.favorited)
