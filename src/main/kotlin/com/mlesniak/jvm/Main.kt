package com.mlesniak.jvm

/**
 * Main loads the class file and starts the interpreter.
 **/
class Main {
    fun main(args: Array<String>) {
        val classFile = ClassFile("fizz-buzz/Main.class")
        val jvm = Interpreter(classFile)
        jvm.run()
    }
}

fun main(args: Array<String>) {
    Main().main(args)
}



