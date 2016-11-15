/**
 * Created by kerbores on 2016/10/8.
 */
$.fn.validation = function (options) {
    var df = {
        rules: {
            double: {
                validate: function (value) {
                    return /^-?\d+\.?\d{0,2}$/.test(value) && value >= 0.01;
                },
                errorMsg: '价格只能由数字组成且最多带有2位小数并且大于0.01',
                defaultValue: '0.01'
            },
            required: {
                validate: function (value) {
                    return value.trim().length > 0;
                },
                errorMsg: '这个字段是必须要填写的',
                defaultValue: '123'
            },
            email: {
                validate: function (value) {
                    return /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/.test(value);
                },
                errorMsg: '请输入正确的邮箱地址',
                defaultValue: 'kerbores@gmail.com'
            },
            userName: {
                validate: function (value) {
                    return /^[\d\w_]{3,12}$/.test(value);
                },
                defaultValue: 'dgj',
                errorMsg: '用户名输入不正确'
            },
            password: {
                validate: function (value) {
                    return value.length >= 6;
                },
                defaultValue: '111111',
                errorMsg: '密码输入不正确'
            },
            phone: {
                validate: function (value) {
                    return /^(13[0-9]|15[0|3|6|7|8|9]|18[0-9])\d{8}$/.test(value);
                },
                defaultValue: '13888888888',
                errorMsg: '电话号码不符合规范'
            },
            number: {
                validate: function (value) {
                    return /^[\d]+$/.test(value);
                },
                defaultValue: '0',
                errorMsg: '只能输入数字'
            },
            integer: {
                validate: function (value) {
                    return /^[\d]+$/.test(value) && value >= 0;
                },
                defaultValue: '0',
                errorMsg: '只能输入正数'
            },
            chinese: {
                validate: function (value) {
                    return /^[\u4e00-\u9fa5]*$/.test(value);
                },
                defaultValue: '中文',
                errorMsg: '只能输入中文汉字'
            },
            url: {
                validate: function (value) {
                    return /^https?:\/\/(([a-zA-Z0-9_-])+(\.)?)*(:\d+)?(\/((\.)?(\?)?=?&?[a-zA-Z0-9_-](\?)?)*)*$/.test(value);
                },
                defaultValue: 'http://www.kerbores.com',
                errorMsg: '请输入正确的url地址'
            },
            cardId: {
                validate: function (value) {
                    return /^(\d{15}$|^\d{18}$|^\d{17}(\d|X|x))$/.test(value);
                },
                defaultValue: '123456789000000000',
                errorMsg: '请输入正确的身份证号'
            }
        },
        validationcallBack: function (status, dom, errorMsg, defaultValue) {
            status || function () {
                Common.validationFail(errorMsg, dom);//仅限此项目使用
            }.call()
        },
        allowedElement: ['input', 'INPUT', 'select', 'SELECT'],
        isAllowed: function (input) {
            var tag = $(input)[0].tagName;
            for (var index in df.allowedElement) {
                if (tag === df.allowedElement[index]) return true;
            }
            return false;
        },
        validationForm: function (input, callback) {
            callback = callback ? callback : this.validationcallBack;
            if (!df.isAllowed(input)) {
                console.log('u r kid me,  u r sure to validation a dom element that is not allowed!');
            }
            var value = $(input).val();
            var type = $(input).data("type");
            if (!type) {
                return true;
            }
            var defultValue = $(input).data("default") || (this.rules[type] && this.rules[type].defaultValue);
            var errorMsg = $(input).data("error") || (this.rules[type] && this.rules[type].errorMsg) || $(input).attr('placeholder');
            var reg_ = type == "reg" ? eval($(input).data("reg")) : /^$/;
            var check_result = type != "reg" ? this.rules[type].validate(value) : reg_.test(value);
            callback(check_result, input, errorMsg, defultValue);
            return check_result;
        },
        addRule: function (name, value) {
            df.rules[name] = value;
        }
    }
    $.extend(df, options);
    options && options.addOnRules && $.extend(df.rules, options.addOnRules);
    var waiteToVal = $(this).find('input,select').andSelf();
    if (!waiteToVal.length) {
        console && console.warn("Nothing selected, can't validate, returning nothing.");
        return true;
    }
    var flag = true;
    $(waiteToVal).each(function (i, item) {
        if (flag) {
            flag = df.validationForm(item, df.validationcallBack);
        }
        if (!flag) {
            return flag;
        }
    })
    return flag;
}