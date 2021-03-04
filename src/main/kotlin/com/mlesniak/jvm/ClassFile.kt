package com.mlesniak.jvm

class ClassFile {
    var majorVersion: Int = 0
    var minorVersion: Int = 0

    constructor(bytes: ByteArray) {
        // TODO(mlesniak) Shall we convert it to an IntArray before?
        readVersion(bytes)
        readConstantPool(bytes)
    }

    private fun readConstantPool(bytes: ByteArray) {

    }

    private fun readVersion(bytes: ByteArray) {
        minorVersion = (bytes[4].toInt() shl 8) + bytes[5]
        majorVersion = (bytes[6].toInt() shl 8) + bytes[7].toInt()
    }
}

