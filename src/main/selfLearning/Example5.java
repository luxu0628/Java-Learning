package main.selfLearning;

public class Example5 {
    public static void main(String[] args) {
        int a=59;
        double b=a+1.5;
        System.out.println(b);
        int score = 59;
//自动类型转换
        double nextScore = score + 1.5; // 59 + 1.5 => 59.0 + 1.5 => 60.5
        System.out.println(nextScore);
//强制类型转换
        int nScore = (int)(score + 1.5); // 59 + 1.5 => 59.0 + 1.5 => 60.5
        System.out.println(nScore);
    }

}
