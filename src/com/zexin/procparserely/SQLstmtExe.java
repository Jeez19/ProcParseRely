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
            /*
            if (!oracleConn.equals(null)) {
                oracleConn.close();
            }
            */
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
            /*
            if (!oracleConn.equals(null)) {
                oracleConn.close();
            }
            */
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String delComment(String stbStmtDetail) {
        stbStmtDetail = stbStmtDetail.replaceAll("\\/\\*.*\\*\\/", "");
        //System.out.println(stbStmtDetail);
        stbStmtDetail = stbStmtDetail.replaceAll("--.*\\n", "");
        //System.out.println(stbStmtDetail);
        for (int sIdx = 0; sIdx < stbStmtDetail.length() - 2; sIdx++) {
            if (stbStmtDetail.substring(sIdx, sIdx + 2).equals("/*")) {
                for (int eIdx = sIdx + 1; eIdx < stbStmtDetail.length() - 2; eIdx++) {
                    if (stbStmtDetail.substring(eIdx, eIdx + 2).equals("*/")) {
                        //System.out.println("This is a comment: \n" + stbStmtDetail.substring(sIdx, eIdx + 2));
                        stbStmtDetail = stbStmtDetail.replace(stbStmtDetail.substring(sIdx, eIdx + 2), "");
                        //sIdx = 0;
                        break;
                    }
                }
            }
        }
        //System.out.println(stbStmtDetail);

        return stbStmtDetail;
    }
}
