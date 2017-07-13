package org.nutz.quartz;

import java.util.Calendar;

import org.nutz.lang.Strings;

/**
 * 判断日（按月）
 * <p>
 * 支持 "W" 工作日的方法:
 * 
 * <pre>
 * "W" : 仅被用于天（月）子表达式，表示工作日
 *     > "W" 为所有工作日
 *     > "4W" 为距离本月第5日最近的工作日
 *     > "4LW" 为距离当月倒数第4日最近的工作日
 * </pre>
 * 
 * <p>
 * 值如果表示工作日，则被 +100，那么
 * <ul>
 * <li>100 表示所有工作日
 * <li>99 表示距离当月最后一天最近的工作日
 * <li>101 表示距离当月第一天最近的工作日
 * </ul>
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class QzItem_dd extends QzDateItem {

    private final static int MOD_dd = 100;

    public QzItem_dd() {
        this.supportLast = true;
    }

    public boolean isAllWorkingDay() {
        if (values.length > 1 && values[1] == MOD_dd)
            return true;
        return false;
    }

    public boolean isReferWorkingDay() {
        if (values.length > 1 && values[1] > (MOD_dd / 2))
            return true;
        return false;
    }

    @Override
    protected boolean match(Calendar c) {
        // 取得日期
        int dd = c.get(Calendar.DAY_OF_MONTH);

        // 当月所有工作日都被匹配的话 ...
        if (this.isAllWorkingDay()) {
            int ww = c.get(Calendar.DAY_OF_WEEK);
            return ww != Calendar.SATURDAY && ww != Calendar.SUNDAY;
        }

        // 忽略 ANY
        if (ANY == values[0])
            return true;

        // 根据当前时间重新判断一下 max
        int maxDayInMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        int max = maxDayInMonth + 1;

        // 准备返回值
        int[] re = new int[values.length];
        re[0] = values[0];

        // 判断是否需要 L 一下 ...
        for (int i = 1; i < re.length; i++) {
            int v = values[i];
            // 那么一定是 workingDay 了
            if (v > 40) {
                v = v - MOD_dd; // 恢复原值

                // 开始寻找当月的这一天
                v = v < 0 ? max + v : v;

                // 取得周几
                int ww = c.get(Calendar.DAY_OF_WEEK);
                // 如果是 SUNDAY，那么前进一天
                if (ww == Calendar.SUNDAY) {
                    // 达到月末，回退到周五
                    if (v >= max) {
                        v -= 2;
                    }
                    // 否则前进一天到周一
                    else {
                        v++;
                    }
                }
                // 如果是 SATURDAY，那么回退一天
                else if (ww == Calendar.SATURDAY) {
                    // 一号是周五，不能回退，那么前进到下周一
                    if (v <= 1) {
                        v += 2;
                    }
                    // 否则回退一天到周五
                    else {
                        v--;
                    }
                }
                // 否则就是工作日
                else {}
            }
            // 否则判断一下
            else if (v < 0) {
                v = max + v;
            }
            // 记录
            re[i] = v;
        }

        // 最后判断一下
        return super._match_(dd, re);
    }

    @Override
    protected int eval4override(String str) {
        int workingDay = 0;

        if (str.endsWith("W")) {
            workingDay = MOD_dd;
            str = str.substring(0, str.length() - 1);
        }

        if (Strings.isBlank(str))
            return workingDay;

        return super.eval4override(str) + workingDay;
    }

}
