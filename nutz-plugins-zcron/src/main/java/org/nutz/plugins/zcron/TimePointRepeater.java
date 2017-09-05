package org.nutz.plugins.zcron;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.Times;
import org.nutz.lang.tmpl.Tmpl;
import org.nutz.lang.util.NutMap;
import org.nutz.lang.util.Region;
import org.nutz.lang.util.TimeRegion;

class TimePointRepeater {

    static enum TmUnit {
        h, m, s
    }

    private String __str;

    /**
     * 限定时间区间
     */
    private TimeRegion region;

    private boolean autoPadding;

    private int offInSec;

    private int stepInSec;

    private int offValue;

    private TmUnit offUnit;

    private int stepValue;

    private TmUnit stepUnit;

    // 如果是步长模式，则确定的绝对时间范围（已经对齐过偏移量）
    private int tStart; // 开始（包含）
    private int tEnd; // 结束（不包含）

    /**
     * 声明了固定时间点（秒）
     * <p>
     * 本项目优先于 `timeOffset/timeStep`
     * <p>
     * !!! 注意，本字段的时间点必须是经过排序的（从小到大）
     */
    private int[] timePoints;

    private int unitInSec(TmUnit tu) {
        if (TmUnit.h == tu)
            return 3600;
        if (TmUnit.m == tu)
            return 60;
        return 1;
    }

    private static final Pattern _P = Pattern.compile("^(>)?((\\d+)([hms])?)?/(\\d+)([hms])$");
    private static final Pattern _P_TIME_REGION = Pattern.compile("^T([\\[\\(][\\d:,-]+[\\]\\)])?([{]([^}]+)[}])?$");

    /**
     * 解析类似 T[..]{..} 格式的字符串
     * 
     * <pre>
     * 0/30m
     * -12s/2h
     * </pre>
     * 
     * @param str
     *            输入字符串
     */
    void parse(String str) {
        // 初始化
        this.region = null;
        this.timePoints = null;
        this.stepValue = 0;
        this.stepInSec = 0;
        this.tStart = -1;
        this.tEnd = -1;
        this.__str = str;

        // 匹配正则
        Matcher m = _P_TIME_REGION.matcher(str);
        if (!m.find())
            throw Lang.makeThrow("Invalid time repeater '%s': ", str);

        // 有时间范围：
        String tmrg = m.group(1);
        if (!Strings.isBlank(tmrg))
            this.region = Region.Time(tmrg);

        // 处理时间点
        String tps = m.group(3);
        if (!Strings.isBlank(tps)) {
            // 如果不是步长，则根据间隔时间点生成固定时间点
            if (!this.__parse_step(tps)) {
                String[] timeList = Strings.splitIgnoreBlank(tps);
                this.timePoints = new int[timeList.length];
                for (int x = 0; x < timeList.length; x++) {
                    this.timePoints[x] = Times.T(timeList[x]);
                }
                // 确保顺序
                Arrays.sort(this.timePoints);
            }
        }

        // 既没有时间范围，有没有时间点，那么不能忍啊
        if (null == this.region && null == this.timePoints && this.stepInSec <= 0) {
            throw Lang.makeThrow("Invalid time repeater '%s': ", str);
        }
    }

    private boolean __parse_step(String str) {
        Matcher m = _P.matcher(str);
        if (m.find()) {
            // ..............................................
            // 解析步长信息
            autoPadding = null != m.group(1);
            stepValue = Integer.parseInt(m.group(5));
            stepUnit = TmUnit.valueOf(m.group(6));
            if (null != m.group(2)) {
                offValue = Integer.parseInt(m.group(3));
                offUnit = null != m.group(4) ? TmUnit.valueOf(m.group(4)) : null;
            }
            // ..............................................
            // 计算
            offInSec = (offValue < 0 ? stepValue : offValue)
                       * unitInSec(null == offUnit ? stepUnit : offUnit);
            stepInSec = stepValue * unitInSec(stepUnit);
            if (stepInSec <= 0) {
                throw Lang.makeThrow("Step Value is not ava!");
            }
            // ..............................................
            // 计算步长的真正区间
            TimeRegion tr = this.region;
            // 全天
            if (null == tr || !tr.isRegion()) {
                this.tStart = 0;
                this.tEnd = 86400;
            }
            // 根据给定时间区域
            else {
                this.tStart = tr.left() + (tr.isLeftOpen() ? 1 : 0);
                this.tEnd = tr.right() + (tr.isRightOpen() ? 0 : 1);
            }
            // ..............................................
            // 调整开始时间
            if (this.offInSec > 0) {
                // 自动对齐
                if (this.autoPadding) {
                    this.tStart = ((int) Math.ceil(((double) tStart) / ((double) offInSec)))
                                  * offInSec;
                }
                // 仅仅是调整
                else {
                    this.tStart += this.offInSec;
                }
            }
            // ..............................................
            // 解析成功
            return true;
        }
        // 解析失败
        return false;
    }

    public boolean matchTime(int sec) {
        // 匹配范围
        if (null != this.region && !this.region.match(sec)) {
            return false;
        }

        // 精确匹配时间点
        if (null != this.timePoints) {
            return Arrays.binarySearch(this.timePoints, sec) >= 0;
        }

        // 根据步长计算
        if (sec >= this.tStart && sec < this.tEnd) {
            return 0 == (sec - this.tStart) % this.stepInSec;
        }
        return false;
    }

    public String getPrimaryString() {
        return this.__str;
    }

    public String toString() {
        return this.__str;
    }

    public boolean isPureRegion() {
        return !this.isPoints() && !this.isStep();
    }

    public boolean isPoints() {
        return null != this.timePoints;
    }

    public boolean isStep() {
        return this.tStart >= 0 && this.tEnd >= 0 && this.stepInSec > 0;
    }

    private static String _T(ZCroni18n i18n, String key, Object value) {
        return i18n.times.get(key).replace("?", value.toString());
    }

    public void joinText(ZCroni18n i18n, List<String> ary) {
        // 时间点
        if (null != timePoints) {
            this.__join_time_points(i18n, ary);
        }
        // 时间范围
        else {
            __join_time_region(i18n, ary);
            if (this.isStep())
                __join_step_info(i18n, ary);
        }
    }

    private void __join_time_points(ZCroni18n i18n, List<String> ary) {
        ArrayList<String> list = new ArrayList<>(this.timePoints.length);
        for (int i = 0; i < this.timePoints.length; i++) {
            int sec = this.timePoints[i];
            list.add(Times.Ti(sec).toString());
        }
        ary.add(Strings.join(", ", list));
    }

    private void __join_step_info(ZCroni18n i18n, List<String> ary) {
        String re = "";
        // 得到步长描述
        String vstp = _T(i18n, stepUnit.name(), stepValue);
        // 得到偏移描述
        String voff = null;
        if (offValue != 0) {
            TmUnit tu = null == offUnit ? stepUnit : offUnit;
            voff = _T(i18n, tu.name(), offValue);
        }
        // 对齐
        if (autoPadding) {
            re += _T(i18n, "pad", Strings.sNull(voff, vstp));
        }
        // 偏移
        else if (offValue != 0) {
            re += _T(i18n, "off", voff);
        }
        // 步长
        re += _T(i18n, "step", vstp);

        // 计入
        ary.add(re);
    }

    private void __join_time_region(ZCroni18n i18n, List<String> ary) {
        if (null == this.region)
            return;
        Integer sFrom = this.region.left();
        Integer sTo = this.region.right();

        Times.TmInfo tFrom = Times.Ti(null == sFrom ? 0 : sFrom);
        Times.TmInfo tTo = Times.Ti(null == sTo ? 86400 : sTo);
        // 解析模板
        Tmpl tmpl = Tmpl.parse(i18n.times.region);

        // 准备上下文
        NutMap c = new NutMap();
        c.put("ieF", this.region.isLeftOpen() ? i18n.EXC : i18n.INV);
        c.put("ieT", this.region.isRightOpen() ? i18n.EXC : i18n.INV);
        c.put("from", tFrom.toString());
        c.put("to", tTo.toString());

        // 渲染
        String str = tmpl.render(c);
        ary.add(str);
    }
}
