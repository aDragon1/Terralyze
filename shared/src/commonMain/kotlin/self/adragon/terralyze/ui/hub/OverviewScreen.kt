package self.adragon.terralyze.ui.hub

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.hoverable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import self.adragon.terralyze.domain.model.Difficulty
import self.adragon.terralyze.domain.model.PermanentUpgrade
import self.adragon.terralyze.domain.model.Player
import self.adragon.terralyze.domain.model.PlayerAppearance
import self.adragon.terralyze.domain.model.PlayerProgress
import self.adragon.terralyze.domain.model.PlayerStats
import self.adragon.terralyze.domain.model.PlayerStorage
import self.adragon.terralyze.ui.CaptionText
import self.adragon.terralyze.ui.HoverTooltip
import self.adragon.terralyze.ui.ItemImage
import self.adragon.terralyze.ui.SecondaryText
import self.adragon.terralyze.ui.SectionTitle
import self.adragon.terralyze.ui.ValueText
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
        permanentUpgrades = emptyList(),
        downedDd2Event = true,
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

@Preview
@Composable
fun OverviewScreen(player: Player = mockPlayer, modifier: Modifier = Modifier) {
    val filledInventoryCount = remember(player.inventory) {
        player.inventory.count { it.stack > 0 }
    }

    Box(modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            item { PlayerHeader(player) }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatsCard(player.playerStats, Modifier.weight(1f))
                    OverviewInventoryCard(filledInventoryCount, player.inventory.size, Modifier.weight(1f))
                }
            }
            item { ProgressCard(player.progress) }
            item { PermanentUpgradesCard(player.progress.permanentUpgrades) }
        }
    }
}

@Composable
private fun PlayerHeader(player: Player) {
    val deathPVP = player.playerStats.deathsPVP?.toString() ?: "—"
    val deathPVE = player.playerStats.deathsPVE?.toString() ?: "—"
    val playtime = player.playtime?.let(::mapPlaytime) ?: "—"

    Card(Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth().padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                SectionTitle(player.name)
                SecondaryText("${player.difficulty} · $playtime")
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                ValueText("Смерти")
                Row(Modifier, Arrangement.spacedBy(12.dp), Alignment.CenterVertically) {
                    DeathsText(" PVE", deathPVE)
                    SecondaryText("·")
                    DeathsText(" PVP", deathPVP)
                }
            }
        }
    }
}

@Composable
fun DeathsText(label: String, value: String) =
    Row(Modifier, Arrangement.SpaceEvenly, Alignment.CenterVertically) {
        SectionTitle(value)
        SecondaryText(label)
    }

@Composable
private fun StatBar(label: String, value: Int, max: Int, color: Color) {
    val fraction = if (max > 0) (value.toFloat() / max).coerceIn(0f, 1f) else 0f
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            SecondaryText(label)
            ValueText("$value / $max")
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
        SectionTitle("Характеристики")
        StatBar("Здоровье", stats.life, stats.maxLife, MaterialTheme.colorScheme.errorContainer)
        StatBar("Мана", stats.mana, stats.maxMana, Color.Blue)
    }
}

@Composable
private fun OverviewInventoryCard(filledSlots: Int, totalSlots: Int, modifier: Modifier = Modifier) =
    Card(modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), Arrangement.spacedBy(8.dp)) {
            SectionTitle("Инвентарь")
            SecondaryText("$filledSlots из $totalSlots слотов занято")
            LinearProgressIndicator(
                progress = { if (totalSlots == 0) 0f else filledSlots.toFloat() / totalSlots },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }


@Composable
private fun ProgressItem(name: String, value: String, hint: String) {
    HoverTooltip(hint) { source, isHovered ->
        Surface(
            Modifier.widthIn(min = 190.dp).hoverable(source).border(
                width = 1.dp,
                color = if (isHovered) Color.Yellow else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            ),
            RoundedCornerShape(10.dp),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
        ) {
            Row(
                Modifier.padding(12.dp, 8.dp),
                Arrangement.spacedBy(12.dp),
                Alignment.CenterVertically,
            ) {
                SecondaryText(name, Modifier.weight(1f))
                ValueText(value)
            }
        }
    }
}

@Composable
private fun ProgressCard(progress: PlayerProgress, modifier: Modifier = Modifier) {
    val anglerQuestStr = progress.numberOfAnglerQuestsFinished?.toString() ?: "-"
    val golderScoreStr = progress.golferScoreAccumulated?.toString() ?: "-"
    val downedDd2EventStr = progress.downedDd2Event?.let { if (it) "Yes" else "No" } ?: "-"
    val bartenderQuestLogStr = progress.bartenderQuestLog?.toString() ?: "-"

    Card(modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SectionTitle("Прогресс")
            FlowRow(Modifier, Arrangement.spacedBy(8.dp), Arrangement.spacedBy(8.dp)) {
                ProgressItem(
                    "Angler Quests",
                    anglerQuestStr,
                    "Number of completed Angler quests"
                )
                ProgressItem("Golfer score", golderScoreStr, "Golfer score accumulated")
                ProgressItem(
                    "Old one's army",
                    downedDd2EventStr,
                    "Does Old one's army event completed"
                )
                ProgressItem(
                    "Bartender quest",
                    bartenderQuestLogStr,
                    "IDK what is this, tbh"
                )
            }
        }
    }
}

@Composable
private fun PermanentUpgradeItem(upgrade: PermanentUpgrade, modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    val primary = scheme.primary
    val outline = scheme.outline
    val outlineVariant = scheme.outlineVariant

    val borderColor = upgrade.isUsed?.let { if (it) primary else outline } ?: outlineVariant

    HoverTooltip(upgrade.effect) { source, isHovered ->
        Column(
            modifier.width(96.dp).hoverable(source),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Box(
                modifier = Modifier.size(56.dp)
                    .border(
                        width = 1.dp,
                        color = if (isHovered) Color.Yellow else borderColor,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(6.dp),
                contentAlignment = Alignment.Center,
            ) {
                ItemImage(upgrade.name, upgrade.imageUrl)

                Canvas(modifier = Modifier.align(Alignment.BottomEnd).size(8.dp)) {
                    drawCircle(
                        color = when (upgrade.isUsed) {
                            true -> Color.Green
                            false -> Color.Red
                            null -> primary
                        },
                    )
                }
            }
            CaptionText(upgrade.name)
        }
    }
}

@Composable
private fun PermanentUpgradesCard(upgrades: List<PermanentUpgrade>, modifier: Modifier = Modifier) {
    val known = upgrades.count { it.isUsed != null }
    val used = upgrades.count { it.isUsed == true }

    Card(modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), Arrangement.spacedBy(12.dp)) {
            SectionTitle("Постоянные улучшения: $used / $known")
            FlowRow(
                Modifier, Arrangement.spacedBy(12.dp),
                Arrangement.spacedBy(12.dp)
            ) { upgrades.forEach { PermanentUpgradeItem(it) } }
        }
    }
}

private fun mapPlaytime(duration: Duration): String =
    "${duration.inWholeDays}д ${duration.inWholeHours % 24}ч ${duration.inWholeMinutes % 60}м"