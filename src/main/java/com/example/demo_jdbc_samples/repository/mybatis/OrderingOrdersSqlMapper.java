package com.example.demo_jdbc_samples.repository.mybatis;

import com.example.demo_jdbc_samples.model.OrderingOrders;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * ordering.orders with MyBatis annotations. The columns are mapped to the model by
 * map-underscore-to-camel-case. The upsert differs per database (databaseId).
 */
@Mapper
public interface OrderingOrdersSqlMapper {
    String COLUMNS = "order_id, customer_id, created_at, status, total_amount";

    @Select("SELECT " + COLUMNS + " FROM ordering.orders WHERE order_id = #{orderId}")
    OrderingOrders select(@Param("orderId") String orderId);

    @Select("SELECT " + COLUMNS + " FROM ordering.orders WHERE order_id = #{orderId} FOR UPDATE")
    OrderingOrders selectForUpdate(@Param("orderId") String orderId);

    @Insert("INSERT INTO ordering.orders (" + COLUMNS + ")"
        + " VALUES (#{orderId}, #{customerId}, #{createdAt}, #{status}, #{totalAmount})")
    int insert(OrderingOrders orders);

    @Update("UPDATE ordering.orders SET customer_id = #{customerId}, created_at = #{createdAt}, status = #{status},"
        + " total_amount = #{totalAmount} WHERE order_id = #{orderId}")
    int update(OrderingOrders orders);

    @Insert(databaseId = "postgresql", value = "INSERT INTO ordering.orders (" + COLUMNS + ")"
        + " VALUES (#{orderId}, #{customerId}, #{createdAt}, #{status}, #{totalAmount})"
        + " ON CONFLICT (order_id) DO UPDATE SET customer_id = EXCLUDED.customer_id, created_at = EXCLUDED.created_at,"
        + " status = EXCLUDED.status, total_amount = EXCLUDED.total_amount")
    @Insert(databaseId = "oracle", value = "MERGE INTO ordering.orders t"
        + " USING (SELECT #{orderId} AS order_id, #{customerId} AS customer_id, #{createdAt} AS created_at,"
        + " #{status} AS status, #{totalAmount} AS total_amount FROM dual) s ON (t.order_id = s.order_id)"
        + " WHEN MATCHED THEN UPDATE SET t.customer_id = s.customer_id, t.created_at = s.created_at,"
        + " t.status = s.status, t.total_amount = s.total_amount"
        + " WHEN NOT MATCHED THEN INSERT (" + COLUMNS + ")"
        + " VALUES (s.order_id, s.customer_id, s.created_at, s.status, s.total_amount)")
    int upsert(OrderingOrders orders);

    @Delete("DELETE FROM ordering.orders WHERE order_id = #{orderId}")
    int delete(@Param("orderId") String orderId);

    @Select("SELECT " + COLUMNS + " FROM ordering.orders FETCH FIRST #{limit} ROWS ONLY")
    List<OrderingOrders> selectAll(@Param("limit") int limit);

    @Select("SELECT " + COLUMNS + " FROM ordering.orders WHERE order_id = #{orderId} FETCH FIRST #{limit} ROWS ONLY")
    List<OrderingOrders> selectByPk(@Param("orderId") String orderId, @Param("limit") int limit);

    @Select("SELECT " + COLUMNS + " FROM ordering.orders WHERE customer_id = #{customerId}")
    List<OrderingOrders> selectByCustomerId(@Param("customerId") int customerId);

    @Update("TRUNCATE TABLE ordering.orders")
    void truncate();
}
