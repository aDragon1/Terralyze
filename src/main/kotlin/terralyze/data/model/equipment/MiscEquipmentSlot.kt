package terralyze.data.model.equipment

import kotlinx.serialization.Serializable
import terralyze.data.model.item.Item

@Serializable
data class MiscEquipmentSlot(val main: Item, val dye: Item)