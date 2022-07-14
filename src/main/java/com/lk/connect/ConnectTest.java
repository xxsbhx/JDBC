package com.lk.connect;

import com.lk.entity.Human;
import com.lk.entity.Persons;
import com.lk.utils.JDBCUtils;
import org.junit.Test;

import java.io.*;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * @ClassName ConnectTest
 * @Description TODO
 * @Author lk
 * @Date 2022/07/13 9:34
 * @Version 1.0
 */
public class ConnectTest {

    /**
     * @return void
     * @Author lk
     * @Description 测试数据库连接
     * @Date 9:35 2022/7/13
     * @Param []
     */
    @Test
    public void testConnection() throws Exception {

        String url = "jdbc:mysql://localhost:3306";
        String user = "root";
        String password = "123mysql";
        Connection connection = DriverManager.getConnection(url, user, password);
        System.out.println(connection);
        connection.close();
    }

    /**
     * @return void
     * @Author lk
     * @Description 测试如何使用properties文件读取jdbc配置
     * @Date 10:19 2022/7/13
     * @Param []
     */
    @Test
    public void testProperties() throws Exception {
        Properties properties = new Properties();
        FileReader reader = new FileReader("src/main/java/com/lk/connect/config.properties");
        properties.load(reader);
        String driverName = properties.getProperty("driver");
        String url = properties.getProperty("url");
        String user = properties.getProperty("user");
        String password = properties.getProperty("password");
        Class.forName(driverName);
        Connection connection = DriverManager.getConnection(url, user, password);
        System.out.println(connection);
        connection.close();
    }

    /**
     * @return void
     * @Author lk
     * @Description 测试JDBCUtils功能
     * @Date 11:12 2022/7/13
     * @Param []
     */
    @Test
    public void testJDBCUtils() {
        Connection connection = JDBCUtils.getConnection();
        System.out.println(connection);
        JDBCUtils.releaseConnection(connection);

    }

    /**
     * @return void
     * @Author lk
     * @Description 测试插入操作
     * @Date 11:31 2022/7/13
     * @Param []
     */
    @Test
    public void testInsert() throws Exception {
        Connection connection = JDBCUtils.getConnection();
        System.out.println(connection);
        String sql = "insert into human(name) values(?)";

        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, "llkkk");
        int i = statement.executeUpdate();
        System.out.println(i);
        JDBCUtils.releaseResource(connection, statement);
        System.out.println(connection.isClosed() + " " + statement.isClosed());
    }

    /**
     * @return void
     * @Author lk
     * @Description 测试删除操作
     * @Date 11:36 2022/7/13
     * @Param []
     */
    @Test
    public void testDelete() throws Exception {
        Connection connection = JDBCUtils.getConnection();
        String sql = "delete from human where id > ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, 50);
        int i = statement.executeUpdate();
        System.out.println(i);
        JDBCUtils.releaseResource(connection, statement);
    }


    @Test
    public void testUniversalSelect() {
        String sql = "select Id_P id, LastName lastName, FirstName firstName, Address address, City city from persons where Id_P < ?";
        try {
            LinkedList<Persons> list = universalSelect(Persons.class, sql, 30);
            list.forEach(System.out::println);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static <T> LinkedList<T> universalSelect(Class<T> clazz, String sql, Object... params) throws Exception {
        Connection connection = JDBCUtils.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            statement.setObject(i + 1, params[i]);
        }
        ResultSet resultSet = statement.executeQuery();
        LinkedList<T> list = new LinkedList<>();
        ResultSetMetaData metaData = resultSet.getMetaData();
        while (resultSet.next()) {
            T t = clazz.newInstance();
            int columnCount = metaData.getColumnCount();
            for (int i = 0; i < columnCount; i++) {
                Object value = resultSet.getObject(i + 1);
                String fieldName = metaData.getColumnLabel(i + 1);
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(t, value);
            }
            list.add(t);
        }
        return list;
    }

    /**
     * @return void
     * @Author lk
     * @Description 测试查询操作
     * @Date 11:40 2022/7/13
     * @Param []
     */
    @Test
    public void testSelect() throws SQLException {
        Connection connection = JDBCUtils.getConnection();
        String sql = "select id,name from human where id > ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, 35);
        ResultSet resultSet = statement.executeQuery();
        List<Human> list = new LinkedList<>();
        ResultSetMetaData metaData = resultSet.getMetaData();
        while (resultSet.next()) {
//            int id = resultSet.getInt(1);
//            String name = resultSet.getString(2);
//            list.add(new Human(id, name));
            int columnCount = metaData.getColumnCount();
            for (int i = 0; i < columnCount; i++) {
                System.out.println(metaData.getColumnName(i + 1));
            }
        }
//        list.forEach(System.out::println);
        JDBCUtils.releaseResource(connection, statement);

    }

    public static int universal(String sql, Object... params) {
        Connection connection = JDBCUtils.getConnection();
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < params.length; i++) {
            try {
                statement.setObject(i + 1, params[i]);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        int i = 0;
        try {
            i = statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        JDBCUtils.releaseResource(connection, statement);
        return i;
    }

    @Test
    public void testUniversal() throws SQLException {
        String sql = "insert into human values(?,?)";
        int i = universal(sql, 48, "kk");
        System.out.println(i);
    }

    @Test
    public void testBlobInsert() throws Exception {
        Connection connection = JDBCUtils.getConnection();
        String sql = "insert into starts values(?,?)";
        InputStream stream = new FileInputStream("D:/Browser-Download/ym.jpeg");
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, "杨幂");
        statement.setBlob(2, stream);
        statement.execute();
        JDBCUtils.releaseResource(connection, statement);
    }

    @Test
    public void testBlobRead() throws Exception {
        Connection connection = JDBCUtils.getConnection();
        String sql = "select img from starts where name = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, "杨幂");
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            Blob blob = resultSet.getBlob(1);
            InputStream stream = blob.getBinaryStream();
            OutputStream fileOutputStream = new FileOutputStream("ym.jpeg");
            byte[] bytes = new byte[1024];
            int len;
            while ((len = stream.read(bytes)) != -1) {
                fileOutputStream.write(bytes,0,len);
            }
        }
        JDBCUtils.releaseResource(connection, statement);
    }

    @Test
    public void TestPrintStream() {
        try (PrintStream writer = new PrintStream(new FileOutputStream("D:/ab.txt", true))) {
            writer.println(34);
            writer.println("ert");
        } catch (IOException e) {
            // ... handle IO exception
        }
    }
}
