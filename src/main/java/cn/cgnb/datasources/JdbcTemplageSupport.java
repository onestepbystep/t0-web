package cn.cgnb.datasources;

import cn.cgnb.conf.PageParam;
import cn.cgnb.conf.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  分页
 * Created by ah on 2018/5/31.
 */


public class JdbcTemplageSupport extends JdbcTemplate{



    /**
     *
     * @param sql
     *            查询语句
     * @param sqlArgs
     *            查询参数Object数组,顺序需要与sql中的?一一对应
     * @param mappedClass
     *            查询的结果返回的封装CLASS
     * @param pageParam
     *            page的参数，包括pageIndex,pageSize
     * @return
     */
    public <SyncTableInfo> PageResult<SyncTableInfo> pagedQuery(JdbcTemplate jdbcTemplate,String sql, Object[] sqlArgs,
                                        Class<SyncTableInfo> mappedClass, PageParam pageParam) {
        String countSql = this.buildCountSql(sql);
        // 查询总条数
        int totalCount = jdbcTemplate.queryForObject(countSql,
                Integer.class, sqlArgs);
        String allSql = this.buildDataSql(sql, pageParam);
        // 查询数据
        RowMapper<SyncTableInfo> rowMapper = BeanPropertyRowMapper.newInstance(mappedClass);
        List<SyncTableInfo> list = jdbcTemplate.query(allSql, sqlArgs,
                rowMapper);
        // 组装PageResult
        PageResult<SyncTableInfo> page = new PageResult<SyncTableInfo>(totalCount, list);
        return page;
    }



    /**
     * 去除SELECT *语句，便于SELECT count(*)
     */
    private String removeSelect(String sql) {
        int beginPosition = sql.toLowerCase().indexOf("from");
        return sql.substring(beginPosition);
    }

    /**
     * 去除order by 提高select count(*)的速度
     */
    private String removeOrders(String sql) {
        Pattern p = Pattern.compile("order\\s*by[\\w|\\W|\\s|\\S]*",
                Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(sql);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, "");
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     * 拼接查询表中总数的sql
     * @param sql
     * @return
     */
    private String buildCountSql(String sql) {
        StringBuffer countSql = new StringBuffer();
        countSql.append("SELECT COUNT(*) ");
        countSql.append(this.removeOrders(this.removeSelect(sql)));
        return countSql.toString();
    }

    /**
     * 分页查询表中数据
     * @param sql
     * @param pageParam
     * @return
     */
    private String buildDataSql(String sql, PageParam pageParam) {
        StringBuffer dataSql = new StringBuffer();
        dataSql.append("SELECT");
        dataSql.append("    *");
        dataSql.append("FROM");
        dataSql.append("    (");
        dataSql.append("        SELECT");
        dataSql.append("            temp.*,");
        dataSql.append("            ROWNUM num");
        dataSql.append("        FROM");
        dataSql.append("            (").append(sql).append(") temp");
        dataSql.append("        WHERE");
        dataSql.append("            ROWNUM <= ").append(pageParam.getEndIndex());
        dataSql.append("    ) WHERE　num > ").append(pageParam.getStartIndex());
        return dataSql.toString();
    }



}
