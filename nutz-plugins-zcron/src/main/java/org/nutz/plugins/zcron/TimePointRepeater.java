package org.nutz.plugins.zcron;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nutz.lang.Strings;
import org.nutz.lang.Times;
import org.nutz.lang.util.TimeRegion;

class TimePointRepeater {

    static TimePointRepeater tryParse(String str) {
        TimePointRepeater tpr = new TimePointRepeater();
        if (tpr.valueOf(str))
            return tpr;
        return null;
    }

    static enum TmUnit {
        h, m, s
    }

    private boolean autoPadding;

    private int offInSec;

    private int stepInSec;

    private int offValue;

    private TmUnit offUnit;

    private int stepValue;

    private TmUnit stepUnit;

    private int unitInSec(TmUnit tu) {
        if (TmUnit.h == tu)
            return 3600;
        if (TmUnit.m == tu)
            return 60;
        return 1;
    }

    /**
     * 解析类似
     * 
     * <pre>
     * 0/30m
     * -12s/2h
     * </pre>
     * 
     * @param str
     *            输入字符串
     * @return 是否是符合规范的字符串
     */
    boolean valueOf(String str) {
        Matcher m = _P.matcher(str);
        if (m.find()) {
            autoPadding = null != m.group(1);
            stepValue = Integer.parseInt(m.group(5));
            stepUnit = TmUnit.valueOf(m.group(6));
            if (null != m.group(2)) {
                offValue = Integer.parseInt(m.group(3));
                offUnit = null != m.group(4) ? TmUnit.valueOf(m.group(4)) : null;
            }

            // 计算
            offInSec = (offValue < 0 ? stepValue : offValue)
                       * unitInSec(null == offUnit ? stepUnit : offUnit);
            stepInSec = stepValue * unitInSec(stepUnit);
            // 解析成功
            return true;
        }
        return false;
    }

    int[] genTimePoints(TimeRegion tr) {
        // 得到时间范围（秒）
        int tStart; // 开始（包含）
        int tEnd; // 结束（不包含）
        // 全天
        if (null == tr || !tr.isRegion()) {
            tStart = 0;
            tEnd = 86400;
        }
        // 根据给定时间区域
        else {
            tStart = tr.left() + (tr.isLeftOpen() ? 1 : 0);
            tEnd = tr.right() - (tr.isRightOpen() ? 0 : 1);
        }
        // ..............................................
        // 调整开始时间
        if (offInSec > 0) {
            // 自动对齐
            if (autoPadding) {
                tStart = ((int) Math.ceil(((double) tStart) / ((double) offInSec))) * offInSec;
            }
            // 仅仅是调整
            else {
                tStart += offInSec;
            }
        }
        // ..............................................
        // 计算时间点，并开始填充
        int len = ((tEnd - tStart) / stepInSec) + 1;
        int[] tps = new int[len];
        tps[0] = tStart;
        for (int i = 1; i < len; i++) {
            tps[i] = tStart + (i * stepInSec);
        }
        // 返回
        return tps;
    }

    Pattern _P = Pattern.compile("^(>)?((\\d+)([hms])?)?/(\\d+)([hms])$");

    String explainTimePoints(TimeRegion tr) {
        int[] tps = this.genTimePoints(tr);
        String[] list = new String[tps.length];
        for (int i = 0; i < tps.length; i++) {
            list[i] = Times.Ti(tps[i]).toString(true);
        }
        return Strings.join(", ", list);
    }

    private static String _T(ZCroni18n i18n, String key, Object value) {
        return i18n.times.get(key).replace("?", value.toString());
    }

    public String toText(ZCroni18n i18n) {
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

        // 返回
        return re;
    }
}
