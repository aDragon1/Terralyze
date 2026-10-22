package self.adragon.terralyze.data.source.itemcatalog

import kotlinx.serialization.json.Json
import self.adragon.terralyze.data.model.item.CatalogItem
import self.adragon.terralyze.data.repository.ItemCatalog
import self.adragon.terralyze.domain.model.ItemTag

class JsonItemCatalog(itemsJson: String, tagsJson: String) : ItemCatalog {
    private val itemsById: Map<Int, CatalogItem> =
        Json.decodeFromString<List<CatalogItem>>(itemsJson).associateBy { it.id }
    private val itemsByInternalName: Map<String, CatalogItem> =
        itemsById.values.associateBy { it.internalName }
    private val itemTags = Json.decodeFromString<Map<String, List<String>>>(tagsJson)
        .flatMap { (category, names) -> names.map { name -> ItemTag(category, name) } }

    override fun getItemById(id: Int) = itemsById[id]
    override fun getItemByInternalName(internalName: String) = itemsByInternalName[internalName]
    override fun getAllItems() = itemsById.values.toList()

    override fun getAllTags() = itemTags
}