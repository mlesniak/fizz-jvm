package com.mlesniak.jvm

import java.nio.file.Files
import java.nio.file.Path

class Main {
    fun main(args: Array<String>) {
        val path = Path.of("fizz-buzz/Main.class")
        val bytes = Files.readAllBytes(path)
        Utils.printBytes(bytes)

        val cf = ClassFile(bytes)
        println(cf.minorVersion)
        println(cf.majorVersion)
        println(cf.numConstPool)
    }
}

fun main(args: Array<String>) {
    Main().main(args)
}


