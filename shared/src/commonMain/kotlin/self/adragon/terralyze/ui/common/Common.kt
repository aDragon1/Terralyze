package self.adragon.terralyze.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import self.adragon.terralyze.ui.hub.ItemImageCache
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun ItemImage(name: String, url: String, modifier: Modifier = Modifier) {
    var image by remember(url) { mutableStateOf(ItemImageCache.peek(url)) }
    LaunchedEffect(url) {
        if (image == null) image = ItemImageCache.load(url)
    }

    image?.let { Image(it, name, modifier) }
}

fun Modifier.wavyBorder(
    color: Color,
    strokeWidth: Dp = 0.5.dp,
    wavelength: Dp = 8.dp,
    amplitude: Dp = 0.5.dp,
): Modifier = drawWithContent {
    drawContent()

    val strokePx = strokeWidth.toPx()
    val wavelengthPx = wavelength.toPx().coerceAtLeast(1f)
    val amplitudePx = amplitude.toPx()
    val inset = strokePx / 2f

    val borderWidth = size.width - strokePx
    val borderHeight = size.height - strokePx

    val path = Path()

    fun drawWave(
        startX: Float, startY: Float, endX: Float, endY: Float,
        horizontal: Boolean,
    ) {
        val length = if (horizontal) endX - startX else endY - startY

        val stepSize = wavelengthPx / 4f
        val steps = (length / stepSize).toInt().coerceAtLeast(1)

        for (index in 0..steps) {
            val progress = index.toFloat() / steps
            val distance = progress * length

            val offset = amplitudePx * sin(
                distance / wavelengthPx * 2.0 * PI,
            ).toFloat()

            val x = if (horizontal) startX + distance else startX + offset
            val y = if (horizontal) startY + offset else startY + distance

            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
    }

    drawWave(inset, inset, inset + borderWidth, inset, true)
    drawWave(inset + borderWidth, inset, inset + borderWidth, inset + borderHeight, false)
    drawWave(inset + borderWidth, inset + borderHeight, inset, inset + borderHeight, true)
    drawWave(inset, inset + borderHeight, inset, inset, false)

    drawPath(path, color, style = Stroke(width = strokePx))
}