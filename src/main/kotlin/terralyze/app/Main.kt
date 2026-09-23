package terralyze.app

import terralyze.data.source.itemcatalog.JsonItemCatalog
import terralyze.data.source.crypto.JvmPlrDecryptor
import terralyze.data.repository.PlayerFileLoader
import terralyze.export.PlayerJsonExporter
import java.io.File

// https://gist.github.com/iconmaster5326/3c723eba0b0ebfc41f85f6b1cc00df91

// tree /f ./src/main
// Get-ChildItem -Recurse -Filter *.kt | Get-Content | Out-File ./src/main/resources/project_code.txt

/*
   TODO:
        0) Handle earlier version?
        1) Map item tags in ItemInfo to some class or enum, instead of Map<String, List<String>>
        2) Name magic numbers in parser
        3) read*() in parser as extension function on BinaryReader?
        4) ItemEnricher (ParsedPlayer + ItemCatalog, id -> ItemInfo)
            Unknown item?
        5) CMP
 */


fun main() {

//    val path = "C:/Users/aDragon/OneDrive/Документы/My Games/Terraria/Players/aDragonJourney.plr"
    val path = "C:/Users/aDragon/OneDrive/Документы/My Games/Terraria/Players/aDragon.plr"
    val file = File(path)
    val bytes = File(path).readBytes()
    val player = PlayerFileLoader(decryptor = JvmPlrDecryptor())
        .load(bytes)

    val itemsJson = File("src/main/resources/items/items.json").readText()
    val itemCatalog = JsonItemCatalog(itemsJson)
    val info = itemCatalog.getItem(6171)
    println(info)
    println(info?.tags)

    val json = PlayerJsonExporter().export(player)
    val outFile = File("src/main/resources/sampleOutput/outFile.json")
    outFile.writeText(json)
}