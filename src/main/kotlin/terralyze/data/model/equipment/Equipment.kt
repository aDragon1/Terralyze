package terralyze.data.model.equipment

import kotlinx.serialization.Serializable

@Serializable
data class Equipment<ItemType>(
    val armor: List<EquipmentSlot<ItemType>>,
    val accessories: List<EquipmentSlot<ItemType>>,
    val misc: List<MiscEquipmentSlot<ItemType>>,
)