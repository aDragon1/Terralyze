package self.adragon.terralyze.ui.hub

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
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
import self.adragon.terralyze.ui.common.EditableContainer
import self.adragon.terralyze.ui.common.HoverTooltip
import self.adragon.terralyze.ui.common.InlineCurrentMaxEditor
import self.adragon.terralyze.ui.common.InlineDropdown
import self.adragon.terralyze.ui.common.InlineIntEditor
import self.adragon.terralyze.ui.common.InlineTextEditor
import self.adragon.terralyze.ui.common.ItemImage
import self.adragon.terralyze.ui.common.SecondaryText
import self.adragon.terralyze.ui.common.SectionTitle
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

        val deathsPVE = player.playerStats.deathsPVE
        val deathsPVP = player.playerStats.deathsPVP
        val permanentUpgrade = player.progress.permanentUpgrades

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(16.dp),
        ) {
            item { PlayerHeader(player, playerEditor::onIntent) }
            item {
                if (compact) Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    DeathsCard(deathsPVE, deathsPVP, playerEditor::onIntent)
                    ProgressCard(player.progress, playerEditor::onIntent)
                    PermanentUpgradesCard(permanentUpgrade, onIntent = playerEditor::onIntent)
                }
                else Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(12.dp)) {
                    Column(Modifier.weight(1f), Arrangement.spacedBy(12.dp)) {
                        DeathsCard(deathsPVE, deathsPVP, playerEditor::onIntent)
                        ProgressCard(player.progress, playerEditor::onIntent)
                    }
                    PermanentUpgradesCard(
                        permanentUpgrade, Modifier.weight(1.5f), onIntent = playerEditor::onIntent
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
                InlineCurrentMaxEditor(
                    stats.life, stats.maxLife, "Health", MaterialTheme.colorScheme.errorContainer,
                    { a, b -> onIntent(PlayerEditorIntent.Stats.UpdateHealth(a, b)) },
                    Modifier.weight(1f)
                )
                InlineCurrentMaxEditor(
                    stats.mana, stats.maxMana, "Mana", Color.Blue,
                    { a, b -> onIntent(PlayerEditorIntent.Stats.UpdateMana(a, b)) },
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

        NullableIntEditor(
            deathsPVE, { DeathsText("PvE", it) },
            { onIntent(PlayerEditorIntent.Stats.UpdateDeathsPVE(it)) })

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        NullableIntEditor(
            deathsPVP, { DeathsText("PvP", it) },
            { onIntent(PlayerEditorIntent.Stats.UpdateDeathsPVP(it)) })
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
private fun ProgressItem(name: String, value: String) =
    Surface(
        Modifier.widthIn(min = 190.dp),
        RoundedCornerShape(10.dp),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
    ) {
        Row(
            Modifier.padding(12.dp, 8.dp),
            Arrangement.spacedBy(12.dp), Alignment.CenterVertically,
        ) {
            SecondaryText(name, Modifier.weight(1f))
            ValueText(value)
        }
    }

@Composable
private fun ProgressCard(progress: PlayerProgress, onIntent: (PlayerEditorIntent) -> Unit) =
    AppCard(Modifier.fillMaxWidth()) {
        val anglerQuests = progress.numberOfAnglerQuestsFinished
        val golferScore = progress.golferScoreAccumulated
        val downedDD2event = progress.downedDd2Event
        val bartenderQuestLog = progress.bartenderQuestLog

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SectionTitle("Progress")
            FlowRow(Modifier, Arrangement.spacedBy(8.dp), Arrangement.spacedBy(8.dp)) {

                NullableIntEditor(
                    anglerQuests, { ProgressItem("Angler Quests", it) },
                    { onIntent(PlayerEditorIntent.Progress.UpdateAnglerQuests(it)) })
                NullableIntEditor(
                    golferScore, { ProgressItem("Golfer Score", it) },
                    { onIntent(PlayerEditorIntent.Progress.UpdateGolferScore(it)) })

                if (downedDD2event != null) {
                    val downedDd2EventStr = if (downedDD2event) "Completed" else "Not completed"
                    EditableContainer({ onIntent(PlayerEditorIntent.Progress.ToggleDownedDd2Event) }) {
                        ProgressItem(
                            "Old one's army", downedDd2EventStr
                        )
                    }
                } else ProgressItem("Old one's army", "-")
                NullableIntEditor(
                    bartenderQuestLog, { ProgressItem("Bartender quest", it) },
                    { onIntent(PlayerEditorIntent.Progress.UpdateBartenderQuestLog(it)) })
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

    val isEditable = upgrade.isUsed != null
    val stateBorderColor = upgrade.isUsed?.let { if (it) primary else outline } ?: outlineVariant

    HoverTooltip(upgrade.effect) {
        EditableContainer(enabled = isEditable, onClick = {
            onIntent(PlayerEditorIntent.Progress.ToggleUpgrade(upgrade.name))
        }) {
            Column(
                Modifier.width(96.dp), Arrangement.spacedBy(4.dp), Alignment.CenterHorizontally
            ) {
                Box(
                    Modifier.size(56.dp).border(1.dp, stateBorderColor, RoundedCornerShape(8.dp))
                        .padding(6.dp), Alignment.Center
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

@Composable
private fun NullableIntEditor(
    value: Int?, content: @Composable (String) -> Unit, onSave: (Int) -> Unit
) = if (value != null) InlineIntEditor(value, { content("$it") }, onSave)
else content("-")