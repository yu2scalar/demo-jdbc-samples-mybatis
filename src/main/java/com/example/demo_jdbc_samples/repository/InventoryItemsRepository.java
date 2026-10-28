package com.example.demo_jdbc_samples.repository;

import com.example.demo_jdbc_samples.exception.CustomException;
import com.example.demo_jdbc_samples.model.InventoryItems;
import com.example.demo_jdbc_samples.repository.mybatis.InventoryItemsMapper;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * inventory.items with MyBatis (InventoryItemsMapper). Runs in the caller's transaction
 * (TransactionRunner). Same operations and error codes as the ScalarDB version:
 * get of a missing row 9700, update / delete of a missing row 9100.
 */
@Repository
public class InventoryItemsRepository {
    private final InventoryItemsMapper mapper;

    private int scanLimit = 100; // Default scan limit

    public InventoryItemsRepository(InventoryItemsMapper mapper) {
        this.mapper = mapper;
    }

    public void setScanLimit(int scanLimit) {
        this.scanLimit = scanLimit;
    }

    public int getScanLimit() {
        return scanLimit;
    }

    // Get Record by Primary Key
    public InventoryItems getInventoryItems(InventoryItems items) {
        return found(mapper.select(items.getItemId()));
    }

    // Get Record by Primary Key and lock it until the end of the transaction
    public InventoryItems getInventoryItemsForUpdate(InventoryItems items) {
        return found(mapper.selectForUpdate(items.getItemId()));
    }

    private InventoryItems found(InventoryItems row) {
        if (row == null) {
            throw new CustomException("No record found in InventoryItems", 9700);
        }
        return row;
    }

    // Insert Record (a duplicate key fails)
    public InventoryItems insertInventoryItems(InventoryItems items) {
        mapper.insert(items);
        return items;
    }

    // Update Record (if exists)
    public InventoryItems updateInventoryItems(InventoryItems items) {
        if (mapper.update(items) == 0) {
            throw new CustomException("Update condition not met: no record in InventoryItems", 9100);
        }
        return items;
    }

    // Upsert Record
    public InventoryItems upsertInventoryItems(InventoryItems items) {
        mapper.upsert(items);
        return items;
    }

    // Delete Record (if exists)
    public void deleteInventoryItems(InventoryItems items) {
        if (mapper.delete(items.getItemId()) == 0) {
            throw new CustomException("Delete condition not met: no record in InventoryItems", 9100);
        }
    }

    // Scan All Records (up to scanLimit, no order)
    public List<InventoryItems> getInventoryItemsListAll() {
        return mapper.selectAll(scanLimit);
    }

    // Scan Records by Primary Key
    public List<InventoryItems> getInventoryItemsListByPk(InventoryItems items) {
        return mapper.selectByPk(items.getItemId(), scanLimit);
    }

    public void truncate() {
        mapper.truncate();
    }
}
