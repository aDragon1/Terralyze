package terralyze.data.model.item

import kotlinx.serialization.Serializable

@Serializable
data class EquipmentItem<ItemType>(val item: ItemType, val favorited: Boolean)