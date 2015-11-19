package com.zexin.procparserely;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Zexin on 2015/11/13.
 */
public class ProcCalledRely {
    String mainProc;
    String mainProcStmt;
    ArrayList subProc;
    Map tbMap;

    ProcCalledRely(String procName, String procStmt) {
        this.mainProc = procName;
        this.mainProcStmt = procStmt.toLowerCase();
        //System.out.println("过程为：\n" + this.mainProcStmt);
        //System.out.println("过程字符长度为：" + this.mainProcStmt.length());
        //this.subProc = this.spRetrieve(this.mainProcStmt);
        this.tbMap = this.tbRetrieve(this.mainProcStmt);
    }

    ArrayList spRetrieve(String mainProcStmt) {
        ArrayList tmpList = new ArrayList();
        int startIdx;
        int endIdx;
//        String strPkg = " pkg_";
//        String strPkgL = "'pkg_";
//        String strNotPkg = " pkg_module_parallel";
//        String strSp = " sp_";
//        String strSpL = "'sp_";
//        //String strNotSp = " sp_parallel_run";
//        //String strNotSpRunTask = " sp_run_task";
//        String strPoint = ".";
//        String strBraF = "(";
//        String strBraA = "')";
//        String strSem = ";";
        int pkgcnt = 1;
        int spcnt = 1;
        for (startIdx = 0; startIdx <= mainProcStmt.length() - " pkg_module_parallel".length(); startIdx++) {
            if ((mainProcStmt.substring(startIdx, startIdx + " pkg_".length()).equals(" pkg_")
                    || mainProcStmt.substring(startIdx, startIdx + "'pkg_".length()).equals("'pkg_"))
                    && !mainProcStmt.substring(startIdx, startIdx + " pkg_module_parallel".length()).equals(" pkg_module_parallel")) {
                for (endIdx = startIdx; endIdx <= mainProcStmt.length() - " pkg_module_parallel".length(); endIdx++) {
                    if (mainProcStmt.substring(endIdx, endIdx + ".".length()).equals(".")
                            || mainProcStmt.substring(endIdx, endIdx + "(".length()).equals("(")) {
                        tmpList.add(mainProcStmt.substring(startIdx + 1, endIdx));
                        //System.out.println("startIdx: " + startIdx);
                        //System.out.println("endIdx: " + endIdx);
                        System.out.println("包名为：" + mainProcStmt.substring(startIdx + 1, endIdx));
                        pkgcnt++;
                        break;
                    }
                }
            }
        }
        System.out.println(pkgcnt);

        //System.out.println("\n\n\n\n\n我草你爸爸！\n\n\n\n\n");
        for (startIdx = 0; startIdx <= mainProcStmt.length() - " pkg_module_parallel".length(); startIdx++) {
            if (mainProcStmt.substring(startIdx, startIdx + " sp_".length()).equals(" sp_")
                    || mainProcStmt.substring(startIdx, startIdx + "'sp_".length()).equals("'sp_")) {
                for (endIdx = startIdx; endIdx <= mainProcStmt.length() - " pkg_module_parallel".length(); endIdx++) {
                    if (mainProcStmt.substring(endIdx, endIdx + ";".length()).equals(";")
                            || mainProcStmt.substring(endIdx, endIdx + "(".length()).equals("(")
                            || mainProcStmt.substring(endIdx, endIdx + "')".length()).equals("')")) {
                        if (mainProcStmt.substring(startIdx + 1, endIdx).indexOf(this.mainProc) == -1) {
                            tmpList.add(mainProcStmt.substring(startIdx + 1, endIdx));
                            //System.out.println("startIdx: " + startIdx);
                            //System.out.println("endIdx: " + endIdx);
                            System.out.println("过程名为：" + mainProcStmt.substring(startIdx + 1, endIdx));
                            spcnt++;
                            break;
                        }
                    }
                }
            }
        }
        System.out.println(spcnt);


        return tmpList;
    }

    Map tbRetrieve(String mainProcStmt) {
        Map tmpMap = new HashMap();
        int startIdx;
        int endIdx;
//        String strInsert = "insert into ";
//        String strMerge = "merge into ";
//        String strFrom = "from ";
//        String strUsing = "using";
//        String strJoin = "join ";
//        String strBraB = "(";
//        String strBlk = " ";
//        String strNotFrom = "dual";


        for (startIdx = 0; startIdx <= mainProcStmt.length() - 20; startIdx++) {
            if (mainProcStmt.substring(startIdx, startIdx + "insert into ".length()).equals("insert into ")) {
                for (endIdx = startIdx + "insert into ".length(); endIdx <= mainProcStmt.length() - " ".length(); endIdx++) {
                    if (mainProcStmt.substring(endIdx, endIdx + " ".length()).equals(" ")
                            || mainProcStmt.substring(endIdx, endIdx + "(".length()).equals("(")) {
                        //System.out.println("insert: " + mainProcStmt.substring(startIdx + "insert into ".length(), endIdx));
                        break;
                    }
                }
            }

            if (mainProcStmt.substring(startIdx, startIdx + "merge into ".length()).equals("merge into ")) {
                for (endIdx = startIdx + "merge into ".length(); endIdx <= mainProcStmt.length() - " ".length(); endIdx++) {
                    if (mainProcStmt.substring(endIdx, endIdx + " ".length()).equals(" ")
                            || mainProcStmt.substring(endIdx, endIdx + "(".length()).equals("(")) {
                        //System.out.println("merge: " + mainProcStmt.substring(startIdx + "merge into ".length(), endIdx));
                        break;
                    }
                }
            }

            if (mainProcStmt.substring(startIdx, startIdx + "update ".length()).equals("update ")) {
                for (endIdx = startIdx + "update ".length(); endIdx <= mainProcStmt.length() - " ".length(); endIdx++) {
                    if (mainProcStmt.substring(endIdx, endIdx + " ".length()).equals(" ")
                            || mainProcStmt.substring(endIdx, endIdx + "(".length()).equals("(")) {
                        //System.out.println("update: " + mainProcStmt.substring(startIdx + "update ".length(), endIdx));
                        break;
                    }
                }
            }

            /*
            if (mainProcStmt.substring(startIdx, startIdx + "from ".length()).equals("from ")) {
                for (endIdx = startIdx + "from ".length(); endIdx <= mainProcStmt.length() - " ".length(); endIdx++) {
                    if ((mainProcStmt.substring(endIdx, endIdx + " ".length()).equals(" ")
                            || mainProcStmt.substring(endIdx, endIdx + ")".length()).equals(")")
                            || mainProcStmt.substring(endIdx, endIdx + ";".length()).equals(";"))
                            && !mainProcStmt.substring(startIdx + "from ".length(), startIdx + "from ".length() + 1).equals("(")) {
                        System.out.println("from: " + mainProcStmt.substring(startIdx + "from ".length(), endIdx));
                        break;
                    }
                }
            }
            */

            if (mainProcStmt.substring(startIdx, startIdx + "from ".length()).equals("from ")) {
                for (endIdx = startIdx + "from ".length(); endIdx <= mainProcStmt.length() - "where ".length(); endIdx++) {
                    if (mainProcStmt.substring(endIdx, endIdx + "where ".length()).equals("where ")
                            || mainProcStmt.substring(endIdx, endIdx + ";".length()).equals(";")) {
                        String tmpStmt = mainProcStmt.substring(startIdx, endIdx);
                        //System.out.println("from to where: " + tmpStmt);
                        if (tmpStmt.contains(",")) {
                            System.out.println("from to where: " + tmpStmt);
                            //System.out.println(tmpStmt.indexOf(" "));
                            for (int eIdx = "from ".length(); eIdx <= tmpStmt.length() - " ".length(); eIdx++) {
                                if (tmpStmt.substring(eIdx, eIdx + " ".length()).equals(" ")
                                        || tmpStmt.substring(eIdx, eIdx + ",".length()).equals(",")
                                        || tmpStmt.substring(eIdx, eIdx + ";".length()).equals(";")) {
                                    System.out.println("First: " + tmpStmt.substring("from ".length(), eIdx));
                                    break;
                                }
                            }

                            tmpStmt = tmpStmt.substring(tmpStmt.indexOf(","));
                            //System.out.println("cut: " + tmpStmt);
                            for (int sIdx = 1; sIdx <= tmpStmt.length() - ",".length(); sIdx++) {
                                if (!tmpStmt.substring(sIdx, sIdx + " ".length()).equals(" ")
                                        && !tmpStmt.substring(sIdx, sIdx + "\n".length()).equals("\n")) {
                                    System.out.println("非空格: " + tmpStmt.substring(sIdx));
                                    tmpStmt = tmpStmt.substring(sIdx);
                                    for (int eIdx = 0; eIdx <= tmpStmt.length() - " ".length(); eIdx++) {
                                        if (tmpStmt.substring(eIdx, eIdx + ",".length()).equals(",")
                                                || tmpStmt.substring(eIdx, eIdx + ";".length()).equals(";")
                                                || tmpStmt.substring(eIdx, eIdx + " ".length()).equals(" ")
                                                || tmpStmt.substring(eIdx, eIdx + "\n".length()).equals("\n")) {
                                            System.out.println("Followed: " + tmpStmt.substring(0, eIdx));
                                            if (tmpStmt.contains(",")) {
                                                tmpStmt = tmpStmt.substring(tmpStmt.indexOf(",", ",".length()));
                                                sIdx = tmpStmt.indexOf(",");
                                            }
                                            System.out.println("cut: " + tmpStmt);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        break;
                    }

                }
            }

            if (mainProcStmt.substring(startIdx, startIdx + "using ".length()).equals("using ")) {
                for (endIdx = startIdx + "using ".length(); endIdx <= mainProcStmt.length() - " ".length(); endIdx++) {
                    if ((mainProcStmt.substring(endIdx, endIdx + " ".length()).equals(" ")
                            || mainProcStmt.substring(endIdx, endIdx + ")".length()).equals(")")
                            || mainProcStmt.substring(endIdx, endIdx + ";".length()).equals(";"))
                            && !mainProcStmt.substring(startIdx + "using ".length(), startIdx + "using ".length() + 1).equals("(")) {
                        //System.out.println("using: " + mainProcStmt.substring(startIdx + "using ".length(), endIdx));
                        break;
                    }
                }
            }

            if (mainProcStmt.substring(startIdx, startIdx + "join ".length()).equals("join ")) {
                for (endIdx = startIdx + "join ".length(); endIdx <= mainProcStmt.length() - " ".length(); endIdx++) {
                    if ((mainProcStmt.substring(endIdx, endIdx + " ".length()).equals(" ")
                            || mainProcStmt.substring(endIdx, endIdx + ")".length()).equals(")")
                            || mainProcStmt.substring(endIdx, endIdx + ";".length()).equals(";"))
                            && !mainProcStmt.substring(startIdx + "join ".length(), startIdx + "join ".length() + 1).equals("(")) {
                        //System.out.println("join: " + mainProcStmt.substring(startIdx + "join ".length(), endIdx));
                        break;
                    }
                }
            }
        }

        return tmpMap;
    }
}
