package com.mlesniak.jvm

class Main {
    fun main(args: Array<String>) {
        val classFile = ClassFile("fizz-buzz/Main.class")
        classFile.debug()

        val jvm = Interpreter(classFile)
        jvm.run()
    }
}

fun main(args: Array<String>) {
    Main().main(args)
}



