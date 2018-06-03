package cn.cgnb.service.impl;

import cn.cgnb.bean.DBSourceBean;
import cn.cgnb.bean.DSBean;
import cn.cgnb.bean.IdNameInfo;
import cn.cgnb.bean.SyncTableInfo;
import cn.cgnb.datasources.OracleClient;
import cn.cgnb.service.ConfigT0Service;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by ah on 2018/5/7.
 */

@Service
public class ConfigT0ServiceImpl implements ConfigT0Service{

    @Value("${zkUrl}")
    private String zkUrl;

    @Autowired
    private OracleClient oracleClient;

    private SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMdd HH:mm:ss");
    private final static Logger logger = LoggerFactory.getLogger(ConfigT0ServiceImpl.class);

    /**
     * 1、获取kafka中topic
     * 2、topic相关信息存放到 t_kafka_topic
     *  返回  topic_id -> topic_name
     * @return
     */
    public  Map<String,String> getAllTopics(){
        ZkUtils zkutils = ZkUtils.apply(zkUrl,30000,30000, JaasUtils.isZkSecurityEnabled());
        // 存放所有的topics
        Map<String,String> topics = new HashMap<String,String>();
        // 存放topic_name 和topic_partition 分区数量
        Map<String,Integer> topics_partitions = new HashMap<>();


        scala.collection.Set<TopicAndPartition> allPartitions = zkutils.getAllPartitions();
        Iterator<TopicAndPartition> partitions = allPartitions.iterator();
        while(partitions.hasNext()){
            TopicAndPartition tp = partitions.next();
            String topic= tp.topic();
            // 去除  --from-beginning和__consumer_offsets
            if("__consumer_offsets".equals(topic) || "--from-beginning".equals(topic)
                    ||"_offsets".equals(topic) )
                continue;

            // 分区是从0 开始  所以每个topic 分区数为 分区+1
            topics_partitions.put(topic,tp.partition()+1);
        }

        // 同时将topic信息存放到t_kafka_topic表 同时返回id——name一一对应的map
        topics = oracleClient.saveTopic(topics_partitions);

        return topics;
    }

    /**
     * 得到所有数据源id
     *
     * @return
     */
    @Override
    public Map<String,String>  getAllSids() {

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
     * 根据数据源的source_table,target_table,topic_name 确定唯一性，
     *
     *
     * 数据不存在  插入
     * 数据存在且未注册，更新
     * 数据存在且已注册，插入
     *
     *
     * @param sti
     */
    @Override
    public void saveConf(SyncTableInfo sti) {
        // 查看是否全选
        Boolean is_all_column = sti.getIs_all_column();
        if(is_all_column)  // 如果true  则表示全选 1  否则 0
            sti.setIs_all_columns("1");
        else
            sti.setIs_all_columns("0");

        String create_time = sdf.format(new Date());
        String release_time="";
        // 判断是否注册，如果注册，注册时间为create_time  否则为空
        if(sti.getIs_released()=="1")
            release_time = create_time;

        sti.setCreate_time(create_time);
        sti.setRelease_time(release_time);

        // 根据source_table,target_table,topic_name 查找数据

        String is_released  = oracleClient.selectConf(sti);
        // 未找到数据 或者 数据已经注册 则插入
        if("".equals(is_released) || "1".equals(is_released))
            oracleClient.insertConf(sti);
        else
            oracleClient.updateConf(sti);

    }

    @Override
    public List<String> getTabls(String db_id) {
       List<String> tabs =  oracleClient.getTabs(db_id);
        return tabs;
    }
}
