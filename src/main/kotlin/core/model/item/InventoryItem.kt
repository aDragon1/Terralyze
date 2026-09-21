package core.model.item

import kotlinx.serialization.Serializable

@Serializable
data class InventoryItem(val item: Item, val stack: Int, val favorited: Boolean)