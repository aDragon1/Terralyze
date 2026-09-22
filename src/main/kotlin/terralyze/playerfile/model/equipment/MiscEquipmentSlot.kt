package terralyze.playerfile.model.equipment

import terralyze.playerfile.model.item.Item
import kotlinx.serialization.Serializable

@Serializable
data class MiscEquipmentSlot(val main: Item, val dye: Item)