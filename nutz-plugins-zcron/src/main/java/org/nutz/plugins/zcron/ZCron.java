package org.nutz.plugins.zcron;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.Times;
import org.nutz.lang.born.Borning;
import org.nutz.lang.tmpl.Tmpl;
import org.nutz.lang.util.DateRegion;
import org.nutz.lang.util.NutMap;
import org.nutz.lang.util.Region;

public class ZCron {

    /**
     * 让数组更紧凑
     * <p>
     * 这个函数可以配合 fill 来使用， fill 过的数组有些元素为 null<br>
     * 为了能紧凑显示，本函数去掉所有 null 元素，但是同时数组的下标信息就丢失了<br>
     * 如果不想丢失下标信息并且还想紧凑表达 请用 compactAll
     * 
     * @param <T>
     * @param array
     *            填充的数组
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
     *            填充的数组
     * @return 紧凑的列表（下标信息保留）
     */
    public static <T> ArrayList<CronObj<T>> compactAll(T[] array) {
        ArrayList<CronObj<T>> list = new ArrayList<CronObj<T>>(array.length);
        for (int i = 0; i < array.length; i++)
            if (null != array[i])
                list.add(new CronObj<T>(array[i], i));
        list.trimToSize();
        return list;
    }

    private String str; // 原始信息

    /**
     * 经过整理的字符串信息，这个数组永远有 4 位
     * 
     * 0 1 2 3 [时间范围/步长][时间部分][日期部分][日期范围] T(..]{..} 0 0 0 * * ? * D[..]
     */
    private String[] parts;

    /*-------------------------标准 Cron 表达式部分----*/
    private CrnStdItem iss;
    private CrnStdItem imm;
    private CrnStdItem iHH;
    private CrnDateItem idd;
    private CrnDateItem iMM;
    private CrnDateItem iww;
    private CrnDateItem iyy; // 「可选」

    /*--------------------------扩展部分「均为可选」----*/

    /**
     * 限定日期区间: D[20170801,20170822)
     */
    private DateRegion rgDate;

    /**
     * 固定时间点重复器
     */
    private List<TimePointRepeater> timeRepeaters;

    private boolean has_time_points;

    private boolean has_time_steps;

    /*------------------------------------构造函数----*/
    public ZCron() {}

    public ZCron(String cron) {
        this.parse(cron);
    }

    /**
     * 根据字符串，重新解析一个表达式
     * 
     * @param cron
     *            表达式字符串
     * @return 自身以便链式使用
     */
    public ZCron parse(String cron) {
        this.str = cron;
        this.parts = new String[4];

        // 初始化
        iHH = new CrnStdItem();
        imm = new CrnStdItem(iHH);
        iss = new CrnStdItem(imm).setIgnoreZeroWhenPrevHasSpan(true);

        idd = new CrnItem_dd();
        iww = new CrnItem_ww(idd).setIgnoreAnyWhenPrevAllAny(true);
        iMM = new CrnItem_MM(idd, iww).setIgnoreAnyWhenPrevAllAny(true);
        iyy = new CrnItem_yy(iMM).setIgnoreAnyWhenPrevAllAny(true);

        // 拆
        String[] items = cron.trim().split("[ \t]+");
        ArrayList<String> stdList = new ArrayList<String>(items.length);

        // 先找一遍,处理扩展表达式项目，剩下的归到标准表达式里面
        __parse_for_ext(items, stdList);

        // 默认标准表达式
        String[] stds = Lang.array("0", "0", "0", "*", "*", "?", "*");

        // 如果标准表达式项目不足，试图补上
        int stdIC = stdList.size();
        int stdN;

        /**
         * <pre>
         * 输入没有年:
         *  - 0 0 0 * * ?   <- 6
         *  - * * ?         <- 3
         * 输入有年
         *  - 0 0 0 * * ? * <- 7
         *  - * * ? *       <- 4
         * 其他长度不正确
         *  - !!! 抛出异常
         * </pre>
         */

        switch (stdIC) {
        // 什么都没给，必须有 timePoints 和 rgDate
        case 0:
            if (0 == timeRepeaters.size())
                throw Lang.makeThrow("No TimePoints : " + cron);
            if (null == rgDate)
                throw Lang.makeThrow("No DateRange : " + cron);
            stdN = 0;
            break;
        // 给了 `日 月 周` 必须还要给定 timePoints
        case 3:
            if (0 == timeRepeaters.size())
                throw Lang.makeThrow("No TimePoints : " + cron);
            stdN = 1;
            break;
        // 给了 `日 月 周 年` 必须还要给定 timePoints
        case 4:
            if (0 == timeRepeaters.size())
                throw Lang.makeThrow("No TimePoints : " + cron);
            stdN = 0;
            break;
        // 给了 `秒 分 时 日 月 周`
        case 6:
            stdN = 1;
            break;
        // 给了 `秒 分 时 日 月 周 年`
        case 7:
            stdN = 0;
            break;
        default:
            throw Lang.makeThrow("Wrong format : " + cron);
        }

        // 补上标准表达式项
        for (int i = 1; i <= stdIC; i++) {
            stds[stds.length - i - stdN] = stdList.get(stdIC - i);
        }

        // 解析子表达式
        iss.parse(stds[0]);
        imm.parse(stds[1]);
        iHH.parse(stds[2]);
        idd.parse(stds[3]);
        iMM.parse(stds[4]);
        iww.parse(stds[5]);
        iyy.parse(stds[6]);

        // 记录成标准
        parts[1] = Strings.join(0, 3, " ", stds);
        parts[2] = Strings.join(3, 4, " ", stds);

        // 返回
        return this;
    }

    private void __parse_for_ext(String[] items, ArrayList<String> stdList) {
        List<TimePointRepeater> trList = new ArrayList<>(3);

        // 循环解析
        for (String s : items) {
            // 为日期范围
            if (s.startsWith("D")) {
                rgDate = Region.Date(s.substring(1));
                parts[3] = s;
            }
            // 为时间范围
            else if (s.startsWith("T")) {
                TimePointRepeater tr = new TimePointRepeater();
                tr.parse(s);
                trList.add(tr);
            }
            // 标准表达式项
            else {
                stdList.add(s);
            }
        }

        // 判断一下是否有时间点，
        this.has_time_points = false;
        for (TimePointRepeater tr : trList) {
            if (tr.isPoints() || tr.isStep()) {
                this.has_time_points = true;
                break;
            }
        }

        // 如果有的话，丢弃所有的纯范围
        this.timeRepeaters = new ArrayList<>(trList.size());
        for (TimePointRepeater tr : trList) {
            if (tr.isPureRegion()) {
                if (!this.has_time_points)
                    this.timeRepeaters.add(tr);
            }
            // 肯定加
            else {
                this.timeRepeaters.add(tr);
            }
        }

        // 设置一下 part[0]
        this.has_time_steps = false;
        List<String> trStrs = new ArrayList<>(this.timeRepeaters.size());
        for (TimePointRepeater tr : this.timeRepeaters) {
            this.has_time_steps |= tr.isStep();
            trStrs.add(tr.getPrimaryString());
        }
        this.parts[0] = trStrs.isEmpty() ? null : Strings.join(" ", trStrs);
    }

    /**
     * 是否匹配一个日期
     * 
     * @param c
     *            日期对象，时间部分无视
     * @return 是否匹配
     */
    public boolean matchDate(Calendar c) {
        if (null != rgDate && !rgDate.match(c.getTime()))
            return false;

        if (!iyy.match(c))
            return false;

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
     * 匹配年
     * 
     * @param year
     *            年份，2017 等
     * @return 是否匹配
     */
    public boolean matchYear(int year) {
        return this.iyy._match_(year, this.iyy.prepare(0));
    }

    /**
     * 根据给定的秒数，判断是否匹配本表达式
     * 
     * @param sec
     *            一天中的秒数，为 0-86399，如果超出，按 86399，如果小于，按0
     * @return 是否匹配
     */
    public boolean matchTime(int sec) {
        Times.TmInfo ti = Times.Ti(sec);
        return matchTime(ti);
    }

    /**
     * 根据给定的时间字符串，判断是否匹配本表达式
     * 
     * @param ts
     *            时间字符串，格式为 HH:mm:ss 或者 HH:MM
     * @return 是否匹配
     */
    public boolean matchTime(String ts) {
        Times.TmInfo ti = Times.Ti(ts);
        return matchTime(ti);
    }

    /**
     * 根据给定的时间，判断是否匹配本表达式
     * 
     * @param ti
     *            时间对象
     * @return 是否匹配
     */
    public boolean matchTime(Times.TmInfo ti) {
        // 指明了时间点的情况
        if (this.has_time_points) {
            for (TimePointRepeater tr : this.timeRepeaters) {
                if (tr.matchTime(ti.value))
                    return true;
            }
            return false;
        }

        // 先匹配时间范围
        if (this.timeRepeaters.size() > 0) {
            for (TimePointRepeater tr : this.timeRepeaters) {
                if (!tr.matchTime(ti.value))
                    return false;
            }
        }

        // 依次对于表达式求职
        if (!iHH.match(ti.hour, 0, 24))
            return false;

        if (!imm.match(ti.minute, 0, 60))
            return false;

        if (!iss.match(ti.second, 0, 60))
            return false;

        return true;
    }

    /**
     * @see #each(Object[], CronEach, Calendar)
     */
    public <T> void each(T[] array, CronEach<T> callback, String ds) {
        this.each(array, callback, Times.C(ds));
    }

    /**
     * @see #each(Object[], CronEach, Calendar)
     */
    public <T> void each(T[] array, CronEach<T> callback, Date d) {
        this.each(array, callback, Times.C(d));
    }

    /**
     * @see #each(Object[], int, int, int, Calendar, CronEach)
     */
    public <T> void each(T[] array, CronEach<T> callback, Calendar c) {
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
     * 关于数组的长度涉及到一个时间的缩放问题，NutzCron 实际上是声明了一天中的一系列启动点<br>
     * 这些点，我们可以用秒来表示，从 0－86399 分别表示一天中的任何一秒。 <br>
     * <b style="color:red">因此，给定的数组的长度最好能把 86400 整除 否则一天中最后一段时间会被忽略掉</b>
     * <p>
     * 根据数组的长度，我就能知道，你所关心的 Cron 表达式精细程度，<br>
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
     * @param callback
     *            如果数组下标被匹配，则要执行的回调
     * @param off
     *            从数组的哪个下标开始访问
     * @param c
     *            日期对象。空的话，一定会执行迭代
     * @param len
     *            访问多少个元素
     * @param unit
     *            一个数组元素表示多少秒
     */
    public <T> void each(T[] array, CronEach<T> callback, Calendar c, int off, int len, int unit) {
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
     * @see #each(Object[], CronEach, Calendar)
     */
    @SuppressWarnings("unchecked")
    public <T extends CronOverlapor> T[] overlap(T[] array,
                                                 final Object obj,
                                                 Calendar c,
                                                 int off,
                                                 int len,
                                                 int unit) {
        if (null != array && array.length > 0) {
            Mirror<?> mi = Mirror.me(array.getClass().getComponentType());
            final Borning<T> borning = (Borning<T>) mi.getBorning();
            final Object[] args = new Object[0];
            this.each(array, new CronEach<T>() {
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
     * @see #overlap(CronOverlapor[], Object, Calendar, int, int, int)
     */
    public <T extends CronOverlapor> T[] overlap(T[] array, final Object obj, Calendar c) {
        if (null == array || array.length == 0)
            return array;
        return this.overlap(array, obj, c, 0, array.length, 86400 / array.length);
    }

    /**
     * 根据当前时间的毫秒数叠加数组
     * 
     * @see #overlap(CronOverlapor[], Object, Calendar)
     */
    public <T extends CronOverlapor> T[] overlapByToday(T[] array, T obj) {
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
     * @see #overlap(CronOverlapor[], Object, Calendar)
     */
    public <T extends CronOverlapor> T[] overlapBy(T[] array, T obj, long ms) {
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
     * @see #overlap(CronOverlapor[], Object, Calendar)
     */
    public <T extends CronOverlapor> T[] overlapBy(T[] array, Object obj, String ds) {
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
     * @see #overlap(CronOverlapor[], Object, Calendar)
     */
    public <T extends CronOverlapor> T[] overlapBy(T[] array, T obj, Date d) {
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
     * 与之不同的，{@link #overlap(CronOverlapor[], Object, Calendar)} 是叠加到一个 QzList
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
     * @see #each(Object[], CronEach, Calendar)
     */
    public <T> T[] fill(T[] array, final T obj, Calendar c, int off, int len, int unit) {
        this.each(array, new CronEach<T>() {
            public void invoke(T[] array, int i) {
                array[i] = obj;
            }
        }, c, off, len, unit);
        return array;
    }

    public boolean isHasTimePoints() {
        return this.has_time_points;
    }

    public boolean isHasTimeSteps() {
        return this.has_time_steps;
    }

    public ZCron setPartExtTime(String str) {
        return this.__set_part(0, str);
    }

    public ZCron setPartStdTime(String str) {
        return this.__set_part(1, str);
    }

    public ZCron setPartStdDate(String str) {
        return this.__set_part(2, str);
    }

    public ZCron setPartExtDate(String str) {
        return this.__set_part(3, str);
    }

    ZCron __set_part(int index, String str) {
        String val = Strings.sBlank(Strings.trim(str), null);
        this.parts[index] = val;
        // 清除时间点
        if (0 == index && null == val) {
            this.timeRepeaters.clear();
            this.has_time_points = false;
        }
        // 清除日期范围
        if (3 == index && null == val) {
            this.rgDate = null;
        }
        // 立即解析
        String cron = this.toString();
        return this.parse(cron);
    }

    public String toString() {
        ArrayList<String> list = new ArrayList<>();
        // 扩展: 时间部分
        if (!Strings.isBlank(parts[0])) {
            list.add(parts[0]);
        }
        // 标准: 时间部分
        if (!this.has_time_points) {
            list.add(parts[1]);
        }
        // 标准: 日期部分
        if (null == rgDate || !"* * ? *".equals(parts[2]) || !this.has_time_points) {
            list.add(parts[2].endsWith(" *") ? parts[2].substring(0, parts[2].length() - 2)
                                             : parts[2]);
        }
        // 扩展: 日期部分
        if (!Strings.isBlank(parts[3])) {
            list.add(parts[3]);
        }
        // 返回结果
        return Strings.join(" ", list);
    }

    public String getPrimaryString() {
        return str;
    }

    /**
     * 将表达式转换成人类可以读懂的文字
     *
     * @param i18n
     *            为多国语言参数，结构参见
     *            <code>src/main/resources/org/nutz/plugins/zcron/i18n/zh_cn.js</code>
     * 
     * @return 人类可以读懂的字符串
     */
    public String toText(ZCroni18n i18n) {
        List<String> ary = new ArrayList<>();
        // ............................................
        // 增加日期范围
        if (null != rgDate) {
            __join_date_region(i18n, ary);
        }

        // ............................................
        // 增加标准表达式的年/月
        this.iyy.joinText(ary, i18n, "year");
        this.iMM.joinText(ary, i18n, "month");
        // ............................................
        // 没限制日期，看看是否用周
        if (this.idd.isANY()) {
            if (this.iww.isANY())
                this.idd.joinText(ary, i18n, "day");
            else
                this.iww.joinText(ary, i18n, "week");
        }
        // 限制了日期，那么就用日期
        else {
            this.idd.joinText(ary, i18n, "day");
        }

        // ............................................
        // 描述了时间点
        for (TimePointRepeater tr : this.timeRepeaters) {
            tr.joinText(i18n, ary);
        }
        // ............................................
        // 如果没有指定时间点，则默认采用标准表达式的时间
        if (!this.has_time_points) {
            this.iHH.joinText(ary, i18n, "hour");
            this.imm.joinText(ary, i18n, "minute");
            this.iss.joinText(ary, i18n, "second");
        }

        // 返回字符串
        return Strings.join("", ary);
    }

    private void __join_date_region(ZCroni18n i18n, List<String> ary) {
        Date dFrom = rgDate.left();
        Date dTo = rgDate.right();
        int yearFrom = null == dFrom ? -1 : Times.C(dFrom).get(Calendar.YEAR);
        int yearTo = null == dTo ? -2 : Times.C(dTo).get(Calendar.YEAR);

        // 准备模板
        Tmpl tmpl;
        // 没有开始
        if (yearFrom < 0) {
            tmpl = Tmpl.parse(i18n.dates.no_from);
        }
        // 没有结束
        else if (yearTo < 0) {
            tmpl = Tmpl.parse(i18n.dates.no_to);
        }
        // 完整区间
        else {
            tmpl = Tmpl.parse(i18n.dates.region);
        }

        // 准备上下文
        NutMap c = new NutMap();
        // 开始
        if (yearFrom > 0) {
            c.put("from", Times.format(i18n.dates.full, dFrom));
            c.put("ieF", rgDate.isLeftOpen() ? i18n.EXC : i18n.INV);
        }
        // 结束
        if (yearTo > 0) {
            // 同年
            if (yearFrom == yearTo) {
                c.put("to", Times.format(i18n.dates.same, dTo));
            }
            // 跨年
            else {
                c.put("to", Times.format(i18n.dates.full, dTo));
            }
            c.put("ieT", rgDate.isRightOpen() ? i18n.EXC : i18n.INV);
        }
        // 渲染
        String str = tmpl.render(c);
        ary.add(str);
    }

    public static void main(String[] args) {
        System.out.println("1.b.52");
    }
}
