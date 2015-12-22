package com.zexin.procparserely;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Zexin on 2015/11/12.
 */
public class SQLstmtExe {

    public static String stmtParse(String strTBName) {
        String stbStmtDetail = getProcPropeties(strTBName);
        stbStmtDetail = delComment(stbStmtDetail);
        return stbStmtDetail;
    }

    public static String getProcPropeties(String strTBName) {
        StringBuilder stbStmtDetail = new StringBuilder();

        try {
            Connection oracleConn = DBConnection.getOracleConn();
            Statement stmtMySQL = oracleConn.createStatement();
            String strSQLQuery = "SELECT text FROM user_source where name = '" + strTBName.toUpperCase() + "'";
            ResultSet rsSet = stmtMySQL.executeQuery(strSQLQuery);
            while (rsSet.next()) {
                stbStmtDetail = stbStmtDetail.append(rsSet.getString("text"));
            }
            if (!rsSet.equals(null)) {
                rsSet.close();
            }
            if (!stmtMySQL.equals(null)) {
                stmtMySQL.close();
            }
            if (!oracleConn.equals(null)) {
                oracleConn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stbStmtDetail.toString();
    }

    public static void executeSQL(String stmt) {

        try {
            Connection oracleConn = DBConnection.getOracleConn();
            Statement stmtMySQL = oracleConn.createStatement();
            stmtMySQL.executeUpdate(stmt);
            if (!stmtMySQL.equals(null)) {
                stmtMySQL.close();
            }
            if (!oracleConn.equals(null)) {
                oracleConn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String delComment(String stbStmtDetail) {
        String cmtB = "/*";
        String cmtA = "*/";
        stbStmtDetail = stbStmtDetail.replaceAll("\\/\\*.*\\*\\/", "");
        stbStmtDetail = stbStmtDetail.replaceAll("--.*\\n", "");
        StringBuilder strFormSQL = new StringBuilder(stbStmtDetail);
        int delIdx01 = 0;
        int delIdx02;
        for (int i = 0; i < strFormSQL.length() - 2; i++) {
            String strLocate = strFormSQL.substring(i, i + 2);
            if (strLocate.equals(cmtB)) {
                delIdx01 = i;
            }
            if (strLocate.equals(cmtA)) {
                delIdx02 = i + 2;
                strFormSQL = strFormSQL.replace(delIdx01, delIdx02, "");
            }
        }

        return strFormSQL.toString();
    }
}
