package com.mlesniak.jvm

class Interpreter(val classFile: ClassFile) {

    fun run() {
        val bytecode = findMainBytecode()
        for (bs in bytecode) {
            println("%02X".format(bs))
        }
    }

    private fun findMainBytecode(): ByteArray {
        // Note that we do not check for the correct signature, access modifiers, etc. but
        // only look for the correct method name.
        for (field in classFile.fields) {
            val name = classFile.name(field.nameIndex) ?: continue
            if (name != "main") {
                continue
            }

            // The only attribute is the actual byte code for this method.
            if (field.attributes.isEmpty()) {
                throw IllegalStateException("Main method does not have attributes?")
            }
            val attrName = classFile.name(field.attributes[0].nameIndex)
            if (attrName != null && attrName != "Code") {
                throw IllegalStateException("Main method does not have a code attribute?")
            }

            return field.attributes[0].data
        }

        throw IllegalStateException("No main method found in class file?")
    }

}