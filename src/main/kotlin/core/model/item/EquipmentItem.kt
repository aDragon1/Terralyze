package core.model.item

import kotlinx.serialization.Serializable

@Serializable
data class EquipmentItem(val item: Item, val favorited: Boolean)