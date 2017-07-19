package org.nutz.plugins.zcron;

import java.util.ArrayList;
import java.util.List;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

/**
 * 标准 Cron 表达式的项目
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
class CrnStdItem implements CrnItem {

    /*---------------------------------------常量表-----*/
    /**
     * 所有的值都可以被匹配
     */
    final static int ANY = 0;

    /**
     * 表范围: values[1] 表示最小值, values[2] 表示最大值
     */
    final static int RANGE = 1;

    /**
     * 枚举: 从 values[1] 开始表示可以允许的值
     */
    final static int LIST = 2;

    /**
     * 步长: values[1] 表示起始值， values[2] 表示步长
     */
    final static int SPAN = 3;

    /**
     * 单值: values[1] 表示被精确匹配的值
     */
    final static int ONE = 4;

    final static int MOD_dd = 100;
    final static int MOD_ww = 1000;

    public CrnStdItem(CrnStdItem... prevs) {
        this.prevItems = prevs;
    }

    /**
     * 指向前一个逻辑上的表达式，比如，年前面应该是月，月前面应该是日和周
     * <p>
     * 这个主要用来<code>joinText</code>好缩减输入的 ANY 文字
     */
    protected CrnStdItem[] prevItems;

    /**
     * 数组表示一个值的范围:
     * 
     * <pre>
     *   [类型(ANY|RANGE|LIST|SPAN|ONE)], [值1], [值2] ...
     * </pre>
     * 
     * 值支持 -1，表示表达式中的 "L" 修饰符
     */
    protected int[] values;

    /**
     * 是否支持 "L" 标记
     */
    protected boolean supportLast;

    /**
     * 如果前面有 SPAN 类型，是否输出文本时是否忽略 0 值
     */
    protected boolean ignoreZeroWhenPrevHasSpan;

    public CrnStdItem setIgnoreZeroWhenPrevHasSpan(boolean ignore) {
        this.ignoreZeroWhenPrevHasSpan = ignore;
        return this;
    }

    /**
     * 如果前面都是 ANY 类型，是否输出文本时是否忽略 ANY 值
     */
    protected boolean ignoreAnyWhenPrevAllAny;

    public CrnStdItem setIgnoreAnyWhenPrevAllAny(boolean ignore) {
        this.ignoreAnyWhenPrevAllAny = ignore;
        return this;
    }

    /**
     * 支持从后面数，等特殊标示的值
     */
    protected boolean supportMOD;

    protected void parse(String str) {
        // 看看是不是 ANY
        if ("?".equals(str) || "*".equals(str)) {
            values = new int[]{ANY};
            return;
        }

        // 看看是不是列表
        String[] ss = str.split(",");
        if (ss.length > 1) {
            ArrayList<Integer> list = new ArrayList<Integer>();
            list.add(LIST);
            for (String s : ss) {
                // values[i++] = eval4override(s);
                String[] subs = s.split("-");
                if (subs.length > 1) {
                    list.add(RANGE);
                    list.add(eval4override(subs[0]));
                    list.add(eval4override(subs[1]));
                } else {
                    list.add(ONE);
                    list.add(eval4override(s));
                }
            }
            values = new int[list.size()];
            int i = 0;
            for (Integer n : list)
                values[i++] = n.intValue();
            return;
        }

        // 看看是不是步长
        ss = str.split("/");
        if (ss.length > 1) {
            values = new int[]{SPAN, eval4override(ss[0]), eval4override(ss[1])};
            return;
        }

        // 看看是不是范围
        ss = str.split("-");
        if (ss.length > 1) {
            values = new int[]{RANGE, eval4override(ss[0]), eval4override(ss[1])};
            return;
        }
        // 那么一定是固定值了
        values = new int[]{ONE, eval4override(str)};
    }

    public boolean isANY() {
        return ANY == this.values[0];
    }

    public boolean isSPAN() {
        return SPAN == this.values[0];
    }

    public boolean isPrevAllAny() {
        if (prevItems.length == 0)
            return false;
        for (CrnStdItem prev : prevItems)
            if (!prev.isANY())
                return false;
        return true;
    }

    public boolean isPrevHasSpan() {
        for (CrnStdItem prev : prevItems)
            if (prev.isSPAN())
                return true;
        return false;
    }

    private String _T(ZCroni18n.Item lc, ZCroni18n i18n, int n) {
        // 特殊的周 FRI#3
        if (this.supportMOD && n > MOD_ww) {
            int n0 = n % MOD_ww;
            int n1 = (n - n0) / MOD_ww;
            String s = lc.hasDict() ? lc.dict[n0 - 1] : lc.tmpl.replace("?", n0 + "");
            return i18n.N.replace("?", n1 + "") + s;
        }
        // 特殊值:工作日 W
        else if (this.supportMOD && n > (MOD_dd / 2)) {
            int n0 = n - MOD_dd;
            return n0 == 0 ? lc.Wonly : _T(lc, i18n, n0) + lc.W;
        }
        // 正常值
        else if (n >= 0) {
            return lc.hasDict() ? lc.dict[n - 1] : lc.tmpl.replace("?", n + "");
        }
        // 那么就表示倒数
        if (-1 == n) {
            return i18n.L1 + lc.unit;
        }
        return i18n.Ln.replace("?", Math.abs(n) + "") + lc.unit;
    };

    @Override
    public void joinText(List<String> ary, ZCroni18n i18n, String key) {
        joinText(ary, i18n, key, 0, false);
    }

    /**
     * 将自身的描述，添加到一个列表里
     * 
     * @param ary
     *            要添加的列表
     * @param i18n
     *            字典
     * @param key
     *            自己对应到字典里的键
     * @param off
     *            读取自己值<code>(this.values)</code>的偏移量
     * @param ignoreSuffix
     *            是否忽略后缀
     * @return 指向下一个值的下标<code>(this.values)</code>
     */
    public int joinText(List<String> ary,
                        ZCroni18n i18n,
                        String key,
                        int off,
                        boolean ignoreSuffix) {
        if (off >= this.values.length)
            return -1;
        ZCroni18n.Item lc = i18n.getItem(key);

        switch (this.values[off++]) {
        case ANY:
            // 忽略输出
            if (this.ignoreAnyWhenPrevAllAny && this.isANY() && this.isPrevAllAny()) {}
            // 输出
            else if (lc.hasANY()) {
                ary.add(lc.ANY);
                if (!ignoreSuffix && lc.hasSuffix())
                    ary.add(lc.suffix);
            }
            break;
        case RANGE:
            ary.add(_T(lc, i18n, this.values[off++]) + i18n.to + _T(lc, i18n, this.values[off++]));
            if (!ignoreSuffix && lc.hasSuffix())
                ary.add(lc.suffix);
            break;
        case LIST:
            List<String> list = new ArrayList<>();
            while (-1 != (off = this.joinText(list, i18n, key, off, true))) {}
            ary.add(Strings.join(",", list));
            if (!ignoreSuffix && lc.hasSuffix())
                ary.add(lc.suffix);
            break;
        case SPAN:
            String s0 = _T(lc, i18n, this.values[off++]);
            ary.add(i18n.start.replace("?", s0));
            ary.add(lc.span.replace("?", this.values[off++] + ""));
            if (!ignoreSuffix && lc.hasSuffix())
                ary.add(lc.suffix);
            break;
        case ONE:
            int n = this.values[off++];

            // 忽略输出
            if (n == 0 && this.ignoreZeroWhenPrevHasSpan && this.isPrevHasSpan()) {}
            // 输出
            else {
                ary.add(_T(lc, i18n, n));
                if (!ignoreSuffix && lc.hasSuffix())
                    ary.add(lc.suffix);
            }
            break;
        default:
            throw Lang.makeThrow("Unknown type : %d", this.values[0]);
        }
        // 返回指向下一个位置的下标
        return off;
    }

    @Override
    public boolean match(int v, int min, int max) {
        // 如果值不在范围中
        if (v < min || v >= max)
            return false;

        // 通配
        if (ANY == values[0])
            return true;

        // 准备一下要判断的数组
        int[] refs = prepare(max);

        // 判断
        return _match_(v, refs);
    }

    protected boolean _match_(int v, int[] refs) {
        switch (refs[0]) {
        case ONE:
            return v == refs[1];

        case RANGE:
            return v >= refs[1] && v <= refs[2];

        case LIST:
            for (int i = 1; i < refs.length; i++) {
                if (ONE == refs[i]) {
                    if (v == refs[++i])
                        return true;
                } else if (RANGE == refs[i]) {
                    int l = refs[++i];
                    int r = refs[++i];
                    if (v >= l && v <= r)
                        return true;
                } else {
                    throw Lang.impossible();
                }
            }
            return false;

        case SPAN:
            return (v - refs[1]) % refs[2] == 0;

        case ANY:
            return true;
        }
        // 默认则不匹配
        return false;
    }

    /**
     * 根据值的范围，返回一个新的 values 数组，这里解决的 "L" 修饰符的问题
     * 
     * @param max
     *            最大值(不包括)
     * 
     * @return 新的数组， match 函数会根据这个数组进行判断
     */
    protected int[] prepare(int max) {
        // 准备返回值
        int[] refs = new int[values.length];
        refs[0] = values[0];

        // 判断是否需要 L 一下 ...
        for (int i = 1; i < refs.length; i++) {
            int v = values[i];
            refs[i] = v < 0 ? max + v : v;
        }

        // 返回
        return refs;
    }

    /**
     * 根据一个值，评估出一个数值来，这里判断了 "L" 的问题
     * 
     * @param str
     *            原始字符串
     * @param dict
     *            字典，如果为 null 那么字符串不接受简名，如果匹配，则采用字典中的下标作为值
     * @param dictOffset
     *            从字典的哪个下标开始查
     * @return
     */
    protected int __eval(String str, String[] dict, int dictOffset) {
        int x = 1;

        if (this.supportLast && str.endsWith("L")) {
            x = -1;
            str = str.substring(0, str.length() - 1);
        }

        // 直接是数字
        if (str.matches("^[0-9]+$")) {
            return Integer.parseInt(str) * x;
        }

        // 使用字典
        if (null != dict) {
            String s = str.toUpperCase();
            for (int i = dictOffset; i < dict.length; i++)
                if (s.equals(dict[i]))
                    return i;
        }
        // 不支持
        throw Lang.makeThrow("isNaN : " + str);
    }

    // 子类重载它，可以支持更丰富的值
    protected int eval4override(String str) {
        return __eval(str, null, -1);
    }

    /*--------------------------------------快捷名称-----*/
    static String[] DAYS_OF_WEEK = new String[]{null,
                                                "SUN",
                                                "MON",
                                                "TUE",
                                                "WED",
                                                "THU",
                                                "FRI",
                                                "SAT"};

    static String[] MONTH_OF_YEAR = new String[]{null,
                                                 "JAN",
                                                 "FEB",
                                                 "MAR",
                                                 "APR",
                                                 "MAY",
                                                 "JUN",
                                                 "JUL",
                                                 "AUG",
                                                 "SEP",
                                                 "OCT",
                                                 "NOV",
                                                 "DEC"};

}
