package self.adragon.terralyze.ui.hub.storage

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable
import self.adragon.terralyze.domain.model.Player
import self.adragon.terralyze.ui.common.DropDown
import self.adragon.terralyze.ui.common.SectionTitle
import self.adragon.terralyze.ui.common.SlotGrid

private const val COLUMNS = 10
private const val HOTBAR_SIZE = 10
private const val COINS_AND_AMMO_SIZE = 8
private val SLOT_GAP = 4.dp
private val MAX_SLOT_SIZE = 56.dp
private val SCREEN_PADDING = 16.dp

@Serializable
enum class StorageDestination(val label: String) {
    INVENTORY("Inventory"),
    PIGGY_BANK("PIGGY BANK"),
    SAFE("SAFE"),
    DEFENDERS_FORGE("DEFENDER'S FORGE"),
    VOID_VAULT("VOID VAULT")
}

@Composable
fun StorageScreen(player: Player, modifier: Modifier = Modifier) {
    var inventoryItemsState by remember(player) {
        mutableStateOf(player.inventory.map { inventoryItem -> inventoryItem.toUiState() })
    }
    var equipmentState by remember(player) { mutableStateOf(player.equipment.toUiState()) }
    val storage = player.playerStorage

    var currentDestination by rememberSaveable { mutableStateOf(StorageDestination.INVENTORY) }
    val destinations = buildList {
        add(StorageDestination.INVENTORY)
        add(StorageDestination.PIGGY_BANK)
        add(StorageDestination.SAFE)
        if (storage.defendersForge != null) add(StorageDestination.DEFENDERS_FORGE)
        if (storage.voidVault != null) add(StorageDestination.VOID_VAULT)
    }


    val piggyBankState = remember(storage) { storage.piggyBank.map { it.toUiState() } }
    val safeState = remember(storage) { storage.safe.map { it.toUiState() } }
    val defendersForgeState = remember(storage) { storage.defendersForge?.map { it.toUiState() } }
    val voidVaultState = remember(storage) { storage.voidVault?.map { it.toUiState() } }

    Scaffold(
        topBar = {
            Box(
                Modifier.fillMaxWidth().padding(SCREEN_PADDING, 8.dp),
                Alignment.CenterEnd
            ) {
                DropDown(
                    currentDestination, destinations,
                    { currentDestination = it }, { it.label })
            }
        }
    ) { pad ->
        BoxWithConstraints(Modifier.padding(pad).fillMaxSize()) {
            val available = maxWidth - SCREEN_PADDING * 2 - SLOT_GAP * (COLUMNS - 1)
            val slotSize = (available / COLUMNS).coerceAtMost(MAX_SLOT_SIZE)

            when (currentDestination) {
                StorageDestination.INVENTORY ->
                    InventoryScreen(
                        inventoryItemsState, equipmentState, slotSize, modifier
                    ) { index, itemState ->
                        // TODO: It's PoC, refactor it later (to PlayerEditroState? Idk)
//                        if (itemState is ItemUiState.Filled) {
//                            inventoryItemsState = inventoryItemsState.mapIndexed { i, state ->
//                                if (state is ItemUiState.Filled && i == index)
//                                    state.copy(stack = 1234) else state
//                            }
//                        }
                    }

                StorageDestination.PIGGY_BANK -> BankScreen(piggyBankState, "Piggy bank", slotSize)
                StorageDestination.SAFE -> BankScreen(safeState, "Safe", slotSize)
                StorageDestination.DEFENDERS_FORGE ->
                    defendersForgeState?.let { BankScreen(it, "Defender's Forge", slotSize) }

                StorageDestination.VOID_VAULT ->
                    voidVaultState?.let { BankScreen(it, "Void vault", slotSize) }
            }
        }
    }
}

@Composable
private fun BankScreen(slots: List<ItemUiState>, title: String, slotSize: Dp) =
    Column(Modifier.padding(SCREEN_PADDING), Arrangement.spacedBy(12.dp)) {
        SectionTitle(title)
        SlotGrid(slots.chunked(COLUMNS), SLOT_GAP, slotSize)
    }

@Composable
fun InventoryScreen(
    inventoryItemsState: List<ItemUiState>, equipmentState: EquipmentUiState,
    slotSize: Dp, modifier: Modifier, onClick: (index: Int, state: ItemUiState) -> Unit
) {
    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    Row(modifier.verticalScroll(verticalScrollState).horizontalScroll(horizontalScrollState)) {
        EquipmentCard(equipmentState, slotSize)
        InventoryCard(inventoryItemsState, slotSize, onClick)
    }
}

@Composable
private fun EquipmentCard(equipment: EquipmentUiState, slotSize: Dp) {
    fun EquipmentSlotUiState.cells(): List<ItemUiState> = listOfNotNull(main, vanity, dye)

    val armorCells = remember(equipment.armor) { equipment.armor.map { it.cells() } }
    val accessoriesCells =
        remember(equipment.accessories) { equipment.accessories.map { it.cells() } }
    val miscCells = remember(equipment.misc) { equipment.misc.map { it.cells() } }

    Card {
        Column(
            Modifier.padding(SCREEN_PADDING),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SectionTitle("Equipment")
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SectionTitle("Armor")
                    SlotGrid(armorCells, SLOT_GAP, slotSize)
                    SectionTitle("Misc")
                    SlotGrid(accessoriesCells, SLOT_GAP, slotSize)
                }
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SectionTitle("Accessories")
                    SlotGrid(miscCells, SLOT_GAP, slotSize)
                }
            }
        }
    }
}

@Composable
private fun InventoryCard(
    inventoryItemsState: List<ItemUiState>, slotSize: Dp,
    onClick: (index: Int, state: ItemUiState) -> Unit
) = Column(Modifier.padding(SCREEN_PADDING), Arrangement.spacedBy(12.dp)) {
    val hotbar = inventoryItemsState.take(HOTBAR_SIZE)
    val inventory = inventoryItemsState.drop(HOTBAR_SIZE).dropLast(COINS_AND_AMMO_SIZE)
    val coinsAndAmmo = inventoryItemsState.takeLast(COINS_AND_AMMO_SIZE)

    SectionTitle("Inventory")
    SlotGrid(hotbar.chunked(COLUMNS), SLOT_GAP, slotSize, 0, onClick)
    SlotGrid(inventory.chunked(COLUMNS), SLOT_GAP, slotSize, HOTBAR_SIZE, onClick)
    SectionTitle("Money & Ammo")
    SlotGrid(
        coinsAndAmmo.chunked(COINS_AND_AMMO_SIZE), SLOT_GAP, slotSize,
        inventoryItemsState.size - COINS_AND_AMMO_SIZE, onClick
    )
}