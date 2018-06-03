package cn.cgnb.bean;

import java.util.Map;

/**
 * Created by ah on 2018/5/9.
 */
public class SyncTableInfo {

    private int id;
    private String source_id;   //数据源id
    private String source_table;    //源表名
    private String target_id;   //目标库id
    private String target_table;    //目标表名
    private String topic_id;      //topic_id
    private Map<String,String> topic_id_name;   // 数据id->库名
    private String table_columns;    //同步的字段(逗号分隔)
    private String all_columns;    //同步的字段(逗号分隔)
    private boolean is_all_column;   // 是否全选 true  不全选 false
    private String is_all_columns;   // 是否全选 1 全选  0 不全选
    private String standarded_column; // 需要标准化的字段 如：col1:138, col2:901
    private String is_released;  //是否发布  1 发布  0 不发布
    private String create_time; // 创建时间  YYYY-MM-DD hh24miss
    private String release_time;  //发布时间
    private String create_user; // 创建人
    private String release_user;  //发布人

    public String getCreate_user() {
        return create_user;
    }

    public void setCreate_user(String create_user) {
        this.create_user = create_user;
    }

    public String getRelease_user() {
        return release_user;
    }

    public void setRelease_user(String release_user) {
        this.release_user = release_user;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSource_id() {
        return source_id;
    }

    public void setSource_id(String source_id) {
        this.source_id = source_id;
    }



    public String getSource_table() {
        return source_table;
    }

    public void setSource_table(String source_table) {
        this.source_table = source_table;
    }

    public String getTarget_id() {
        return target_id;
    }

    public void setTarget_id(String target_id) {
        this.target_id = target_id;
    }

    public String getTarget_table() {
        return target_table;
    }

    public void setTarget_table(String target_table) {
        this.target_table = target_table;
    }



    public String getTable_columns() {
        return table_columns;
    }

    public void setTable_columns(String table_columns) {
        this.table_columns = table_columns;
    }



    public String getRelease_time() {
        return release_time;
    }

    public void setRelease_time(String release_time) {
        this.release_time = release_time;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getIs_released() {
        return is_released;
    }

    public void setIs_released(String is_released) {
        this.is_released = is_released;
    }

    public String getStandarded_column() {
        return standarded_column;
    }

    public void setStandarded_column(String standarded_column) {
        this.standarded_column = standarded_column;
    }




    @Override
    public String toString() {
        return "id:"+id+"   source_id:"+source_id+"   source_table:"+source_table
                +"   target_id:"+target_id+"   target_table:"+target_table
                +"    topic_id:"+topic_id+"  topic_id_name:"+topic_id_name+"  all_columns:"+all_columns
                +"  table_columns:"+table_columns+"  is_all_columns:"+is_all_columns+"  standarded_column:"+standarded_column
                +"  is_released:"+is_released+"  create_time:"+create_time+"  release_time:"+release_time
                +"  create_user:"+create_user+"  release_user:"+release_user;
    }

    public boolean isIs_all_column() {
        return is_all_column;
    }

    public void setIs_all_column(boolean is_all_column) {
        this.is_all_column = is_all_column;
    }

    public Boolean getIs_all_column() {
        return is_all_column ;
    }

    public void setIs_all_columns(String is_all_columns) {
        this.is_all_columns = is_all_columns;
    }

    public String getIs_all_columns() {
        return is_all_columns ;
    }


    public void setTopic_id_name(Map<String, String> topic_id_name) {
        this.topic_id_name = topic_id_name;
    }

    public Map<String ,String> getTopic_id_name(){
        return  topic_id_name;
    }

    public void setTopic_id(String topic_id) {
        this.topic_id = topic_id;
    }

    public String getTopic_id(){
        return  topic_id;
    }

    public String getAll_columns() {
        return all_columns;
    }

    public void setAll_columns(String all_columns) {
        this.all_columns = all_columns;
    }
}
