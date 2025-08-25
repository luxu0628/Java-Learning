package main.selfLearning;

public class Example2 {
    public static void main(String[] args) {
        //变量在使用之前必须完成初始化操作。换言之就是给变量赋值。
        // int a, b;
        // a = 5;
        // b = 10;
        int a = 5, b = 11;//可以在一行代码中同时声明多个变量并完成赋值
        int c = a + b;
        System.out.println("a + b = " + c);
        int d = b - a;
        System.out.println("b - a = " + d);
        int e = a * b; //注意：在Java中乘法使用的是*
        System.out.println("a * b = " + e);
        int f = b / a; //注意：在Java中除法使用的是/
        System.out.println("b / a = " + f);
    }
}
