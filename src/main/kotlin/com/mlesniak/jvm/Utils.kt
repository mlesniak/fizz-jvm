package com.mlesniak.jvm

object Utils {
    /**
     * printBytes displays the byte array in a "readable" format.
     **/
    fun printBytes(bytes: ByteArray) {
        var ip = 0x00
        var sb = StringBuilder()
        for (b in bytes) {
            if (ip % 8 == 0) {
                sb.clear()
                sb.append(String.format("%04X\t", ip))
            }

            val s = String.format("%02X ", b)
            sb.append(s)
            ip++

            if (ip % 8 == 0) {
                println(sb.toString())
            }
        }

        if (sb.isNotEmpty()) {
            println(sb.toString())
        }
    }
}