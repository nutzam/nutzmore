package org.nutz.plugins.undertow.query;

public enum SORT {

    /**
     * 从小到大
     */
    ASC(1),
    /**
     * 从大到小
     */
    DESC(-1);

    private int value = 0;

    SORT(int v) {
        this.value = v;
    }

    public static SORT valueOf(int v) {
        switch (v) {
        case 1:
            return ASC;
        case -1:
            return DESC;
        default:
            return ASC;
        }
    }

    public int value() {
        return value;
    }

}
