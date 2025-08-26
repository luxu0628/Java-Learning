package main.selfLearning;

import java.util.Scanner;

public class Exercise1 {
    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);
        System.out.println("Please enter your name:");
        String name=sc.next();
        System.out.println(String.format("你好，%s",name));
        System.out.println("你的年龄是多少？");
        Integer age=sc.nextInt();
        System.out.println(String.format("你好%s，你今年%d岁啦",name,age));
    }
}
