package com.abr.orders.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderItem {

    private final UUID productId;
    private final int quantity;
    private final Money price;

    public OrderItem (UUID productId, int quantity, Money price){
        if (quantity <= 0){
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    public Money total(){
       return new Money(price.getAmount().multiply(BigDecimal.valueOf(quantity)));
    }

    public UUID getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public Money getPrice() {
        return price;
    }
}


