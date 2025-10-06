package com.example.demo.asynvirtual;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@SpringBootApplication
public class StartPoint {

    static void main(String[] args) throws Exception {
        ConfigurableApplicationContext ctx = new AnnotationConfigApplicationContext(StartPoint.class);
        //  SpringApplication.run(StartPoint.class, args);
        StartService st = ctx.getBean(StartService.class);

        st.run();
    }
}
