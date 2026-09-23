package terralyze.data.repository

import terralyze.data.model.ItemInfo

interface ItemCatalog {
    fun getItem(id:Int): ItemInfo?
}