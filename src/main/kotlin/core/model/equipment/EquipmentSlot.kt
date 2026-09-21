package core.model.equipment

import core.model.item.EquipmentItem
import kotlinx.serialization.Serializable

@Serializable
data class EquipmentSlot(
    val main: EquipmentItem,
    val vanity: EquipmentItem,
    val dye: EquipmentItem
)