import self.adragon.BinaryReader
import java.io.File
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

const val ENCRYPTION_KEY = "h3y_gUyZ"

// https://gist.github.com/iconmaster5326/3c723eba0b0ebfc41f85f6b1cc00df91
fun main() {
    val rawBytes = getRawBytes("C:/Users/aDragon/OneDrive/Документы/My Games/Terraria/Players/aDragon.plr")
//    val rawBytes = getRawBytes("C:/Users/aDragon/OneDrive/Документы/My Games/Terraria/Players/aDragonJourney.plr")
//    val rawBytes = getRawBytes("C:/Users/aDragon/Downloads/Инжир.plr")

    val playerParser = PlayerParser(rawBytes)
    playerParser.parse()
}

fun readSP(reader: BinaryReader): Pair<Int, Triple<Int, Int, String>> {
    val x = reader.readS4()
    val rest = when (x) {
        -1 -> readDummy(reader)
        else -> readSpBody(reader)
    }
    return x to rest
}

fun readDummy(reader: BinaryReader) = Triple(-1, -1, "")

fun readSpBody(reader: BinaryReader): Triple<Int, Int, String> {
    val y = reader.readS4()
    val i = reader.readS4()
    val n = reader.readString()

    return Triple(y, i, n)
}


fun getRawBytes(path: String): ByteArray {
    val key = ENCRYPTION_KEY.toByteArray(Charsets.UTF_16LE)
    return decryptPLR(path, key)
}

fun decryptPLR(path: String, key: ByteArray): ByteArray {
    val f = File(path)

    val secretKey = SecretKeySpec(key, "AES")
    val iv = IvParameterSpec(key)

    val cipher = Cipher.getInstance("AES/CBC/NoPadding")
    cipher.init(Cipher.DECRYPT_MODE, secretKey, iv)

    return cipher.doFinal(f.readBytes()) ?: byteArrayOf()
}