package terralyze.data.model.equipment

import kotlinx.serialization.Serializable

@Serializable
data class MiscEquipmentSlot<ItemType>(val main: ItemType, val dye: ItemType)