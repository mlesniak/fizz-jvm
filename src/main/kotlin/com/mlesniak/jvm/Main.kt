package com.mlesniak.jvm

/**
 * Main loads the class file and starts the interpreter.
 **/
class Main {
    fun main(args: Array<String>) {
        if (args.isEmpty()) {
            System.err.println("Missing .class file name")
            return
        }

        val classFile = ClassFile(args[0])
        val jvm = Interpreter(classFile)
        jvm.run()
    }
}

fun main(args: Array<String>) {
    Main().main(args)
}



