package terralyze.data.source.itemcatalog

import kotlinx.serialization.json.Json
import terralyze.data.model.ItemInfo
import terralyze.data.repository.ItemCatalog

class JsonItemCatalog(rawJson: String) : ItemCatalog {

    private val itemsById: Map<Int, ItemInfo> = Json.decodeFromString<List<ItemInfo>>(rawJson).associateBy { it.id }
    private val itemsByInternalName: Map<String, ItemInfo> = itemsById.values.associateBy { it.internalName }


    override fun getItemById(id: Int) = itemsById[id]
    override fun getItemByInternalName(internalName: String) = itemsByInternalName[internalName]
}