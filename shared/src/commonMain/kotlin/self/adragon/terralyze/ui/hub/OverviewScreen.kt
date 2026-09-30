package self.adragon.terralyze.ui.hub

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import self.adragon.terralyze.data.model.player.ShimmerUpgradesUsed
import self.adragon.terralyze.domain.model.Player
import self.adragon.terralyze.domain.model.PlayerProgress
import self.adragon.terralyze.domain.model.PlayerStats
import self.adragon.terralyze.ui.hub.inventory.InventoryItemUiState
import self.adragon.terralyze.ui.hub.inventory.toUiState
import kotlin.time.Duration

@Composable
fun OverviewScreen(player: Player, modifier: Modifier = Modifier) {
    val stats = player.playerStats
    val progress = player.progress

    val inventoryItems = remember(player.inventory) {
        player.inventory.map { it.toUiState() }
    }

    val filledInventoryCount = remember(inventoryItems) {
        inventoryItems.count { it !is InventoryItemUiState.Empty }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { PlayerHeader(player) }
        item { StatsCard(stats) }
        item { InventoryCard(filledInventoryCount, inventoryItems.size) }
        item { ProgressCard(player, progress) }
        item { ShimmerCard(progress.shimmerUpgradesUsed) }
    }
}

@Composable
private fun PlayerHeader(player: Player) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = player.name,
                style = MaterialTheme.typography.headlineMedium,
            )

            Text(
                text = "${player.difficulty} · ${player.playtime?.let(::mapPlaytime)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun mapPlaytime(duration: Duration): String =
    "${duration.inWholeDays}д " +
            "${duration.inWholeHours % 24}ч " +
            "${duration.inWholeMinutes % 60}м"

@Composable
private fun StatsCard(stats: PlayerStats) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Характеристики",
                style = MaterialTheme.typography.titleMedium,
            )
            StatRow("Здоровье", "${stats.life} / ${stats.maxLife}")
            StatRow("Мана", "${stats.mana} / ${stats.maxMana}")
            HorizontalDivider()
            StatRow("Смерти в PvE", formatValue(stats.deathsPVE))
            StatRow("Смерти в PvP", formatValue(stats.deathsPVP))
        }
    }
}

@Composable
private fun InventoryCard(
    filledSlots: Int,
    totalSlots: Int,
) = Card(modifier = Modifier.fillMaxWidth()) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "Инвентарь",
            style = MaterialTheme.typography.titleMedium,
        )

        Text(
            text = "$filledSlots из $totalSlots слотов занято",
            style = MaterialTheme.typography.bodyLarge,
        )

        LinearProgressIndicator(
            progress = {
                if (totalSlots == 0) 0f
                else filledSlots.toFloat() / totalSlots
            },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}


@Composable
private fun ProgressCard(
    player: Player,
    progress: PlayerProgress,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text("Прогресс", style = MaterialTheme.typography.titleMedium)
            StatusRow("Дополнительный слот аксессуаров", progress.hasExtraAccessorySlot)
            StatusRow("Армия древних пройдена", progress.downedDd2Event)
            StatusRow("Биомные факела разблокированы", progress.unlockedBiomeTorches)
            StatusRow("Биомные факела используются", player.usingBiomeTorches)
            StatusRow("Хотбар заблокирован", player.hotbarLocked)
            StatusRow("Съеден Artisan Bread", progress.ateArtisanBread)
            StatusRow("Квестов рыбака сдано", progress.numberOfAnglerQuestsFinished)
            StatusRow("Очков в гольфе", progress.golferScoreAccumulated)
            StatusRow("Bartender Quest Log", progress.bartenderQuestLog)
        }
    }
}

@Composable
private fun ShimmerCard(upgrades: ShimmerUpgradesUsed?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(text = "Улучшения Шиммера", style = MaterialTheme.typography.titleMedium)
            StatusRow("Aegis Crystal", upgrades?.aegisCrystal)
            StatusRow("Aegis Fruit", upgrades?.aegisFruit)
            StatusRow("Arcane Crystal", upgrades?.arcaneCrystal)
            StatusRow("Galaxy Pearl", upgrades?.galaxyPearl)
            StatusRow("Gummy Worm", upgrades?.gummyWorm)
            StatusRow("Ambrosia", upgrades?.ambrosia)
        }
    }
}

@Composable
private fun StatRow(name: String, value: String) = Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
) {
    Text(text = name, color = MaterialTheme.colorScheme.onSurfaceVariant)
    Text(text = value, style = MaterialTheme.typography.bodyLarge)
}

@Composable
private fun <T> StatusRow(name: String, value: T?) = Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
) {
    Text(
        text = name,
        modifier = Modifier.weight(1f),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )

    StatusValue(value)
}


@Composable
private fun <T> StatusValue(value: T?) {
    when (value) {
        null -> {
            Text(
                text = "—",
                color = MaterialTheme.colorScheme.outline,
            )
        }

        is Boolean -> {
            Text(
                text = if (value) "Да" else "Нет",
                color = if (value) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        else -> Text(value.toString())
    }
}

private fun <T> formatValue(value: T?) = value?.toString() ?: "—"
