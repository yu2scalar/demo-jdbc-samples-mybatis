package com.example.demo_jdbc_samples.repository.mybatis;

import com.example.demo_jdbc_samples.model.InventoryItems;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * inventory.items with MyBatis annotations. The columns are mapped to the model by
 * map-underscore-to-camel-case. The upsert differs per database (databaseId).
 */
@Mapper
public interface InventoryItemsMapper {
    String COLUMNS = "item_id, name, price, stock";

    @Select("SELECT " + COLUMNS + " FROM inventory.items WHERE item_id = #{itemId}")
    InventoryItems select(@Param("itemId") int itemId);

    @Select("SELECT " + COLUMNS + " FROM inventory.items WHERE item_id = #{itemId} FOR UPDATE")
    InventoryItems selectForUpdate(@Param("itemId") int itemId);

    @Insert("INSERT INTO inventory.items (" + COLUMNS + ") VALUES (#{itemId}, #{name}, #{price}, #{stock})")
    int insert(InventoryItems items);

    @Update("UPDATE inventory.items SET name = #{name}, price = #{price}, stock = #{stock} WHERE item_id = #{itemId}")
    int update(InventoryItems items);

    @Insert(databaseId = "postgresql", value = "INSERT INTO inventory.items (" + COLUMNS + ")"
        + " VALUES (#{itemId}, #{name}, #{price}, #{stock})"
        + " ON CONFLICT (item_id) DO UPDATE SET name = EXCLUDED.name, price = EXCLUDED.price, stock = EXCLUDED.stock")
    @Insert(databaseId = "oracle", value = "MERGE INTO inventory.items t"
        + " USING (SELECT #{itemId} AS item_id, #{name} AS name, #{price} AS price, #{stock} AS stock FROM dual) s"
        + " ON (t.item_id = s.item_id)"
        + " WHEN MATCHED THEN UPDATE SET t.name = s.name, t.price = s.price, t.stock = s.stock"
        + " WHEN NOT MATCHED THEN INSERT (" + COLUMNS + ") VALUES (s.item_id, s.name, s.price, s.stock)")
    int upsert(InventoryItems items);

    @Delete("DELETE FROM inventory.items WHERE item_id = #{itemId}")
    int delete(@Param("itemId") int itemId);

    @Select("SELECT " + COLUMNS + " FROM inventory.items FETCH FIRST #{limit} ROWS ONLY")
    List<InventoryItems> selectAll(@Param("limit") int limit);

    @Select("SELECT " + COLUMNS + " FROM inventory.items WHERE item_id = #{itemId} FETCH FIRST #{limit} ROWS ONLY")
    List<InventoryItems> selectByPk(@Param("itemId") int itemId, @Param("limit") int limit);

    @Update("TRUNCATE TABLE inventory.items")
    void truncate();
}
