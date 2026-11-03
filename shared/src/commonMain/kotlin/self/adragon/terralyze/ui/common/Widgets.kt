package self.adragon.terralyze.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.round

@Composable
fun AppCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) = Card(
    modifier = modifier,
    shape = RoundedCornerShape(10.dp),
//    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
) { content() }

@Composable
fun HoverTooltip(hint: String, content: @Composable () -> Unit) {
    val tooltipState = rememberTooltipState()
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
    ) { content() }
}

@Composable
fun SearchTextField(
    value: String, onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search...",
    keyboardOptions: KeyboardOptions = KeyboardOptions()
) = OutlinedTextField(
    value, onValueChange, modifier, placeholder = { CaptionText(placeholder) },
    leadingIcon = { Text("⌕") }, trailingIcon = {
        if (value.isNotEmpty()) IconButton(onClick = { onValueChange("") }) { Text("×") }
    }, singleLine = true, keyboardOptions = keyboardOptions
)

@Composable
fun <T> FilterChipRow(
    selected: T, items: List<T>, onSelect: (T) -> Unit, itemLabel: (T) -> String,
    modifier: Modifier = Modifier,
) = Row(
    modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), Arrangement.spacedBy(8.dp),
) { items.forEach { FilterChip(it == selected, { onSelect(it) }, { Text(itemLabel(it)) }) } }

@Composable
fun LabeledValue(name: String, value: String) = Surface(
    Modifier.widthIn(min = 190.dp),
    RoundedCornerShape(10.dp),
    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
) {
    Row(
        Modifier.padding(12.dp, 8.dp),
        Arrangement.spacedBy(12.dp), Alignment.CenterVertically,
    ) {
        SecondaryText(name, Modifier.weight(1f))
        ValueText(value)
    }
}

@Composable
fun StatBar(
    label: String, value: Int, max: Int, color: Color,
    modifier: Modifier = Modifier, showPercent: Boolean = false
) {
    val fraction = if (max > 0) (value.toFloat() / max).coerceIn(0f, 1f) else 0f
    Column(modifier, Arrangement.spacedBy(6.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            SecondaryText(label)
            val percentText =
                if (showPercent) " · ${round(fraction * 100 * 100) / 100} %" else ""
            ValueText("$value / $max $percentText")
        }
        RoundedProgressBar(
            fraction, color,
            MaterialTheme.colorScheme.surfaceVariant,
            Modifier.fillMaxWidth().height(10.dp)
        )
    }
}

@Composable
private fun RoundedProgressBar(
    progress: Float, color: Color, trackColor: Color, modifier: Modifier = Modifier
) = Canvas(modifier) {
    val radius = size.height / 2
    val fraction = progress.coerceIn(0f, 1f)

    drawRoundRect(color = trackColor, cornerRadius = CornerRadius(radius))

    if (fraction > 0f) {
        val progressWidth = size.width * fraction

        drawRoundRect(
            color, size = Size(progressWidth, size.height), cornerRadius = CornerRadius(radius)
        )

        if (fraction < 1f && progressWidth > radius)
            drawRect(color, Offset(progressWidth - radius, 0f), Size(radius, size.height))
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

@Composable
fun <T> GroupedDropDown(
    selected: T, items: List<T>, onSelect: (T) -> Unit, groupLabel: (T) -> String,
    itemLabel: (T) -> String, modifier: Modifier = Modifier, enabled: Boolean = true
) = Box(modifier) {
    var expanded by remember { mutableStateOf(false) }
    val groupedItems = remember(items, groupLabel) {
        items.groupBy(groupLabel)
    }
    TextButton({ expanded = true }, enabled = enabled) {
        Text(itemLabel(selected))
        Spacer(Modifier.width(8.dp))
        Text("▾")
    }
    DropdownMenu(expanded, { expanded = false }) {
        groupedItems.forEach { (name, items) ->
            DropdownMenuItem(
                {
                    Text(name, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }, {}, enabled = false
            )

            items.forEach { item ->
                DropdownMenuItem(
                    {
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