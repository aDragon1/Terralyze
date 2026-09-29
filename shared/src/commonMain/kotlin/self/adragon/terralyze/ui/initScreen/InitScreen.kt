package self.adragon.terralyze.ui.initScreen

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import self.adragon.terralyze.data.source.crypto.PlrDecryptor
import self.adragon.terralyze.data.source.itemcatalog.JsonItemCatalog
import self.adragon.terralyze.data.source.parser.PlayerParser
import self.adragon.terralyze.domain.mapper.PlayerMapper
import self.adragon.terralyze.domain.usecase.LoadPlayerUseCase
import self.adragon.terralyze.ui.navigation.NavGraph
import terralyze.shared.generated.resources.Res

/*
    TODO:
      Юай:
        * Переделать Юай под логику из /ui/state/LoadState
        * Сделать юай
      Экспортер:
        * Доделать экспортер (PlayerJsonExporter: Допилить экспорт обычного плеера, удалить ExportParsedPlayer?)
        * Добавить некую логику вызова этого экспортера. Мб добавить экспорт в другие форматы
 */

@Composable
fun App() {
    var initState by remember { mutableStateOf<InitState>(InitState.Loading) }

    LaunchedEffect(Unit) {
        initState = runCatching { initLoadPlayer() }.fold(
            onSuccess = { InitState.Ready(it) },
            onFailure = { InitState.Error(it.message ?: "Ошибка инициализации") }
        )
    }
    MaterialTheme {
        when (val state = initState) {
            is InitState.Error -> Text("Ошибка: ${state.message}")
            InitState.Loading -> Text("Инициализация…")
            is InitState.Ready -> NavGraph(state.loadPlayer)
        }
    }
}

private suspend fun initLoadPlayer(): LoadPlayerUseCase {
    val decryptor = PlrDecryptor()
    val parser = PlayerParser()

    val rawJson = Res
        .readBytes("files/items.json")
        .decodeToString()

    val catalog = JsonItemCatalog(rawJson)
    val mapper = PlayerMapper()

    return LoadPlayerUseCase(decryptor, parser, mapper, catalog)
}
