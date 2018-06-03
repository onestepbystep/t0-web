package cn.cgnb.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import static org.apache.logging.log4j.message.MapMessage.MapFormat;

/**
 * Created by lvjianyu on 2017/6/27.
 */
public class BaseController {

    protected HttpServletRequest request;

    private static final Logger logger = Logger.getLogger(BaseController.class);

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public String handleException(Exception e) {
        e.printStackTrace();
        logger.error("系统错误日志:" + e);
        return returnMsg(500,e.getMessage(),false);
    }

    /**
     * 返回格式 {"code":500,"msg":"错误","flag":false}
     * @param code 状态码
     * @param msg 消息
     * @param flag 请求成功或失败状态 true|false
     * @return
     */
    public String returnMsg(int code,String msg,Boolean flag){
        JSONObject json = new JSONObject();
        json.put("code",code);
        json.put("msg",msg);
        json.put("flag",flag);
        return JSON.toJSONString(json);
    }

    public String returnMsg(int code,String msg,Boolean flag,Object data){
        JSONObject json = new JSONObject();
        json.put("code",code);
        json.put("msg",msg);
        json.put("flag",flag);
        json.put("data",data);
        return JSON.toJSONString(json);
    }

    public String returnSpecialMsg(int code,String msg,Boolean flag,Object data,Object data2){
        JSONObject json = new JSONObject();
        json.put("code",code);
        json.put("msg",msg);
        json.put("flag",flag);
        json.put("data",data);
        JSONObject data1 =json.getJSONObject("data");
        data1.put("menu", data2);
        json.put("data",data1);
        return JSON.toJSONString(json);
    }
    
    /**
     * 返回数据格式 {"code":200,"data":"{}"}
     * @param code
     * @param data
     * @return
     */
    public String returnData(int code,Object data){
        JSONObject json = new JSONObject();
        json.put("code",code);
        json.put("data",data);
        return JSON.toJSONString(json);
    }

    /**
     * 返回分页数据格式
     * @param code
     * @param data
     * @param pageNum
     * @param pageSize
     * @param total
     * @return
     */
    public String returnPage(int code,Object data,Integer pageNum,Integer pageSize,Long total){
        JSONObject json = new JSONObject();
        json.put("code",code);
        json.put("data",data);
        json.put("pageNum",pageNum);
        json.put("pageSize",pageSize);
        json.put("total",total);
        return JSON.toJSONString(json);
    }

    @Resource
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

//    public SysUser getCurrentUser(){
//        HttpSession session = request.getSession();
//        SysUser sysUser = (SysUser) session.getAttribute("SYS_USER");
////        SysUser sysUser = new SysUser();
////        sysUser.setId(2);
////        sysUser.setPower(0);
////        sysUser.setEmail("471677724@qq.com");
////        sysUser.setParentId(1);
////        sysUser.setPhoneNotice("1");
////        sysUser.setNoticeWay("0");
////        sysUser.setParentIds("0,1,2,");
////        sysUser.setPassword("5bc97e02dde6d9f745c6937531c306d1fe8b9b66be0cf313a5ae58ca");
//        return sysUser;
//    }
}
