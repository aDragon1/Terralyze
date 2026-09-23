package terralyze.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ItemInfo(
    val id: Int,
    val name: String,
    val internalName: String,
    val tags: Map<String, List<String>>,
    val neededForResearch: Int,
    val isUnobtainable: Boolean = false,
    val itemUrl: String,
    val imageUrl: String
)