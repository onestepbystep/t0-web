package cn.cgnb.datasources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ah on 2018/5/7.
 */

@Repository
public class DBTools {

    @Autowired
    @Qualifier("defaultJdbc")
    protected JdbcTemplate jdbcTemplate;


    public  Connection dbConn(String driver,String url,String name, String pass) {
        Connection c = null;
        try {
            Class.forName(driver);
            // 要是导入驱动没有成功的话都是会出现classnotfoundException.自己看看是不是哪里错了,例如classpath这些设置
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            c = DriverManager.getConnection(
                    url, name, pass);



        } catch (SQLException e) {
            e.printStackTrace();
        }
        return c;
    }

    public  Map<Connection,String> getDbInfo(String db_id){

        // 根据id 在t_data_source 表中查找相应数据源信息
        String sql = "select * from t_data_source where id =?";
        Map<String,Object> db = new HashMap<String,Object>();
        db = jdbcTemplate.queryForMap(sql,new Object[]{db_id});

        List<String> query = new ArrayList<String>();
        String url = (String)db.get("db_url");
        String db_name = (String)db.get("db_name");
        String user = (String)db.get("db_user");
        String pwd = (String)db.get("db_pwd");
        String type = (String)db.get("db_type");

        // 默认driver 为oracle
        String driver = "oracle.jdbc.driver.OracleDriver";
        if("mysql".equals(type)) {
            driver = "com.mysql.jdbc.Driver";


        }
        Connection conn = dbConn(driver, url, user, pwd);
        Map<Connection,String> ct = new HashMap<Connection,String>();
        ct.put(conn,type);
        return ct;

    }



    public static void main(String[] args) throws Exception {


       Connection con = new DBTools().dbConn("com.mysql.jdbc.Driver","jdbc:mysql://master:3306/hive?useSSL=false","hive", "hive");
        if (con == null) {
            System.out.print("连接失败2");
            System.exit(0);
        }
        Statement sql = con.createStatement();
       ResultSet rs = sql.executeQuery("select COLUMN_NAME from INFORMATION_SCHEMA.Columns where table_schema='test'  and table_name='people'  ");
        while(rs.next())
            System.out.println(rs.getString(1));


    }


}
