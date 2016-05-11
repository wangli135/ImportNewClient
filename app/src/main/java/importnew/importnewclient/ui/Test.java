package importnew.importnewclient.ui;

import java.lang.reflect.Field;

/**
 * Created by Xingfeng on 2016/5/11.
 */
public class Test {

    static class Person{
        private int age;

        public Person(int age){
            this.age=age;
        }

        public int getAge(){
            return age;
        }
    }

    public static void main(String[] args) {

        Person person=new Person(10);
        System.out.println("Age: "+person.getAge());

        try {
            Class personClass=person.getClass();
            Field ageFiled=personClass.getDeclaredField("age");
            ageFiled.setAccessible(true);
            ageFiled.setInt(person,20);
            System.out.println("Age: "+person.getAge());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
