package self.adragon.terralyze.ui.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.material3.ripple
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.input.KeyboardType
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
fun EditableContainer(
    onClick: () -> Unit, modifier: Modifier = Modifier, isEditing: Boolean = false,
    enabled: Boolean = true, content: @Composable () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()

    val isHighlighted = isEditing || isHovered || isPressed

    Box(
        modifier.hoverable(interactionSource = interactionSource, enabled = enabled).clickable(
            interactionSource, ripple(), enabled,
            onClick = onClick,
        ).border(
            1.dp, if (isHighlighted) Color.Yellow else Color.Transparent,
            RoundedCornerShape(8.dp),
        )
    ) { content() }
}

@Composable
fun <T> InlineValueEditor(
    value: T, modifier: Modifier = Modifier, content: @Composable (T) -> Unit, onSave: (T) -> Unit,
    editor: @Composable (value: T, onDone: (T) -> Unit, onCancel: () -> Unit) -> Unit
) = Column(modifier) {
    var isEditing by remember { mutableStateOf(false) }
    EditableContainer(isEditing = isEditing, onClick = { isEditing = true }) { content(value) }
    AnimatedVisibility(isEditing) {
        editor(value, { onSave(it); isEditing = false }, { isEditing = false })
    }
}

@Composable
fun InlineTextEditor(
    value: String, content: @Composable (value: String) -> Unit,
    onSave: (String) -> Unit, modifier: Modifier = Modifier
) = InlineValueEditor(
    value, modifier, content, onSave, { initialValue, save, cancel ->
        var draft by remember(initialValue) { mutableStateOf(initialValue) }
        Column {
            val focus = remember { FocusRequester() }
            LaunchedEffect(Unit) { focus.requestFocus() }

            EditorTextField(
                draft, { draft = it },
                modifier = Modifier.focusRequester(focus).onPreviewKeyEvent { event ->
                    if (event.type != KeyEventType.KeyUp) return@onPreviewKeyEvent false
                    when (event.key) {
                        Key.Enter -> {
                            save(draft)
                            true
                        }

                        Key.Escape -> {
                            cancel()
                            true
                        }

                        else -> false
                    }
                }
            )
            Row {
                TextButton(onClick = cancel) { SecondaryText("Cancel") }
                TextButton(onClick = { save(draft) }) { SecondaryText("Save") }
            }
        }
    }
)

@Composable
fun InlineIntEditor(
    value: Int, content: @Composable (Int) -> Unit, onSave: (Int) -> Unit,
    modifier: Modifier = Modifier, placeholder: String = ""
) = InlineValueEditor(
    value, modifier, content, onSave,
    { initialValue, save, cancel ->
        var draft by remember(initialValue) { mutableStateOf(initialValue.toString()) }

        Column {
            val focus = remember { FocusRequester() }
            LaunchedEffect(Unit) { focus.requestFocus() }

            EditorTextField(
                draft, { input -> draft = input }, placeholder = placeholder,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.focusRequester(focus).onPreviewKeyEvent { event ->
                    if (event.type != KeyEventType.KeyUp) return@onPreviewKeyEvent false
                    when (event.key) {
                        Key.Enter -> {
                            draft.toIntOrNull()?.let(save)
                            true
                        }

                        Key.Escape -> {
                            cancel()
                            true
                        }

                        else -> false
                    }
                }
            )

            Row {
                TextButton(onClick = cancel) { SecondaryText("Cancel") }
                TextButton(
                    enabled = draft.toIntOrNull() != null,
                    onClick = { draft.toIntOrNull()?.let(save) }
                ) { SecondaryText("Save") }
            }
        }
    }
)

@Composable
fun <T> InlineDropdown(
    value: T, items: List<T>, content: @Composable (T) -> Unit,
    itemLabel: (T) -> String, onSave: (T) -> Unit, modifier: Modifier = Modifier,
) = Box(modifier) {
    var expanded by remember { mutableStateOf(false) }
    EditableContainer(isEditing = expanded, onClick = { expanded = true }) { content(value) }
    DropdownMenu(expanded, { expanded = false }) {
        items.forEach {
            DropdownMenuItem({ Text(itemLabel(it)) }, { onSave(it); expanded = false })
        }
    }
}

@Composable
fun InlineCurrentMaxEditor(
    current: Int, maximum: Int, label: String, color: Color,
    onSave: (current: Int, maximum: Int) -> Unit, modifier: Modifier = Modifier,
) = InlineValueEditor(
    Pair(current, maximum), modifier, { StatBar(label, it.first, it.second, color) },
    { updated -> onSave(updated.first, updated.second) },
) { initialValue, save, cancel ->
    var currentDraft by remember(initialValue) { mutableStateOf("${initialValue.first}") }
    var maximumDraft by remember(initialValue) { mutableStateOf("${initialValue.second}") }

    val parsedCurrent = currentDraft.toIntOrNull()
    val parsedMaximum = maximumDraft.toIntOrNull()
    val isValid = parsedCurrent != null && parsedMaximum != null && parsedMaximum >= 0

    val currentFocus = remember { FocusRequester() }
    val maximumFocus = remember { FocusRequester() }
    LaunchedEffect(Unit) { currentFocus.requestFocus() }

    Column(
        modifier = Modifier.padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Column(Modifier.weight(1f)) {
                CaptionText("Current")
                EditorTextField(
                    currentDraft, { currentDraft = it },
                    Modifier.fillMaxWidth().focusRequester(currentFocus).onPreviewKeyEvent {
                        if (it.type == KeyEventType.KeyUp && it.key == Key.Enter) {
                            maximumFocus.requestFocus()
                            true
                        } else false
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            Column(Modifier.weight(1f)) {
                CaptionText("Maximum")
                EditorTextField(
                    maximumDraft, { maximumDraft = it },
                    Modifier.fillMaxWidth().focusRequester(maximumFocus).onPreviewKeyEvent {
                        if (it.type != KeyEventType.KeyUp) return@onPreviewKeyEvent false

                        when (it.key) {
                            Key.Enter -> {
                                if (isValid) save(parsedCurrent to parsedMaximum)
                                true
                            }

                            Key.Escape -> {
                                cancel()
                                true
                            }

                            else -> false
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        }
        Row {
            TextButton(onClick = cancel) { SecondaryText("Cancel") }
            TextButton(
                enabled = isValid, onClick = { save(Pair(parsedCurrent!!, parsedMaximum!!)) },
            ) { SecondaryText("Save") }
        }
    }
}

@Composable
fun InlineSlider(
    value: Int, content: @Composable (value: Int) -> Unit, onSave: (Int) -> Unit,
    modifier: Modifier = Modifier
) = InlineValueEditor(
    value, modifier, content, onSave, { initialValue, save, cancel ->
        var draft by remember(initialValue) { mutableStateOf(initialValue) }
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Slider(
                    draft.toFloat(), onValueChange = { draft = it.toInt() },
                    valueRange = 0f..999f, steps = 99, modifier = Modifier.weight(1f)
                )
                ValueText("$draft", modifier = Modifier.weight(1f))
            }
            Row {
                TextButton(onClick = cancel) { SecondaryText("Cancel") }
                TextButton(onClick = { save(draft) }) { SecondaryText("Save") }
            }
        }
    }
)

@Composable
fun EditorTextField(
    value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier,
    placeholder: String = "", keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) = TextField(
    value,
    onValueChange,
    modifier,
    placeholder = { CaptionText(placeholder) },
    singleLine = true,
    keyboardOptions = keyboardOptions
)

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