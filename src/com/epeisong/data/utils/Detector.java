package com.epeisong.data.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.epeisong.utils.LogUtils;

public class Detector {

    public static boolean isBreak2(int start, List<Integer> list) {
        if (list == null || list.isEmpty()) {
            return false;
        }
        Collections.sort(list);
        Collections.reverse(list);
        return start - list.get(list.size() - 1) == list.size();
    }

    public static boolean isBreak(int start, List<Integer> list) {
        if (list == null || list.isEmpty()) {
            return false;
        }
        Collections.sort(list);
        Collections.reverse(list);
        if (start > 0 && start - list.get(0) > 1) {
            return true;
        }
        long last = 0;
        for (long i : list) {
            if (last == 0) {
                last = i;
            } else {
                if (last - i > 1) {
                    return true;
                }
                last = i;
            }
        }
        return false;
    }

    public static boolean isBreak(long start, List<Long> list) {
        if (list == null || list.isEmpty()) {
            return false;
        }
        Collections.sort(list);
        Collections.reverse(list);
        if (start > 0 && start - list.get(0) > 1) {
            return true;
        }
        long last = 0;
        for (long i : list) {
            if (last == 0) {
                last = i;
            } else {
                if (last - i > 1) {
                    return true;
                }
                last = i;
            }
        }
        return false;
    }

    public static List<Long> detect(long start, int size, List<Long> list) {
        Collections.sort(list);
        Collections.reverse(list);

        List<Long> keeps = new ArrayList<Long>();
        List<Long> lost = new ArrayList<Long>();
        out: for (Long item : list) {
            keeps.add(item);
            if (start == -1) {
                start = item;
            } else {
                if (start - item <= 1) {
                    start = item;
                    if (keeps.size() + lost.size() >= size) {
                        break out;
                    }
                } else {
                    while (item != --start && start > 0) {
                        lost.add(start);
                        if (keeps.size() + lost.size() >= size) {
                            break out;
                        }
                    }
                }
            }
        }
        LogUtils.t("keeps:" + keeps);
        LogUtils.t("lost:" + lost);
        return lost;
    }

    public static List<Integer> detect(Integer start, int size, List<Integer> list) {
        Collections.sort(list);
        Collections.reverse(list);

        List<Integer> keeps = new ArrayList<Integer>();
        List<Integer> lost = new ArrayList<Integer>();
        out: for (Integer item : list) {
            keeps.add(item);
            if (start == -1) {
                start = item;
            } else {
                if (start - item <= 1) {
                    start = item;
                    if (keeps.size() + lost.size() >= size) {
                        break out;
                    }
                } else {
                    while (item != --start && start > 0) {
                        lost.add(start);
                        if (keeps.size() + lost.size() >= size) {
                            break out;
                        }
                    }
                }
            }
        }
        LogUtils.t("keeps:" + keeps);
        LogUtils.t("lost:" + lost);
        return lost;
    }
}
