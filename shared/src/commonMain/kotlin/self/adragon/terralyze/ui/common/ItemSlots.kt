package self.adragon.terralyze.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import self.adragon.terralyze.ui.hub.ItemImageCache
import self.adragon.terralyze.ui.hub.storage.ItemUiState

@Composable
fun ItemImage(name: String, url: String, modifier: Modifier = Modifier) {
    var image by remember(url) { mutableStateOf(ItemImageCache.peek(url)) }
    LaunchedEffect(url) {
        if (image == null) image = ItemImageCache.load(url)
    }

    image?.let { Image(it, name, modifier) }
}

@Composable
fun SlotGrid(
    itemRows: List<List<ItemUiState>>, slotGap: Dp, slotSize: Dp, indexOffset: Int = 0,
    onClick: (index: Int, itemState: ItemUiState) -> Unit = { _, _ -> }
) = Column(verticalArrangement = Arrangement.spacedBy(slotGap)) {

    itemRows.forEachIndexed { rowIndex, itemRow ->
        Row(horizontalArrangement = Arrangement.spacedBy(slotGap)) {
            itemRow.forEachIndexed { index, state ->
                val currentIndex = indexOffset + index + rowIndex * itemRow.size
                ItemOverview(state, slotGap, slotSize) {
                    onClick(currentIndex, state)
                }
            }
        }
    }
}

@Composable
fun ItemSlotFrame(slotGap: Dp, slotSize: Dp, content: @Composable BoxScope.() -> Unit) = Box(
    Modifier.size(slotSize)
        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(6.dp))
        .padding(slotGap),
    contentAlignment = Alignment.Center,
    content = content,
)

@Composable
fun ItemOverview(
    itemState: ItemUiState, slotGap: Dp, slotSize: Dp,
    onClick: () -> Unit
) = ItemSlotFrame(slotGap, slotSize) {
    when (itemState) {
        ItemUiState.Empty -> Unit
        is ItemUiState.Unknown -> UnknownItem(itemState.id, Modifier.align(Alignment.BottomEnd))
        is ItemUiState.Filled -> FilledItem(itemState)
    }
}

@Composable
fun UnknownItem(id: Int, modifier: Modifier = Modifier) =
    CaptionText(id.toString(), modifier.padding(2.dp))

@Composable
fun FilledItem(itemState: ItemUiState.Filled) {
    val name = itemState.item.name
    val id = itemState.item.id
    val stack = itemState.stack

    val favoritedStr = if (itemState.favorited) "{Favorite}" else ""
    Box(
        Modifier.fillMaxSize()
            .border(
                width = 1.dp,
                color = Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .then(
                if (itemState.favorited) Modifier.wavyBorder(MaterialTheme.colorScheme.tertiary)
                else Modifier
            ),
        Alignment.Center
    ) {
        ItemImage(name, itemState.item.imageUrl)
        if (stack > 1)
            ValueText(
                stack.toString(), Modifier.align(Alignment.BottomEnd).padding(2.dp)
            )
    }
}