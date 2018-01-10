import java.sql.*;
import java.util.ArrayList;

public class Db {
    private String url;
    private String tableName = "table_1";
    private String status;
    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;

    Db(String url) {
        this.url = url;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:" + url);
            if (conn != null)
                status = "Connect database success!";
            else
                status = "Connect database failed!";
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getStatus() {
        return status;
    }

    /**
     * 获取table_1的若干个条目
     * @param offset
     * @param other
     * @return 以\n分割的若干组数据
     */
    public String select(int limit, int offset, String other) {
        String rt = "";
        try {
            stmt = conn.createStatement();
            String sql;
            if (other.equals(""))
                sql = "select * from table_1 limit " + limit + " offset " + offset;
            else
                sql = "select * from table_1 order by " + other + " limit " + limit + " offset " + offset;
            rs = stmt.executeQuery(sql);
            status = sql;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (rs != null) {
            try {
                while (rs.next()) {
                    String s = rs.getInt(1) + "|"
                            + rs.getString(2) + "|"
                            + rs.getInt(4) + "|"
                            + rs.getInt(5) + "|"
                            + rs.getString(6) + "|"
                            + rs.getInt(7) + "\n";
                    rt += s;
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return rt;
    }

    /**
     * 根据TITLE更新KEYWORDS和MARKED
     * @param title
     * @param keywords
     * @param marked
     * @return rows effected
     */
    public int updateWhere(String title, String keywords, String marked) {
        int rt = 0;
        try {
            stmt = conn.createStatement();
            String sql = "update table_1 set KEYWORDS = \'" + keywords
                    + "\', MARKED = " + marked
                    + " where TITLE = \'" + title + "\'";
            rt = stmt.executeUpdate(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rt;
    }

    /**
     * 根据TITLE获取LINKS
     * @param title
     * @return url
     */
    public String getUrl(String title) {
        String url = null;
        try {
            stmt = conn.createStatement();
            String sql = "select LINK from table_1 where TITLE = \'" + title + "\'";
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                url = rs.getString(1);
            }
            status = sql;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return url;
    }
}
