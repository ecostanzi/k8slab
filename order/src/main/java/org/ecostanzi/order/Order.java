package org.ecostanzi.order;

import java.util.Map;

public class Order {

    private String id;
    private Map<String, Integer> products;

    public Order() {
    }

    public Order(String id, Map<String, Integer> products) {
        this.id = id;
        this.products = products;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Integer> getProducts() {
        return products;
    }

    public void setProducts(Map<String, Integer> products) {
        this.products = products;
    }

}
