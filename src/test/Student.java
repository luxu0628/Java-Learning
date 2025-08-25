package test;

public class Student {
    public String name;
    public int id;
    public static int nextid=1;
    public static int totalCount=0;
    public  Student(String name) {
        this.name = name;
        this.id=nextid;
        totalCount++;
        nextid++;
    }
    public void introduce() {
        System.out.println("大家好，我是"+name+"我的学号是"+id);

    }
    public static void showTotal(){
        System.out.println("目前总共有"+totalCount+"个学生");
    }
}
