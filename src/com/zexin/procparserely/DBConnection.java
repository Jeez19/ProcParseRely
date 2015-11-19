package com.zexin.procparserely;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static Connection mySQlConn;

    private static Connection oracleConn;


    public static Connection getMySQLConn() {
        final String DB_URL = "jdbc:mysql://localhost:3306/jdbctest";
        final String DB_DRIVER = "com.mysql.jdbc.Driver";
        final String DB_USER = "root";
        final String DB_PASS = "123456";
        if (null == mySQlConn) {
            try {
                Class.forName(DB_DRIVER);
                mySQlConn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return mySQlConn;
    }


    public static Connection getOracleConn() {
        final String DB_URL = "jdbc:oracle:thin:@192.168.10.163:1521:racdb1";
        final String DB_DRIVER = "oracle.jdbc.driver.OracleDriver";
        final String DB_USER = "dwh";
        final String DB_PASS = "asusp4v8x";
        if (null == oracleConn) {
            try {
                Class.forName(DB_DRIVER);
                oracleConn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return oracleConn;
    }
}
