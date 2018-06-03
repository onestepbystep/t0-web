package cn.cgnb.controller;


import cn.cgnb.bean.DBSourceBean;
import cn.cgnb.bean.DSBean;
import cn.cgnb.bean.IdNameInfo;
import cn.cgnb.bean.SyncTableInfo;
import cn.cgnb.conf.PageParam;
import cn.cgnb.conf.PageResult;
import cn.cgnb.service.ConfigT0ServiceNew;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ah on 2018/5/4.
 */
@RestController
public class ConfigT0Controller extends BaseController{


    @Autowired
    private ConfigT0ServiceNew service;
    private static final Logger logger = LoggerFactory.getLogger(ConfigT0Controller.class);
    private DSBean dsBean = new DSBean();
    private SyncTableInfo sti = new SyncTableInfo();
    private DBSourceBean dbBean = new DBSourceBean();
    // 存放 id和db关系
    private List<IdNameInfo>id_db  = new ArrayList<IdNameInfo>();


    /**
     *
     * 查询数据库信息
     *
     * db_id - db_name
     *
     * @return
     */
    @RequestMapping(value="dbs")
    public String getDBInfo(){
        id_db = service.getAllSids();
        return returnData(200,id_db);

    }

    /**
     * 根据数据库 查找数据库表
     * @param src_id
     * @return
     */
    @RequestMapping(value="srcTbs")
    public String querySrcTables(String src_id ){

       List<String> tabs =  service.getTabls(src_id);
        return returnData(200,tabs);
    }

    /**
     * 根据数据库 查找数据库表
     * @param tag_id
     * @return
     */
    @RequestMapping(value="tagTbs")
    public String queryTagTables(String tag_id ){
        List<String> tabs  = service.getTabls(tag_id);
        return returnData(200,tabs);
    }

    /**
     *
     * 查询kafka topic
     * @return
     */
    @RequestMapping(value="topics")
    public String getTopics(){
        //
        List<IdNameInfo> topics = service.getAllTopics(false);
        return returnData(200,topics);
    }

    /**
     * 查询配置表中保存的数据
     * @param src_id
     * @param src_table_name
     * @param tag_id
     * @param tag_table_name
     * @param topic
     * @param is_released
     * @param pageNum
     * @param pageSize
     * @return
     */

    @RequestMapping(value="querySave")
    public String querySaveSyncInfos(String src_id, String src_table_name
            ,String tag_id , String tag_table_name,  String topic ,String is_released ,int pageNum,int pageSize){

        PageResult<SyncTableInfo>  result = service.querySyncInfos( src_id,  src_table_name
                , tag_id ,  tag_table_name,   topic,is_released,new PageParam(pageNum,pageSize));
        return  returnPage(200,result.getRows(),pageNum,pageSize,result.getTotal());
    }

    /**
     * 查询配置表注册的数据
     * @param src_id
     * @param src_table_name
     * @param tag_id
     * @param tag_table_name
     * @param topic
     * @param is_released
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value="queryRelease")
    public String queryReleaseSyncInfos(String src_id, String src_table_name
            ,String tag_id , String tag_table_name,  String topic ,String is_released,int pageNum,int pageSize){
        PageResult<SyncTableInfo>  result = service.querySyncInfos( src_id,  src_table_name
                , tag_id ,  tag_table_name,   topic,is_released,new PageParam(pageNum,pageSize));
        return  returnPage(200,result.getRows(),pageNum,pageSize,result.getTotal());
    }

    /**
     * 保存数据源信息
     * @param db_Name
     * @param db_user
     * @param db_pwd
     * @param db_url
     * @param db_type
     * @return
     */
    @RequestMapping(value="savaDB")
    public String savaDB(String db_Name,String db_user,String db_pwd,String db_url,String db_type){
        DBSourceBean db = new DBSourceBean();
        db.setDb_name(db_Name);
        db.setDb_user(db_user);
        db.setDb_pwd(db_pwd);
        db.setDb_url(db_url);
        db.setDb_type(db_type);
        service.savaDB(db);
        return returnMsg(200,"数据源操作成功",true);

    }

    /**
     * 根据数据库表信息查询字段
     * @param src_id
     * @param src_table_name
     * @return
     */
    @RequestMapping(value="queryCols")
    public String queryTabCols(String src_id, String src_table_name){
        DSBean db = new DSBean();
        db.setSource_id(src_id);
        db.setSource_table(src_table_name);
        List<String> cols = service.getTabCols(db);
        return returnData(200,cols);
    }

    /**
     * 查询 dim_src2std_desc中system_id
     *
     * @return
     */
    @RequestMapping(value="queryStdSys")
    public String queryStdSys(){
        List<String> syss= service.queryStdSys();

        return returnData(200,syss);

    }


    /**
     * 根据 system_id查询 dim_src2std_desc中std_id
     *
     * @return
     */
    @RequestMapping(value="queryStds")
    public String queryStds(String system_id ){
        List<String> stds= service.queryStds(system_id);
        return returnData(200,stds);

    }


    /**
     * 根据模板中的数据 ，将保存或者注册的信息插入到配置表
     * @param src_id
     * @param src_table_name
     * @param tag_id
     * @param tag_table_name
     * @param topic_id
     * @param is_released
     * @param is_all_columns
     * @param standarded_column
     * @param table_columns
     * @param create_time
     * @param create_user
     * @param release_time
     * @param release_user
     * @return
     */
    @RequestMapping(value="saveSync")
    public String SaveSyncInfos(String src_id, String src_table_name
            ,String tag_id , String tag_table_name,  String topic_id ,String is_released ,String is_all_columns
    ,String standarded_column,String table_columns,String create_time,String create_user,
                                String release_time,String release_user){

        SyncTableInfo sti = new SyncTableInfo();
        service.saveConf(sti);
        return returnMsg(200,"数据配置操作成功",true);
    }

    /**
     * 更新保存为 注册
     * @param id
     * @return
     */

    @RequestMapping(value="updateRelease")
    public String updateSaveSync(String id) {
        service.updateSaveSync(id);
        return returnMsg(200,"更新保存到注册",true);
    }

    /**
     * 进入编辑页面
     * @param id
     * @return
     */
    @RequestMapping(value="editSync")
    public String editSyncInfo(String id){
        SyncTableInfo sti = service.editSyncInfo(id);

        return returnData(200,sti);
    }



}
