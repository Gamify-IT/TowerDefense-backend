package de.unistuttgart.towerdefensebackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class TowerDefenseServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TowerDefenseServiceApplication.class, args);
    }
}