package me.echo.community.entity;

/**
 * 分页支持
 */
public class Page {
    // 当前页码
    private int current = 1;
    // 每页显示 limit 条数据
    private int limit = 10;
    // 总共有多少数据
    private int rows;
    // 页面地址
    private String Path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if (current>0){
            this.current = current;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if (limit>0&&limit<100) {
            this.limit = limit;
        }
    }

    public String getPath() {
        return Path;
    }

    public void setPath(String path) {
        Path = path;
    }

    public int getOffset(){
        return (current-1)*limit;
    }

    /**
     * 获取总页数
     */
    public int getTotal(){
        return (rows%limit==0)?(rows/limit):(rows/limit)+1;
    }

    /**
     * 获取起始页
     */
    public int getFrom(){
        return (current-2 > 0)?(current-2):1;
    }

    /**
     * 获取结束页
     */
    public int getTo(){
        return Math.min(current + 2, getTotal());
    }
}
