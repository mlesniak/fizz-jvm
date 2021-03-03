package com.mlesniak.jvm

object Utils {
    /**
     * printBytes displays the byte array in a "readable" format.
     **/
    fun printBytes(bytes: ByteArray) {
        var ip = 0x00
        var hex = StringBuilder()
        var values = StringBuilder()

        for (b in bytes) {
            if (ip % 8 == 0) {
                hex.clear()
                values.clear()
                hex.append(String.format("%04X\t", ip))
            }

            val s = String.format("%02X ", b)
            hex.append(s)

            var bval = '.'
            if (b > 31) {
                bval = b.toChar()
            }
            values.append(bval)

            ip++

            if (ip % 8 == 0) {
                println(hex.toString() + "\t" + values.toString())
            }
        }

        if (hex.isNotEmpty()) {
            println(hex.toString())
        }
    }
}