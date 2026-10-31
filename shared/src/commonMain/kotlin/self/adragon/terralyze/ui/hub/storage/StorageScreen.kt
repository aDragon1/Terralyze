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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import self.adragon.terralyze.data.model.item.Item
import self.adragon.terralyze.data.repository.ItemCatalog
import self.adragon.terralyze.ui.common.AppCard
import self.adragon.terralyze.ui.common.DropDown
import self.adragon.terralyze.ui.common.EditableContainer
import self.adragon.terralyze.ui.common.InlineDropdown
import self.adragon.terralyze.ui.common.InlineIntEditor
import self.adragon.terralyze.ui.common.ItemImage
import self.adragon.terralyze.ui.common.LabeledValue
import self.adragon.terralyze.ui.common.SecondaryText
import self.adragon.terralyze.ui.common.SectionTitle
import self.adragon.terralyze.ui.common.SlotGrid
import self.adragon.terralyze.ui.common.ValueText
import self.adragon.terralyze.ui.hub.PlayerEditorIntent
import self.adragon.terralyze.ui.hub.PlayerEditorStateHolder

private const val COLUMNS = 10
private const val HOTBAR_SIZE = 10
private const val COINS_SIZE = 4
private const val AMMO_SIZE = 4
private val SLOT_GAP = 4.dp
private val MAX_SLOT_SIZE = 56.dp
private val SCREEN_PADDING = 12.dp

@Serializable
enum class StorageDestination(val label: String) {
    INVENTORY("Inventory"),
    PIGGY_BANK("PIGGY BANK"),
    SAFE("SAFE"),
    DEFENDERS_FORGE("DEFENDER'S FORGE"),
    VOID_VAULT("VOID VAULT")
}

@Composable
fun StorageScreen(
    playerEditor: PlayerEditorStateHolder, modifier: Modifier = Modifier, catalog: ItemCatalog
) {
    val player by playerEditor.editedPlayer.collectAsState()
    val storage = player.playerStorage

    val equipment = remember(player, catalog) { player.equipment.resolve(catalog) }

    val inventory = remember(storage, catalog) { storage.inventory.map { it.resolve(catalog) } }
    val piggyBank = remember(storage, catalog) { storage.piggyBank.map { it.resolve(catalog) } }
    val safe = remember(storage, catalog) { storage.safe.map { it.resolve(catalog) } }
    val defendersForge =
        remember(storage, catalog) { storage.defendersForge?.map { it.resolve(catalog) } }
    val voidVault = remember(storage, catalog) { storage.voidVault?.map { it.resolve(catalog) } }

    var currentDestination by rememberSaveable { mutableStateOf(StorageDestination.INVENTORY) }
    val destinations = buildList {
        add(StorageDestination.INVENTORY)
        add(StorageDestination.PIGGY_BANK)
        add(StorageDestination.SAFE)
        if (storage.defendersForge != null) add(StorageDestination.DEFENDERS_FORGE)
        if (storage.voidVault != null) add(StorageDestination.VOID_VAULT)
    }

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
                StorageDestination.INVENTORY -> InventoryScreen(
                    inventory, equipment,
                    slotSize, modifier, playerEditor::onIntent, catalog,
                )

                StorageDestination.PIGGY_BANK -> BankScreen(piggyBank, "Piggy bank", slotSize)
                StorageDestination.SAFE -> BankScreen(safe, "Safe", slotSize)
                StorageDestination.DEFENDERS_FORGE ->
                    defendersForge?.let { BankScreen(it, "Defender's Forge", slotSize) }

                StorageDestination.VOID_VAULT ->
                    voidVault?.let { BankScreen(it, "Void vault", slotSize) }
            }
        }
    }
}

@Composable
private fun BankScreen(slots: List<ResolvedItem>, title: String, slotSize: Dp) =
    Column(Modifier.padding(SCREEN_PADDING), Arrangement.spacedBy(SCREEN_PADDING)) {
        SectionTitle(title)
        SlotGrid(slots.chunked(COLUMNS), SLOT_GAP, slotSize)
    }

@Composable
fun InventoryScreen(
    inventory: List<ResolvedItem>, equipment: ResolvedEquipment, slotSize: Dp, modifier: Modifier,
    onIntent: (PlayerEditorIntent) -> Unit, catalog: ItemCatalog
) = Row(
    modifier.verticalScroll(rememberScrollState())
        .horizontalScroll(rememberScrollState()),
    Arrangement.spacedBy(SCREEN_PADDING)
) {
    EquipmentCard(equipment, slotSize)
    InventoryCard(inventory, slotSize, onIntent)

    // TODO: TEMP
    GridSlotEditor(inventory[1], 1, onIntent, catalog)
}

@Composable
private fun EquipmentCard(equipment: ResolvedEquipment, slotSize: Dp) {
    fun ResolvedEquipmentSlot.cells() = listOfNotNull(main, vanity, dye)
    fun ResolvedMiscEquipmentSlot.cells() = listOfNotNull(main, dye)

    val armorCells = remember(equipment.armor) { equipment.armor.map { it.cells() } }
    val accessoriesCells =
        remember(equipment.accessories) { equipment.accessories.map { it.cells() } }
    val miscCells = remember(equipment.misc) { equipment.misc.map { it.cells() } }

    AppCard {
        Column(
            Modifier.padding(SCREEN_PADDING),
            verticalArrangement = Arrangement.spacedBy(SCREEN_PADDING),
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
    inventoryItemsState: List<ResolvedItem>, slotSize: Dp, onIntent: (PlayerEditorIntent) -> Unit
) = AppCard {
    Column(Modifier.padding(SCREEN_PADDING), Arrangement.spacedBy(SCREEN_PADDING)) {
        val hotbar = inventoryItemsState.take(HOTBAR_SIZE)
        val inventory = inventoryItemsState.drop(HOTBAR_SIZE).dropLast(COINS_SIZE + AMMO_SIZE)
        val coins = inventoryItemsState.takeLast(COINS_SIZE + AMMO_SIZE).dropLast(AMMO_SIZE)
        val ammo = inventoryItemsState.takeLast(AMMO_SIZE)

        SectionTitle("Inventory")
        SlotGrid(hotbar.chunked(COLUMNS), SLOT_GAP, slotSize)
        SlotGrid(inventory.chunked(COLUMNS), SLOT_GAP, slotSize)

        Row(horizontalArrangement = Arrangement.spacedBy(SCREEN_PADDING)) {
            Column {
                SectionTitle("Money")
                SlotGrid(coins.chunked(COINS_SIZE), SLOT_GAP, slotSize)
            }
            Column {
                SectionTitle("Ammo")
                SlotGrid(ammo.chunked(COINS_SIZE), SLOT_GAP, slotSize)
            }
        }
    }
}
