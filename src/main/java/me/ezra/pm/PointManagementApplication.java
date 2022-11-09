package me.ezra.pm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@EnableBatchProcessing
@SpringBootApplication
@Slf4j
public class PointManagementApplication {
    public static void main(String[] args) {
        log.info("application arguments: " + String.join(",", args));
        SpringApplication.run(PointManagementApplication.class, args);
    }
}
