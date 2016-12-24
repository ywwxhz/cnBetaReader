package com.ywwxhz.lib;

import java.util.LinkedList;
import java.util.List;

/**
 * 屏蔽指定元素
 * <p>
 * Created by 远望の无限(ywwxhz) on 2016 2016/11/29 20:51.
 */

public class BlockList {
    private static List<String> removeList = new LinkedList<>();

    private BlockList() {
    }

    /**
     * 获取屏蔽列表
     * 
     * @return 屏蔽列表实例
     */
    public static List<String> getRemoveList() {
        return removeList;
    }

    /**
     * 获取屏蔽列表长度
     * 
     * @return 屏蔽列表长度
     */
    public static int size() {
        return removeList.size();
    }

    /**
     * 获取屏蔽列表元素
     * 
     * @param i
     *            元素位置
     * @return 元素
     */
    public static String get(int i) {
        return removeList.get(i);
    }

    /**
     * 更新屏蔽列表
     * 
     * @param list
     *            新列表
     */
    public static void updateList(List<String> list) {
        removeList.clear();
        removeList.addAll(list);
    }
}
