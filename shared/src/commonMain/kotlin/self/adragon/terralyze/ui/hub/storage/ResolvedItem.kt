package self.adragon.terralyze.ui.hub.storage

import self.adragon.terralyze.data.model.equipment.Equipment
import self.adragon.terralyze.data.model.equipment.EquipmentSlot
import self.adragon.terralyze.data.model.equipment.MiscEquipmentSlot
import self.adragon.terralyze.data.model.item.CatalogItem
import self.adragon.terralyze.data.model.item.Item
import self.adragon.terralyze.data.repository.ItemCatalog

data class ResolvedItem(val item: Item, val catalogItem: CatalogItem?) {
    val state: ItemState
        get() = when {
            item.isEmpty -> ItemState.EMPTY
            catalogItem == null -> ItemState.UNKNOWN
            else -> ItemState.KNOWN
        }
}

data class ResolvedEquipmentSlot(
    val main: ResolvedItem, val vanity: ResolvedItem, val dye: ResolvedItem
)

data class ResolvedMiscEquipmentSlot(val main: ResolvedItem, val dye: ResolvedItem)

enum class ItemState { EMPTY, UNKNOWN, KNOWN }

fun Item.resolve(catalog: ItemCatalog) =
    ResolvedItem(this, if (!isEmpty) catalog.getItemById(id) else null)

data class ResolvedEquipment(
    val armor: List<ResolvedEquipmentSlot>, val accessories: List<ResolvedEquipmentSlot>,
    val misc: List<ResolvedMiscEquipmentSlot>,
)

fun Equipment.resolve(catalog: ItemCatalog) = ResolvedEquipment(
    armor.map { it.resolve(catalog) }, accessories.map { it.resolve(catalog) },
    misc.map { it.resolve(catalog) }
)

private fun EquipmentSlot.resolve(catalog: ItemCatalog) =
    ResolvedEquipmentSlot(main.resolve(catalog), vanity.resolve(catalog), dye.resolve(catalog))

private fun MiscEquipmentSlot.resolve(catalog: ItemCatalog) =
    ResolvedMiscEquipmentSlot(main.resolve(catalog), dye.resolve(catalog))