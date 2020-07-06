package org.ecostanzi.product;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

@RestController
public class ProductController {

    private final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @GetMapping("/products")
    public List<Product> getProducts() throws UnknownHostException {
        var ip = InetAddress.getLocalHost();
        var hostname = ip.getHostName();
        logger.info("Getting the list of products  from {}", hostname);
        return List.of(
                new Product("Book1", 12.2d),
                new Product("Book2", 12.2d)
        );
    }

}
