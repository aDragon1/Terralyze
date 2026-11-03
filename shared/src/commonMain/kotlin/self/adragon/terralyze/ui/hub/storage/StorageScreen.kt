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
import self.adragon.terralyze.data.repository.ItemCatalog
import self.adragon.terralyze.ui.common.AppCard
import self.adragon.terralyze.ui.common.DropDown
import self.adragon.terralyze.ui.common.SectionTitle
import self.adragon.terralyze.ui.common.SlotGrid
import self.adragon.terralyze.ui.hub.editor.EquipmentPart
import self.adragon.terralyze.ui.hub.editor.EquipmentType
import self.adragon.terralyze.ui.hub.editor.PlayerEditorStateHolder
import self.adragon.terralyze.ui.hub.editor.SlotLocation
import self.adragon.terralyze.ui.hub.editor.StorageType
import self.adragon.terralyze.ui.hub.editor.itemAt

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
fun StorageScreen(playerEditor: PlayerEditorStateHolder, catalog: ItemCatalog) {
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
    var selectedLocation by remember { mutableStateOf<SlotLocation?>(null) }

    Scaffold(
        topBar = {
            Box(
                Modifier.fillMaxWidth().padding(SCREEN_PADDING, 8.dp),
                Alignment.CenterEnd
            ) {
                DropDown(
                    currentDestination, destinations, { currentDestination = it }, { it.label })
            }
        }) { pad ->
        BoxWithConstraints(Modifier.padding(pad).fillMaxSize()) {
            val available = maxWidth - SCREEN_PADDING * 2 - SLOT_GAP * (COLUMNS - 1)
            val slotSize = (available / COLUMNS).coerceAtMost(MAX_SLOT_SIZE)

            val selectedItem = selectedLocation?.let { location ->
                when (location) {
                    is SlotLocation.StorageLocation -> when (location.type) {
                        StorageType.INVENTORY -> inventory.getOrNull(location.index)
                        StorageType.PIGGY_BANK -> piggyBank.getOrNull(location.index)
                        StorageType.SAFE -> safe.getOrNull(location.index)
                        StorageType.DEFENDERS_FORGE -> defendersForge?.getOrNull(location.index)
                        StorageType.VOID_VAULT -> voidVault?.getOrNull(location.index)
                    }

                    is SlotLocation.EquipmentLocation -> equipment.itemAt(location)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(SCREEN_PADDING)) {
                when (currentDestination) {
                    StorageDestination.INVENTORY ->
                        InventoryScreen(inventory, equipment, slotSize, selectedLocation)
                        { selectedLocation = it }

                    StorageDestination.PIGGY_BANK ->
                        BankScreen(
                            piggyBank, "Piggy bank", slotSize, selectedLocation,
                            StorageType.PIGGY_BANK
                        ) { selectedLocation = it }

                    StorageDestination.SAFE ->
                        BankScreen(safe, "Safe", slotSize, selectedLocation, StorageType.SAFE)
                        { selectedLocation = it }

                    StorageDestination.DEFENDERS_FORGE -> defendersForge?.let {
                        BankScreen(
                            it, "Defender's Forge", slotSize, selectedLocation,
                            StorageType.DEFENDERS_FORGE
                        ) { loc -> selectedLocation = loc }
                    }

                    StorageDestination.VOID_VAULT -> voidVault?.let {
                        BankScreen(
                            it, "Void vault", slotSize, selectedLocation, StorageType.VOID_VAULT
                        ) { loc -> selectedLocation = loc }
                    }
                }
                GridSlotEditor(selectedItem, selectedLocation, playerEditor::onIntent, catalog)
            }
        }
    }
}

@Composable
private fun BankScreen(
    items: List<ResolvedItem>, title: String, slotSize: Dp, selectedLocation: SlotLocation?,
    type: StorageType, onClick: (SlotLocation) -> Unit
) = Column(Modifier.padding(SCREEN_PADDING), Arrangement.spacedBy(SCREEN_PADDING)) {
    val chunkedItems = items.chunked(COLUMNS)
    val locations = List(items.size) { SlotLocation.StorageLocation(type, it) }.chunked(COLUMNS)
    SectionTitle(title)
    SlotGrid(chunkedItems, locations, SLOT_GAP, slotSize, selectedLocation, onClick)
}

@Composable
fun InventoryScreen(
    inventory: List<ResolvedItem>, equipment: ResolvedEquipment, slotSize: Dp,
    selectedLocation: SlotLocation?, onClick: (SlotLocation) -> Unit
) = Row(
    Modifier.verticalScroll(rememberScrollState()).horizontalScroll(rememberScrollState()),
    Arrangement.spacedBy(SCREEN_PADDING)
) {
    EquipmentCard(equipment, slotSize, selectedLocation, onClick)
    InventoryCard(inventory, slotSize, selectedLocation, onClick)
}

@Composable
private fun EquipmentCard(
    equipment: ResolvedEquipment, slotSize: Dp, selectedLocation: SlotLocation?,
    onClick: (SlotLocation) -> Unit
) {
    fun equipmentLocations(type: EquipmentType, size: Int): List<List<SlotLocation>> {
        val parts = when (type) {
            EquipmentType.ARMOR, EquipmentType.ACCESSORIES ->
                listOf(EquipmentPart.MAIN, EquipmentPart.VANITY, EquipmentPart.DYE)

            EquipmentType.MISC -> listOf(EquipmentPart.MAIN, EquipmentPart.DYE)
        }
        return List(size) { parts.map { part -> SlotLocation.EquipmentLocation(type, part, it) } }
    }

    fun ResolvedEquipmentSlot.cells() = listOfNotNull(main, vanity, dye)
    fun ResolvedMiscEquipmentSlot.cells() = listOfNotNull(main, dye)

    val armorLocations = remember(equipment.armor.size) {
        equipmentLocations(EquipmentType.ARMOR, equipment.armor.size)
    }
    val accessoriesLocations = remember(equipment.accessories.size) {
        equipmentLocations(EquipmentType.ACCESSORIES, equipment.accessories.size)
    }
    val miscLocations = remember(equipment.misc.size) {
        equipmentLocations(EquipmentType.MISC, equipment.misc.size)
    }

    val armorCells = remember(equipment, armorLocations) { equipment.armor.map { it.cells() } }
    val accessoriesCells =
        remember(equipment, accessoriesLocations) { equipment.accessories.map { it.cells() } }
    val miscCells = remember(equipment, miscLocations) { equipment.misc.map { it.cells() } }

    AppCard {
        Column(
            Modifier.padding(SCREEN_PADDING),
            verticalArrangement = Arrangement.spacedBy(SCREEN_PADDING),
        ) {
            SectionTitle("Equipment")
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SectionTitle("Armor")
                    SlotGrid(
                        armorCells, armorLocations, SLOT_GAP, slotSize,
                        selectedLocation, onClick
                    )
                    SectionTitle("Misc")
                    SlotGrid(
                        miscCells, miscLocations, SLOT_GAP,
                        slotSize, selectedLocation, onClick
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SectionTitle("Accessories")
                    SlotGrid(
                        accessoriesCells,
                        accessoriesLocations,
                        SLOT_GAP,
                        slotSize,
                        selectedLocation,
                        onClick
                    )
                }
            }
        }
    }
}

@Composable
private fun InventoryCard(
    inventory: List<ResolvedItem>, slotSize: Dp, selectedLocation: SlotLocation?,
    onClick: (SlotLocation) -> Unit
) = AppCard {
    val hotbar = inventory.take(HOTBAR_SIZE)
    val regularInventory = inventory.drop(HOTBAR_SIZE).dropLast(COINS_SIZE + AMMO_SIZE)
    val coins = inventory.takeLast(COINS_SIZE + AMMO_SIZE).dropLast(AMMO_SIZE)
    val ammo = inventory.takeLast(AMMO_SIZE)

    val hotbarLocations =
        List(hotbar.size) { SlotLocation.StorageLocation(StorageType.INVENTORY, it) }
    val inventoryLocations = List(regularInventory.size) {
        SlotLocation.StorageLocation(StorageType.INVENTORY, HOTBAR_SIZE + it)
    }

    val coinsStartIndex = inventory.size - COINS_SIZE - AMMO_SIZE
    val coinLocations = List(coins.size) {
        SlotLocation.StorageLocation(StorageType.INVENTORY, coinsStartIndex + it)
    }

    val ammoStartIndex = inventory.size - AMMO_SIZE
    val ammoLocations = List(ammo.size) {
        SlotLocation.StorageLocation(StorageType.INVENTORY, ammoStartIndex + it)
    }

    Column(Modifier.padding(SCREEN_PADDING), Arrangement.spacedBy(SCREEN_PADDING)) {
        SectionTitle("Inventory")
        SlotGrid(
            hotbar.chunked(COLUMNS), hotbarLocations.chunked(COLUMNS),
            SLOT_GAP, slotSize, selectedLocation, onClick,
        )
        SlotGrid(
            regularInventory.chunked(COLUMNS), inventoryLocations.chunked(COLUMNS),
            SLOT_GAP, slotSize, selectedLocation, onClick,
        )

        Row(horizontalArrangement = Arrangement.spacedBy(SCREEN_PADDING)) {
            Column {
                SectionTitle("Money")
                SlotGrid(
                    coins.chunked(COINS_SIZE), coinLocations.chunked(COINS_SIZE),
                    SLOT_GAP, slotSize, selectedLocation, onClick
                )
            }

            Column {
                SectionTitle("Ammo")
                SlotGrid(
                    ammo.chunked(AMMO_SIZE), ammoLocations.chunked(AMMO_SIZE),
                    SLOT_GAP, slotSize, selectedLocation, onClick
                )
            }
        }
    }
}