package com.example.demo_jdbc_samples.repository;

import com.example.demo_jdbc_samples.exception.CustomException;
import com.example.demo_jdbc_samples.model.OrderingOrders;
import com.example.demo_jdbc_samples.repository.mybatis.OrderingOrdersMapper;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * ordering.orders with MyBatis (OrderingOrdersMapper). Runs in the caller's transaction
 * (TransactionRunner). Same operations and error codes as the ScalarDB version:
 * get of a missing row 9700, update / delete of a missing row 9100.
 */
@Repository
public class OrderingOrdersRepository {
    private final OrderingOrdersMapper mapper;

    private int scanLimit = 100; // Default scan limit

    public OrderingOrdersRepository(OrderingOrdersMapper mapper) {
        this.mapper = mapper;
    }

    public void setScanLimit(int scanLimit) {
        this.scanLimit = scanLimit;
    }

    public int getScanLimit() {
        return scanLimit;
    }

    // Get Record by Primary Key
    public OrderingOrders getOrderingOrders(OrderingOrders orders) {
        return found(mapper.select(orders.getOrderId()));
    }

    // Get Record by Primary Key and lock it until the end of the transaction
    public OrderingOrders getOrderingOrdersForUpdate(OrderingOrders orders) {
        return found(mapper.selectForUpdate(orders.getOrderId()));
    }

    private OrderingOrders found(OrderingOrders row) {
        if (row == null) {
            throw new CustomException("No record found in OrderingOrders", 9700);
        }
        return row;
    }

    // Insert Record (a duplicate key fails)
    public OrderingOrders insertOrderingOrders(OrderingOrders orders) {
        mapper.insert(orders);
        return orders;
    }

    // Update Record (if exists)
    public OrderingOrders updateOrderingOrders(OrderingOrders orders) {
        if (mapper.update(orders) == 0) {
            throw new CustomException("Update condition not met: no record in OrderingOrders", 9100);
        }
        return orders;
    }

    // Upsert Record
    public OrderingOrders upsertOrderingOrders(OrderingOrders orders) {
        mapper.upsert(orders);
        return orders;
    }

    // Delete Record (if exists)
    public void deleteOrderingOrders(OrderingOrders orders) {
        if (mapper.delete(orders.getOrderId()) == 0) {
            throw new CustomException("Delete condition not met: no record in OrderingOrders", 9100);
        }
    }

    // Scan All Records (up to scanLimit, no order)
    public List<OrderingOrders> getOrderingOrdersListAll() {
        return mapper.selectAll(scanLimit);
    }

    // Scan Records by Primary Key
    public List<OrderingOrders> getOrderingOrdersListByPk(OrderingOrders orders) {
        return mapper.selectByPk(orders.getOrderId(), scanLimit);
    }

    // All orders of a customer (index on customer_id; no limit, as the ScalarDB version)
    public List<OrderingOrders> getOrderingOrdersListByCustomerId(int customerId) {
        return mapper.selectByCustomerId(customerId);
    }

    public void truncate() {
        mapper.truncate();
    }
}
