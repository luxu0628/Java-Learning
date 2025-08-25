package main.selfLearning;

public class Example4 {
    public static void main(String[] args) {
        int number1 = 10;
//将number1 + 10 的结果赋值给number1
        number1 = number1 + 10;
        number1 += 10;//与上一行代码的作用是一样的
        double d1 = 20.0;
        d1 = d1 / 2;
        d1 /= 2; //与上一行代码的作用是一样的
        int result1 = 10;//整数
        double add = 2.0;//浮点数
        result1 = (int)(result1 + add);//add是double类型，占八个字节，而result1是int，占四个空间，计算结果是一个
        //高位的数字，不能自动从高向低转化.要强制类型转换
        result1 += add; //说明 += 还具有其他的功能，可以
    }
}
