package com.chuan;

import com.chuan.spring.formework.context.GPApplicationContext;

/**
 * author:曲终、人散
 * Date:2019/5/1 16:33
 */
public class Test {
    public static void main(String[] args) {
        GPApplicationContext context = new GPApplicationContext("classpath:application.properties");
        Object myAction = context.getBean("myAction");

        System.out.println(myAction);
    }
}
