package self.adragon.terralyze.ui.hub

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import self.adragon.terralyze.domain.model.Player
import self.adragon.terralyze.ui.hub.inventory.InventoryItemUiState
import self.adragon.terralyze.ui.hub.inventory.toUiState
import terralyze.shared.generated.resources.Res

@Composable
fun InventoryScreen(player: Player) {
    val inventoryItems =
        remember(player.inventory) { player.inventory.map { it.toUiState() } }

    BoxWithConstraints {
        val slotSize = (maxWidth - 9 * 4.dp) / 10

        LazyVerticalGrid(
            columns = GridCells.Fixed(10),
            modifier = Modifier.width(slotSize * 5 + 4.dp * 4),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(inventoryItems) { item ->
                InventoryItemOverview(item)
            }
        }
    }
}

@Composable
private fun InventoryItemOverview(item: InventoryItemUiState, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.aspectRatio(1f).border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline,
            shape = RoundedCornerShape(6.dp)
        ).padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        when (item) {
            InventoryItemUiState.Empty -> {}
            is InventoryItemUiState.Unknown -> {
                Text(
                    text = "${item.id}",
                    modifier = Modifier.align(Alignment.BottomEnd).padding(2.dp),
                    style = MaterialTheme.typography.labelSmall
                )
            }

            is InventoryItemUiState.Filled -> {
                val fileName = item.item.imageUrl
                ItemImage(fileName, "Description of $fileName")
                if (item.stack > 1)
                    Text(
                        text = item.stack.toString(),
                        modifier = Modifier.align(Alignment.BottomEnd).padding(2.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
            }
        }
    }
}

@Composable
fun ItemImage(
    fileName: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    val image by produceState<ImageBitmap?>(
        initialValue = null,
        key1 = fileName,
    ) {
        value = runCatching {
            Res.readBytes("files/images/$fileName").decodeToImageBitmap()
        }.fold(onSuccess = { it }, onFailure = { error(it) }) // TODO исправить на нормальный error-handling
    }

    image?.let { Image(it, contentDescription, modifier) }
}