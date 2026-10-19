package self.adragon.terralyze.ui.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

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
