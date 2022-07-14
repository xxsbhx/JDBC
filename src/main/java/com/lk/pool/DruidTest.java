package com.lk.pool;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.lk.entity.Human;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.junit.Test;

import java.io.InputStream;
import java.sql.Connection;
import java.util.List;
import java.util.Properties;

/**
 * @ClassName DruidTest
 * @Description TODO
 * @Author lk
 * @Date 2022/07/13 22:42
 * @Version 1.0
 */
public class DruidTest {


    public static Connection getConnection() throws Exception {
        DruidDataSource source;
        Properties properties = new Properties();
        //通过类加载器加载配置文件
        InputStream inputStream = DruidTest.class.getClassLoader().getResourceAsStream("druid.properties");
        properties.load(inputStream);
        source = (DruidDataSource) DruidDataSourceFactory.createDataSource(properties);
        return source.getConnection();
    }

    @Test
    public void testQueryRunner() throws Exception {
        QueryRunner runner = new QueryRunner();
        String sql = "update human set name = ? where id = ?";
        int i = runner.update(getConnection(), sql, "ym", 48);
        System.out.println(i);

        sql = "select id, name from human where id > ?";
        BeanListHandler<Human> handler = new BeanListHandler<>(Human.class);
        List<Human> list = runner.query(getConnection(), sql, handler, 30);
        list.forEach(System.out::println);
    }
}
