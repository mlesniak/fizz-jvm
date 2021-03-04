# Overview

While showing your basic programming skills by implementing [FizzBuzz](https://en.wikipedia.org/wiki/Fizz_buzz) is nice, I thought *Let's bring it to the next level*. In this repository you'll find a **functional (but very basic) JVM implementation** which
is able to interpret (run) the class file produced by `javac` from the following source code, i.e.

    $ cat fizz-buzz/Main.java 
    public class Main {
        public static void main(String[] args) {
            for (int i = 1; i <= 100; i++) {
                if (i % 3 == 0 && i % 5 == 0) {
                    System.out.println("FizzBuzz");
                } else if (i % 3 == 0) {
                    System.out.println("Fizz");
                } else if (i % 5 == 0) {
                    System.out.println("Buzz");
                } else {
                    System.out.println(i);
                }
            }
        }
    }

    $ # Compile file
    $ javac -d fizz-buzz fizz-buzz/Main.java

    # Run fizz-JVM
    $ kotlin -cp build/libs/fizz-jvm-1.0-SNAPSHOT.jar com.mlesniak.jvm.MainKt fizz-buzz/Main.class
    1
    2
    Fizz
    4
    Buzz
    Fizz
    7
    8
    Fizz
    Buzz
    11
    Fizz
    13
    14
    FizzBuzz
    16
    17
    Fizz
    19
    ...

## Approach

After understanding the class file format and loading the class file and bytecode, I set the program counter for the instruction set to 0, wrote an empty `when` statement with a default branch throwing an 
error about an unknown opcode and implemented all opcodes until the example ran without errors.

## How to build

You need to have Gradle and Kotlin installed. Compile everything with

    ./gradlew build

and run the interpreter with

    kotlin -cp build/libs/fizz-jvm-1.0-SNAPSHOT.jar com.mlesniak.jvm.MainKt fizz-buzz/Main.class
    
## Limitations

A lot. Anything outside the scope of the trivial FizzBuzz example will not work, i.e. no other methods, no function calls, no additional local variables. Nevertheless, it was
a **great learning experience** to peek under the hood of the JVM's classfile loading, the bytecode instruction set, and execution structure.

Or, to state it otherwise: this implementation is able to execute the following instruction set

    $ javap -c -p -s -constants fizz-buzz/Main.class
      <... ommitted ...>
      public static void main(java.lang.String[]);
        descriptor: ([Ljava/lang/String;)V
        Code:
           0: iconst_1
           1: istore_1
           2: iload_1
           3: bipush        100
           5: if_icmpgt     78
           8: iload_1
           9: iconst_3
          10: irem
          11: ifne          31
          14: iload_1
          15: iconst_5
          16: irem
          17: ifne          31
          20: getstatic     #7                  // Field java/lang/System.out:Ljava/io/PrintStream;
          23: ldc           #13                 // String FizzBuzz
          25: invokevirtual #15                 // Method java/io/PrintStream.println:(Ljava/lang/String;)V
          28: goto          72
          31: iload_1
          32: iconst_3
          33: irem
          34: ifne          48
          37: getstatic     #7                  // Field java/lang/System.out:Ljava/io/PrintStream;
          40: ldc           #21                 // String Fizz
          42: invokevirtual #15                 // Method java/io/PrintStream.println:(Ljava/lang/String;)V
          45: goto          72
          48: iload_1
          49: iconst_5
          50: irem
          51: ifne          65
          54: getstatic     #7                  // Field java/lang/System.out:Ljava/io/PrintStream;
          57: ldc           #23                 // String Buzz
          59: invokevirtual #15                 // Method java/io/PrintStream.println:(Ljava/lang/String;)V
          62: goto          72
          65: getstatic     #7                  // Field java/lang/System.out:Ljava/io/PrintStream;
          68: iload_1
          69: invokevirtual #25                 // Method java/io/PrintStream.println:(I)V
          72: iinc          1, 1
          75: goto          2
          78: return
    }


## Resources

- [Official class file layout](https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html)
- [Class file layout](https://en.wikipedia.org/wiki/Java_class_file#General_layout)
- [JVM instruction set](https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html)