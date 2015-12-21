package com.zexin.procparserely;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Zexin on 2015/11/13.
 */
public class ProcCalledRely {
    String mainProcName;
    String mainProcStmt;
    ArrayList<String> subProc;
    Map<String, String> tbMap;

    ProcCalledRely(String procName, String procStmt) {
        this.mainProcName = procName;
        this.mainProcStmt = procStmt.toLowerCase();
        this.subProc = this.spRetrieve(this.mainProcStmt);
        this.tbMap = this.tbRetrieve(this.mainProcStmt);
    }

    ArrayList<String> spRetrieve(String mainProcStmt) {
        ArrayList<String> tmpList = new ArrayList<String>();
        int sIdx;
        int eIdx;
        Pattern pattern = Pattern.compile("\\w|_");
        Matcher matcher;
        boolean flag;
        for (sIdx = 0; sIdx <= mainProcStmt.length() - 4; sIdx++) {
            if (mainProcStmt.substring(sIdx, sIdx + "pkg_".length()).equals("pkg_")
                    || mainProcStmt.substring(sIdx, sIdx + "sp_".length()).equals("sp_")) {
                for (eIdx = sIdx; eIdx <= mainProcStmt.length() - 4; eIdx++) {
                    matcher = pattern.matcher(mainProcStmt.substring(eIdx, eIdx + 1));
                    flag = matcher.matches();
                    if (!flag) {
                        if (!(mainProcStmt.substring(sIdx, eIdx).equals("sp_run_task")
                                || (mainProcStmt.substring(sIdx, eIdx).equals("pkg_module_parallel"))
                                || (mainProcStmt.substring(sIdx, eIdx).equals("sp_parallel_run")))) {
                            if (!tmpList.contains(mainProcStmt.substring(sIdx, eIdx))) {
                                tmpList.add(mainProcStmt.substring(sIdx, eIdx));
                            }
                        }
                        break;
                    }
                }
            }
        }

        return tmpList;
    }


    Map<String, String> tbRetrieve(String mainProcStmt) {
        Map<String, String> tmpMap = new HashMap<String, String>();
        int sIdx;
        int eIdx;
        int blockFlag = 0;
        for (sIdx = 0; sIdx <= mainProcStmt.length() - 1; sIdx++) {
            if (mainProcStmt.substring(sIdx, sIdx + 1).equals("(")) {
                blockFlag = blockFlag + 1;
                for (eIdx = sIdx; eIdx <= mainProcStmt.length() - 1; eIdx++) {
                    if (mainProcStmt.substring(eIdx, eIdx + 1).equals("(")) {
                        blockFlag = blockFlag + 1;
                    } else if (mainProcStmt.substring(eIdx, eIdx + 1).equals(")")) {
                        blockFlag = blockFlag - 1;
                        if (blockFlag == 0) {
                            String tmpBlockStmt = mainProcStmt.substring(sIdx + 1, eIdx);
                            System.out.println(tmpBlockStmt);
                            mainProcStmt = mainProcStmt.replace(tmpBlockStmt, "");
                            break;
                        }
                    }
                }
            }
        }

        return tmpMap;
    }
    /*
    ArrayList<String> spRetrieve(String mainProcStmt) {
        ArrayList<String> tmpList = new ArrayList<String>();
        int startIdx;
        int endIdx;
        for (startIdx = 0; startIdx <= mainProcStmt.length() - " pkg_module_parallel".length(); startIdx++) {
            if ((mainProcStmt.substring(startIdx, startIdx + " pkg_".length()).equals(" pkg_")
                    || mainProcStmt.substring(startIdx, startIdx + "'pkg_".length()).equals("'pkg_"))
                    && !mainProcStmt.substring(startIdx, startIdx + " pkg_module_parallel".length()).equals(" pkg_module_parallel")) {
                for (endIdx = startIdx; endIdx <= mainProcStmt.length() - " pkg_module_parallel".length(); endIdx++) {
                    if (mainProcStmt.substring(endIdx, endIdx + ".".length()).equals(".")
                            || mainProcStmt.substring(endIdx, endIdx + "(".length()).equals("(")) {
                        if (!tmpList.contains(mainProcStmt.substring(startIdx + 1, endIdx))) {
                            tmpList.add(mainProcStmt.substring(startIdx + 1, endIdx));
                        }
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
                            if (!tmpList.contains(mainProcStmt.substring(startIdx + 1, endIdx))) {
                                tmpList.add(mainProcStmt.substring(startIdx + 1, endIdx));
                            }
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
        Map<String, String> tmpMap = new HashMap<String, String>();
        int startIdx;
        int endIdx;

        for (startIdx = 0; startIdx <= mainProcStmt.length() - 20; startIdx++) {
            if (mainProcStmt.substring(startIdx, startIdx + "insert into ".length()).equals("insert into ")) {
                for (endIdx = startIdx + "insert into ".length(); endIdx <= mainProcStmt.length() - " ".length(); endIdx++) {
                    if (mainProcStmt.substring(endIdx, endIdx + " ".length()).equals(" ")
                            || mainProcStmt.substring(endIdx, endIdx + "(".length()).equals("(")
                            || mainProcStmt.substring(endIdx, endIdx + "\n".length()).equals("\n")) {
                        //System.out.println("insert: " + mainProcStmt.substring(startIdx + "insert into ".length(), endIdx));
                        if (!(tmpMap.containsKey(mainProcStmt.substring(startIdx + "insert into ".length(), endIdx))
                                && tmpMap.containsValue("insert"))) {
                            tmpMap.put(mainProcStmt.substring(startIdx + "insert into ".length(), endIdx), "insert");
                        }
                        break;
                    }
                }
            }

            if (mainProcStmt.substring(startIdx, startIdx + "merge into ".length()).equals("merge into ")) {
                for (endIdx = startIdx + "merge into ".length(); endIdx <= mainProcStmt.length() - " ".length(); endIdx++) {
                    if (mainProcStmt.substring(endIdx, endIdx + " ".length()).equals(" ")
                            || mainProcStmt.substring(endIdx, endIdx + "(".length()).equals("(")
                            || mainProcStmt.substring(endIdx, endIdx + "\n".length()).equals("\n")) {
                        //System.out.println("merge: " + mainProcStmt.substring(startIdx + "merge into ".length(), endIdx));
                        if (!(tmpMap.containsKey(mainProcStmt.substring(startIdx + "merge into ".length(), endIdx))
                                && tmpMap.containsValue("insert"))) {
                            tmpMap.put(mainProcStmt.substring(startIdx + "merge into ".length(), endIdx), "insert");
                        }
                        break;
                    }
                }
            }

            if (mainProcStmt.substring(startIdx, startIdx + "update ".length()).equals("update ")) {
                for (endIdx = startIdx + "update ".length(); endIdx <= mainProcStmt.length() - " ".length(); endIdx++) {
                    if (mainProcStmt.substring(endIdx, endIdx + " ".length()).equals(" ")
                            || mainProcStmt.substring(endIdx, endIdx + "(".length()).equals("(")
                            || mainProcStmt.substring(endIdx, endIdx + "\n".length()).equals("\n")) {
                        //System.out.println("update: " + mainProcStmt.substring(startIdx + "update ".length(), endIdx));
                        if (!(tmpMap.containsKey(mainProcStmt.substring(startIdx + "update ".length(), endIdx))
                                && tmpMap.containsValue("insert"))) {
                            tmpMap.put(mainProcStmt.substring(startIdx + "update ".length(), endIdx), "insert");
                        }
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
            /*
            if (mainProcStmt.substring(startIdx, startIdx + "from ".length()).equals("from ")) {
                for (endIdx = startIdx + "from ".length(); endIdx <= mainProcStmt.length() - "where ".length(); endIdx++) {
                    if (mainProcStmt.substring(endIdx, endIdx + "where ".length()).equals("where ")
                            || mainProcStmt.substring(endIdx, endIdx + ";".length()).equals(";")) {
                        String tmpStmt = mainProcStmt.substring(startIdx + "from ".length(), endIdx);
                        //System.out.println("from to where: " + tmpStmt);
                        if (tmpStmt.contains(",")) {
                            //System.out.println("from to where: " + tmpStmt);
                            //System.out.println(tmpStmt.indexOf(" "));
                            String[] tmpSubStr = tmpStmt.split(",");
                            for (String e : tmpSubStr) {
                                //System.out.println("    these is : " + e.trim());
                                e = e.trim();
                                if (!e.contains(" ")) {
                                    //System.out.println("        So the subTable is:" + e);
                                    if (!(tmpMap.containsKey(e)
                                            && tmpMap.containsValue("from"))) {
                                        tmpMap.put(e, "from");
                                    }
                                } else {
                                    for (int i = 0; i < e.length(); i++) {
                                        if (e.substring(i, i + " ".length()).equals(" ")
                                                || e.substring(i, i + "\n".length()).equals("\n")) {
                                            //System.out.println("        So the subTable is:" + e.substring(0, i));
                                            if (!(tmpMap.containsKey(e.substring(0, i))
                                                    && tmpMap.containsValue("from"))) {
                                                tmpMap.put(e.substring(0, i), "from");
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                        } else {
                            String e = tmpStmt;
                            for (int i = 0; i < e.length(); i++) {
                                if (e.substring(i, i + " ".length()).equals(" ")
                                        || e.substring(i, i + "\n".length()).equals("\n")) {
                                    //System.out.println("        So the subTable is:" + e.substring(0, i));
                                    if (!(tmpMap.containsKey(e.substring(0, i))
                                            && tmpMap.containsValue("from"))) {
                                        tmpMap.put(e.substring(0, i), "from");
                                    }
                                    break;
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
                            || mainProcStmt.substring(endIdx, endIdx + ";".length()).equals(";")
                            || mainProcStmt.substring(endIdx, endIdx + "\n".length()).equals("\n"))
                            && !mainProcStmt.substring(startIdx + "using ".length(), startIdx + "using ".length() + 1).equals("(")) {
                        //System.out.println("using: " + mainProcStmt.substring(startIdx + "using ".length(), endIdx));
                        if (!(tmpMap.containsKey(mainProcStmt.substring(startIdx + "using ".length(), endIdx))
                                && tmpMap.containsValue("from"))) {
                            tmpMap.put(mainProcStmt.substring(startIdx + "using ".length(), endIdx), "from");
                        }
                        break;
                    }
                }
            }

            if (mainProcStmt.substring(startIdx, startIdx + "join ".length()).equals("join ")) {
                for (endIdx = startIdx + "join ".length(); endIdx <= mainProcStmt.length() - " ".length(); endIdx++) {
                    if ((mainProcStmt.substring(endIdx, endIdx + " ".length()).equals(" ")
                            || mainProcStmt.substring(endIdx, endIdx + ")".length()).equals(")")
                            || mainProcStmt.substring(endIdx, endIdx + ";".length()).equals(";")
                            || mainProcStmt.substring(endIdx, endIdx + "\n".length()).equals("\n"))
                            && !mainProcStmt.substring(startIdx + "join ".length(), startIdx + "join ".length() + 1).equals("(")) {
                        //System.out.println("join: " + mainProcStmt.substring(startIdx + "join ".length(), endIdx));
                        if (!(tmpMap.containsKey(mainProcStmt.substring(startIdx + "join ".length(), endIdx))
                                && tmpMap.containsValue("from"))) {
                            tmpMap.put(mainProcStmt.substring(startIdx + "join ".length(), endIdx), "from");
                        }
                        break;
                    }
                }
            }
        }

        return tmpMap;
    }
    */
}
