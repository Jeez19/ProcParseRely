package com.zexin.procparserely;

/**
 * Created by Zexin on 2015/11/12.
 */
public class GameStart {
    public static void goBabyGo(String originalProc) {
        SQLstmt sqlStmt = new SQLstmt();
        sqlStmt.strTBName = originalProc;

        sqlStmt.strStmtDetail = SQLstmtExe.stmtParse(sqlStmt.strTBName);


        ProcCalledRely procCalledRely = new ProcCalledRely(sqlStmt.strTBName, sqlStmt.strStmtDetail);
        System.out.println("MainProcName is: " + procCalledRely.mainProcName);
        System.out.println("MainProcStmt is: \n" + procCalledRely.mainProcStmt);
        System.out.print("\n");

        //StringBuilder subProcSQL = new StringBuilder();
        for (String e : procCalledRely.subProc) {
            String tmpSQL = "insert into zzx_tb_proc_rely (proc_name, rely_type, rely_name) values ('" + procCalledRely.mainProcName + "','subProc','" + e + "')";
            System.out.println("        SubProc is:" + tmpSQL);
            SQLstmtExe.executeSQL(tmpSQL);
            //subProcSQL.append(tmpSQL);
            goBabyGo(e);
        }


        System.out.print("\n");
        //StringBuilder intoTableSQL = new StringBuilder();
        for (String e : procCalledRely.tbIntoMap) {
            String tmpSQL = "insert into zzx_tb_proc_rely (proc_name, rely_type, rely_name) values ('" + procCalledRely.mainProcName + "','into table','" + e + "')";
            System.out.println("        SubIntoTable is:" + tmpSQL);
            SQLstmtExe.executeSQL(tmpSQL);
            //intoTableSQL.append(tmpSQL);
        }

        System.out.print("\n");
        //StringBuilder fromTableSQL = new StringBuilder();
        for (String e : procCalledRely.tbFromMap) {
            String tmpSQL = "insert into zzx_tb_proc_rely (proc_name, rely_type, rely_name) values ('" + procCalledRely.mainProcName + "','from table','" + e + "')";
            System.out.println("        SubFromTable is:" + tmpSQL);
            SQLstmtExe.executeSQL(tmpSQL);
            //fromTableSQL.append(tmpSQL);
        }
        /*
        System.out.println(subProcSQL);
        System.out.println(intoTableSQL);
        System.out.println(fromTableSQL);
        StringBuilder executeSQL = new StringBuilder();
        executeSQL.append(subProcSQL).append(intoTableSQL).append(fromTableSQL);
        System.out.println(executeSQL);
        SQLstmtExe.executeSQL(executeSQL.toString());
        */
    }

    public static void main(String args[]){
        goBabyGo("sp_dw_list_manager_day");
    }
}
