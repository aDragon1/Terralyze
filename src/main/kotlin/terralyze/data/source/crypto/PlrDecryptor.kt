package terralyze.data.source.crypto

interface PlrDecryptor {
    fun decrypt(bytes: ByteArray): ByteArray
}