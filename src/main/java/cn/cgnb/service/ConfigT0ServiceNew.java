package cn.cgnb.service;


import cn.cgnb.bean.DBSourceBean;
import cn.cgnb.bean.DSBean;
import cn.cgnb.bean.IdNameInfo;
import cn.cgnb.bean.SyncTableInfo;
import cn.cgnb.conf.PageParam;
import cn.cgnb.conf.PageResult;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;


/**
 * Created by ah on 2018/5/7.
 */
public interface ConfigT0ServiceNew {


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
     * @falg true 先查询kafka后入库 flase 直接查询数据库
     * @return
     */
    public  List<IdNameInfo> getAllTopics(boolean flag);

    /**
     * 查询所有数据源信息
     *
     */
    public List<IdNameInfo> getAllSids();

    /**
     * 保存数据库信息
     * @param dbBean
     */
    public void savaDB(DBSourceBean dbBean) ;

    /**
     * 根据数据库 查询数据库中表
     * @param db_id
     * @return
     */
    public List<String> getTabls(String db_id);

    /**
     * 查询 dim_src2std_desc中system_id
     *
     * @return
     */
    public List<String> queryStdSys();

    /**
     * 根据 system_id查询 dim_src2std_desc中std_id
     *
     * @return
     */
    public List<String> queryStds(String system_id);

    /**
     *
     * 根据查询条件 查找保存和发布数据 分页
     * @param src_id
     * @param src_table_name
     * @param tag_id
     * @param tag_table_name
     * @param topic
     * @param is_released
     * @param parm
     * @return
     */
    public PageResult<SyncTableInfo> querySyncInfos(String src_id, String src_table_name
            , String tag_id , String tag_table_name, String topic , String is_released, PageParam parm);


    /**
     * 保存 更新为 注册
     * @param id
     */
    public void updateSaveSync(String id);

    /**
     * 进入编辑页面
     * @param id
     * @return
     */
    public SyncTableInfo editSyncInfo(String id);
}
