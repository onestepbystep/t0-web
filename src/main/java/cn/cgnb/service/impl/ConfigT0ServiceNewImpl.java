package cn.cgnb.service.impl;

import cn.cgnb.bean.DBSourceBean;
import cn.cgnb.bean.DSBean;
import cn.cgnb.bean.IdNameInfo;
import cn.cgnb.bean.SyncTableInfo;
import cn.cgnb.conf.PageParam;
import cn.cgnb.conf.PageResult;
import cn.cgnb.datasources.OracleClient;
import cn.cgnb.datasources.OracleClientNew;
import cn.cgnb.service.ConfigT0Service;
import cn.cgnb.service.ConfigT0ServiceNew;
import kafka.common.TopicAndPartition;
import kafka.utils.ZkUtils;
import org.apache.kafka.common.security.JaasUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import scala.collection.Iterator;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created by ah on 2018/5/7.
 */

@Service
public class ConfigT0ServiceNewImpl implements ConfigT0ServiceNew{

    @Value("${zkUrl}")
    private String zkUrl;

    @Autowired
    private OracleClientNew oracleClient;

    private SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
    private final static Logger logger = LoggerFactory.getLogger(ConfigT0ServiceNewImpl.class);

    /**
     * 1、获取kafka中topic
     * 2、topic相关信息存放到 t_kafka_topic
     *  返回  topic_id -> topic_name
     * @return
     */
    public  List<IdNameInfo> getAllTopics(boolean flag){
        // 存放所有的topics
        List<IdNameInfo> topics = new ArrayList<IdNameInfo>();
        // 存放topic_name 和topic_partition 分区数量
        Map<String,Integer> topics_partitions = new HashMap<>();
        if(flag){
            ZkUtils zkutils = ZkUtils.apply(zkUrl,30000,30000, JaasUtils.isZkSecurityEnabled());

            scala.collection.Set<TopicAndPartition> allPartitions = zkutils.getAllPartitions();
            Iterator<TopicAndPartition> partitions = allPartitions.iterator();
            while(partitions.hasNext()){
                TopicAndPartition tp = partitions.next();
                String topic= tp.topic();
                // 去除  --from-beginning和__consumer_offsets
                if("__consumer_offsets".equals(topic) || "--from-beginning".equals(topic)
                        ||"offsets".equals(topic) )
                    continue;

                // 分区是从0 开始  所以每个topic 分区数为 分区+1
                topics_partitions.put(topic,tp.partition()+1);
            }

            // 同时将topic信息存放到t_kafka_topic表 同时返回id——name一一对应的map
            topics = oracleClient.saveTopic(topics_partitions);
        }
      else{
            topics = oracleClient.selectTopics();
        }

        return topics;
    }

    /**
     * 得到所有数据源id
     *
     * @return
     */
    @Override
    public List<IdNameInfo> getAllSids() {

        return oracleClient.selectAllSiddb();
    }


    /**
     * 数据源操作
     * @param dbBean
     */
    @Override
    public void savaDB(DBSourceBean dbBean) {


        oracleClient.insertDB(dbBean);
    }

    /**
     * 根据表名，查找表的字段
     * @return
     */
    @Override
    public List<String> getTabCols(DSBean dsBean) {
        List<String> cols = oracleClient.getCols(dsBean);
        return cols;
    }






    /**
     * 根据数据库 查询所有表
     * @param db_id
     * @return
     */
    @Override
    public List<String> getTabls(String db_id) {
       List<String> tabs =  oracleClient.getTabs(db_id);
        return tabs;
    }



    /**
     * 分页查询信息
     * @param src_id
     * @param src_table_name
     * @param tag_id
     * @param tag_table_name
     * @param topic
     * @param is_released
     * @param parm
     * @return
     */
    @Override
    public PageResult<SyncTableInfo> querySyncInfos(String src_id, String src_table_name
            , String tag_id , String tag_table_name, String topic , String  is_released, PageParam parm){
        PageResult<SyncTableInfo> result =  oracleClient.querySyncInfos( src_id,  src_table_name
                , tag_id ,  tag_table_name,   topic,is_released,parm);

        return result;
    }

    /**
     * 查询 dim_src2std_desc中system_id
     *
     * @return
     */
    @Override
    public List<String> queryStdSys(){
       return  oracleClient.queryStdSys();
    }

    public List<String> queryStds(String system_id){
        return oracleClient.queryStds(system_id);
    }

    /**
     * 保存 更新为 注册
     * @param id
     */
    @Override
    public void updateSaveSync(String id) {
        String release_time =  sdf.format(new Date());
        oracleClient.updateSaveSync(id,release_time);

    }

    /**
     *
     * 根据模板中的数据 ，将保存或者注册的信息插入到配置表
     *
     * @param sti
     */
    @Override
    public void saveConf(SyncTableInfo sti) {
        String create_time = sdf.format(new Date());
        String release_time="";
        // 判断是否注册，如果注册，注册时间为create_time  否则为空
        if(sti.getIs_released()=="1"){
            release_time = create_time;
            sti.setRelease_user("admin");
        }

        sti.setCreate_time(create_time);
        sti.setRelease_time(release_time);
        sti.setCreate_user("admin");


        oracleClient.insertConf(sti);
    }

    /**
     * 进入编辑页面
     * @param id
     * @return
     */
    public SyncTableInfo editSyncInfo(String id){
        SyncTableInfo sti =  oracleClient.editSyncInfo(id);
        return  sti;
    }
}
