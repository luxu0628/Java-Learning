package main.selfLearning;


public class Example3 {
    public static void main(String[] args) {
        int number=5678;
        int a=number/1000;
        int b=number/100%10;
        int c=number/10%10;
        int d=number%10;
        System.out.printf("千位数是：%d,百位数是：%d，十位数是：%d，个位数是：%d%n",a,b,c,d);
        System.out.println("千位"+a);//ctrl + d 复制一行代码
        System.out.println("百位"+b);
        System.out.println("十位"+c);
        System.out.println("个位"+d);
        int e = 10 % 3; //求10除以3的余数
        System.out.println("10 % 3 = " + e);
        //++运算符分为前置++和后置++
        int count = 0;
        count++;//表示自加1 => count = 1;
        ++count;//表示自加1 => count = 2;
//如果一行代码中，只有++运算符，没有其他运算符，那么不论++在前，还是在后执行结果都是一样的
//因为++运算符在变量的后面，因此，先将变量count的值赋值给变量number1,然后变量count自加1
        int number1 = count++; //number1 = 2 count = 3;
//因为++运算符在变量的前面，因此，变量count先自加1，然后将count变量赋值给变量number2
        int number2 = ++count; //count = 4 number2 = 4;
        System.out.println(number1);
        System.out.println(number2);
        int f = 10;
//在java中运算都是从左往右依次执行，因此c++先执行，++c后执行。
//c++ => c = 10; => c = 11;
//++c => c = 12
        int j = f++ + ++f;
        System.out.println(j); //22 11+11? 10+12?
    }
}
