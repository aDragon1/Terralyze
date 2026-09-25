package terralyze.data.model.item

import kotlinx.serialization.Serializable

@Serializable
data class RawItem(val id: Int, val prefix: UByte)