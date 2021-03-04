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
    var numMethods: Int = 0

    // TODO(mlesniak) Shall we convert it to an IntArray before?
    constructor(bytes: ByteArray) {

        readVersion(bytes)
        val cpSize = readConstantPool(bytes)

        // We can ignore all other fields for FizzBuzz, e.g. access flags, interface definitions, ...
        // (at least until we actually need them).
        //
        //      cp_info        constant_pool[constant_pool_count-1]; <--- WE ARE HERE
        //      u2             access_flags;
        //      u2             this_class;
        //      u2             super_class;
        //      u2             interfaces_count;
        //      u2             interfaces[interfaces_count];
        //      u2             fields_count;
        //      field_info     fields[fields_count];
        //      u2             methods_count;                        <--- INTERESTED IN THIS
        val methodCountPos = cpSize + 10 + 2 + 2 + 2 + 2 + + 2

        // Constructor and actual main method
        readMethods(bytes, methodCountPos)
    }

    private fun readMethods(bytes: ByteArray, curPos: Int) {
        numMethods = readU2(bytes, curPos)
    }


    fun debug() {
        println("Version")
        println("  major=$majorVersion")
        println("  major=$minorVersion")

        println("Constant Pool:")
        for (i in 1 until constantPool.size) {
            println("  % 4d\t${constantPool[i]}".format(i))
        }

        println("Methods:")
        println("  numMethods=$numMethods")
    }

    private fun readConstantPool(bytes: ByteArray): Int {
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
        return pos - 10
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

