package terralyze.playerfile.model.equipment

import terralyze.playerfile.model.item.EquipmentItem
import kotlinx.serialization.Serializable

@Serializable
data class EquipmentSlot(
    val main: EquipmentItem,
    val vanity: EquipmentItem,
    val dye: EquipmentItem
)