package com.mlesniak.jvm

import java.util.*

/**
 * OperandValue contains values which are allowed on the operand stack.
 *
 * In a real implementation, you'll find references to static objects,
 * strings, etc. here as well.
 **/
open class OperandValue {
    data class IntValue(val value: Int) : OperandValue()
    data class StringValue(val value: String) : OperandValue()
}


/**
 * Interpreter is an implementation of a JVM bytecode interpreter which barely suffices
 * to execute the JVM version of the famous FizzBuzz example.
 *
 * It won't work if you use any other methods than System.out.println, other complex math
 * operations, ..., or, to summarize: if you do something non-fizz-buzzy.
 **/
class Interpreter(private val classFile: ClassFile) {
    fun run() {
        val code = findEntryByteCode()

        // We only have one frame (and thus support only one method in our execution):
        //
        // "Each frame has its own array of local variables (§2.6.1), its own operand stack (§2.6.2),
        // and a reference to the run-time constant pool (§2.5.5) of the class of the current method."
        // (https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-2.html#jvms-2.6)
        var localVariables = Array(2) { 0 }
        var operandStack = Stack<OperandValue>()

        // We skip the first 8 bytes: Parsing the .class file seems correct, but
        // according to 'javap -p' the actual instructions start at the 8th byte.
        // We might fix this later, but for now ("executing FizzBuzz"), it seems
        // that manually starting this at 8 bytes in suffices.
        var pc = 8

        while (true) {
            val opcode = code[pc].toInt() and 0xFF
            when (opcode) {
                // iconst_1
                0x04 -> {
                    operandStack.push(OperandValue.IntValue(1))
                    pc++
                }

                // iconst_3
                0x06 -> {
                    operandStack.push(OperandValue.IntValue(3))
                    pc++
                }

                // iconst_5
                0x08 -> {
                    operandStack.push(OperandValue.IntValue(5))
                    pc++
                }

                // ldc
                0x12 -> {
                    val index = code.getInt(pc + 1)
                    val sref = classFile.constantPool[index]
                    if (sref !is ConstantPoolEntry.StringReference) {
                        throw java.lang.IllegalStateException("Constant pool value at $index is not of type StringReference")
                    }
                    val sval = classFile.constantPool[sref.index]
                    if (sval !is ConstantPoolEntry.String) {
                        throw java.lang.IllegalStateException("Constant pool value at $index is not of type String")
                    }
                    operandStack.push(OperandValue.StringValue(sval.value))
                    pc += 2
                }

                // istore_1
                0x3C -> {
                    localVariables[1] = (operandStack.pop() as OperandValue.IntValue).value
                    pc++
                }

                // iload_1
                0x1B -> {
                    operandStack.push(OperandValue.IntValue(localVariables[1]))
                    pc++
                }

                // bipush
                0x10 -> {
                    operandStack.push(OperandValue.IntValue(code[pc + 1].toInt()))
                    pc += 2
                }

                // irem
                0x70 -> {
                    val a2 = (operandStack.pop() as OperandValue.IntValue).value
                    val a1 = (operandStack.pop() as OperandValue.IntValue).value
                    val res = a1 - (a1 / a2) * a2
                    operandStack.push(OperandValue.IntValue(res))
                    pc++
                }

                // iinc
                0x84 -> {
                    val index = code.getInt(pc + 1)
                    val delta = code[pc + 2].toInt()
                    localVariables[index] += delta
                    pc += 3
                }

                // ifne
                0x9A -> {
                    val n = (operandStack.pop() as OperandValue.IntValue).value
                    pc +=
                        if (n != 0) {
                            (code[pc + 1].toInt() and 0xFF shl 8) + code[pc + 2].toInt() and 0xFF
                        } else {
                            3
                        }
                }

                // if_icmpgt
                0xA3 -> {
                    val a2 = (operandStack.pop() as OperandValue.IntValue).value
                    val a1 = (operandStack.pop() as OperandValue.IntValue).value
                    pc +=
                        if (a1 > a2) {
                            (code[pc + 1].toInt() and 0xFF shl 8) + code[pc + 2].toInt() and 0xFF
                        } else {
                            3
                        }
                }

                // goto
                0xA7 -> {
                    pc += (code[pc + 1].toInt() shl 8) or code[pc + 2].toInt()
                }

                // return
                0xB1 -> {
                    break
                }

                // getstatic
                //
                // We cheat here (a bit): since the only function we are actually calling is System.out.println with
                // different signatures due to method overloading, we do not need to look in the constant pool, load the
                // object reference in the operand stack, etc.
                0xB2 -> {
                    operandStack.push(OperandValue.IntValue(Int.MIN_VALUE))
                    pc += 3
                }

                // invokevirtual
                //
                // In the real JVM we would have the correct object on the operand stack based on the previous
                // getstatic call and would now invoke the correct method. In our case, we simply print out a
                // number from the operand stack and choose the correct method based on the referenced index.
                0xB6 -> {
                    // Check what method we want to invoke.
                    val index = (code[pc + 1].toInt() and 0xFF shl 8) + code[pc + 2].toInt() and 0xFF
                    val name = getMethodName(index)

                    when (name) {
                        "java/io/PrintStream.println:(I)V" -> {
                            val num = (operandStack.pop() as OperandValue.IntValue).value
                            // Discard the non-existing object reference from getstatic
                            operandStack.pop()
                            println(num)
                        }
                        "java/io/PrintStream.println:(Ljava/lang/String;)V" -> {
                            val sval = (operandStack.pop() as OperandValue.StringValue).value
                            // Discard the non-existing object references from getstatic
                            operandStack.pop()
                            println(sval)
                        }
                        else -> {
                            throw IllegalArgumentException("Unknown method invocation pc=${pc - 8} name=$name")
                        }
                    }
                    pc += 3
                }

                else -> throw IllegalArgumentException("Unknown opcode pc=${pc - 8} opcode=%02X".format(opcode))
            }
        }
    }

    /**
     * getMethodName determines the actual method name based on the index to the method reference
     * into the constant pool.
     *
     * Note that we assume that the index and the constant pool are actually correct.
     **/
    private fun getMethodName(index: Int): String {
        val cp = classFile.constantPool

        val mref = cp[index] as ConstantPoolEntry.MethodReference
        val classRef = cp[mref.classIndex] as ConstantPoolEntry.ClassReference
        val className = cp[classRef.index] as ConstantPoolEntry.String
        val desc = cp[mref.descriptorIndex] as ConstantPoolEntry.Descriptor
        val name = cp[desc.nameIndex] as ConstantPoolEntry.String
        val type = cp[desc.typeIndex] as ConstantPoolEntry.String

        return "${className.value}.${name.value}:${type.value}"
    }

    /**
     * findEntryByteCode searches the class file for a method 'main' and returns
     * its corresponding bytecode.
     *
     * Note that we do not check for the correct signature, access modifiers, etc.
     * but only look for the correct method name.
     **/
    private fun findEntryByteCode(): ByteArray {
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