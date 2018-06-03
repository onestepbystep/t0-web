package cn.cgnb.conf;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ah on 2018/5/31.
 */
public class PageResult<SyncTableInfo> implements Serializable {
    private static final long serialVersionUID = -1750386840274995765L;

    private long total; // 总记录数
    private List<SyncTableInfo> rows; // 查询出的结果数

    public PageResult() {
        super();
    }

    public PageResult(long total, List<SyncTableInfo> rows) {
        super();
        this.total = total;
        this.rows = rows;
    }

    @Override
    public String toString() {
        return "PageResult [total=" + total + ", rows=" + rows + "]";
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<?> getRows() {
        return rows;
    }

    public void setRows(List<SyncTableInfo> rows) {
        this.rows = rows;
    }
}
