package com.example.demo_jdbc_samples.repository;

import com.example.demo_jdbc_samples.exception.CustomException;
import com.example.demo_jdbc_samples.model.OrderingStatements;
import com.example.demo_jdbc_samples.repository.mybatis.OrderingStatementsSqlMapper;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * ordering.statements with MyBatis (OrderingStatementsSqlMapper; primary key order_id, line_no).
 * Runs in the caller's transaction (TransactionRunner). Same operations and error codes as
 * the ScalarDB version: get of a missing row 9700, update / delete of a missing row 9100.
 */
@Repository
public class OrderingStatementsRepository {
    private final OrderingStatementsSqlMapper mapper;

    private int scanLimit = 100; // Default scan limit

    public OrderingStatementsRepository(OrderingStatementsSqlMapper mapper) {
        this.mapper = mapper;
    }

    public void setScanLimit(int scanLimit) {
        this.scanLimit = scanLimit;
    }

    public int getScanLimit() {
        return scanLimit;
    }

    // Get Record by Primary Key
    public OrderingStatements getOrderingStatements(OrderingStatements statements) {
        OrderingStatements row = mapper.select(statements.getOrderId(), statements.getLineNo());
        if (row == null) {
            throw new CustomException("No record found in OrderingStatements", 9700);
        }
        return row;
    }

    // Insert Record (a duplicate key fails)
    public OrderingStatements insertOrderingStatements(OrderingStatements statements) {
        mapper.insert(statements);
        return statements;
    }

    // Update Record (if exists)
    public OrderingStatements updateOrderingStatements(OrderingStatements statements) {
        if (mapper.update(statements) == 0) {
            throw new CustomException("Update condition not met: no record in OrderingStatements", 9100);
        }
        return statements;
    }

    // Upsert Record: update, or insert if there is no such row. No ON CONFLICT / MERGE, so the
    // same SQL runs on PostgreSQL, Oracle and through ScalarDB Native JDBC (which refuses them).
    // A concurrent insert of the same key fails with a duplicate key.
    public OrderingStatements upsertOrderingStatements(OrderingStatements statements) {
        if (mapper.update(statements) == 0) {
            mapper.insert(statements);
        }
        return statements;
    }

    // Delete Record (if exists)
    public void deleteOrderingStatements(OrderingStatements statements) {
        if (mapper.delete(statements.getOrderId(), statements.getLineNo()) == 0) {
            throw new CustomException("Delete condition not met: no record in OrderingStatements", 9100);
        }
    }

    // Scan All Records (up to scanLimit, no order)
    public List<OrderingStatements> getOrderingStatementsListAll() {
        return mapper.selectAll(scanLimit);
    }

    // Scan Records by Partition Key (order_id), in line_no order as the ScalarDB clustering key
    public List<OrderingStatements> getOrderingStatementsListByPk(OrderingStatements statements) {
        return mapper.selectByOrderId(statements.getOrderId(), scanLimit);
    }

    public void truncate() {
        mapper.truncate();
    }
}
