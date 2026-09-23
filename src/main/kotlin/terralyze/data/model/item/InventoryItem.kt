package terralyze.data.model.item

import kotlinx.serialization.Serializable

@Serializable
data class InventoryItem(val item: terralyze.data.model.item.Item, val stack: Int, val favorited: Boolean)