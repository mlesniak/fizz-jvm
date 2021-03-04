# Overview

While showing your basic programming skills by implementing [FizzBuzz](https://en.wikipedia.org/wiki/Fizz_buzz) is nice, I thought *Let's bring it to the next level'. In this repository you'll find a functional JVM implementation which
is able to interpret the class file produced by `javac` from the following source code, i.e.

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

## How to build

You need to have Gradle and Kotlin installed. Compile everything with

    ./gradlew build

and run the interpreter with

    kotlin -cp build/libs/fizz-jvm-1.0-SNAPSHOT.jar com.mlesniak.jvm.MainKt fizz-buzz/Main.class
    
## Limitations

A lot. Anything outside the scope of the trivial FizzBuzz example will not work, i.e. no other methods, no function calls, no additional local variables. Nevertheless, it was
a great learning experience to peek under the hood of the JVM's classfile loading, the bytecode instruction set, and execution structure.

## Java Compiler does not check i%3==0 twice! 

## Resources

- (Official class file layout)[https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html]
- (Class file layout)[https://en.wikipedia.org/wiki/Java_class_file#General_layout]
- (JVM instruction set)[https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html]