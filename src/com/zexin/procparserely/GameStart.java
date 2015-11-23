package com.zexin.procparserely;

import java.util.Iterator;

/**
 * Created by Zexin on 2015/11/12.
 */
public class GameStart {
    public static void main(String args[]) {
        SQLstmt sqlStmt = new SQLstmt();
        sqlStmt.strTBName = "sp_intf_user_register";

        //sqlStmt.strStmtDetail = SQLstmtParse.stmtParse(sqlStmt.strTBName);
        sqlStmt.strStmtDetail = "procedure sp_intf_user_register(v_date in varchar2 default to_char(sysdate-1,'yyyymmdd')) as\n" +
                "  v_log_id int;\n" +
                "  \n" +
                "  v_sql    varchar2(2000):=q'<\n" +
                "    insert into intf_user_register\n" +
                "      ( imsi,\n" +
                "        mobilephone,\n" +
                "        inserttime,\n" +
                "        updatetime,\n" +
                "        fromer )\n" +
                "    select \"imsi\",\"phone\",unix_to_oracle(\"insert_time\"),unix_to_oracle(\"update_time\"),'10010105' fromer\n" +
                "      from \"t_user_imsi_reverse\"@egame_user\n" +
                "     where \"insert_time\" >= :1\n" +
                "       and \"insert_time\" <= :2                                 \n" +
                "  >';\n" +
                "  \n" +
                "  v_sql2  varchar2(2000):=q'<\n" +
                "    insert into intf_user_register(imsi,inserttime,fromer)\n" +
                "    select tup.\"u_id\",unix_to_oracle(\"reg_date\"),'10020102'\n" +
                "    from \"test01\"@egame_user,\n" +
                "         \"test02\"@egame_user,\n" +
                "         \"test03\"@egame_user test,\n" +
                "         \"test04\"@egame_user tt\n" +
                "   where tup.\"password\" is not null\n" +
                "     and tup.\"password\" != ''\n" +
                "     and tur.\"reg_date\" >= :1\n" +
                "     and tur.\"reg_date\" <= :2\n" +
                "     and tup.\"u_id\" = tur.\"u_id\"\n" +
                "     and \"fromer\" in (10020101,10020301,10020702,10020703,10050301,10060301,10080301)\n" +
                "  >';\n" +
                "  \n" +
                "  v_begintime  number(20):=time_to_timestamp(to_date(v_date,'yyyymmdd'));\n" +
                "  v_endtime    number(20):=time_to_timestamp(to_date(v_date,'yyyymmdd')+1);\n" +
                "\n" +
                "begin\n" +
                "  select seq_proc_run_log_id.nextval into v_log_id from dual;\n" +
                "  sp_proc_run_log('insert',v_log_id,'sp_intf_user_register',v_date,'intf_user_register','insert','1',null,null,null,null);\n" +
                "  \n" +
                "  execute immediate 'alter table intf_user_register truncate partition p'||v_date;\n" +
                "\n" +
                "  insert into intf_user_register\n" +
                "        (userid,\n" +
                "         egameno,\n" +
                "         cipher,\n" +
                "         paycipher,\n" +
                "         egame,\n" +
                "         mobilephone,\n" +
                "         imsi,\n" +
                "         modelid,\n" +
                "         checkphone,\n" +
                "         email,\n" +
                "         checkemail,\n" +
                "         tvmac,\n" +
                "         psncode,\n" +
                "         itvcode,\n" +
                "         platformid,\n" +
                "         provinceid,\n" +
                "         cityid,\n" +
                "         phonekey,\n" +
                "         emailkey,\n" +
                "         accountstatus,\n" +
                "         realname,\n" +
                "         cardno,\n" +
                "         nickname,\n" +
                "         gender,\n" +
                "         headportrait,\n" +
                "         isdefault,\n" +
                "         birthday,\n" +
                "         homecity,\n" +
                "         province,\n" +
                "         city,\n" +
                "         typeoffigure,\n" +
                "         bloodgroup,\n" +
                "         standing,\n" +
                "         maritalstatus,\n" +
                "         platitude,\n" +
                "         interestobject,\n" +
                "         interestrange,\n" +
                "         linkphone,\n" +
                "         telephone,\n" +
                "         linkemail,\n" +
                "         address,\n" +
                "         qqcode,\n" +
                "         wbcode,\n" +
                "         msncode,\n" +
                "         workunit,\n" +
                "         schoolid,\n" +
                "         schoolname,\n" +
                "         homepage,\n" +
                "         comments,\n" +
                "         friendship,\n" +
                "         searched,\n" +
                "         footmark,\n" +
                "         message,\n" +
                "         greeting,\n" +
                "         sharing,\n" +
                "         showage,\n" +
                "         showlocation,\n" +
                "         channelcode,\n" +
                "         sourceurl,\n" +
                "         fromer,\n" +
                "         actiontime,\n" +
                "         inserttime,\n" +
                "         updatetime,\n" +
                "         lasttwonum)\n" +
                "    select userid,\n" +
                "           egameno,\n" +
                "           cipher,\n" +
                "           paycipher,\n" +
                "           egame,\n" +
                "           mobilephone,\n" +
                "           imsi,\n" +
                "           modelid,\n" +
                "           checkphone,\n" +
                "           email,\n" +
                "           checkemail,\n" +
                "           tvmac,\n" +
                "           psncode,\n" +
                "           itvcode,\n" +
                "           platformid,\n" +
                "           provinceid,\n" +
                "           cityid,\n" +
                "           phonekey,\n" +
                "           emailkey,\n" +
                "           accountstatus,\n" +
                "           realname,\n" +
                "           cardno,\n" +
                "           nickname,\n" +
                "           gender,\n" +
                "           headportrait,\n" +
                "           isdefault,\n" +
                "           birthday,\n" +
                "           homecity,\n" +
                "           province,\n" +
                "           city,\n" +
                "           typeoffigure,\n" +
                "           bloodgroup,\n" +
                "           standing,\n" +
                "           maritalstatus,\n" +
                "           platitude,\n" +
                "           interestobject,\n" +
                "           interestrange,\n" +
                "           linkphone,\n" +
                "           telephone,\n" +
                "           linkemail,\n" +
                "           address,\n" +
                "           qqcode,\n" +
                "           wbcode,\n" +
                "           msncode,\n" +
                "           workunit,\n" +
                "           schoolid,\n" +
                "           schoolname,\n" +
                "           homepage,\n" +
                "           comments,\n" +
                "           friendship,\n" +
                "           searched,\n" +
                "           footmark,\n" +
                "           message,\n" +
                "           greeting,\n" +
                "           sharing,\n" +
                "           showage,\n" +
                "           showlocation,\n" +
                "           channelcode,\n" +
                "           sourceurl,\n" +
                "           fromer,\n" +
                "           actiontime,\n" +
                "           inserttime,\n" +
                "           updatetime,\n" +
                "           lasttwonum\n" +
                "      from tb_user_register@to_gamedb t\n" +
                "     where t.inserttime >= to_date(v_date, 'yyyymmdd')\n" +
                "       and t.inserttime < to_date(v_date, 'yyyymmdd') + 1;\n" +
                "       \n" +
                "    execute immediate replace(replace(v_sql,':1',v_begintime),':2',v_endtime);\n" +
                "    execute immediate replace(replace(v_sql2,':1',v_begintime),':2',v_endtime);\n" +
                "\n" +
                "    sp_proc_run_log('update',v_log_id,null,null,null,null,0,sqlcode,sql%rowcount,null,null);\n" +
                "    commit;\n" +
                "exception\n" +
                "    when others then\n" +
                "         sp_proc_run_log('update',v_log_id,null,null,null,null,-1,substr(sqlerrm,1,200),null,null,null);\n" +
                "end;";

        //System.out.println(sqlStmt.strStmtDetail);

        ProcCalledRely procCalledRely = new ProcCalledRely(sqlStmt.strTBName, sqlStmt.strStmtDetail);
        System.out.println("MainProc is:" + procCalledRely.mainProc);
        System.out.print("\n");
        for (String e : procCalledRely.subProc) {
            System.out.println("        SubProc is:" + e);
        }
        System.out.print("\n");
        for (String key : procCalledRely.tbMap.keySet()) {
            System.out.println("        SubTable is:" + key + "\n               and the rely type is: " + procCalledRely.tbMap.get(key));
        }

        //System.out.println("主过程名：" + procCalledRely.mainProc);
        /*
        Iterator it1 = procCalledRely.subProc.iterator();
        while (it1.hasNext()) {
            System.out.println("子过程名：" + it1.next());
        }
        */
        //System.out.println("Table Name is : \n" + sqlStmt.strTBName);
        //System.out.println("The SQL is : \n" + sqlStmt.strStmtDetail);
    }
}
