package terralyze.app

import terralyze.data.model.player.ParsedPlayer
import terralyze.data.repository.PlayerFileLoader
import terralyze.data.source.crypto.JvmPlrDecryptor
import terralyze.data.source.itemcatalog.JsonItemCatalog
import terralyze.domain.mapper.PlayerMapper
import terralyze.export.PlayerJsonExporter
import java.io.File

// https://gist.github.com/iconmaster5326/3c723eba0b0ebfc41f85f6b1cc00df91

// tree /f ./src/main
// Get-ChildItem -Recurse -Filter *.kt | Get-Content | Out-File ./src/main/resources/project_code.txt

/*
   TODO:
        * Map item tags in ItemInfo to some class or enum, instead of Map<String, List<String>>
        * read*() in parser as extension function on BinaryReader?
        * CMP
 */


fun main() {
//    val path = "C:/Users/aDragon/Downloads/Инжир.plr"
    val path = "C:/Users/aDragon/OneDrive/Документы/My Games/Terraria/Players/aDragonJourney.plr"
//    val path = "C:/Users/aDragon/OneDrive/Документы/My Games/Terraria/Players/aDragon.plr"
    val bytes = File(path).readBytes()
    val player = PlayerFileLoader(decryptor = JvmPlrDecryptor()).load(bytes)

    val itemsJson = File("src/main/resources/items/items.json").readText()
    val itemCatalog = JsonItemCatalog(itemsJson)
    PlayerMapper().parsedPlayerToDomain(itemCatalog, player)
    writeFile(player)
}

fun writeFile(player: ParsedPlayer) {
    val json = PlayerJsonExporter().export(player)
    val outFile = File("src/main/resources/outFile.json")
    outFile.writeText(json)
}
