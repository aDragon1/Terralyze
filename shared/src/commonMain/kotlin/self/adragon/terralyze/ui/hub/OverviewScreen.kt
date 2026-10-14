package self.adragon.terralyze.ui.hub

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import self.adragon.terralyze.data.model.FileType
import self.adragon.terralyze.data.model.PlayerColor
import self.adragon.terralyze.data.model.equipment.Equipment
import self.adragon.terralyze.data.model.player.FileInfo
import self.adragon.terralyze.data.model.player.PlayerColors
import self.adragon.terralyze.data.model.player.ShimmerUpgradesUsed
import self.adragon.terralyze.domain.model.Difficulty
import self.adragon.terralyze.domain.model.Player
import self.adragon.terralyze.domain.model.PlayerAppearance
import self.adragon.terralyze.domain.model.PlayerProgress
import self.adragon.terralyze.domain.model.PlayerStats
import self.adragon.terralyze.domain.model.PlayerStorage
import self.adragon.terralyze.ui.HoverTooltip
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Suppress("BooleanLiteralArgument")
val mockPlayer = Player(
    FileInfo(101, FileType.Player, 1u, true),
    "aDragon MOCK", Difficulty.MEDIUMCORE, 12345.toDuration(DurationUnit.SECONDS),
    PlayerAppearance(
        1, 2, listOf(false), 0, 1,
        PlayerColors(
            PlayerColor(0, 255, 0),
            PlayerColor(0, 255, 0),
            PlayerColor(0, 255, 0),
            PlayerColor(0, 255, 0),
            PlayerColor(0, 255, 0),
            PlayerColor(0, 255, 0),
            PlayerColor(0, 255, 0)
        )
    ),
    PlayerStats(500, 500, 500, 500, 505, 50),
    1,
    PlayerProgress(
        hasExtraAccessorySlot = true,
        unlockedBiomeTorches = true,
        downedDd2Event = true,
        ateArtisanBread = true,
        shimmerUpgradesUsed = ShimmerUpgradesUsed(true, true, true, true, true, true),
        numberOfAnglerQuestsFinished = 31,
        golferScoreAccumulated = 0,
        bartenderQuestLog = 1
    ),
    123,
    Equipment(emptyList(), emptyList(), emptyList()), emptyList(),
    PlayerStorage(emptyList(), emptyList(), emptyList(), emptyList()),
    1, emptyList(), emptyList(),
    true, true, listOf(false), listOf(), listOf(1),
    false, null, 1234567890L, emptyList()
)

val RED = Color.Red
val BLUE = Color.Blue
val GREEN = Color.Green
val LGRAY = Color.LightGray

@Preview
@Composable
fun OverviewScreen(player: Player = mockPlayer, modifier: Modifier = Modifier) {
    val filledInventoryCount = remember(player.inventory) {
        player.inventory.count { it.stack > 0 }
    }

    Box(modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            item { PlayerHeader(player) }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatsCard(player.playerStats, Modifier.weight(1f))
                    InventoryCard(filledInventoryCount, player.inventory.size, Modifier.weight(1f))
                }
            }
            item { ProgressCard(player.progress, player.usingBiomeTorches, player.hotbarLocked) }
            item { ShimmerCard(player.progress.shimmerUpgradesUsed) }
        }
    }
}

@Composable
private fun PlayerHeader(player: Player) {
    val deathPVP = player.playerStats.deathsPVP?.toString() ?: "—"
    val deathPVE = player.playerStats.deathsPVE?.toString() ?: "—"

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(player.name, style = MaterialTheme.typography.headlineSmall)
                Text(
                    text = "${player.difficulty} · ${player.playtime?.let(::mapPlaytime) ?: "—"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Column(Modifier, Arrangement.spacedBy(6.dp), Alignment.End) {
                Text(
                    text = "Смертей",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(deathPVE, style = MaterialTheme.typography.titleMedium)
                        Text(
                            "PVE",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(deathPVP, style = MaterialTheme.typography.titleMedium)
                        Text(
                            "PVP",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatBar(label: String, value: Int, max: Int, color: Color) {
    val fraction = if (max > 0) (value.toFloat() / max).coerceIn(0f, 1f) else 0f
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodyLarge)
            Text("$value / $max", style = MaterialTheme.typography.titleSmall)
        }
        LinearProgressIndicator(
            { fraction }, Modifier.fillMaxWidth().height(10.dp),
            color, MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

@Composable
private fun StatsCard(stats: PlayerStats, modifier: Modifier = Modifier) = Card(modifier) {
    Column(Modifier.padding(16.dp), Arrangement.spacedBy(12.dp)) {
        Text("Характеристики", style = MaterialTheme.typography.titleMedium)
        StatBar("Здоровье", stats.life, stats.maxLife, RED)
        StatBar("Здоровье", stats.mana, stats.maxMana, BLUE)
    }
}

@Composable
private fun InventoryCard(filledSlots: Int, totalSlots: Int, modifier: Modifier = Modifier) =
    Card(modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), Arrangement.spacedBy(8.dp)) {
            Text("Инвентарь", style = MaterialTheme.typography.titleMedium)
            Text(
                "$filledSlots из $totalSlots слотов занято",
                style = MaterialTheme.typography.bodyLarge,
            )
            LinearProgressIndicator(
                progress = { if (totalSlots == 0) 0f else filledSlots.toFloat() / totalSlots },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }


@Composable
private fun ProgressItem(text: String, hint: String, value: Boolean?) =
    HoverTooltip(hint) {
        AssistChip(
            {}, { Text(text) },
            leadingIcon = {
                Canvas(modifier = Modifier.size(10.dp)) {
                    drawCircle(value?.let { if (value) GREEN else RED } ?: LGRAY)
                }
            },
            colors = AssistChipDefaults.assistChipColors(Color.Transparent)
        )
    }

@Composable
private fun ProgressCard(
    progress: PlayerProgress, usingBiomeTorches: Boolean?, hotbarLocked: Boolean?,
    modifier: Modifier = Modifier
) = Card(modifier) {
    Text("Прогресс", style = MaterialTheme.typography.titleMedium)
    FlowRow {
        val anglerQuestStr = progress.numberOfAnglerQuestsFinished ?: "-"
        val anglerQuestVal = progress.numberOfAnglerQuestsFinished?.let { true }

        val golderScoreStr = progress.golferScoreAccumulated ?: "-"
        val golderScoreVal = progress.golferScoreAccumulated?.let { true }

        val bartenderQuestStr = progress.bartenderQuestLog ?: "-"
        val bartenderQuestVal = progress.bartenderQuestLog?.let { true }

        val biomeTorchesVal =
            if (progress.unlockedBiomeTorches == null || usingBiomeTorches == null) null
            else progress.unlockedBiomeTorches && usingBiomeTorches

        ProgressItem("Дополнительный слот аксессуаров", "Тут", progress.hasExtraAccessorySlot)
        ProgressItem("Армия древних", "Должны", progress.downedDd2Event)
        ProgressItem("Биомные факела", "Быть", biomeTorchesVal)
        ProgressItem("Хотбар", "Подсказки", hotbarLocked)
        ProgressItem("Съеден Artisan Bread", "Но", progress.ateArtisanBread)
        ProgressItem("Квестов рыбака сдано: $anglerQuestStr", "Я", anglerQuestVal)
        ProgressItem("Очков в гольфе: $golderScoreStr", "ХЗ че сказать", golderScoreVal)
        ProgressItem(
            "Bartender Quest Log: $bartenderQuestStr",
            "А это я без понятия че такое",
            bartenderQuestVal
        )
    }
}

private fun mapPlaytime(duration: Duration): String =
    "${duration.inWholeDays}д ${duration.inWholeHours % 24}ч ${duration.inWholeMinutes % 60}м"


// TODO: Redesign it somehow
@Composable
private fun ShimmerCard(upgrades: ShimmerUpgradesUsed?) = Card(modifier = Modifier.fillMaxWidth()) {
    Column(Modifier.padding(16.dp), Arrangement.spacedBy(8.dp)) {
        Text(text = "Улучшения Шиммера использованы", style = MaterialTheme.typography.titleMedium)
        Text("Aegis Crystal- ${upgrades?.aegisCrystal}")
        Text("Aegis Fruit -${upgrades?.aegisFruit}")
        Text("Arcane Crystal - ${upgrades?.arcaneCrystal}")
        Text("Galaxy Pearl - ${upgrades?.galaxyPearl}")
        Text("Gummy Worm - ${upgrades?.gummyWorm}")
        Text("Ambrosia - ${upgrades?.ambrosia}")
    }
}
