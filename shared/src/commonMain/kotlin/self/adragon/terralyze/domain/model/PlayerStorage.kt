package self.adragon.terralyze.domain.model

import self.adragon.terralyze.data.model.item.BankItem
import self.adragon.terralyze.data.model.item.InventoryItem

data class PlayerStorage(
    val piggyBank: List<BankItem<ItemSlot>>,
    val safe: List<BankItem<ItemSlot>>,
    val defendersForge: List<BankItem<ItemSlot>>?,
    val voidVault: List<InventoryItem<ItemSlot>>?,
)