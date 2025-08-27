package com.example.demo.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import com.amazonaws.services.s3.AmazonS3;

@TestConfiguration
public class FakeS3Config {
    @Bean
    public AmazonS3 amazonS3() {
        return Mockito.mock(AmazonS3.class);
    }
}
