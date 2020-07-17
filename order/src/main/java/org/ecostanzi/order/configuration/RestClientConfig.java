package org.ecostanzi.order.configuration;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestClientConfig {

    @Bean
    public RestTemplate restTemplate(Environment env) {
        return new RestTemplateBuilder()
                .rootUri(env.getProperty("product-endpoint"))
                .build();

    }
}
