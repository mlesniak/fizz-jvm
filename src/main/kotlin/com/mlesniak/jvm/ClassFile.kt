package com.mlesniak.jvm

import java.nio.charset.Charset

open class ConstantPoolEntry {
    data class String(val value: kotlin.String) : ConstantPoolEntry()
    data class ClassReference(val index: Int) : ConstantPoolEntry()
    data class StringReference(val index: Int) : ConstantPoolEntry()
    data class FieldReference(val classIndex: Int, val descriptorIndex: Int) : ConstantPoolEntry()
    data class MethodReference(val classIndex: Int, val descriptorIndex: Int) : ConstantPoolEntry()
    data class Descriptor(val nameIndex: Int, val typeIndex: Int) : ConstantPoolEntry()
}

class ClassFile {
    var majorVersion: Int = 0
    var minorVersion: Int = 0
    var numConstPool: Int = 0
    lateinit var constantPool: List<ConstantPoolEntry>

    constructor(bytes: ByteArray) {
        // TODO(mlesniak) Shall we convert it to an IntArray before?
        readVersion(bytes)
        readConstantPool(bytes)
    }

    fun debug() {
        println("major=$majorVersion minor=$minorVersion")
        for (i in 1 until constantPool.size) {
            println("%04d ${constantPool[i]}".format(i))
        }
    }

    private fun readConstantPool(bytes: ByteArray) {
        numConstPool = readU2(bytes, 8)
        val cp = mutableListOf<ConstantPoolEntry>()
        // Constant Pool start at 1, prevent subtracting one on each reference
        // by adding this dummy element.
        cp.add(ConstantPoolEntry.String("<not used>"))

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
                    val value = readString(bytes, pos + 2, length)
                    cp.add(ConstantPoolEntry.String(value))
                    pos += 2 + length
                }
                // Class reference.
                7 -> {
                    // Class reference: an index within the constant pool to a UTF-8 string containing the fully qualified class name (in internal format) (big-endian)
                    val index = readU2(bytes, pos)
                    cp.add(ConstantPoolEntry.ClassReference(index))
                    pos += 2
                }
                // String reference
                8 -> {
                    // String reference: an index within the constant pool to a UTF-8 string (big-endian too)
                    val index = readU2(bytes, pos)
                    cp.add(ConstantPoolEntry.StringReference(index))
                    pos += 2
                }
                // Field reference
                9 -> {
                    // Field reference: two indexes within the constant pool, the first pointing to a Class reference, the second to a Name and Type descriptor. (big-endian)
                    val classIndex = readU2(bytes, pos)
                    val descriptorIndex = readU2(bytes, pos + 2)
                    cp.add(ConstantPoolEntry.FieldReference(classIndex, descriptorIndex))
                    pos += 4
                }
                // Method reference.
                10 -> {
                    // Method reference: two indexes within the constant pool, the first pointing to a Class reference, the second to a Name and Type descriptor. (big-endian)
                    val classIndex = readU2(bytes, pos)
                    val descriptorIndex = readU2(bytes, pos + 2)
                    cp.add(ConstantPoolEntry.MethodReference(classIndex, descriptorIndex))
                    pos += 4
                }
                // Name and type descriptor
                12 -> {
                    // Name and type descriptor: two indexes to UTF-8 strings within the constant pool, the first representing a name (identifier) and the second a specially
                    // encoded type descriptor.
                    val nameIndex = readU2(bytes, pos)
                    val typeIndex = readU2(bytes, pos + 2)
                    cp.add(ConstantPoolEntry.Descriptor(nameIndex, typeIndex))
                    pos += 4
                }

                else ->
                    throw IllegalStateException("Unknown tag ${tag.toInt()}")
            }
        }

        // TODO(mlesniak) Should this be an actual array for performance instead of a list?
        constantPool = cp
    }

    private fun readU2(bytes: ByteArray, start: Int): Int {
        return (bytes[start].toInt() shl 8) + bytes[start + 1]
    }

    private fun readString(bytes: ByteArray, start: Int, length: Int): String {
        val strBytes = bytes.copyOfRange(start, start + length)
        return String(strBytes, Charset.forName("UTF-8"))
    }

    private fun readVersion(bytes: ByteArray) {
        minorVersion = readU2(bytes, 4)
        majorVersion = readU2(bytes, 6)
    }
}

