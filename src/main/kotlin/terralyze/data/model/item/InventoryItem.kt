package terralyze.data.model.item

import kotlinx.serialization.Serializable

@Serializable
data class InventoryItem<ItemType>(val item: ItemType, val stack: Int, val favorited: Boolean)