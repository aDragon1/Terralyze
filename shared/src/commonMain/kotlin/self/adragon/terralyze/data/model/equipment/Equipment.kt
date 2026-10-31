package self.adragon.terralyze.data.model.equipment

import kotlinx.serialization.Serializable

@Serializable
data class Equipment(
    val armor: List<EquipmentSlot>,
    val accessories: List<EquipmentSlot>,
    val misc: List<MiscEquipmentSlot>,
)