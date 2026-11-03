package self.adragon.terralyze.ui.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun FloatingContainer(
    visible: Boolean, modifier: Modifier = Modifier, alignment: Alignment = Alignment.BottomCenter,
    enter: EnterTransition = slideInVertically { it / 2 } + fadeIn(),
    exit: ExitTransition = slideOutVertically { it / 2 } + fadeOut(),
    content: @Composable () -> Unit
) = Box(modifier.fillMaxSize(), alignment) {
    AnimatedVisibility(visible, enter = enter, exit = exit) { content() }
}

@Composable
fun FloatingCard(
    visible: Boolean, modifier: Modifier = Modifier, alignment: Alignment = Alignment.BottomCenter,
    shape: Shape = RoundedCornerShape(16.dp),
    elevation: Dp = 8.dp,
    content: @Composable () -> Unit
) = FloatingContainer(visible, alignment = alignment) {
    Surface(
        modifier.padding(16.dp), shape, tonalElevation = 6.dp, shadowElevation = elevation
    ) { content() }
}
