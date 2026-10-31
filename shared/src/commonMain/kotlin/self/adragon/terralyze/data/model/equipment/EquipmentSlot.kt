package self.adragon.terralyze.data.model.equipment

import kotlinx.serialization.Serializable
import self.adragon.terralyze.data.model.item.Item

@Serializable
data class EquipmentSlot(
    val main: Item,
    val vanity: Item,
    val dye: Item
)