package self.adragon.terralyze.data.model.item

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import self.adragon.terralyze.domain.model.ItemTag

@Serializable
data class CatalogItem(
    val id: Int,
    val name: String,
    val internalName: String,
    @SerialName("tags")
    private val rawTags: Map<String, List<String>> = emptyMap(),
    val neededForResearch: Int,
    val isUnobtainable: Boolean = false,
    val itemUrl: String,
    val imageUrl: String
) {
    val itemTags: List<ItemTag> = rawTags
        .flatMap { (category, names) -> names.map { name -> ItemTag(category, name) } }
}