package com.hedbanz.hedbanzAPI;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.PostConstruct;
import java.util.LinkedHashSet;
import java.util.Set;

@SpringBootTest
public class HedbanzApiApplicationTests {

    @Test
    public void contextLoads() {
        String str = "\"asdd\",\"asdd\",\"asdd\",\"asdd\"";
        String str1 = str.replaceAll("\"","");
        System.out.println(str1);
    }
}

class Base {
    static {
        System.out.printf("%s - %s - %s\n", "base", "static", "block");
    }

    {
        System.out.printf("%s - %s - %s\n", "base", "instance", "block");
    }

    public Base() {
        System.out.printf("%s - %s\n", "base", "constructor");
    }

    @PostConstruct
    public void init() {
        System.out.printf("%s - %s\n", "base", "PostConstruct");
    }

    public void hello() {
        System.out.printf("%s - %s\n", "base", "method");
    }
}

class Sub extends Base {
    static {
        System.out.printf("%s - %s - %s\n", "sub", "static", "block");
    }

    {
        System.out.printf("%s - %s - %s\n", "sub", "instance", "block");
    }

    public Sub() {
        System.out.printf("%s - %s\n", "sub", "constructor");
    }

    @PostConstruct
    public void init() {
        System.out.printf("%s - %s\n", "sub", "PostConstruct");
    }

    @Override
    public void hello() {
        super.hello();
        System.out.printf("%s - %s\n", "sub", "method");
    }
}
