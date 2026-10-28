package com.example.demo_jdbc_samples.repository.mybatis;

import com.example.demo_jdbc_samples.model.CustomerCustomers;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * customer.customers with MyBatis annotations. The columns are mapped to the model by
 * map-underscore-to-camel-case.
 */
@Mapper
public interface CustomerCustomersSqlMapper {
    String COLUMNS = "customer_id, name, credit_limit, credit_total";

    @Select("SELECT " + COLUMNS + " FROM customer.customers WHERE customer_id = #{customerId}")
    CustomerCustomers select(@Param("customerId") int customerId);

    @Select("SELECT " + COLUMNS + " FROM customer.customers WHERE customer_id = #{customerId} FOR UPDATE")
    CustomerCustomers selectForUpdate(@Param("customerId") int customerId);

    @Insert("INSERT INTO customer.customers (" + COLUMNS + ")"
        + " VALUES (#{customerId}, #{name}, #{creditLimit}, #{creditTotal})")
    int insert(CustomerCustomers customers);

    @Update("UPDATE customer.customers SET name = #{name}, credit_limit = #{creditLimit}, credit_total = #{creditTotal}"
        + " WHERE customer_id = #{customerId}")
    int update(CustomerCustomers customers);


    @Delete("DELETE FROM customer.customers WHERE customer_id = #{customerId}")
    int delete(@Param("customerId") int customerId);

    @Select("SELECT " + COLUMNS + " FROM customer.customers FETCH FIRST #{limit} ROWS ONLY")
    List<CustomerCustomers> selectAll(@Param("limit") int limit);

    @Select("SELECT " + COLUMNS + " FROM customer.customers WHERE customer_id = #{customerId} FETCH FIRST #{limit} ROWS ONLY")
    List<CustomerCustomers> selectByPk(@Param("customerId") int customerId, @Param("limit") int limit);

    // exact: name = #{value}, prefix: name LIKE #{value} (the caller adds '%'). limit 0 = no limit
    @Select({"<script>",
        "SELECT " + COLUMNS + " FROM customer.customers",
        "<choose><when test='prefix'>WHERE name LIKE #{value}</when><otherwise>WHERE name = #{value}</otherwise></choose>",
        "<if test='limit &gt; 0'>FETCH FIRST #{limit} ROWS ONLY</if>",
        "</script>"})
    List<CustomerCustomers> searchByName(@Param("value") String value, @Param("prefix") boolean prefix, @Param("limit") int limit);

    @Select("SELECT " + COLUMNS + " FROM customer.customers")
    List<CustomerCustomers> selectEveryRow();

    // DELETE, not TRUNCATE: ScalarDB Native JDBC refuses TRUNCATE on a ScalarDB table
    @Delete("DELETE FROM customer.customers")
    void truncate();
}
