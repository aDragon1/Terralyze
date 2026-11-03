package self.adragon.terralyze.ui.common

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties


private val POPUP_GAP = 4.dp

private class BelowAnchorPositionProvider(private val gapPx: Int) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect, windowSize: IntSize,
        layoutDirection: LayoutDirection, popupContentSize: IntSize,
    ): IntOffset {
        val maxX = (windowSize.width - popupContentSize.width).coerceAtLeast(0)
        val x = anchorBounds.left.coerceIn(0, maxX)

        val below = anchorBounds.bottom + gapPx
        val y = if (below + popupContentSize.height <= windowSize.height) below
        else (anchorBounds.top - gapPx - popupContentSize.height).coerceAtLeast(0)

        return IntOffset(x, y)
    }
}

@Composable
private fun BelowAnchorPopup(onDismiss: () -> Unit, content: @Composable () -> Unit) {
    val gapPx = with(LocalDensity.current) { POPUP_GAP.roundToPx() }
    Popup(
        remember(gapPx) { BelowAnchorPositionProvider(gapPx) }, onDismiss,
        PopupProperties(focusable = true), content,
    )
}

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
            interactionSource, ripple(), enabled, onClick = onClick,
        ).border(
            1.dp, if (isHighlighted) Color.Yellow else Color.Transparent,
            RoundedCornerShape(8.dp),
        )
    ) { content() }
}

@Composable
fun <T> InlineValueEditor(
    value: T, modifier: Modifier = Modifier, content: @Composable (T) -> Unit, onSave: (T) -> Unit,
    editor: @Composable (value: T, onDone: (T) -> Unit, onCancel: () -> Unit) -> Unit,
) = Box(modifier) {
    var isEditing by remember { mutableStateOf(false) }
    EditableContainer(isEditing = isEditing, onClick = { isEditing = true }) { content(value) }
    if (isEditing) BelowAnchorPopup(onDismiss = { isEditing = false }) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            tonalElevation = 6.dp, shadowElevation = 8.dp,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        ) {
            Box(Modifier.padding(8.dp)) {
                editor(value, { onSave(it); isEditing = false }, { isEditing = false })
            }
        }
    }
}

@Composable
private fun EditorLayout(
    onCancel: () -> Unit, onSave: () -> Unit, width: Dp, saveEnabled: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) = Column(Modifier.width(width), Arrangement.spacedBy(6.dp)) {
    content()
    Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(4.dp, Alignment.End)) {
        TextButton(onCancel, contentPadding = PaddingValues(horizontal = 10.dp)) {
            SecondaryText("Cancel")
        }
        TextButton(
            onSave, enabled = saveEnabled, contentPadding = PaddingValues(horizontal = 10.dp)
        ) { SecondaryText("Save") }
    }
}

@Composable
fun EditorTextField(
    value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier,
    placeholder: String = "", keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    autoFocus: Boolean = false,
) {
    val focus = remember { FocusRequester() }
    LaunchedEffect(Unit) { focus.requestFocus() }

    BasicTextField(
        value, onValueChange,
        modifier = modifier
            .then(if (autoFocus) Modifier.focusRequester(focus) else Modifier)
            .height(36.dp)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(6.dp))
            .padding(horizontal = 10.dp),
        textStyle = MaterialTheme.typography.bodyMedium
            .copy(color = MaterialTheme.colorScheme.onSurface),
        singleLine = true,
        keyboardOptions = keyboardOptions,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        decorationBox = { inner ->
            Box(Modifier.fillMaxHeight(), contentAlignment = Alignment.CenterStart) {
                if (value.isEmpty() && placeholder.isNotEmpty()) CaptionText(placeholder)
                inner()
            }
        },
    )
}

private fun Modifier.onEnterEscape(onEnter: () -> Unit, onEscape: () -> Unit) =
    onPreviewKeyEvent { event ->
        if (event.type != KeyEventType.KeyUp) return@onPreviewKeyEvent false
        when (event.key) {
            Key.Enter -> {
                onEnter(); true
            }

            Key.Escape -> {
                onEscape(); true
            }

            else -> false
        }
    }

@Composable
fun InlineTextEditor(
    value: String, content: @Composable (String) -> Unit,
    onSave: (String) -> Unit, modifier: Modifier = Modifier,
) = InlineValueEditor(value, modifier, content, onSave) { initial, save, cancel ->
    var draft by remember(initial) { mutableStateOf(initial) }

    EditorLayout(onCancel = cancel, onSave = { save(draft) }, width = 200.dp) {
        EditorTextField(
            draft, { draft = it },
            Modifier.fillMaxWidth().onEnterEscape({ save(draft) }, cancel),
            autoFocus = true,
        )
    }
}

@Composable
fun InlineIntEditor(
    value: Int, content: @Composable (Int) -> Unit, onSave: (Int) -> Unit,
    modifier: Modifier = Modifier, placeholder: String = "",
) = InlineValueEditor(value, modifier, content, onSave) { initial, save, cancel ->
    var draft by remember(initial) { mutableStateOf(initial.toString()) }
    val parsed = draft.toIntOrNull()

    EditorLayout(
        onCancel = cancel, onSave = { parsed?.let(save) },
        width = 140.dp, saveEnabled = parsed != null,
    ) {
        EditorTextField(
            draft, { draft = it },
            Modifier.fillMaxWidth().onEnterEscape({ parsed?.let(save) }, cancel),
            placeholder = placeholder,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            autoFocus = true,
        )
    }
}

@Composable
fun <T> InlineDropdown(
    value: T, items: List<T>, content: @Composable (T) -> Unit, itemLabel: (T) -> String,
    onSave: (T) -> Unit, modifier: Modifier = Modifier
) = Box(modifier) {
    var expanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    EditableContainer(isEditing = expanded, onClick = { expanded = true }) {
        content(value)
    }
    val filteredItems = remember(items, searchQuery) {
        if (searchQuery.isNotBlank()) items.filter { itemLabel(it).contains(searchQuery, true) }
        else items
    }

    if (expanded) BelowAnchorPopup(onDismiss = { expanded = false; searchQuery = "" }) {
        LazyColumn(
            Modifier.widthIn(190.dp, 280.dp).heightIn(max = 300.dp).background(
                MaterialTheme.colorScheme.surface,
                RoundedCornerShape(8.dp)
            )
        ) {
            item {
                SearchTextField(
                    searchQuery, { searchQuery = it },
                    Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                )
            }
            items(filteredItems) {
                DropdownMenuItem(
                    { Text(itemLabel(it)) },
                    { onSave(it); expanded = false; searchQuery = "" }
                )
            }
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
) { initial, save, cancel ->
    var currentDraft by remember(initial) { mutableStateOf("${initial.first}") }
    var maximumDraft by remember(initial) { mutableStateOf("${initial.second}") }
    val parsedCurrent = currentDraft.toIntOrNull()
    val parsedMaximum = maximumDraft.toIntOrNull()
    val focusRequester = remember { FocusRequester() }

    val doSave: () -> Unit = {
        if (parsedCurrent != null && parsedMaximum != null) save(parsedCurrent to parsedMaximum)
    }
    val number = KeyboardOptions(keyboardType = KeyboardType.Number)

    EditorLayout(
        cancel, doSave, 220.dp, parsedCurrent != null && parsedMaximum != null,
    ) {
        Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
            Column(Modifier.weight(1f)) {
                CaptionText("Current")
                EditorTextField(
                    currentDraft, { currentDraft = it },
                    Modifier.fillMaxWidth()
                        .onEnterEscape({ focusRequester.requestFocus() }, cancel),
                    keyboardOptions = number, autoFocus = true,
                )
            }
            Column(Modifier.weight(1f)) {
                CaptionText("Maximum")
                EditorTextField(
                    maximumDraft, { maximumDraft = it },
                    Modifier.fillMaxWidth().focusRequester(focusRequester)
                        .onEnterEscape(doSave, cancel),
                    keyboardOptions = number,
                )
            }
        }
    }
}

@Composable
fun InlineSlider(
    current: Int, maximum: Int, content: @Composable (value: Int) -> Unit, onSave: (Int) -> Unit,
    modifier: Modifier = Modifier
) = InlineValueEditor(
    current, modifier, content, onSave, { initialValue, save, cancel ->
        var draft by remember(initialValue) { mutableStateOf(initialValue) }
        val doSave: () -> Unit = { save(draft) }
        val number = KeyboardOptions(keyboardType = KeyboardType.Number)

        val range = 0f..maximum.toFloat()

        EditorLayout(cancel, doSave, 300.dp) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Slider(
                    draft.toFloat(), { draft = it.toInt() },
                    Modifier.weight(1f), valueRange = range, steps = maximum - 1,
                )
                EditorTextField(
                    "$draft", { draft = it.toInt() },
                    modifier = Modifier.weight(1f), keyboardOptions = number
                )
            }
        }
    }
)