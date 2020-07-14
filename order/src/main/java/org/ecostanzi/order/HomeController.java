package org.ecostanzi.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@RestController
public class HomeController {

    final String hostname = System.getenv().getOrDefault("HOSTNAME", "unknown");

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

    @GetMapping("/sysresources")
    public String getSystemResources() {
        long memory = Runtime.getRuntime().maxMemory();
        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println("/sysresources " + hostname);
        return
                " Memory: " + (memory / 1024 / 1024) +
                        " Cores: " + cores + "\n";
    }

    @GetMapping("/dostuff")
    public String doStuff(@RequestParam(value = "base", defaultValue = "1", required = false) Integer baseNum) {
        long start = System.currentTimeMillis();
        mySlowFunction(baseNum);
        long ms = System.currentTimeMillis() - start;
        return "doing stuff took " + ms + "ms";
    }

    void mySlowFunction(int baseNumber) {
        System.out.println("mySlowFunction");
        int result = 0;
        for (var i = Math.pow(baseNumber, 7); i >= 0; i--) {
            result += Math.atan(i) * Math.tan(i);
        };
    }
}
