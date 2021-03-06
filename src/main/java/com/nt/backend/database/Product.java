package com.nt.backend.database;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
@Setter
@Getter
public class Product {

    @Id
    @Column(name="product_name")
    private String productName;


    @Column(name="product_items_cnt")
    private int productItemsCount;

    public Product(String productName) {
        this.productName = productName;
        this.productItemsCount = 1;
    }
}
