package core.model.equipment

import core.model.item.Item
import kotlinx.serialization.Serializable

@Serializable
data class MiscEquipmentSlot(val main: Item, val dye: Item)