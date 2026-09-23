package terralyze.data.model.item

import kotlinx.serialization.Serializable

@Serializable
data class EquipmentItem(val item: terralyze.data.model.item.Item, val favorited: Boolean)