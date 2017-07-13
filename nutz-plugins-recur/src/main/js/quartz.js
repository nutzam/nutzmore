(function() {
//================================================================
var _T = function(lc, i18n, n){
    // 特殊的周 FRI#3
    if(n>MOD_ww){
        var n0 = n % MOD_ww;
        var n1 = (n - n0) / MOD_ww;
        var s = lc.dict ? lc.dict[n0-1] : lc.tmpl.replace("?", n0);
        return i18n.N.replace("?", n1) + s;
    }
    // 特殊值:工作日 W
    else if(n > (MOD_dd/2)){
        var n0 = n - MOD_dd;
        return n0 == 0 ? lc.Wonly : _T(lc, i18n, n0) + lc.W;
    }
    // 正常值
    else if(n>=0){
        return lc.dict ? lc.dict[n-1] : lc.tmpl.replace("?", n);
    }
    // 那么就表示倒数
    if(-1 == n){
        return i18n.L1 + lc.unit;
    }
    return i18n.Ln.replace("?", Math.abs(n))+lc.unit;
};
//================================================================
var ANY   = 0;   // *
var RANGE = 1;   // 表范围: values[1] 表示最小值, values[2] 表示最大值
var LIST  = 2;   // 枚举: 从 values[1] 开始表示可以允许的值
var SPAN  = 3;   // 步长: values[1] 表示起始值， values[2] 表示步长
var ONE   = 4;   // 单值: values[1] 表示被精确匹配的值
var MOD_dd = 100;
var MOD_ww = 1000;
var DAYS_OF_WEEK  = [null,"SUN","MON","TUE","WED","THU","FRI","SAT"];
var MONTH_OF_YEAR = [null, "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"];
//================================================================
// 表达式项的构造函数
var QzItem = function(){};
//................................................................
// Methods & Properties
QzItem.prototype = {
    __eval : function(str, dict, dictOffset) {
        var x = 1;

        if (this.supportLast && /L$/.test(str)) {
            x = -1;
            str = str.substring(0, str.length - 1);
        }

        // 直接是数字
        if(/^[0-9]+$/.test(str)){
            return str * x;
        }
        
        // 使用字典
        if (dict) {
            var s = str.toUpperCase();
            for (var i = dictOffset; i < dict.length; i++)
                if (s == dict[i])
                    return i;
        }
        // 不支持
        throw "isNaN : " + str;
    },
    // 子类重载它，可以支持更丰富的值
    eval4override : function(str) {
        return this.__eval(str, null, -1);
    },
    parse : function(str){
        // 看看是不是 ANY
        if ("?" == str || "*" == str) {
            this.values = ["ANY"];
            return;
        }

        // 看看是不是列表
        var ss = str.split(",");
        if (ss.length > 1) {
            this.values = ["LIST"];
            for (var i=0; i<ss.length; i++) {
                var s = ss[i];
                var subs = s.split("-");
                if (subs.length > 1) {
                    this.values.push("RANGE");
                    this.values.push(this.eval4override(subs[0]));
                    this.values.push(this.eval4override(subs[1]));
                } else {
                    this.values.push("ONE");
                    this.values.push(this.eval4override(s));
                }
            }
            return;
        }

        // 看看是不是步长
        ss = str.split("/");
        if (ss.length > 1) {
            this.values = ["SPAN", this.eval4override(ss[0]), this.eval4override(ss[1])];
            return;
        }

        // 看看是不是范围
        ss = str.split("-");
        if (ss.length > 1) {
            this.values = ["RANGE", this.eval4override(ss[0]), this.eval4override(ss[1])];
            return;
        }
        // 那么一定是固定值了
        this.values = ["ONE", this.eval4override(str)];
    },
    isANY : function(){
        return "ANY" == this.values[0];
    },
    prepare : function(max) {
        // 准备返回值
        var refs = [];
        refs[0] = this.values[0];

        // 判断是否需要 L 一下 ...
        for (var i = 1; i < this.values.length; i++) {
            var v = this.values[i];
            refs[i] = v < 0 ? max + v : v;
        }

        // 返回
        return refs;
    },
    _match_ : function(v, refs) {
        switch (refs[0]) {
        case "ONE":
            return v == refs[1];

        case "RANGE":
            return v >= refs[1] && v <= refs[2];

        case "LIST":
            for (var i = 1; i < refs.length; i++) {
                if ("ONE" == refs[i]) {
                    if (v == refs[++i])
                        return true;
                } else if ("RANGE" == refs[i]) {
                    var l = refs[++i];
                    var r = refs[++i];
                    if (v >= l && v <= r)
                        return true;
                } else {
                    throw "Fuck! It is impossiable!";
                }
            }
            return false;

        case "SPAN":
            return (v - refs[1]) % refs[2] == 0;

        case "ANY":
            return true;
        }
        // 默认则不匹配
        return false;
    },
    matchTime : function(v, min, max) {
        // 如果值不在范围中
        if (v < min || v >= max)
            return false;

        // 通配
        if ("ANY" == this.values[0])
            return true;

        // 准备一下要判断的数组
        var refs = this.prepare(max);

        // 判断
        return this._match_(v, refs);
    },
    joinText : function(ary, i18n, key, off, ignoreSuffix){
        off = off || 0;
        if(off >= this.values.length)
            return -1;
        var lc = i18n[key];

        switch(this.values[off++]){
        case "ANY":
            ary.push(lc.ANY);
            if(!ignoreSuffix && lc.suffix) ary.push(lc.suffix);
            break;
        case "RANGE" :
            ary.push(_T(lc, i18n, this.values[off++])
                    + i18n.to
                    + _T(lc, i18n, this.values[off++]));
            if(!ignoreSuffix && lc.suffix) ary.push(lc.suffix);
            break;
        case "LIST":
            var list = [];
            while(-1!=(off = this.joinText(list, i18n, key, off, true))){}
            ary.push(list.join(","));
            if(!ignoreSuffix && lc.suffix) ary.push(lc.suffix);
            break;
        case "SPAN":
            var s0 = _T(lc, i18n, this.values[off++]);
            ary.push(i18n.start.replace("?", s0));
            ary.push(lc.span.replace("?", this.values[off++]));
            if(!ignoreSuffix && lc.suffix) ary.push(lc.suffix);
            break;
        case "ONE":
            var n = this.values[off++];
            if(!this.ignoreZero || n>0){
                ary.push(_T(lc, i18n, n));
                if(!ignoreSuffix && lc.suffix) ary.push(lc.suffix);
            }
            break;
        default:
            throw "Unknown type : " + this.values[0];
        }
        // 返回指向下一个位置的下标
        return off;
    }
};
//
//================================================================
var QzItem_dd = function(){this.supportLast = true;};
QzItem_dd.prototype = new QzItem();
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
QzItem_dd.prototype.eval4override = function(str){
    var workingDay = 0;

    if (/W$/.test(str)) {
        workingDay = MOD_dd;
        this.workingDay = true;
        str = str.substring(0, str.length - 1);
    }

    if (!str)
        return workingDay;

    return this.__eval(str) + workingDay;
};
QzItem_dd.prototype.matchDate = function(c){
    // 忽略 ANY
    if ("ANY" == this.values[0])
        return true;

    // 当月所有工作日都被匹配的话 ...
    if (this.values[1] == MOD_dd) {
        var ww = c.getDay();  // 0 为 SUN.
        return ww != 0 && ww != 6;
    }

    // 根据当前时间重新判断一下 max
    var maxDayInMonth = new Date(c.getYear(), c.getMonth()+1, 0).getDate();
    //console.log("maxDayInMonth=", maxDayInMonth, "of", c);
    var max = maxDayInMonth + 1;

    // 准备返回值
    var re = [];
    re[0] = this.values[0];

    // 判断是否需要 L 一下 ...
    for (var i = 1; i < this.values.length; i++) {
        var v = this.values[i];
        // 那么一定是 workingDay 了
        if (v > 40) {
            v = v - MOD_dd; // 恢复原值

            // 开始寻找当月的这一天
            v = v < 0 ? max + v : v;

            // 取得周几
            var CV = new Date(c.getFullYear(), c.getMonth(), v);
            var ww = CV.getDay();   // 0 为 SUN.
            // 如果是 SUNDAY，那么前进一天
            if (ww == 0) {
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
            else if (ww == 6) {
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
    var dd = c.getDate();
    return this._match_(dd, re);
};
//
//================================================================
var QzItem_MM = function(){};
QzItem_MM.prototype = new QzItem();
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
QzItem_MM.prototype.eval4override = function(str){
    return this.__eval(str, MONTH_OF_YEAR, 1)
};
QzItem_MM.prototype.matchDate = function(c){
    var MM = c.getMonth() + 1;
    return this._match_(MM, this.prepare(13));
};
//
//================================================================
var QzItem_ww = function(){};
QzItem_ww.prototype = new QzItem();
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
QzItem_ww.prototype.eval4override = function(str){
    if (/L$/.test(str))
        throw "Week item don's support 'L' : '"+str+"'";

    var n = 0;
    var pos = str.lastIndexOf("#");
    if (pos > 0) {
        if (str.indexOf('0') >= 0 || str.indexOf(',') >= 0) {
            throw "Wrong week item '"+str+"'!!!";
        }
        n = parseInt(str.substring(pos + 1));
        str = str.substring(0, pos);
        this.breakWeek = true;
    }
    var v = this.__eval(str, DAYS_OF_WEEK, 1);
    return v + MOD_ww * n;
};
QzItem_ww.prototype.matchDate = function(c){
    // 忽略 ANY
    if ("ANY" == this.values[0])
        return true;

    // 得到周
    var ww = c.getDay() + 1;

    // 准备判断数组
    var refs = null;

    // 特殊模式
    if (this.breakWeek) {
        var nowWeekN = parseInt((c.getDate()) / 7) + 1;
        var v = this.values[1];
        var w = v % MOD_ww;
        var n = parseInt((v - w) / MOD_ww);
        // 周数不等
        if (n != nowWeekN)
            return false;
        // 周几数不等
        if (w != ww)
            return false;
        refs = ["ONE", w];
    }

    // 普通模式
    return this._match_(ww, null == refs ? this.prepare(8) : refs);
};
//================================================================
// 主要构造函数
var QuartzObj = function(str) {
    this.iss = new QzItem();
    this.imm = new QzItem();
    this.iHH = new QzItem();
    this.idd = new QzItem_dd();
    this.iMM = new QzItem_MM();
    this.iww = new QzItem_ww();
    // 解析
    if(typeof str == "string")
        this.parse(str);
    // 是一个其他的 Quartz
    if(str.__str && str.iss && str.imm && str.iHH && str.idd && str.iMM && str.iww )
        this.parse(str.__str);
};
//................................................................
// Methods & Properties
QuartzObj.prototype = {
    //............................................................
    parse : function(s){
        if(!s)
            return "haha";
        this.__str = s;
        // 拆
        var ss = s.split(/[ ]+/);
        // 验证
        if (6 != ss.length)
            throw "Wrong format '"+s+"': expect 6 items but " + ss.length;
        // 解析子表达式
        this.iss.parse(ss[0]);
        this.imm.parse(ss[1]);
        this.iHH.parse(ss[2]);
        this.idd.parse(ss[3]);
        this.iMM.parse(ss[4]);
        this.iww.parse(ss[5]);
        // 返回
        return this;
    },
    //............................................................
    // 启动点精确到分,即不是 0分0秒的
    isTiny : function(){
        if(this.iss.values[0] != "ONE" || this.iss.values[1] !=0)
            return true;
        if(this.imm.values[0] != "ONE" || this.imm.values[1] !=0)
            return true;
        return false;
    },
    //............................................................
    isWeekly : function(){
        return this.iww.values[0] != "ANY";
    },
    //............................................................
    isMonthly : function(){
        return this.idd.values[0] != "ANY";
    },
    isWorkingDay : function(){
        return this.idd.workingDay;
    },
    //............................................................
    // day : [1,7] 表 [Sun, Sat]
    matchDayInWeek : function(day) {
        return this.iww._match_(day, this.iww.prepare(8));
    },
    matchDayInMonth : function(day) {
        if(this.idd.workingDay)
            day += MOD_dd;
        return this.idd._match_(day, this.idd.prepare(32));
    },
    matchMonth : function(m) {
        return this.iMM._match_(m, this.iMM.prepare(13));
    },
    //............................................................
    matchDate : function(c) {
        if(!this.idd.matchDate(c))
            return false;

        if(!this.iMM.matchDate(c))
            return false;
        
        if(!this.iww.matchDate(c))
            return false;
        
        return true;
    },
    matchTime : function(sec) {
        var HH,mm,ss;
        // 如果是字符串 ...
        if(typeof sec == "string"){
            var ary = sec.split(/[ :\t]+/);
            HH = parseInt(ary[0]);
            mm = parseInt(ary[1]);
            ss = ary.length>2 ? parseInt(ary[2]) : 0;
        }
        // 否则当做绝对秒数，先不计算
        else{
            HH = -1;
            mm = -1;
            ss = -1;
        }

        HH = HH<0 ? parseInt(sec / 3600) : HH;
        if (!this.iHH.matchTime(HH, 0, 24))
            return false;

        mm = mm<0 ? parseInt((sec - (HH * 3600)) / 60) : mm;
        if (!this.imm.matchTime(mm, 0, 60))
            return false;

        ss = ss<0 ? sec - (HH * 3600) - (mm * 60) : ss;
        if (!this.iss.matchTime(ss, 0, 60))
            return false;

        return true;
    },
    // callback : F(array, index)
    each : function(array, callback, c, off, len, unit) {
        // 填充数组为空，每必要填充
        if (!array || array.length == 0)
            return;

        // 如果日期不匹配，无视
        if (c && !this.matchDate(c))
            return;

        // 根据数组，获得一个数组元素表示多少秒
        off  = off  || 0;
        len  = len  || array.length;
        unit = unit || parseInt(86400 / array.length);

        // 循环数组
        var maxIndex = Math.min(array.length, off + len);
        for (var i = off; i < maxIndex; i++) {
            var sec = i * unit;
            var max = sec + unit;
            // 循环每个数组元素
            for (; sec < max; sec++) {
                if (this.matchTime(sec)) {
                    callback(array, i);
                    break;
                }
            }
        }
    },
    fill : function(array, obj, c, off, len, unit) {
        this.each(array, function(array, index) {
            array[index] = obj;
        }, c, off, len, unit);
        return array;
    },
    overlap : function(array, obj, c, off, len, unit) {
        this.each(array, function(array, index) {
            var ov = array[index];
            // 增加一个叠加器
            if (!ov){
                array[index] = [obj];
            }
            // 推入当前对象
            else{
                ov.push(obj);
            }
        }, c, off, len, unit);
        return array;
    },
    //............................................................
    toString : function(){
        return this.__str;
    },
    //............................................................
    toText : function(i18n){
        var ary = [];
        this.iMM.joinText(ary, i18n, "month");
        // 没限制日期，看看是否用周
        if(this.idd.isANY()){
            this.iww.isANY() 
                ? this.idd.joinText(ary, i18n, "day")
                : this.iww.joinText(ary, i18n, "week");
        }
        // 限制了日期，那么就用日期
        else{
            this.idd.joinText(ary, i18n, "day");
        }
        this.iHH.joinText(ary, i18n, "hour");
        this.imm.joinText(ary, i18n, "minute");
        this.iss.joinText(ary, i18n, "second");
        return ary.join("");
    },
    toTimeText : function(i18n){
        var ary = [];
        this.iHH.joinText(ary, i18n, "hour");
        this.imm.joinText(ary, i18n, "minute");
        this.iss.joinText(ary, i18n, "second");
        return ary.join("");
    }
};
//............................................................
// 下面两个是静态方法，可直接 Quartz.xxx 调用
//............................................................
var Quartz = function(qz){
    if(typeof qz == "string")
        return new QuartzObj(qz);
    if(qz.iss && qz.imm && qz.iHH && qz.idd && qz.iMM && qz.iww)
        return qz;
    throw "Quartz can not wrap : " + qz;
};
Quartz.compact = function(array) {
    var list = [];
    for (var i=0; i<array.length; i++){
        var ele = array[i];
        if (null != ele && typeof ele != 'undefined')
            list.push(ele);
    }
    return list;
};
Quartz.compactAll = function(array) {
    var list = [];
    for (var i = 0; i < array.length; i++){
        var ele = array[i];
        if (null != ele && typeof ele != 'undefined')
            list.push({
                obj   : ele,
                index : i
            });
    }
    return list;
};

// 挂载到 window 对象
// 
window.Quartz = Quartz;
// TODO 支持 AMD | CMD 
//===============================================================
if (typeof define === "function") {
    // CMD
    if(define.cmd) {
        define(function (require, exports, module) {
            module.exports = Quartz;
        });
    }
    // AMD
    else {
        define("quartz", [], function () {
            return Quartz;
        });
    }
}
//================================================================
})();