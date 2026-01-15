package com.paozero.game;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.Scanner;

@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
public class Starter {
    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(Starter.class, args);
        System.out.println("service-persist start...");
        Scanner sc = new Scanner(System.in);
        while (sc.hasNextLine()) {
            String input = sc.nextLine();
            if("a".equals(input)){
                JobGroupBusiness jobGroupBusiness = applicationContext.getBean(JobGroupBusiness.class);
                jobGroupBusiness.testOut();
            }
            if("b".equals(input)){
                JobGroupBusiness jobGroupBusiness = applicationContext.getBean(JobGroupBusiness.class);
                jobGroupBusiness.test();
            }
            if("c".equals(input)){
                JobGroupBusiness jobGroupBusiness = applicationContext.getBean(JobGroupBusiness.class);
                jobGroupBusiness.testTran();
            }
            if("d".equals(input)){
                TestBusiness testBusiness = applicationContext.getBean(TestBusiness.class);
                testBusiness.test();
            }
        }

        sc.close();
    }
}
