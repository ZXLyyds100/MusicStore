package com.music.store.studioproject;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.music.store.studioproject.dao")
public class StudioProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudioProjectApplication.class, args);
    }

}
