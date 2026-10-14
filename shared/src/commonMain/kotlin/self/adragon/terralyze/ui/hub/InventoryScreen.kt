package self.adragon.terralyze.ui.hub

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import self.adragon.terralyze.domain.model.Player
import self.adragon.terralyze.ui.HoverTooltip
import self.adragon.terralyze.ui.ItemImageCache
import self.adragon.terralyze.ui.hub.inventory.InventoryItemUiState
import self.adragon.terralyze.ui.hub.inventory.toUiState

@Composable
fun InventoryScreen(player: Player) {
    val inventoryItems = remember(player.inventory) { player.inventory.map { it.toUiState() } }

    BoxWithConstraints {
        val slotSize = (maxWidth - 9 * 4.dp) / 10

        LazyVerticalGrid(
            GridCells.Fixed(10), Modifier.width(slotSize * 5 + 4.dp * 4),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(inventoryItems) { item -> InventoryItemOverview(item) }
        }
    }
}

@Composable
private fun InventoryItemOverview(itemState: InventoryItemUiState, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.aspectRatio(1f).border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline,
            shape = RoundedCornerShape(6.dp)
        ).padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        when (itemState) {
            InventoryItemUiState.Empty -> Unit
            is InventoryItemUiState.Unknown ->
                UnknownItem(itemState.id, Modifier.align(Alignment.BottomEnd))

            is InventoryItemUiState.Filled -> FilledItem(
                itemState.item.name, itemState.item.id, itemState.stack, itemState.item.imageUrl
            )
        }
    }
}

@Composable
private fun UnknownItem(id: Int, modifier: Modifier) =
    Text("$id", modifier = modifier.padding(2.dp), style = MaterialTheme.typography.labelSmall)


@Composable
private fun FilledItem(name: String, id: Int, stack: Int, imageUrl: String) {
    HoverTooltip("$name (#$id)") {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            ItemImage(name, imageUrl)
            if (stack > 1) {
                Text(
                    "$stack", Modifier.align(Alignment.BottomEnd).padding(2.dp),
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}

@Composable
private fun ItemImage(name: String, url: String) {
    var image by remember(url) { mutableStateOf(ItemImageCache.peek(url)) }
    LaunchedEffect(url) {
        if (image == null) image = ItemImageCache.load(url)
    }

    image?.let { Image(it, name) }
}

