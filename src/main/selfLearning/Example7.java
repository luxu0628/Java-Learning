package main.selfLearning;

import java.util.Scanner;

public class Example7 {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.println("请输入三个成绩");
        double score1=input.nextDouble();
        double score2=input.nextDouble();
        double score3=input.nextDouble();
        System.out.println(String.format("三个人的平均成绩为：%f",(score1+score2+score3)/3));
    }
}
