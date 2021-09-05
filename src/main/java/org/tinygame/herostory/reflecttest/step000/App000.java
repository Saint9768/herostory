package org.tinygame.herostory.reflecttest.step000;


import org.tinygame.herostory.reflecttest.step000.entity.UserEntity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 主应用程序类
 */
public class App000 {
    /**
     * 应用程序主函数
     *
     * @param argvArray 参数数组
     * @throws Exception
     */
    static public void main(String[] argvArray) throws Exception {
        (new App000()).start();
    }

    /**
     * 测试开始
     */
    private void start() throws Exception {
        // 加载 Mysql 驱动
        Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
        // 数据库连接地址
        String dbConnStr = "jdbc:mysql://localhost:3306/ormtest?user=root&password=123456";
        // 创建数据库连接
        Connection conn = DriverManager.getConnection(dbConnStr);

//        String insertSql = "insert into t_user (user_id, user_name, password) values (?, ?, ?)";
//        PreparedStatement ps = conn.prepareStatement(insertSql);
//        final int batchSize = 1000;
//        int count = 0;
//        for (int i = 1005; i <= 21000; ++i) {
//            ps.setInt(1, i);
//            ps.setString(2, "name" +i);
//            ps.setString(3,"password" + i);
//            ps.addBatch();
//            if ( ++ count % batchSize == 0) {
//                ps.executeBatch();
//            }
//        }
//        ps.executeBatch();
//        ps.close();


        // 简历陈述对象
        Statement stmt = conn.createStatement();

        // 创建 SQL 查询
        // ormtest 数据库中有个 t_user 数据表,
        // t_user 数据表包括三个字段: user_id、user_name、password,
        // t_user 数据表有 20 万条数据
        String sql = "select * from t_user limit 20000";

        // 执行查询
        ResultSet rs = stmt.executeQuery(sql);

        // 获取开始时间
        long t0 = System.currentTimeMillis();

        while (rs.next()) {
            // 创建新的实体对象
            UserEntity ue = new UserEntity();

            ue._userId = rs.getInt("user_id");
            ue._userName = rs.getString("user_name");
            ue._password = rs.getString("password");
            //
            // 关于上面这段代码,
            // 我们是否可以将其封装到一个助手类里??
            // 这样做的好处是:
            // 当实体类发生修改时, 只需要改助手类就可以了...
            //
        }

        // 获取结束时间
        long t1 = System.currentTimeMillis();

        // 关闭数据库连接
        stmt.close();
        conn.close();

        // 打印实例化花费时间
        System.out.println("实例化花费时间 = " + (t1 - t0) + "ms");
    }
}
