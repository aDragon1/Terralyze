package terralyze.app

import terralyze.export.PlayerJsonExporter
import terralyze.application.PlayerFileLoader
import java.io.File

// tree /f ./src/main
// Get-ChildItem -Recurse -Filter *.kt | Get-Content | Out-File ./src/main/resources/project_code.txt

// https://gist.github.com/iconmaster5326/3c723eba0b0ebfc41f85f6b1cc00df91
fun main() {

//    val path = "C:/Users/aDragon/OneDrive/Документы/My Games/Terraria/Players/aDragonJourney.plr"
    val path = "C:/Users/aDragon/OneDrive/Документы/My Games/Terraria/Players/aDragon.plr"
    val file = File(path)
    val player = PlayerFileLoader().load(file)

    // Temporary: dumping to JSON is just today's way of looking at the result.
    // Once the UI is in place this will likely become a different consumer
    // of `save` instead of (or alongside) this file write.
    val json = PlayerJsonExporter().export(player)
    val outFile = File("src/main/resources/sampleOutput/outFile.json")
    outFile.writeText(json)
}