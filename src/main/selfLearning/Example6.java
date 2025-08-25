package main.selfLearning;

import java.util.Scanner;

public class Example6 {
    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);
        System.out.println("Please enter your name:");
        String name =sc.next();
        System.out.println(name);
        System.out.println("Please enter a integer:");
        int num=sc.nextInt();
        System.out.println(num);
        System.out.println("Please enter your score:");
        Double score=sc.nextDouble();
        System.out.println(String.format("亲爱的%s,你的成绩是%f",name,score));

    }
}
