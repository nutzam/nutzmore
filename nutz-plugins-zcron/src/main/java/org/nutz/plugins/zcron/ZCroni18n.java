package org.nutz.plugins.zcron;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

public class ZCroni18n {

    public String start;
    public String to;
    public String L1;
    public String Ln;
    public String N;
    public String INV;
    public String EXC;

    public static class RgDates {
        public String full;
        public String same;
        public String region;
        public String no_from;
        public String no_to;
    }

    public RgDates dates;

    public static class RgTimes {
        public String region;
        public String off;
        public String h;
        public String m;
        public String s;
        public String step;
        public String pad;

        public String get(String key) {
            if ("region".equals(key))
                return region;
            if ("off".equals(key))
                return off;
            if ("h".equals(key))
                return h;
            if ("m".equals(key))
                return m;
            if ("s".equals(key))
                return s;
            if ("step".equals(key))
                return step;
            if ("pad".equals(key))
                return pad;
            throw Lang.makeThrow("Invalid unit : %s", key);
        }
    }

    public RgTimes times;

    public static class Item {
        public String unit;
        public String span;
        public String ANY;
        public String[] dict;
        public String suffix;
        public String tmpl;
        public String W;
        public String Wonly;

        public boolean hasSuffix() {
            return !Strings.isBlank(suffix);
        }

        public boolean hasDict() {
            return null != dict && dict.length > 0;
        }

        public boolean hasANY() {
            return !Strings.isBlank(ANY);
        }
    }

    public Item year;
    public Item month;
    public Item day;
    public Item week;
    public Item hour;
    public Item minute;
    public Item second;

    public Item getItem(String key) {
        if ("year".equals(key))
            return year;
        if ("month".equals(key))
            return month;
        if ("day".equals(key))
            return day;
        if ("week".equals(key))
            return week;
        if ("hour".equals(key))
            return hour;
        if ("minute".equals(key))
            return minute;
        if ("second".equals(key))
            return second;
        throw Lang.makeThrow("Unknown i18n key '%s'", key);
    }

}
