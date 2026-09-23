package terralyze.data.model.equipment

import kotlinx.serialization.Serializable

@Serializable
data class Equipment(
    val armor: List<terralyze.data.model.equipment.EquipmentSlot>,
    val accessories: List<terralyze.data.model.equipment.EquipmentSlot>,
    val misc: List<terralyze.data.model.equipment.MiscEquipmentSlot>,
)