package org.nutz.mongo.fieldfilter;

import java.util.regex.Pattern;

/**
 * 根据给定的正则表达式，来判断Java字段或者Mongo字段是否忽略该字段
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class ZMoRegexFF extends ZMoFF {

    private Pattern regex;

    public ZMoRegexFF(String regex) {
        super();
        this.regex = Pattern.compile(regex);
    }

    @Override
    public boolean match(String fld) {
        return regex.matcher(fld).find();
    }

}
