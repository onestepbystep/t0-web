package cn.cgnb.datasources;

import cn.cgnb.bean.IdNameInfo;
import cn.cgnb.bean.DBSourceBean;
import cn.cgnb.bean.DSBean;
import cn.cgnb.bean.SyncTableInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ah on 2018/5/9.
 */

@Repository
public class OracleClient {


    private final static Logger logger = LoggerFactory.getLogger(OracleClient.class);

    @Autowired
    DBTools dbTools;

    @Autowired
    @Qualifier("defaultJdbc")
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("oggjdbc")
    protected JdbcTemplate jdbcTemplate_ogg;

    @Autowired
    @Qualifier("mobilejdbc")
    protected JdbcTemplate jdbcTemplate_mobile;


    /**
     *
     * 在数据源表（t_data_source）中，查询所有数据库id 和数据源db 一一对应
     * @return
     */
    public Map<String,String> selectAllSiddb(){
        String sql = "select id,db_name from t_data_source";
        final Map<String,String> id_db = new HashMap<String,String>();
        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet resultSet) throws SQLException {
                id_db.put(resultSet.getString("id"),resultSet.getString("db_name"));
            }
        });




        return id_db;
    }


    /**
     * 数据源表操作  t_data_source
     * @param dbBean
     */
    public void insertDB(DBSourceBean dbBean){
        // 先查询数据源表中最大的id
        String sql ="select max(id) from t_data_source ";
        List<String> query = jdbcTemplate.query(sql, new Object[]{}, new SingleColumnRowMapper<String>());
        String id = query.get(0);
        // 如果表中数据，则id为 最大值+1 否则为1
        if(id!=null && !"".equals(id))
            id = String.valueOf(Integer.valueOf(id)+1);
        else
            id = "1";

        sql = "insert into t_data_source " +
                "values("+
                "'"+id+"',"+
                "'"+dbBean.getDb_name()+"'," +
                "'"+dbBean.getDb_user()+"'," +
                "'"+dbBean.getDb_pwd()+"'," +
                "'"+dbBean.getDb_url()+"'," +
                "'"+dbBean.getDb_type()+"')" ;

        jdbcTemplate.execute(sql);
        logger.info("插入数据源(t_data_source)成功");


    }



    /**
     * 根据 数据库id 查询数据库中所有表
     *
     * @param id_db
     * @return
     */
    public List<String> getTabs(String id_db){

        // 取connection-数据类型
        Map<Connection,String > ct = dbTools.getDbInfo(id_db);
        Map.Entry<Connection, String> next = ct.entrySet().iterator().next();
        Connection conn =next.getKey();
        String type = next.getValue();

        List<String> tabs = new ArrayList<String>();
        String sql = "";
        if("oracle".equals(type)){
            sql = "select table_name from user_tables order by table_name ";
        }
        // mysql
        if("mysql".equals(type)){
            try {
                String schema = conn.getSchema();
                sql ="select COLUMN_NAME from INFORMATION_SCHEMA.Columns where table_schema='"+schema+"'";
            } catch (SQLException e1) {
                e1.printStackTrace();
            }

        }

        logger.info("sql :"+sql);
        Statement stat = null;
        try {
            stat = conn.createStatement();
            ResultSet rs = stat.executeQuery(sql);
            while(rs.next())
                tabs.add(rs.getString(1));
        } catch (SQLException e1) {
            e1.printStackTrace();
        } finally {

            try {
                stat.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return tabs;

    }




    /**
     *
     * 在数据库中查询表信息
     * @param dsBean
     * @return
     */
    public List<String> getCols(DSBean dsBean) {
        //全部将表名转换成大写
        String tableName = dsBean.getSource_table();
        String id = dsBean.getSource_id();
        // 取connection-数据类型
        Map<Connection,String > ct = dbTools.getDbInfo(id);
        Map.Entry<Connection, String> next = ct.entrySet().iterator().next();
        Connection conn =next.getKey();
        String type = next.getValue();
        String schema = "";
        String sql="";
        List<String> query = new ArrayList<String>();
        // oracle
        if("oracle".equals(type))
                sql = "select  COLUMN_NAME from USER_TAB_COLS  WHERE TABLE_NAME = '"+tableName+"'";
        // mysql
        if("mysql".equals(type)){
            try {
                schema = conn.getSchema();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            sql ="select COLUMN_NAME from INFORMATION_SCHEMA.Columns where table_schema='"+schema+"' and table_name='"+tableName+"'";
        }



        logger.info("sql :"+sql);
        Statement stat = null;
        try {
            stat = conn.createStatement();
            ResultSet rs = stat.executeQuery(sql);
            while(rs.next())
                query.add(rs.getString(1));
        } catch (SQLException e1) {
            e1.printStackTrace();
        } finally {

        try {
            stat.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

        return query;
    }






    /**
     * 将topic相关信息存放到t_kafka_topic中
     *
     *
     *
     * @param topics_partitions
     */
    public Map<String,String> saveTopic(Map<String,Integer> topics_partitions){

        String sql ="";
        // 默认topic_id 从‘1’ 开始
        Map<String,String> topics = new HashMap<String,String>();

        for(Map.Entry<String,Integer> topic:topics_partitions.entrySet()){

            String id="";
            String topic_name = topic.getKey();
            Integer partitions = topic.getValue();
            String topic_business ="";
            // 先查找topic信息是否存在，不存在插入，存在更新
            sql = "select id from t_kafka_topic where topic_name=? ";
            List<String> rs = jdbcTemplate.query(sql,new Object[]{topic_name},new SingleColumnRowMapper<String>());

            // 有值更新
            if(!rs.isEmpty()){
                id = rs.get(0);
                sql ="update t_kafka_topic set topic_partition =?,topic_business=? where topic_name=?";
                jdbcTemplate.update(sql,partitions,topic_business,topic_name);
                logger.info("更新kafka主题表(t_kafka_topic)成功");
            }
            // 无值插入
            else{
                // 查询最大topic_id
                sql = "select max(id) from t_kafka_topic";
                List<String> rs1 = jdbcTemplate.query(sql,new Object[]{},new SingleColumnRowMapper<String>());
                id = rs1.get(0);
                if( id!=null && !"".equals( id))
                    id = String.valueOf(Integer.valueOf( id)+1);
                else
                    id = "1";
                sql = "insert into t_kafka_topic values(" +
                        "'"+id+"'," +
                        "'"+topic_name+"'," +
                        partitions+"," +
                        "'"+topic_business+"')";

                jdbcTemplate.update(sql);
                logger.info("##########################插入主题表##########################");
            }

            topics.put(id,topic_name);
        }
        return topics;

    }



    /**
     * 将页面相关配置信息插入到配置表 t_sync_table_info
     * @param sti
     * @return
     */
    public int insertConf(SyncTableInfo sti){

        String sql ="insert into t_sync_table_info(" +
            "id," +
            "source_id," +
            "source_table," +
            "target_id," +
            "target_table," +
            "topic_id," +
            "table_columns," +
            "is_all_columns," +
            "standarded_column," +
            "is_released," +
            "create_time," +
            "release_time) " +
            "values(" +
            "id_seq.Nextval," +
            "'" +sti.getSource_id()+"',"+
            "'" +sti.getSource_table()+"',"+
            "'" +sti.getTarget_id()+"',"+
            "'" +sti.getTarget_table()+"',"+
            "'" +sti.getTopic_id()+"',"+
            "'" +sti.getTable_columns()+"',"+
            "'" +sti.getIs_all_columns()+"',"+
            "'" +sti.getStandarded_column()+"',"+
            "'" +sti.getIs_released()+"',"+
            "'" +sti.getCreate_time()+"',"+
            "'" +sti.getRelease_time()+"')";
        logger.info("插入语句:"+sql);

        jdbcTemplate.execute(sql);
        logger.info("##########################插入配置表##########################");
        return 0;

    }

    /**
     * 将页面相关配置信息 更新到配置表
     * @param sti
     * @return
     */
    public int updateConf(SyncTableInfo sti){

        String sql ="update t_sync_table_info set "+
                "target_id=? , " +
                "table_columns=? , " +
                "is_all_columns=? , " +
                "standarded_column=? , " +
                "is_released=? , " +
                "create_time=? , " +
                "release_time=?  " +
                "where source_table= '"+sti.getSource_table()+"' and " +
                "target_table ='"+sti.getTarget_table()+"' and " +
                "topic_id='"+sti.getTopic_id()+"'";
        String target_id = sti.getTarget_id();
        String table_columns = sti.getTable_columns();
        String is_all_columns = sti.getIs_all_columns();
        String standarded_column = sti.getStandarded_column();
        String is_released = sti.getIs_released();
        String create_time = sti.getCreate_time();
        String release_time = sti.getRelease_time();
        System.out.println("更新语句  "+sql);
        jdbcTemplate.update(sql,new Object[]{target_id,table_columns,
                is_all_columns,standarded_column,is_released,create_time,release_time});

        logger.info("##########################更新配置表##########################");
        return 0;

    }


    /**
     * 根据source_table, target_table, topic_id 3个字段确定唯一性， 查询数据库
     * 返回 is_released 是否注册字段
     * @param sti
     * @return
     */
    public String  selectConf(SyncTableInfo sti){
        String source_table = sti.getSource_table();
        String target_table = sti.getTarget_table();
        String topic_id = sti.getTopic_id();

        // 根据唯一性 取最新一条数据
        String sql = "select " +
                "is_released " +
                "from " +
                "(select is_released," +
                "row_number() over(partition by source_table,target_table,topic_id order by release_time desc) as num " +
                " from t_sync_table_info  " +
                "where source_table=? and target_table=? and  topic_id=?  ) t1 " +
                "where t1.num=1";
       logger.info("查询sql "+sql);
        List<String> datas = jdbcTemplate.query(sql, new Object[]{source_table, target_table, topic_id}, new SingleColumnRowMapper<String>());
        // 默认是否注册为null
        String is_released = "";
        if(!datas.isEmpty())
            is_released =datas.get(0);

        return is_released;

    }


}
