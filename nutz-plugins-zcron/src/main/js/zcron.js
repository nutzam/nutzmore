/**
提供了 ZCron 表达式的实现。无依赖
 
表达式文档 : https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-zcron

2017 by zozoh(zozohtnt@gmail.com)
*/
(function() {
//================================================================
var Tmpl = function(tmpl, c, left, right) {
    var re = tmpl;
    for(var key in c) {
        var val = c[key] || "";
        re = re.replace((left||"")+key+(right||""), val);
    }
    return re;
};
//================================================================
var formatDate = function(fmt, d) {
    var year = d.getFullYear();
    var yy = (""+year).substring(2);
    var month = d.getMonth() + 1;
    var date  = d.getDate();
    return Tmpl(fmt, {
        yyyy : year,
        yy   : yy,
        MM   : month > 9 ? month : "0"+month,
        M    : month,
        dd   : date > 9 ? date : "0"+date,
        d    : date,
    });
};
//================================================================
var AsDate = function(str) {
    if("string" == (typeof str)) {
        var m = /^(\d{4})[^\d]?(\d{1,2})([^\d]?(\d{1,2}))?$/.exec(str);
        if(!m)
            throw "Not a Date: '"+v+"'!!";
        return new Date(m[1],m[2]-1,m[4]||1);
    }
    var d = new Date(str);
    d.setHours(0,0,0,0);
    return d;
};
var AsTimeInObj = function(input, dft) {
	var _pad = function(v, width) {
		width = width || 2;
		if(3 == width){
			return v>99 ? v : (v>9 ? "0"+v : "00"+v);
		}
		return v>9 ? v : "0"+v;
	};
	input = (typeof input) == "number" ? input : input || dft;
	var inType = (typeof input);
    var ms  = 0;
    var ti  = {};
    // 字符串
    if ("string" == inType) {
        var m = /^([0-9]{1,2}):([0-9]{1,2})(:([0-9]{1,2})([.,]([0-9]{1,3}))?)?$/
                    .exec(input);
        if (!m)
            throw "Not a Time: '" + input + "'!!";
        // 仅仅到分钟
        if (!m[3]) {
            ti.hour = parseInt(m[1]);
            ti.minute = parseInt(m[2]);
            ti.second = 0;
            ti.millisecond = 0;
        }
        // 到秒
        else if (!m[5]) {
            ti.hour = parseInt(m[1]);
            ti.minute = parseInt(m[2]);
            ti.second = parseInt(m[4]);
            ti.millisecond = 0;
        }
        // 到毫秒
        else {
            ti.hour = parseInt(m[1]);
            ti.minute = parseInt(m[2]);
            ti.second = parseInt(m[4]);
            ti.millisecond = parseInt(m[6]);
        }
    }
    // 数字
    else if ("number" == inType) {
        var sec;
        if("ms" == dft) {
            sec = parseInt(input / 1000);
            ms  = Math.round(input - sec * 1000);
        }else{
            sec = parseInt(input);
            ms  = Math.round(input*1000 - sec*1000);
        }
        ti.hour   = Math.min(23, parseInt(sec / 3600));
        ti.minute = Math.min(59, parseInt((sec - ti.hour * 3600) / 60));
        ti.second = Math.min(59, sec - ti.hour * 3600 - ti.minute * 60);
        ti.millisecond = ms;
    }
    // 其他
    else{
        throw "Not a Time: " + input;
    }
    // 计算其他的值
    ti.value  = ti.hour * 3600 + ti.minute * 60 + ti.second;
    ti.valueInMillisecond = ti.value * 1000 + ti.millisecond;
    // 增加一个函数
    ti.toString = function (fmt) {
        // 默认的格式化方式
        if(!fmt) {
            fmt = "HH:mm";
            // 到毫秒
            if (0 != this.millisecond) {
                fmt += ":ss.SSS";
            }
            // 到秒
            else if (0 != this.second) {
                fmt += ":ss";
            }
        }
        // 进行格式化
        var sb  = "";
        var reg = /a|[HhKkms]{1,2}|S(SS)?/g;
        var pos = 0;
        var m;
        while (m = reg.exec(fmt)) {
            //console.log(reg.lastIndex, m.index, m.input)
            var l = m.index;
            // 记录之前
            if (l > pos) {
                sb += fmt.substring(pos, l);
            }
            // 偏移
            pos = reg.lastIndex;

            // 替换
            var s = m[0];
            if ("a" == s) {
                sb += this.value > 43200 ? "PM" : "AM";
            }
            // H Hour in day (0-23)
            else if ("H" == s) {
                sb += this.hour;
            }
            // k Hour in day (1-24)
            else if ("k" == s) {
                sb += (this.hour + 1);
            }
            // K Hour in am/pm (0-11)
            else if ("K" == s) {
                sb += (this.hour % 12);
            }
            // h Hour in am/pm (1-12)
            else if ("h" == s) {
                sb += ((this.hour % 12) + 1);
            }
            // m Minute in hour
            else if ("m" == s) {
                sb += this.minute;
            }
            // s Second in minute
            else if ("s" == s) {
                sb += this.second;
            }
            // S Millisecond Number
            else if ("S" == s) {
                sb += this.millisecond;
            }
            // HH 补零的小时(0-23)
            else if ("HH" == s) {
                sb += _pad(this.hour);
            }
            // kk 补零的小时(1-24)
            else if ("kk" == s) {
                sb += _pad(this.hour + 1);
            }
            // KK 补零的半天小时(0-11)
            else if ("KK" == s) {
                sb += _pad(this.hour % 12);
            }
            // hh 补零的半天小时(1-12)
            else if ("hh" == s) {
                sb += _pad((this.hour % 12) + 1);
            }
            // mm 补零的分钟
            else if ("mm" == s) {
                sb += _pad(this.minute);
            }
            // ss 补零的秒
            else if ("ss" == s) {
                sb += _pad(this.second);
            }
            // SSS 补零的毫秒
            else if ("SSS" == s) {
                sb += _pad(this.millisecond, 3);
            }
            // 不认识
            else {
                sb.append(s);
            }
        }
        // 结尾
        if (pos < fmt.length) {
            sb.append(fmt.substring(pos));
        }

        // 返回
        return sb.toString();
    };
    ti.valueOf = ti.toString;
    // 嗯，返回吧
    return ti;
};
var AsTimeInSec = function(str) {
    return AsTimeInObj(str).value;
};
var AsTimeInStr = function(sec, fmt) {
    return AsTimeInObj(sec).toString(fmt);
};
//================================================================
/*
返回数组:
    len==4 as Region:        [开/闭, 左值, 右值, 开/闭]
    len==3 as Single value:  [开/闭, 值, 开/闭]
    - true  : 开
    - false : 闭
*/
var Region = function(str, formatFunc){
    // 处理格式化值的方式
    if(formatFunc) {
        var fft = (typeof formatFunc);
        if("string" == fft) {
            // 日期
            if("date" == formatFunc) {
                formatFunc = function(v){
                    return AsDate(v);
                };
            }
            // 时间
            else if("time" == formatFunc){
                formatFunc = function(v){
                    return AsTimeInSec(v);
                };
            }
            // 数字
            else if("number" == formatFunc){
                formatFunc = function(v){
                    return parseInt(v);
                };
            }
            // 不支持
            else {
                throw "Region formatFunc can not be a '"+fftp+"'";
            }
        }
        // 那么必须是函数了
        else if("function" != fft) {
            throw "Region(.., formatFunc) can not be a " + fft;
        }
    }

    // 整理字符串
    var s = str.replace(/[ \t]/g, "");
    // eval:  |   1  ||  2  || 3 || 4  ||  5   |
    var m = /^([\[\(])([^,]*)(,)?([^,]*)([\)\]])$/.exec(str);
    if(!m){
        throw "invalid region: " + str;
    }
    // 范围
    var re;
    if(m[3]) {
        re = [
            m[1] == '(' ? true : false,  // [0]
            m[2] || null,                // [1]
            m[4] || null,                // [2] 
            m[5] == ')' ? true : false,  // [3]
        ];
        if(formatFunc) {
            if(re[1]!=null)re[1] = formatFunc(re[1]);
            if(re[2]!=null)re[2] = formatFunc(re[2]);
        }
    }
    // 单值
    else {
        re = [
            m[1] == '(' ? true : false,  // [0]
            m[2] || null,                // [1]
            m[5] == ')' ? true : false,  // [2]
        ];
        if(formatFunc) {
            if(re[1]!=null)re[1] = formatFunc(re[1]);
        }
    }
    // 添加帮助函数
    re.left  = function(){
        return this[1];
    };
    re.right = function(){
        return this[this.length - 2];
    };
    re.leftAsStr  = function(fmt){
        var v = this[1];
        return v ? formatDate(fmt||"yyyy-MM-dd",v) : "";
    };
    re.rightAsStr = function(fmt){
        var v = this[this.length - 2];
        return v ? formatDate(fmt||"yyyy-MM-dd",v) : "";
    };
    re.isLeftOpen  = function(){return this[0];};
    re.isRightOpen = function(){return this[this.length-1];};
    re.isRegion = function(){return this.length==4;};
    re.match = function(v) {
        // 区间
        if(this.length == 4) {
            if(null!=this[1]){
                if((this[0] && this[1]>=v) || (!this[0] && this[1]>v))
                    return false;
            }
            if(null!=this[2]){
                if((this[3] && this[2]<=v) || (!this[3] && this[2]<v))
                    return false;
            }
            return true;
        }
        // 不等于
        if(this[0] && this[2])
            return this[1] != v;
        // 等于
        return this[1] == v;
    };
    re.valueOf = function(){
        var s = this.isLeftOpen()?"(":"[";
        if(this.isRegion()){
            s += this.leftAsStr()  || "";
            s += ",";
            s += this.rightAsStr() || "";
        }else{
            s += this.leftAsStr();
        }
        s += this.isRightOpen()?")":"]";
        return s;
    };
    re.toString = re.valueOf;
    // 返回
    return re;
};
//================================================================
var TimePointRepeater = function(){};
TimePointRepeater.prototype = {
    unitInSec : function(tu) {
        if ("h" == tu)
            return 3600;
        if ("m" == tu)
            return 60;
        return 1;
    },
    //........................................................
    parse : function(str) {
        // 初始化
        this.region = null;
        this.timePoints = null;
        this.stepValue = 0;
        this.stepInSec = 0;
        this.tStart = -1;
        this.tEnd = -1;
        this.__str = str;

        // 匹配正则
        var m = /^T([\[\(][\d:,-]+[\]\)])?([{]([^}]+)[}])?$/.exec(str);
        if (!m)
            throw "Invalid time repeater '" + str + "'";

        // 有时间范围：
        var tmrg = m[1];
        if (tmrg)
            this.region = Region(tmrg, "time");

        // 处理时间点
        var tps = m[3];
        if (tps) {
            // 如果不是步长，则根据间隔时间点生成固定时间点
            if (!this.__parse_step(tps)) {
                var timeList = tps.split(/[ ,]+/g);
                this.timePoints = [];
                for (var x = 0; x < timeList.length; x++) {
                    this.timePoints[x] = AsTimeInSec(timeList[x]);
                }
                // 确保顺序
                this.timePoints.sort(function(a,b){return a-b;})
            }
        }

        // 既没有时间范围，有没有时间点，那么不能忍啊
        if (!this.region && !this.timePoints && this.stepInSec <= 0) {
            throw "Invalid time repeater '" + str + "'";
        }
    },
    //........................................................
    __parse_step : function(str) {
        var m = /^(>)?((\d+)([hms])?)?\/(\d+)([hms])$/.exec(str);
        if (m) {
            // ..............................................
            // 解析步长信息
            this.autoPadding = m[1] ? true : false;
            this.stepValue   = parseInt(m[5]);
            this.stepUnit    = m[6];
            if (m[2]) {
                this.offValue = parseInt(m[3]);
                this.offUnit  = m[4] || null;
            }
            // ..............................................
            // 计算
            this.offInSec  = (this.offValue < 0 ? this.stepValue : this.offValue)
                             * this.unitInSec(this.offUnit || this.stepUnit);
            this.stepInSec = this.stepValue * this.unitInSec(this.stepUnit);
            if (this.stepInSec <= 0) {
                throw "Step Value is not ava!";
            }
            // ..............................................
            // 计算步长的真正区间
            var tr = this.region;
            // 全天
            if (!tr || !tr.isRegion()) {
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
                    this.tStart = Math.ceil(this.tStart/this.offInSec) 
                                          * this.offInSec;
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
    },
    //........................................................
    matchTime : function(sec) {
        // 匹配范围
        if (null != this.region && !this.region.match(sec)) {
            return false;
        }

        // 精确匹配时间点
        if (null != this.timePoints) {
            return this.timePoints.indexOf(sec) >= 0;
        }

        // 根据步长计算
        if (sec >= this.tStart && sec < this.tEnd) {
            return 0 == (sec - this.tStart) % this.stepInSec;
        }
        return false;
    },
    //........................................................
    getPrimaryString : function() {
        return this.__str;
    },
    valueOf : function() {
        return this.__str;
    },
    //........................................................
    isPureRegion : function() {
        return !this.isPoints() && !this.isStep();
    },
    //........................................................
    isPoints : function() {
        return this.timePoints ? true : false;
    },
    //........................................................
    isStep : function() {
        return this.tStart >= 0 && this.tEnd >= 0 && this.stepInSec > 0;
    },
    //........................................................
    _T : function(i18n, key, value) {
        return i18n.times[key].replace(/\?/g, value);
    },
    //........................................................
    joinText : function(i18n, ary) {
        // 时间点
        if (this.timePoints) {
            this.__join_time_points(i18n, ary);
        }
        // 时间范围
        else {
            this.__join_time_region(i18n, ary);
            if (this.isStep())
                this.__join_step_info(i18n, ary);
        }
    },
    //........................................................
    __join_time_points: function(i18n, ary) {
        var list = [];
        for (var i = 0; i < this.timePoints.length; i++) {
            var sec = this.timePoints[i];
            list.push(AsTimeInStr(sec));
        }
        ary.push(list.join(", "));
    },
    //........................................................
    __join_step_info: function(i18n, ary) {
        var re = "";
        // 得到步长描述
        var vstp = this._T(i18n, this.stepUnit, this.stepValue);
        // 得到偏移描述
        var voff = null;
        if (this.offValue != 0) {
            var tu = this.offUnit || this.stepUnit;
            voff = this._T(i18n, tu, this.offValue);
        }
        // 对齐
        if (this.autoPadding) {
            re += this._T(i18n, "pad", voff || vstp);
        }
        // 偏移
        else if (this.offValue != 0) {
            re += this._T(i18n, "off", voff);
        }
        // 步长
        re += this._T(i18n, "step", vstp);

        // 计入
        ary.push(re);
    },
    //........................................................
    __join_time_region: function(i18n, ary) {
        if (!this.region)
            return;
        var sFrom = this.region.left();
        var sTo   = this.region.right();

        var tFrom = AsTimeInObj(sFrom, 0);
        var tTo   = AsTimeInObj(sTo,   86400);

        // 准备上下文
        var c = {};
        c.ieF  = this.region.isLeftOpen() ? i18n.EXC : i18n.INV;
        c.ieT  = this.region.isRightOpen() ? i18n.EXC : i18n.INV;
        c.from = tFrom.toString();
        c.to   = tTo.toString();

        // 渲染
        var str = Tmpl(i18n.times.region, c, "${", "}");
        ary.push(str);
    }
    //........................................................
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
var MONTH_OF_YEAR = [null, "JAN", "FEB", "MAR", "APR", "MAY", "JUN", 
                           "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"];

//================================================================
// 表达式项的构造函数
var CrnItem = function(){
    this.prevItems = Array.from(arguments);
};
//................................................................
// Methods & Properties
CrnItem.prototype = {
    setIgnoreZeroWhenPrevHasSpan: function(ignore) {
        this.ignoreZeroWhenPrevHasSpan = ignore;
        return this;
    },
    setIgnoreAnyWhenPrevAllAny: function(ignore) {
        this.ignoreAnyWhenPrevAllAny = ignore;
        return this;
    },
    // 解析表达式项 （相当于 Java 版的 CrnStdItem.parse）
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
    isSPAN : function() {
        return "SPAN" == this.values[0];
    },
    isONE : function() {
        return "ONE" == this.values[0];
    },
    isPrevAllAny : function() {
        if (!this.prevItems || this.prevItems.length == 0)
            return false;
        for (var i=0; i<this.prevItems.length; i++){
            var prev = this.prevItems[i];
            if (!prev.isANY())
                return false;
        }
        return true;
    },
    isPrevHasSpan : function() {
        if(this.prevItems && this.prevItems.length>0){
            for (var i=0; i<this.prevItems.length; i++){
                var prev = this.prevItems[i];
                if (prev.isSPAN())
                    return true;
            }
        }
        return false;
    },
    _T : function(lc, i18n, n){
        // 特殊的周 FRI#3
        if(this.supportMOD && n>MOD_ww){
            var n0 = n % MOD_ww;
            var n1 = (n - n0) / MOD_ww;
            var s = lc.dict ? lc.dict[n0-1] : lc.tmpl.replace("?", n0);
            return i18n.N.replace("?", n1) + s;
        }
        // 特殊值:工作日 W
        else if(this.supportMOD && n > (MOD_dd/2)){
            var n0 = n - MOD_dd;
            return n0 == 0 ? lc.Wonly : this._T(lc, i18n, n0) + lc.W;
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
    },
    joinText : function(ary, i18n, key, off, ignoreSuffix){
        off = off || 0;
        if(off >= this.values.length)
            return -1;
        var lc = i18n[key];

        switch(this.values[off++]){
        case "ANY":
            // 忽略输出
            if (this.ignoreAnyWhenPrevAllAny && this.isANY() && this.isPrevAllAny()) {}
            // 输出
            else if (lc.ANY) {
                ary.push(lc.ANY);
                if(!ignoreSuffix && lc.suffix)
                    ary.push(lc.suffix);
            }
            break;
        case "RANGE" :
            ary.push(this._T(lc, i18n, this.values[off++])
                    + i18n.to
                    + this._T(lc, i18n, this.values[off++]));
            if(!ignoreSuffix && lc.suffix) ary.push(lc.suffix);
            break;
        case "LIST":
            var list = [];
            while(-1!=(off = this.joinText(list, i18n, key, off, true))){}
            ary.push(list.join(","));
            if(!ignoreSuffix && lc.suffix) ary.push(lc.suffix);
            break;
        case "SPAN":
            var s0 = this._T(lc, i18n, this.values[off++]);
            ary.push(i18n.start.replace("?", s0));
            ary.push(lc.span.replace("?", this.values[off++]));
            if(!ignoreSuffix && lc.suffix) ary.push(lc.suffix);
            break;
        case "ONE":
            var n = this.values[off++];
            
            // 忽略输出
            if (n == 0 && this.ignoreZeroWhenPrevHasSpan && this.isPrevHasSpan()) {}
            // 输出
            else {
                ary.push(this._T(lc, i18n, n));
                if(!ignoreSuffix && lc.suffix)
                    ary.push(lc.suffix);
            }
            break;
        default:
            throw "Unknown type : " + this.values[0];
        }
        // 返回指向下一个位置的下标
        return off;
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
    // 解析表达式项字典值
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
};
//
//================================================================
var CrnItem_dd = function(){
    CrnItem.apply(this, Array.from(arguments));
    this.supportLast = true;
    this.supportMOD  = true;
};
CrnItem_dd.prototype = new CrnItem();
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
CrnItem_dd.prototype.eval4override = function(str){
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
CrnItem_dd.prototype.matchDate = function(c){
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
var CrnItem_MM = function(){
    CrnItem.apply(this, Array.from(arguments));
};
CrnItem_MM.prototype = new CrnItem();
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
CrnItem_MM.prototype.eval4override = function(str){
    return this.__eval(str, MONTH_OF_YEAR, 1)
};
CrnItem_MM.prototype.matchDate = function(c){
    var MM = c.getMonth() + 1;
    return this._match_(MM, this.prepare(13));
};
//================================================================
var CrnItem_yy = function(){
    CrnItem.apply(this, Array.from(arguments));
};
CrnItem_yy.prototype = new CrnItem();
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
CrnItem_yy.prototype.eval4override = function(str){
    return this.__eval(str, null, 1)
};
CrnItem_yy.prototype.matchDate = function(c){
    var yy = c.getFullYear() + 1;
    return this._match_(yy, this.prepare(0));
};
//
//================================================================
var CrnItem_ww = function(){
    CrnItem.apply(this, Array.from(arguments));
    this.supportMOD  = true;
};
CrnItem_ww.prototype = new CrnItem();
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
CrnItem_ww.prototype.eval4override = function(str){
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
CrnItem_ww.prototype.matchDate = function(c){
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
var ZCronObj = function(cron) {
    this.__zcron_version = "1.1";

    // 字符串
    if(typeof cron == "string") {
        this.parse(cron);
    }
    // 是一个其他的 ZCron
    else if(str.__str && str.__zcron_version) {
        this.parse(cron.__str);
    }
    // 其他情况抛错
    else {
        throw "ZCron.parse invalid input: " + s;
    }
};
//................................................................
// Methods & Properties
ZCronObj.prototype = {
    //............................................................
    parse : function(cron){
        this.__str = cron;
        this.parts = [null,null,null,null];

        // 初始化
        this.iHH = new CrnItem();
        this.imm = new CrnItem(this.iHH);
        this.iss = new CrnItem(this.imm).setIgnoreZeroWhenPrevHasSpan(true);

        this.idd = new CrnItem_dd();
        this.iww = new CrnItem_ww(this.idd).setIgnoreAnyWhenPrevAllAny(true);
        this.iMM = new CrnItem_MM(this.idd, this.iww).setIgnoreAnyWhenPrevAllAny(true);
        this.iyy = new CrnItem_yy(this.iMM).setIgnoreAnyWhenPrevAllAny(true);

        // 拆
        var items = cron.trim().split(/[ \t]+/g);
        var stdList = [];

        // 先找一遍,处理扩展表达式项目，剩下的归到标准表达式里面
        this.__parse_for_ext(items, stdList);

        // 默认标准表达式
        var stds = ["0", "0", "0", "*", "*", "?", "*"];

        // 如果标准表达式项目不足，试图补上
        var stdIC = stdList.length;
        var stdN;
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
            if (0 == this.timeRepeaters.length)
                throw "No TimePoints : " + cron;
            if (!this.rgDate)
                throw "No DateRange : " + cron;
            stdN = 0;
            break;
        // 给了 `日 月 周` 必须还要给定 timePoints
        case 3:
            if (0 == this.timeRepeaters.length)
                throw "No TimePoints : " + cron;
            stdN = 1;
            break;
        // 给了 `日 月 周 年` 必须还要给定 timePoints
        case 4:
            if (0 == this.timeRepeaters.length)
                throw "No TimePoints : " + cron;
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
            throw "Wrong format : " + cron;
        }

        // 补上标准表达式项
        for (var i = 1; i <= stdIC; i++) {
            stds[stds.length - i - stdN] = stdList[stdIC - i];
        }

        // 解析子表达式
        this.iss.parse(stds[0]);
        this.imm.parse(stds[1]);
        this.iHH.parse(stds[2]);
        this.idd.parse(stds[3]);
        this.iMM.parse(stds[4]);
        this.iww.parse(stds[5]);
        this.iyy.parse(stds[6]);

        // 记录成标准
        this.parts[1] = stds.slice(0, 3).join(" ");
        this.parts[2] = stds.slice(3, 7).join(" ");

        // 返回
        return this;
    },
    __parse_for_ext : function(items, stdList) {
        var trList = [];

        // 循环解析
        for (var i=0; i<items.length; i++) {
            var s = items[i];
            // 为日期范围
            if (/^D/.test(s)) {
                this.rgDate = Region(s.substring(1), "date");
                this.parts[3] = s;
            }
            // 为时间范围
            else if (/^T/.test(s)) {
                var tr = new TimePointRepeater();
                tr.parse(s);
                trList.push(tr);
            }
            // 标准表达式项
            else {
                stdList.push(s);
            }
        }

        // 判断一下是否有时间点，
        this.has_time_points = false;
        for (var i=0; i<trList.length; i++) {
            var tr = trList[i];
            if (tr.isPoints() || tr.isStep()) {
                this.has_time_points = true;
                break;
            }
        }

        // 如果有的话，丢弃所有的纯范围
        this.timeRepeaters = [];
        for (var i=0; i<trList.length; i++) {
            var tr = trList[i];
            if (tr.isPureRegion()) {
                if (!this.has_time_points)
                    this.timeRepeaters.push(tr);
            }
            // 肯定加
            else {
                this.timeRepeaters.push(tr);
            }
        }

        // 设置一下 part[0]
        this.has_time_steps  = false;
        var trStrs = [];
        for (var i=0; i<this.timeRepeaters.length; i++) {
            var tr = this.timeRepeaters[i];
            this.has_time_steps |= tr.isStep();
            trStrs.push(tr.getPrimaryString());
        }
        this.parts[0] = trStrs.length == 0 ? null : trStrs.join(" ");
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
    isFromLastDay : function(){
        var dd = this.idd.values[1];
        return this.idd.isONE()
               && (dd < 0
                  || (dd > 40 && dd<MOD_dd));
    },
    isHasTimePoints : function() {
        return this.has_time_points;
    },
    isHasTimeSteps : function() {
        return this.has_time_steps;
    },
    //............................................................
    // day : [1,7] 表 [Sun, Sat]
    matchDayInWeek : function(day) {
        return this.iww._match_(day, this.iww.prepare(8));
    },
    matchDayInMonth : function(day) {
        //console.log("matchDayInMonth", day)
        if(this.idd.workingDay){
            day += MOD_dd;
            if(this.isFromLastDay())
                day -= 32;
        }
        return this.idd._match_(day, this.idd.prepare(32));
    },
    matchMonth : function(m) {
        return this.iMM._match_(m, this.iMM.prepare(13));
    },
    matchYear : function(year) {
        return this.iyy._match_(year, this.iyy.prepare(0));
    },
    //............................................................
    matchDate : function(c) {
        var d = AsDate(c);

        if (this.rgDate && !this.rgDate.match(d))
            return false;

        if (!this.iyy.matchDate(d))
            return false;

        if(!this.idd.matchDate(d))
            return false;

        if(!this.iMM.matchDate(d))
            return false;
        
        if(!this.iww.matchDate(d))
            return false;
        
        return true;
    },
    matchTime : function(sec) {
        var ti = AsTimeInObj(sec);

        // 指明了时间点的情况
        if (this.has_time_points) {
            for (var i=0; i < this.timeRepeaters.length; i++) {
                var tr = this.timeRepeaters[i];
                if (tr.matchTime(ti.value))
                    return true;
            }
            return false;
        }

        // 先匹配时间范围
        if (this.timeRepeaters.length > 0) {
            for (var i=0; i < this.timeRepeaters.length; i++) {
                var tr = this.timeRepeaters[i];
                if (!tr.matchTime(ti.value))
                    return false;
            }
        }

        // 依次对于表达式求职
        if (!this.iHH.matchTime(ti.hour, 0, 24))
            return false;

        if (!this.imm.matchTime(ti.minute, 0, 60))
            return false;

        if (!this.iss.matchTime(ti.second, 0, 60))
            return false;

        return true;
    },
    matchHour : function(HH) {
        return this.iHH.matchTime(HH, 0, 24);
    },
    matchMinute : function(mm) {
        return this.imm.matchTime(mm, 0, 60);
    },
    matchSecond : function(ss) {
        return this.iss.matchTime(ss, 0, 60);
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
    setPartExtTime : function(str) {
        return this.__set_part(0, str);
    },
    setPartStdTime : function(str) {
        return this.__set_part(1, str);
    },
    setPartStdDate : function(str) {
        return this.__set_part(2, str);
    },
    setPartExtDate : function(str) {
        return this.__set_part(3, str);
    },
    __set_part : function(index, str) {
        var val = (str||"").trim() || null;
        //console.log("__set_part", index, str);
        this.parts[index] = val;
        // 清除时间点
        if (0 == index && !val) {
            this.timeRepeaters   = [];
            this.has_time_points = false;
        }
        // 清除日期范围
        if (3 == index && !val) {
            this.rgDate = null;
        }
        // 立即解析
        var cron = this.toString();
        return this.parse(cron);
    },
    //............................................................
    toString : function() {
        var list = [];
        // 扩展: 时间部分
        if (this.parts[0]) {
            list.push(this.parts[0]);
        }
        // 标准: 时间部分
        if (!this.has_time_points) {
            list.push(this.parts[1]);
        }
        // 标准: 日期部分
        if (!this.rgDate 
            || "* * ? *" != this.parts[2] 
            || !this.has_time_points) {
            list.push(/ [*]$/.test(this.parts[2]) 
                ? this.parts[2].substring(0, this.parts[2].length - 2)
                : this.parts[2]);
        }
        // 扩展: 日期部分
        if (this.parts[3]) {
            list.push(this.parts[3]);
        }
        // 返回结果
        return list.join(" ");
    },
    //............................................................
    // JS 默认对象变字符串的函数，相当于 Java 的 toString()
    valueOf : function(){
        return this.toString();
    },
    getPrimaryString : function(){
        return this.__str;
    },
    //............................................................
    toTimeText : function(i18n){
        var ary = [];
        // ............................................
        // 描述了时间点
        for (var i=0; i < this.timeRepeaters.length; i++) {
            var tr = this.timeRepeaters[i];
            tr.joinText(i18n, ary);
        }
        // ............................................
        // 如果没有指定时间点，则默认采用标准表达式的时间
        if (!this.has_time_points) {
            this.iHH.joinText(ary, i18n, "hour");
            this.imm.joinText(ary, i18n, "minute");
            this.iss.joinText(ary, i18n, "second");
        }
        // 返回 
        return ary.join("");
    },
    //............................................................
    toText : function(i18n){
        var ary = [];
        // ............................................
        // 增加日期范围
        if (this.rgDate) {
            this.__join_date_region(i18n, ary);
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
        for (var i=0; i < this.timeRepeaters.length; i++) {
            var tr = this.timeRepeaters[i];
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
        return ary.join("");
    },
    //........................................................
    __join_date_region: function(i18n, ary) {
        var dFrom = this.rgDate.left();
        var dTo   = this.rgDate.right();

        var yearFrom = !dFrom ? -1 : dFrom.getFullYear();
        var yearTo   = !dTo   ? -2 : dTo.getFullYear();

        // 准备模板
        var tmpl;
        // 没有开始
        if (yearFrom < 0) {
            tmpl = i18n.dates.no_from;
        }
        // 没有结束
        else if (yearTo < 0) {
            tmpl = i18n.dates.no_to;
        }
        // 完整区间
        else {
            tmpl = i18n.dates.region;
        }


        // 准备上下文
        var c  = {};
        // 开始
        if (yearFrom > 0) {
            c.from = formatDate(i18n.dates.full, dFrom);
            c.ieF  = this.rgDate.isLeftOpen() ? i18n.EXC : i18n.INV;
        }
        // 结束
        if (yearTo > 0) {
            // 同年
            if (yearFrom == yearTo) {
                c.to = formatDate(i18n.dates.same, dTo);
            }
            // 跨年
            else {
                c.to = formatDate(i18n.dates.full, dTo);
            }
            c.ieT  = this.rgDate.isRightOpen() ? i18n.EXC : i18n.INV;
        }
        // 渲染
        var str = Tmpl(tmpl, c, "${", "}");
        ary.push(str);
    },
    //........................................................
};
//............................................................
// 下面两个是静态方法，可直接 ZCron.xxx 调用
//............................................................
var ZCron = function(qz){
    if(typeof qz == "string")
        return new ZCronObj(qz);
    if(qz.iss && qz.imm && qz.iHH && qz.idd && qz.iMM && qz.iww)
        return qz;
    throw "ZCron can not wrap : " + qz;
};
ZCron.compact = function(array) {
    var list = [];
    for (var i=0; i<array.length; i++){
        var ele = array[i];
        if (null != ele && typeof ele != 'undefined')
            list.push(ele);
    }
    return list;
};
ZCron.compactAll = function(array) {
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
window.ZCron = ZCron;
// TODO 支持 AMD | CMD 
//===============================================================
if (typeof define === "function") {
    // CMD
    if(define.cmd) {
        define(function (require, exports, module) {
            module.exports = ZCron;
        });
    }
    // AMD
    else {
        define("zcron", [], function () {
            return ZCron;
        });
    }
}
//================================================================
})();