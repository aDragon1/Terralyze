package self.adragon.terralyze.ui.hub

import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import self.adragon.terralyze.domain.model.Player
import self.adragon.terralyze.ui.CaptionText
import self.adragon.terralyze.ui.HoverTooltip
import self.adragon.terralyze.ui.ItemImage
import self.adragon.terralyze.ui.SectionTitle
import self.adragon.terralyze.ui.ValueText
import self.adragon.terralyze.ui.hub.equipment.EquipmentItemUiState
import self.adragon.terralyze.ui.hub.equipment.EquipmentSlotUiState
import self.adragon.terralyze.ui.hub.equipment.EquipmentUiState
import self.adragon.terralyze.ui.hub.equipment.MiscEquipmentSlotUiState
import self.adragon.terralyze.ui.hub.equipment.toUiState
import self.adragon.terralyze.ui.hub.inventory.InventoryItemUiState
import self.adragon.terralyze.ui.hub.inventory.toUiState

private const val COLUMNS = 10
private const val HOTBAR_SIZE = 10
private const val COINS_AND_AMMO_SIZE = 8
private val SLOT_GAP = 4.dp
private val MAX_SLOT_SIZE = 56.dp
private val SCREEN_PADDING = 16.dp

@Composable
fun InventoryScreen(player: Player, modifier: Modifier = Modifier) {
    val inventoryItemsState = remember(player.inventory) {
        player.inventory.map { inventoryItem -> inventoryItem.toUiState() }
    }
    val equipmentState = remember(player.equipment) { player.equipment.toUiState() }

    val hotbar = inventoryItemsState.take(HOTBAR_SIZE)
    val inventory = inventoryItemsState.drop(HOTBAR_SIZE).dropLast(COINS_AND_AMMO_SIZE)
    val coinsAndAmmo = inventoryItemsState.takeLast(COINS_AND_AMMO_SIZE)

    val verticalScrollState = rememberScrollState()
    BoxWithConstraints(modifier.fillMaxSize()) {
        val available = maxWidth - SCREEN_PADDING * 2 - SLOT_GAP * (COLUMNS - 1)
        val slotSize = (available / COLUMNS).coerceAtMost(MAX_SLOT_SIZE)

        Row(Modifier.verticalScroll(verticalScrollState)) {
            EquipmentCard(equipmentState, slotSize)
            InventoryCard(hotbar, inventory, coinsAndAmmo, slotSize)
        }
    }
}

@Composable
private fun EquipmentCard(equipment: EquipmentUiState, slotSize: Dp) = Card {
    Column(
        Modifier.padding(SCREEN_PADDING),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SectionTitle("Equipment")
        SectionTitle("Armor")
        EquipmentGrid(equipment.armor, slotSize)
        SectionTitle("Accessories")
        EquipmentGrid(equipment.accessories, slotSize)
        SectionTitle("Misc")
        MiscEquipmentGrid(equipment.misc, slotSize)
    }
}

@Composable
private fun InventoryCard(
    hotbar: List<InventoryItemUiState>, inventory: List<InventoryItemUiState>,
    coinsAndAmmo: List<InventoryItemUiState>, slotSize: Dp
) = Column(Modifier.padding(SCREEN_PADDING), Arrangement.spacedBy(12.dp)) {
    SectionTitle("Inventory")
    InventoryGrid(hotbar, COLUMNS, slotSize)
    InventoryGrid(inventory, COLUMNS, slotSize)
    SectionTitle("Money & Ammo")
    InventoryGrid(coinsAndAmmo, COINS_AND_AMMO_SIZE, slotSize)
}

@Composable
private fun EquipmentGrid(slots: List<EquipmentSlotUiState>, slotSize: Dp) =
    Column(verticalArrangement = Arrangement.spacedBy(SLOT_GAP)) {
        slots.forEach { slot ->
            Row(horizontalArrangement = Arrangement.spacedBy(SLOT_GAP)) {
                EquipmentItemOverview(slot.main, slotSize)
                EquipmentItemOverview(slot.vanity, slotSize)
                EquipmentItemOverview(slot.dye, slotSize)
            }
        }
    }

@Composable
private fun MiscEquipmentGrid(slots: List<MiscEquipmentSlotUiState>, slotSize: Dp) =
    Column(verticalArrangement = Arrangement.spacedBy(SLOT_GAP)) {
        slots.forEach { slot ->
            Row(horizontalArrangement = Arrangement.spacedBy(SLOT_GAP)) {
                EquipmentItemOverview(slot.main, slotSize)
                EquipmentItemOverview(slot.dye, slotSize)
            }
        }
    }

@Composable
private fun InventoryGrid(slots: List<InventoryItemUiState>, columns: Int, slotSize: Dp) {
    Column(verticalArrangement = Arrangement.spacedBy(SLOT_GAP)) {
        slots.chunked(columns).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(SLOT_GAP)) {
                row.forEach { InventoryItemOverview(it, slotSize) }
            }
        }
    }
}

@Composable
private fun EquipmentItemOverview(itemState: EquipmentItemUiState, slotSize: Dp) =
    ItemSlotFrame(slotSize) {
        when (itemState) {
            EquipmentItemUiState.Empty -> Unit
            is EquipmentItemUiState.Unknown ->
                UnknownItem(itemState.id, Modifier.align(Alignment.BottomEnd))

            is EquipmentItemUiState.Filled ->
                FilledItem(
                    itemState.item.name, itemState.item.id, 1, itemState.item.imageUrl,
                )
        }
    }

@Composable
private fun InventoryItemOverview(itemState: InventoryItemUiState, slotSize: Dp) =
    ItemSlotFrame(slotSize) {
        when (itemState) {
            InventoryItemUiState.Empty -> Unit
            is InventoryItemUiState.Unknown ->
                UnknownItem(itemState.id, Modifier.align(Alignment.BottomEnd))

            is InventoryItemUiState.Filled ->
                FilledItem(
                    itemState.item.name, itemState.item.id, itemState.stack, itemState.item.imageUrl
                )
        }
    }

@Composable
private fun ItemSlotFrame(slotSize: Dp, content: @Composable BoxScope.() -> Unit) {
    Box(
        Modifier.size(slotSize)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(6.dp))
            .padding(SLOT_GAP),
        contentAlignment = Alignment.Center,
        content = content,
    )
}

@Composable
private fun UnknownItem(id: Int, modifier: Modifier = Modifier) =
    CaptionText(id.toString(), modifier.padding(2.dp))

@Composable
private fun FilledItem(name: String, id: Int, stack: Int, imageUrl: String) {
    HoverTooltip("$name (#$id)") { source, isHovered ->
        Box(
            Modifier
                .hoverable(source)
                .fillMaxSize()
                .border(
                    width = 1.dp,
                    color = if (isHovered) Color.Yellow else Color.Transparent,
                    shape = RoundedCornerShape(8.dp)
                ), Alignment.Center
        ) {
            ItemImage(name, imageUrl)
            if (stack > 1)
                ValueText(stack.toString(), Modifier.align(Alignment.BottomEnd).padding(2.dp))
        }
    }
}