package self.adragon.terralyze.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import self.adragon.terralyze.ui.hub.ItemImageCache

@Composable
fun HoverTooltip(
    hint: String,
    content: @Composable (source: MutableInteractionSource, isHovered: Boolean) -> Unit
) {

    val tooltipState = rememberTooltipState()
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
            TooltipAnchorPosition.Above
        ),
        {
            Box(
                modifier = Modifier.shadow(4.dp, RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.DarkGray.copy(alpha = 0.9f))
                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                    .padding(12.dp, 8.dp)
            ) {
                Column(Modifier.padding(4.dp)) {
                    Text(hint, style = MaterialTheme.typography.titleSmall)
                }
            }
        },
        tooltipState,
    ) { content(interactionSource, isHovered) }
}

@Composable
fun HoverTooltip(hint: String, content: @Composable () -> Unit) =
    HoverTooltip(hint) { _, _ -> content() }

@Composable
fun ItemImage(name: String, url: String, modifier: Modifier = Modifier) {
    var image by remember(url) { mutableStateOf(ItemImageCache.peek(url)) }
    LaunchedEffect(url) {
        if (image == null) image = ItemImageCache.load(url)
    }

    image?.let { Image(it, name, modifier) }
}

@Composable
fun SectionTitle(text: String, modifier: Modifier = Modifier) = Text(
    text = text, modifier = modifier, style = MaterialTheme.typography.titleMedium
)

@Composable
fun SecondaryText(text: String, modifier: Modifier = Modifier) = Text(
    text = text, modifier = modifier,
    color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium
)

@Composable
fun ValueText(text: String, modifier: Modifier = Modifier) = Text(
    text = text, modifier = modifier,
    style = MaterialTheme.typography.labelLarge
)

@Composable
fun CaptionText(text: String, modifier: Modifier = Modifier) = Text(
    text, modifier,
    color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelSmall
)