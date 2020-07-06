package org.ecostanzi.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@RestController
public class HomeController {

    private final Logger logger = LoggerFactory.getLogger(HomeController.class);
    private final RestTemplate restTemplate;

    public HomeController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping
    public List<Product> hello() {
        logger.info("Load order page");
        Product[] products = restTemplate.getForObject("/products", Product[].class);
        return Arrays.asList(products);
    }
}
