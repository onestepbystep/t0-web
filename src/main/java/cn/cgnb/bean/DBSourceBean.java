package cn.cgnb.bean;

/**
 * Created by ah on 2018/5/14.
 */
public class DBSourceBean {

    private String id;
    private String db_name;
    private String db_user;
    private String db_pwd;
    private String db_url;
    private String db_type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDb_name() {
        return db_name;
    }

    public void setDb_name(String db_name) {
        this.db_name = db_name;
    }

    public String getDb_user() {
        return db_user;
    }

    public void setDb_user(String db_user) {
        this.db_user = db_user;
    }

    public String getDb_pwd() {
        return db_pwd;
    }

    public void setDb_pwd(String db_pwd) {
        this.db_pwd = db_pwd;
    }

    public String getDb_url() {
        return db_url;
    }

    public void setDb_url(String db_url) {
        this.db_url = db_url;
    }

    public String getDb_type() {
        return db_type;
    }

    public void setDb_type(String db_type) {
        this.db_type = db_type;
    }
}
