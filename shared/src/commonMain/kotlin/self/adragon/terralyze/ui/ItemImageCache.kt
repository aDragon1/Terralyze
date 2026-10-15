package self.adragon.terralyze.ui

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.decodeToImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import terralyze.shared.generated.resources.Res
import kotlin.coroutines.cancellation.CancellationException

object ItemImageCache {
    private val cache = HashMap<String, ImageBitmap>()

    fun peek(fileName: String): ImageBitmap? = cache[fileName]

    suspend fun load(fileName: String): ImageBitmap? {
        cache[fileName]?.let { return it }

        return try {
            withContext(Dispatchers.Default) {
                Res.readBytes("files/images/$fileName").decodeToImageBitmap()
            }.also { cache[fileName] = it }
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            null
        }
    }
}