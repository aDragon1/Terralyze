package terralyze.data.source.itemcatalog

import kotlinx.serialization.json.Json
import terralyze.data.model.ItemInfo
import terralyze.data.repository.ItemCatalog

class JsonItemCatalog(rawJson: String) : ItemCatalog {

    private val items: Map<Int, ItemInfo> =
        Json.Default.decodeFromString<List<ItemInfo>>(rawJson).associateBy { it.id }

    override fun getItem(id: Int) = items[id]
}