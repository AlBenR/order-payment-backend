package com.abr.orders.infrastructure.persistence.adapter;

import com.abr.orders.domain.model.*;
import com.abr.orders.infrastructure.persistence.entity.OrderEntity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderPersistenceMapperTest {

    private final OrderPersistenceMapper mapper = new OrderPersistenceMapper();

    @Test
    void shouldMapOrderWithStatusHistoryBothWays() {

        // given
        Order order = Order.create(
                UUID.randomUUID(),
                List.of(sampleItem())
        );

        order.confirm();
        order.markAsPaid();
        order.ship();

        // when
        OrderEntity entity = mapper.toEntity(order);
        Order reconstructed = mapper.toDomain(entity);

        // then
        assertEquals(order.getId(), reconstructed.getId());
        assertEquals(order.getCustomerId(), reconstructed.getCustomerId());
        assertEquals(order.getStatus(), reconstructed.getStatus());

        assertEquals(
                order.getItems().size(),
                reconstructed.getItems().size()
        );

        assertEquals(
                order.getStatusHistory().size(),
                reconstructed.getStatusHistory().size()
        );

        // comprobamos que el último estado sea el mismo
        assertEquals(
                order.getStatusHistory().get(order.getStatusHistory().size() - 1).getStatus(),
                reconstructed.getStatusHistory().get(reconstructed.getStatusHistory().size() - 1).getStatus()
        );
    }

    private OrderItem sampleItem() {
        return new OrderItem(
                UUID.randomUUID(),
                2,
                new Money(new BigDecimal("10.00"))
        );
    }
}