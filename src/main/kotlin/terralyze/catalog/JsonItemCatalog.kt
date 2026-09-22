package terralyze.catalog

class JsonItemCatalog : ItemCatalog {
    private val items: Map<Int, ItemInfo> = loadItems()

    override fun getItem(id: Int): ItemInfo = items[id] ?: error("Unknown item id: $id")


    fun loadItems(): Map<Int, ItemInfo> {
        return emptyMap()
    }
}