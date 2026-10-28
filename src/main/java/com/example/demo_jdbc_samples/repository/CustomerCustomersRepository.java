package com.example.demo_jdbc_samples.repository;

import com.example.demo_jdbc_samples.exception.CustomException;
import com.example.demo_jdbc_samples.model.CustomerCustomers;
import com.example.demo_jdbc_samples.repository.mybatis.CustomerCustomersSqlMapper;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * customer.customers with MyBatis (CustomerCustomersSqlMapper). Runs in the caller's transaction
 * (TransactionRunner). Same operations and error codes as the ScalarDB version:
 * get of a missing row 9700, update / delete of a missing row 9100.
 */
@Repository
public class CustomerCustomersRepository {
    private final CustomerCustomersSqlMapper mapper;

    private int scanLimit = 100; // Default scan limit

    public CustomerCustomersRepository(CustomerCustomersSqlMapper mapper) {
        this.mapper = mapper;
    }

    public void setScanLimit(int scanLimit) {
        this.scanLimit = scanLimit;
    }

    public int getScanLimit() {
        return scanLimit;
    }

    // Get Record by Primary Key
    public CustomerCustomers getCustomerCustomers(CustomerCustomers customers) {
        return found(mapper.select(customers.getCustomerId()));
    }

    // Get Record by Primary Key and lock it until the end of the transaction
    public CustomerCustomers getCustomerCustomersForUpdate(CustomerCustomers customers) {
        return found(mapper.selectForUpdate(customers.getCustomerId()));
    }

    private CustomerCustomers found(CustomerCustomers row) {
        if (row == null) {
            throw new CustomException("No record found in CustomerCustomers", 9700);
        }
        return row;
    }

    // Insert Record (a duplicate key fails)
    public CustomerCustomers insertCustomerCustomers(CustomerCustomers customers) {
        mapper.insert(customers);
        return customers;
    }

    // Update Record (if exists)
    public CustomerCustomers updateCustomerCustomers(CustomerCustomers customers) {
        if (mapper.update(customers) == 0) {
            throw new CustomException("Update condition not met: no record in CustomerCustomers", 9100);
        }
        return customers;
    }

    // Upsert Record: update, or insert if there is no such row. No ON CONFLICT / MERGE, so the
    // same SQL runs on PostgreSQL, Oracle and through ScalarDB Native JDBC (which refuses them).
    // A concurrent insert of the same key fails with a duplicate key.
    public CustomerCustomers upsertCustomerCustomers(CustomerCustomers customers) {
        if (mapper.update(customers) == 0) {
            mapper.insert(customers);
        }
        return customers;
    }

    // Delete Record (if exists)
    public void deleteCustomerCustomers(CustomerCustomers customers) {
        if (mapper.delete(customers.getCustomerId()) == 0) {
            throw new CustomException("Delete condition not met: no record in CustomerCustomers", 9100);
        }
    }

    // Scan All Records (up to scanLimit, no order)
    public List<CustomerCustomers> getCustomerCustomersListAll() {
        return mapper.selectAll(scanLimit);
    }

    // Scan Records by Primary Key
    public List<CustomerCustomers> getCustomerCustomersListByPk(CustomerCustomers customers) {
        return mapper.selectByPk(customers.getCustomerId(), scanLimit);
    }

    /**
     * Search by name: exact (name = value) or prefix (name LIKE 'value%').
     * limit 0 = no limit. Not locked.
     */
    public List<CustomerCustomers> searchByName(String value, boolean prefix, int limit) {
        return mapper.searchByName(prefix ? value + "%" : value, prefix, limit);
    }

    // customer_id, name and credit_total of every row (load test check)
    public List<CustomerCustomers> getAllRows() {
        return mapper.selectEveryRow();
    }

    public void truncate() {
        mapper.truncate();
    }
}
