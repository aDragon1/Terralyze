package self.adragon.terralyze.ui.playerLoadScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.launch
import self.adragon.terralyze.domain.model.Player
import self.adragon.terralyze.domain.usecase.LoadPlayerUseCase


@Composable
fun PlayerLoadScreen(
    loadPlayer: LoadPlayerUseCase,
    onLoadComplete: (Player) -> Unit
) {
    var state by remember { mutableStateOf<PlayerLoadState>(PlayerLoadState.Empty) }
    val scope = rememberCoroutineScope()

    val launcher = rememberFilePickerLauncher { file ->
        if (file == null) return@rememberFilePickerLauncher

        scope.launch {
            state = PlayerLoadState.Loading

            runCatching {
                val bytes = file.readBytes()
                loadPlayer(bytes)
            }.fold(
                onSuccess = { player -> onLoadComplete(player) },
                onFailure = {
                    state = PlayerLoadState.Error(it.message ?: "Не удалось загрузить файл")
                })
        }
    }

    Column {
        Button(onClick = { launcher.launch() })
        {
            Text(if (state is PlayerLoadState.Loading) "Загрузка…" else "Выбрать файл")
        }

        when (val curState = state) {
            PlayerLoadState.Empty -> Text("Файл ещё не выбран")
            PlayerLoadState.Loading -> Text("Чтение и обработка файла…")
            is PlayerLoadState.Error -> Text("Ошибка: ${curState.message}")
        }
    }
}
