package self.adragon.terralyze.data.model.item

import kotlinx.serialization.Serializable

@Serializable
data class Item(
    val id: Int = -1, val prefix: Int = 0, val stack: Int = 0, val favorite: Boolean = false
) {
    val isEmpty: Boolean get() = id <= 0
}