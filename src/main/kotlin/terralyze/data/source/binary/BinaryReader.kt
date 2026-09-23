package terralyze.data.source.binary

import java.nio.ByteBuffer
import java.nio.ByteOrder

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

    fun readS4() = read(4).getInt()
    fun readS8() = read(8).getLong()

    fun readU1() = data[cursor++].toUByte()
    fun readU4() = read(4).getInt().toUInt()
    fun readU8() = read(8).getLong().toULong()

    fun readString(): String {
        val length = readVlq()
        val buffer = mutableListOf<Byte>()

        repeat(length) { buffer.add(data[cursor++]) }

        return String(buffer.toByteArray())
    }

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

    fun leftToRead() = data.size - cursor - 1

    private fun read(shift: Int): ByteBuffer {
        val value = ByteBuffer.wrap(data, cursor, shift).order(ByteOrder.LITTLE_ENDIAN)
        cursor += shift
        return value
    }
}