package core.model.item

import kotlinx.serialization.Serializable

@Serializable
data class Item(val id: Int, val prefix: UByte)