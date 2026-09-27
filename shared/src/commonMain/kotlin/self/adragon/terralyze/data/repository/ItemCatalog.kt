package self.adragon.terralyze.data.repository

import self.adragon.terralyze.data.model.ItemInfo

interface ItemCatalog {
    fun getItemById(id: Int): ItemInfo?
    fun getItemByInternalName(internalName: String): ItemInfo?
}