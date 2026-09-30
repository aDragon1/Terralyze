package self.adragon.terralyze.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.launch
import self.adragon.terralyze.domain.model.Player
import self.adragon.terralyze.domain.usecase.LoadPlayerUseCase


@Composable
fun PlayerLoadScreen(
    loadPlayer: LoadPlayerUseCase,
    onLoadComplete: (Player) -> Unit,
) {
    var state by remember { mutableStateOf<PlayerLoadState>(PlayerLoadState.Empty) }
    val scope = rememberCoroutineScope()

    val launcher = rememberFilePickerLauncher(
        type = FileKitType.File(extension = "plr")
    ) { file ->
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

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = { launcher.launch() })
        {
            Text(if (state is PlayerLoadState.Loading) "Загрузка…" else "Выбрать файл персонажа")
        }

        when (val curState = state) {
            PlayerLoadState.Empty -> Text("Файл ещё не выбран")
            PlayerLoadState.Loading -> Text("Чтение и обработка файла…")
            is PlayerLoadState.Error -> Text("Ошибка: ${curState.message}")
        }
        Text("Формат полностью поддерживается, начиная с версии Terraria 1.4.0.1 (Journey's End).\n Все ,что было раньше - поддерживается частично и может выдавать ошибики")
    }
}

sealed interface PlayerLoadState {
    data object Empty : PlayerLoadState
    data object Loading : PlayerLoadState
    data class Error(val message: String) : PlayerLoadState
}