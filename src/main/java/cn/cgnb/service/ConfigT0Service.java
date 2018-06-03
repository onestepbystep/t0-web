package cn.cgnb.service;


import cn.cgnb.bean.DBSourceBean;
import cn.cgnb.bean.DSBean;
import cn.cgnb.bean.IdNameInfo;
import cn.cgnb.bean.SyncTableInfo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;


/**
 * Created by ah on 2018/5/7.
 */
public interface ConfigT0Service {


    /**
     * 根据表名，查找表的字段
     * @return
     */
    public List<String> getTabCols(DSBean dsBean);



    /**
     * 保存配置信息到数据库
     * @param sti
     */
    public void saveConf(SyncTableInfo sti);

    /**
     * 查找集群中所有topic
     * @return
     */
    public Map<String,String> getAllTopics();

    /**
     * 查询所有数据源信息
     *
     */
    public Map<String,String>  getAllSids();

    /**
     * 保存数据库信息
     * @param dbBean
     */
    public void savaDB(DBSourceBean dbBean) ;

    public List<String> getTabls(String db_id);



}
