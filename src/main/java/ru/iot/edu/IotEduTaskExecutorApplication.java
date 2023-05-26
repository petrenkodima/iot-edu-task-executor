package ru.iot.edu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class IotEduTaskExecutorApplication {

    public static void main(String[] args) {
        SpringApplication.run(IotEduTaskExecutorApplication.class, args);
    }

}
