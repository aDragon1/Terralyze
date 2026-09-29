package self.adragon.terralyze.domain.usecase

import self.adragon.terralyze.domain.model.Player
import self.adragon.terralyze.export.PlayerExporter

class ExportPlayer(
    private val exporter: PlayerExporter
) {
    operator fun invoke(player: Player) = exporter.export(player)
}