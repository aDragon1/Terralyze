package terralyze.data.repository

import terralyze.data.model.ItemInfo

interface ItemCatalog {
    fun getItemById(id: Int): ItemInfo?
    fun getItemByInternalName(internalName: String): ItemInfo?
}