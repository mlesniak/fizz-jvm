package com.mlesniak.jvm

import java.lang.StringBuilder
import java.nio.file.Files
import java.nio.file.Path

class Main {
    fun main(args: Array<String>) {
        val path = Path.of("fizz-buzz/Main.class")
        val bytes = Files.readAllBytes(path)

        printBytes(bytes)
    }

    /**
     * printBytes displays the byte array in a "readable" format.
     **/
    private fun printBytes(bytes: ByteArray) {
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

fun main(args: Array<String>) {
    Main().main(args)
}


