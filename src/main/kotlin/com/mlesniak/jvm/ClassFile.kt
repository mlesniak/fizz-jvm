package com.mlesniak.jvm

import java.nio.charset.Charset

class ClassFile {
    var majorVersion: Int = 0
    var minorVersion: Int = 0
    var numConstPool: Int = 0

    constructor(bytes: ByteArray) {
        // TODO(mlesniak) Shall we convert it to an IntArray before?
        readVersion(bytes)
        readConstantPool(bytes)
    }

    private fun readConstantPool(bytes: ByteArray) {
        numConstPool = readU2(bytes, 8)

        val constPoolIndex = 10
        var pos = constPoolIndex
        for (index in 1 until numConstPool) {
            val tag = bytes[pos]
            pos += 1
            when (tag.toInt()) {
                // 1
                1 -> {
                    // UTF-8 (Unicode) string: a character string prefixed by a 16-bit number (type u2) indicating the number of bytes in the encoded string which immediately
                    // follows (which may be different than the number of characters). Note that the encoding used is not actually UTF-8, but involves a slight modification of
                    // the Unicode standard encoding form.
                    val length = readU2(bytes, pos)
                    val strValue = readString(bytes, pos+2, length)
                    println(strValue)
                    pos += 2 + length
                }
                // Class reference.
                7 -> {
                    // Class reference: an index within the constant pool to a UTF-8 string containing the fully qualified class name (in internal format) (big-endian)
                    pos += 2
                }
                // String reference
                8 -> {
                    // String reference: an index within the constant pool to a UTF-8 string (big-endian too)
                    pos += 2
                }
                // Field reference
                9 -> {
                    // Field reference: two indexes within the constant pool, the first pointing to a Class reference, the second to a Name and Type descriptor. (big-endian)
                    pos += 4
                }
                // Method reference.
                10 -> {
                    // Method reference: two indexes within the constant pool, the first pointing to a Class reference, the second to a Name and Type descriptor. (big-endian)
                    pos += 4
                }
                // Name and type descriptor
                12 -> {
                    // Name and type descriptor: two indexes to UTF-8 strings within the constant pool, the first representing a name (identifier) and the second a specially
                    // encoded type descriptor.
                    pos += 4
                }

                else ->
                    throw IllegalStateException("Unknown tag ${tag.toInt()}")
            }
        }
    }

    private fun readU2(bytes: ByteArray, start: Int): Int {
        return (bytes[start].toInt() shl 8) + bytes[start + 1]
    }

    private fun readString(bytes: ByteArray, start: Int, length: Int): String {
        val strBytes =  bytes.copyOfRange(start, start+length)
        return String(strBytes, Charset.forName("UTF-8"))
    }

    private fun readVersion(bytes: ByteArray) {
        // TODO(mlesniak) Refactoring
        minorVersion = (bytes[4].toInt() shl 8) + bytes[5]
        majorVersion = (bytes[6].toInt() shl 8) + bytes[7].toInt()
    }
}

