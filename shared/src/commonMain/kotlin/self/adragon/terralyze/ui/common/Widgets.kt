package self.adragon.terralyze.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
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

@Composable
fun HoverTooltip(
    hint: String,
    content: @Composable (source: MutableInteractionSource, isPressed:Boolean, isHovered: Boolean) -> Unit
) {
    val tooltipState = rememberTooltipState()
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()

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
    ) { content(interactionSource, isPressed, isHovered) }
}

@Composable
fun StatBar(label: String, value: Int, max: Int, color: Color, modifier: Modifier = Modifier) {
    val fraction = if (max > 0) (value.toFloat() / max).coerceIn(0f, 1f) else 0f
    Column(modifier, Arrangement.spacedBy(6.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            SecondaryText(label)
            ValueText("$value / $max")
        }
        LinearProgressIndicator(
            { fraction }, Modifier.fillMaxWidth().height(10.dp),
            color, MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

@Composable
fun <T> DropDown(
    selected: T, items: List<T>, onSelect: (T) -> Unit, itemLabel: (T) -> String,
    modifier: Modifier = Modifier, enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier) {
        TextButton(onClick = { expanded = true }, enabled = enabled) {
            Text(itemLabel(selected))
            Spacer(Modifier.width(8.dp))
            Text("▾")
        }
        DropdownMenu(expanded, { expanded = false }) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(if (item == selected) "✓" else "", Modifier.width(24.dp))
                            Text(itemLabel(item))
                        }
                    },
                    onClick = {
                        onSelect(item)
                        expanded = false
                    }
                )
            }
        }
    }
}