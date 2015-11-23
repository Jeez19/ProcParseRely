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
        this.subProc = this.spRetrieve(this.mainProcStmt);
        this.tbMap = this.tbRetrieve(this.mainProcStmt);
    }

    ArrayList spRetrieve(String mainProcStmt) {
        ArrayList tmpList = new ArrayList();
        int startIdx;
        int endIdx;
        for (startIdx = 0; startIdx <= mainProcStmt.length() - " pkg_module_parallel".length(); startIdx++) {
            if ((mainProcStmt.substring(startIdx, startIdx + " pkg_".length()).equals(" pkg_")
                    || mainProcStmt.substring(startIdx, startIdx + "'pkg_".length()).equals("'pkg_"))
                    && !mainProcStmt.substring(startIdx, startIdx + " pkg_module_parallel".length()).equals(" pkg_module_parallel")) {
                for (endIdx = startIdx; endIdx <= mainProcStmt.length() - " pkg_module_parallel".length(); endIdx++) {
                    if (mainProcStmt.substring(endIdx, endIdx + ".".length()).equals(".")
                            || mainProcStmt.substring(endIdx, endIdx + "(".length()).equals("(")) {
                        tmpList.add(mainProcStmt.substring(startIdx + 1, endIdx));
                        //System.out.println("包名为：" + mainProcStmt.substring(startIdx + 1, endIdx));
                        break;
                    }
                }
            }
        }

        for (startIdx = 0; startIdx <= mainProcStmt.length() - " pkg_module_parallel".length(); startIdx++) {
            if (mainProcStmt.substring(startIdx, startIdx + " sp_".length()).equals(" sp_")
                    || mainProcStmt.substring(startIdx, startIdx + "'sp_".length()).equals("'sp_")) {
                for (endIdx = startIdx; endIdx <= mainProcStmt.length() - " pkg_module_parallel".length(); endIdx++) {
                    if (mainProcStmt.substring(endIdx, endIdx + ";".length()).equals(";")
                            || mainProcStmt.substring(endIdx, endIdx + "(".length()).equals("(")
                            || mainProcStmt.substring(endIdx, endIdx + "')".length()).equals("')")) {
                        if (mainProcStmt.substring(startIdx + 1, endIdx).indexOf(this.mainProc) == -1) {
                            tmpList.add(mainProcStmt.substring(startIdx + 1, endIdx));
                            //System.out.println("过程名为：" + mainProcStmt.substring(startIdx + 1, endIdx));
                            break;
                        }
                    }
                }
            }
        }


        return tmpList;
    }

    Map tbRetrieve(String mainProcStmt) {
        Map tmpMap = new HashMap();
        int startIdx;
        int endIdx;

        for (startIdx = 0; startIdx <= mainProcStmt.length() - 20; startIdx++) {
            if (mainProcStmt.substring(startIdx, startIdx + "insert into ".length()).equals("insert into ")) {
                for (endIdx = startIdx + "insert into ".length(); endIdx <= mainProcStmt.length() - " ".length(); endIdx++) {
                    if (mainProcStmt.substring(endIdx, endIdx + " ".length()).equals(" ")
                            || mainProcStmt.substring(endIdx, endIdx + "(".length()).equals("(")) {

                        break;
                    }
                }
            }

            if (mainProcStmt.substring(startIdx, startIdx + "merge into ".length()).equals("merge into ")) {
                for (endIdx = startIdx + "merge into ".length(); endIdx <= mainProcStmt.length() - " ".length(); endIdx++) {
                    if (mainProcStmt.substring(endIdx, endIdx + " ".length()).equals(" ")
                            || mainProcStmt.substring(endIdx, endIdx + "(".length()).equals("(")) {

                        break;
                    }
                }
            }

            if (mainProcStmt.substring(startIdx, startIdx + "update ".length()).equals("update ")) {
                for (endIdx = startIdx + "update ".length(); endIdx <= mainProcStmt.length() - " ".length(); endIdx++) {
                    if (mainProcStmt.substring(endIdx, endIdx + " ".length()).equals(" ")
                            || mainProcStmt.substring(endIdx, endIdx + "(".length()).equals("(")) {

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
                        String tmpStmt = mainProcStmt.substring(startIdx + "from ".length(), endIdx);
                        if (tmpStmt.contains(",")) {
                            System.out.println("from to where: " + tmpStmt);
                            String[] tmpSubStr = tmpStmt.split(",");
                            for (String e : tmpSubStr) {
                                System.out.println("    these is : " + e.trim());
                                e = e.trim();
                                if (!e.contains(" ")) {
                                    System.out.println("        So the subTable is:" + e);
                                } else {
                                    for (int i = 0; i < e.length(); i++) {
                                        if (e.substring(i, i + " ".length()).equals(" ")
                                                || e.substring(i, i + "\n".length()).equals("\n")) {
                                            System.out.println("        So the subTable is:" + e.substring(0, i));
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

                        break;
                    }
                }
            }
        }

        return tmpMap;
    }
}
