package terralyze.data.model.equipment

import kotlinx.serialization.Serializable
import terralyze.data.model.item.EquipmentItem

@Serializable
data class EquipmentSlot(
    val main: EquipmentItem,
    val vanity: EquipmentItem,
    val dye: EquipmentItem
)