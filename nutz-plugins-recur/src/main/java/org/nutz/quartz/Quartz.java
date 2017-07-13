package org.nutz.quartz;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.Times;
import org.nutz.lang.born.Borning;
import org.nutz.lang.util.NutMap;

/**
 * 封装 Quartz 表达式的解析，和解释
 * <ul>
 * <li>解析 : 将字符串格式化
 * <li>解释 : 根据解释结果，填充一个执行数组
 * </ul>
 * 
 * <h4>关于 Quartz 的简要说明</h4>
 * 
 * <pre>
 * 表达式是一个字符串，它有六个子表达式构成。这些子表达式用空格来分隔。 
 *  # 子表达式描述如下： 
 *     0) 秒（0~59） 
 *     1) 分钟（0~59） 
 *     2) 小时（0~23） 
 *     3) 天（月）（1~31，但是你需要考虑你月的天数） 
 *     4) 月（1~12） 
 *     5) 天（星期）（1~7 1=SUN 或 SUN，MON，TUE，WED，THU，FRI，SAT）
 *  # 特殊符号
 *     '-' : 范围， 比如在子表达式（月），"1-4" 表示 2月到5月
 *     "," : 列表分隔, 比如在子表达式（天－星期），"1,3" 表示 周日和周二
 *     "*" : 代表所有可能的值
 *     "/" : 用来指定数值的增量
 *            > 例如: 在子表达式（分钟）里的 "0/15" 表示从第0分钟开始，每15分钟
 *            > 又如: 在子表达式（分钟）里的 "3/20" 表示从第3分钟开始，每20分钟
 *                   （它和 "3，23，43" ）的含义一样 
 *     "?" : 仅被用于天（月）和天（星期）两个子表达式，表示不指定值 
 *     "L" : 仅被用于天（月）和天（星期）两个子表达式，它是单词“last”的缩写
 *            > 在天（月）子表达式中，“L”表示一个月的最后一天
 *                >> 6L”表示这个月的倒数第６天
 *            > 在天（星期）自表达式中，“L”表示一个星期的最后一天，也就是SAT
 *                >> “FRIL”表示这个月的最一个星期五
 *     "W" : 仅被用于天（月）子表达式，表示工作日
 *            > "W" 为所有工作日
 *            > "4W" 为距离本月第5日最近的工作日
 *            > "4LW" 为距离当月倒数第4日最近的工作日
 *     "#" : 仅用在天（星期）表示第几个
 *            > 3#1 表示 第1个周二
 *            > FRI#2 第2个周五
 * 
 * 
 * </pre>
 * 
 * 
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
// TODO 还未支持年
public class Quartz {

    public static Quartz NEW(String qzs) {
        Quartz qs = NEW();
        qs.valueOf(qzs);
        return qs;
    }

    public static Quartz NEW() {
        return new Quartz();
    }

    /**
     * 让数组更紧凑
     * <p>
     * 这个函数可以配合 fill 来使用， fill 过的数组有些元素为 null<br>
     * 为了能紧凑显示，本函数去掉所有 null 元素，但是同时数组的下标信息就丢失了<br>
     * 如果不想丢失下标信息并且还想紧凑表达 请用 compactAll
     * 
     * @param <T>
     * @param array
     *            Quartz 填充的数组
     * @return 紧凑的数组（下标信息丢失）
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] compact(T[] array) {
        ArrayList<T> list = new ArrayList<T>(array.length);
        for (T ele : array)
            if (null != ele)
                list.add(ele);
        T[] re = (T[]) Array.newInstance(array.getClass().getComponentType(), list.size());
        return list.toArray(re);
    }

    /**
     * 让数组更紧凑，并保留下标信息
     * 
     * @param <T>
     * @param array
     *            Quartz 填充的数组
     * @return 紧凑的列表（下标信息保留）
     */
    public static <T> ArrayList<QzObj<T>> compactAll(T[] array) {
        ArrayList<QzObj<T>> list = new ArrayList<QzObj<T>>(array.length);
        for (int i = 0; i < array.length; i++)
            if (null != array[i])
                list.add(new QzObj<T>(array[i], i));
        list.trimToSize();
        return list;
    }

    /*-------------------------------子表达式下标的含义----*/
    private String str; // 原始信息
    private QzItem iss;
    private QzItem imm;
    private QzItem iHH;
    private QzDateItem idd;
    private QzDateItem iMM;
    private QzDateItem iww;

    /*---------------------------------------构造函数----*/
    public Quartz() {
        iss = new QzItem();
        imm = new QzItem();
        iHH = new QzItem();
        idd = new QzItem_dd();
        iMM = new QzItem_MM();
        iww = new QzItem_ww();
    }

    /**
     * 根据字符串，重新解析一个 Quartz 表达式
     * 
     * @param qzs
     *            Quartz 表达式字符串
     * @return 自身以便链式使用
     */
    public Quartz valueOf(String qzs) {
        this.str = qzs;
        // 拆
        String[] ss = Strings.splitIgnoreBlank(qzs, "[ ]");
        // 验证
        if (6 != ss.length)
            throw Lang.makeThrow("Wrong format '%s': expect %d items but %d", qzs, 6, ss.length);
        // 解析子表达式
        iss.valueOf(ss[0]);
        imm.valueOf(ss[1]);
        iHH.valueOf(ss[2]);
        idd.valueOf(ss[3]);
        iMM.valueOf(ss[4]);
        iww.valueOf(ss[5]);
        // 返回
        return this;
    }

    /**
     * 是否匹配一个日期
     * 
     * @param c
     *            日期对象，时间部分无视
     * @return 是否匹配
     */
    public boolean matchDate(Calendar c) {
        if (!idd.match(c))
            return false;

        if (!iMM.match(c))
            return false;

        if (!iww.match(c))
            return false;

        return true;
    }

    /**
     * 是否匹配一个日期
     * 
     * @param ds
     *            日期字符串，格式为 yyyy-MM-dd 的字符串
     * @return 是否匹配
     */
    public boolean matchDate(String ds) {
        return matchDate(Times.C(ds));
    }

    /**
     * 是否匹配一个日期
     * 
     * @param d
     *            日期
     * @return 是否匹配
     */
    public boolean matchDate(Date d) {
        return matchDate(Times.C(d.getTime()));
    }

    /**
     * 看看能不能匹配上一周中的天数
     * 
     * @param day
     *            一周内的天数，1 表示周日, 2 表示周一 ... 7 表示周六
     * @return 是否匹配
     */
    public boolean matchDayInWeek(int day) {
        return this.iww._match_(day, this.iww.prepare(8));
    }

    /**
     * 看看能不能匹配上一个月的天数
     * 
     * @param day
     *            月中的天数，1 - 31
     * @return 是否匹配
     */
    public boolean matchDayInMonth(int day) {
        return this.idd._match_(day, this.idd.prepare(32));
    }

    /**
     * 匹配月
     * 
     * @param m
     *            月份，1-12
     * @return 是否匹配
     */
    public boolean matchMonth(int m) {
        return this.iMM._match_(m, this.iMM.prepare(13));
    }

    /**
     * 根据给定的秒数，判断是否匹配本表达式
     * 
     * @param sec
     *            一天中的秒数，为 0-86399，如果超出，按 86399，如果小于，按0
     * @return 是否匹配
     */
    public boolean matchTime(int sec) {
        int HH = sec / 3600;
        if (!iHH.match(HH, 0, 24))
            return false;

        int mm = (sec - (HH * 3600)) / 60;
        if (!imm.match(mm, 0, 60))
            return false;

        int ss = sec - (HH * 3600) - (mm * 60);
        if (!iss.match(ss, 0, 60))
            return false;

        return true;
    }

    /**
     * 根据给定的时间字符串，判断是否匹配本表达式
     * 
     * @param ts
     *            时间字符串，格式为 HH:mm:ss 或者 HH:MM
     * @return 是否匹配
     */
    public boolean matchTime(String ts) {
        String[] ary = ts.split(":");

        int HH = Integer.parseInt(ary[0]);
        if (!iHH.match(HH, 0, 24))
            return false;

        int mm = Integer.parseInt(ary[1]);
        if (!imm.match(mm, 0, 60))
            return false;

        int ss = Integer.parseInt(ary[2]);
        if (!iss.match(ss, 0, 60))
            return false;

        return true;
    }

    /**
     * @see #each(Object[], QzEach, Calendar)
     */
    public <T> void each(T[] array, QzEach<T> callback, String ds) {
        this.each(array, callback, Times.C(ds));
    }

    /**
     * @see #each(Object[], QzEach, Calendar)
     */
    public <T> void each(T[] array, QzEach<T> callback, Date d) {
        this.each(array, callback, Times.C(d));
    }

    /**
     * @see #each(Object[], int, int, int, Calendar, QzEach)
     */
    public <T> void each(T[] array, QzEach<T> callback, Calendar c) {
        if (null != array && array.length > 0)
            each(array, callback, c, 0, array.length, 86400 / array.length);
    }

    /**
     * 本函数用来迭代一个目标数组
     * <p>
     * 你的数组最多可以是 86400 的元素，对应一天中的每一秒，<br>
     * 根据你给定的日期，本函数来决定具体哪个一数组项目要被执行回调<br>
     * 当然，如果你给定日期不能匹配表达式，本函数会直接跳过执行，如果你不给定日期，则本函数一定会执行
     * <p>
     * 关于数组的长度涉及到一个时间的缩放问题，Quartz 实际上是声明了一天中的一系列启动点<br>
     * 这些点，我们可以用秒来表示，从 0－86399 分别表示一天中的任何一秒。 <br>
     * <b style="color:red">因此，给定的数组的长度最好能把 86400 整除 否则一天中最后一段时间会被忽略掉</b>
     * <p>
     * 根据数组的长度，我就能知道，你所关心的 Quartz 表达式精细程度，<br>
     * 比如 如果长度为 24 则，你其实仅仅关心到一个小时，如果 1440 你仅仅关心到1分钟
     * <p>
     * 同时，你可以自由的定义，比如你给定一个 400 长度的数组，那么 86400/400=216。<br>
     * 因此，对你来说，时间的单位是 216 秒。
     * <p>
     * 本函数，执行的策略
     * <ol>
     * <li>得到时间单位
     * <li>然后，循环数组，根据下标，我们能得到一个秒数范围
     * <li>这个范围内，如果任意一秒能被匹配，就算匹配成功
     * </ol>
     * 
     * @param <T>
     * @param array
     *            要被访问数组
     * 
     * @param callback
     *            如果数组下标被匹配，则要执行的回调 * @param off 从数组的哪个下标开始访问
     * 
     * @param c
     *            日期对象。空的话，一定会执行迭代
     * @param len
     *            访问多少个元素
     * @param unit
     *            一个数组元素表示多少秒
     */
    public <T> void each(T[] array, QzEach<T> callback, Calendar c, int off, int len, int unit) {
        // 填充数组为空，每必要填充
        if (null == array || array.length == 0)
            return;

        // 如果日期不匹配，无视
        if (null != c && !matchDate(c))
            return;

        // 循环数组
        try {
            int maxIndex = Math.min(array.length, off + len);
            for (int i = off; i < maxIndex; i++) {
                int sec = i * unit;
                int max = sec + unit;
                // 循环每个数组元素
                for (; sec < max; sec++) {
                    if (this.matchTime(sec)) {
                        callback.invoke(array, i);
                        break;
                    }
                }
            }
        }
        catch (Exception e) {
            throw Lang.wrapThrow(e);
        }
    }

    /**
     * 本函数用来叠加数组
     * <p>
     * 它将调用 each 函数，叠加数组中的匹配项。
     * <p>
     * 所谓叠加，就是数组每个元素都是一个 QzList，匹配的数组项，会 add 叠加对象。 <br>
     * 与之不同的，{@link #fill(Object[], Object, Calendar)} 是直接替换
     * 
     * @param <T>
     * @param array
     *            要被叠加的数组
     * @param obj
     *            叠加对象
     * @param c
     *            日期对象
     * @param off
     *            从数组的哪个下标开始访问
     * @param len
     *            访问多少个元素
     * @param unit
     *            一个数组元素表示多少秒
     * 
     * @return 数组本身以便链式赋值
     * @see #each(Object[], QzEach, Calendar)
     */
    @SuppressWarnings("unchecked")
    public <T extends QzOverlapor> T[] overlap(T[] array,
                                               final Object obj,
                                               Calendar c,
                                               int off,
                                               int len,
                                               int unit) {
        if (null != array && array.length > 0) {
            Mirror<?> mi = Mirror.me(array.getClass().getComponentType());
            final Borning<T> borning = (Borning<T>) mi.getBorning();
            final Object[] args = new Object[0];
            this.each(array, new QzEach<T>() {
                public void invoke(T[] array, int i) {
                    // 增加一个叠加器
                    if (null == array[i])
                        array[i] = borning.born(args);
                    // 加入叠加对象
                    array[i].add(obj);
                }
            }, c, off, len, unit);
        }
        return array;
    }

    /**
     * @see #overlap(QzOverlapor[], Object, Calendar, int, int, int)
     */
    public <T extends QzOverlapor> T[] overlap(T[] array, final Object obj, Calendar c) {
        if (null == array || array.length == 0)
            return array;
        return this.overlap(array, obj, c, 0, array.length, 86400 / array.length);
    }

    /**
     * 根据当前时间的毫秒数叠加数组
     * 
     * @see #overlap(QzOverlapor[], Object, Calendar)
     */
    public <T extends QzOverlapor> T[] overlapByToday(T[] array, T obj) {
        return overlap(array, obj, Calendar.getInstance());
    }

    /**
     * 根据时间的毫秒数叠加数组
     * 
     * @param <T>
     * @param array
     *            要被叠加的数组
     * @param obj
     *            叠加的对象
     * @param ms
     *            毫秒数，但是仅仅其中的天这部分有意义
     * @return 数组本身以便链式赋值
     * @see #overlap(QzOverlapor[], Object, Calendar)
     */
    public <T extends QzOverlapor> T[] overlapBy(T[] array, T obj, long ms) {
        return overlap(array, obj, Times.C(ms));
    }

    /**
     * 根据时间的毫秒数叠加数组
     * 
     * @param <T>
     * @param array
     *            要被叠加的数组
     * @param obj
     *            叠加的对象
     * @param ds
     *            日期字符串，格式为 yyyy-MM-dd 的字符串
     * @return 数组本身以便链式赋值
     * @see #overlap(QzOverlapor[], Object, Calendar)
     */
    public <T extends QzOverlapor> T[] overlapBy(T[] array, Object obj, String ds) {
        return overlap(array, obj, Times.C(ds));
    }

    /**
     * 根据时间的毫秒数叠加数组
     * 
     * @param <T>
     * @param array
     *            要被叠加的数组
     * @param obj
     *            叠加的对象
     * @param d
     *            时间，但是仅仅其中的天这部分有意义
     * @return 数组本身以便链式赋值
     * @see #overlap(QzOverlapor[], Object, Calendar)
     */
    public <T extends QzOverlapor> T[] overlapBy(T[] array, T obj, Date d) {
        return overlap(array, obj, Times.C(d.getTime()));
    }

    /**
     * @see #fill(Object[], Object, Calendar)
     */
    public <T> T[] fillByToday(T[] array, T obj) {
        return this.fill(array, obj, Calendar.getInstance());
    }

    /**
     * @see #fill(Object[], Object, Calendar)
     */
    public <T> T[] fillBy(T[] array, T obj, long ms) {
        return this.fill(array, obj, Times.C(ms));
    }

    /**
     * @see #fill(Object[], Object, Calendar)
     */
    public <T> T[] fillBy(T[] array, final T obj, String ds) {
        return this.fill(array, obj, Times.C(ds));
    }

    /**
     * @see #fill(Object[], Object, Calendar)
     */
    public <T> T[] fillBy(T[] array, T obj, Date d) {
        return this.fill(array, obj, Times.C(d));
    }

    /**
     * @see #fill(Object[], Object, Calendar, int, int, int)
     */
    public <T> T[] fill(T[] array, final T obj, Calendar c) {
        if (null == array || array.length == 0)
            return array;
        return fill(array, obj, c, 0, array.length, 86400 / array.length);
    }

    /**
     * 本函数用来填充数组
     * <p>
     * 它将调用 each 函数，填充数组中的匹配项。
     * <p>
     * 所谓填充，就是数组每个元素都是一个 T，匹配的数组项，被设置成填充对象。 <br>
     * 与之不同的，{@link #overlap(QzOverlapor[], Object, Calendar)} 是叠加到一个 QzList
     * 
     * @param <T>
     * @param array
     *            要被填充的数组
     * @param obj
     *            填充的对象
     * @param c
     *            日期对象
     * @param off
     *            从数组的哪个下标开始访问
     * @param len
     *            访问多少个元素
     * @param unit
     *            一个数组元素表示多少秒
     * 
     * @return 数组本身以便链式赋值
     * @see #each(Object[], QzEach, Calendar)
     */
    public <T> T[] fill(T[] array, final T obj, Calendar c, int off, int len, int unit) {
        this.each(array, new QzEach<T>() {
            public void invoke(T[] array, int i) {
                array[i] = obj;
            }
        }, c, off, len, unit);
        return array;
    }

    public String toString() {
        return str;
    }

    /**
     * 将表达式转换成人类可以读懂的文字
     *
     * @param i18n
     *            为多国语言参数，结构如下:
     * 
     *            <pre>
        {
            start : "从?开始",
            to    : "至",
            L1    : "最后一",
            Ln    : "倒数第?",
            N     : "第?个",
            month : {
                span : "每?个月",
                ANY  : "每月的",
                dict : ["一月","二月","三月","四月","五月","六月","七月","八月","九月","十月","十一月","十二月"],
                suffix : "之中的"
            },
            day : {
                unit   : "日",
                span   : "每?天",
                ANY    : "每天",
                tmpl   : "?号",
                suffix : "的",
                W      : "最近的工作日",
                Wonly  : "所有工作日"
            },
            week : {
                unit : "周",
                span : "每隔?周", 
                ANY  : "每周",
                dict : ["周日","周一","周二","周三","周四","周五","周六"],
                suffix : "的每天"
            },
            hour : {
                span  : "每?小时",
                ANY   : "",
                tmpl  : "?点",
                suffix : "的"
            },
            minute : {
                scope : ", 的",
                span  : "每?分钟",
                ANY   : "",
                tmpl  : "?分",
                suffix : "的"
            },
            second : {
                scope : ", 其中每分钟的",
                span  : "每?秒钟",
                ANY   : "",
                tmpl  : "?秒"
            }
        }
     *            </pre>
     * 
     * @return 人类可以读懂的字符串
     */
    public String toText(NutMap i18n) {
        throw Lang.noImplement();
    }

    public static void main(String[] args) {
        System.out.println("1.b.52");
    }
}
