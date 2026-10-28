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
 * map-underscore-to-camel-case.
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


    @Delete("DELETE FROM ordering.orders WHERE order_id = #{orderId}")
    int delete(@Param("orderId") String orderId);

    @Select("SELECT " + COLUMNS + " FROM ordering.orders FETCH FIRST #{limit} ROWS ONLY")
    List<OrderingOrders> selectAll(@Param("limit") int limit);

    @Select("SELECT " + COLUMNS + " FROM ordering.orders WHERE order_id = #{orderId} FETCH FIRST #{limit} ROWS ONLY")
    List<OrderingOrders> selectByPk(@Param("orderId") String orderId, @Param("limit") int limit);

    @Select("SELECT " + COLUMNS + " FROM ordering.orders WHERE customer_id = #{customerId}")
    List<OrderingOrders> selectByCustomerId(@Param("customerId") int customerId);

    // DELETE, not TRUNCATE: ScalarDB Native JDBC refuses TRUNCATE on a ScalarDB table
    @Delete("DELETE FROM ordering.orders")
    void truncate();
}
