package self.adragon.terralyze.domain.model

import self.adragon.terralyze.data.model.item.Item

data class PlayerStorage(
    val inventory: List<Item>,
    val piggyBank: List<Item>,
    val safe: List<Item>,
    val defendersForge: List<Item>?,
    val voidVault: List<Item>?,
)