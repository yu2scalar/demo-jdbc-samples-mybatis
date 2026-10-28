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
 * map-underscore-to-camel-case.
 */
@Mapper
public interface InventoryItemsSqlMapper {
    String COLUMNS = "item_id, name, price, stock";

    @Select("SELECT " + COLUMNS + " FROM inventory.items WHERE item_id = #{itemId}")
    InventoryItems select(@Param("itemId") int itemId);

    @Select("SELECT " + COLUMNS + " FROM inventory.items WHERE item_id = #{itemId} FOR UPDATE")
    InventoryItems selectForUpdate(@Param("itemId") int itemId);

    @Insert("INSERT INTO inventory.items (" + COLUMNS + ") VALUES (#{itemId}, #{name}, #{price}, #{stock})")
    int insert(InventoryItems items);

    @Update("UPDATE inventory.items SET name = #{name}, price = #{price}, stock = #{stock} WHERE item_id = #{itemId}")
    int update(InventoryItems items);


    @Delete("DELETE FROM inventory.items WHERE item_id = #{itemId}")
    int delete(@Param("itemId") int itemId);

    @Select("SELECT " + COLUMNS + " FROM inventory.items FETCH FIRST #{limit} ROWS ONLY")
    List<InventoryItems> selectAll(@Param("limit") int limit);

    @Select("SELECT " + COLUMNS + " FROM inventory.items WHERE item_id = #{itemId} FETCH FIRST #{limit} ROWS ONLY")
    List<InventoryItems> selectByPk(@Param("itemId") int itemId, @Param("limit") int limit);

    // DELETE, not TRUNCATE: ScalarDB Native JDBC refuses TRUNCATE on a ScalarDB table
    @Delete("DELETE FROM inventory.items")
    void truncate();
}
