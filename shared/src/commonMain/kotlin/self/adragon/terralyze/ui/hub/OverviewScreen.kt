package self.adragon.terralyze.ui.hub

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import self.adragon.terralyze.ui.common.AppCard
import self.adragon.terralyze.ui.common.CaptionText
import self.adragon.terralyze.ui.common.HoverTooltip
import self.adragon.terralyze.ui.common.InlineDropdown
import self.adragon.terralyze.ui.common.InlineIntEditor
import self.adragon.terralyze.ui.common.InlineTextEditor
import self.adragon.terralyze.ui.common.ItemImage
import self.adragon.terralyze.ui.common.SecondaryText
import self.adragon.terralyze.ui.common.SectionTitle
import self.adragon.terralyze.ui.common.StatBar
import self.adragon.terralyze.ui.common.ValueText
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

@Composable
fun OverviewScreen(playerEditor: PlayerEditorStateHolder, modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier.fillMaxSize()) {
        val compact = maxWidth < 600.dp
        val player by playerEditor.editedPlayer.collectAsState()

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(16.dp),
        ) {
            item { PlayerHeader(player, playerEditor::onIntent) }
            item {
                if (compact) Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    DeathsCard(
                        player.playerStats.deathsPVE, player.playerStats.deathsPVP,
                        playerEditor::onIntent
                    )
                    ProgressCard(player.progress, playerEditor::onIntent)
                    PermanentUpgradesCard(
                        player.progress.permanentUpgrades, onIntent = playerEditor::onIntent
                    )
                }
                else
                    Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(12.dp)) {
                        Column(Modifier.weight(1f), Arrangement.spacedBy(12.dp)) {
                            DeathsCard(
                                player.playerStats.deathsPVE, player.playerStats.deathsPVP,
                                playerEditor::onIntent
                            )
                            ProgressCard(player.progress, playerEditor::onIntent)
                        }
                        PermanentUpgradesCard(
                            player.progress.permanentUpgrades, Modifier.weight(1.5f),
                            onIntent = playerEditor::onIntent
                        )
                    }
            }
        }
    }
}

@Composable
private fun PlayerHeader(player: Player, onIntent: (PlayerEditorIntent) -> Unit) {
    val playtime = player.playtime?.let(::mapPlaytime) ?: "—"
    val stats = player.playerStats

    AppCard(Modifier.fillMaxWidth()) {
        Column(
            Modifier.fillMaxWidth().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    InlineTextEditor(
                        player.name, { ValueText(it) },
                        { onIntent(PlayerEditorIntent.Stats.UpdateName(it)) }
                    )
                    Row {
                        InlineDropdown(
                            player.difficulty, Difficulty.entries.toList(),
                            { SecondaryText(it.name) }, { it.name },
                            { onIntent(PlayerEditorIntent.Stats.ChangeDifficulty(it)) })
                        SecondaryText(" · ")
                        SecondaryText(playtime)
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                StatBar(
                    "Health", stats.life, stats.maxLife, MaterialTheme.colorScheme.errorContainer,
                    Modifier.weight(1f)
                )
                StatBar(
                    "Mana", stats.mana, stats.maxMana, Color.Blue,
                    Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun DeathsCard(
    deathsPVE: Int?, deathsPVP: Int?, onIntent: (PlayerEditorIntent) -> Unit
) = AppCard(Modifier.fillMaxWidth()) {

    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SectionTitle("Deaths")

//        private fun IntEditorIfNotNull1(
//            value: Int?,
//            content: @Composable (Int) -> Unit,
//            fallback: @Composable () -> Unit,
//            onSave: (Int) -> Unit
//        ) = if (value != null) InlineIntEditor(value, content, { onSave(it) })
//        else fallback()


        if (deathsPVE != null) InlineIntEditor(
            deathsPVE, { DeathsText("PvE", "$it") },
            { onIntent(PlayerEditorIntent.Stats.UpdateDeathsPVE(it)) }
        ) else DeathsText("PvE", "-")
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        if (deathsPVP != null) InlineIntEditor(
            deathsPVP, { DeathsText("PvP", "$it") },
            { onIntent(PlayerEditorIntent.Stats.UpdateDeathsPVP(it)) }
        ) else DeathsText("PvP", "-")
    }
}

@Composable
private fun DeathsText(label: String, value: String) = Row(
    modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
) {
    SecondaryText(label)
    SecondaryText(value)
}

@Composable
private fun ProgressItem(name: String, value: String, hint: String) {
    HoverTooltip(hint) { source, isPressed, isHovered ->
        val shouldHighlight = isPressed || isHovered
        Surface(
            Modifier.widthIn(min = 190.dp)
                .hoverable(source)

                .border(
                    width = 1.dp,
                    color = if (shouldHighlight) Color.Yellow else Color.Transparent,
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
private fun ProgressCard(progress: PlayerProgress, onIntent: (PlayerEditorIntent) -> Unit) {
    AppCard(Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SectionTitle("Progress")
            FlowRow(Modifier, Arrangement.spacedBy(8.dp), Arrangement.spacedBy(8.dp)) {

                if (progress.numberOfAnglerQuestsFinished != null) InlineIntEditor(
                    progress.numberOfAnglerQuestsFinished,
                    { ProgressItem("Angler Quests", "$it", "Number of completed Angler quests") },
                    { onIntent(PlayerEditorIntent.Progress.UpdateAnglerQuests(it)) }
                ) else ProgressItem("Angler Quests", "-", "Number of completed Angler quests")

                if (progress.golferScoreAccumulated != null) InlineIntEditor(
                    progress.golferScoreAccumulated,
                    { ProgressItem("Golfer score", "$it", "Golfer score accumulated") },
                    { onIntent(PlayerEditorIntent.Progress.UpdateGolferScore(it)) }
                ) else ProgressItem("Golfer score", "-", "Golfer score accumulated")

                val downedDd2EventStr =
                    progress.downedDd2Event?.let { if (it) "Yes" else "No" } ?: "-"
                val bartenderQuestLogStr = progress.bartenderQuestLog?.toString() ?: "-"

                if (progress.downedDd2Event != null) InlineDropdown(
                    progress.downedDd2Event, listOf(true, false),
                    {
                        ProgressItem(
                            "Old one's army", downedDd2EventStr,
                            "Does Old one's army event completed"
                        )
                    }, { "$it" }, { onIntent(PlayerEditorIntent.Progress.UpdateDownedDd2Event(it)) }
                ) else ProgressItem("Old one's army", "-", "Does Old one's army event completed")

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
private fun PermanentUpgradeItem(
    upgrade: PermanentUpgrade, onIntent: (PlayerEditorIntent) -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    val primary = scheme.primary
    val outline = scheme.outline
    val outlineVariant = scheme.outlineVariant

    val borderColor = upgrade.isUsed?.let { if (it) primary else outline } ?: outlineVariant

    HoverTooltip(upgrade.effect) { source, isPressed, isHovered ->
        val shouldHighlight = isPressed || isHovered
        Column(
            Modifier.width(96.dp).hoverable(source)
                .clickable(source, ripple()) {
                    if (upgrade.isUsed != null) onIntent(
                        PlayerEditorIntent.Progress.ToggleUpgrade(upgrade.name, upgrade.isUsed)
                    )
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Box(
                modifier = Modifier.size(56.dp)
                    .border(
                        width = 1.dp,
                        color = if (shouldHighlight) Color.Yellow else borderColor,
                        shape = RoundedCornerShape(8.dp)
                    ).padding(6.dp),
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
private fun PermanentUpgradesCard(
    upgrades: List<PermanentUpgrade>, modifier: Modifier = Modifier,
    onIntent: (PlayerEditorIntent) -> Unit
) = AppCard(modifier.fillMaxWidth()) {
    val known = upgrades.count { it.isUsed != null }
    val used = upgrades.count { it.isUsed == true }
    Column(Modifier.padding(16.dp), Arrangement.spacedBy(12.dp)) {
        SectionTitle("Permanent upgrades: $used / $known")
        FlowRow(
            Modifier, Arrangement.spacedBy(6.dp),
            Arrangement.spacedBy(6.dp)
        ) { upgrades.forEach { PermanentUpgradeItem(it, onIntent) } }
    }
}

private fun mapPlaytime(duration: Duration): String =
    "${duration.inWholeDays}д ${duration.inWholeHours % 24}ч ${duration.inWholeMinutes % 60}м"