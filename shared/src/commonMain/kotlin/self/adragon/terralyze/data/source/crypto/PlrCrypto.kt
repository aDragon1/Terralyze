package self.adragon.terralyze.data.source.crypto

internal object PlrCrypto {
    const val ENCRYPTION_KEY = "h3y_gUyZ"

    fun encryptionKeyBytes(): ByteArray {
        val bytes = ByteArray(ENCRYPTION_KEY.length * 2)
        for (i in ENCRYPTION_KEY.indices) {
            val code = ENCRYPTION_KEY[i].code
            bytes[i * 2] = (code and 0xFF).toByte()
            bytes[i * 2 + 1] = (code shr 8).toByte()
        }
        return bytes
    }
}