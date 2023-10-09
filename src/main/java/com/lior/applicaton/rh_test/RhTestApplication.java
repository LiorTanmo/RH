package com.lior.applicaton.rh_test;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RhTestApplication {

   // static final Logger log = (Logger) LoggerFactory.getLogger(RhTestApplication.class);
    public static void main(String[] args) {
        SpringApplication.run(RhTestApplication.class, args);
    }

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

}
