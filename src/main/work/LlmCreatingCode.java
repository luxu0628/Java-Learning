package main.work;
import java.util.Random;
import java.util.Scanner;

public class LlmCreatingCode {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        int numberToGuess = random.nextInt(100) + 1; // 1~100 的随机数
        int attempts = 10; // 玩家最多猜 10 次

        System.out.println("🎮 欢迎来到猜数字游戏！");
        System.out.println("我已经想好了一个 1 到 100 的数字，你有 " + attempts + " 次机会。");

        while (attempts > 0) {
            System.out.print("请输入你的猜测：");
            int guess = scanner.nextInt();

            if (guess == numberToGuess) {
                System.out.println("🎉 恭喜你，猜对了！答案就是 " + numberToGuess);
                break;
            } else if (guess < numberToGuess) {
                System.out.println("太小了！");
            } else {
                System.out.println("太大了！");
            }

            attempts--;
            if (attempts > 0) {
                System.out.println("你还有 " + attempts + " 次机会。");
            } else {
                System.out.println("😢 很遗憾，你没有猜中。正确答案是 " + numberToGuess);
            }
        }

        scanner.close();
    }
}
