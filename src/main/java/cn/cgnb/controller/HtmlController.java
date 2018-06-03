package cn.cgnb.controller;

import cn.cgnb.bean.DBSourceBean;
import cn.cgnb.bean.DSBean;
import cn.cgnb.bean.SyncTableInfo;
import cn.cgnb.service.ConfigT0Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ah on 2018/5/4.
 */
@Controller
public class HtmlController {


    @Autowired
    private ConfigT0Service service;
    private static final Logger logger = LoggerFactory.getLogger(HtmlController.class);
    private DSBean dsBean = new DSBean();
    private SyncTableInfo sti = new SyncTableInfo();
    private DBSourceBean dbBean = new DBSourceBean();
    // 存放 id和db关系
    private Map<String,String> id_db  = new HashMap<>();



    @RequestMapping("/")
    public String query(ModelMap modelMap) {
        logger.info("*****************进入初始化页面*******************");
        id_db = service.getAllSids();
        modelMap.addAttribute("dsBean",dsBean);
        modelMap.addAttribute("id_db",id_db);
        return "query";
    }

    /**
     * 进入数据源操作界面
     *
     * @param modelMap
     * @param dbBean
     * @return
     */
    @RequestMapping(value = "query",produces="text/plain;charset=UTF-8",method= RequestMethod.GET,params="action=add")
    @CrossOrigin
    public String insert(ModelMap modelMap, DBSourceBean dbBean) {
        logger.info("*****************进入数据源操作页面*******************");
        modelMap.addAttribute("dbBean",dbBean);
        return "addDs";
    }

    /**
     *
     * 数据源更新
     * @param modelMap
     * @param dbBean
     * @return
     */
    @RequestMapping(value = "add",produces="text/plain;charset=UTF-8",method= RequestMethod.POST)
    @CrossOrigin
    public String add(ModelMap modelMap, DBSourceBean dbBean) {
        logger.info("*****************进入数据源操作后台*******************");

        service.savaDB(dbBean);
        id_db = service.getAllSids();
        modelMap.addAttribute("dsBean",dsBean);
        modelMap.addAttribute("id_db",id_db);
        return "query";
    }


    /**
     * 根据db table 查询字段
     * @param modelMap
     * @param dsBean
     * @return
     */
    @RequestMapping(value = "query",produces="text/plain;charset=UTF-8",method= RequestMethod.POST,params="action=query")
    @CrossOrigin
    public String query(ModelMap modelMap, DSBean dsBean) {
        logger.info("*****************进入查询表字段后台*******************");
        String source_id = dsBean.getSource_id();
        String source_table = dsBean.getSource_table();
        String target_id = dsBean.getTarget_id();

        // 根据id 查找db
        String source_db = id_db.get(source_id);
        String target_db = id_db.get(target_id);

        /**
         * 先根据页面传递的id 去查找数据信息
         * 再根据数据库信息，查询的字段
         */

        List<String> cols = service.getTabCols(dsBean);
        // 如果表不存在 则返回到初始页面
        if(cols.isEmpty()){
            modelMap.addAttribute("dsBean",dsBean);
            modelMap.addAttribute("id_db",id_db);
            modelMap.addAttribute("msg","无法查询到该表，请重新输入");
            return "query" ;
        }

        // 存在topic_id和topic_name 关系
//        Map<String,String> topic_id_name = service.getAllTopics();
//        List<String> topics = Arrays.asList("t1","t2");
            Map<String,String> topic_id_name = new HashMap<>();
        topic_id_name.put("12","termgsi");



        sti.setSource_id(source_db);
        sti.setSource_table(source_table);
        sti.setTarget_id(target_db);
        modelMap.addAttribute("sti",sti);
        modelMap.addAttribute("cols",cols);
        modelMap.addAttribute("topics",topic_id_name);
        modelMap.addAttribute("id_db",id_db);
        return "setting";
    }

    /**
     * 保存相关配置到数据库
     * 只保存，不发布
                *
     * @return
     */
        @RequestMapping(value = "save",produces="text/plain;charset=UTF-8",method= RequestMethod.POST,params="action1=save")
        public String save(ModelMap modelMap, SyncTableInfo sti){
            logger.info("*****************进入保存配置后台*******************");
            String is_released = "0";  // 保存
            sti.setIs_released(is_released);
            //转换sti
            updateSti(sti);
            service.saveConf(sti);

            // 返回初始页面
            modelMap.addAttribute("dsBean",dsBean);
            modelMap.addAttribute("id_db",id_db);
            return "query";
    }



    /**
     * 保存相关配置到数据库
     * 保存并发布
     *
     * @return
     */
    @RequestMapping(value = "save",produces="text/plain;charset=UTF-8",method= RequestMethod.POST,params="action1=release")
    public String  release(ModelMap modelMap, SyncTableInfo sti){
        logger.info("*****************进入发布配置后台*******************");
        String is_released = "1";  // 发布
        sti.setIs_released(is_released);
        //转换sti
        updateSti(sti);
        service.saveConf(sti);

        // 返回初始页面
        modelMap.addAttribute("dsBean",dsBean);
        modelMap.addAttribute("id_db",id_db);
        return "query";
    }


    /**
     *
     * 将页面传过来的sti 重新整理
     * 1 将db_name 转换成 db_id
     * 2 将标准化字段转换成准确的
     * 如果没有则(,,,,,,,,) 会根据字段的个数 以','分开，只需要标准化的字段
     *  2,23,3,,,,  => 2,23,3
     *  ,,,,, => ""
     * @param sti
     * @return
     */
    public SyncTableInfo updateSti(SyncTableInfo sti){
        String source_name = sti.getSource_id();
        String target_name = sti.getTarget_id();
        String Standarded_column = sti.getStandarded_column();
        String source_id ="";
        String target_id="";

        /**
         * 根据db_name 去查找db_id
         */

        for(Map.Entry db:id_db.entrySet()){
            if(source_name.equals(db.getValue()))
                source_id = db.getKey().toString();
            if(target_name.equals(db.getValue()))
                target_id = db.getKey().toString();

        }

        /**
         *   2,23,3,,,,  => 2,23,3 size>1
         *   ,,,,, => ""   size 0
         *   ""  => ""
         */

        List<Object> cols = Arrays.asList(Standarded_column.split(","));
        StringBuffer sb = new StringBuffer();
        if("".equals(Standarded_column) || cols.size()==0)
            Standarded_column = "";
        else{
            for(Object co:cols){
                if(!"".equals(co))
                    sb.append(co+",");
            }
            Standarded_column = sb.substring(0,sb.length()-1);
        }


        sti.setStandarded_column(Standarded_column);
        sti.setSource_id(source_id);
        sti.setTarget_id(target_id);

        return sti;
    }
}
