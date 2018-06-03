package cn.cgnb.conf;

/**
 *
 *
 * Created by ah on 2018/5/31.
 */
public class PageParam {

    private int page = 1; // 当前页码
    private int rows = 10; // 每页的记录数
    private int startIndex;// SQL查询起始INDEX
    private int endIndex;// SQL查询结束INDEX

    private void calculateIndex() {
        this.startIndex = (page - 1) * rows;
        this.endIndex = startIndex + rows;
    }

    public PageParam() {
        super();
        this.calculateIndex();
    }

    public PageParam(int page, int rows) {
        super();
        this.page = page;
        this.rows = rows;
        this.calculateIndex();
    }

    public void setPage(int page) {
        this.page = page;
        this.calculateIndex();
    }

    public void setRows(int rows) {
        this.rows = rows;
        this.calculateIndex();
    }

    @Override
    public String toString() {
        return "PageParam [page=" + page + ", rows=" + rows + "]";
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }
    public int getPage() {
        return page;
    }

    public int getRows() {
        return rows;
    }
}
