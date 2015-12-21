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
        //遍历mainProcStmt，解析出所有procedure和package
        for (sIdx = 0; sIdx <= mainProcStmt.length() - 4; sIdx++) {
            if (mainProcStmt.substring(sIdx, sIdx + "pkg_".length()).equals("pkg_")
                    || mainProcStmt.substring(sIdx, sIdx + "sp_".length()).equals("sp_")) {
                for (eIdx = sIdx; eIdx <= mainProcStmt.length() - 4; eIdx++) {
                    matcher = pattern.matcher(mainProcStmt.substring(eIdx, eIdx + 1));
                    flag = matcher.matches();
                    if (!flag) {
                        //排除指定procedure和package
                        if (!(mainProcStmt.substring(sIdx, eIdx).equals("sp_run_task")
                                || (mainProcStmt.substring(sIdx, eIdx).equals("pkg_module_parallel"))
                                || (mainProcStmt.substring(sIdx, eIdx).equals("sp_proc_run_log"))
                                || (mainProcStmt.substring(sIdx, eIdx).equals("sp_parallel_run"))
                                || (mainProcStmt.substring(sIdx, eIdx).equals(mainProcName)))) {
                            //arraylist需要去重
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
        Pattern pattern = Pattern.compile("\\w|_");
        Matcher matcher;
        boolean flag;
        //第一步先筛选出所有括号内内容
        for (sIdx = 0; sIdx <= mainProcStmt.length() - 1; sIdx++) {
            if (mainProcStmt.substring(sIdx, sIdx + 1).equals("(")) {
                //blockFlag表示括号范围的进出
                int blockFlag = 0;
                for (eIdx = sIdx; eIdx <= mainProcStmt.length() - 1; eIdx++) {
                    if (mainProcStmt.substring(eIdx, eIdx + 1).equals("(")) {
                        blockFlag = blockFlag + 1;
                    } else if (mainProcStmt.substring(eIdx, eIdx + 1).equals(")")) {
                        blockFlag = blockFlag - 1;
                        //blockFlag为0时表示这一段括号解析完毕
                        if (blockFlag == 0) {
                            String tmpBlockStmt = mainProcStmt.substring(sIdx + 1, eIdx);
                            //去除括号内内容
                            mainProcStmt = mainProcStmt.replace("(" + tmpBlockStmt + ")", "()");
                            //迭代解析括号内括号
                            //System.out.println(tmpBlockStmt);
                            tmpMap.putAll(this.tbRetrieve(tmpBlockStmt));
                            break;
                        }
                    }
                }
            }
        }
        //System.out.println(mainProcStmt);

        //对去除括号内内容的sql语句开始进行语法解析。
        //解析insert类table。包括insert into, merge into, update三类。
        for (sIdx = 0; sIdx <= mainProcStmt.length() - 12; sIdx++) {
            //insert
            if (mainProcStmt.substring(sIdx, sIdx + "insert into ".length()).equals("insert into ")) {
                for (eIdx = sIdx + "insert into ".length(); eIdx <= mainProcStmt.length() - 1; eIdx++) {
                    matcher = pattern.matcher(mainProcStmt.substring(eIdx, eIdx + 1));
                    flag = matcher.matches();
                    if (!flag) {
                        tmpMap.put(mainProcStmt.substring(sIdx + "insert into ".length(), eIdx), "insert");
                        //System.out.println(mainProcStmt.substring(sIdx + "insert into ".length(), eIdx));
                        break;
                    }
                }
            }
            //merge
            if (mainProcStmt.substring(sIdx, sIdx + "merge into ".length()).equals("merge into ")) {
                for (eIdx = sIdx + "merge into ".length(); eIdx <= mainProcStmt.length() - 1; eIdx++) {
                    matcher = pattern.matcher(mainProcStmt.substring(eIdx, eIdx + 1));
                    flag = matcher.matches();
                    if (!flag) {
                        tmpMap.put(mainProcStmt.substring(sIdx + "merge into ".length(), eIdx), "insert");
                        //System.out.println(mainProcStmt.substring(sIdx + "merge into ".length(), eIdx));
                        break;
                    }
                }
            }
            //update
            if (mainProcStmt.substring(sIdx, sIdx + "update ".length()).equals("update ")) {
                for (eIdx = sIdx + "update ".length(); eIdx <= mainProcStmt.length() - 1; eIdx++) {
                    matcher = pattern.matcher(mainProcStmt.substring(eIdx, eIdx + 1));
                    flag = matcher.matches();
                    if (!flag) {
                        tmpMap.put(mainProcStmt.substring(sIdx + "update ".length(), eIdx), "insert");
                        //System.out.println(mainProcStmt.substring(sIdx + "update ".length(), eIdx));
                        break;
                    }
                }
            }
        }

        //解析from类table。包括join, using, from三类。
        for (sIdx = 0; sIdx < mainProcStmt.length() - 10; sIdx++) {
            //using
            if (mainProcStmt.substring(sIdx, sIdx + "using ".length()).equals("using ")) {
                for (eIdx = sIdx + "using ".length(); eIdx < mainProcStmt.length() - 10; eIdx++) {
                    if (mainProcStmt.substring(eIdx, eIdx + " on".length()).equals(" on")) {
                        String tmpString = mainProcStmt.substring(sIdx + "using ".length(), eIdx);
                        String[] tmpSplitAll = tmpString.split(",");
                        for (String e : tmpSplitAll) {
                            String[] tmpSplitEm = e.trim().split(" ");
                            if (!(tmpSplitEm[0].trim().equals("dual")
                                    || tmpSplitEm[0].trim().equals("()"))) {
                                //System.out.println(tmpSplitEm[0].trim());
                                tmpMap.put(tmpSplitEm[0].trim(), "from");
                            }
                        }
                        break;
                    }
                }
            }

            //join
            if (mainProcStmt.substring(sIdx, sIdx + "join ".length()).equals("join ")) {
                for (eIdx = sIdx + "join ".length(); eIdx < mainProcStmt.length() - 10; eIdx++) {
                    if (mainProcStmt.substring(eIdx, eIdx + " on".length()).equals(" on")) {
                        String tmpString = mainProcStmt.substring(sIdx + "join ".length(), eIdx);
                        String[] tmpSplitAll = tmpString.split(" ");
                        if (!(tmpSplitAll[0].trim().equals("dual")
                                || tmpSplitAll[0].trim().equals("()"))) {
                            tmpMap.put(tmpSplitAll[0].trim(), "from");
                        }
                        break;
                    }
                }
            }

            //from
            if (mainProcStmt.substring(sIdx, sIdx + "from ".length()).equals("from ")) {
                for (eIdx = sIdx + "from ".length(); eIdx < mainProcStmt.length() - 10; eIdx++) {
                    if (mainProcStmt.substring(eIdx, eIdx + " join".length()).equals(" join")) {
                        String tmpString = mainProcStmt.substring(sIdx + "from ".length(), eIdx);
                        String[] tmpSplitAll = tmpString.split(" ");
                        if (!(tmpSplitAll[0].trim().equals("dual")
                                || tmpSplitAll[0].trim().equals("()"))) {
                            //System.out.println(tmpSplitAll[0].trim());
                            tmpMap.put(tmpSplitAll[0].trim(), "from");
                        }
                        break;
                    } else if (mainProcStmt.substring(eIdx, eIdx + "where".length()).equals("where")) {
                        String tmpString = mainProcStmt.substring(sIdx + "from ".length(), eIdx);
                        String[] tmpSplitAll = tmpString.split(",");
                        for (String e : tmpSplitAll) {
                            String[] tmpSplitEm = e.trim().split(" ");
                            if (!(tmpSplitEm[0].trim().equals("dual")
                                    || tmpSplitEm[0].trim().equals("()"))) {
                                //System.out.println(tmpSplitEm[0].trim());
                                tmpMap.put(tmpSplitEm[0].trim(), "from");
                            }
                        }
                        break;
                    } else if (mainProcStmt.substring(eIdx, eIdx + ";".length()).equals(";")) {
                        String tmpString = mainProcStmt.substring(sIdx + "from ".length(), eIdx);
                        String[] tmpSplitAll = tmpString.split(",");
                        for (String e : tmpSplitAll) {
                            String[] tmpSplitEm = e.trim().split(" ");
                            if (!(tmpSplitEm[0].trim().equals("dual")
                                    || tmpSplitEm[0].trim().equals("()"))) {
                                //System.out.println(tmpSplitEm[0].trim());
                                tmpMap.put(tmpSplitEm[0].trim(), "from");
                            }
                        }
                        break;
                    }
                }
            }
        }
        return tmpMap;
    }


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

