//-------------------------------------------------------
function log() {
	var ePre = document.getElementById("log");
	var str = Array.from(arguments).join(" ");
	ePre.innerHTML += (str || "~empty log line~") + "\n";
}
//-------------------------------------------------------
function explain(cr) {
	var str = "<b>" + cr + "</b>";
	for (var i = cr.length; i < 22; i++)
		str += " ";
	log(str + " : " 
		 + (str.length > 32 ? "\n   " : "")
		 + ZCron(cr).toText(i18n));
}
//-------------------------------------------------------
var __cr;
function Crn(str) {
	var ms0 = Date.now();
	__cr = ZCron(str);
	var ms1 = Date.now();
	log("\nPARSE(" + (ms1 - ms0) + "ms):");
	explain(__cr);
}
//-------------------------------------------------------
function ae_set_part(cron, expect, parts) {
	var args   = Array.from(arguments);
	var cron   = args[0];
	var expect = args[1];
	var parts  = args.slice(2);
	var cr = ZCron(cron);
	for (var i=0;i<parts.length;i++) {
		var part  = parts[i];
        var index = parseInt(part.substring(0, 1));
        var str   = part.substring(3).trim();
        cr.__set_part(index, "null" == str ? null : str);
    }
    assert_value(cron, expect, cr.toString(), ' -> '+parts.join(" | ")+'\n');
}
function aeT(cr, expect) {
	assert_value(cr, expect, ZCron(cr).toText(i18n));
}
function aeS(cr, expect) {
	assert_value(cr, expect, ZCron(cr).toString());
}
//-------------------------------------------------------
function assert_value(cr, expect, actual, prefix){
	var str = "";
	// 成功
	if(actual == expect) {
		str += '<span class=OK>OK: ' + cr + '</span>';
		for (var i = cr.length; i < 22; i++)
			str += " ";
		if(prefix)
			str += '<u>'+prefix+'</u>';
		str += " : ";
		str += actual;
	}
	// 失败
	else {
		str += '<span class=KO>!!: ' + cr + '</span>';
		for (var i = cr.length; i < 22; i++)
			str += " ";
		if(prefix)
			str += '<u>'+prefix+'</u>';
		str += " : ";
		str += actual;
		str += '\n  <em>- expect: </em><b>' + expect + '</b>';
	}
	log(str);
}
//-------------------------------------------------------
function assert_match_date(expect, ds) {
	var c = new Date(ds);
	var ms0 = Date.now();
	var re = __cr.matchDate(c);
	var ms1 = Date.now();
	var str = re == expect ? "<span class=OK>OK" : "<span class=KO>!!";
	str += " (" + (ms1 - ms0) + "ms)";
	str += " : " + ds + "</span>";
	str += " : <em>expect [" + expect + "], while actually [" + re + "]</em>";
	log(str);
}
function assert_match_time(expect, ts) {
	var ms0 = Date.now();
	var re = __cr.matchTime(ts);
	var ms1 = Date.now();
	var str = re == expect ? "<span class=OK>OK" : "<span class=KO>!!";
	str += " (" + (ms1 - ms0) + "ms)";
	str += " : " + ts + "</span>";
	str += " : <em>expect [" + expect + "], while actually [" + re + "]</em>";
	log(str);
}
function hh() {
	var ary = [];
	ary[24 - 1] = null;
	return ary;
}

function mm() {
	var ary = [];
	ary[24 * 60 - 1] = null;
	return ary;
}

function ss() {
	var ary = [];
	ary[24 * 60 * 60 - 1] = null;
	return ary;
}
function Fhh() {
	var args = [ hh() ].concat(Array.from(arguments));
	FILL.apply(this, args);
}
function Fmm() {
	var args = [ mm() ].concat(Array.from(arguments));
	FILL.apply(this, args);
}
function Fss() {
	var args = [ ss() ].concat(Array.from(arguments));
	FILL.apply(this, args);
}
// array, ds, String crs, String... expect
function FILL() {
	var array = arguments[0];
	var ds = arguments[1];
	var crs = arguments[2];
	var expect = arguments[3];
	var off = arguments.length > 4 ? arguments[4] : 0;
	var len = arguments.length > 5 ? arguments[5] : array.length;
	var unit = arguments.length > 6 ? arguments[6] : parseInt(86400 / array.length);
	// 创建 & 解析
	var cr = ZCron(crs);

	if ("0 0 8-10 * * ?" == crs) {
		console.log("haha")
	}

	// 执行
	var ms0 = Date.now();
	cr.fill(array, "E", new Date(ds), off, len, unit);
	var ms1 = Date.now();
	var cros = ZCron.compactAll(array);
	var ms2 = Date.now();

	// 输出结果
	var ss = [];

	// 结果可显示
	for (var i = 0; i < cros.length; i++) {
		cro = cros[i];
		ss.push(cro.index + ":" + cro.obj);
	}

	// 验证
	var ok = true;
	if (ss.length != expect.length) {
		ok = false;
	}
	// 继续验证
	else {
		for (var i = 0; i < ss.length; i++) {
			if (ss[i] != expect[i]) {
				ok = false;
				break;
			}
		}
	}
	// 打印   
	var str = ok ? "<span class=OK>OK" : "<span class=KO>!!";
	str += " (" + (ms1 - ms0) + "+" + (ms2 - ms1) + "=" + (ms2 - ms0) + "ms)";
	str += " : </span><b>" + crs + "</b>";
	str += " : <em>expect [" + expect.join(",") + "], while actually [" + ss.join(",") + "]</em>";
	log(str);
}
// int scale, String ds, String[] crss, String[] expect
function OVERL() {
	var scale = arguments[0];
	var ds = arguments[1];
	var crs = arguments[2];
	var expect = arguments[3];

	var c = new Date(ds);

	var qos = [];
	qos[scale - 1] = null;

	var ms0 = Date.now();
	for (var i = 0; i < crs.length; i++) {
		var cr = ZCron(crs[i]);
		cr.overlap(qos, i + ".E", c);
	}
	var ms1 = Date.now();
	// 压缩结果
	var cros = ZCron.compactAll(qos);
	var ms2 = Date.now();

	// 输出结果
	var ss = [];

	// 结果可显示
	for (var i = 0; i < cros.length; i++) {
		var cro = cros[i];
		ss.push(cro.index + ":" + cro.obj.join(","));
	}

	// 验证
	var ok = true;
	if (ss.length != expect.length) {
		ok = false;
	}
	// 继续验证
	else {
		for (var i = 0; i < ss.length; i++) {
			if (ss[i] != expect[i]) {
				ok = false;
				break;
			}
		}
	}
	// 打印   
	var str = ok ? "<span class=OK>OK" : "<span class=KO>!!";
	str += " (" + (ms1 - ms0) + "+" + (ms2 - ms1) + "=" + (ms2 - ms0) + "ms)";
	str += " : </span><b>" + crs + "</b>";
	str += " : <em>expect [" + expect.join(",") + "], while actually [" + ss.join(",") + "]</em>";
	log(str);
}