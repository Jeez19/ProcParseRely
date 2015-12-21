package com.zexin.procparserely;

import java.util.Iterator;

/**
 * Created by Zexin on 2015/11/12.
 */
public class GameStart {
    public static void main(String args[]) {
        SQLstmt sqlStmt = new SQLstmt();
        sqlStmt.strTBName = "sp_intf_goldmall_user_task";

        sqlStmt.strStmtDetail = SQLstmtParse.stmtParse(sqlStmt.strTBName);


        ProcCalledRely procCalledRely = new ProcCalledRely(sqlStmt.strTBName, sqlStmt.strStmtDetail);
        //System.out.println("MainProcName is: " + procCalledRely.mainProcName);
        //System.out.println("MainProcStmt is: \n" + procCalledRely.mainProcStmt);
        //System.out.print("\n");

        /*
        for (String e : procCalledRely.subProc) {
            System.out.println("        SubProc is:" + e);
        }

        /*
        System.out.print("\n");
        for (String key : procCalledRely.tbMap.keySet()) {
            System.out.println("        SubTable is:" + key + "\n               and the rely type is: " + procCalledRely.tbMap.get(key));
        }
        */
    }
}
