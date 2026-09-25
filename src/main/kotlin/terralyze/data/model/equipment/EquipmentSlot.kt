package terralyze.data.model.equipment

import kotlinx.serialization.Serializable
import terralyze.data.model.item.EquipmentItem

@Serializable
data class EquipmentSlot<ItemType>(
    val main: EquipmentItem<ItemType>,
    val vanity: EquipmentItem<ItemType>,
    val dye: EquipmentItem<ItemType>
)