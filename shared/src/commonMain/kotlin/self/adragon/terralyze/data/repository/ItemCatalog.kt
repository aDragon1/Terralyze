package self.adragon.terralyze.data.repository

import self.adragon.terralyze.data.model.item.CatalogItem
import self.adragon.terralyze.domain.model.ItemTag

interface ItemCatalog {
    fun getItemById(id: Int): CatalogItem?
    fun getItemByInternalName(internalName: String): CatalogItem?
    fun getAllItems(): List<CatalogItem>

    fun getAllTags(): List<ItemTag>
}