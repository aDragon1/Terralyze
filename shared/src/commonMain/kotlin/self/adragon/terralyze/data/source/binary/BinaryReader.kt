package self.adragon.terralyze.data.source.binary

class BinaryReader(
    private val data: ByteArray
) {
    private var cursor = 0
    private var bitCursor = 0

    fun readB1(): Int {
        val value = (data[cursor].toInt() shr (7 - bitCursor)) and 1
        bitCursor++
        if (bitCursor == 8) {
            bitCursor = 0
            cursor++
        }
        return value
    }

    fun readBitsAndReset(count: Int): List<Int> {
        val result = mutableListOf<Int>()
        repeat(count) { result.add(readB1()) }

        if (bitCursor != 0) {
            cursor++
            bitCursor = 0
        }

        return result
    }

    fun readBoolean() = readU1() != 0.toUByte()


    fun readS4(): Int = readU4().toInt()
    fun readS8(): Long = readU8().toLong()

    fun readU1() = data[cursor++].toUByte()

    fun readU4(): UInt {
        var result = 0u

        repeat(4) { byteIndex ->
            result = result or (readU1().toUInt() shl (byteIndex * 8))
        }

        return result
    }

    fun readU8(): ULong {
        var result = 0uL

        repeat(8) { byteIndex ->
            result = result or (readU1().toULong() shl (byteIndex * 8))
        }

        return result
    }

    fun readString(): String {
        val length = readVlq()
        val start = cursor
        cursor += length

        return data.copyOfRange(start, cursor).decodeToString()
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun dumpFromHere(shift: Int) {
        val dump = data.slice(cursor..<cursor + shift).toByteArray()
        print("Bytes:")
        dump.forEach { print("$it ") }
        print("\nHex: ")
        dump.map { it.toHexString(HexFormat.UpperCase) }.forEach { print("$it ") }
        println("\n Cursor = $cursor")
    }

    fun readVlq(): Int {
        var result = 0
        var shift = 0

        while (true) {
            val byte = data[cursor++].toInt() and 0xFF
            result = result or ((byte and 0x7F) shl shift)
            if ((byte and 0x80) == 0) {
                return result
            }

            shift += 7
        }
    }

    fun skip(bytesToSkip: Int) {
        cursor += bytesToSkip
    }
}