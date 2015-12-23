package com.zexin.procparserely;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Zexin on 2015/11/12.
 */
public class GameStart {
    public static ArrayList<String> procList = new ArrayList<String>();

    public static void goBabyGo(String originalProc) {
        if (!GameStart.procList.contains(originalProc.toLowerCase())) {
            GameStart.procList.add(originalProc.toLowerCase());
            SQLstmt sqlStmt = new SQLstmt();
            sqlStmt.strTBName = originalProc.toLowerCase();

            sqlStmt.strStmtDetail = SQLstmtExe.stmtParse(sqlStmt.strTBName);

            ProcCalledRely procCalledRely = new ProcCalledRely(sqlStmt.strTBName, sqlStmt.strStmtDetail);
            //System.out.println("MainProcName is: " + procCalledRely.mainProcName);
            //System.out.println("MainProcStmt is: \n" + procCalledRely.mainProcStmt);

            //System.out.print("\n");
            for (String e : procCalledRely.subProc) {
                //String tmpSQL = "insert into zzx_tb_proc_rely (proc_name, rely_type, rely_name) values ('" + procCalledRely.mainProcName + "','subProc','" + e + "')";
                //System.out.println("        SubProc is:" + tmpSQL);
                goBabyGo(e);
            }

            //System.out.print("\n");
            for (String e : procCalledRely.tbIntoMap) {
                String tmpSQL = "insert into zzx_tb_proc_rely (proc_name, rely_type, rely_name) values ('" + procCalledRely.mainProcName + "','into table','" + e + "')";
                System.out.println("        SubIntoTable is:" + tmpSQL);
                SQLstmtExe.executeSQL(tmpSQL);
            }

            //System.out.print("\n");
            for (String e : procCalledRely.tbFromMap) {
                String tmpSQL = "insert into zzx_tb_proc_rely (proc_name, rely_type, rely_name) values ('" + procCalledRely.mainProcName + "','from table','" + e + "')";
                System.out.println("        SubFromTable is:" + tmpSQL);
                SQLstmtExe.executeSQL(tmpSQL);
            }
        }
    }

    public static void main(String args[]) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        System.out.println(df.format(new Date()));// new Date()为获取当前系统时间

        String sqlTruncateTable = "truncate table zzx_tb_proc_rely";
        SQLstmtExe.executeSQL(sqlTruncateTable);
        goBabyGo("sp_dw_list_manager_day");//SP_INTF_USER_CLIENT_VISIT_LOG, pkg_tm_yzf

        df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        System.out.println(df.format(new Date()));// new Date()为获取当前系统时间
    }
}
