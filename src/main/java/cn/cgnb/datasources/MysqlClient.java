package cn.cgnb.datasources;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by ah on 2018/5/7.
 */


@Repository
public class MysqlClient {


    @Autowired
    @Qualifier("mobilejdbc")
    protected JdbcTemplate jdbcTemplate1;


    public List<String> getCols(String sql,String tableName) {
        List<String> query = jdbcTemplate1.query(sql, new Object[]{tableName}, new SingleColumnRowMapper<String>());

        return query;

    }



}
