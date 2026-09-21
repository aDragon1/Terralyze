import core.crypto.PlrDecryptor
import core.parser.PlayerParser

const val ENCRYPTION_KEY = "h3y_gUyZ"

// https://gist.github.com/iconmaster5326/3c723eba0b0ebfc41f85f6b1cc00df91
fun main() {

//    val path = "C:/Users/aDragon/OneDrive/Документы/My Games/Terraria/Players/aDragonJourney.plr"
    val path = "C:/Users/aDragon/OneDrive/Документы/My Games/Terraria/Players/aDragon.plr"
    val rawBytes = PlrDecryptor.decrypt(path)

    val playerParser = PlayerParser(rawBytes)
    val player = playerParser.parse()
}