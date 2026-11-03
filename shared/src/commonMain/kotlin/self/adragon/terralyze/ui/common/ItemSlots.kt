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
import self.adragon.terralyze.ui.hub.editor.SlotLocation
import self.adragon.terralyze.ui.hub.storage.ItemState
import self.adragon.terralyze.ui.hub.storage.ResolvedItem

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
    itemRows: List<List<ResolvedItem>>, locationRows: List<List<SlotLocation>>,
    slotGap: Dp, slotSize: Dp, selectedLocation: SlotLocation?, onClick: (SlotLocation) -> Unit
) = Column(verticalArrangement = Arrangement.spacedBy(slotGap)) {
    itemRows.forEachIndexed { i, row ->
        Row(horizontalArrangement = Arrangement.spacedBy(slotGap)) {
            row.forEachIndexed { j, item ->
                val location = locationRows[i][j]
                ItemOverview(item, slotGap, slotSize, location == selectedLocation)
                { onClick(location) }
            }
        }
    }
}

@Composable
fun ItemSlotFrame(
    slotGap: Dp,
    slotSize: Dp,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) = Box(
    modifier.size(slotSize)
        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(6.dp))
        .padding(slotGap),
    Alignment.Center,
    content = content
)

@Composable
fun ItemOverview(
    item: ResolvedItem, slotGap: Dp, slotSize: Dp, isSelected: Boolean = false,
    onClick: () -> Unit = {},
) = EditableContainer(onClick, isEditing = isSelected) {
    ItemSlotFrame(slotGap, slotSize) {
        when (item.state) {
            ItemState.EMPTY -> Unit
            ItemState.UNKNOWN ->
                UnknownItem(item.item.id, Modifier.align(Alignment.BottomEnd))

            ItemState.KNOWN -> FilledItem(item)
        }
    }
}

@Composable
fun UnknownItem(id: Int?, modifier: Modifier = Modifier) =
    CaptionText("$id", modifier.padding(2.dp))

@Composable
fun FilledItem(item: ResolvedItem) {
    val name = item.catalogItem!!.name
    val stack = item.item.stack

    Box(
        Modifier.fillMaxSize().border(
            width = 1.dp,
            color = Color.Transparent,
            shape = RoundedCornerShape(8.dp)
        ).then(
            if (item.item.favorite) Modifier.wavyBorder(MaterialTheme.colorScheme.tertiary)
            else Modifier
        ),
        Alignment.Center
    ) {
        ItemImage(name, item.catalogItem.imageUrl)
        if (stack > 1) ValueText("$stack", Modifier.align(Alignment.BottomEnd).padding(2.dp))
    }
}