package com.zexin.procparserely;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Zexin on 2015/11/13.
 */
public class ProcCalledRely {
    String mainProcName;
    String mainProcStmt;
    ArrayList<String> subProc;
    ArrayList<String> tbIntoMap;
    ArrayList<String> tbFromMap;

    ProcCalledRely(String procName, String procStmt) {
        this.mainProcName = procName;
        this.mainProcStmt = procStmt.toLowerCase();
        this.subProc = this.spRetrieve(this.mainProcStmt);
        this.tbIntoMap = this.tbIntoRetrieve(this.mainProcStmt);
        this.tbFromMap = this.tbFromRetrieve(this.mainProcStmt);
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

    //解析into类型的table
    ArrayList<String> tbIntoRetrieve(String mainProcStmt) {
        ArrayList<String> tmpArray = new ArrayList<String>();
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
                            tmpArray.addAll(this.tbIntoRetrieve(tmpBlockStmt));
                            break;
                        }
                    }
                }
            }
        }

        //对去除括号内内容的sql语句开始进行语法解析。
        //解析insert类table。包括insert into, merge into, update三类。
        for (sIdx = 0; sIdx <= mainProcStmt.length() - 12; sIdx++) {
            //insert
            if (mainProcStmt.substring(sIdx, sIdx + "insert into ".length()).equals("insert into ")) {
                for (eIdx = sIdx + "insert into ".length(); eIdx <= mainProcStmt.length() - 1; eIdx++) {
                    matcher = pattern.matcher(mainProcStmt.substring(eIdx, eIdx + 1));
                    flag = matcher.matches();
                    if (!flag) {
                        if (!tmpArray.contains(mainProcStmt.substring(sIdx + "insert into ".length(), eIdx))) {
                            tmpArray.add(mainProcStmt.substring(sIdx + "insert into ".length(), eIdx));
                        }
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
                        if (!tmpArray.contains(mainProcStmt.substring(sIdx + "merge into ".length(), eIdx))) {
                            tmpArray.add(mainProcStmt.substring(sIdx + "merge into ".length(), eIdx));
                        }
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
                        if (!tmpArray.contains(mainProcStmt.substring(sIdx + "update ".length(), eIdx))) {
                            tmpArray.add(mainProcStmt.substring(sIdx + "update ".length(), eIdx));
                        }
                        break;
                    }
                }
            }
        }
        return tmpArray;
    }

    //解析from类型的table
    ArrayList<String> tbFromRetrieve(String mainProcStmt) {
        ArrayList<String> tmpArray = new ArrayList<String>();
        int sIdx;
        int eIdx;
        //Pattern pattern = Pattern.compile("\\w|_");
        //Matcher matcher;
        //boolean flag;
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
                            tmpArray.addAll(this.tbFromRetrieve(tmpBlockStmt));
                            break;
                        }
                    }
                }
            }
        }

        //对去除括号内内容的sql语句开始进行语法解析。
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
                                if (!tmpArray.contains(tmpSplitEm[0].trim())) {
                                    tmpArray.add(tmpSplitEm[0].trim());
                                }
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
                            if (!tmpArray.contains(tmpSplitAll[0].trim())) {
                                tmpArray.add(tmpSplitAll[0].trim());
                            }
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
                            if (!tmpArray.contains(tmpSplitAll[0].trim())) {
                                tmpArray.add(tmpSplitAll[0].trim());
                            }
                        }
                        break;
                    } else if (mainProcStmt.substring(eIdx, eIdx + "where".length()).equals("where")) {
                        String tmpString = mainProcStmt.substring(sIdx + "from ".length(), eIdx);
                        String[] tmpSplitAll = tmpString.split(",");
                        for (String e : tmpSplitAll) {
                            String[] tmpSplitEm = e.trim().split(" ");
                            if (!(tmpSplitEm[0].trim().equals("dual")
                                    || tmpSplitEm[0].trim().equals("()"))) {
                                if (!tmpArray.contains(tmpSplitEm[0].trim())) {
                                    tmpArray.add(tmpSplitEm[0].trim());
                                }
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
                                if (!tmpArray.contains(tmpSplitEm[0].trim())) {
                                    tmpArray.add(tmpSplitEm[0].trim());
                                }
                            }
                        }
                        break;
                    }
                }
            }
        }
        return tmpArray;
    }

}

