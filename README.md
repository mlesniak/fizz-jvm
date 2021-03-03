## TODOs

- Understand class file
- Be able to read a class file
- "Simulate execution"

## Disassembled code

    $ javap -c Main.class
    Compiled from "Main.java"
    public class Main {
    public Main();
    Code:
    0: aload_0
    1: invokespecial #1                  // Method java/lang/Object."<init>":()V
    4: return
    
    public static void main(java.lang.String[]);
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