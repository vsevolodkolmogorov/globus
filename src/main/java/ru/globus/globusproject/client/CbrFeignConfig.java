package ru.globus.globusproject.client;

import feign.codec.Decoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CbrFeignConfig {
    @Bean
    public Decoder feignDecoder() {
        return new SimpleJaxbDecoder();
    }
}
