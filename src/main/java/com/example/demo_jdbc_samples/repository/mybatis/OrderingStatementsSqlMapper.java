package com.example.demo_jdbc_samples.repository.mybatis;

import com.example.demo_jdbc_samples.model.OrderingStatements;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * ordering.statements with MyBatis annotations (primary key order_id, line_no). The columns
 * are mapped to the model by map-underscore-to-camel-case.
 */
@Mapper
public interface OrderingStatementsSqlMapper {
    String COLUMNS = "order_id, line_no, item_id, qty, unit_price";

    @Select("SELECT " + COLUMNS + " FROM ordering.statements WHERE order_id = #{orderId} AND line_no = #{lineNo}")
    OrderingStatements select(@Param("orderId") String orderId, @Param("lineNo") int lineNo);

    @Insert("INSERT INTO ordering.statements (" + COLUMNS + ")"
        + " VALUES (#{orderId}, #{lineNo}, #{itemId}, #{qty}, #{unitPrice})")
    int insert(OrderingStatements statements);

    @Update("UPDATE ordering.statements SET item_id = #{itemId}, qty = #{qty}, unit_price = #{unitPrice}"
        + " WHERE order_id = #{orderId} AND line_no = #{lineNo}")
    int update(OrderingStatements statements);


    @Delete("DELETE FROM ordering.statements WHERE order_id = #{orderId} AND line_no = #{lineNo}")
    int delete(@Param("orderId") String orderId, @Param("lineNo") int lineNo);

    @Select("SELECT " + COLUMNS + " FROM ordering.statements FETCH FIRST #{limit} ROWS ONLY")
    List<OrderingStatements> selectAll(@Param("limit") int limit);

    // In line_no order, as the ScalarDB clustering key
    @Select("SELECT " + COLUMNS + " FROM ordering.statements WHERE order_id = #{orderId} ORDER BY line_no"
        + " FETCH FIRST #{limit} ROWS ONLY")
    List<OrderingStatements> selectByOrderId(@Param("orderId") String orderId, @Param("limit") int limit);

    // DELETE, not TRUNCATE: ScalarDB Native JDBC refuses TRUNCATE on a ScalarDB table
    @Delete("DELETE FROM ordering.statements")
    void truncate();
}
