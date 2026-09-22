package terralyze.catalog

import kotlinx.serialization.Serializable

@Serializable
data class ItemInfo(
    val id: Int,
    val name: String,
    val internalName: String,
    val tags: Map<String, List<String>>,
    val researched: Int,
    val neededForResearch: Int,
    val isUnobtainable: Boolean,
    val itemUrl: String,
    val imageUrl: String
)